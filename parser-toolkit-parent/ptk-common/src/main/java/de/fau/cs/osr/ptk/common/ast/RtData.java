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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import de.fau.cs.osr.utils.StringTools;
import de.fau.cs.osr.utils.WrappedException;

public class RtData
		implements
			Serializable,
			Cloneable
{
	public static final String SEP = new String();

	public static final RtData SUPPRESS = new RtDataSuppressSingleton();

	// =========================================================================

	private static final long serialVersionUID = 1L;

	private static final Object[] EMPTY_FIELD = {};

	private final Object[][] fields;

	// =========================================================================

	/**
	 * Constructor for SUPPRESS singleton.
	 */
	protected RtData()
	{
		this.fields = new Object[0][];
	}

	/**
	 * Instantiates an RtData object and initializes its size to match that of
	 * the given AstNodeInterface<?>.
	 * 
	 * An AstNodeInterface<?> with two children will need an RtData object with
	 * size three.
	 */
	public RtData(AstNode<?> node)
	{
		this(node.size() + 1);
	}

	/**
	 * Instantiates an empty RtData object with the given size.
	 */
	public RtData(int size)
	{
		if (size <= 0)
			throw new IllegalArgumentException("RtData must have a size of at least 1");

		this.fields = new Object[size][];
		Arrays.fill(this.fields, EMPTY_FIELD);
	}

	/**
	 * Instantiates an RtData object, initializes its size to match that of the
	 * given AstNodeInterface<?> and immediately fills it with glue information.
	 * 
	 * To move on to the next glue field insert a SEP object.
	 */
	public RtData(AstNode<?> node, Object... glue)
	{
		this(node);
		set(glue);
	}

	/**
	 * Instantiates an RtData object with the given size and immediately fills
	 * it with glue information.
	 * 
	 * To move on to the next glue field insert a SEP object.
	 */
	public RtData(int size, Object... glue)
	{
		this(size);
		set(glue);
	}

	/**
	 * Instantiates an RtData object, initializes its size to match that of the
	 * given AstNodeInterface<?> and immediately fills it with glue information.
	 * 
	 * To move on to the next glue field insert a SEP object.
	 */
	public RtData(AstNode<?> node, String... glue)
	{
		this(node);
		set(glue);
	}

	/**
	 * Instantiates an RtData object with the given size and immediately fills
	 * it with glue information.
	 * 
	 * To move on to the next glue field insert a SEP object.
	 */
	public RtData(int size, String... glue)
	{
		this(size);
		set(glue);
	}

	protected RtData(RtData rtData)
	{
		Object[][] other = rtData.fields;

		this.fields = new Object[other.length][];
		for (int i = 0; i < fields.length; ++i)
		{
			this.fields[i] =
					(other[i] == EMPTY_FIELD) ?
							EMPTY_FIELD :
							other[i].clone();
		}
	}

	// =========================================================================

	public void set(Object... glue)
	{
		int seps = 0;
		int from = 0;
		int to = 0;
		for (int field = 0; field < fields.length; ++field)
		{
			while (to < glue.length)
			{
				if (glue[to] == SEP)
				{
					++seps;
					break;
				}
				++to;
			}

			setFieldFromArraySection(field, glue, from, to);

			if (to < glue.length)
				++to;
			from = to;
		}

		if (seps >= fields.length)
			throw new IndexOutOfBoundsException("The glue array has too many fields!");
	}

	public void set(String... glue)
	{
		int seps = 0;
		int from = 0;
		int to = 0;
		for (int field = 0; field < fields.length; ++field)
		{
			while (to < glue.length)
			{
				if (glue[to] == SEP)
				{
					++seps;
					break;
				}
				++to;
			}

			setFieldFromArraySection(field, glue, from, to);

			if (to < glue.length)
				++to;
			from = to;
		}

		if (seps >= fields.length)
			throw new IndexOutOfBoundsException("The glue array has too many fields!");
	}

	// =========================================================================

	public void setField(int field, Object... glue)
	{
		setFieldFromArraySection(field, glue, 0, glue.length);
	}

	public void setField(int field, String... glue)
	{
		setFieldFromArraySection(field, glue, 0, glue.length);
	}

	public void setField(int field, String glue)
	{
		if (glue == null || glue.isEmpty())
		{
			this.fields[field] = EMPTY_FIELD;
		}
		else
		{
			this.fields[field] = new Object[] { glue };
		}
	}

	// =========================================================================

	private void setFieldFromArraySection(
			int field,
			Object[] glue,
			int from,
			int to)
	{
		if (from < 0 || to > glue.length)
			throw new IndexOutOfBoundsException();

		if (to <= from)
			return;

		ArrayList<Object> result = new ArrayList<Object>(glue.length);
		for (int i = from; i < to; ++i)
		{
			Object o = glue[i];
			if (o == null)
			{
			}
			else if (o instanceof Object[])
			{
				for (Object o2 : (Object[]) o)
					addObject(result, o2);
			}
			else
			{
				addObject(result, o);
			}
		}

		if (result.isEmpty())
		{
			this.fields[field] = EMPTY_FIELD;
		}
		else
		{
			this.fields[field] = result.toArray();
		}
	}

	protected void addObject(ArrayList<Object> result, Object o)
	{
		if (o instanceof Character)
		{
			rtAddString(result, String.valueOf((Character) o));
		}
		else if (o instanceof String)
		{
			rtAddString(result, o.toString());
		}
		else
		{
			addNodeOrObject(result, o);
		}
	}

	protected void addNodeOrObject(ArrayList<Object> result, Object o)
	{
		if (o instanceof AstNode)
		{
			AstNode<?> node = (AstNode<?>) o;
			if (node.getNodeType() == AstNode.NT_NODE_LIST)
			{
				for (AstNode<?> c : (AstNodeList<?>) o)
					addNodeOrObject(result, c);
			}
			else if (node.getNodeType() == AstNode.NT_TEXT)
			{
				rtAddString(result, ((AstText<?>) o).getContent());
			}
			else
			{
				result.add(o);
			}
		}
		else
		{
			result.add(o);
		}
	}

	protected static void rtAddString(ArrayList<Object> result, String text)
	{
		if (text.isEmpty())
			return;

		int i = result.size() - 1;
		if (i >= 0)
		{
			Object last = result.get(i);
			if (last instanceof String)
			{
				result.set(i, (String) last + text);
				return;
			}
		}

		result.add(text);
	}

	private void setFieldFromArraySection(
			int field,
			String[] glue,
			int from,
			int to)
	{
		if (from < 0 || to > glue.length)
			throw new IndexOutOfBoundsException();

		if (to <= from)
			return;

		StringBuilder sb = new StringBuilder();
		for (int i1 = from; i1 < to; ++i1)
		{
			String g = glue[i1];
			if (g == null)
				continue;
			sb.append(g);
		}

		setField(field, sb.toString());
	}

	// =========================================================================

	public boolean isSuppress()
	{
		return this == SUPPRESS;
	}

	public int size()
	{
		return this.fields.length;
	}

	public Object[][] getFields()
	{
		return fields;
	}

	/**
	 * Returns the glue field that can be found between two child nodes or in
	 * front of the first or after the last child node.
	 */
	public Object[] getField(int index)
	{
		return fields[index];
	}

	/**
	 * Converts a glue field to a string.
	 */
	public String toString(int index)
	{
		Object[] field = fields[index];
		if (field.length == 0)
		{
			return "";
		}
		else if (field.length == 1 && field[0] instanceof String)
		{
			return (String) field[0];
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			toString(index, sb);
			return sb.toString();
		}
	}

	public boolean isStringOnly(int index)
	{
		Object[] field = fields[index];
		return (field.length == 0)
				|| (field.length == 1 && field[0] instanceof String);
	}

	protected void toString(int index, StringBuilder sb)
	{
		Object[] field = fields[index];
		for (int i = 0; i < field.length; ++i)
		{
			if (i != 0)
				sb.append(" + ");

			Object o = field[i];
			if (o instanceof String)
			{
				stringRep(sb, (String) o);
			}
			else
			{
				stringRep(sb, o);
			}
		}
	}

	protected void stringRep(StringBuilder sb, Object o)
	{
		sb.append(o.toString());
	}

	protected void stringRep(StringBuilder sb, String str)
	{
		sb.append('"');
		sb.append(StringTools.escJava(str));
		sb.append('"');
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("RTD[ ");
		for (int i = 0; i < fields.length; ++i)
		{
			if (i > 0)
				sb.append(" <o> ");

			if (getField(i).length == 0)
			{
				sb.append("\"\"");
			}
			else
			{
				toString(i, sb);
			}
		}
		sb.append(" ]");
		return sb.toString();
	}

	// =========================================================================

	public void prepend(Object... glue)
	{
		if (glue.length == 0)
			return;
		setField(0, glue, getField(0));
	}

	public void append(Object... glue)
	{
		if (glue.length == 0)
			return;
		int last = size() - 1;
		setField(last, getField(last), glue);
	}

	public void prepend(String text)
	{
		if (text == null || text.isEmpty())
			return;

		Object[] firstField = fields[0];
		if (firstField.length == 0)
		{
			setField(0, text);
		}
		else
		{
			Object firstGlue = firstField[0];
			if (firstGlue instanceof String)
			{
				firstField[0] = text + firstGlue;
			}
			else
			{
				Object[] newField = new Object[firstField.length + 1];
				System.arraycopy(firstField, 0, newField, 1, firstField.length);
				newField[0] = text;
				fields[0] = newField;
			}
		}
	}

	// =========================================================================

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fields);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RtData other = (RtData) obj;
		if (!Arrays.deepEquals(fields, other.fields))
			return false;
		return true;
	}

	// =========================================================================

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return new RtData(this);
	}

	public RtData cloneWrapException()
	{
		try
		{
			return (RtData) this.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new WrappedException(e);
		}
	}

	// =========================================================================

	protected static class RtDataSuppressSingleton
			extends
				RtData
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void set(Object... glue)
		{
			notSupported();
		}

		@Override
		public void set(String... glue)
		{
			notSupported();
		}

		@Override
		public void setField(int field, Object... glue)
		{
			notSupported();
		}

		@Override
		public void setField(int field, String glue)
		{
			notSupported();
		}

		@Override
		public void setField(int field, String... glue)
		{
			notSupported();
		}

		@Override
		public void prepend(String text)
		{
			notSupported();
		}

		@Override
		public void append(Object... glue)
		{
			notSupported();
		}

		@Override
		public void prepend(Object... glue)
		{
			notSupported();
		}

		private void notSupported()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			return false;
		}

		@Override
		public Object clone() throws CloneNotSupportedException
		{
			throw new CloneNotSupportedException();
		}
	}
}
