package de.fau.cs.osr.ptk.common.json;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.Location;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import de.fau.cs.osr.utils.NameAbbrevService;

public class JsonConverterImpl
{
	private static final int MAX_ENTRIES = 128;
	
	// =========================================================================
	
	private final Map<PropKey, PropSetter> propTypeCache;
	
	private final NameAbbrevService abbrev;
	
	private final boolean saveLocation;
	
	// =========================================================================
	
	JsonConverterImpl(NameAbbrevService abbrev, boolean saveLocation)
	{
		this.saveLocation = saveLocation;
		
		if (abbrev == null)
			abbrev = new NameAbbrevService();
		this.abbrev = abbrev;
		
		propTypeCache = new LinkedHashMap<PropKey, PropSetter>()
		{
			private static final long serialVersionUID = 1L;
			
			protected boolean removeEldestEntry(
					Map.Entry<PropKey, PropSetter> eldest)
			{
				return size() > MAX_ENTRIES;
			}
		};
	}
	
	// =========================================================================
	
	public JsonElement serializeAstNode(
			AstNode src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		JsonObject node = new JsonObject();
		node.add("!type", new JsonPrimitive(abbrev(src.getClass())));
		
		if (saveLocation && src.getNativeLocation() != null)
			node.add("!location", new JsonPrimitive(locationToStr(src)));
		
		if (!src.getAttributes().isEmpty())
		{
			for (Entry<String, Object> e : src.getAttributes().entrySet())
			{
				String name = "@" + e.getKey();
				Object value = e.getValue();
				
				JsonElement jsonValue;
				if (value != null)
				{
					JsonObject attr = new JsonObject();
					attr.add(
							"type",
							new JsonPrimitive(abbrev(value.getClass())));
					attr.add(
							"value",
							context.serialize(value));
					jsonValue = attr;
				}
				else
				{
					jsonValue = null;
				}
				
				node.add(name, jsonValue);
			}
		}
		
		if (src.getPropertyCount() > 0)
		{
			AstNodePropertyIterator i = src.propertyIterator();
			while (i.next())
				node.add(i.getName(), context.serialize(i.getValue()));
		}
		
		if (!src.isEmpty())
		{
			if (src.isList())
			{
				JsonArray list = new JsonArray();
				node.add("!list", list);
				
				for (AstNode child : src)
					list.add(context.serialize(child));
			}
			else
			{
				int i = 0;
				for (String name : src.getChildNames())
					node.add(name, context.serialize(src.get(i++)));
			}
		}
		
		return node;
	}
	
	public AstNode deserializeAstNode(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		if (json.isJsonPrimitive())
		{
			return deserializeText(json, typeOfT, context);
		}
		else if (json.isJsonArray())
		{
			return deserializeNodeList(json, typeOfT, context);
		}
		else
		{
			JsonObject jo = json.getAsJsonObject();
			
			AstNode n = instantiateNode(jo);
			
			JsonElement location = jo.get("!location");
			if (location != null)
				n.setNativeLocation(strToLocation(location));
			
			if (n.isList())
			{
				JsonElement list = jo.get("!list");
				if (list != null)
				{
					for (JsonElement i : list.getAsJsonArray())
						n.add((AstNode) context.deserialize(i, AstNode.class));
				}
			}
			
			String[] childNames = n.getChildNames();
			
			for (Entry<String, JsonElement> field : jo.entrySet())
			{
				String name = field.getKey();
				if (name.length() >= 1 && name.charAt(0) == '!')
					continue;
				
				if (name.charAt(0) == '@')
				{
					loadAttribute(context, field, n);
				}
				else
				{
					int i = 0;
					for (; i < childNames.length; ++i)
					{
						if (name.equals(childNames[i]))
							break;
					}
					
					if (i == childNames.length)
					{
						loadProperty(context, field, n);
					}
					else
					{
						loadChild(context, field, n, i);
					}
				}
			}
			
			return n;
		}
	}
	
	private String locationToStr(AstNode src)
	{
		Location l = src.getNativeLocation();
		if (l.getFile() == null)
			return l.getLine() + ":" + l.getColumn();
		else
			return l.getFile() + ":" + l.getLine() + ":" + l.getColumn();
	}
	
	private Location strToLocation(JsonElement location)
	{
		String s = location.getAsString();
		if (s == null)
			return null;
		int i = s.indexOf(':');
		if (i == -1)
			return null;
		int j = s.indexOf(':', i + 1);
		int line;
		int column;
		String file = null;
		if (j == -1)
		{
			line = Integer.parseInt(s.substring(0, i));
			column = Integer.parseInt(s.substring(i + 1));
		}
		else
		{
			file = s.substring(0, i);
			line = Integer.parseInt(s.substring(i + 1, j));
			column = Integer.parseInt(s.substring(j + 1));
		}
		return new Location(file, line, column);
	}
	
	private AstNode instantiateNode(JsonObject jo)
	{
		JsonElement typeElem = jo.get("!type");
		if (typeElem == null)
			throw new JsonParseException("Missing `type' field on AST node");
		
		String typeSuffix = typeElem.getAsString();
		
		Exception e;
		try
		{
			return (AstNode) resolve(typeSuffix).newInstance();
		}
		catch (ClassNotFoundException e_)
		{
			e = e_;
		}
		catch (InstantiationException e_)
		{
			e = e_;
		}
		catch (IllegalAccessException e_)
		{
			e = e_;
		}
		
		throw new JsonParseException("Cannot create AST node for name `" + typeSuffix + "'", e);
	}
	
	private void loadAttribute(
			JsonDeserializationContext context,
			Entry<String, JsonElement> field,
			AstNode n)
	{
		String name = field.getKey().substring(1);
		
		Exception e;
		try
		{
			Object value = null;
			if (!field.getValue().isJsonNull())
			{
				JsonObject jsonValue = field.getValue().getAsJsonObject();
				
				String type = jsonValue.get("type").getAsString();
				
				value = context.<Object> deserialize(
						jsonValue.get("value"),
						resolve(type));
			}
			
			n.setAttribute(name, value);
			return;
		}
		catch (JsonParseException e_)
		{
			e = e_;
		}
		catch (ClassNotFoundException e_)
		{
			e = e_;
		}
		
		throw new JsonParseException("Failed to deserialize attribute `" + name + "'", e);
	}
	
	private void loadProperty(
			JsonDeserializationContext context,
			Entry<String, JsonElement> field,
			AstNode n)
	{
		PropSetter setter = getPropertyType(
				n.getClass(),
				field.getKey());
		
		setter.set(
				n,
				context.deserialize(
						field.getValue(),
						setter.type));
	}
	
	private void loadChild(
			JsonDeserializationContext context,
			Entry<String, JsonElement> field,
			AstNode n,
			int i)
	{
		AstNode value =
				context.deserialize(
						field.getValue(),
						AstNode.class);
		
		n.set(i, value);
	}
	
	// =========================================================================
	
	JsonElement serializeNodeList(
			NodeList src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		if (saveLocation && src.getNativeLocation() != null)
			return serializeAstNode(src, typeOfSrc, context);
		
		JsonArray array = new JsonArray();
		for (AstNode c : (NodeList) src)
			array.add(context.serialize(c));
		return array;
	}
	
	NodeList deserializeNodeList(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context)
	{
		if (json.isJsonObject())
			return (NodeList) deserializeAstNode(json, typeOfT, context);
		
		NodeList l = new NodeList();
		for (JsonElement i : json.getAsJsonArray())
			l.add((AstNode) context.deserialize(i, AstNode.class));
		return l;
	}
	
	// =========================================================================
	
	JsonElement serializeText(
			Text src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		if (saveLocation && src.getNativeLocation() != null)
			return serializeAstNode(src, typeOfSrc, context);
		
		return new JsonPrimitive(((Text) src).getContent());
	}
	
	Text deserializeText(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context)
	{
		if (json.isJsonObject())
			return (Text) deserializeAstNode(json, typeOfT, context);
		
		return new Text(json.getAsString());
	}
	
	// =========================================================================
	
	private String abbrev(Class<?> clazz)
	{
		return this.abbrev.abbrev(clazz);
	}
	
	private Class<?> resolve(String typeSuffix) throws ClassNotFoundException
	{
		return this.abbrev.resolve(typeSuffix);
	}
	
	// =========================================================================
	
	private PropSetter getPropertyType(
			Class<? extends AstNode> nodeClass,
			String propertyName)
	{
		PropKey key = new PropKey(nodeClass, propertyName);
		PropSetter setter = propTypeCache.get(key);
		if (setter != null)
			return setter;
		
		String head = ("" + propertyName.charAt(0)).toUpperCase();
		String tail = propertyName.substring(1);
		String getterName = "get" + head + tail;
		String setterName = "set" + head + tail;
		
		Method getterMethod;
		try
		{
			getterMethod = nodeClass.getMethod(getterName);
		}
		catch (NoSuchMethodException e)
		{
			// We also checked if it's the name of a child
			throw new JsonParseException("The field `" + propertyName
					+ "' is not a child nor a property of AST node of type `"
					+ nodeClass.getName() + "'", e);
		}
		catch (SecurityException e)
		{
			throw new JsonParseException("Cannot deduce type of property `"
					+ propertyName + "' in AST node of type `"
					+ nodeClass.getName() + "'. ", e);
		}
		
		Class<?> propType = getterMethod.getReturnType();
		
		Method setterMethod;
		try
		{
			setterMethod = nodeClass.getMethod(setterName, propType);
		}
		catch (Exception e)
		{
			throw new JsonParseException("Cannot set property `"
					+ propertyName + "' in AST node of type `"
					+ nodeClass.getName() + "'. ", e);
		}
		
		setter = new PropSetter(propType, setterMethod);
		propTypeCache.put(key, setter);
		return setter;
	}
	
	// =========================================================================
	
	private static final class PropKey
	{
		public final Class<? extends AstNode> nodeClazz;
		
		public final String propName;
		
		// =====================================================================
		
		public PropKey(Class<? extends AstNode> nodeClazz, String propName)
		{
			this.nodeClazz = nodeClazz;
			this.propName = propName;
		}
		
		// =====================================================================
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((nodeClazz == null) ? 0 : nodeClazz.hashCode());
			result = prime * result + ((propName == null) ? 0 : propName.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PropKey other = (PropKey) obj;
			if (nodeClazz == null)
			{
				if (other.nodeClazz != null)
					return false;
			}
			else if (!nodeClazz.equals(other.nodeClazz))
				return false;
			if (propName == null)
			{
				if (other.propName != null)
					return false;
			}
			else if (!propName.equals(other.propName))
				return false;
			return true;
		}
	}
	
	// =========================================================================
	
	private static final class PropSetter
	{
		public final Class<?> type;
		
		public final Method setter;
		
		// =====================================================================
		
		public PropSetter(Class<?> type, Method setter)
		{
			this.type = type;
			this.setter = setter;
		}
		
		// =====================================================================
		
		public void set(AstNode n, Object value)
		{
			try
			{
				setter.invoke(n, value);
			}
			catch (Exception e)
			{
				throw new JsonParseException("Cannot invoke property setter `"
						+ setter.getName() + "' in AST node of type `"
						+ n.getClass().getName() + "'. ");
			}
		}
	}
}
