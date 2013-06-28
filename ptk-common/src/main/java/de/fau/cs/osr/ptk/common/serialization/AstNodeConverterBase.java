/**
 * Copyright 2011 The Open Source Research Group,
 *                University of Erlangen-Nürnberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fau.cs.osr.ptk.common.serialization;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.AstStringNode;
import de.fau.cs.osr.ptk.common.serialization.NodeFactory.NamedMemberId;
import de.fau.cs.osr.utils.ReflectionUtils;

public class AstNodeConverterBase<T extends AstNode<T>>
		extends
			AstConverterBase
{
	private static final int LOWER_CAPACITY = 256;
	
	private static final int UPPER_CAPACITY = 384;
	
	private static final float LOAD_FACTOR = .6f;
	
	private static final ConcurrentHashMap<Getter, Getter> CACHE =
			new ConcurrentHashMap<Getter, Getter>(LOWER_CAPACITY, LOAD_FACTOR);
	
	// =========================================================================
	
	private final Class<T> nodeType;
	
	private NodeFactory<T> nodeFactory = new SimpleNodeFactory<T>();
	
	private boolean alwaysStoreType = false;
	
	private Set<Class<?>> suppressTypeInfo = null;
	
	private boolean storeLocation = true;
	
	private boolean storeAttributes = true;
	
	private Set<String> suppressAttributes = null;
	
	private boolean suppressEmptyStringProperties = false;
	
	private Set<String> suppressProperties = null;
	
	private Set<Class<? extends T>> suppressNodes = null;
	
	private Class<? extends AstStringNode<T>> stringNodeType;
	
	private boolean suppressEmptyStringNodes = false;
	
	// =========================================================================
	
	public AstNodeConverterBase(Class<T> nodeType)
	{
		this.nodeType = nodeType;
	}
	
	// =========================================================================
	
	public void setNodeFactory(NodeFactory<T> nodeFactory)
	{
		this.nodeFactory = nodeFactory;
	}
	
	public NodeFactory<T> getNodeFactory()
	{
		return nodeFactory;
	}
	
	public void setAlwaysStoreType(boolean alwaysStoreType)
	{
		this.alwaysStoreType = alwaysStoreType;
	}
	
	public void suppressTypeInfo(Class<?> type)
	{
		if (this.suppressTypeInfo == null)
			this.suppressTypeInfo = new HashSet<Class<?>>();
		this.suppressTypeInfo.add(type);
	}
	
	public void setStoreLocation(boolean storeLocation)
	{
		this.storeLocation = storeLocation;
	}
	
	public void setStoreAttributes(boolean storeAttributes)
	{
		this.storeAttributes = storeAttributes;
	}
	
	public void suppressAttribute(String name)
	{
		if (this.suppressAttributes == null)
			this.suppressAttributes = new HashSet<String>();
		this.suppressAttributes.add(name);
	}
	
	public void setSuppressEmptyStringProperties(
			boolean suppressEmptyStringProperties)
	{
		this.suppressEmptyStringProperties = suppressEmptyStringProperties;
	}
	
	public void suppressProperty(String name)
	{
		if (this.suppressProperties == null)
			this.suppressProperties = new HashSet<String>();
		this.suppressProperties.add(name);
	}
	
	public void suppressNode(Class<? extends T> nodeType)
	{
		if (this.suppressNodes == null)
			this.suppressNodes = new HashSet<Class<? extends T>>();
		this.suppressNodes.add(nodeType);
	}
	
	public void setStringNodeType(
			Class<? extends AstStringNode<T>> stringNodeType)
	{
		this.stringNodeType = stringNodeType;
	}
	
	public void setSuppressEmptyStringNodes(boolean suppressEmptyStringNodes)
	{
		this.suppressEmptyStringNodes = suppressEmptyStringNodes;
	}
	
	// =========================================================================
	
	protected Class<T> getNodeType()
	{
		return nodeType;
	}
	
	public boolean isAlwaysStoreType()
	{
		return alwaysStoreType;
	}
	
	protected boolean isTypeInfoSuppressed(Class<?> type)
	{
		return !alwaysStoreType &&
				((suppressTypeInfo != null) && suppressTypeInfo.contains(type));
	}
	
	protected boolean isLocationSuppressed()
	{
		return !storeLocation;
	}
	
	protected boolean isAttributesSuppressed()
	{
		return !storeAttributes;
	}
	
	protected boolean isAttributeSuppressed(String name)
	{
		return isAttributesSuppressed() ||
				((suppressAttributes != null) && suppressAttributes.contains(name));
	}
	
	protected boolean isPropertySuppressed(String name)
	{
		return ((suppressProperties != null) && suppressProperties.contains(name));
	}
	
	protected boolean isSuppressed(Object n)
	{
		Class<?> objType = n.getClass();
		if (suppressNodes != null && suppressNodes.contains(objType))
		{
			return true;
		}
		else if (suppressEmptyStringNodes && isStringNode(objType))
		{
			@SuppressWarnings("unchecked")
			String content = ((AstStringNode<T>) n).getContent();
			if (content.isEmpty())
				return true;
		}
		else if (suppressEmptyStringProperties && String.class == objType)
		{
			String content = (String) n;
			if (content.isEmpty())
				return true;
		}
		
		return false;
	}
	
	protected boolean isStringNode(Class<?> clazz)
	{
		return stringNodeType == clazz;
	}
	
	public Class<? extends AstStringNode<T>> getStringNodeType()
	{
		return stringNodeType;
	}
	
	// =========================================================================
	
	protected T instantiateNode(Class<?> clazz)
	{
		return nodeFactory.instantiateNode(clazz);
	}
	
	protected T instantiateDefaultChild(T n, String name, Class<?> child)
	{
		return nodeFactory.instantiateDefaultChild(
				new NamedMemberId(n.getClass(), name), child);
	}
	
	protected void setDefaultChild(T n, int childIndex, String childName)
	{
		Class<?> type = getGetterType(n, childName);
		T value = instantiateDefaultChild(n, childName, type);
		n.set(childIndex, value);
	}
	
	private Object instantiateDefaultProperty(T n, String name, Class<?> type)
	{
		return nodeFactory.instantiateDefaultProperty(
				new NamedMemberId(n.getClass(), name), type);
	}
	
	protected void setDefaultProperty(T n, AstNodePropertyIterator i)
	{
		String name = i.getName();
		Class<?> type = getGetterType(n, name);
		Object value = instantiateDefaultProperty(n, name, type);
		i.setValue(value);
	}
	
	// =========================================================================
	
	protected String getTypeAlias(AstNode<T> n)
	{
		return getTypeAlias(n.getClass());
	}
	
	protected boolean serializedTypeIsExpectedType(
			Class<?> expectedType,
			Class<?> childType)
	{
		if (childType == expectedType)
			return true;
		
		// Maybe we have a (Integer != int) problem?
		if (expectedType.isPrimitive())
			return ReflectionUtils.mapPrimitiveToUcType(expectedType) == childType;
		
		return false;
	}
	
	protected boolean serializedTypeIsExpectedType(
			AstNode<T> parent,
			String name,
			Class<?> valueType)
	{
		return serializedTypeIsExpectedType(getGetterType(parent, name), valueType);
	}
	
	protected boolean isTypeInfoRequired(
			AstNode<T> parentNode,
			String name,
			Class<?> valueType)
	{
		return alwaysStoreType
				|| (!serializedTypeIsExpectedType(parentNode, name, valueType) && !isTypeInfoSuppressed(valueType));
	}
	
	protected Class<?> getGetterType(AstNode<T> n, String name)
	{
		Class<?> nodeType = n.getClass();
		
		Getter getter = new Getter(nodeType, name);
		Getter cached = CACHE.get(getter);
		if (cached != null)
			return cached.getGetterType();
		
		return analyzeGetter(name, nodeType);
	}
	
	private Class<?> analyzeGetter(String name, Class<?> nodeType)
	{
		Method getterMethod;
		try
		{
			getterMethod = new PropertyDescriptor(name, nodeType).getReadMethod();
		}
		catch (IntrospectionException e)
		{
			throw new IncompatibleAstNodeClassException("Class '" + nodeType.getName() + "' is malformed", e);
		}
		
		Class<?> getterType = getterMethod.getReturnType();
		rememberGetter(name, nodeType, getterType);
		return getterType;
	}
	
	private void rememberGetter(
			String name,
			Class<?> nodeType,
			Class<?> getterType)
	{
		Getter getter = new Getter(nodeType, name, getterType);
		Getter cached = CACHE.putIfAbsent(getter, getter);
		if (cached == null)
		{
			// Make sure the target is not swept from the cache ...
			getter.touch();
			if (CACHE.size() > UPPER_CAPACITY)
				sweepCache();
		}
	}
	
	private static synchronized void sweepCache()
	{
		if (CACHE.size() <= UPPER_CAPACITY)
			return;
		
		Getter keys[] = new Getter[CACHE.size()];
		
		Enumeration<Getter> keysEnum = CACHE.keys();
		
		int i = 0;
		while (i < keys.length && keysEnum.hasMoreElements())
			keys[i++] = keysEnum.nextElement();
		
		int length = i;
		Arrays.sort(keys, 0, length);
		
		int to = length - LOWER_CAPACITY;
		for (int j = 0; j < to; ++j)
			CACHE.remove(keys[j]);
	}
	
	// =========================================================================
	
	protected static final class Getter
			implements
				Comparable<Getter>
	{
		private static long useCounter = 0;
		
		private long lastUse = -1;
		
		private Class<?> nodeType;
		
		private String fieldName;
		
		private Class<?> getterType;
		
		public Getter(Class<?> nodeType, String fieldName)
		{
			this.nodeType = nodeType;
			this.fieldName = fieldName;
			this.getterType = null;
		}
		
		public Getter(Class<?> nodeType, String fieldName, Class<?> getterType)
		{
			this.nodeType = nodeType;
			this.fieldName = fieldName;
			this.getterType = getterType;
		}
		
		public Class<?> getNodeType()
		{
			return nodeType;
		}
		
		public String getFieldName()
		{
			return fieldName;
		}
		
		public Class<?> getGetterType()
		{
			return getterType;
		}
		
		@Override
		public int hashCode()
		{
			return fieldName.hashCode() + 31 * nodeType.hashCode();
		}
		
		@Override
		public boolean equals(Object obj)
		{
			Getter other = (Getter) obj;
			if (!fieldName.equals(other.fieldName))
				return false;
			if (nodeType != other.nodeType)
				return false;
			return true;
		}
		
		public void touch()
		{
			lastUse = ++useCounter;
		}
		
		@Override
		public int compareTo(Getter o)
		{
			// Equality is not possible!
			return (lastUse < o.lastUse) ? -1 : +1;
		}
	}
}
