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

import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.ptk.common.VisitingException;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.xml.WikiTechAstNode.Properties;

public class XmlWriter
		extends
		AstVisitor
{
	private ObjectFactory objectFactory;

	private OutputStream out;

	private DOMImplementation domImplementation;

	// =========================================================================

	public XmlWriter(OutputStream out)
	{
		this.out = out;
	}

	// =========================================================================

	@Override
	protected final boolean before(AstNode node)
	{
		try
		{
			domImplementation =
					DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
		} catch (ParserConfigurationException e)
		{
			throw new VisitingException(e);
		}

		objectFactory = new ObjectFactory();
		return super.before(node);
	}

	@Override
	protected Object after(AstNode node, Object result)
	{
		try
		{
			WikitechAst ast = this.objectFactory.createWikitechAst();
			ast.setNode((WikiTechAstNode) result);

			JAXBContext jaxbContext = JAXBContext.newInstance(
					"de.fau.cs.osr.ptk.common.xml");

			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			marshaller.marshal(ast, out);
		} catch (JAXBException e)
		{
			throw new VisitingException("Failed marshalling the AST", e);
		}

		return super.after(node, result);
	}

	// =========================================================================

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WikiTechAstNode visit(AstNode n) throws JAXBException
	{
		WikiTechAstNode node = this.objectFactory.createWikiTechAstNode();

		node.setType(n.getNodeTypeName());

		WikiTechAstNode.Properties properties = null;

		Set<String> propertyNames = n.getAttributes().keySet();
		if (!propertyNames.isEmpty())
		{
			properties = this.objectFactory.createWikiTechAstNodeProperties();

			for (String name : propertyNames)
				dumpProperty(properties, name, n.getAttribute(name));
		}

		AstNodePropertyIterator i = n.propertyIterator();
		while (i.next())
		{
			if (properties == null)
				properties = this.objectFactory.createWikiTechAstNodeProperties();

			dumpProperty(properties, i.getName(), i.getValue());
		}

		if (properties != null)
			node.setProperties(properties);

		if (!n.isEmpty())
		{
			WikiTechAstNode.Children children =
					this.objectFactory.createWikiTechAstNodeChildren();

			children.getNode().addAll((List) map(n));

			node.setChildren(children);
		}

		return node;
	}

	// =========================================================================

	protected void dumpProperty(Properties properties, String name, Object value) throws JAXBException
	{
		WikiTechAstProperty property = this.objectFactory.createWikiTechAstProperty();

		property.setName(name);

		if (value != null)
		{
			Class<?> clazz = value.getClass();
			if (clazz == Byte.class)
			{
				property.setByte((Byte) value);
			}
			else if (clazz == Short.class)
			{
				property.setShort((Short) value);
			}
			else if (clazz == Integer.class)
			{
				property.setInt((Integer) value);
			}
			else if (clazz == Long.class)
			{
				property.setLong((Long) value);
			}
			else if (clazz == Float.class)
			{
				property.setFloat((Float) value);
			}
			else if (clazz == Double.class)
			{
				property.setDouble((Double) value);
			}
			else if (clazz == Character.class)
			{
				property.setChar(((Character) value).toString());
			}
			else if (clazz == String.class)
			{
				property.setString((String) value);
			}
			else if (clazz == Boolean.class)
			{
				property.setBoolean((Boolean) value);
			}
			else
			{
				property.setObject(createDomFromObject(value));
				// property.setObject(createDomFromObject(value));
			}
		}

		properties.getProperty().add(property);
	}

	private de.fau.cs.osr.ptk.common.xml.WikiTechAstProperty.Object createDomFromObject(Object value)
			throws JAXBException
	{
		Class<? extends Object> clazz = value.getClass();
		JAXBContext jc = JAXBContext.newInstance(clazz);

		Marshaller marshaller = jc.createMarshaller();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		JAXBElement<? extends Object> jaxbElement =
				new JAXBElement(new QName(clazz.getName().replace('$', '-')), clazz, value);

		DOMResult result = new DOMResult();

		marshaller.marshal(jaxbElement, result);

		de.fau.cs.osr.ptk.common.xml.WikiTechAstProperty.Object o = new de.fau.cs.osr.ptk.common.xml.WikiTechAstProperty.Object();
		String name = clazz.getName();
		o.setClazz(name);
		o.setObject(result.getNode().getFirstChild());
		return o;
	}
}
