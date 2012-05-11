package de.fau.cs.osr.ptk.common.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fau.cs.osr.ptk.common.ast.NodeList;

public final class NodeListGsonTypeAdapter
		implements
			JsonSerializer<NodeList>,
			JsonDeserializer<NodeList>
{
	private final JsonConverterImpl config;
	
	// =========================================================================
	
	public NodeListGsonTypeAdapter(JsonConverterImpl config)
	{
		this.config = config;
	}
	
	@Override
	public JsonElement serialize(
			NodeList src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		return config.serializeNodeList(src, typeOfSrc, context);
	}
	
	@Override
	public NodeList deserialize(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		return config.deserializeNodeList(json, typeOfT, context);
	}
}
