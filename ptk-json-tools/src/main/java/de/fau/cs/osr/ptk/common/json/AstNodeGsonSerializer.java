package de.fau.cs.osr.ptk.common.json;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;

public final class AstNodeGsonSerializer
		implements
			JsonSerializer<AstNode>,
			JsonDeserializer<AstNode>
{
	//private static final String[] EXPECTED_AST_NODE_MEMBERS = { "type", "props", "attrs", "children" };
	
	private static final int MAX_ENTRIES = 100;
	
	// =========================================================================
	
	private final Map<String, Class<?>> attrTypeCache;
	
	private final Map<PropKey, PropSetter> propTypeCache;
	
	private final Map<String, Class<? extends AstNode>> nodeTypeCache;
	
	private final List<String> packages = new ArrayList<String>();
	
	// =========================================================================
	
	public AstNodeGsonSerializer()
	{
		packages.add("de.fau.cs.osr.ptk.common.json");
		
		attrTypeCache = new LinkedHashMap<String, Class<?>>()
		{
			private static final long serialVersionUID = 1L;
			
			protected boolean removeEldestEntry(
					Map.Entry<String, Class<?>> eldest)
			{
				return size() > MAX_ENTRIES;
			}
		};
		
		propTypeCache = new LinkedHashMap<PropKey, PropSetter>()
		{
			private static final long serialVersionUID = 1L;
			
			protected boolean removeEldestEntry(
					Map.Entry<PropKey, PropSetter> eldest)
			{
				return size() > MAX_ENTRIES;
			}
		};
		
		nodeTypeCache = new LinkedHashMap<String, Class<? extends AstNode>>()
		{
			private static final long serialVersionUID = 1L;
			
			protected boolean removeEldestEntry(
					Map.Entry<String, Class<? extends AstNode>> eldest)
			{
				return size() > MAX_ENTRIES;
			}
		};
	}
	
	// =========================================================================
	
	@Override
	public JsonElement serialize(
			AstNode src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		JsonObject node = new JsonObject();
		node.add("!type", new JsonPrimitive(src.getNodeName()));
		
		if (!src.getAttributes().isEmpty())
		{
			for (Entry<String, Object> e : src.getAttributes().entrySet())
			{
				String name = "@" + e.getKey();
				Object value = e.getValue();
				
				JsonObject attr = new JsonObject();
				attr.add(
						"type",
						new JsonPrimitive(value.getClass().getName()));
				attr.add(
						"value",
						context.serialize(value));
				
				node.add(name, attr);
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
			int i = 0;
			for (String name : src.getChildNames())
				node.add(name, context.serialize(src.get(i++)));
		}
		
		return node;
	}
	
	@Override
	public AstNode deserialize(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		if (json.isJsonPrimitive())
		{
			return TextGsonTypeAdatper.deserialize_(json, typeOfT, context);
		}
		else if (json.isJsonArray())
		{
			return NodeListGsonTypeAdapter.deserialize_(json, typeOfT, context);
		}
		else
		{
			JsonObject jo = json.getAsJsonObject();
			
			AstNode n = instantiateNode(jo);
			
			String[] childNames = n.getChildNames();
			
			for (Entry<String, JsonElement> field : jo.entrySet())
			{
				String name = field.getKey();
				if (name.equals("!type"))
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
	
	private AstNode instantiateNode(JsonObject jo)
	{
		JsonElement typeElem = jo.get("!type");
		if (typeElem == null)
			throw new JsonParseException("Missing `type' field on AST node");
		
		String typeSuffix = typeElem.getAsString();
		
		AstNode n = createNodeFromCache(typeSuffix);
		if (n != null)
			return n;
		
		String fqn = typeSuffix;
		Iterator<String> i = packages.iterator();
		while (true)
		{
			n = tryToInstantiate(typeSuffix, fqn);
			if (n != null)
				return n;
			
			if (!i.hasNext())
				throw new JsonParseException("Cannot find class for AST node with type suffix `" + typeSuffix + "'");
			
			fqn = i.next() + "." + typeSuffix;
		}
	}
	
	private void loadAttribute(
			JsonDeserializationContext context,
			Entry<String, JsonElement> field,
			AstNode n)
	{
		JsonObject value = field.getValue().getAsJsonObject();
		
		n.setAttribute(
				field.getKey().substring(1),
				context.<Object> deserialize(
						value.get("value"),
						getAttributeType(
						value.get("type").getAsString())));
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
	
	private Class<?> getAttributeType(String fqn)
	{
		Class<?> type = attrTypeCache.get(fqn);
		if (type != null)
			return type;
		
		try
		{
			type = Class.forName(fqn);
		}
		catch (ClassNotFoundException e1)
		{
			throw new JsonParseException("Cannot find class `" + fqn + "'");
		}
		
		attrTypeCache.put(fqn, type);
		return type;
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
	
	private AstNode createNodeFromCache(String typeSuffix)
	{
		Class<? extends AstNode> nodeClass = nodeTypeCache.get(typeSuffix);
		if (nodeClass == null)
			return null;
		return createNode(nodeClass);
	}
	
	@SuppressWarnings("unchecked")
	private AstNode tryToInstantiate(String typeSuffix, String fqn)
	{
		Class<? extends AstNode> nodeClass;
		try
		{
			nodeClass = (Class<? extends AstNode>) Class.forName(fqn);
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
		
		nodeTypeCache.put(typeSuffix, nodeClass);
		return createNode(nodeClass);
	}
	
	private AstNode createNode(Class<? extends AstNode> nodeClass)
	{
		try
		{
			return nodeClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new JsonParseException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new JsonParseException(e);
		}
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
