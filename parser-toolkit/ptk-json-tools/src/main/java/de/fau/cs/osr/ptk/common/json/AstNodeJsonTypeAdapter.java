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

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fau.cs.osr.ptk.common.ast.AstLocation;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.AstStringNode;
import de.fau.cs.osr.ptk.common.serialization.AstNodeConverterBase;
import de.fau.cs.osr.ptk.common.serialization.SyntaxErrorException;

public class AstNodeJsonTypeAdapter<T extends AstNode<T>>
		extends
			AstNodeConverterBase<T>
		implements
			JsonSerializer<T>,
			JsonDeserializer<T>
{
	static final String SPECIAL_FIELD_LIST = "!list";
	
	static final String SPECIAL_FIELD_TYPE = "!type";
	
	static final String SPECIAL_FIELD_OBJ = "!obj";
	
	static final String SPECIAL_FIELD_LOCATION = "!location";
	
	// =========================================================================
	
	public AstNodeJsonTypeAdapter(Class<T> nodeType)
	{
		super(nodeType);
	}
	
	public static <S extends AstNode<S>> AstNodeJsonTypeAdapter<S> forNodeType(
			Class<S> nodeType)
	{
		return new AstNodeJsonTypeAdapter<S>(nodeType);
	}
	
	// =========================================================================
	
	@Override
	public JsonElement serialize(
			T src,
			Type typeOfSrc,
			JsonSerializationContext context)
	{
		return marshalNode(src, true, context);
	}
	
	@Override
	public T deserialize(
			JsonElement json,
			Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		return unmarshalNode(json, (Class<?>) typeOfT, context);
	}
	
	// =========================================================================
	
	private JsonElement marshalNode(
			T n,
			boolean typeInfoRequired,
			JsonSerializationContext context)
	{
		Class<?> nodeType = n.getClass();
		
		boolean isStringNode =
				isStringNode(nodeType) &&
						!n.hasAttributes() &&
						!n.hasLocation();
		
		if (isStringNode)
			// we can only invoke this function for string nodes!
			isStringNode &= !hasStringNodeVisibleProperties(n);
		
		if (!isStringNode)
		{
			JsonObject gsonNode = new JsonObject();
			if (typeInfoRequired)
				storeType(nodeType, gsonNode);
			
			storeLocation(n, gsonNode);
			
			storeAttributes(n, gsonNode, context);
			
			storeProperties(n, isStringNode, gsonNode, context);
			
			if (n.isList())
			{
				JsonArray array = new JsonArray();
				for (T c : n)
				{
					if (!isSuppressed(c))
						array.add(marshalNode(c, true, context));
				}
				gsonNode.add(SPECIAL_FIELD_LIST, array);
			}
			else
			{
				String[] childNum = n.getChildNames();
				for (int i = 0; i < childNum.length; ++i)
					storeNamedChild(n, i, gsonNode, context);
			}
			
			return gsonNode;
		}
		else
		{
			return new JsonPrimitive(((AstStringNode<T>) n).getContent());
		}
	}
	
	private T unmarshalNode(
			JsonElement json,
			Class<?> nodeType,
			JsonDeserializationContext context)
	{
		if (json.isJsonPrimitive())
		{
			if (nodeType != null && !isStringNode(nodeType))
				throw new SyntaxErrorException("Got JSON primitive but was not expecting String node");
			if (nodeType == null)
				nodeType = getStringNodeType();
			
			T n = instantiateNode(nodeType);
			((AstStringNode<T>) n).setContent(json.getAsString());
			
			initializeStringNodeProperties(n);
			
			return n;
		}
		else if (json.isJsonObject())
		{
			JsonObject jo = json.getAsJsonObject();
			
			nodeType = getExplicitType(jo, nodeType);
			if (nodeType == null)
				throw new SyntaxErrorException("Missing node type!");
			
			T n = instantiateNode(nodeType);
			
			String[] childNames = n.getChildNames();
			
			boolean[] initProperties = new boolean[n.getPropertyCount()];
			boolean[] initChildren = new boolean[childNames.length];
			
			for (Entry<String, JsonElement> e : jo.entrySet())
			{
				String key = e.getKey();
				JsonElement value = e.getValue();
				
				switch (key.charAt(0))
				{
					case '!':
						if (key.equals(SPECIAL_FIELD_LIST))
						{
							for (JsonElement c : value.getAsJsonArray())
								n.add(unmarshalNode(c, null, context));
						}
						else if (key.equals(SPECIAL_FIELD_LOCATION))
						{
							String loc = value.getAsString();
							n.setNativeLocation(AstLocation.valueOf(loc));
						}
						else if (key.equals(SPECIAL_FIELD_TYPE))
						{
							// We've handled this already...
						}
						else
						{
							throw new SyntaxErrorException("Unexpected special key in JSON " +
									"object when tryingn to deserialize AST node: " + key);
						}
						break;
					
					case '@':
						restoreAttribute(n, key, value, context);
						break;
					
					case '$':
						restoreProperty(n, key, value, context, initProperties);
						break;
					
					default:
						restoreNamedChild(n, key, value, context, childNames, initChildren);
						break;
				}
			}
			
			AstNodePropertyIterator propIter = n.propertyIterator();
			for (boolean b : initProperties)
			{
				propIter.next();
				if (!b)
					setDefaultProperty(n, propIter);
			}
			
			for (int i = 0; i < childNames.length; ++i)
			{
				if (!initChildren[i])
					setDefaultChild(n, i, childNames[i]);
			}
			
			return n;
		}
		else
		{
			throw new SyntaxErrorException("Expected node or text but got JSON array or null");
		}
	}
	
	// =========================================================================
	
	private void storeLocation(AstNode<T> n, JsonObject gsonNode)
	{
		if (!isLocationSuppressed())
		{
			AstLocation loc = n.getNativeLocation();
			if (loc != null)
				gsonNode.add(SPECIAL_FIELD_LOCATION, new JsonPrimitive(loc.toString()));
		}
	}
	
	// =========================================================================
	
	private void storeAttributes(
			AstNode<T> n,
			JsonObject gsonNode,
			JsonSerializationContext context)
	{
		if (isAttributesSuppressed())
			return;
		
		Map<String, Object> attrs = n.getAttributes();
		if (!attrs.isEmpty())
		{
			for (Entry<String, Object> e : attrs.entrySet())
			{
				String name = e.getKey();
				if (!isAttributeSuppressed(name))
					writeAttribute(name, e.getValue(), gsonNode, context);
			}
		}
	}
	
	private void writeAttribute(
			String name,
			Object value,
			JsonObject gsonNode,
			JsonSerializationContext context)
	{
		JsonObject jsonValue = null;
		if (value != null)
		{
			jsonValue = new JsonObject();
			storeType(value.getClass(), jsonValue);
			jsonValue.add("value", context.serialize(value));
		}
		gsonNode.add("@" + name, jsonValue);
	}
	
	private void restoreAttribute(
			T n,
			String key,
			JsonElement gsonValue,
			JsonDeserializationContext context)
	{
		String name = key.substring(1);
		
		Object value = null;
		if (!gsonValue.isJsonNull())
		{
			JsonObject jo = gsonValue.getAsJsonObject();
			
			Class<?> valueType = null;
			valueType = getExplicitType(jo, valueType);
			if (valueType == null)
				throw new SyntaxErrorException("Missing attribute type field!");
			
			JsonElement valueField = jo.get("value");
			if (valueField == null)
				throw new SyntaxErrorException("Missing attribute value field!");
			
			value = context.<Object> deserialize(valueField, valueType);
		}
		n.setAttribute(name, value);
	}
	
	// =========================================================================
	
	private boolean hasStringNodeVisibleProperties(AstNode<T> n)
	{
		for (AstNodePropertyIterator i = n.propertyIterator(); i.next();)
		{
			Object value = i.getValue();
			if (value == null)
				continue;
			
			String name = i.getName();
			if (isPropertySuppressed(name))
				continue;
			
			if ("content".equals(name))
				continue;
			
			return true;
		}
		
		return false;
	}
	
	private void storeProperties(
			AstNode<T> n,
			boolean suppressContent,
			JsonObject gsonNode,
			JsonSerializationContext context)
	{
		if (n.getPropertyCount() > 0)
		{
			for (AstNodePropertyIterator i = n.propertyIterator(); i.next();)
			{
				Object value = i.getValue();
				if (value == null)
					continue;
				
				String name = i.getName();
				if (isPropertySuppressed(name))
					continue;
				
				writeProperty(n, name, value, gsonNode, context);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void writeProperty(
			AstNode<T> parentNode,
			String name,
			Object value,
			JsonObject gsonNode,
			JsonSerializationContext context)
	{
		if (isSuppressed(value))
			return;
		
		String fieldName = "$" + name;
		JsonElement gsonValue = null;
		
		if (value != null)
		{
			// We always have to add the type information to the property name.
			// Otherwise we would have to check if a serialized object is an 
			// AST node and if so, we would have to peek into the serialized 
			// node to learn the type of the node...
			
			Class<? extends Object> valueType = value.getClass();
			
			if (isTypeInfoRequired(parentNode, name, valueType))
				fieldName += ":" + getTypeAlias(valueType);
			
			gsonValue = getNodeType().isAssignableFrom(valueType) ?
					marshalNode((T) value, isAlwaysStoreType(), context) :
					context.serialize(value);
		}
		
		gsonNode.add(fieldName, gsonValue);
	}
	
	private void restoreProperty(
			T n,
			String key,
			JsonElement gsonValue,
			JsonDeserializationContext context,
			boolean[] initProperties)
	{
		String name = key.substring(1);
		if (!gsonValue.isJsonNull())
		{
			Class<?> propType;
			
			int colon = key.indexOf(':');
			if (colon != -1)
			{
				name = key.substring(1, colon);
				propType = getClassForAlias(key.substring(colon + 1));
			}
			else
			{
				propType = getGetterType(n, name);
			}
			
			AstNodePropertyIterator propIter = n.propertyIterator();
			for (int i = 0; propIter.next(); ++i)
			{
				if (propIter.getName().equals(name))
				{
					Object value = context.deserialize(gsonValue, propType);
					propIter.setValue(value);
					initProperties[i] = true;
					break;
				}
			}
		}
		else
		{
			AstNodePropertyIterator propIter = n.propertyIterator();
			for (int i = 0; propIter.next(); ++i)
			{
				if (propIter.getName().equals(name))
				{
					setDefaultProperty(n, propIter);
					initProperties[i] = true;
					break;
				}
			}
		}
	}
	
	private void initializeStringNodeProperties(T n)
	{
		if (n.getPropertyCount() > 0)
		{
			for (AstNodePropertyIterator i = n.propertyIterator(); i.next();)
			{
				if (!i.getName().equals("content"))
					// If it's a string node the content property will be set
					// from a child, not a property!
					setDefaultProperty(n, i);
			}
		}
	}
	
	// =========================================================================
	
	private void storeNamedChild(
			T n,
			int i,
			JsonObject gsonParent,
			JsonSerializationContext context)
	{
		T child = n.get(i);
		if (isSuppressed(child))
			return;
		
		String name = n.getChildNames()[i];
		String fieldName = name;
		
		boolean typeInfoRequired = isTypeInfoRequired(n, name, child.getClass());
		JsonElement gsonNode = marshalNode(child, typeInfoRequired, context);
		gsonParent.add(fieldName, gsonNode);
	}
	
	private void restoreNamedChild(
			T n,
			String key,
			JsonElement gsonValue,
			JsonDeserializationContext context,
			String[] childNames,
			boolean[] initChildren)
	{
		for (int i = 0; i < childNames.length; ++i)
		{
			if (childNames[i].equals(key))
			{
				Class<?> childType = getGetterType(n, key);
				T child = unmarshalNode(gsonValue, childType, context);
				n.set(i, child);
				initChildren[i] = true;
				return;
			}
		}
		
		throw new SyntaxErrorException("Unexpected child element: '" +
				key + "' when unmarshalling node of type '" +
				n.getClass().getName() + "'");
	}
	
	// =========================================================================
	
	private void storeType(Class<?> type, JsonObject gsonNode)
	{
		String alias = getTypeAlias(type);
		storeType(alias, gsonNode);
	}
	
	protected static void storeType(String alias, JsonObject gsonNode)
	{
		gsonNode.add(SPECIAL_FIELD_TYPE, new JsonPrimitive(alias));
	}
	
	protected static void storeObject(JsonElement obj, JsonObject gsonNode)
	{
		gsonNode.add(SPECIAL_FIELD_OBJ, obj);
	}
	
	protected static String readType(JsonObject gsonNode)
	{
		JsonElement type = gsonNode.get(SPECIAL_FIELD_TYPE);
		if (type == null)
			throw new SyntaxErrorException("Expected key: " + SPECIAL_FIELD_TYPE);
		return type.getAsString();
	}
	
	protected static JsonElement readObject(JsonObject gsonNode)
	{
		JsonElement obj = gsonNode.get(SPECIAL_FIELD_OBJ);
		if (obj == null)
			throw new SyntaxErrorException("Expected key: " + SPECIAL_FIELD_OBJ);
		return obj;
	}
	
	private Class<?> getExplicitType(JsonObject jo, Class<?> valueType)
	{
		JsonElement explicitType = jo.get(SPECIAL_FIELD_TYPE);
		if (explicitType != null)
			valueType = getClassForAlias(explicitType.getAsString());
		return valueType;
	}
}
