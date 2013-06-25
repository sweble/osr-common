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

package de.fau.cs.osr.ptk.common.comparer;

import java.io.IOException;
import java.io.Writer;

import de.fau.cs.osr.ptk.common.ast.AstNode;

public class ComparisonOfChildrenFailedException
		extends
			AstComparisonException
{
	private static final long serialVersionUID = 1L;
	
	private final int childIndex;
	
	private final String childName;
	
	public ComparisonOfChildrenFailedException(
			AstNode<?> a,
			AstNode<?> b,
			int childIndex,
			AstComparisonException e)
	{
		super(e, a, b);
		this.childIndex = childIndex;
		this.childName = null;
	}
	
	public ComparisonOfChildrenFailedException(
			AstNode<?> a,
			AstNode<?> b,
			int childIndex,
			String childName,
			AstComparisonException e)
	{
		super(e, a, b);
		this.childIndex = childIndex;
		this.childName = childName;
	}
	
	public int getChildIndex()
	{
		return childIndex;
	}
	
	public String getChildName()
	{
		return childName;
	}
	
	public Writer toString(Writer w) throws IOException
	{
		((AstComparisonException) getCause()).toString(w);
		if (childName == null)
		{
			w.append(String.format(
					"The %dth child nodes of two list nodes of type %s differ.\n",
					childIndex,
					getA().getNodeName()));
		}
		else
		{
			w.append(String.format(
					"The %dth child nodes (`%s') of two list nodes of type %s differ.\n",
					childIndex,
					childName,
					getA().getNodeName()));
		}
		return w;
	}
}
