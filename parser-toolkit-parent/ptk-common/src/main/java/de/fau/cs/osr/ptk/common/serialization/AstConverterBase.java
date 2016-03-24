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
package de.fau.cs.osr.ptk.common.serialization;

import de.fau.cs.osr.utils.MissingTypeInformationException;
import de.fau.cs.osr.utils.SimpleTypeNameMapper;
import de.fau.cs.osr.utils.TypeNameMapper;
import de.fau.cs.osr.utils.UnknownTypeException;

public class AstConverterBase
{
	private TypeNameMapper typeNameMapper = new SimpleTypeNameMapper();

	// =========================================================================

	public void setTypeNameMapper(TypeNameMapper typeNameMapper)
	{
		this.typeNameMapper = typeNameMapper;
	}

	// =========================================================================

	protected String getTypeAlias(Class<?> n)
	{
		String typeAlias = typeNameMapper.nameForType(n);
		if (typeAlias == null)
			throw new MissingTypeInformationException(
					"Cannot determine type alias for class '" + n.getName() + "'");
		return typeAlias;
	}

	protected Class<?> getClassForAlias(String typeAlias)
	{
		Class<?> type = typeNameMapper.typeForName(typeAlias);
		if (type == null)
			throw new UnknownTypeException("Cannot find type for name '" + typeAlias + "'");
		return type;
	}
}
