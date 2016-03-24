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
import java.io.StringWriter;
import java.io.Writer;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.utils.ComparisonException;

public class AstComparisonException
		extends
			ComparisonException
{
	private static final long serialVersionUID = 1L;

	private final AstDifference reason;

	public AstComparisonException()
	{
		this(null, null, AstDifference.DEEP_COMPARISON_FAILED);
	}

	public AstComparisonException(AstComparisonException e)
	{
		this(e, null, null, AstDifference.DEEP_COMPARISON_FAILED);
	}

	public AstComparisonException(
			AstNode<?> a,
			AstNode<?> b,
			AstDifference reason)
	{
		super(a, b);
		this.reason = reason;
	}

	protected AstComparisonException(
			AstComparisonException e,
			AstNode<?> a,
			AstNode<?> b)
	{
		this(e, a, b, AstDifference.CHILDREN_DIFFER);
	}

	protected AstComparisonException(
			AstComparisonException e,
			AstNode<?> a,
			AstNode<?> b,
			AstDifference reason)
	{
		super(e, a, b);
		this.reason = reason;
	}

	public AstNode<?> getA()
	{
		return (AstNode<?>) super.getA();
	}

	public AstNode<?> getB()
	{
		return (AstNode<?>) super.getB();
	}

	public AstDifference getReason()
	{
		return reason;
	}

	protected Writer toString(Writer w) throws IOException
	{
		if ((getA() != null) != (getB() != null))
		{
			if (getA() != null)
			{
				w.append(String.format(
						"Node A of type %s is not null while node B is null: %s\n",
						getA().getNodeName(),
						reason.getReason()));
			}
			else
			{
				w.append(String.format(
						"Node B of type %s is not null while node A is null: %s\n",
						getB().getNodeName(),
						reason.getReason()));
			}
		}
		else if (getA().getClass() != getB().getClass())
		{
			w.append(String.format(
					"Two nodes differ in type %s vs. %s: %s\n",
					getA().getClass().getName(),
					getB().getClass().getName(),
					reason.getReason()));
		}
		else
		{
			w.append(String.format(
					"Two nodes of type %s differ: %s\n",
					getA().getNodeName(),
					reason.getReason()));
		}
		return w;
	}

	@Override
	public String toString()
	{
		try
		{
			return toString(new StringWriter()).toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
