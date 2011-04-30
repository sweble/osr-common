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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;

public class Visitor
{
	private static final int LOWER_CAPACITY = 256;
	
	private static final int UPPER_CAPACITY = 384;
	
	private static final float LOAD_FACTOR = .6f;
	
	private static final ConcurrentHashMap<Target, Target> cache =
	        new ConcurrentHashMap<Target, Target>(
	                LOWER_CAPACITY,
	                LOAD_FACTOR);
	
	// =========================================================================
	
	/**
	 * Start visitation at the given node.
	 * 
	 * @param node
	 *            The node at which the visitation will start.
	 * @return The result of the visitation. If the visit() method for the given
	 *         node doesn't return a value, <code>null</code> is returned.
	 */
	public final Object go(AstNode node)
	{
		if (!before(node))
			return null;
		
		Object result = dispatch(node);
		return after(node, result);
	}
	
	// =========================================================================
	
	/**
	 * Dispatches to the appropriate visit() method and returns the result of
	 * the visitation. If the given node is <code>null</code> this method
	 * returns immediately with <code>null</code> as result.
	 */
	protected final Object dispatch(AstNode node)
	{
		if (node == null)
			return null;
		return visit(node);
	}
	
	protected final void iterate(AstNode node)
	{
		if (node != null)
		{
			for (AstNode n : node)
				dispatch(n);
		}
	}
	
	protected final List<Object> map(AstNode node)
	{
		if (node == null)
			return Collections.emptyList();
		
		List<Object> result = new ArrayList<Object>(node.size());
		for (AstNode n : node)
			result.add(dispatch(n));
		return result;
	}
	
	/**
	 * Iterates over the children of an AST node and replaces each child node
	 * with the result of the visitation of the respective child. If the given
	 * AST node is a NodeList, the call will be passed to mapInPlace(NodeList)
	 * which has special semantics.
	 */
	protected final void mapInPlace(AstNode node)
	{
		if (node == null)
		{
			return;
		}
		else if (node.getNodeType() == AstNode.NT_NODE_LIST)
		{
			mapInPlace((NodeList) node);
		}
		else
		{
			ListIterator<AstNode> i = node.listIterator();
			while (i.hasNext())
			{
				AstNode current = i.next();
				AstNode result = (AstNode) dispatch(current);
				if (result != current)
					i.set(result);
			}
		}
	}
	
	/**
	 * Iterates over a NodeList and replaces each node with the result of the
	 * visitation. If a visit() call returns <code>null</code>, the node that
	 * was passed to the visit() method will be removed from the list. If the
	 * visit() call returns another NodeList as result, the result's children
	 * will replace the node that was being visited. The visitation will
	 * continue after the embedded children. Otherwise, each visited node will
	 * simply be replaced by the result of the visit() call.
	 */
	protected final void mapInPlace(NodeList list)
	{
		if (list == null)
			return;
		
		ListIterator<AstNode> i = list.listIterator();
		while (i.hasNext())
		{
			AstNode current = i.next();
			
			AstNode result = (AstNode) dispatch(current);
			if (result == current)
				continue;
			
			if (result == null)
			{
				i.remove();
			}
			else if (result.getNodeType() == AstNode.NT_NODE_LIST)
			{
				i.remove();
				i.add(result);
			}
			else
			{
				i.set(result);
			}
		}
	}
	
	// =========================================================================
	
	/**
	 * Called before the visitation starts.
	 * 
	 * @param node
	 *            The node at which the visitation will start.
	 * @return Always returns <code>true</code>. If an overridden version of
	 *         this method returns <code>false</code> the visitation will be
	 *         aborted.
	 */
	protected boolean before(AstNode node)
	{
		return true;
	}
	
	/**
	 * Called after the visitation has finished. This method will not be called
	 * if before() returned false.
	 * 
	 * @param node
	 *            The node at which the visitation started.
	 * @param result
	 *            The result of the visitation. If the visit() method for the
	 *            given node doesn't return a value, <code>null</code> is
	 *            returned.
	 * @return Returns the result parameter.
	 */
	protected Object after(AstNode node, Object result)
	{
		return result;
	}
	
	/**
	 * This method is called if no suitable visit() method could be found. If
	 * not overridden, this method will throw an UnvisitableException.
	 * 
	 * @param node
	 *            The node that should have been visited.
	 * @return The result of the visitation.
	 */
	protected Object visitNotFound(AstNode node)
	{
		throw new VisitNotFoundException(this, node);
	}
	
	// =========================================================================
	
	private final Object visit(AstNode node)
	{
		Class<? extends Visitor> vClass = this.getClass();
		Class<? extends AstNode> nClass = node.getClass();
		
		Target key = new Target(vClass, nClass);
		Target cached = cache.get(key);
		try
		{
			if (cached == null && !cache.contains(key))
				cached = findVisit(key);
			
			if (cached.getMethod() == null)
				return visitNotFound(node);
			
			return cached.invoke(this, node);
		}
		catch (InvocationTargetException e)
		{
			Throwable cause = e.getCause();
			if (cause instanceof VisitingException)
				throw (VisitingException) cause;
			throw new VisitingException(node, cause);
		}
		catch (VisitingException e)
		{
			throw e;
		}
		catch (VisitNotFoundException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new VisitorException(node, e);
		}
	}
	
	private final Target findVisit(Target key) throws SecurityException
	{
		Method method = null;
		
		Class<?> vClass = key.getVClass();
		Class<?> nClass = key.getNClass();
		do
		{
			try
			{
				method = vClass.getMethod("visit", nClass);
				break;
			}
			catch (NoSuchMethodException e)
			{
				// Try to find visit() method for a superclass of node class
				nClass = nClass.getSuperclass();
			}
		} while (nClass != null);
		
		Target target = new Target(key, method);
		Target cached = cache.putIfAbsent(target, target);
		if (cached != null)
		{
			return cached;
		}
		else
		{
			// Make sure the target is not swept from the cache ...
			target.touch();
			if (cache.size() > UPPER_CAPACITY)
				sweepCache();
			
			return target;
		}
	}
	
	private final synchronized void sweepCache()
	{
		if (cache.size() <= UPPER_CAPACITY)
			return;
		
		Target keys[] = new Target[cache.size()];
		
		Enumeration<Target> keysEnum = cache.keys();
		
		int i = 0;
		while (i < keys.length && keysEnum.hasMoreElements())
			keys[i++] = keysEnum.nextElement();
		
		int length = i;
		Arrays.sort(keys, 0, length);
		
		int to = length - LOWER_CAPACITY;
		for (int j = 0; j < to; ++j)
			cache.remove(keys[j]);
	}
	
	// =========================================================================
	
	private static final class Target
	        implements
	            Comparable<Target>
	{
		private static int useCounter = 0;
		
		private int lastUse = -1;
		
		private final Class<? extends Visitor> vClass;
		
		private final Class<? extends AstNode> nClass;
		
		private final Method method;
		
		public Target(Class<? extends Visitor> vClass, Class<? extends AstNode> nClass)
		{
			this.vClass = vClass;
			this.nClass = nClass;
			this.method = null;
		}
		
		public Target(Target key, Method method)
		{
			this.vClass = key.vClass;
			this.nClass = key.nClass;
			this.method = method;
		}
		
		public Class<? extends Visitor> getVClass()
		{
			return vClass;
		}
		
		public Class<? extends AstNode> getNClass()
		{
			return nClass;
		}
		
		public Method getMethod()
		{
			return method;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + nClass.hashCode();
			result = prime * result + vClass.hashCode();
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			Target other = (Target) obj;
			if (nClass != other.nClass)
				return false;
			if (vClass != other.vClass)
				return false;
			return true;
		}
		
		public void touch()
		{
			lastUse = ++useCounter;
		}
		
		public Object invoke(Visitor visitor, AstNode node) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
		{
			touch();
			return method.invoke(visitor, node);
		}
		
		@Override
		public int compareTo(Target o)
		{
			// Equality is not possible!
			return (lastUse < o.lastUse) ? -1 : +1;
		}
		
		@Override
		public String toString()
		{
			return String.format(
			        "Target [%d - %s; %s:%s]",
			        lastUse,
			        method != null ? "O" : "X",
			        vClass.getSimpleName(),
			        nClass.getSimpleName());
		}
	}
}
