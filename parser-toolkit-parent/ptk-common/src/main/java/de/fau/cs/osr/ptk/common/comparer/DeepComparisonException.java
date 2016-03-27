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
import de.fau.cs.osr.utils.StringTools;

public class DeepComparisonException
		extends
			AstComparisonException
{
	private static final long serialVersionUID = 1L;

	private final Object a;

	private final Object b;

	public DeepComparisonException(Object a, Object b)
	{
		this.a = a;
		this.b = b;
	}

	public DeepComparisonException(
			AstComparisonException e,
			AstNode<?> a,
			AstNode<?> b)
	{
		super(e);
		this.a = a;
		this.b = b;
	}

	public Object getObjectA()
	{
		return a;
	}

	public Object getObjectB()
	{
		return b;
	}

	public Writer toString(Writer w) throws IOException
	{
		if (getCause() != null)
			((AstComparisonException) getCause()).toString(w);
		w.append(String.format(
				"Values during deep comparison differed (%s vs. %s):\n",
				(a == null ? "-" : a.getClass().getName()),
				(b == null ? "-" : b.getClass().getName())));
		printValue("a", a, w);
		printValue("b", b, w);
		return w;
	}

	private void printValue(String which, Object value, Writer w) throws IOException
	{
		w.append("\t");
		w.append(which);
		w.append(": ");
		if (value == null)
			w.append("null\n");
		else
		{
			w.append("\"\"\"");
			w.append(StringTools.escJava(value.toString()));
			w.append("\"\"\"\n");
		}
	}
}
