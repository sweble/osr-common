package de.fau.cs.osr.ptk.common.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fau.cs.osr.ptk.common.ast.AstNode;

public final class AstNodeGsonTypeAdapter
		implements
			JsonSerializer<AstNode>,
			JsonDeserializer<AstNode>
{
	private final JsonConverterImpl impl;
	
	// =========================================================================
	
	public AstNodeGsonTypeAdapter(JsonConverterImpl impl)
	{
		this.impl = impl;
	}
	
	// =========================================================================
	
	@Override
	public JsonElement serialize(
			AstNode src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		return impl.serializeAstNode(src, typeOfSrc, context);
	}
	
	@Override
	public AstNode deserialize(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		return impl.deserializeAstNode(json, typeOfT, context);
	}
}
