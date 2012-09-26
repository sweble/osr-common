package de.fau.cs.osr.ptk.common.ast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import de.fau.cs.osr.utils.StringUtils;

public class RtData
		implements
			Serializable
{
	public static final String SEP = new String();
	
	// =========================================================================
	
	private static final long serialVersionUID = 1L;
	
	private static final Object[] EMPTY_FIELD = {};
	
	private final Object[][] fields;
	
	// =========================================================================
	
	/**
	 * Instantiates an RtData object and initializes its size to match that of
	 * the given AstNode.
	 * 
	 * An AstNode with two children will need an RtData object with size three.
	 */
	public RtData(AstNode node)
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
	 * given AstNode and immediately fills it with glue information.
	 */
	public RtData(AstNode node, Object... glue)
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
	 * given AstNode and immediately fills it with glue information.
	 */
	public RtData(AstNode node, String... glue)
	{
		this(node);
		set(glue);
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
					++to;
					break;
				}
				++to;
			}
			
			setFieldFromArraySection(field, glue, from, to);
			
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
		if (to <= from)
			return;
		
		ArrayList<Object> result = new ArrayList<Object>(glue.length);
		for (int i = from; i < to; ++i)
		{
			Object o = glue[i];
			if (o instanceof AstNode)
			{
				AstNode n = (AstNode) o;
				switch (n.getNodeType())
				{
					case AstNode.NT_NODE_LIST:
						for (AstNode c : (NodeList) n)
						{
							if (c.getNodeType() == AstNode.NT_TEXT)
							{
								rtAddString(result, ((Text) c).getContent());
							}
							else
							{
								result.add(c);
							}
						}
						break;
					
					case AstNode.NT_TEXT:
						rtAddString(result, ((Text) n).getContent());
						break;
					
					default:
						result.add(n);
						break;
				}
			}
			else
			{
				if (o == null)
				{
				}
				else if (o instanceof Character)
				{
					rtAddString(result, String.valueOf((Character) o));
				}
				else if (o instanceof String)
				{
					rtAddString(result, o.toString());
				}
				else
				{
					result.add(o);
				}
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
	
	private static void rtAddString(ArrayList<Object> result, String text)
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
		if (to <= from)
			return;
		
		StringBuilder sb = new StringBuilder();
		for (int i1 = from; i1 < to; ++i1)
			sb.append(glue[i1]);
		
		setField(field, sb.toString());
	}
	
	// =========================================================================
	
	public int size()
	{
		return this.fields.length;
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
	
	private void toString(int index, StringBuilder sb)
	{
		Object[] field = fields[index];
		for (Object o : field)
			sb.append(StringUtils.escJava(stringRep(o)));
	}
	
	protected String stringRep(Object o)
	{
		return o.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("RtData: [ ");
		for (int i = 0; i < fields.length; ++i)
		{
			if (i > 0)
				sb.append(" O ");
			sb.append('"');
			toString(i, sb);
			sb.append('"');
		}
		sb.append(" ]");
		return sb.toString();
	}
	
	// =========================================================================
	
	public void prepend(String text)
	{
		if (text.isEmpty())
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
	
}
