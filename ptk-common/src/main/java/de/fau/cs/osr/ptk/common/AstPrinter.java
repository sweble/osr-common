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

import de.fau.cs.osr.ptk.common.ast.AstLeafNodeImpl;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodeListImpl;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.AstStringNodeImpl;
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
	
	public void visit(AstLeafNodeImpl<T> n)
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
	
	public void visit(AstStringNodeImpl<T> n)
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
	
	public void visit(AstNodeListImpl<T> n)
	{
		Memoize m = p.memoizeStart(n);
		if (m != null)
		{
			if (hasVisibleProperties(n))
			{
				printNode(n);
			}
			else if (n.isEmpty())
			{
				p.indentln("[ ]");
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
						p.indent("[ ");
						p.print(output);
						p.println(" ]");
						singleLine = true;
					}
				}
				
				if (!singleLine)
				{
					p.indentln("[");
					
					p.incIndent();
					printListOfNodes(n);
					p.decIndent();
					
					p.indentln(']');
				}
			}
			p.memoizeStop(m);
		}
	}
	
	/* FIXME: Remove
	public void visit(GenericContentNode<T, AstNodeListImpl<T>> n)
	{
		Memoize m = p.memoizeStart(n);
		if (m != null)
		{
			if (hasVisibleProperties(n))
			{
				printNode(n);
			}
			else if (n.isEmpty())
			{
				p.indent(n.getNodeName());
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
						p.indent(n.getNodeName());
						p.print('(');
						p.print(output);
						p.println(')');
						singleLine = true;
					}
				}
				
				if (!singleLine)
				{
					p.indent(n.getNodeName());
					p.println("([");
					
					p.incIndent();
					printListOfNodes(n.getContent());
					p.decIndent();
					
					p.indentln("])");
				}
			}
			p.memoizeStop(m);
		}
	}
	*/
	
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
				else if (!i.getName().equals("content") || !(n instanceof AstStringNodeImpl))
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
		{
			printProperties(n);
			if (!n.isEmpty())
				p.needNewlines(2);
		}
		
		printListOfNodes(n);
	}
	
	protected void printProperties(AstNode<T> n)
	{
		Map<String, Object> props = new TreeMap<String, Object>();
		
		for (Entry<String, Object> entry : n.getAttributes().entrySet())
			props.put(entry.getKey(), entry.getValue());
		
		AstNodePropertyIterator i = n.propertyIterator();
		while (i.next())
		{
			if (i.getValue() != null || !i.getName().equals("rtd"))
				props.put(i.getName(), i.getValue());
		}
		
		p.indentln("Properties:");
		
		p.incIndent();
		for (Entry<String, Object> entry : props.entrySet())
		{
			p.indent(n.hasAttribute(entry.getKey()) ? "" : "{N} ");
			p.print(entry.getKey());
			p.print(" = ");
			printPropertyValue(entry.getValue());
		}
		p.decIndent();
	}
	
	protected void printPropertyValue(Object value)
	{
		if (value == null)
		{
			p.println("null");
		}
		else if (value instanceof String)
		{
			p.print('"');
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
		/*
		else if (value instanceof GenericEntityMap)
		{
			@SuppressWarnings("unchecked")
			GenericEntityMap<T> map = (GenericEntityMap<T>) value;
			printEntityMap(map);
		}
		*/
		else if (value instanceof Collection)
		{
			printCollection((Collection<?>) value);
		}
		else
		{
			p.println(value);
		}
	}
	
	/*
	private void printEntityMap(GenericEntityMap<T> entityMap)
	{
		if (entityMap.getMap().isEmpty())
		{
			p.println("[]");
		}
		else
		{
			Map<Integer, T> map = new TreeMap<Integer, T>(entityMap.getMap());
			
			p.println("[");
			
			p.incIndent();
			for (Iterator<Entry<Integer, T>> k = map.entrySet().iterator(); k.hasNext();)
			{
				p.indent();
				Entry<Integer, T> entry = k.next();
				p.print(entry.getKey().toString());
				p.print(" = ");
				printPropertyValue(entry.getValue());
				p.ignoreNewlines();
				p.println(k.hasNext() ? "," : "");
			}
			p.decIndent();
			
			p.indentln(']');
		}
	}
	*/
	
	private void printCollection(Collection<?> c)
	{
		if (c.isEmpty())
		{
			p.println("[]");
		}
		else
		{
			// TODO: Also include single line case!
			
			p.indentln("[");
			
			p.incIndent();
			for (Iterator<?> k = c.iterator(); k.hasNext();)
			{
				p.indent();
				printPropertyValue(k.next());
				p.ignoreNewlines();
				p.println(k.hasNext() ? "," : "");
			}
			p.decIndent();
			
			p.indentln(']');
		}
	}
	
	protected void printListOfNodes(AstNode<T> n)
	{
		for (Iterator<T> i = n.iterator(); i.hasNext();)
			dispatch(i.next());
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
