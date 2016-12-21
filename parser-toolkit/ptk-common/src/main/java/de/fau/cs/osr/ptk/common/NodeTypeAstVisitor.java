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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodeList;
import de.fau.cs.osr.ptk.common.ast.AstParserEntity;
import de.fau.cs.osr.ptk.common.ast.AstText;
import de.fau.cs.osr.utils.visitor.VisitingException;

public class NodeTypeAstVisitor<T extends AstNode<T>>
{
	public static final Object REMOVE = new Object();
	
	// =========================================================================
	
	public Object go(T node)
	{
		return dispatch(node);
	}
	
	// =========================================================================
	
	/**
	 * Dispatches to the appropriate visit() method and returns the result of
	 * the visitation. If the given node is <code>null</code> this method
	 * returns immediately with <code>null</code> as result.
	 */
	protected final Object dispatch(T node)
	{
		if (node == null)
			throw new NullPointerException();
		
		try
		{
			return resolveAndVisit(node, node.getNodeType());
		}
		catch (VisitingException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new VisitingException(node, e);
		}
	}
	
	protected final void iterate(T node)
	{
		if (node == null)
			throw new NullPointerException();
		for (T n : node)
			dispatch(n);
	}
	
	protected final List<Object> map(T node)
	{
		if (node == null)
			throw new NullPointerException();
		
		List<Object> result = new ArrayList<Object>(node.size());
		for (T n : node)
			result.add(dispatch(n));
		return result;
	}
	
	/**
	 * Iterates over the children of an AST node and replaces each child node
	 * with the result of the visitation of the respective child. If the given
	 * AST node is a WtNodeList, the call will be passed to
	 * mapInPlace(WtNodeList) which has special semantics.
	 */
	protected final void mapInPlace(T node)
	{
		if (node == null)
			throw new NullPointerException();
		
		if (node.isList())
		{
			ListIterator<T> i = node.listIterator();
			while (i.hasNext())
			{
				T current = i.next();
				Object result = dispatch(current);
				if (result == null)
				{
					throw new NullPointerException();
				}
				else if (result == REMOVE)
				{
					i.remove();
				}
				else
				{
					if (result == current)
						continue;
					
					@SuppressWarnings("unchecked")
					T resultNode = (T) result;
					
					if (resultNode.getNodeType() == AstNode.NT_NODE_LIST)
					{
						i.remove();
						i.add(resultNode);
					}
					else
					{
						i.set(resultNode);
					}
				}
			}
		}
		else
		{
			ListIterator<T> i = node.listIterator();
			while (i.hasNext())
			{
				T current = i.next();
				
				@SuppressWarnings("unchecked")
				T result = (T) dispatch(current);
				
				if (result != current)
					i.set(result);
			}
		}
	}
	
	// =========================================================================
	
	protected Object resolveAndVisit(T node, int type) throws Exception
	{
		switch (type)
		{
			case T.NT_TEXT:
				return visit((AstText<T>) node);
				
			case T.NT_NODE_LIST:
				return visit((AstNodeList<T>) node);
				
			case T.NT_PARSER_ENTITY:
				return visit((AstParserEntity<T>) node);
				
			default:
				return visitUnspecific(node);
		}
	}
	
	// =========================================================================
	
	protected Object visitUnspecific(AstNode<T> node) throws Exception
	{
		return node;
	}
	
	protected Object visit(AstText<T> node) throws Exception
	{
		return visitUnspecific(node);
	}
	
	protected Object visit(AstNodeList<T> node) throws Exception
	{
		return visitUnspecific(node);
	}
	
	protected Object visit(AstParserEntity<T> node) throws Exception
	{
		return visitUnspecific(node);
	}
}
