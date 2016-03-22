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

package de.fau.cs.osr.utils.visitor;

/**
 * Thrown if a suitable visit() method could not be found for a given node.
 */
public class VisitNotFoundException
        extends
            RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	private final Object node;
	
	private final VisitorBase<?> visitor;
	
	public VisitNotFoundException(VisitorBase<?> visitorBase, Object node2)
	{
		super(makeMessage(visitorBase, node2));
		this.visitor = visitorBase;
		this.node = node2;
	}
	
	public Object getNode()
	{
		return node;
	}
	
	public VisitorBase<?> getVisitor()
	{
		return visitor;
	}
	
	private static String makeMessage(VisitorBase<?> visitorBase, Object node)
	{
		final String nodeName = node.getClass().getName();
		final String visitorName = visitorBase.getClass().getName();
		
		return "Unable to find visit() method for node of type `" +
		        nodeName + "' in visitor `" + visitorName + "'";
	}
}
