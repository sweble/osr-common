package de.fau.cs.osr.ptk.common.json;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import sun.org.mozilla.javascript.ast.AstNode;

final class AstNodeGsonAdapter
		implements
			JsonSerializer<AstNode>
{
	@Override
	public JsonElement serialize(
			AstNode src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		if (src.isList())
		{
			JsonArray array = new JsonArray();
			for (AstNode c : src)
				array.add(context.serialize(c));
			return array;
		}
		else if (src instanceof Text)
		{
			return new JsonPrimitive(((Text) src).getContent());
		}
		else
		{
			JsonObject node = new JsonObject();
			node.add("type", new JsonPrimitive(src.getNodeName()));
			
			if (!src.getAttributes().isEmpty())
			{
				JsonObject attrs = new JsonObject();
				for (Entry<String, Object> e : src.getAttributes().entrySet())
				{
					if (e.getKey().equals("RTD"))
					{
						attrs.add(e.getKey(), context.serialize(
								((RtData) e.getValue()).getRts()));
					}
					else
					{
						attrs.add(e.getKey(), context.serialize(e.getValue()));
					}
				}
				node.add("attrs", attrs);
			}
			
			if (src.getPropertyCount() > 0)
			{
				JsonObject props = new JsonObject();
				AstNodePropertyIterator i = src.propertyIterator();
				while (i.next())
					props.add(i.getName(), context.serialize(i.getValue()));
				node.add("props", props);
			}
			
			if (!src.isEmpty())
			{
				JsonObject children = new JsonObject();
				int i = 0;
				for (String name : src.getChildNames())
					children.add(name, context.serialize(src.get(i++)));
				node.add("children", children);
			}
			
			return node;
		}
	}
}
