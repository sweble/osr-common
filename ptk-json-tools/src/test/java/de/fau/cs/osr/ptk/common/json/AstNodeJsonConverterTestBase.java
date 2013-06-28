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

import org.apache.commons.lang.StringUtils;
import org.junit.Before;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.comparer.AstComparer;
import de.fau.cs.osr.ptk.common.json.AstNodeJsonTypeAdapter;
import de.fau.cs.osr.ptk.common.serialization.SimpleTypeNameMapper;
import de.fau.cs.osr.ptk.common.test.TestAstBuilder;
import de.fau.cs.osr.ptk.common.test.TestAstBuilder.Document;
import de.fau.cs.osr.ptk.common.test.TestAstBuilder.TestAstNode;
import de.fau.cs.osr.ptk.common.test.TestAstBuilder.Text;
import de.fau.cs.osr.utils.ComparisonException;

public class AstNodeJsonConverterTestBase
{
	private AstNodeJsonTypeAdapter<TestAstNode> converter;
	
	private Gson gson;
	
	// =========================================================================
	
	@Before
	public void before()
	{
		converter = AstNodeJsonTypeAdapter.forNodeType(TestAstNode.class);
		converter.setStringNodeType(Text.class);
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeHierarchyAdapter(AstNode.class, converter);
		builder.serializeNulls();
		builder.setPrettyPrinting();
		gson = builder.create();
	}
	
	public void setupDefaultNodeFactory()
	{
		converter.setNodeFactory(TestAstBuilder.getFactory());
	}
	
	public void setupDefaultTypeMappings()
	{
		SimpleTypeNameMapper typeNameMapper = new SimpleTypeNameMapper();
		typeNameMapper.add(TestAstBuilder.Text.class, "text");
		typeNameMapper.add(TestAstBuilder.NodeList.class, "list");
		typeNameMapper.add(TestAstBuilder.Section.class, "section");
		typeNameMapper.add(TestAstBuilder.Title.class, "title");
		typeNameMapper.add(TestAstBuilder.Body.class, "body");
		typeNameMapper.add(TestAstBuilder.Document.class, "document");
		typeNameMapper.add(TestAstBuilder.IdNode.class, "id");
		typeNameMapper.add(TestAstBuilder.Url.class, "url");
		typeNameMapper.add(TestAstBuilder.NodeWithObjProp.class, "nwop");
		typeNameMapper.add(TestAstBuilder.NodeWithPropAndContent.class, "nwpac");
		converter.setTypeNameMapper(typeNameMapper);
		
		converter.suppressNode(TestAstBuilder.Body.NoBody.class);
		converter.suppressNode(TestAstBuilder.Title.NoTitle.class);
		
		converter.suppressTypeInfo(TestAstBuilder.Body.EmptyBody.class);
		converter.suppressTypeInfo(TestAstBuilder.Body.BodyImpl.class);
		converter.suppressTypeInfo(TestAstBuilder.Title.EmptyTitle.class);
		converter.suppressTypeInfo(TestAstBuilder.Title.TitleImpl.class);
	}
	
	public AstNodeJsonTypeAdapter<TestAstNode> getConverter()
	{
		return converter;
	}
	
	public Gson getGson()
	{
		return gson;
	}
	
	public String serialize(Object what)
	{
		return gson.toJson(what);
	}
	
	public Document deserialize(String json, Class<?> typeOfT)
	{
		return (Document) gson.fromJson(json, typeOfT);
	}
	
	public void roundtrip(TestAstNode node) throws ComparisonException
	{
		String json = serialize(node);
		
		TestAstNode restoredNode = (TestAstNode) deserialize(json, Document.class);
		
		try
		{
			AstComparer.compareAndThrow(node, restoredNode, true, true);
		}
		catch (ComparisonException e)
		{
			printJson(json);
			printRestoredJson(restoredNode);
			throw e;
		}
	}
	
	public void printSerialized(TestAstNode node)
	{
		System.out.println(StringUtils.repeat("=", 80));
		System.out.println("\"\"\"" + serialize(node) + "\"\"\"");
		System.out.println(StringUtils.repeat("=", 80));
	}
	
	public void printJson(String json)
	{
		System.err.println("Original JSON:");
		System.err.println(StringUtils.repeat("=", 80));
		System.err.println("\"\"\"" + json + "\"\"\"");
		System.err.println(StringUtils.repeat("=", 80));
	}
	
	public void printRestoredJson(Object restoredNode)
	{
		System.err.println("JSON after round trip:");
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
	
}
