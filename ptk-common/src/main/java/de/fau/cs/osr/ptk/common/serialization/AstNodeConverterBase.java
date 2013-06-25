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
import java.util.HashSet;
import java.util.Set;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.AstStringNode;
import de.fau.cs.osr.ptk.common.serialization.NodeFactory.NamedMemberId;
import de.fau.cs.osr.utils.ReflectionUtils;

public class AstNodeConverterBase<T extends AstNode<T>>
{
	private final Class<T> nodeType;
	
	private TypeNameMapper typeNameMapper = new SimpleTypeNameMapper();
	
	private NodeFactory<T> nodeFactory = new SimpleNodeFactory<T>();
	
	private boolean storeTypes = true;
	
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
	
	public void setTypeNameMapper(TypeNameMapper typeNameMapper)
	{
		this.typeNameMapper = typeNameMapper;
	}
	
	public void setNodeFactory(NodeFactory<T> nodeFactory)
	{
		this.nodeFactory = nodeFactory;
	}
	
	public void setStoreTypes(boolean storeTypes)
	{
		this.storeTypes = storeTypes;
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
	
	protected boolean isTypeInfoSuppressed(Class<?> type)
	{
		return !storeTypes ||
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
		if (suppressNodes != null && suppressNodes.contains(n.getClass()))
		{
			return true;
		}
		else if (suppressEmptyStringNodes && isStringNode(n))
		{
			@SuppressWarnings("unchecked")
			String content = ((AstStringNode<T>) n).getContent();
			if (content.isEmpty())
				return true;
		}
		else if (suppressEmptyStringProperties && String.class.isInstance(n))
		{
			String content = (String) n;
			if (content.isEmpty())
				return true;
		}
		
		return false;
	}
	
	protected boolean isStringNode(Object n)
	{
		return stringNodeType != null && stringNodeType.isInstance(n);
	}
	
	protected boolean isStringNode(Class<?> clazz)
	{
		return stringNodeType != null && stringNodeType.isAssignableFrom(clazz);
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
	
	protected String getTypeAlias(Class<?> n)
	{
		String typeAlias = typeNameMapper.nameForType(n);
		if (typeAlias == null)
			throw new MissingTypeInformationException(
					"Cannot determine type alias for class '" + n.getName() + "'");
		return typeAlias;
	}
	
	protected boolean serializedTypeIsExpectedType(
			AstNode<T> parent,
			String name,
			Object value)
	{
		return serializedTypeIsExpectedType(getGetterType(parent, name), value);
	}
	
	protected boolean serializedTypeIsExpectedType(
			Class<?> expectedType,
			Object value)
	{
		Class<?> childType = value.getClass();
		if (childType == expectedType)
			return true;
		
		// Maybe we have a (Integer != int) problem?
		if (expectedType.isPrimitive())
			return ReflectionUtils.mapPrimitiveToUcType(expectedType) == childType;
		
		return false;
	}
	
	protected Class<?> getGetterType(AstNode<T> n, String name)
	{
		try
		{
			Method getter = new PropertyDescriptor(name, n.getClass()).getReadMethod();
			return getter.getReturnType();
		}
		catch (IntrospectionException e)
		{
			throw new IncompatibleAstNodeClassException("Class '" + n.getClass().getName() + "' is malformed", e);
		}
	}
	
	protected Class<?> getClassForAlias(String typeAlias)
	{
		Class<?> type = typeNameMapper.typeForName(typeAlias);
		if (type == null)
			throw new UnknownTypeException("Cannot find type for name '" + typeAlias + "'");
		return type;
	}
}
