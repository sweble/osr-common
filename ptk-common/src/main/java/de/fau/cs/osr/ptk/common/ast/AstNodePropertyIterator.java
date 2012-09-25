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

package de.fau.cs.osr.ptk.common.ast;

/**
 * An iterator for AstNode properties. This class does not offer a
 * <code>hasNext()</code> method, like the {@link java.util.Iterator}. Instead
 * it only offers a <code>next()</code> method, which returns <code>true</code>
 * if the iterator points to a valid property <b>after</b> the call to
 * <code>next()</code>.
 * 
 * <p>
 * Example loop:
 * 
 * <pre>
 * AstNodePropertyIterator i = iterateProperties();
 * while (i.next())
 * {
 * 	// Call getName(), getValue() or setValue()
 * 	// (these do NOT move the iterator)
 * }
 * </pre>
 */
public abstract class AstNodePropertyIterator
{
	private int i = -1;
	
	// =========================================================================
	
	public boolean next()
	{
		return ++i < getPropertyCount();
	}
	
	public String getName()
	{
		assert i >= 0 && i < getPropertyCount();
		/*
		if (i < 0 || i >= getPropertyCount())
			throw new NoSuchElementException();
		*/
		return getName(i);
	}
	
	public Object getValue()
	{
		assert i >= 0 && i < getPropertyCount();
		/*
		if (i < 0 || i >= getPropertyCount())
			throw new NoSuchElementException();
		*/
		return getValue(i);
	}
	
	public Object setValue(Object value)
	{
		assert i >= 0 && i < getPropertyCount();
		/*
		if (i < 0 || i >= getPropertyCount())
			throw new NoSuchElementException();
		*/
		return setValue(i, value);
	}
	
	// =========================================================================
	
	protected abstract int getPropertyCount();
	
	protected abstract String getName(int index);
	
	protected abstract Object getValue(int index);
	
	protected abstract Object setValue(int index, Object value);
}
