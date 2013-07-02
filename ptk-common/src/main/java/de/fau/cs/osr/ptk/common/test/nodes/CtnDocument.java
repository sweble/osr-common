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

import xtc.util.Pair;
import de.fau.cs.osr.ptk.common.ast.AstNodeListImpl;

public final class CtnDocument
		extends
			AstNodeListImpl<CtnNode>
		implements
			CtnNode
{
	private static final long serialVersionUID = 1L;
	
	// =========================================================================
	
	public CtnDocument()
	{
	}
	
	public CtnDocument(Collection<? extends CtnNode> list)
	{
		super(list);
	}
	
	public CtnDocument(Pair<? extends CtnNode> list)
	{
		super(list);
	}
	
	public CtnDocument(CtnNode car, Pair<? extends CtnNode> cdr)
	{
		super(car, cdr);
	}
	
	public CtnDocument(CtnNode a, CtnNode b, CtnNode c)
	{
		super(a, b, c);
	}
	
	public CtnDocument(CtnNode a, CtnNode b)
	{
		super(a, b);
	}
	
	public CtnDocument(CtnNode... children)
	{
		super(children);
	}
	
	public CtnDocument(CtnNode child)
	{
		super(child);
	}
	
	// =========================================================================
	
	@Override
	public int getNodeType()
	{
		return NT_TEST_DOCUMENT;
	}
	
	@Override
	public String getNodeName()
	{
		return "document";
	}
}
