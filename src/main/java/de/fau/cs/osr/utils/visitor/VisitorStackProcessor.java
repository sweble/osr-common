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
package de.fau.cs.osr.utils.visitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public abstract class VisitorStackProcessor<T>
{
	private static final String VISIT_METHOD_NAME = "visit";
	
	private static final Map<String, Cache> CACHES = new HashMap<String, Cache>();
	
	// =========================================================================
	
	public static <S> Cache getOrRegisterCache(
			String name,
			List<? extends StackedVisitorInterface<S>> visitorStack)
	{
		return getOrRegisterCache(name, visitorStack, .6f, 256, 384);
	}
	
	public static synchronized <S> Cache getOrRegisterCache(
			String name,
			List<? extends StackedVisitorInterface<S>> visitorStack,
			float loadFactor,
			int lowerCapacity,
			int upperCapacity)
	{
		Cache cache = CACHES.get(name);
		if (cache == null)
		{
			cache = new Cache(visitorStack, loadFactor, lowerCapacity, upperCapacity);
			CACHES.put(name, cache);
		}
		else
		{
			cache.verifyDefinition(visitorStack);
		}
		return cache;
	}
	
	public static synchronized boolean dropCache(String name)
	{
		return (CACHES.remove(name) != null);
	}
	
	// =========================================================================
	
	private final Cache cache;
	
	private StackedVisitorInterface<T>[] visitorStack;
	
	private StackedVisitorInterface<T>[] enabledVisitors;
	
	// =========================================================================
	
	protected VisitorStackProcessor(
			String cacheName,
			List<? extends StackedVisitorInterface<T>> visitorStack)
	{
		this(getOrRegisterCache(cacheName, visitorStack), visitorStack);
	}
	
	protected VisitorStackProcessor(
			Cache cache,
			List<? extends StackedVisitorInterface<T>> visitorStack)
	{
		for (StackedVisitorInterface<T> visitor : visitorStack)
		{
			if (visitor == null)
				throw new NullPointerException("Visitor stack contains <null>s");
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		int tmp = new HashSet(visitorStack).size();
		if (tmp != visitorStack.size())
			throw new IllegalArgumentException("Visitor stack contains duplicates!");
		
		cache.verifyDefinition(visitorStack);
		
		@SuppressWarnings({ "unchecked" })
		StackedVisitorInterface<T>[] stackArray = new StackedVisitorInterface[visitorStack.size()];
		
		this.cache = cache;
		this.visitorStack = visitorStack.toArray(stackArray);
		this.enabledVisitors = Arrays.copyOf(this.visitorStack, this.visitorStack.length);
	}
	
	// =========================================================================
	
	public int indexOfVisitor(StackedVisitorInterface<T> visitor)
	{
		for (int i = 0; i < visitorStack.length; ++i)
		{
			if (visitorStack[i] == visitor)
				return i;
		}
		return -1;
	}
	
	public void setVisitor(int i, StackedVisitorInterface<T> visitor)
	{
		if (visitor == null)
			throw new NullPointerException();
		if (cache.cacheDef.visitorStackDef[i] != visitor.getClass())
			throw new IllegalArgumentException("Replacement visitor's class does not matched the replaced visitor's class");
		visitorStack[i] = visitor;
		if (isVisitorEnabled(i))
			enabledVisitors[i] = visitor;
	}
	
	public StackedVisitorInterface<T> getVisitor(int i)
	{
		return visitorStack[i];
	}
	
	public StackedVisitorInterface<T> getEnabledVisitor(int i)
	{
		return enabledVisitors[i];
	}
	
	public boolean isVisitorEnabled(int i)
	{
		return (getEnabledVisitor(i) != null);
	}
	
	public void disableVisitor(int i)
	{
		enabledVisitors[i] = null;
	}
	
	public void enableVisitor(int i)
	{
		enabledVisitors[i] = visitorStack[i];
	}
	
	public void setVisitorEnabled(int i, boolean enable)
	{
		if (enable)
			enableVisitor(i);
		else
			disableVisitor(i);
	}
	
	// =========================================================================
	
	/**
	 * Start visitation at the given node.
	 * 
	 * @param node
	 *            The node at which the visitation will start.
	 * @return The result of the visitation. If the visit() method for the given
	 *         node doesn't return a value, <code>null</code> is returned.
	 */
	public Object go(T node)
	{
		before(node);
		
		Object result = resolveAndVisit(node);
		
		return after(node, result);
	}
	
	protected T before(T node)
	{
		for (int i = 0; i < visitorStack.length; ++i)
		{
			if (isVisitorEnabled(i)) {
				T transformed = getEnabledVisitor(i).before(node);
				if (transformed==null) {
					disableVisitor(i);
				} else {
					node = transformed;
				}
			}
		}
		return node;
	}
	
	protected Object after(T node, Object result)
	{
		for (int i = 0; i < visitorStack.length; ++i)
		{
			if (isVisitorEnabled(i))
				result = getEnabledVisitor(i).after(node, result);
		}
		return result;
	}
	
	// =========================================================================
	
	protected abstract Object visitNotFound(T node);
	
	protected Object handleVisitingException(T node, Throwable cause)
	{
		throw new VisitingException(node, cause);
	}
	
	// =========================================================================
	
	protected Object resolveAndVisit(T node)
	{
		Class<?> nClass = node.getClass();
		
		VisitChain key = new VisitChain(nClass);
		VisitChain visiChain = cache.get(key);
		try
		{
			if (visiChain == null)
			{
				visiChain = buildVisitChain(key);
				cache.put(visiChain);
			}
			
			if (visiChain.isEmpty())
			{
				return visitNotFound(node);
			}
			else
			{
				return visiChain.invokeChain(enabledVisitors, node);
			}
		}
		catch (InvocationTargetException e)
		{
			Throwable cause = e.getCause();
			if (cause instanceof VisitingException)
				throw (VisitingException) cause;
			
			return handleVisitingException(node, cause);
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
	
	private VisitChain buildVisitChain(VisitChain key) throws SecurityException, NoSuchMethodException
	{
		Class<?> nClass = key.getNodeClass();
		
		List<Link> chain = new ArrayList<Link>();
		
		for (int i = 0; i < visitorStack.length; ++i)
		{
			Class<?> vClass = visitorStack[i].getClass();
			
			Method method = findVisit(vClass, nClass);
			if (method != null)
				chain.add(new Link(i, method));
		}
		
		return new VisitChain(key, chain);
	}
	
	private static Method findVisit(final Class<?> vClass, final Class<?> nClass) throws NoSuchMethodException, SecurityException
	{
		Method method = null;
		
		List<Class<?>> candidates = new ArrayList<Class<?>>();
		
		// Do a breadth first search in the hierarchy
		Queue<Class<?>> work = new ArrayDeque<Class<?>>();
		work.add(nClass);
		while (!work.isEmpty())
		{
			Class<?> workItem = work.remove();
			try
			{
				method = vClass.getMethod(VISIT_METHOD_NAME, workItem);
				candidates.add(workItem);
			}
			catch (NoSuchMethodException e)
			{
				// Consider non-interface classes first
				Class<?> superclass = workItem.getSuperclass();
				if (superclass != null)
					work.add(superclass);
				for (Class<?> i : workItem.getInterfaces())
					work.add(i);
			}
		}
		
		if (!candidates.isEmpty())
		{
			Collections.sort(candidates, new Comparator<Class<?>>()
			{
				@Override
				public int compare(Class<?> arg0, Class<?> arg1)
				{
					if (arg0 == arg1)
					{
						return 0;
					}
					else if (arg0.isAssignableFrom(arg1))
					{
						return +1;
					}
					else if (arg1.isAssignableFrom(arg0))
					{
						return -1;
					}
					else
					{
						throw new MultipleVisitMethodsMatchException(vClass, nClass, arg0, arg1);
					}
				}
			});
			
			method = vClass.getMethod(VISIT_METHOD_NAME, candidates.get(0));
		}
		
		return method;
	}
	
	// =========================================================================
	
	protected static final class VisitChain
			implements
				Comparable<VisitChain>
	{
		private static long useCounter = 0;
		
		private long lastUse = -1;
		
		private final Class<?> nodeClass;
		
		private final Link[] chain;
		
		public VisitChain(Class<?> nClass)
		{
			this.nodeClass = nClass;
			this.chain = new Link[0];
		}
		
		public VisitChain(VisitChain chain, List<Link> links)
		{
			this.nodeClass = chain.nodeClass;
			this.chain = links.toArray(new Link[links.size()]);
		}
		
		public Class<?> getNodeClass()
		{
			return nodeClass;
		}
		
		public boolean isEmpty()
		{
			return (chain.length == 0);
		}
		
		public Object invokeChain(
				StackedVisitorInterface<?>[] enabledVisitorStack,
				Object node) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
		{
			touch();
			
			// this method must only be called on non-empty chains
			if (isEmpty())
				throw new AssertionError();
			
			Object result = node;
			
			int i = 0;
			while (true)
			{
				StackedVisitorInterface<?> visitor = enabledVisitorStack[chain[i].visitorIndex];
				if (visitor != null)
				{
					result = chain[i].method.invoke(visitor, result);
					
					if (result == null)
						break;
					
					++i;
					if (i >= chain.length)
						break;
					
					if (!nodeClass.isInstance(result))
						break;
				}
				else
				{
					++i;
					if (i >= chain.length)
						break;
				}
			}
			
			return result;
		}
		
		public void touch()
		{
			lastUse = ++useCounter;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + nodeClass.hashCode();
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			VisitChain other = (VisitChain) obj;
			if (nodeClass != other.nodeClass)
				return false;
			return true;
		}
		
		@Override
		public int compareTo(VisitChain o)
		{
			// Equality is not possible!
			return (lastUse < o.lastUse) ? -1 : +1;
		}
	}
	
	// =========================================================================
	
	private static final class Link
	{
		private final int visitorIndex;
		
		private final Method method;
		
		public Link(int visitorIndex, Method method)
		{
			super();
			this.visitorIndex = visitorIndex;
			this.method = method;
		}
	}
	
	// =========================================================================
	
	public static final class Cache
	{
		private int lowerCapacity;
		
		private int upperCapacity;
		
		private final CacheDefinition cacheDef;
		
		private final ConcurrentHashMap<VisitChain, VisitChain> cache;
		
		private Cache(List<? extends StackedVisitorInterface<?>> visitorStack,
				float loadFactor,
				int lowerCapacity,
				int upperCapacity)
		{
			this.lowerCapacity = lowerCapacity;
			this.upperCapacity = upperCapacity;
			this.cacheDef = new CacheDefinition(visitorStack);
			this.cache = new ConcurrentHashMap<VisitChain, VisitChain>(lowerCapacity, loadFactor);
		}
		
		private void verifyDefinition(
				List<? extends StackedVisitorInterface<?>> visitorStack)
		{
			if (!new CacheDefinition(visitorStack).equals(cacheDef))
				throw new IllegalArgumentException("Incompatible visitor stack");
		}
		
		private VisitChain get(VisitChain key)
		{
			return cache.get(key);
		}
		
		private synchronized VisitChain put(VisitChain chain)
		{
			VisitChain cached = cache.putIfAbsent(chain, chain);
			if (cached != null)
			{
				return cached;
			}
			else
			{
				// Make sure the target is not swept from the cache ...
				chain.touch();
				if (cache.size() > upperCapacity)
					sweepCache();
				
				return chain;
			}
		}
		
		private synchronized void sweepCache()
		{
			if (cache.size() <= upperCapacity)
				return;
			
			VisitChain keys[] = new VisitChain[cache.size()];
			
			Enumeration<VisitChain> keysEnum = cache.keys();
			
			int i = 0;
			while (i < keys.length && keysEnum.hasMoreElements())
				keys[i++] = keysEnum.nextElement();
			
			int length = i;
			Arrays.sort(keys, 0, length);
			
			int to = length - lowerCapacity;
			for (int j = 0; j < to; ++j)
				cache.remove(keys[j]);
		}
	}
	
	// =========================================================================
	
	private static final class CacheDefinition
	{
		private final int hash;
		
		private final Class<?>[] visitorStackDef;
		
		public CacheDefinition(
				List<? extends StackedVisitorInterface<?>> visitorStack)
		{
			@SuppressWarnings("rawtypes")
			Class[] visitorStackDef = new Class[visitorStack.size()];
			int hash = 0;
			
			int i = 0;
			for (StackedVisitorInterface<?> visitor : visitorStack)
			{
				visitorStackDef[i] = visitor.getClass();
				hash = hash * 13 + visitorStackDef[i].hashCode() * 17;
				++i;
			}
			
			this.hash = hash;
			this.visitorStackDef = visitorStackDef;
		}
		
		@Override
		public int hashCode()
		{
			return hash;
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
			CacheDefinition other = (CacheDefinition) obj;
			if (!Arrays.equals(visitorStackDef, other.visitorStackDef))
				return false;
			return true;
		}
	}
}
