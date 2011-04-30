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

import de.fau.cs.osr.ptk.common.ast.AstNode;

/**
 * Thrown if a suitable visit() method could not be found for a given node.
 */
public class VisitNotFoundException
        extends
            RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	private final AstNode node;
	
	private final Visitor visitor;
	
	public VisitNotFoundException(Visitor visitor, AstNode node)
	{
		super(makeMessage(visitor, node));
		this.visitor = visitor;
		this.node = node;
	}
	
	public AstNode getNode()
	{
		return node;
	}
	
	public Visitor getVisitor()
	{
		return visitor;
	}
	
	private static String makeMessage(Visitor visitor, AstNode node)
	{
		return "Unable to find visit() method for node of type `" +
		        node.getNodeName() + "' in visitor `" +
		        visitor.getClass().getSimpleName() + "'";
	}
}
