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

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;

import de.fau.cs.osr.utils.ReflectionUtils.ArrayInfo;

public class SimpleTypeNameMapper
		implements
			TypeNameMapper
{
	private DualHashBidiMap typeToName;

	// =========================================================================

	public SimpleTypeNameMapper()
	{
		typeToName = new DualHashBidiMap();
	}

	public SimpleTypeNameMapper(SimpleTypeNameMapper stnm)
	{
		typeToName = new DualHashBidiMap(stnm.typeToName);
	}

	// =========================================================================

	public void add(Class<?> type, String name)
	{
		typeToName.put(type, name);
	}

	// =========================================================================

	@Override
	public String nameForType(Class<?> n)
	{
		String typeAlias = (String) typeToName.get(n);
		if (typeAlias != null)
			return typeAlias;

		if (n.isArray())
		{
			ArrayInfo dim = ReflectionUtils.arrayDimension(n);
			String elementTypeName = nameForType(dim.elementClass);
			return elementTypeName + StringUtils.repeat("[]", dim.dim);
		}

		if (ReflectionUtils.isBasicDataType(n))
			return ReflectionUtils.abbreviateBasicDataTypeName(n);

		return nameForUnmappedType(n);
	}

	protected String nameForUnmappedType(Class<?> n)
	{
		return n.getName();
	}

	// =========================================================================

	@Override
	public Class<?> typeForName(String name)
	{
		if (name.isEmpty())
			return typeForInvalidName(name);

		Class<?> type = (Class<?>) typeToName.getKey(name);
		if (type != null)
			return type;

		int nameEnd = name.indexOf("[]");
		if (nameEnd > 0)
		{
			int i;
			for (i = nameEnd + 2; i < name.length(); i += 2)
			{
				if (name.indexOf("[]", i) != i)
					return typeForInvalidName(name);
			}

			return ReflectionUtils.arrayClassFor(
					typeForName(name.substring(0, nameEnd)),
					(i - nameEnd) / 2);
		}

		if (ReflectionUtils.isBasicDataType(name))
			return ReflectionUtils.getTypeFromAbbreviation(name);

		return typeForUnmappedName(name);
	}

	protected Class<?> typeForInvalidName(String name)
	{
		throw new IllegalArgumentException("Illegal type name '" + name + "'");
	}

	protected Class<?> typeForUnmappedName(String name)
	{
		try
		{
			return Class.forName(name);
		}
		catch (ClassNotFoundException e1)
		{
			// Cannot map type
			return null;
		}
	}
}
