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
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class VisitorLogic<T>
{
	private static final int LOWER_CAPACITY = 256;
	
	private static final int UPPER_CAPACITY = 384;
	
	private static final float LOAD_FACTOR = .6f;
	
	private static final ConcurrentHashMap<Target, Target> CACHE =
			new ConcurrentHashMap<Target, Target>(LOWER_CAPACITY, LOAD_FACTOR);
	
	private VisitorInterface<T> visitorImpl;
	
	// =========================================================================
	
	public VisitorLogic(VisitorInterface<T> visitorImpl)
	{
		this.visitorImpl = visitorImpl;
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
	public final Object go(T node)
	{
		if (!visitorImpl.before(node))
			return null;
		
		Object result = visitorImpl.dispatch(node);
		return visitorImpl.after(node, result);
	}
	
	public void setImpl(VisitorInterface<T> visitorImpl)
	{
		this.visitorImpl = visitorImpl;
	}
	
	public VisitorInterface<T> getImpl()
	{
		return this.visitorImpl;
	}
	
	public static <T> Object dispatchTo(VisitorInterface<T> modeImpl, T n)
	{
		return resolveAndVisit(modeImpl, n);
	}
	
	// =========================================================================
	
	protected final Object resolveAndVisit(T node)
	{
		return resolveAndVisit(visitorImpl, node);
	}
	
	protected static <T> Object resolveAndVisit(
			VisitorInterface<T> visitorImpl,
			T node)
	{
		Class<?> vClass = visitorImpl.getClass();
		Class<?> nClass = node.getClass();
		
		Target key = new Target(vClass, nClass);
		Target cached = CACHE.get(key);
		try
		{
			if (cached == null)
				cached = findVisit(key);
			
			if (cached.getMethod() == null)
				return visitorImpl.visitNotFound(node);
			
			return cached.invoke(visitorImpl, node);
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
	
	private static Target findVisit(Target key) throws SecurityException
	{
		Method method = null;
		
		Class<?> vClass = key.getVClass();
		Class<?> nClass = key.getNClass();
		
		List<Class<?>> candidates = new ArrayList<Class<?>>();
		
		// Do a breadth first search in the hierarchy
		Queue<Class<?>> work = new ArrayDeque<Class<?>>();
		work.add(nClass);
		while (!work.isEmpty())
		{
			Class<?> workItem = work.remove();
			try
			{
				method = vClass.getMethod("visit", workItem);
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
						throw new MultipleVisitMethodsMatchException(arg0, arg1);
					}
				}
			});
			
			try
			{
				method = vClass.getMethod("visit", candidates.get(0));
			}
			catch (NoSuchMethodException e)
			{
				throw new InternalError("This cannot happen");
			}
		}
		
		Target target = new Target(key, method);
		Target cached = CACHE.putIfAbsent(target, target);
		if (cached != null)
		{
			return cached;
		}
		else
		{
			// Make sure the target is not swept from the cache ...
			target.touch();
			if (CACHE.size() > UPPER_CAPACITY)
				sweepCache();
			
			return target;
		}
	}
	
	private static synchronized void sweepCache()
	{
		if (CACHE.size() <= UPPER_CAPACITY)
			return;
		
		Target keys[] = new Target[CACHE.size()];
		
		Enumeration<Target> keysEnum = CACHE.keys();
		
		int i = 0;
		while (i < keys.length && keysEnum.hasMoreElements())
			keys[i++] = keysEnum.nextElement();
		
		int length = i;
		Arrays.sort(keys, 0, length);
		
		int to = length - LOWER_CAPACITY;
		for (int j = 0; j < to; ++j)
			CACHE.remove(keys[j]);
	}
	
	// =========================================================================
	
	protected static final class Target
			implements
				Comparable<Target>
	{
		private static long useCounter = 0;
		
		private long lastUse = -1;
		
		private final Class<?> vClass;
		
		private final Class<?> nClass;
		
		private final Method method;
		
		public Target(Class<?> vClass, Class<?> nClass)
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
		
		public Class<?> getVClass()
		{
			return vClass;
		}
		
		public Class<?> getNClass()
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
		
		public Object invoke(VisitorInterface<?> visitor, Object node) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
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
