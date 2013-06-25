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

package de.fau.cs.osr.ptk.common.xml;

import java.util.Map.Entry;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.fau.cs.osr.ptk.common.ast.AstLocation;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.AstStringNode;
import de.fau.cs.osr.ptk.common.serialization.AstNodeConverterBase;
import de.fau.cs.osr.ptk.common.serialization.SyntaxErrorException;

public class AstNodeConverter<T extends AstNode<T>>
		extends
			AstNodeConverterBase<T>
		implements
			Converter
{
	protected static final String NODE_NAME_ATTRIBUTE = "ptk:a";
	
	protected static final String ATTR_NAME_LOCATION = "ptk:location";
	
	protected static final String ATTR_NAME_TYPE = "ptk:type";
	
	// =========================================================================
	
	protected boolean explicitRoots = false;
	
	// =========================================================================
	
	public AstNodeConverter(Class<T> nodeType)
	{
		super(nodeType);
	}
	
	public static <S extends AstNode<S>> AstNodeConverter<S> forNodeType(
			Class<S> nodeType)
	{
		return new AstNodeConverter<S>(nodeType);
	}
	
	// =========================================================================
	
	public void setExplicitRoots(boolean explicitRoots)
	{
		this.explicitRoots = explicitRoots;
	}
	
	// =========================================================================
	
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type)
	{
		return getNodeType().isAssignableFrom(type);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void marshal(
			Object source,
			HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		dispatch((T) source, explicitRoots, writer, context);
	}
	
	@Override
	public Object unmarshal(
			HierarchicalStreamReader reader,
			UnmarshallingContext context)
	{
		if (explicitRoots)
		{
			if (!reader.hasMoreChildren())
				throw new SyntaxErrorException("Expected child element for explicit root node");
			
			reader.moveDown();
		}
		
		Class<?> type = context.getRequiredType();
		T node = unmarshalNode(type, reader, context);
		
		if (explicitRoots)
			reader.moveUp();
		return node;
	}
	
	// =========================================================================
	
	private void marshalNode(
			T n,
			boolean explicit,
			HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		if (explicit)
			writer.startNode(getTypeAlias(n));
		
		boolean isStringNode = isStringNode(n) && !n.hasAttributes();
		
		storeLocation(n, writer);
		storeAttributes(n, writer, context);
		
		boolean hasVisibleProperties = true;
		if (isStringNode)
		{
			// we can only invoke this function for string nodes!
			hasVisibleProperties = hasStringNodeVisibleProperties(n);
			isStringNode &= !hasVisibleProperties;
		}
		
		if (hasVisibleProperties)
			storeProperties(n, isStringNode, writer, context);
		
		if (isStringNode)
		{
			writer.setValue(((AstStringNode<T>) n).getContent());
		}
		else if (n.isList())
		{
			for (T c : n)
				dispatch(c, true, writer, context);
		}
		else
		{
			String[] childNum = n.getChildNames();
			for (int i = 0; i < childNum.length; ++i)
				storeNamedChild(n, i, writer, context);
		}
		
		if (explicit)
			writer.endNode();
	}
	
	private T unmarshalNode(
			Class<?> nodeType,
			HierarchicalStreamReader reader,
			UnmarshallingContext context)
	{
		T n = instantiateNode(nodeType);
		
		restoreLocation(n, reader);
		
		boolean down = false;
		boolean isStringNode = isStringNode(nodeType);
		boolean initializedProperties = false;
		
		if (reader.hasMoreChildren())
		{
			// A node with children cannot be serialized as pure text node
			isStringNode = false;
			
			reader.moveDown();
			down = restoreAttributes(n, reader, context);
			
			if (down)
			{
				// If restoreAttributes() returns down = false then there are no 
				// more children. This means that we don't have to check if 
				// there are any more children and go down again.
				down = restoreProperties(n, reader, context);
				initializedProperties = true;
			}
		}
		
		if (!initializedProperties)
			initializeProperties(n, isStringNode);
		
		if (isStringNode)
		{
			// If restoreAttributes() or restoreProperties() returns down = 
			// true then there are children!
			if (down)
				failOnUnexpectedChild(nodeType, reader);
			
			((AstStringNode<T>) n).setContent(reader.getValue());
		}
		else if (down)
		{
			// If restoreAttributes() or restoreProperties() returns down = 
			// false then there are no more children. This means that we don't 
			// have to check if there are any more children and go down again.
			if (n.isList())
			{
				restoreListOfChildren(reader, context, n);
			}
			else
			{
				restoreNamedChildren(nodeType, reader, context, n);
			}
		}
		else
			initializeChildren(n);
		
		return n;
	}
	
	// =========================================================================
	
	private void storeLocation(AstNode<T> n, HierarchicalStreamWriter writer)
	{
		if (!isLocationSuppressed())
		{
			AstLocation loc = n.getNativeLocation();
			if (loc != null)
				writer.addAttribute(ATTR_NAME_LOCATION, loc.toString());
		}
	}
	
	private void restoreLocation(T n, HierarchicalStreamReader reader)
	{
		String locStr = reader.getAttribute(ATTR_NAME_LOCATION);
		if (locStr != null)
			n.setNativeLocation(AstLocation.valueOf(locStr));
	}
	
	// =========================================================================
	
	private void storeAttributes(
			AstNode<T> n,
			HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		if (isAttributesSuppressed())
			return;
		
		for (Entry<String, Object> e : n.getAttributes().entrySet())
		{
			String name = e.getKey();
			if (!isAttributeSuppressed(name))
				writeAttribute(name, e.getValue(), writer, context);
		}
	}
	
	private void writeAttribute(
			String name,
			Object value,
			HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		writer.startNode(NODE_NAME_ATTRIBUTE);
		writer.addAttribute("name", name);
		
		if (value == null)
		{
			writer.addAttribute("null", "true");
		}
		else
		{
			storeType(value, writer);
			context.convertAnother(value);
		}
		
		writer.endNode();
	}
	
	private boolean restoreAttributes(
			T n,
			HierarchicalStreamReader reader,
			UnmarshallingContext context)
	{
		while (true)
		{
			boolean down = readAttribute(n, reader, context);
			
			if (down)
				// The child was not an attribute. 
				// Current still points to the child.
				return true;
			
			if (!reader.hasMoreChildren())
				// There was an attribute but there are no more children.
				// Current points to the parent node again.
				return false;
			
			// There was an attribute and there are more children, move on.
			reader.moveDown();
		}
	}
	
	private boolean readAttribute(
			T n,
			HierarchicalStreamReader reader,
			UnmarshallingContext context)
	{
		if (!reader.getNodeName().equals(NODE_NAME_ATTRIBUTE))
			return true;
		
		String name = reader.getAttribute("name");
		String nullAttr = reader.getAttribute("null");
		boolean isNull = (nullAttr != null) && nullAttr.equals("true");
		if (isNull)
		{
			n.setAttribute(name, null);
		}
		else
		{
			Class<?> type = getExplicitType(reader);
			n.setAttribute(name, context.convertAnother(n, type));
		}
		
		reader.moveUp();
		return false;
	}
	
	// =========================================================================
	
	private boolean hasStringNodeVisibleProperties(AstNode<T> n)
	{
		for (AstNodePropertyIterator i = n.propertyIterator(); i.next();)
		{
			Object value = i.getValue();
			if (value == null)
				continue;
			
			String name = i.getName();
			if (isPropertySuppressed(name))
				continue;
			
			if ("content".equals(name))
				continue;
			
			return true;
		}
		
		return false;
	}
	
	private void storeProperties(
			AstNode<T> n,
			boolean suppressContent,
			HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		for (AstNodePropertyIterator i = n.propertyIterator(); i.next();)
		{
			Object value = i.getValue();
			if (value == null)
				continue;
			
			String name = i.getName();
			if (isPropertySuppressed(name))
				continue;
			
			writeProperty(n, name, value, writer, context);
		}
	}
	
	private void writeProperty(
			AstNode<T> parentNode,
			String name,
			Object value,
			HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		if (isSuppressed(value))
			return;
		
		writer.startNode(name);
		
		if (!serializedTypeIsExpectedType(parentNode, name, value))
			storeType(value, writer);
		
		context.convertAnother(value);
		
		writer.endNode();
	}
	
	private boolean restoreProperties(
			T n,
			HierarchicalStreamReader reader,
			UnmarshallingContext context)
	{
		if (n.getPropertyCount() == 0)
			// We should not be able to find properties here.
			return true;
		
		AstNodePropertyIterator curProp = n.propertyIterator();
		while (true)
		{
			String name = reader.getNodeName();
			
			boolean down = true;
			
			// We write out properties in the order they appear in the iterator. 
			// We assume that this order is fixed and that everybody else does 
			// it like that too. This way we can save time if our assumption 
			// holds but loose time if it doesn't hold then it goes BOOM.
			AstNodePropertyIterator i = curProp;
			while (i.next())
			{
				if (i.getName().equals(name))
				{
					Class<?> type = getTypeFromGetter(n, reader);
					i.setValue(context.convertAnother(n, type));
					reader.moveUp();
					down = false;
					break;
				}
				else
				{
					setDefaultProperty(n, i);
				}
			}
			
			if (down)
				// No matching property found. This means syntax error or we've 
				// reached the parent node's children.
				return true;
			
			if (!reader.hasMoreChildren())
			{
				// There was a property but there are no more children.
				// Current points to the parent node again.
				while (i.next())
					setDefaultProperty(n, i);
				
				return false;
			}
			
			// There was a property and there are more children, move on.
			reader.moveDown();
		}
	}
	
	private void initializeProperties(T n, boolean isStringNode)
	{
		if (n.getPropertyCount() > 0)
		{
			for (AstNodePropertyIterator i = n.propertyIterator(); i.next();)
			{
				if (!(isStringNode && i.getName().equals("content")))
					// If it's a string node the content property will be set
					// form a child, not a property!
					setDefaultProperty(n, i);
			}
		}
	}
	
	// =========================================================================
	
	private void storeNamedChild(
			T n,
			int i,
			HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		T child = n.get(i);
		if (isSuppressed(child))
			return;
		
		String name = n.getChildNames()[i];
		writer.startNode(name);
		
		if (!serializedTypeIsExpectedType(n, name, child))
			storeType(child, writer);
		
		dispatch(child, false, writer, context);
		
		writer.endNode();
	}
	
	private void restoreNamedChildren(
			Class<?> nodeType,
			HierarchicalStreamReader reader,
			UnmarshallingContext context,
			T n)
	{
		// The current node is already the first child
		
		int childIndex = 0;
		String[] childNames = n.getChildNames();
		
		while (true)
		{
			if (childIndex >= childNames.length)
			{
				// There are still children in the XML but we don't expect 
				// any more.
				failOnUnexpectedChild(nodeType, reader);
				return;
			}
			
			// Skip all children for which no value was stored in the XML
			String childName = reader.getNodeName();
			while (true)
			{
				String expectedChildName = childNames[childIndex];
				if (childName.equals(expectedChildName))
					break;
				
				setDefaultChild(n, childIndex, expectedChildName);
				
				++childIndex;
				if (childIndex >= childNames.length)
				{
					failOnUnexpectedChild(nodeType, reader);
					return;
				}
			}
			
			// We found a match
			Class<?> childType = getTypeFromGetter(n, reader);
			T child = unmarshalNode(childType, reader, context);
			n.set(childIndex, child);
			
			// Continue...
			reader.moveUp();
			++childIndex;
			
			if (!reader.hasMoreChildren())
			{
				// No more children in the XML -> skip remaining children
				for (; childIndex < childNames.length; ++childIndex)
					setDefaultChild(n, childIndex, childNames[childIndex]);
				return;
			}
			
			reader.moveDown();
		}
	}
	
	private void restoreListOfChildren(
			HierarchicalStreamReader reader,
			UnmarshallingContext context,
			T n)
	{
		// The current node is already the first child
		while (true)
		{
			Class<?> type = getClassForAlias(reader.getNodeName());
			T child = unmarshalNode(type, reader, context);
			n.add(child);
			
			reader.moveUp();
			if (!reader.hasMoreChildren())
				break;
			reader.moveDown();
		}
	}
	
	private void initializeChildren(T n)
	{
		String[] childNames = n.getChildNames();
		for (int i = 0; i < childNames.length; ++i)
			setDefaultChild(n, i, childNames[i]);
	}
	
	// =========================================================================
	
	private void dispatch(
			T n,
			boolean explicit,
			HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		if (n == null)
			throw new NullPointerException();
		
		if (isSuppressed(n))
			return;
		
		marshalNode(n, explicit, writer, context);
	}
	
	private void storeType(Object obj, HierarchicalStreamWriter writer)
	{
		Class<? extends Object> type = obj.getClass();
		if (!isTypeInfoSuppressed(type))
			writer.addAttribute(ATTR_NAME_TYPE, getTypeAlias(type));
	}
	
	private Class<?> getExplicitType(HierarchicalStreamReader reader)
	{
		String typeName = reader.getAttribute(ATTR_NAME_TYPE);
		if (typeName == null)
			throw new SyntaxErrorException("Expected attribute '" + ATTR_NAME_TYPE + "'!");
		return getClassForAlias(typeName);
	}
	
	/**
	 * Explicit type attribute override the getter type.
	 */
	private Class<?> getTypeFromGetter(
			AstNode<T> parent,
			HierarchicalStreamReader reader)
	{
		String typeName = reader.getAttribute(ATTR_NAME_TYPE);
		if (typeName == null)
			return getGetterType(parent, reader.getNodeName());
		return getClassForAlias(typeName);
	}
	
	private void failOnUnexpectedChild(
			Class<?> nodeType,
			HierarchicalStreamReader reader)
	{
		throw new SyntaxErrorException("Unexpected child element: '" +
				reader.getNodeName() + "' when unmarshalling node of type '" +
				nodeType.getName() + "'");
	}
}
