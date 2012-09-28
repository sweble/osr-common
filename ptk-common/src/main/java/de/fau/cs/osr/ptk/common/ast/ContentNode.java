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
import de.fau.cs.osr.ptk.common.ast.InnerNode.InnerNode1;

public abstract class ContentNode
        extends
            InnerNode1
{
	private static final long serialVersionUID = -1222289289745315685L;
	
	public ContentNode()
	{
		setContent(new NodeList());
	}
	
	public ContentNode(AstNodeInterface content)
	{
		setContent(new NodeList(content));
	}
	
	public ContentNode(AstNodeInterface content, Location location)
	{
		super(location);
		setContent(new NodeList(content));
	}
	
	public ContentNode(NodeList content)
	{
		setContent(content);
	}
	
	public ContentNode(NodeList content, Location location)
	{
		super(location);
		setContent(content);
	}
	
	// =========================================================================
	
	public NodeList getContent()
	{
		return (NodeList) get(0);
	}
	
	public NodeList setContent(NodeList name)
	{
		return (NodeList) set(0, name);
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
