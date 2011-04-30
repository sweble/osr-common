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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.ContentNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.StringContentNode;
import de.fau.cs.osr.utils.StringUtils;

public class AstPrinter
        extends
            Visitor
{
	protected PrintWriter out;
	
	private String indentStr = new String();
	
	private boolean legacyIndentation;
	
	// =========================================================================
	
	public AstPrinter(Writer writer)
	{
		this.out = new PrintWriter(writer);
		this.legacyIndentation = false;
	}
	
	@Override
	protected Object after(AstNode node, Object result)
	{
		this.out.close();
		return super.after(node, result);
	}
	
	public void setLegacyIndentation(boolean legacyIndentation)
	{
		this.legacyIndentation = legacyIndentation;
	}
	
	public boolean isLegacyIndentation()
	{
		return legacyIndentation;
	}
	
	// =========================================================================
	
	public static String print(AstNode node)
	{
		StringWriter writer = new StringWriter();
		new AstPrinter(writer).go(node);
		return writer.toString();
	}
	
	public static Writer print(Writer writer, AstNode node)
	{
		new AstPrinter(writer).go(node);
		return writer;
	}
	
	// =========================================================================
	
	public void visit(AstNode n)
	{
		if (!replay(n))
		{
			Memoize m = memoizeStart(n);
			
			if (n.isEmpty() && !n.hasAttributes() && !n.hasProperties())
			{
				indent();
				out.println(n.getClass().getSimpleName() + "()");
			}
			else
			{
				indent();
				out.println(n.getClass().getSimpleName() + "(");
				
				incIndent();
				printNodeContent(n);
				decIndent();
				
				indent();
				out.println(")");
			}
			
			memoizeStop(m);
		}
	}
	
	public void visit(NodeList n)
	{
		if (!replay(n))
		{
			Memoize m = memoizeStart(n);
			
			if (n.hasAttributes() || n.hasProperties())
			{
				visit((AstNode) n);
			}
			else if (n.isEmpty())
			{
				indent();
				out.println("[ ]");
			}
			else
			{
				incIndent();
				String singleLine = printNodeContentToString(n);
				decIndent();
				
				if (singleLine != null)
				{
					indent();
					out.println("[ " + singleLine + " ]");
				}
				else
				{
					indent();
					out.println("[");
					
					incIndent();
					printNodeContent(n);
					decIndent();
					
					indent();
					out.println("]");
				}
			}
			
			memoizeStop(m);
		}
	}
	
	public void visit(ContentNode n)
	{
		if (!replay(n))
		{
			Memoize m = memoizeStart(n);
			
			if (n.hasAttributes() || n.hasProperties())
			{
				visit((AstNode) n);
			}
			else if (n.getContent().isEmpty())
			{
				indent();
				out.println(n.getClass().getSimpleName() + "([ ])");
			}
			else
			{
				incIndent();
				String singleLine = printNodeContentToString(n.getContent());
				decIndent();
				
				if (singleLine != null)
				{
					indent();
					out.println(n.getClass().getSimpleName() + "([ " + singleLine + " ])");
				}
				else
				{
					indent();
					out.println(n.getClass().getSimpleName() + "([");
					
					incIndent();
					printNodeContent(n.getContent());
					decIndent();
					
					indent();
					out.println("])");
				}
			}
			
			memoizeStop(m);
		}
	}
	
	public void visit(StringContentNode n)
	{
		if (!replay(n))
		{
			Memoize m = memoizeStart(n);
			
			if (n.hasAttributes()/* || n.hasProperties()*/)
			{
				visit((AstNode) n);
			}
			else
			{
				indent();
				out.println(n.getClass().getSimpleName() + "(" + mkStr(n.getContent()) + ")");
			}
			
			memoizeStop(m);
		}
	}
	
	// =========================================================================
	
	protected static final class Memoize
	{
		private final AstNode node;
		
		private final int indent;
		
		private final PrintWriter oldOut;
		
		private final StringWriter writer;
		
		public Memoize(int indent, AstNode node)
		{
			this.indent = indent;
			this.node = node;
			this.oldOut = null;
			this.writer = null;
		}
		
		public Memoize(int indent, AstNode node, PrintWriter oldOut, StringWriter writer)
		{
			this.indent = indent;
			this.node = node;
			this.oldOut = oldOut;
			this.writer = writer;
		}
		
		public PrintWriter getOldOut()
		{
			return oldOut;
		}
		
		public String getText()
		{
			return writer.toString();
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + indent;
			result = prime * result + System.identityHashCode(node);
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
			Memoize other = (Memoize) obj;
			if (indent != other.indent)
				return false;
			if (node != other.node)
				return false;
			return true;
		}
	}
	
	private final HashMap<Memoize, Memoize> cache = new HashMap<Memoize, Memoize>();
	
	protected boolean replay(AstNode n)
	{
		Memoize m = cache.get(new Memoize(indentStr.length(), n));
		if (m == null)
			return false;
		
		play(m);
		return true;
	}
	
	protected Memoize memoizeStart(AstNode n)
	{
		StringWriter w = new StringWriter();
		Memoize m = new Memoize(indentStr.length(), n, out, w);
		
		out = new PrintWriter(w);
		
		return m;
	}
	
	protected void memoizeStop(Memoize m)
	{
		out = m.getOldOut();
		cache.put(m, m);
		play(m);
	}
	
	private void play(Memoize m)
	{
		out.write(m.getText());
	}
	
	// =========================================================================
	
	protected void printNodeContent(AstNode n)
	{
		Map<String, Object> attrs = n.getAttributes();
		
		Map<String, Object> props = new HashMap<String, Object>();
		props.putAll(attrs);
		
		AstNodePropertyIterator i = n.propertyIterator();
		while (i.next())
			props.put(i.getName(), i.getValue());
		
		if (!props.isEmpty())
		{
			indent();
			out.println("Properties:");
		}
		
		List<String> keys = new ArrayList<String>(props.keySet());
		Collections.sort(keys);
		
		incIndent();
		for (String name : keys)
		{
			Object value = props.get(name);
			
			if (attrs.containsKey(name))
			{
				indent();
				if (!legacyIndentation)
					out.print("    ");
				out.print(name + " = ");
			}
			else
			{
				indent();
				out.print("{N} " + name + " = ");
			}
			
			if (value instanceof String)
			{
				out.println(mkStr((String) value));
			}
			else if (value instanceof AstNode)
			{
				out.println();
				incIndent();
				dispatch((AstNode) value);
				decIndent();
			}
			else if (value instanceof Collection)
			{
				Collection<?> c = (Collection<?>) value;
				if (c.isEmpty())
				{
					out.println("[]");
				}
				else
				{
					out.println();
					indent();
					out.println('[');
					incIndent();
					{
						Iterator<?> k = c.iterator();
						int last = c.size() - 1;
						for (int j = 0; k.hasNext(); ++j)
						{
							Object o = k.next();
							indent();
							String s = "null";
							if (o != null)
								s = o.toString();
							out.println((j != last) ? s + ',' : s);
						}
					}
					decIndent();
					indent();
					out.println(']');
				}
			}
			else
			{
				out.println(value);
			}
		}
		decIndent();
		
		if (!props.isEmpty() && !n.isEmpty())
			out.println();
		
		for (AstNode c : n)
			dispatch(c);
	}
	
	protected String printNodeContentToString(AstNode n)
	{
		String singleLine = null;
		
		if (n.size() == 1)
		{
			PrintWriter oldOut = out;
			StringWriter w = new StringWriter();
			out = new PrintWriter(w);
			
			printNodeContent(n);
			
			out = oldOut;
			singleLine = w.toString().trim();
			if (singleLine.indexOf('\n') != -1 ||
			        singleLine.indexOf('\r') != -1)
				singleLine = null;
		}
		
		return singleLine;
	}
	
	protected void incIndent()
	{
		this.indentStr += "  ";
	}
	
	protected void decIndent()
	{
		this.indentStr =
		        this.indentStr.substring(0, this.indentStr.length() - 2);
	}
	
	protected void indent()
	{
		out.print(this.indentStr);
	}
	
	private String mkStr(String str)
	{
		if (str == null)
			return "null";
		else
			return '"' + StringUtils.escJava(str) + '"';
	}
}
