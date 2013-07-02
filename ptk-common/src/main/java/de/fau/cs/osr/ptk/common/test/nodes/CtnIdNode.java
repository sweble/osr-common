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

import de.fau.cs.osr.ptk.common.ast.AstLeafNodeImpl;

public final class CtnIdNode
		extends
			AstLeafNodeImpl<CtnNode>
		implements
			CtnNode
{
	private static final long serialVersionUID = 1L;
	
	public int id = -1;
	
	// =========================================================================
	
	protected CtnIdNode(int id)
	{
		this.id = id;
	}
	
	// =========================================================================
	
	@Override
	public int getNodeType()
	{
		return NT_ID_NODE;
	}
	
	@Override
	public String getNodeName()
	{
		return "id";
	}
	
	// =========================================================================
	
	@Override
	public String toString()
	{
		return "IdNode [id=" + id + "]";
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CtnIdNode other = (CtnIdNode) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
