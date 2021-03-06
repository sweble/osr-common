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

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AccumulatingRingBufferDouble
		implements
			Iterable<Double>
{
	private final double[] buffer;

	private int fill = 0;

	private int start = 0;

	private BigDecimal sum = BigDecimal.ZERO;

	// =========================================================================

	public AccumulatingRingBufferDouble(int capacity)
	{
		this.buffer = new double[capacity];
	}

	// =========================================================================

	/**
	 * Adds a new value to the ring buffer, overwriting the oldest value if the
	 * ring buffer is already full.
	 */
	public void add(double value)
	{
		int i = indexOf(getN());
		if (willAddOverwrite())
		{
			removeValueFormSumAt(0);
			advance(1);
		}
		else
		{
			fill += 1;
		}
		buffer[i] = value;
		addToSum(value);
	}

	public void removeNewestN(int n)
	{
		if (getN() < n)
			throw new IllegalArgumentException();
		for (int i = 0; i < n; ++i)
			removeValueFormSumAt(fill - i - 1);
		fill -= n;
	}

	public void removeOldestN(int n)
	{
		if (getN() < n)
			throw new IllegalArgumentException();
		for (int i = 0; i < n; ++i)
			removeValueFormSumAt(i);
		advance(n);
		fill -= n;
	}

	public void clear()
	{
		start = 0;
		fill = 0;
	}

	/**
	 * @return @{code true} if the next call to {@link #add(double)} will
	 *         overwrite the oldest value.
	 */
	public boolean willAddOverwrite()
	{
		return getN() == getCapacity();
	}

	/**
	 * @param index
	 *            The index of the value, where 0 refers to the newest (most
	 *            recent) value.
	 */
	public double get(int index)
	{
		if (getN() == 0)
			throw new NoSuchElementException();
		return getAt(getN() - index - 1);
	}

	/**
	 * @return The value added last (the most recent value).
	 */
	public double getNewest()
	{
		if (getN() == 0)
			throw new NoSuchElementException();
		return getAt(getN() - 1);
	}

	public double getOldest()
	{
		if (getN() == 0)
			throw new NoSuchElementException();
		return getAt(0);
	}

	public BigDecimal getSum()
	{
		return sum;
	}

	public int getN()
	{
		return fill;
	}

	public int getCapacity()
	{
		return buffer.length;
	}

	@Override
	public Iterator<Double> iterator()
	{
		return new Iterator<Double>()
		{
			private int i = 0;

			@Override
			public boolean hasNext()
			{
				return i < getN();
			}

			@Override
			public Double next()
			{
				if (!hasNext())
					throw new NoSuchElementException();

				return getAt(i++);
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	// =========================================================================

	private int advance(int n)
	{
		return start = (start + n) % getCapacity();
	}

	/**
	 * @param index
	 *            0 is the oldest value.
	 */
	private int indexOf(int index)
	{
		return (start + index) % getCapacity();
	}

	private double getAt(int index)
	{
		return buffer[indexOf(index)];
	}

	private void addToSum(double value)
	{
		sum = sum.add(BigDecimal.valueOf(value));
	}

	private void removeValueFormSumAt(int index)
	{
		sum = sum.subtract(BigDecimal.valueOf(getAt(index)));
	}
}
