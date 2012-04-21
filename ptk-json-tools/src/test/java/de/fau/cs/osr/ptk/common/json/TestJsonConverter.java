package de.fau.cs.osr.ptk.common.json;

import static de.fau.cs.osr.ptk.common.test.TestAstBuilder.astSection;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import de.fau.cs.osr.ptk.common.test.TestNodeSection;
import de.fau.cs.osr.ptk.common.test.TestNodeUrl;

public class TestJsonConverter
{
	@Test
	public void testAstToJsonConversion() throws Exception
	{
		TestNodeSection ast = astSection().build();
		ast.setAttribute("someAttr", "someAttrValue");
		
		String json = JsonConverter.toJson(ast);
		
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
	public void testJsonToASTNodeConversion() throws Exception
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
		
		TestNodeUrl url = JsonConverter.fromJson(
				""
						+ "{"
						+ "  \"!type\": \"TestNodeUrl\","
						+ "  \"@someAttr\": {"
						+ "    \"type\": \"java.lang.String\","
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
}
