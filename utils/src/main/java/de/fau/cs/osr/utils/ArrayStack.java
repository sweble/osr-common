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

import java.util.Arrays;
import java.util.EmptyStackException;

public class ArrayStack<T>
{
	private Object[] stack;
	
	private final int capacityIncrease;
	
	private int size = 0;
	
	public ArrayStack()
	{
		this.stack = new Object[10];
		this.capacityIncrease = 10;
	}
	
	public ArrayStack(int initialCapacity, int capacityIncrease)
	{
		if (initialCapacity < 0 || capacityIncrease < 1)
			throw new IllegalArgumentException();
		
		this.stack = new Object[initialCapacity];
		this.capacityIncrease = capacityIncrease;
	}
	
	public boolean isEmpty()
	{
		return size == 0;
	}
	
	@SuppressWarnings("unchecked")
	public T peek()
	{
		if (isEmpty())
			throw new EmptyStackException();
		return (T) stack[size - 1];
	}
	
	public T pop()
	{
		T top = peek();
		--size;
		return top;
	}
	
	public T push(T item)
	{
		if (size == stack.length)
			stack = Arrays.copyOf(stack, stack.length + capacityIncrease);
		
		stack[size++] = item;
		
		return item;
	}
	
	public void clear()
	{
		size = 0;
	}
}
