package de.fau.cs.osr.ptk.common.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;

public class JsonConverter
{
	public static String toJson(Object object)
	{
		return toJson(object, true);
	}
	
	public static String toJson(Object object, boolean prettyPrinting)
	{
		return getGsonConverter(prettyPrinting).toJson(object);
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
		getGsonConverter(prettyPrinting).toJson(object, appendable);
	}
	
	// =========================================================================
	
	public static <T> T fromJson(String json, Class<? extends T> clazz)
	{
		return getGsonConverter(false).fromJson(json, clazz);
	}
	
	// =========================================================================
	
	public static Gson createGsonConverter(boolean prettyPrinting)
	{
		GsonBuilder builder = new GsonBuilder();
		registerAstTypeAdapters(builder);
		
		if (prettyPrinting)
			builder.setPrettyPrinting();
		
		Gson gson = builder.create();
		return gson;
	}
	
	public static void registerAstTypeAdapters(GsonBuilder gson)
	{
		gson.registerTypeAdapter(NodeList.class, new NodeListGsonTypeAdapter());
		gson.registerTypeAdapter(Text.class, new TextGsonTypeAdatper());
		
		// Fallback
		gson.registerTypeHierarchyAdapter(AstNode.class, new AstNodeGsonSerializer());
	}
	
	// =========================================================================
	
	private static Gson ppGson = null;
	
	private static Gson gson = null;
	
	private static Gson getGsonConverter(boolean prettyPrinting)
	{
		if (prettyPrinting)
		{
			if (ppGson == null)
			{
				synchronized (JsonConverter.class)
				{
					if (ppGson == null)
						ppGson = createGsonConverter(true);
				}
			}
			return ppGson;
		}
		else
		{
			if (gson == null)
			{
				synchronized (JsonConverter.class)
				{
					if (gson == null)
						gson = createGsonConverter(false);
				}
			}
			return gson;
		}
	}
}
