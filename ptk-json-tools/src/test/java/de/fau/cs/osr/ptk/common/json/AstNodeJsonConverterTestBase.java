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
import de.fau.cs.osr.utils.SimpleTypeNameMapper;

public class AstNodeJsonConverterTestBase
{
	private AstNodeJsonTypeAdapter<CtnNode> converter;
	
	private Gson gson;
	
	// =========================================================================
	
	@Before
	public void before()
	{
		converter = AstNodeJsonTypeAdapter.forNodeType(CtnNode.class);
		converter.setStringNodeType(CtnText.class);
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeHierarchyAdapter(AstNode.class, converter);
		builder.serializeNulls();
		builder.setPrettyPrinting();
		gson = builder.create();
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
	
	public AstNodeJsonTypeAdapter<CtnNode> getConverter()
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
	
	public CtnDocument deserialize(String json, Class<?> typeOfT)
	{
		return (CtnDocument) gson.fromJson(json, typeOfT);
	}
	
	public void roundtrip(CtnNode node) throws ComparisonException
	{
		String json = serialize(node);
		
		CtnNode restoredNode = (CtnNode) deserialize(json, CtnDocument.class);
		
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
	
	public void printSerialized(CtnNode node)
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
