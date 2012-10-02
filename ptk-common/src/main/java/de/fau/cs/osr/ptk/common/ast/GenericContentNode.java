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

package de.fau.cs.osr.ptk.common.ast;

import java.io.IOException;

import xtc.tree.Location;
import de.fau.cs.osr.ptk.common.ast.GenericInnerNode.GenericInnerNode1;

public abstract class GenericContentNode<T extends AstNodeInterface<T>, L extends GenericNodeList<T>>
		extends
			GenericInnerNode1<T>
{
	private static final long serialVersionUID = -1222289289745315685L;
	
	// =========================================================================
	
	public GenericContentNode()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public GenericContentNode(Location arg0, T n0)
	{
		super(arg0, n0);
		// TODO Auto-generated constructor stub
	}
	
	public GenericContentNode(Location arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public GenericContentNode(T n0)
	{
		super(n0);
		// TODO Auto-generated constructor stub
	}
	
	public L getContent()
	{
		return (L) get(0);
	}
	
	@SuppressWarnings("unchecked")
	public L setContent(L value)
	{
		return (L) set(0, (T) value);
	}
	
	// =========================================================================
	
	private static final String[] CHILD_NAMES = new String[] { "content" };
	
	public final String[] getChildNames()
	{
		return CHILD_NAMES;
	}
	
	// =========================================================================
	
	@Override
	public void toString(Appendable out) throws IOException
	{
		out.append(getClass().getSimpleName());
		out.append(getContent().toString());
	}
}
