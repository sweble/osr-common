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

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.fau.cs.osr.ptk.common.comparer.AstComparer;
import de.fau.cs.osr.ptk.common.serialization.SimpleTypeNameMapper;
import de.fau.cs.osr.ptk.common.test.nodes.CtnBody;
import de.fau.cs.osr.ptk.common.test.nodes.CtnDocument;
import de.fau.cs.osr.ptk.common.test.nodes.CtnFactory;
import de.fau.cs.osr.ptk.common.test.nodes.CtnIdNode;
import de.fau.cs.osr.ptk.common.test.nodes.CtnNode;
import de.fau.cs.osr.ptk.common.test.nodes.CtnNodeList;
import de.fau.cs.osr.ptk.common.test.nodes.CtnNodeWithObjProp;
import de.fau.cs.osr.ptk.common.test.nodes.CtnNodeWithPropAndContent;
import de.fau.cs.osr.ptk.common.test.nodes.CtnSection;
import de.fau.cs.osr.ptk.common.test.nodes.CtnText;
import de.fau.cs.osr.ptk.common.test.nodes.CtnTitle;
import de.fau.cs.osr.ptk.common.test.nodes.CtnUrl;
import de.fau.cs.osr.utils.ComparisonException;

public class AstNodeXmlConverterTestBase
{
	private XStream xstream;

	private AstNodeXmlConverter<CtnNode> converter;

	// =========================================================================

	@Before
	public void before()
	{
		converter = AstNodeXmlConverter.forNodeType(CtnNode.class);
		converter.setStringNodeType(CtnText.class);

		xstream = new XStream(new DomDriver());
		xstream.registerConverter(converter);
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.processAnnotations(ArticleContainer.class);
	}

	public void setupDefaultNodeFactory()
	{
		converter.setNodeFactory(CtnFactory.get());
	}

	public void setupDefaultTypeMappings()
	{
		SimpleTypeNameMapper typeNameMapper = new SimpleTypeNameMapper();
		typeNameMapper.add(CtnText.class, "text");
		typeNameMapper.add(CtnNodeList.class, "list");
		typeNameMapper.add(CtnSection.class, "section");
		typeNameMapper.add(CtnTitle.class, "title");
		typeNameMapper.add(CtnBody.class, "body");
		typeNameMapper.add(CtnDocument.class, "document");
		typeNameMapper.add(CtnIdNode.class, "id");
		typeNameMapper.add(CtnUrl.class, "url");
		typeNameMapper.add(CtnNodeWithObjProp.class, "nwop");
		typeNameMapper.add(CtnNodeWithPropAndContent.class, "nwpac");
		converter.setTypeNameMapper(typeNameMapper);

		converter.suppressNode(CtnBody.CtnNoBody.class);
		converter.suppressNode(CtnTitle.CtnNoTitle.class);

		converter.suppressTypeInfo(CtnBody.CtnEmptyBody.class);
		converter.suppressTypeInfo(CtnBody.CtnBodyImpl.class);
		converter.suppressTypeInfo(CtnTitle.CtnEmptyTitle.class);
		converter.suppressTypeInfo(CtnTitle.CtnTitleImpl.class);
	}

	public AstNodeXmlConverter<CtnNode> getConverter()
	{
		return converter;
	}

	public XStream getXstream()
	{
		return xstream;
	}

	public String serialize(Object what)
	{
		return xstream.toXML(what);
	}

	public Object deserialize(String xml)
	{
		return xstream.fromXML(xml);
	}

	public void roundtrip(CtnNode node) throws ComparisonException
	{
		String xml = serialize(node);

		CtnNode restoredNode = (CtnNode) deserialize(xml);

		try
		{
			AstComparer.compareAndThrow(node, restoredNode, true, true);
		}
		catch (ComparisonException e)
		{
			printXml(xml);
			printRestoredXml(restoredNode);
			throw e;
		}
	}

	public void printSerialized(CtnNode node)
	{
		System.out.println(StringUtils.repeat("=", 80));
		System.out.println("\"\"\"" + serialize(node) + "\"\"\"");
		System.out.println(StringUtils.repeat("=", 80));
	}

	public void printXml(String xml)
	{
		System.err.println("Original XML:");
		System.err.println(StringUtils.repeat("=", 80));
		System.err.println("\"\"\"" + xml + "\"\"\"");
		System.err.println(StringUtils.repeat("=", 80));
	}

	public void printRestoredXml(Object restoredNode)
	{
		System.err.println("XML after round trip:");
		System.err.println(StringUtils.repeat("=", 80));
		try
		{
			System.err.println("\"\"\"" + serialize(restoredNode) + "\"\"\"");
		}
		catch (Exception e)
		{
			System.err.println("Failed to serialize restored AST!");
			e.printStackTrace(System.err);
		}
		System.err.println(StringUtils.repeat("=", 80));
	}

	public Document parseXml(String xml) throws Exception
	{
		ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(is);
		is.close();
		doc.getDocumentElement().normalize();
		return doc;
	}

	public Node queryNode(String expression, Node doc) throws Exception
	{
		return (Node) xpath(expression, doc, XPathConstants.NODE);
	}

	public NodeList queryNodeSet(String expression, Node doc) throws Exception
	{
		return (NodeList) xpath(expression, doc, XPathConstants.NODESET);
	}

	public Object xpath(String expression, Node doc, QName returnType) throws Exception
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		return xpath.evaluate(expression, doc, returnType);
	}
}
