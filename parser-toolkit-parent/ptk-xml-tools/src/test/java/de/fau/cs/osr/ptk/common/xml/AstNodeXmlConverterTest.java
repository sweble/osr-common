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

import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.ctnDoc;
import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.ctnObjProp;
import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.ctnPropContent;
import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.ctnSection;
import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.ctnText;
import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.ctnUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import de.fau.cs.osr.ptk.common.ast.AstLocation;
import de.fau.cs.osr.ptk.common.comparer.AstComparer;
import de.fau.cs.osr.ptk.common.test.nodes.CtnBody;
import de.fau.cs.osr.ptk.common.test.nodes.CtnDocument;
import de.fau.cs.osr.ptk.common.test.nodes.CtnNodeWithPropAndContent;
import de.fau.cs.osr.ptk.common.test.nodes.CtnSection;
import de.fau.cs.osr.ptk.common.test.nodes.CtnText;
import de.fau.cs.osr.ptk.common.test.nodes.CtnTitle;
import de.fau.cs.osr.ptk.common.test.nodes.CtnUrl;
import de.fau.cs.osr.utils.ComparisonException;

public class AstNodeXmlConverterTest
		extends
			AstNodeXmlConverterTestBase
{
	@Before
	public void before()
	{
		super.before();
		setupDefaultNodeFactory();
		setupDefaultTypeMappings();
	}

	@Test
	public void testRoundTripWithImplicitRoots() throws Exception
	{
		CtnDocument doc = ctnDoc();
		roundtrip(doc);
	}

	@Test
	public void testRoundTripWithExplicitRoots() throws Exception
	{
		getConverter().setExplicitRoots(true);

		CtnDocument doc = ctnDoc();
		roundtrip(doc);
	}

	@Test
	public void testXmlFormatWithImplicitRoots() throws Exception
	{
		org.w3c.dom.Document doc = parseXml(serialize(ctnDoc()));

		// Has correct root node?
		String root = "/" + CtnDocument.class.getName().replace("$", "_-");
		assertNotNull(queryNode(root, doc));

		// Implicit root has no other children?
		assertEquals(0, queryNodeSet(root + "/*", doc).getLength());
	}

	@Test
	public void testXmlFormatWithExplicitRoots() throws Exception
	{
		CtnDocument doc = ctnDoc();
		getConverter().setExplicitRoots(true);

		org.w3c.dom.Document xmlDoc = parseXml(serialize(doc));

		// Has correct root node?
		String root = "/" + CtnDocument.class.getName().replace("$", "_-");
		assertNotNull(queryNode(root, xmlDoc));

		// Has one explicit element as root?
		assertEquals(1, queryNodeSet(root + "/document", xmlDoc).getLength());

		// Explicit root has no other children?
		assertEquals(0, queryNodeSet(root + "/document/*", xmlDoc).getLength());
	}

	@Test
	public void testSerializationOfIntAttribute() throws Exception
	{
		CtnDocument doc = ctnDoc();
		doc.setAttribute("int", 5);
		roundtrip(doc);
	}

	@Test
	public void testSerializationOfStringAttribute() throws Exception
	{
		CtnDocument doc = ctnDoc();
		doc.setAttribute("str", "Hello World");
		roundtrip(doc);
	}

	@Test
	public void testSerializationOfPropertyWithArbitraryObjectAsValue() throws Exception
	{
		ArbitraryObj obj = new ArbitraryObj();
		obj.set();

		CtnDocument doc = ctnDoc(ctnObjProp(obj));
		roundtrip(doc);
	}

	@Test
	public void testSerializationOfPropertyWithNodeAsValue() throws Exception
	{
		ArbitraryObj obj = new ArbitraryObj();
		obj.set();

		CtnDocument doc = ctnDoc(ctnObjProp(ctnUrl().build()));
		roundtrip(doc);
	}

	@Test
	public void testSerializationOfPropertyWithNodeAsValueAndExplicitRoots() throws Exception
	{
		getConverter().setExplicitRoots(true);

		ArbitraryObj obj = new ArbitraryObj();
		obj.set();

		CtnDocument doc = ctnDoc(ctnObjProp(ctnUrl().build()));
		roundtrip(doc);
	}

	@Test
	public void testSerializationOfObjectArray() throws Exception
	{
		CtnDocument doc = ctnDoc();
		doc.setAttribute("array", new Object[] { ctnText("Hallo"), ctnUrl().build() });
		roundtrip(doc);
	}

	@Test
	public void testNoContentPropertyFoundWhenTextNodeTypeSet() throws Exception
	{
		CtnDocument doc = ctnDoc(ctnText("Hallo"));
		assertFalse(serialize(doc).contains("<content>"));

		getConverter().setStringNodeType(null);
		assertTrue(serialize(doc).contains("<content>"));
	}

	@Test
	public void testAttributesOnTextNodeForceContentElement() throws Exception
	{
		CtnText text = ctnText("Hallo");
		text.setAttribute("ruins", "it");
		CtnDocument doc = ctnDoc(text);
		assertTrue(serialize(doc).contains("<content>"));
	}

	@Test
	public void testWrapAstInArticleContainerAndRoundtrip() throws Exception
	{
		CtnDocument doc = ctnDoc(ctnText("Hallo Welt"));
		ArticleContainer ac = new ArticleContainer(doc);

		String xml = serialize(ac);

		ArticleContainer restoredAc = (ArticleContainer) deserialize(xml);

		try
		{
			AstComparer.compareAndThrow(doc, restoredAc.doc, true, true);
		}
		catch (ComparisonException e)
		{
			printXml(xml);
			printRestoredXml(restoredAc);
			throw e;
		}
	}

	@Test
	public void testWrapAstInArticleContainerAndCheckXml() throws Exception
	{
		ArticleContainer ac =
				new ArticleContainer(ctnDoc(ctnText("Hallo Welt")));

		org.w3c.dom.Document doc = parseXml(serialize(ac));

		Element rootElem = (Element) queryNode("/article-container", doc);
		assertNotNull(rootElem);
		assertEquals(ArticleContainer.XMLNS, rootElem.getAttribute("xmlns"));
		assertEquals(ArticleContainer.XMLNS_PTK, rootElem.getAttribute("xmlns:ptk"));

		assertEquals(1, queryNodeSet("/article-container/*", doc).getLength());
		assertNotNull(queryNode("/article-container/document", doc));

		assertEquals(1, queryNodeSet("/article-container/document/*", doc).getLength());
		assertNotNull(queryNode("/article-container/document/text[text() = 'Hallo Welt']", doc));
	}

	@Test
	public void testInstantiationOfNullReplacementProperty() throws Exception
	{
		CtnDocument doc = ctnDoc(ctnUrl().withProtocol("").build());
		getConverter().setSuppressEmptyStringProperties(true);

		org.w3c.dom.Document xmlDoc = parseXml(serialize(doc));
		assertEquals(0, queryNodeSet("//protocol", xmlDoc).getLength());

		roundtrip(doc);
	}

	@Test
	public void testNullProperty() throws Exception
	{
		CtnDocument doc = ctnDoc(ctnObjProp(null));
		org.w3c.dom.Document xmlDoc = parseXml(serialize(doc));
		assertEquals(0, queryNodeSet("//prop", xmlDoc).getLength());
		roundtrip(doc);
	}

	@Test
	public void testSuppressedProperty() throws Exception
	{
		CtnDocument doc = ctnDoc(ctnObjProp("Hello World"));
		getConverter().suppressProperty("prop");
		org.w3c.dom.Document xmlDoc = parseXml(serialize(doc));
		assertEquals(0, queryNodeSet("//prop", xmlDoc).getLength());
	}

	@Test
	public void testNodeWithContentAndAnotherProperty() throws Exception
	{
		// First make sure that the node is properly recognized as string node
		CtnDocument doc = ctnDoc(ctnPropContent(null, "Hello World"));
		getConverter().setStringNodeType(CtnNodeWithPropAndContent.class);
		org.w3c.dom.Document xmlDoc = parseXml(serialize(doc));
		assertEquals(0, queryNodeSet("//content", xmlDoc).getLength());
		roundtrip(doc);

		doc = ctnDoc(ctnPropContent(42, "Hello World"));
		getConverter().setStringNodeType(CtnNodeWithPropAndContent.class);
		xmlDoc = parseXml(serialize(doc));
		assertEquals(1, queryNodeSet("//content", xmlDoc).getLength());
		roundtrip(doc);
	}

	@Test
	public void testNodeWithAtLeastTwoNamedChildren() throws Exception
	{
		CtnDocument doc = ctnDoc(ctnSection().build());
		org.w3c.dom.Document xmlDoc = parseXml(serialize(doc));
		assertNotNull(queryNode("//title", xmlDoc));
		assertNotNull(queryNode("//body", xmlDoc));
		roundtrip(doc);
	}

	@Test
	public void testStoreLocation() throws Exception
	{
		CtnDocument doc = astWithLocations();
		roundtrip(doc);
		org.w3c.dom.Document xmlDoc = parseXml(serialize(doc));
		assertNotNull(((Element) queryNode("//text", xmlDoc)).getAttributeNode("ptk:location"));
		assertNotNull(((Element) queryNode("//url", xmlDoc)).getAttributeNode("ptk:location"));
	}

	@Test
	public void testSuppressLocation() throws Exception
	{
		CtnDocument doc = astWithLocations();
		getConverter().setStoreLocation(false);
		org.w3c.dom.Document xmlDoc = parseXml(serialize(doc));
		assertNull(((Element) queryNode("//text", xmlDoc)).getAttributeNode("ptk:location"));
		assertNull(((Element) queryNode("//url", xmlDoc)).getAttributeNode("ptk:location"));
	}

	private CtnDocument astWithLocations()
	{
		CtnText text = ctnText("Hello");
		text.setNativeLocation(new AstLocation("some file", 42, 43));
		CtnUrl url = ctnUrl().build();
		url.setNativeLocation(new AstLocation("some file", 44, 45));
		CtnDocument doc = ctnDoc(text, url);
		return doc;
	}

	@Test
	public void testStoreNodesWithAttributes() throws Exception
	{
		CtnDocument doc = astWithAttributes();
		roundtrip(doc);
	}

	@Test
	public void testSuppressCertainAttributes() throws Exception
	{
		CtnDocument doc = astWithAttributes();
		getConverter().suppressAttribute("area52");
		String xml = serialize(doc);
		assertTrue(xml.contains("area51"));
		assertFalse(xml.contains("area52"));
	}

	@Test
	public void testSuppressAllAttributes() throws Exception
	{
		CtnDocument doc = astWithAttributes();
		getConverter().setStoreAttributes(false);
		String xml = serialize(doc);
		assertFalse(xml.contains("area51"));
		assertFalse(xml.contains("area52"));
	}

	private CtnDocument astWithAttributes()
	{
		CtnUrl url = ctnUrl().build();
		url.setAttribute("area51", "Hello World 1");
		url.setAttribute("area52", "Hello World 2");
		CtnDocument doc = ctnDoc(url);
		return doc;
	}

	@Test
	public void testStoreNullAttribute() throws Exception
	{
		CtnUrl url = ctnUrl().build();
		url.setAttribute("area51", null);
		CtnDocument doc = ctnDoc(url);
		roundtrip(doc);
	}

	@Test
	public void testBodyInterfaceNode() throws Exception
	{
		CtnSection sec = ctnSection().build();
		CtnDocument doc = ctnDoc(sec);
		assertEquals(CtnBody.CtnBodyImpl.class, sec.getBody().getClass());
		assertTrue(sec.hasBody());
		org.w3c.dom.Document xmlDoc = parseXml(serialize(doc));
		assertNotNull(queryNode("//section/body", xmlDoc));
		roundtrip(doc);
	}

	@Test
	public void testNoBodyNode() throws Exception
	{
		CtnSection sec = ctnSection().build();
		sec.removeBody();
		CtnDocument doc = ctnDoc(sec);
		assertEquals(CtnBody.CtnNoBody.class, sec.getBody().getClass());
		assertFalse(sec.hasBody());

		roundtrip(doc);

		String xml = serialize(doc);
		org.w3c.dom.Document xmlDoc = parseXml(xml);
		assertNull(queryNode("//section/body", xmlDoc));

		CtnDocument restoredDoc = (CtnDocument) deserialize(xml);
		assertFalse(((CtnSection) restoredDoc.get(0)).hasBody());
	}

	@Test
	public void testNoTitleNodeToCoverAllPaths() throws Exception
	{
		CtnSection sec = ctnSection().build();
		sec.removeTitle();
		CtnDocument doc = ctnDoc(sec);
		assertEquals(CtnTitle.CtnNoTitle.class, sec.getTitle().getClass());
		assertFalse(sec.hasTitle());
		assertTrue(sec.hasBody());

		roundtrip(doc);

		String xml = serialize(doc);
		org.w3c.dom.Document xmlDoc = parseXml(xml);
		assertNull(queryNode("//section/title", xmlDoc));

		CtnDocument restoredDoc = (CtnDocument) deserialize(xml);
		assertFalse(((CtnSection) restoredDoc.get(0)).hasTitle());
	}

	@Test
	public void testStoreComplexArrayAsAttribute() throws Exception
	{
		CtnDocument doc = ctnDoc();
		doc.setAttribute("DoubleTrouble", new Double[][] {
				new Double[] { 3.1415, 2 * 3.1415 },
				new Double[] { 2.7182, 2 * 2.7182 } });
		roundtrip(doc);
	}
}
