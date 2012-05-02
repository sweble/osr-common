package de.fau.cs.osr.ptk.common.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fau.cs.osr.ptk.common.ast.Text;

public final class TextGsonTypeAdatper
		implements
			JsonSerializer<Text>,
			JsonDeserializer<Text>
{
	@Override
	public JsonElement serialize(
			Text src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		return new JsonPrimitive(((Text) src).getContent());
	}
	
	@Override
	public Text deserialize(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		return deserialize_(json, typeOfT, context);
	}
	
	protected static Text deserialize_(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context)
	{
		return new Text(json.getAsString());
	}
}
