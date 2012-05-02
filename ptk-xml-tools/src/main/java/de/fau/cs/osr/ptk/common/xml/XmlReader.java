package de.fau.cs.osr.ptk.common.xml;

import java.io.StringReader;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import de.fau.cs.osr.utils.NameAbbrevService;
import de.fau.cs.osr.utils.ReflectionUtils;

public class XmlReader
{
	private XMLEventReader reader;
	
	private NameAbbrevService abbrevService;
	
	// =========================================================================
	
	public static AstNode read(String xml) throws DeserializationException
	{
		return new XmlReader().deserialize(xml);
	}
	
	public static AstNode read(String xml, NameAbbrevService as) throws DeserializationException
	{
		return new XmlReader().deserialize(xml, as);
	}
	
	// =========================================================================
	
	public XmlReader()
	{
	}
	
	// =========================================================================
	
	public AstNode deserialize(String xml) throws DeserializationException
	{
		return deserialize(xml, new NameAbbrevService());
	}
	
	public AstNode deserialize(String xml, NameAbbrevService abbrevService) throws DeserializationException
	{
		try
		{
			reader = XMLInputFactory.newInstance().createXMLEventReader(
					new StringReader(xml));
			
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
	
	private AstNode readAst() throws XMLStreamException, DeserializationException
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
		
		AstNode root = readNodeListOrTextOrNode();
		
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
	
	private AstNode readNodeListOrTextOrNode() throws XMLStreamException, DeserializationException
	{
		XMLEvent event = null;
		if (skipWhitespace())
		{
			event = reader.nextEvent();
			if (event.isStartElement())
			{
				AstNode node;
				
				StartElement elem = event.asStartElement();
				if (elem.getName().equals(XmlConstants.LIST_QNAME))
				{
					node = readNodeList(elem);
				}
				else if (elem.getName().equals(XmlConstants.TEXT_QNAME))
				{
					node = readText(elem);
				}
				else
				{
					node = readNode(elem);
				}
				
				expectEndElement();
				return node;
			}
		}
		
		throw new DeserializationException(event, "Expected element");
	}
	
	private AstNode readText(StartElement e) throws XMLStreamException
	{
		return new Text(readChars());
	}
	
	private AstNode readNodeList(StartElement elem) throws XMLStreamException, DeserializationException
	{
		NodeList list = new NodeList();
		
		XMLEvent event = null;
		while (skipWhitespace())
		{
			event = reader.peek();
			if (event.isEndElement())
				break;
			
			list.add(readNodeListOrTextOrNode());
		}
		
		return list;
	}
	
	private AstNode readNode(StartElement elem) throws XMLStreamException, DeserializationException
	{
		String name = elem.getName().getLocalPart();
		
		Exception e;
		try
		{
			Class<?> clazz = abbrevService.resolve(name);
			
			AstNode n = (AstNode) clazz.newInstance();
			
			readNodeAttributes(n);
			
			readNodeProperties(n);
			
			readNodeChildren(n);
			
			return n;
		}
		catch (InstantiationException e_)
		{
			e = e_;
		}
		catch (IllegalAccessException e_)
		{
			e = e_;
		}
		catch (ClassCastException e_)
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
	
	private void readNodeAttributes(AstNode n) throws XMLStreamException, DeserializationException
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
	
	private void readNodeAttribute(StartElement elem, AstNode n) throws XMLStreamException, DeserializationException
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
			
			String msg = "Unable to deserialize attribute: " + attrName;
			throw new DeserializationException(elem, msg, e);
		}
		else
		{
			n.setAttribute(attrName, null);
		}
	}
	
	private void readNodeProperties(AstNode n) throws XMLStreamException, DeserializationException
	{
		AstNodePropertyIterator i = n.propertyIterator();
		while (i.next())
		{
			String name = i.getName();
			
			StartElement elem = expectStartElement(new QName(name), false);
			
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
			
			String msg = "Unable to deserialize node property: " + name;
			throw new DeserializationException(elem, msg, e);
		}
	}
	
	private void readNodeChildren(AstNode n) throws XMLStreamException, DeserializationException
	{
		for (int i = 0; i < n.getChildNames().length; ++i)
		{
			expectStartElement(new QName(n.getChildNames()[i]));
			
			AstNode child = readNodeListOrTextOrNode();
			
			n.set(i, child);
			
			expectEndElement();
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
	
	// =========================================================================
	
	private Object unmarshal(Class<?> clazz) throws XMLStreamException, DeserializationException, JAXBException, NumberFormatException
	{
		Object value = unmarshalPrimitive(clazz);
		
		if (value == null)
		{
			JAXBContext jc = JAXBContext.newInstance(clazz);
			
			Unmarshaller u = jc.createUnmarshaller();
			
			value = u.unmarshal(reader, clazz).getValue();
		}
		
		return value;
	}
	
	private Object unmarshalPrimitive(Class<?> clazz) throws XMLStreamException, DeserializationException, NumberFormatException
	{
		if (clazz == Byte.class || clazz == byte.class)
		{
			expectStartElement(null);
			Byte valueOf = Byte.valueOf(readChars());
			expectEndElement();
			return valueOf;
		}
		else if (clazz == Short.class || clazz == short.class)
		{
			expectStartElement(null);
			Short valueOf = Short.valueOf(readChars());
			expectEndElement();
			return valueOf;
		}
		else if (clazz == Integer.class || clazz == int.class)
		{
			expectStartElement(null);
			Integer valueOf = Integer.valueOf(readChars());
			expectEndElement();
			return valueOf;
		}
		else if (clazz == Long.class || clazz == long.class)
		{
			expectStartElement(null);
			Long valueOf = Long.valueOf(readChars());
			expectEndElement();
			return valueOf;
		}
		else if (clazz == Float.class || clazz == float.class)
		{
			expectStartElement(null);
			Float valueOf = Float.valueOf(readChars());
			expectEndElement();
			return valueOf;
		}
		else if (clazz == Double.class || clazz == double.class)
		{
			expectStartElement(null);
			Double valueOf = Double.valueOf(readChars());
			expectEndElement();
			return valueOf;
		}
		else if (clazz == Boolean.class || clazz == boolean.class)
		{
			expectStartElement(null);
			Boolean valueOf = Boolean.valueOf(readChars());
			expectEndElement();
			return valueOf;
		}
		else if (clazz == Character.class || clazz == char.class)
		{
			expectStartElement(null);
			String chars = readChars();
			char ch = (chars.length() >= 1) ? chars.charAt(0) : (char) -1;
			expectEndElement();
			return ch;
		}
		else
		{
			return null;
		}
	}
}
