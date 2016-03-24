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

package de.fau.cs.osr.ptk.common.comparer;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.utils.ComparisonException;
import de.fau.cs.osr.utils.DeepComparer;
import de.fau.cs.osr.utils.DeepComparerDelegate;

public class AstNodeComparerDelegate
		implements
			DeepComparerDelegate
{
	private final boolean compareAttributes;

	private final boolean compareLocation;

	// =========================================================================

	public AstNodeComparerDelegate(
			boolean compareAttributes,
			boolean compareLocation)
	{
		this.compareAttributes = compareAttributes;
		this.compareLocation = compareLocation;
	}

	// =========================================================================

	@Override
	public boolean compare(Object a, Object b, DeepComparer comparer) throws ComparisonException
	{
		if (!(a instanceof AstNode<?> && b instanceof AstNode<?>))
			return false;
		compare((AstNode<?>) a, (AstNode<?>) b, comparer);
		return true;
	}

	// =========================================================================

	public void compare(AstNode<?> a, AstNode<?> b, DeepComparer comparer) throws ComparisonException
	{
		if (a == b)
			return;

		if ((a == null) != (b == null))
			throw new AstComparisonException(a, b, AstDifference.NULL_VS_NON_NULL);

		if (a == null)
			return;

		if (a.getClass() != b.getClass())
			throw new AstComparisonException(a, b, AstDifference.NODE_TYPES_DIFFER);

		if (compareLocation)
		{
			if (a.getNativeLocation() == null)
			{
				if (b.getNativeLocation() != null)
					throw new AstComparisonException(a, b, AstDifference.LOCATION_DIFFERS);
			}
			else if (!a.getNativeLocation().equals(b.getNativeLocation()))
				throw new AstComparisonException(a, b, AstDifference.LOCATION_DIFFERS);
		}

		// Compare attributes
		if (compareAttributes)
		{
			compareAttributes(a, b, comparer);
		}

		// Compare properties
		{
			AstNodePropertyIterator i = a.propertyIterator();
			AstNodePropertyIterator j = b.propertyIterator();
			while (i.next())
			{
				// Should not be necessary, but it's here for safety
				if (!j.next())
					throw new InternalError();

				// Should not be necessary, but it's here for safety
				if (!i.getName().equals(j.getName()))
					throw new InternalError();

				try
				{
					comparer.compare(i.getValue(), j.getValue());
				}
				catch (AstComparisonException e)
				{
					throw new PropertyComparisonException(e, a, b, i.getName(), i.getValue(), j.getValue());
				}
			}

			// Should not be necessary, but it's here for safety
			if (j.next())
				throw new InternalError();
		}

		// Compare children
		if (a.isList())
		{
			if (a.size() != b.size())
				throw new AstComparisonException(a, b, AstDifference.NUMBER_OF_CHILDREN_DIFFERS);

			@SuppressWarnings({ "rawtypes", "unchecked" })
			Iterator<AstNode> i = (Iterator) a.iterator();

			@SuppressWarnings({ "rawtypes", "unchecked" })
			Iterator<AstNode> j = (Iterator) b.iterator();

			int k = 0;
			while (i.hasNext() & j.hasNext())
			{
				try
				{
					comparer.compare(i.next(), j.next());
					++k;
				}
				catch (AstComparisonException e)
				{
					throw new ComparisonOfChildrenFailedException(a, b, k, e);
				}
			}
		}
		else
		{
			String[] acn = a.getChildNames();
			String[] bcn = b.getChildNames();

			// Should not be necessary, but it's here for safety
			if (acn.length != bcn.length)
				throw new InternalError();

			for (int i = 0; i < acn.length; ++i)
			{
				// Should not be necessary, but it's here for safety
				if (!acn[i].equals(bcn[i]))
					throw new InternalError();

				try
				{
					comparer.compare(a.get(i), b.get(i));
				}
				catch (AstComparisonException e)
				{
					throw new ComparisonOfChildrenFailedException(a, b, i, acn[i], e);
				}
			}
		}

		// Subtree is equal
	}

	private void compareAttributes(
			AstNode<?> na,
			AstNode<?> nb,
			DeepComparer comparer) throws ComparisonException
	{
		Map<String, Object> a = na.getAttributes();
		Map<String, Object> b = nb.getAttributes();

		if (b == a)
			return;

		if (b.size() != a.size())
			throw new AstComparisonException(na, nb, AstDifference.NUMBER_OF_ATTRIBUTES_DIFFERS);

		Iterator<Entry<String, Object>> i = a.entrySet().iterator();
		while (i.hasNext())
		{
			Entry<String, Object> e = i.next();
			String key = e.getKey();
			Object value = e.getValue();
			if (value == null)
			{
				if (!(b.get(key) == null && b.containsKey(key)))
					throw new AttributeComparisonException(na, nb, key, null, b.get(key));
			}
			else
			{
				try
				{
					comparer.compare(value, b.get(key));
				}
				catch (AstComparisonException ce)
				{
					throw new AttributeComparisonException(ce, na, nb, key, value, b.get(key));
				}
			}
		}
	}
}
