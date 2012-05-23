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

import static de.fau.cs.osr.ptk.common.test.TestAstBuilder.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.google.gson.Gson;

import de.fau.cs.osr.ptk.common.AstComparer;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import de.fau.cs.osr.ptk.common.test.TestNodeSection;
import de.fau.cs.osr.ptk.common.test.TestNodeUrl;
import de.fau.cs.osr.utils.NameAbbrevService;

public class TestJsonConverter
{
	@Test
	public void testAstToJsonConversion() throws Exception
	{
		TestNodeSection ast = astSection().build();
		ast.setAttribute("someAttr", "someAttrValue");
		
		String json = JsonConverter.toJson(
				ast,
				new NameAbbrevService("de.fau.cs.osr.ptk.common.test"));
		
		assertThat(json, containsString("\"!type\""));
		assertThat(json, containsString("\"TestNodeSection\""));
		
		// attributes
		assertThat(json, containsString("\"@someAttr\""));
		assertThat(json, containsString("\"someAttrValue\""));
		
		// properties
		assertThat(json, containsString("\"level\""));
		assertThat(json, containsString("0"));
		
		// children
		assertThat(json, containsString("\"title\""));
		assertThat(json, containsString("\"Default section title\""));
		assertThat(json, containsString("\"body\""));
		assertThat(json, containsString("\"Default section body\""));
	}
	
	@Test
	public void testJsonToTextConversion() throws Exception
	{
		Text t = JsonConverter.fromJson("\"Some text\"", Text.class);
		assertThat("Some text", equalTo(t.getContent()));
	}
	
	@Test
	public void testJsonToNodeListConversion() throws Exception
	{
		NodeList nl = JsonConverter.fromJson(
				"[\"Some text\", [\"Some more text\"]]",
				NodeList.class);
		
		assertThat(nl.size(), equalTo(2));
		assertThat(((Text) nl.get(0)).getContent(), equalTo("Some text"));
		assertThat(((Text) nl.get(1)).getContent(), equalTo("Some more text"));
	}
	
	@Test
	public void testJsonToAstNodeConversion() throws Exception
	{
		final String protocol = "protocol";
		final String path = "path";
		
		/*
		TestNodeUrl ast = astUrl()
				.withProtocol(protocol)
				.withPath(path)
				.build();
		ast.setAttribute("someAttr", "someAttrValue");
		
		System.out.println(JsonConverter.toJson(ast));
		*/
		
		Gson json = JsonConverter.createGsonConverter(
				false,
				new NameAbbrevService(
						"de.fau.cs.osr.ptk.common.test"));
		
		TestNodeUrl url = json.fromJson(
				""
						+ "{"
						+ "  \"!type\": \"TestNodeUrl\","
						+ "  \"@someAttr\": {"
						+ "    \"type\": \"String\","
						+ "    \"value\": \"someAttrValue\""
						+ "  },"
						+ "  \"protocol\": \"protocol\","
						+ "  \"path\": \"path\""
						+ "}",
				TestNodeUrl.class);
		
		// attributes
		assertThat((String) url.getAttribute("someAttr"), equalTo("someAttrValue"));
		
		// properties
		assertThat(url.getProtocol(), equalTo(protocol));
		assertThat(url.getPath(), equalTo(path));
	}
	
	@Test
	public void testSerializationAndDeserialization() throws Exception
	{
		TestNodeSection in = astSection()
				.withLevel(1)
				.withBody(
						astText(),
						astUrl().build())
				.build();
		
		in.setAttribute("int attr", 5);
		
		in.setAttribute("string attr", "string value");
		
		in.setAttribute("array attr", new Double[] { 3.1415, 2.7182 });
		
		in.setAttribute("bad ass array attr", new double[][] { { 3.1415, 2.7182 }, { 3.1415 * 2, 2.7182 * 2 } });
		
		in.setAttribute("arbitrary object", new ArbitraryNode());
		
		in.setAttribute("null valued attr", null);
		
		// -------
		
		NameAbbrevService as = new NameAbbrevService(
				"de.fau.cs.osr.ptk.common.test",
				"de.fau.cs.osr.ptk.common.json");
		
		String json = serialize(in, as);
		
		AstNode out = deserialize(json, as);
		
		// -------
		
		assertTrue(AstComparer.compare(in, out, false, true));
		
		// We have to compare the attribuets manually. The arrays contained in 
		// the attribute won't be compared correctly by the .equals call.
		deepCompareMaps(in.getAttributes(), out.getAttributes());
	}
	
	// =========================================================================
	
	private String serialize(AstNode ast, NameAbbrevService as) throws Exception
	{
		return JsonConverter.toJson(ast, as);
	}
	
	private AstNode deserialize(String json, NameAbbrevService as) throws Exception
	{
		return JsonConverter.fromJson(json, AstNode.class, as);
	}
	
	private boolean deepCompareMaps(
			Map<String, Object> a,
			Map<String, Object> b)
	{
		Iterator<Entry<String, Object>> i = a.entrySet().iterator();
		while (i.hasNext())
		{
			Entry<String, Object> e = i.next();
			String key = e.getKey();
			Object value = e.getValue();
			if (value == null)
			{
				if (b.get(key) != null || !b.containsKey(key))
					return false;
			}
			else if (value.getClass().isArray())
			{
				Arrays.deepEquals((Object[]) value, (Object[]) b.get(key));
			}
			else
			{
				if (!value.equals(b.get(key)))
					return false;
			}
		}
		
		return true;
	}
	
	// =========================================================================
	
	protected static final class ArbitraryNode
	{
		public Object nullValue = null;
		
		public int intValue = 42;
		
		public String strValue = "some string";
		
		public double[] doubleValues = { 3.1415, 2.7182 };
		
		// =====================================================================
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ArbitraryNode other = (ArbitraryNode) obj;
			if (!Arrays.equals(doubleValues, other.doubleValues))
				return false;
			if (intValue != other.intValue)
				return false;
			if (nullValue == null)
			{
				if (other.nullValue != null)
					return false;
			}
			else if (!nullValue.equals(other.nullValue))
				return false;
			if (strValue == null)
			{
				if (other.strValue != null)
					return false;
			}
			else if (!strValue.equals(other.strValue))
				return false;
			return true;
		}
	}
}
