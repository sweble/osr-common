package de.fau.cs.osr.ptk.common.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fau.cs.osr.ptk.common.ast.Text;

public final class TextGsonTypeAdatper
		implements
			JsonSerializer<Text>,
			JsonDeserializer<Text>
{
	private final JsonConverterImpl config;
	
	// =========================================================================
	
	public TextGsonTypeAdatper(JsonConverterImpl config)
	{
		this.config = config;
	}
	
	@Override
	public JsonElement serialize(
			Text src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		return config.serializeText(src, typeOfSrc, context);
	}
	
	@Override
	public Text deserialize(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		return config.deserializeText(json, typeOfT, context);
	}
}
