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

package de.fau.cs.osr.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class ComparisonException
		extends
			Exception
{
	private static final long serialVersionUID = 1L;

	private final Object a;

	private final Object b;

	// =========================================================================

	public ComparisonException()
	{
		this(null, null);
	}

	public ComparisonException(ComparisonException e)
	{
		this(e, null, null);
	}

	public ComparisonException(
			Object a,
			Object b)
	{
		this.a = a;
		this.b = b;
	}

	protected ComparisonException(
			ComparisonException e,
			Object a,
			Object b)
	{
		super(e);
		this.a = a;
		this.b = b;
	}

	// =========================================================================

	public Object getA()
	{
		return a;
	}

	public Object getB()
	{
		return b;
	}

	// =========================================================================

	protected String getObjectDesc(Object o)
	{
		return o.getClass().getName();
	}

	// =========================================================================

	protected Writer toString(Writer w) throws IOException
	{
		if ((getA() != null) != (getB() != null))
		{
			if (getA() != null)
			{
				w.append(String.format(
						"Object A of type %s is not null while object B is null",
						getObjectDesc(getA())));
			}
			else
			{
				w.append(String.format(
						"Object B of type %s is not null while object A is null",
						getObjectDesc(getB())));
			}
		}
		else if (getA().getClass() != getB().getClass())
		{
			w.append(String.format(
					"Two objects differ in type %s vs. %s",
					getA().getClass().getName(),
					getB().getClass().getName()));
		}
		else
		{
			w.append(String.format(
					"Two objects of type %s differ",
					getObjectDesc(getA())));
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
