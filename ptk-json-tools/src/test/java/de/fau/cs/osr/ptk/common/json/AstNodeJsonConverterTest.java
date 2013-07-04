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

package de.fau.cs.osr.ptk.common.json;

import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.fau.cs.osr.ptk.common.ast.AstLocation;
import de.fau.cs.osr.ptk.common.test.nodes.CtnBody;
import de.fau.cs.osr.ptk.common.test.nodes.CtnDocument;
import de.fau.cs.osr.ptk.common.test.nodes.CtnNodeWithPropAndContent;
import de.fau.cs.osr.ptk.common.test.nodes.CtnSection;
import de.fau.cs.osr.ptk.common.test.nodes.CtnText;
import de.fau.cs.osr.ptk.common.test.nodes.CtnTitle;
import de.fau.cs.osr.ptk.common.test.nodes.CtnUrl;

public class AstNodeJsonConverterTest
		extends
			AstNodeJsonConverterTestBase
{
	@Before
	public void before()
	{
		super.before();
		setupDefaultNodeFactory();
		setupDefaultTypeMappings();
	}
	
	@Test
	public void testRoundTripWithRoots() throws Exception
	{
		CtnDocument doc = astDoc();
		roundtrip(doc);
	}
	
	@Test
	public void testSerializationOfIntAttribute() throws Exception
	{
		CtnDocument doc = astDoc();
		doc.setAttribute("int", 5);
		roundtrip(doc);
	}
	
	@Test
	public void testSerializationOfStringAttribute() throws Exception
	{
		CtnDocument doc = astDoc();
		doc.setAttribute("str", "Hello World");
		roundtrip(doc);
	}
	
	@Test
	public void testSerializationOfPropertyWithArbitraryObjectAsValue() throws Exception
	{
		ArbitraryObj obj = new ArbitraryObj();
		obj.set();
		
		CtnDocument doc = astDoc(astObjProp(obj));
		roundtrip(doc);
	}
	
	@Test
	public void testSerializationOfPropertyWithNodeAsValue() throws Exception
	{
		ArbitraryObj obj = new ArbitraryObj();
		obj.set();
		
		CtnDocument doc = astDoc(astObjProp(astUrl().build()));
		roundtrip(doc);
	}
	
	/**
	 * TODO: How can we fix this situation?
	 */
	@Test
	@Ignore
	public void testSerializationOfObjectArray() throws Exception
	{
		CtnDocument doc = astDoc();
		doc.setAttribute("array", new Object[] { astText("Hallo"), astUrl().build() });
		roundtrip(doc);
	}
	
	@Test
	public void testNoContentPropertyFoundWhenTextNodeTypeSet() throws Exception
	{
		CtnDocument doc = astDoc(astText("Hallo"));
		assertFalse(serialize(doc).contains("\"$content\""));
		
		getConverter().setStringNodeType(null);
		assertTrue(serialize(doc).contains("\"$content\""));
	}
	
	@Test
	public void testAttributesOnTextNodeForceContentElement() throws Exception
	{
		CtnText text = astText("Hallo");
		text.setAttribute("ruins", "it");
		CtnDocument doc = astDoc(text);
		assertTrue(serialize(doc).contains("\"$content\""));
	}
	
	@Test
	public void testInstantiationOfNullReplacementProperty() throws Exception
	{
		CtnDocument doc = astDoc(astUrl().withProtocol("").build());
		getConverter().setSuppressEmptyStringProperties(true);
		assertFalse(serialize(doc).contains("\"$protocol\""));
		roundtrip(doc);
	}
	
	@Test
	public void testNullProperty() throws Exception
	{
		CtnDocument doc = astDoc(astObjProp(null));
		assertFalse(serialize(doc).contains("\"$prop"));
		roundtrip(doc);
	}
	
	@Test
	public void testSuppressedProperty() throws Exception
	{
		CtnDocument doc = astDoc(astObjProp("Hello World"));
		getConverter().suppressProperty("prop");
		assertFalse(serialize(doc).contains("\"$prop"));
	}
	
	@Test
	public void testNodeWithContentAndAnotherProperty() throws Exception
	{
		// First make sure that the node is properly recognized as string node
		CtnDocument doc = astDoc(astObjProp(astPropContent(null, "Hello World")));
		getConverter().setStringNodeType(CtnNodeWithPropAndContent.class);
		assertFalse(serialize(doc).contains("\"$content\""));
		roundtrip(doc);
		
		doc = astDoc(astObjProp(astPropContent(42, "Hello World")));
		getConverter().setStringNodeType(CtnNodeWithPropAndContent.class);
		assertTrue(serialize(doc).contains("\"$content\""));
		roundtrip(doc);
	}
	
	@Test
	public void testNodeWithAtLeastTwoNamedChildren() throws Exception
	{
		CtnDocument doc = astDoc(astSection().build());
		String serialized = serialize(doc);
		assertTrue(serialized.contains("\"title\":"));
		assertTrue(serialized.contains("\"body\":"));
		roundtrip(doc);
	}
	
	@Test
	public void testStoreLocation() throws Exception
	{
		CtnDocument doc = astWithLocations();
		roundtrip(doc);
		assertTrue(serialize(doc).contains("\"!location\""));
	}
	
	@Test
	public void testSuppressLocation() throws Exception
	{
		CtnDocument doc = astWithLocations();
		getConverter().setStoreLocation(false);
		assertFalse(serialize(doc).contains("\"!location\""));
	}
	
	private CtnDocument astWithLocations()
	{
		CtnText text = astText("Hello");
		text.setNativeLocation(new AstLocation("some file", 42, 43));
		CtnUrl url = astUrl().build();
		url.setNativeLocation(new AstLocation("some file", 44, 45));
		CtnDocument doc = astDoc(text, url);
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
		CtnUrl url = astUrl().build();
		url.setAttribute("area51", "Hello World 1");
		url.setAttribute("area52", "Hello World 2");
		CtnDocument doc = astDoc(url);
		return doc;
	}
	
	@Test
	public void testStoreNullAttribute() throws Exception
	{
		CtnUrl url = astUrl().build();
		url.setAttribute("area51", null);
		CtnDocument doc = astDoc(url);
		roundtrip(doc);
	}
	
	@Test
	public void testBodyInterfaceNode() throws Exception
	{
		CtnSection sec = astSection().build();
		CtnDocument doc = astDoc(sec);
		assertEquals(CtnBody.CtnBodyImpl.class, sec.getBody().getClass());
		assertTrue(sec.hasBody());
		assertTrue(serialize(doc).contains("\"body\""));
		roundtrip(doc);
	}
	
	@Test
	public void testNoBodyNode() throws Exception
	{
		CtnSection sec = astSection().build();
		sec.removeBody();
		CtnDocument doc = astDoc(sec);
		assertEquals(CtnBody.CtnNoBody.class, sec.getBody().getClass());
		assertFalse(sec.hasBody());
		
		roundtrip(doc);
		
		String json = serialize(doc);
		assertFalse(json.contains("\"body\""));
		CtnDocument restoredDoc = (CtnDocument) deserialize(json, CtnDocument.class);
		assertFalse(((CtnSection) restoredDoc.get(0)).hasBody());
	}
	
	@Test
	public void testNoTitleNodeToCoverAllPaths() throws Exception
	{
		CtnSection sec = astSection().build();
		sec.removeTitle();
		CtnDocument doc = astDoc(sec);
		assertEquals(CtnTitle.CtnNoTitle.class, sec.getTitle().getClass());
		assertFalse(sec.hasTitle());
		assertTrue(sec.hasBody());
		
		roundtrip(doc);
		
		String json = serialize(doc);
		assertFalse(json.contains("\"title\""));
		CtnDocument restoredDoc = (CtnDocument) deserialize(json, CtnDocument.class);
		assertFalse(((CtnSection) restoredDoc.get(0)).hasTitle());
	}
	
	@Test
	public void testStoreComplexArrayAsAttribute() throws Exception
	{
		CtnDocument doc = astDoc();
		doc.setAttribute("DoubleTrouble", new Double[][] {
				new Double[] { 3.1415, 2 * 3.1415 },
				new Double[] { 2.7182, 2 * 2.7182 } });
		roundtrip(doc);
	}
}
