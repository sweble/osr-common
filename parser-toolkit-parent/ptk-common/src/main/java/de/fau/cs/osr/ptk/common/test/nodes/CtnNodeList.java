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
package de.fau.cs.osr.ptk.common.test.nodes;

import java.util.Collection;

import de.fau.cs.osr.ptk.common.ast.AstNodeListImpl;
import xtc.util.Pair;

public final class CtnNodeList
		extends
			AstNodeListImpl<CtnNode>
		implements
			CtnNode
{
	private static final long serialVersionUID = 1L;

	// =========================================================================

	protected CtnNodeList()
	{
	}

	protected CtnNodeList(Collection<? extends CtnNode> list)
	{
		super(list);
	}

	protected CtnNodeList(Pair<? extends CtnNode> list)
	{
		super(list);
	}

	protected CtnNodeList(CtnNode car, Pair<? extends CtnNode> cdr)
	{
		super(car, cdr);
	}

	protected CtnNodeList(
			CtnNode a,
			CtnNode b,
			CtnNode c,
			CtnNode d)
	{
		super(a, b, c, d);
	}

	protected CtnNodeList(CtnNode a, CtnNode b, CtnNode c)
	{
		super(a, b, c);
	}

	protected CtnNodeList(CtnNode a, CtnNode b)
	{
		super(a, b);
	}

	protected CtnNodeList(CtnNode... children)
	{
		super(children);
	}

	protected CtnNodeList(CtnNode child)
	{
		super(child);
	}

	// =========================================================================

	@Override
	public String getNodeName()
	{
		return "list";
	}
}
