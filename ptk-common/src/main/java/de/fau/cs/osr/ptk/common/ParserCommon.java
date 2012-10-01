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

import de.fau.cs.osr.ptk.common.ast.AstNodeInterface;

/**
 * @deprecated
 */
public abstract class ParserCommon
		implements
			ParserInterface
{
	private final List<AstVisitor> visitors = new LinkedList<AstVisitor>();
	
	// =========================================================================
	
	@Override
	public List<AstVisitor> getVisitors()
	{
		return visitors;
	}
	
	@Override
	public ParserInterface addVisitor(AstVisitor v)
	{
		visitors.add(v);
		return this;
	}
	
	@Override
	public ParserInterface addVisitors(Collection<? extends AstVisitor> v)
	{
		visitors.addAll(v);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends AstNodeInterface<T>> AstNodeInterface<T> process(
			AstNodeInterface<T> n)
	{
		AstNodeInterface<T> result = n;
		for (AstVisitor v : getVisitors())
		{
			Object o = v.go(result);
			if (o instanceof AstNodeInterface)
				result = (AstNodeInterface<T>) o;
		}
		return result;
	}
}
