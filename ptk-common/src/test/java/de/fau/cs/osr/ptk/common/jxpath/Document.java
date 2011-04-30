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

package de.fau.cs.osr.ptk.common.jxpath;

import java.io.IOException;

import de.fau.cs.osr.ptk.common.ast.AstNode;

public class Document
        extends
            AstNode
{
	private static final long serialVersionUID = 1L;
	
	private AstNode n0;
	
	// =========================================================================
	
	public Document(AstNode root)
	{
		set(0, root);
	}
	
	// =========================================================================
	
	@Override
	public int size()
	{
		return 1;
	}
	
	@Override
	public AstNode get(int index)
	{
		switch (index)
		{
			case 0:
				return n0;
			default:
				throw new IndexOutOfBoundsException(
				        "Size: " + size() + ", Index: " + index);
		}
	}
	
	@Override
	public AstNode set(int index, AstNode n)
	{
		AstNode o;
		switch (index)
		{
			case 0:
				o = n0;
				n0 = n;
				break;
			default:
				throw new IndexOutOfBoundsException(
				        "Size: " + size() + ", Index: " + index);
		}
		
		return o;
	}
	
	@Override
	public AstNode remove(int index)
	{
		return set(index, null);
	}
	
	@Override
	public int getNodeType()
	{
		return -1;
	}
	
	@Override
	public void toString(Appendable out) throws IOException
	{
		out.append(getClass().getSimpleName());
		out.append('(');
		AstNode child = get(0);
		if (child == null)
			out.append("null");
		else
			child.toString(out);
		out.append(')');
	}
}
