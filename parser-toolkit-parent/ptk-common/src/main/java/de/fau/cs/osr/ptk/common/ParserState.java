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

import de.fau.cs.osr.utils.ArrayStack;
import de.fau.cs.osr.utils.Utils;
import xtc.util.State;

public class ParserState<C extends ParserContext>
		implements
			State
{
	private final Class<C> contextClass;

	private final ArrayStack<C> stack = new ArrayStack<C>(16, 16);

	private final ArrayStack<C> pool;

	// =========================================================================

	public ParserState()
	{
		this.contextClass = null;
		this.pool = null;
		start();
	}

	public ParserState(Class<C> contextClass)
	{
		this.contextClass = contextClass;
		this.pool = new ArrayStack<C>(16, 16);
		start();
	}

	// =========================================================================

	public final C getTop()
	{
		return stack.peek();
	}

	public final void reset(String file)
	{
		stack.clear();
	}

	public final void start()
	{
		if (stack.isEmpty())
		{
			push();
			stack.peek().clear();
		}
		else
		{
			C last = stack.peek();
			push();
			stack.peek().init(last);
		}
	}

	public final void commit()
	{
		pop();
	}

	public final void abort()
	{
		pop();
	}

	// =========================================================================

	protected C instantiateContext()
	{
		return pool.isEmpty() ? Utils.getInstance(contextClass) : pool.pop();
	}

	private final void push()
	{
		stack.push(instantiateContext());
	}

	private final void pop()
	{
		if (pool != null)
		{
			pool.push(stack.pop());
		}
		else
		{
			stack.pop();
		}
	}
}
