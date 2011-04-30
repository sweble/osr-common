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

public final class Utils
{
	public static <D> D getInstance(Class<D> clazz)
	{
		try
		{
			return clazz.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new FmtRuntimeException(
			        e,
			        "The given class `%s' is either a class which cannot be " +
			                "instantiated this way (abstract class, " +
			                "interface, array class, primitive type, void), " +
			                "or the class has no nullary constructor. The " +
			                "instantiation could also have failed for " +
			                "another reason.",
			        clazz.getName());
		}
		catch (IllegalAccessException e)
		{
			throw new FmtRuntimeException(
			        e,
			        "The specified class `%s' is not accessible or its " +
			                "nullary constructor is not accessible.",
			        clazz.getName());
		}
	}
}
