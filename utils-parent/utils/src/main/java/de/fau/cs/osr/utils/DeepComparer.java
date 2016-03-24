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

package de.fau.cs.osr.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DeepComparer
{
	private static final MapComparerDelegate MAP_DELEGATE = new MapComparerDelegate();

	private static final CollectionComparerDelegate COLLECTION_DELEGATE = new CollectionComparerDelegate();

	private static final ArrayComparerDelegate ARRAY_DELEGATE = new ArrayComparerDelegate();

	private final ArrayList<DeepComparerDelegate> delegates =
			new ArrayList<DeepComparerDelegate>();

	// =========================================================================

	/**
	 * Compare two objects for equality.
	 * 
	 * @param rootA
	 *            First object.
	 * @param rootB
	 *            Second object.
	 * @throws ComparisonException
	 *             Thrown if the two trees differ.
	 */
	public static void compareAndThrow(Object rootA, Object rootB) throws ComparisonException
	{
		new DeepComparer().compare(rootA, rootB);
	}

	public static boolean compareNoThrow(Object rootA, Object rootB)
	{
		try
		{
			new DeepComparer().compare(rootA, rootB);
			return true;
		}
		catch (ComparisonException e)
		{
			return false;
		}
	}

	// =========================================================================

	public DeepComparer()
	{
		addDefaultCollectionComparer();
		addDefaultMapComparer();
		addDefaultArrayComparer();
	}

	public DeepComparer(
			DeepComparerDelegate comparer0,
			DeepComparerDelegate... comparersN)
	{
		this();

		delegates.add(comparer0);
		for (DeepComparerDelegate c : comparersN)
			delegates.add(c);
	}

	// =========================================================================

	public void dropAllComparers()
	{
		delegates.clear();
	}

	public void addComparer(DeepComparerDelegate comparer)
	{
		delegates.add(0, comparer);
	}

	public void addDefaultMapComparer()
	{
		addComparer(MAP_DELEGATE);
	}

	public void addDefaultCollectionComparer()
	{
		addComparer(COLLECTION_DELEGATE);
	}

	public void addDefaultArrayComparer()
	{
		addComparer(ARRAY_DELEGATE);
	}

	// =========================================================================

	public void compare(Object a, Object b) throws ComparisonException
	{
		if (a == b)
			return;
		if ((a == null) != (b == null))
			throw new ComparisonException(a, b);

		int size = delegates.size();
		for (int i = 0; i < size; ++i)
		{
			DeepComparerDelegate delegate = delegates.get(i);
			if (delegate.compare(a, b, this))
				return;
		}

		if (!a.equals(b))
		{
			a.equals(b);
			throw new ComparisonException(a, b);
		}
	}

	// =========================================================================

	public static class ArrayComparerDelegate
			implements
				DeepComparerDelegate
	{
		@Override
		public boolean compare(Object a, Object b, DeepComparer comparer) throws ComparisonException
		{
			if (!(a.getClass().isArray() && b.getClass().isArray()))
				return false;

			Class<?> clazz = a.getClass();
			if (!clazz.getComponentType().isPrimitive())
			{
				Object[] aObjA = (Object[]) a;
				Object[] aObjB = (Object[]) b;

				int length = aObjA.length;
				if (aObjB.length != length)
					throw new ComparisonException(a, b);

				for (int i = 0; i < length; i++)
				{
					Object ac = aObjA[i];
					Object bc = aObjB[i];
					comparer.compare(ac, bc);
				}
			}
			else
			{
				boolean eq;
				try
				{
					Method cmp = Arrays.class.getMethod("equals", clazz, clazz);
					eq = (Boolean) cmp.invoke(null, a, b);
				}
				catch (Exception e)
				{
					// This should never happen...
					throw new RuntimeException("Internal error!", e);
				}

				if (!eq)
					throw new ComparisonException(a, b);
			}

			return true;
		}
	}

	public static class CollectionComparerDelegate
			implements
				DeepComparerDelegate
	{
		@Override
		public boolean compare(Object _a, Object _b, DeepComparer comparer) throws ComparisonException
		{
			if (!((_a instanceof Collection) && (_b instanceof Collection)))
				return false;

			Collection<?> a = (Collection<?>) _a;
			Collection<?> b = (Collection<?>) _b;

			Iterator<?> e1 = a.iterator();
			Iterator<?> e2 = b.iterator();
			while (e1.hasNext() && e2.hasNext())
			{
				Object o1 = e1.next();
				Object o2 = e2.next();
				if ((o1 == null) != (o2 == null))
					throw new ComparisonException(a, b);
				comparer.compare(o1, o2);
			}
			if (e1.hasNext() || e2.hasNext())
				throw new ComparisonException(a, b);

			return true;
		}
	}

	public static class MapComparerDelegate
			implements
				DeepComparerDelegate
	{
		@Override
		public boolean compare(Object _a, Object _b, DeepComparer comparer) throws ComparisonException
		{
			if (!((_a instanceof Map) && (_b instanceof Map)))
				return false;

			Map<?, ?> a = (Map<?, ?>) _a;
			Map<?, ?> b = (Map<?, ?>) _b;

			if (b.size() != a.size())
				throw new ComparisonException(a, b);

			Iterator<?> i = a.entrySet().iterator();
			while (i.hasNext())
			{
				Entry<?, ?> e = (Entry<?, ?>) i.next();
				Object key = e.getKey();
				Object value = e.getValue();
				if (value == null)
				{
					if (!(b.get(key) == null && b.containsKey(key)))
						throw new ComparisonException(a, b);
				}
				else
				{
					comparer.compare(value, b.get(key));
				}
			}

			return true;
		}
	}
}
