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

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import de.fau.cs.osr.utils.NameAbbrevService;

public class JsonConverter
{
	public static String toJson(Object object)
	{
		return toJson(object, true);
	}
	
	public static String toJson(Object object, boolean prettyPrinting)
	{
		return createGsonConverter(prettyPrinting, null).toJson(object);
	}
	
	public static void toJson(Object object, Appendable appendable)
	{
		toJson(object, true, appendable);
	}
	
	public static void toJson(
			Object object,
			boolean prettyPrinting,
			Appendable appendable)
	{
		createGsonConverter(prettyPrinting, null).toJson(object, appendable);
	}
	
	public static String toJson(Object object, NameAbbrevService as)
	{
		return toJson(object, true, as);
	}
	
	public static String toJson(
			Object object,
			boolean prettyPrinting,
			NameAbbrevService as)
	{
		return createGsonConverter(prettyPrinting, as).toJson(object);
	}
	
	public static void toJson(
			Object object,
			Appendable appendable,
			NameAbbrevService as)
	{
		toJson(object, true, appendable, as);
	}
	
	public static void toJson(
			Object object,
			boolean prettyPrinting,
			Appendable appendable,
			NameAbbrevService as)
	{
		createGsonConverter(prettyPrinting, as).toJson(object, appendable);
	}
	
	// =========================================================================
	
	public static <T> T fromJson(String json, Class<? extends T> clazz)
	{
		return createGsonConverter(false, null).fromJson(json, clazz);
	}
	
	public static <T> T fromJson(Reader reader, Class<? extends T> clazz)
	{
		return createGsonConverter(false, null).fromJson(reader, clazz);
	}
	
	public static <T> T fromJson(
			String json,
			Class<? extends T> clazz,
			NameAbbrevService as)
	{
		return createGsonConverter(false, as).fromJson(json, clazz);
	}
	
	public static <T> T fromJson(
			Reader reader,
			Class<? extends T> clazz,
			NameAbbrevService as)
	{
		return createGsonConverter(false, as).fromJson(reader, clazz);
	}
	
	// =========================================================================
	
	public static Gson createGsonConverter(
			boolean prettyPrinting,
			NameAbbrevService as)
	{
		return createGsonConverter(prettyPrinting, as, false);
	}
	
	public static Gson createGsonConverter(
			boolean prettyPrinting,
			NameAbbrevService as,
			boolean saveLocation)
	{
		GsonBuilder builder = new GsonBuilder();
		registerAstTypeAdapters(builder, as, saveLocation);
		
		if (prettyPrinting)
			builder.setPrettyPrinting();
		
		Gson gson = builder.create();
		return gson;
	}
	
	public static void registerAstTypeAdapters(
			GsonBuilder gson,
			NameAbbrevService as,
			boolean saveLocation)
	{
		JsonConverterImpl config = new JsonConverterImpl(as, saveLocation);
		
		gson.registerTypeAdapter(NodeList.class, new NodeListGsonTypeAdapter(config));
		gson.registerTypeAdapter(Text.class, new TextGsonTypeAdatper(config));
		
		// Fallback
		gson.registerTypeHierarchyAdapter(
				AstNode.class,
				new AstNodeGsonTypeAdapter(config));
		
		// We require the serialization of null values
		gson.serializeNulls();
	}
}
