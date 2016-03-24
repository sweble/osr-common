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

import de.fau.cs.osr.ptk.common.ast.AstNode;

public class SimpleNodeFactory<T extends AstNode<T>>
		implements
			NodeFactory<T>
{
	public T instantiateNode(Class<?> clazz)
	{
		try
		{
			@SuppressWarnings("unchecked")
			T n = (T) clazz.newInstance();
			return n;
		}
		catch (InstantiationException e)
		{
			throw new IncompatibleAstNodeClassException("Class '" + clazz.getName() + "' cannot be instantiated", e);
		}
		catch (IllegalAccessException e)
		{
			throw new IncompatibleAstNodeClassException("Class '" + clazz.getName() + "' cannot be instantiated", e);
		}
	}

	public T instantiateDefaultChild(NamedMemberId id, Class<?> type)
	{
		throw new NoDefaultValueException("Don't know which class to " +
				"instantiate as default for child '" + id.memberName +
				"' of node '" + id.nodeType.getName() + "'");
	}

	public Object instantiateDefaultProperty(
			NamedMemberId id,
			Class<?> type)
	{
		throw new NoDefaultValueException("Don't know which class to " +
				"instantiate as default for property '" + id.memberName +
				"' of node '" + id.nodeType.getName() + "'");
	}
}
