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

package de.fau.cs.osr.ptk.common;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.fau.cs.osr.ptk.common.ast.AstNode;

public abstract class ParserCommon<T extends AstNode<T>>
		implements
			ParserInterface<T>
{
	private final List<AstVisitor<T>> visitors = new LinkedList<AstVisitor<T>>();

	// =========================================================================

	@Override
	public List<AstVisitor<T>> getVisitors()
	{
		return visitors;
	}

	@Override
	public ParserInterface<T> addVisitor(AstVisitor<T> v)
	{
		visitors.add(v);
		return this;
	}

	@Override
	public ParserInterface<T> addVisitors(Collection<? extends AstVisitor<T>> v)
	{
		visitors.addAll(v);
		return this;
	}

	public abstract Object getConfig();

	@SuppressWarnings("unchecked")
	protected T process(T n)
	{
		T result = n;
		for (AstVisitor<T> v : getVisitors())
		{
			Object o = v.go(result);
			if (o instanceof AstNode)
				result = (T) o;
		}
		return result;
	}
}
