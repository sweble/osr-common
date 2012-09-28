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

public abstract class LeafNode
        extends
            AstNode
{
	private static final long serialVersionUID = 3078845253977311630L;
	
	// =========================================================================
	
	@Override
	public int size()
	{
		return 0;
	}
	
	// =========================================================================
	
	public final String[] getChildNames()
	{
		return EMPTY_CHILD_NAMES;
	}
	
	// =========================================================================
	
	@Override
    public void toString(Appendable out) throws IOException
	{
		out.append(getClass().getSimpleName());
		out.append("()");
	}
}
