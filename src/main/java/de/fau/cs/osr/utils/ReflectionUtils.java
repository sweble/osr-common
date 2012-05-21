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

public class ReflectionUtils
{
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
		if (className == "byte")
		{
			return byte.class;
		}
		else if (className == "short")
		{
			return short.class;
		}
		else if (className == "int")
		{
			return int.class;
		}
		else if (className == "long")
		{
			return long.class;
		}
		else if (className == "float")
		{
			return float.class;
		}
		else if (className == "double")
		{
			return double.class;
		}
		else if (className == "boolean")
		{
			return boolean.class;
		}
		else if (className == "char")
		{
			return char.class;
		}
		else if (className == "void")
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
		return (clazz == Byte.class
				|| clazz == Short.class
				|| clazz == Integer.class
				|| clazz == Long.class
				|| clazz == Float.class
				|| clazz == Double.class
				|| clazz == Boolean.class
				|| clazz == Character.class
				|| clazz == Void.class);
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
