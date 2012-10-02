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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.Iterator;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.codec.binary.Base64;

import de.fau.cs.osr.ptk.common.ast.AstNodeInterface;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.GenericNodeList;
import de.fau.cs.osr.ptk.common.ast.GenericText;
import de.fau.cs.osr.ptk.common.ast.Location;
import de.fau.cs.osr.utils.NameAbbrevService;
import de.fau.cs.osr.utils.ReflectionUtils;

public class XmlReader<T extends AstNodeInterface<T>>
{
	private XMLEventReader reader;
	
	private NameAbbrevService abbrevService;
	
	private MyByteArrayInputStream bais;
	
	private ObjectInputStream ois;
	
	private Base64 b64;
	
	private final Class<? extends T> nodeClass;
	
	private final Class<? extends T> listClass;
	
	private final Class<? extends T> textClass;
	
	// =========================================================================
	/*
	
	public static T read(String xml) throws DeserializationException
	{
		return new XmlReader().deserialize(new StringReader(xml));
	}
	
	public static T read(String xml, NameAbbrevService as) throws DeserializationException
	{
		return new XmlReader().deserialize(new StringReader(xml), as);
	}
	
	public static T read(Reader reader) throws DeserializationException
	{
		return new XmlReader().deserialize(reader);
	}
	
	public static T read(Reader reader, NameAbbrevService as) throws DeserializationException
	{
		return new XmlReader().deserialize(reader, as);
	}
	
	*/
	// =========================================================================
	
	public XmlReader(
			Class<? extends T> nodeClass,
			Class<? extends T> listClass,
			Class<? extends T> textClass)
	{
		this.nodeClass = nodeClass;
		this.listClass = listClass;
		this.textClass = textClass;
		
		if (!nodeClass.isAssignableFrom(listClass))
			throw new IllegalArgumentException("An instance of listClass must be assignable to nodeClass");
		if (!nodeClass.isAssignableFrom(textClass))
			throw new IllegalArgumentException("An instance of textClass must be assignable to nodeClass");
		
		if (!GenericNodeList.class.isAssignableFrom(listClass))
			throw new IllegalArgumentException("An instance of listClass must be assignable to type " + GenericNodeList.class.getName());
		if (!GenericText.class.isAssignableFrom(textClass))
			throw new IllegalArgumentException("An instance of textClass must be assignable to type " + GenericText.class.getName());
	}
	
	// =========================================================================
	
	public T deserialize(Reader reader) throws DeserializationException
	{
		return deserialize(reader, new NameAbbrevService());
	}
	
	public T deserialize(
			Reader reader,
			NameAbbrevService abbrevService) throws DeserializationException
	{
		try
		{
			this.reader = XMLInputFactory.newInstance().createXMLEventReader(reader);
			
			this.abbrevService = abbrevService;
			
			return readAst();
		}
		catch (XMLStreamException e)
		{
			throw new DeserializationException(e);
		}
		catch (FactoryConfigurationError e)
		{
			throw new DeserializationException(e);
		}
	}
	
	// =========================================================================
	
	private T readAst() throws XMLStreamException, DeserializationException
	{
		XMLEvent event = null;
		while (reader.hasNext())
		{
			event = reader.nextEvent();
			if (event.isStartDocument())
				break;
		}
		
		if (event == null || !event.isStartDocument())
			throw new DeserializationException(event, "Expected document start");
		
		skipWhitespace();
		expectStartElement(XmlConstants.AST_QNAME);
		
		T root = readNodeListOrTextOrNode();
		
		expectEndElement();
		
		event = null;
		if (skipWhitespace())
		{
			event = reader.nextEvent();
			if (!event.isEndDocument())
				event = null;
		}
		
		if (event == null)
			throw new DeserializationException(event, "Expected document end");
		
		return root;
	}
	
	private T readNodeListOrTextOrNode() throws XMLStreamException, DeserializationException
	{
		XMLEvent event = null;
		if (skipWhitespace())
		{
			event = reader.nextEvent();
			if (event.isStartElement())
			{
				T node;
				
				StartElement elem = event.asStartElement();
				if (elem.getName().equals(XmlConstants.LIST_QNAME))
				{
					node = readNodeList(elem);
				}
				else if (elem.getName().equals(XmlConstants.TEXT_QNAME))
				{
					node = readText(elem);
				}
				else if (elem.getName().equals(XmlConstants.NULL_QNAME))
				{
					node = null;
				}
				else
				{
					node = readNode(elem);
				}
				
				if (node != null)
				{
					Attribute l = elem.getAttributeByName(XmlConstants.ATTR_LOCATION_QNAME);
					if (l != null)
						node.setNativeLocation(Location.valueOf(l.getValue()));
				}
				
				expectEndElement();
				return node;
			}
		}
		
		throw new DeserializationException(event, "Expected element");
	}
	
	private T readText(StartElement elem) throws XMLStreamException, DeserializationException
	{
		T text = instantiateNode(elem, textClass);
		((GenericText<T>) text).setContent(readChars());
		return (T) text;
	}
	
	private T readNodeList(StartElement elem) throws XMLStreamException, DeserializationException
	{
		T list = instantiateNode(elem, listClass);
		
		XMLEvent event = null;
		while (skipWhitespace())
		{
			event = reader.peek();
			if (event.isEndElement())
				break;
			
			list.add(checkNodeType(readNodeListOrTextOrNode()));
		}
		
		return list;
	}
	
	private T readNode(StartElement elem) throws XMLStreamException, DeserializationException
	{
		String name = elem.getName().getLocalPart();
		
		String className = XmlConstants.tagNameToClassName(name);
		
		Exception e;
		try
		{
			Class<?> clazz = abbrevService.resolve(className);
			
			T n = instantiateNode(elem, clazz);
			
			readNodeAttributes(n);
			
			readNodeProperties(n);
			
			readNodeChildren(n);
			
			return n;
		}
		catch (IOException e_)
		{
			e = e_;
		}
		catch (ClassNotFoundException e_)
		{
			e = e_;
		}
		
		String msg = "Cannot create AST node from name: " + name;
		throw new DeserializationException(elem, msg, e);
	}
	
	private void readNodeAttributes(T n) throws XMLStreamException, DeserializationException, IOException
	{
		XMLEvent event = null;
		while (skipWhitespace())
		{
			event = reader.peek();
			if (!event.isStartElement())
				return;
			
			StartElement elem = event.asStartElement();
			if (!elem.getName().equals(XmlConstants.ATTR_QNAME))
				return;
			
			readNodeAttribute(reader.nextEvent().asStartElement(), n);
			
			expectEndElement();
		}
	}
	
	private void readNodeAttribute(StartElement elem, T n) throws XMLStreamException, DeserializationException
	{
		String attrName = null;
		boolean isNull = false;
		int isArray = 0;
		
		@SuppressWarnings("unchecked")
		Iterator<Attribute> iterator = elem.getAttributes();
		while (iterator.hasNext())
		{
			Attribute attribute = iterator.next();
			
			QName name = attribute.getName();
			if (name.equals(XmlConstants.ATTR_NAME_QNAME))
			{
				attrName = attribute.getValue();
			}
			else if (name.equals(XmlConstants.ATTR_ARRAY_QNAME))
			{
				isArray = Integer.valueOf(attribute.getValue());
			}
			else if (name.equals(XmlConstants.ATTR_NULL_QNAME))
			{
				isNull = Boolean.valueOf(attribute.getValue());
			}
		}
		
		if (attrName == null)
			throw new DeserializationException(elem, "Missing `name' attribute");
		
		if (!isNull)
		{
			StartElement valueElem = expectStartElement(null, false);
			
			String className = XmlConstants.tagNameToClassName(
					valueElem.getName().getLocalPart());
			
			Exception e;
			try
			{
				Class<?> clazz = abbrevService.resolve(className);
				
				if (isArray > 0)
					clazz = ReflectionUtils.arrayClassFor(clazz, isArray);
				
				n.setAttribute(attrName, unmarshal(clazz));
				
				return;
			}
			catch (ClassNotFoundException e_)
			{
				e = e_;
			}
			catch (JAXBException e_)
			{
				e = e_;
			}
			catch (NumberFormatException e_)
			{
				e = e_;
			}
			catch (IOException e_)
			{
				e = e_;
			}
			
			String msg = "Unable to deserialize attribute: " + attrName;
			throw new DeserializationException(elem, msg, e);
		}
		else
		{
			n.setAttribute(attrName, null);
		}
	}
	
	private void readNodeProperties(T n) throws XMLStreamException, DeserializationException
	{
		AstNodePropertyIterator i = n.propertyIterator();
		while (i.next())
		{
			String name = i.getName();
			
			StartElement elem = expectStartElement(new QName(name), false);
			
			Attribute isNullAttr = elem.getAttributeByName(XmlConstants.ATTR_NULL_QNAME);
			if (isNullAttr != null && Boolean.valueOf(isNullAttr.getValue()))
			{
				expectStartElement(null);
				i.setValue(null);
				expectEndElement();
				continue;
			}
			
			Exception e;
			try
			{
				i.setValue(unmarshal(getPropertyType(n.getClass(), name)));
				
				continue;
			}
			catch (JAXBException e_)
			{
				e = e_;
			}
			catch (NumberFormatException e_)
			{
				e = e_;
			}
			catch (NoSuchMethodException e_)
			{
				e = e_;
			}
			catch (SecurityException e_)
			{
				e = e_;
			}
			catch (ClassNotFoundException e_)
			{
				e = e_;
			}
			catch (IOException e_)
			{
				e = e_;
			}
			
			String msg = "Unable to deserialize node property: " + name;
			throw new DeserializationException(elem, msg, e);
		}
	}
	
	private void readNodeChildren(T n) throws XMLStreamException, DeserializationException
	{
		if (n.isList())
		{
			XMLEvent event = null;
			while (skipWhitespace())
			{
				event = reader.peek();
				if (event.isEndElement())
					break;
				
				n.add(checkNodeType(readNodeListOrTextOrNode()));
			}
		}
		else
		{
			for (int i = 0; i < n.getChildNames().length; ++i)
			{
				expectStartElement(new QName(n.getChildNames()[i]));
				
				T child = readNodeListOrTextOrNode();
				
				n.set(i, child);
				
				expectEndElement();
			}
		}
	}
	
	// =========================================================================
	
	private StartElement expectStartElement(QName qname) throws XMLStreamException, DeserializationException
	{
		return expectStartElement(qname, true);
	}
	
	private StartElement expectStartElement(QName qname, boolean consume) throws XMLStreamException, DeserializationException
	{
		XMLEvent event = null;
		if (skipWhitespace())
		{
			event = consume ? reader.nextEvent() : reader.peek();
			if (event.isStartElement())
			{
				StartElement e = event.asStartElement();
				if (qname == null || e.getName().equals(qname))
					return e;
			}
		}
		
		throw new DeserializationException(event, "Expected element: " + qname);
	}
	
	private void expectEndElement() throws XMLStreamException, DeserializationException
	{
		XMLEvent event = null;
		if (skipWhitespace())
		{
			event = reader.nextEvent();
			if (event.isEndElement())
				return;
		}
		
		throw new DeserializationException(event, "Expected end element");
	}
	
	private boolean skipWhitespace() throws XMLStreamException
	{
		while (true)
		{
			if (!reader.hasNext())
				return false;
			
			XMLEvent event = reader.peek();
			if (!event.isCharacters() || !isWhitespace(event.asCharacters().getData()))
				return true;
			
			reader.nextEvent();
		}
	}
	
	private boolean isWhitespace(String data)
	{
		for (int i = 0; i < data.length(); ++i)
		{
			if (!Character.isWhitespace(data.charAt(i)))
				return false;
		}
		return true;
	}
	
	private String readChars() throws XMLStreamException
	{
		StringBuilder b = new StringBuilder();
		
		while (reader.hasNext())
		{
			XMLEvent event = reader.peek();
			if (!event.isCharacters())
				break;
			
			event = reader.nextEvent();
			
			b.append(event.asCharacters().getData());
		}
		
		return b.toString();
	}
	
	// =========================================================================
	
	private Class<?> getPropertyType(Class<?> clazz, String name) throws NoSuchMethodException, SecurityException
	{
		String head = ("" + name.charAt(0)).toUpperCase();
		String tail = name.substring(1);
		String getterName = "get" + head + tail;
		return clazz.getMethod(getterName).getReturnType();
	}
	
	private T checkNodeType(Object o)
	{
		if (o == null || nodeClass.isInstance(o))
		{
			@SuppressWarnings("unchecked")
			T node = (T) o;
			return node;
		}
		else
		{
			throw new ClassCastException("Cannot cast " + o.getClass().getName() + " to " + nodeClass.getName());
		}
	}
	
	private T instantiateNode(XMLEvent event, Class<?> clazz) throws DeserializationException
	{
		Exception e = null;
		try
		{
			return checkNodeType(clazz.newInstance());
		}
		catch (InstantiationException e_)
		{
			e = e_;
		}
		catch (IllegalAccessException e_)
		{
			e = e_;
		}
		
		throw new DeserializationException(event, "Cannot create AST node `" + clazz.getName() + "'", e);
	}
	
	// =========================================================================
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object unmarshal(Class<?> clazz) throws XMLStreamException, DeserializationException, JAXBException, NumberFormatException, IOException, ClassNotFoundException
	{
		Object value = unmarshalPrimitive(clazz);
		
		if (value == null)
		{
			/*
			JAXBContext jc = JAXBContext.newInstance(clazz);
			
			Unmarshaller u = jc.createUnmarshaller();
			
			value = u.unmarshal(reader, clazz).getValue();
			*/
			
			if (Enum.class.isAssignableFrom(clazz))
			{
				expectStartElement(null);
				String chars = readChars();
				expectEndElement();
				
				return Enum.valueOf((Class) clazz, chars);
			}
			else if (nodeClass.isAssignableFrom(clazz))
			{
				expectStartElement(null);
				T node = readNodeListOrTextOrNode();
				expectEndElement();
				return node;
			}
			else
			{
				if (bais == null)
				{
					b64 = new Base64();
					bais = new MyByteArrayInputStream();
				}
				
				expectStartElement(null);
				String chars = readChars();
				expectEndElement();
				
				bais.setBuf(b64.decode(chars));
				
				if (ois == null)
					ois = new ObjectInputStream(bais);
				
				value = ois.readObject();
			}
		}
		
		return value;
	}
	
	private Object unmarshalPrimitive(Class<?> clazz) throws XMLStreamException, DeserializationException, NumberFormatException
	{
		Object result = null;
		
		if (clazz == Byte.class || clazz == byte.class)
		{
			expectStartElement(null);
			result = Byte.valueOf(readChars());
			expectEndElement();
		}
		else if (clazz == Short.class || clazz == short.class)
		{
			expectStartElement(null);
			result = Short.valueOf(readChars());
			expectEndElement();
		}
		else if (clazz == Integer.class || clazz == int.class)
		{
			expectStartElement(null);
			result = Integer.valueOf(readChars());
			expectEndElement();
		}
		else if (clazz == Long.class || clazz == long.class)
		{
			expectStartElement(null);
			result = Long.valueOf(readChars());
			expectEndElement();
		}
		else if (clazz == Float.class || clazz == float.class)
		{
			expectStartElement(null);
			result = Float.valueOf(readChars());
			expectEndElement();
		}
		else if (clazz == Double.class || clazz == double.class)
		{
			expectStartElement(null);
			result = Double.valueOf(readChars());
			expectEndElement();
		}
		else if (clazz == Boolean.class || clazz == boolean.class)
		{
			expectStartElement(null);
			result = Boolean.valueOf(readChars());
			expectEndElement();
		}
		else if (clazz == Character.class || clazz == char.class)
		{
			expectStartElement(null);
			String chars = readChars();
			result = (char) ((chars.length() >= 1) ? chars.charAt(0) : (char) -1);
			expectEndElement();
		}
		else if (clazz == String.class)
		{
			expectStartElement(null);
			result = readChars();
			expectEndElement();
		}
		
		return result;
	}
	
	// =========================================================================
	
	private final class MyByteArrayInputStream
			extends
				ByteArrayInputStream
	{
		private MyByteArrayInputStream()
		{
			super(new byte[0]);
		}
		
		public void setBuf(byte buf[])
		{
			this.buf = buf;
			this.pos = 0;
			this.count = buf.length;
		}
	}
}
