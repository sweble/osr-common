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

import org.junit.Ignore;
import org.junit.Test;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.LeafNode;

@Ignore
public class VisitorTest
{
	@Test
	public void test()
	{
		new VisitorB().go(new NodeA());
		new VisitorB().go(new NodeB());
	}
	
	static class VisitorA
	        extends
	            Visitor
	{
		public void visit(AstNode n)
		{
			System.out.println("AstNode: " + n.getClass().getSimpleName());
		}
	}
	
	final static class VisitorB
	        extends
	            VisitorA
	{
		public void visit(NodeA n)
		{
			System.out.println("NodeA: " + n.getClass().getSimpleName());
		}
	}
	
	final static class NodeA
	        extends
	            LeafNode
	{
		private static final long serialVersionUID = 1L;
	}
	
	final static class NodeB
	        extends
	            LeafNode
	{
		private static final long serialVersionUID = 1L;
	}
}
