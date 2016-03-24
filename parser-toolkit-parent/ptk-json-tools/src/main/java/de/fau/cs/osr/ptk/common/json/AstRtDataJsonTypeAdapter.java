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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fau.cs.osr.ptk.common.ast.RtData;
import de.fau.cs.osr.ptk.common.serialization.AstConverterBase;
import de.fau.cs.osr.ptk.common.serialization.IncompatibleAstNodeClassException;
import de.fau.cs.osr.ptk.common.serialization.SyntaxErrorException;

public class AstRtDataJsonTypeAdapter<T extends RtData>
		extends
			AstConverterBase
		implements
			JsonSerializer<T>,
			JsonDeserializer<T>
{
	private final Constructor<? extends RtData> rtdCtor;

	// =========================================================================

	public AstRtDataJsonTypeAdapter(Class<T> rtDataType)
	{
		try
		{
			rtdCtor = rtDataType.getConstructor(int.class, Object[].class);
		}
		catch (NoSuchMethodException e)
		{
			throw new IncompatibleAstNodeClassException("Class '" + rtDataType.getName() + "' is malformed", e);
		}
		catch (SecurityException e)
		{
			throw new IncompatibleAstNodeClassException("Class '" + rtDataType.getName() + "' is malformed", e);
		}
	}

	// =========================================================================

	@Override
	public JsonElement serialize(
			RtData src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		JsonArray fields = new JsonArray();
		int last = src.size() - 1;
		for (int i = 0; i <= last; ++i)
		{
			Object[] srcField = src.getField(i);
			if (srcField.length > 0)
			{
				for (Object srcObj : srcField)
				{
					JsonElement item = null;
					if (srcObj instanceof String)
					{
						item = new JsonPrimitive((String) srcObj);
					}
					else if (srcObj != null)
					{
						item = new JsonObject();
						AstNodeJsonTypeAdapter.storeType(getTypeAlias(srcObj.getClass()), (JsonObject) item);
						AstNodeJsonTypeAdapter.storeObject(context.serialize(srcObj), (JsonObject) item);
					}
					fields.add(item);
				}
			}
			if (i != last)
				fields.add(null);
		}
		return fields;
	}

	@Override
	public T deserialize(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		if (!json.isJsonArray())
			throw new SyntaxErrorException("RTD deserializer expected array");
		JsonArray fields = json.getAsJsonArray();

		int size = 1;
		ArrayList<Object> l = new ArrayList<Object>();
		for (JsonElement e : fields)
		{
			if (e.isJsonNull())
			{
				l.add(RtData.SEP);
				++size;
			}
			else if (e.isJsonPrimitive())
			{
				l.add(e.getAsString());
			}
			else
			{
				JsonObject item = e.getAsJsonObject();
				Class<?> type = getClassForAlias(AstNodeJsonTypeAdapter.readType(item));
				Object obj = context.deserialize(AstNodeJsonTypeAdapter.readObject(item), type);
				l.add(obj);
			}
		}

		return newRtd(size, l);
	}

	private T newRtd(int size, ArrayList<Object> l)
	{
		try
		{
			@SuppressWarnings("unchecked")
			T tmp = (T) rtdCtor.newInstance(size, l.toArray());
			return tmp;
		}
		catch (InstantiationException e)
		{
			throw new JsonParseException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new JsonParseException(e);
		}
		catch (IllegalArgumentException e)
		{
			throw new JsonParseException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new JsonParseException(e);
		}
	}
}
