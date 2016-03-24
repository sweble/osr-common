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

package de.fau.cs.osr.utils;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ReflectionUtils
{
	private static final Map<Class<?>, Class<?>> lcToUcTypeMap = new HashMap<Class<?>, Class<?>>();

	private static final Map<Class<?>, Class<?>> ucToLcTypeMap = new HashMap<Class<?>, Class<?>>();

	private static final Map<Class<?>, String> typeToAbbrev = new HashMap<Class<?>, String>();

	private static final Map<String, Class<?>> abbrevToType = new HashMap<String, Class<?>>();

	// =========================================================================

	static
	{
		lcToUcTypeMap.put(byte.class, Byte.class);
		lcToUcTypeMap.put(short.class, Short.class);
		lcToUcTypeMap.put(int.class, Integer.class);
		lcToUcTypeMap.put(long.class, Long.class);
		lcToUcTypeMap.put(float.class, Float.class);
		lcToUcTypeMap.put(double.class, Double.class);
		lcToUcTypeMap.put(boolean.class, Boolean.class);
		lcToUcTypeMap.put(char.class, Character.class);

		for (Entry<Class<?>, Class<?>> e : lcToUcTypeMap.entrySet())
			ucToLcTypeMap.put(e.getValue(), e.getKey());

		typeToAbbrev.put(byte.class, "byte");
		typeToAbbrev.put(short.class, "short");
		typeToAbbrev.put(int.class, "int");
		typeToAbbrev.put(long.class, "long");
		typeToAbbrev.put(float.class, "float");
		typeToAbbrev.put(double.class, "double");
		typeToAbbrev.put(boolean.class, "boolean");
		typeToAbbrev.put(char.class, "char");

		typeToAbbrev.put(Byte.class, "Byte");
		typeToAbbrev.put(Short.class, "Short");
		typeToAbbrev.put(Integer.class, "Integer");
		typeToAbbrev.put(Long.class, "Long");
		typeToAbbrev.put(Float.class, "Float");
		typeToAbbrev.put(Double.class, "Double");
		typeToAbbrev.put(Boolean.class, "Boolean");
		typeToAbbrev.put(Character.class, "Character");

		typeToAbbrev.put(String.class, "String");

		for (Entry<Class<?>, String> e : typeToAbbrev.entrySet())
			abbrevToType.put(e.getValue(), e.getKey());
	}

	// =========================================================================

	/**
	 * Class.forName() cannot instantiate Class objects for primitive data types
	 * like `int'. This method considers these cases too.
	 * 
	 * @param className
	 *            The name of the class or the name of a primitive data type.
	 * @return The Class object for the given name.
	 * @throws ClassNotFoundException
	 *             If no class for the given name could be found.
	 */
	public static Class<?> classForName(String className) throws ClassNotFoundException
	{
		if (className.equals("byte"))
		{
			return byte.class;
		}
		else if (className.equals("short"))
		{
			return short.class;
		}
		else if (className.equals("int"))
		{
			return int.class;
		}
		else if (className.equals("long"))
		{
			return long.class;
		}
		else if (className.equals("float"))
		{
			return float.class;
		}
		else if (className.equals("double"))
		{
			return double.class;
		}
		else if (className.equals("boolean"))
		{
			return boolean.class;
		}
		else if (className.equals("char"))
		{
			return char.class;
		}
		else if (className.equals("void"))
		{
			return void.class;
		}
		else
		{
			return Class.forName(className);
		}
	}

	/**
	 * Class.isPrimitive only works for the built-in Java types (eg. int,
	 * boolean). This method only works for the "uppercase" variants of Java's
	 * primitive types (eg. Integer, Boolean, ...).
	 * 
	 * @param clazz
	 *            The class of the type to query.
	 * @return True if it's a "real" primitive data type of one of the
	 *         "uppercase" variants, false otherwise.
	 */
	public static boolean isExtPrimitive(Class<?> clazz)
	{
		return ucToLcTypeMap.containsKey(clazz);
	}

	/**
	 * Tells whether a data type is a primitive an "ext" primitive (see
	 * isExtPrimitive) or a String.
	 * 
	 * @param clazz
	 *            The class of the type to query.
	 * @return True if it's a primitive an "ext" primitive or a String, false
	 *         otherwise.
	 */
	public static boolean isBasicDataType(Class<?> clazz)
	{
		return (clazz.isPrimitive()
				|| isExtPrimitive(clazz)
				|| clazz == String.class);
	}

	public static boolean isBasicDataType(String name)
	{
		return abbrevToType.containsKey(name);
	}

	public static Class<?> mapPrimitiveToUcType(Class<?> clazz)
	{
		Class<?> ucType = lcToUcTypeMap.get(clazz);
		if (ucType == null)
			throw new IllegalArgumentException("Not a primitive data type!");
		return ucType;
	}

	public static String abbreviateBasicDataTypeName(Class<?> clazz)
	{
		String abbrev = typeToAbbrev.get(clazz);
		if (abbrev == null)
			throw new IllegalArgumentException("Not a basic data type!");
		return abbrev;
	}

	public static Class<?> getTypeFromAbbreviation(String abbrev)
	{
		Class<?> type = abbrevToType.get(abbrev);
		if (type == null)
			throw new IllegalArgumentException("Not the abbreviation of a basic data type!");
		return type;
	}

	/**
	 * Creates an array type for the given component type with the given
	 * dimension.
	 * 
	 * @param clazz
	 *            The component type of the array.
	 * @param dim
	 *            The dimension of the array.
	 * @return The Class object describing the array type.
	 */
	public static Class<?> arrayClassFor(Class<?> clazz, int dim)
	{
		if (dim <= 0)
			throw new IllegalArgumentException("Invalid dimension");

		Class<?> arrayClass = clazz;
		for (int i = 1; i <= dim; ++i)
			arrayClass = Array.newInstance(arrayClass, 0).getClass();

		return arrayClass;
	}

	/**
	 * Get the dimension and element type of an array type.
	 * 
	 * @param clazz
	 *            The array type.
	 * @return The dimension and element type of the represented array. If the
	 *         specified type is not an arry type, the dimension is 0 and the
	 *         element type is the specified type itself.
	 */
	public static ArrayInfo arrayDimension(Class<?> clazz)
	{
		int dim = 0;

		Class<?> cClass = clazz;
		while (cClass.isArray())
		{
			cClass = cClass.getComponentType();
			++dim;
		}
		return new ArrayInfo(cClass, dim);
	}

	// =========================================================================

	public static final class ArrayInfo
	{
		public final Class<?> elementClass;

		public final int dim;

		public ArrayInfo(Class<?> elementClass, int dim)
		{
			super();
			this.elementClass = elementClass;
			this.dim = dim;
		}
	}
}
