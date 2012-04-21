package de.fau.cs.osr.ptk.common.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;

public final class NodeListGsonTypeAdapter
		implements
			JsonSerializer<NodeList>,
			JsonDeserializer<NodeList>
{
	@Override
	public JsonElement serialize(
			NodeList src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		JsonArray array = new JsonArray();
		for (AstNode c : (NodeList) src)
			array.add(context.serialize(c));
		return array;
	}
	
	@Override
	public NodeList deserialize(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		return deserialize_(json, typeOfT, context);
	}
	
	public static NodeList deserialize_(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context)
	{
		NodeList l = new NodeList();
		for (JsonElement i : json.getAsJsonArray())
			l.add((AstNode) context.deserialize(i, AstNode.class));
		return l;
	}
}
