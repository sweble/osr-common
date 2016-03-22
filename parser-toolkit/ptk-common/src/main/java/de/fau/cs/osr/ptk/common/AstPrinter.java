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

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.fau.cs.osr.ptk.common.ast.AstLeafNode;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodeList;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.AstStringNode;
import de.fau.cs.osr.ptk.common.ast.AstText;
import de.fau.cs.osr.utils.PrinterBase;
import de.fau.cs.osr.utils.PrinterBase.Memoize;
import de.fau.cs.osr.utils.PrinterBase.OutputBuffer;
import de.fau.cs.osr.utils.StringUtils;

public class AstPrinter<T extends AstNode<T>>
		extends
			AstVisitor<T>
{
	public void visit(AstNode<T> n)
	{
		Memoize m = p.memoizeStart(n);
		if (m != null)
		{
			if (n.isEmpty() && !hasVisibleProperties(n))
			{
				p.indent(n.getNodeName());
				p.println("()");
			}
			else
			{
				printNode(n);
			}
			p.memoizeStop(m);
		}
	}
	
	public void visit(AstLeafNode<T> n)
	{
		if (n.isEmpty() && !hasVisibleProperties(n))
		{
			p.indent(n.getNodeName());
			p.println("()");
		}
		else
		{
			printNode(n);
		}
	}
	
	public void visit(AstText<T> n)
	{
		if (!hasVisibleProperties(n))
		{
			p.indent('"');
			p.print(StringUtils.escJava(n.getContent()));
			p.println('"');
		}
		else
		{
			printNode(n);
		}
	}
	
	public void visit(AstStringNode<T> n)
	{
		if (!hasVisibleProperties(n))
		{
			p.indent(n.getNodeName());
			p.print("(\"");
			p.print(StringUtils.escJava(n.getContent()));
			p.println("\")");
		}
		else
		{
			printNode(n);
		}
	}
	
	public void visit(AstNodeList<T> n)
	{
		Memoize m = p.memoizeStart(n);
		if (m != null)
		{
			String name = "";
			if (n.getNodeType() != AstNode.NT_NODE_LIST)
				name = n.getNodeName();
			
			if (hasVisibleProperties(n))
			{
				printNode(n);
			}
			else if (n.isEmpty())
			{
				p.indent(name);
				p.println("[]");
			}
			else
			{
				boolean singleLine = false;
				if (isCompact() && n.size() <= 1)
				{
					OutputBuffer b = p.outputBufferStart();
					printListOfNodes(n);
					b.stop();
					
					String output = b.getBuffer().trim();
					if (isSingleLine(output))
					{
						p.indent(name);
						p.print("[ ");
						p.print(output);
						p.println(" ]");
						singleLine = true;
					}
				}
				
				if (!singleLine)
				{
					p.indent(name);
					p.println('[');
					
					p.incIndent();
					printListOfNodes(n);
					p.decIndent();
					
					p.indentln(']');
				}
			}
			p.memoizeStop(m);
		}
	}
	
	// =========================================================================
	
	protected boolean hasVisibleProperties(AstNode<T> n)
	{
		if (n.hasAttributes())
		{
			return true;
		}
		else
		{
			// Shortcuts
			int count = n.getPropertyCount();
			if (count > 2)
				return true;
			if (count == 0)
				return false;
			
			AstNodePropertyIterator i = n.propertyIterator();
			while (i.next())
			{
				if (i.getName().equals("rtd"))
				{
					if (i.getValue() != null)
						return true;
				}
				else if (!i.getName().equals("content") || !(n instanceof AstStringNode))
				{
					return true;
				}
			}
			
			return false;
		}
	}
	
	protected void printNode(AstNode<T> n)
	{
		p.indent(n.getNodeName());
		p.println('(');
		
		p.incIndent();
		printNodeContent(n);
		p.decIndent();
		
		p.indentln(')');
	}
	
	protected void printNodeContent(AstNode<T> n)
	{
		if (hasVisibleProperties(n))
			printProperties(n);
		
		printListOfNodes(n);
	}
	
	protected void printProperties(AstNode<T> n)
	{
		Map<String, Object> props = new TreeMap<String, Object>();
		
		for (Entry<String, Object> entry : n.getAttributes().entrySet())
			props.put("{A} " + entry.getKey(), entry.getValue());
		
		AstNodePropertyIterator i = n.propertyIterator();
		while (i.next())
		{
			if (i.getValue() != null || !i.getName().equals("rtd"))
				props.put("{P} " + i.getName(), i.getValue());
		}
		
		for (Entry<String, Object> entry : props.entrySet())
		{
			p.indent(entry.getKey());
			p.print(" = ");
			p.eatNewlinesAndIndents(1);
			printPropertyValue(entry.getValue());
			p.clearEatNewlinesAndIndents();
		}
	}
	
	protected void printPropertyValue(Object value)
	{
		if (value == null)
		{
			p.indentln("null");
		}
		else if (value instanceof String)
		{
			p.indent('"');
			p.print(StringUtils.escJava((String) value));
			p.println('"');
		}
		else if (value instanceof AstNode)
		{
			p.incIndent();
			@SuppressWarnings("unchecked")
			T node = (T) value;
			dispatch(node);
			p.decIndent();
		}
		else if (value instanceof AstEntityMap)
		{
			@SuppressWarnings("unchecked")
			AstEntityMap<T> map = (AstEntityMap<T>) value;
			printEntityMap(map);
		}
		else if (value instanceof Collection)
		{
			printCollection((Collection<?>) value);
		}
		else
		{
			p.indentln(value.toString());
		}
	}
	
	protected void printEntityMap(AstEntityMap<T> entityMap)
	{
		if (entityMap.getMap().isEmpty())
		{
			p.indentln("-");
		}
		else
		{
			Map<Integer, T> map = new TreeMap<Integer, T>(entityMap.getMap());
			
			p.indentln("{");
			
			p.incIndent();
			for (Iterator<Entry<Integer, T>> k = map.entrySet().iterator(); k.hasNext();)
			{
				Entry<Integer, T> entry = k.next();
				p.indent('[');
				p.print(entry.getKey().toString());
				p.print("] = ");
				p.eatNewlinesAndIndents(1);
				printPropertyValue(entry.getValue());
				p.clearEatNewlinesAndIndents();
				p.ignoreNewlines();
				p.println(k.hasNext() ? "," : "");
			}
			p.decIndent();
			
			p.indentln('}');
		}
	}
	
	protected void printCollection(Collection<?> c)
	{
		if (c.isEmpty())
		{
			p.indentln("C[]");
		}
		else
		{
			boolean singleLine = false;
			if (isCompact() && c.size() == 1)
			{
				OutputBuffer b = p.outputBufferStart();
				printPropertyValue(c.toArray()[0]);
				b.stop();
				
				String output = b.getBuffer().trim();
				if (isSingleLine(output))
				{
					p.indent("C[ ");
					p.print(output);
					p.println(" ]");
					singleLine = true;
				}
			}
			
			if (!singleLine)
			{
				p.indentln("C[");
				
				p.incIndent();
				for (Iterator<?> k = c.iterator(); k.hasNext();)
				{
					printPropertyValue(k.next());
					p.ignoreNewlines();
					p.println(k.hasNext() ? "," : "");
				}
				p.decIndent();
				
				p.indentln(']');
			}
		}
	}
	
	protected void printListOfNodes(AstNode<T> n)
	{
		int j = 0;
		String[] childNames = n.getChildNames();
		for (Iterator<T> i = n.iterator(); i.hasNext();)
		{
			if (!n.isList())
			{
				p.indent(childNames[j++]);
			}
			else
			{
				p.indent('[');
				p.print(String.valueOf(j++));
				p.print(']');
			}
			p.print(" = ");
			p.eatNewlinesAndIndents(1);
			dispatch(i.next());
			p.clearEatNewlinesAndIndents();
			p.ignoreNewlines();
			p.println(i.hasNext() ? "," : "");
		}
	}
	
	// =========================================================================
	
	protected boolean isSingleLine(String text)
	{
		return (text.indexOf('\n') == -1) && (text.indexOf('\r') == -1);
	}
	
	// =========================================================================
	
	public static <T extends AstNode<T>> String print(T node)
	{
		return print(new StringWriter(), node).toString();
	}
	
	public static <T extends AstNode<T>> Writer print(Writer writer, T node)
	{
		new AstPrinter<T>(writer).go(node);
		return writer;
	}
	
	// =========================================================================
	
	protected final PrinterBase p;
	
	public AstPrinter(Writer writer)
	{
		this.p = new PrinterBase(writer);
		this.p.setMemoize(true);
		this.setCompact(true);
	}
	
	@Override
	protected Object after(T node, Object result)
	{
		p.flush();
		return result;
	}
	
	// =========================================================================
	
	private boolean compact = true;
	
	public void setCompact(boolean compact)
	{
		this.compact = compact;
	}
	
	public boolean isCompact()
	{
		return compact;
	}
}
