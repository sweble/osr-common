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

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RingBuffer<T>
        implements
            Iterable<T>
{
	private final Object[] buffer;
	
	private int fill = 0;
	
	private int start = 0;
	
	public RingBuffer(int capacity)
	{
		this.buffer = new Object[capacity];
	}
	
	public void add(T x)
	{
		if (fill == buffer.length)
		{
			start = (start + 1) % buffer.length;
			
			buffer[(start + fill) % buffer.length] = x;
		}
		else
		{
			buffer[(start + fill) % buffer.length] = x;
			
			++fill;
		}
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return new Iterator<T>()
		{
			private int i = 0;
			
			@Override
			public boolean hasNext()
			{
				return i < fill;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public T next()
			{
				if (!hasNext())
					throw new NoSuchElementException();
				
				return (T) buffer[(start + i++) % buffer.length];
			}
			
			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public int size()
	{
		return fill;
	}
}
