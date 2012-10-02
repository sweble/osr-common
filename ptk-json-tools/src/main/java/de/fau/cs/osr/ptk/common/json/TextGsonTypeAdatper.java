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

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fau.cs.osr.ptk.common.ast.AstNode;

public final class TextGsonTypeAdatper<T extends AstNode<T>>
		implements
			JsonSerializer<T>,
			JsonDeserializer<T>
{
	private final JsonConverterImpl<T> config;
	
	// =========================================================================
	
	public TextGsonTypeAdatper(JsonConverterImpl<T> config)
	{
		this.config = config;
	}
	
	@Override
	public JsonElement serialize(
			T src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		return config.serializeText(src, typeOfSrc, context);
	}
	
	@Override
	public T deserialize(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		return config.deserializeText(json, typeOfT, context);
	}
}
