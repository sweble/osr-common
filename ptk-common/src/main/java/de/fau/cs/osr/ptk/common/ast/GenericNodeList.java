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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import xtc.util.Pair;

/**
 * List of nodes using a Pair<AstNodeInterface> as underlying data structure.
 * 
 * Invariants: <br />
 * <ul>
 * <li>This list never contains {@code null} as node.</li>
 * <li>This list never contains another {@link GenericNodeList} as node.</li>
 * </ul>
 * 
 * In order to assure these invariants, all functions adding nodes to this list
 * will discard null references and will recursively flatten any
 * {@link GenericNodeList}.
 */
public class GenericNodeList<T extends AstNode<T>>
		extends
			GenericInnerNode<T>
{
	private static final long serialVersionUID = -3855416846550776026L;
	
	// =========================================================================
	
	private LinkedList<T> children = new LinkedList<T>();
	
	// =========================================================================
	
	public GenericNodeList()
	{
	}
	
	public GenericNodeList(T child)
	{
		add(child);
	}
	
	public GenericNodeList(
			T car,
			Pair<? extends T> cdr)
	{
		add(car);
		addAll(cdr);
	}
	
	public GenericNodeList(T a, T b)
	{
		add(a);
		add(b);
	}
	
	public GenericNodeList(
			T a,
			T b,
			T c)
	{
		add(a);
		add(b);
		add(c);
	}
	
	public GenericNodeList(
			T a,
			T b,
			T c,
			T d)
	{
		add(a);
		add(b);
		add(c);
		add(d);
	}
	
	public GenericNodeList(Pair<? extends T> list)
	{
		addAll(list);
	}
	
	public GenericNodeList(Collection<? extends T> list)
	{
		addAll(list);
	}
	
	public GenericNodeList(T... children)
	{
		for (T x : children)
			add(x);
	}
	
	// =========================================================================
	
	public int getNodeType()
	{
		return NT_NODE_LIST;
	}
	
	// =========================================================================
	// Implementation of the List interface
	
	@Override
	public int size()
	{
		return children.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		return children.isEmpty();
	}
	
	@Override
	public boolean contains(Object o)
	{
		return children.contains(o);
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return children.iterator();
	}
	
	@Override
	public Object[] toArray()
	{
		return children.toArray();
	}
	
	@Override
	public <S> S[] toArray(S[] a)
	{
		return children.toArray(a);
	}
	
	// Modification Operations
	
	@Override
	public boolean add(T e)
	{
		if (e == null)
		{
			return false;
		}
		else if (e.getClass() == this.getClass())
		{
			return children.addAll(((GenericNodeList<T>) e).children);
		}
		else
		{
			return children.add(e);
		}
	}
	
	@Override
	public boolean remove(Object o)
	{
		return children.remove(o);
	}
	
	// Bulk Modification Operations
	
	@Override
	public boolean containsAll(Collection<?> c)
	{
		return children.containsAll(c);
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		boolean changed = false;
		for (T n : c)
			changed |= add(n);
		return changed;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		final LinkedList<T> insert = new LinkedList<T>();
		for (T n : c)
		{
			if (n == null)
			{
				continue;
			}
			else if (n.getClass() == this.getClass())
			{
				insert.addAll(((GenericNodeList<T>) n).children);
			}
			else
			{
				insert.add(n);
			}
		}
		return children.addAll(index, insert);
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		return children.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		return children.retainAll(c);
	}
	
	@Override
	public void clear()
	{
		children.clear();
	}
	
	// Positional Access Operations
	
	@Override
	public T get(int index)
	{
		return children.get(index);
	}
	
	@Override
	public T set(int index, T value)
	{
		if (value == null)
		{
			throw new NullPointerException(
					"A NodeList must not contain a null element!");
		}
		else if (value.getClass() == this.getClass())
		{
			throw new IllegalArgumentException(
					"Must not set a single element to a NodeList");
		}
		else
		{
			return children.set(index, value);
		}
	}
	
	@Override
	public void add(int index, T element)
	{
		if (element == null)
			return;
		
		else if (element.getClass() == this.getClass())
		{
			addAll(index, element);
		}
		else
		{
			children.add(index, element);
		}
	}
	
	@Override
	public T remove(int index)
	{
		return children.remove(index);
	}
	
	// Search Operations
	
	@Override
	public int indexOf(Object o)
	{
		return children.indexOf(o);
	}
	
	@Override
	public int lastIndexOf(Object o)
	{
		return children.lastIndexOf(o);
	}
	
	// List Iterators
	
	@Override
	public ListIterator<T> listIterator()
	{
		return new ChildListIterator();
	}
	
	@Override
	public ListIterator<T> listIterator(int index)
	{
		return new ChildListIterator(index);
	}
	
	// View
	
	@Override
	public List<T> subList(int fromIndex, int toIndex)
	{
		return children.subList(fromIndex, toIndex);
	}
	
	// =========================================================================
	// Extension of the List interface for Pair
	
	@Override
	public boolean addAll(Pair<? extends T> p)
	{
		boolean changed = false;
		while (!p.isEmpty())
		{
			changed |= add(p.head());
			p = p.tail();
		}
		return changed;
	}
	
	// =========================================================================
	// Introspection
	
	@Override
	public boolean isList()
	{
		return true;
	}
	
	@Override
	public String[] getChildNames()
	{
		return EMPTY_CHILD_NAMES;
	}
	
	// =========================================================================
	
	@Override
	public void toString(Appendable out) throws IOException
	{
		out.append('[');
		
		boolean first = true;
		for (T node : this)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				out.append(", ");
			}
			
			if (node == null)
			{
				out.append("null");
			}
			else
			{
				node.toString(out);
			}
		}
		
		out.append(']');
	}
	
	// =========================================================================
	
	private final class ChildListIterator
			implements
				AstChildIterator<T>
	{
		private ListIterator<T> i;
		
		private T current = null;
		
		private final int start;
		
		public ChildListIterator()
		{
			start = 0;
			reset();
		}
		
		public ChildListIterator(int index)
		{
			start = index;
			reset();
		}
		
		@Override
		public boolean hasNext()
		{
			return i.hasNext();
		}
		
		@Override
		public T next()
		{
			current = i.next();
			return current;
		}
		
		@Override
		public boolean hasPrevious()
		{
			return i.hasPrevious();
		}
		
		@Override
		public T previous()
		{
			current = i.previous();
			return current;
		}
		
		@Override
		public int nextIndex()
		{
			return i.nextIndex();
		}
		
		@Override
		public int previousIndex()
		{
			return i.previousIndex();
		}
		
		@Override
		public void remove()
		{
			i.remove();
			current = null;
		}
		
		@Override
		public void set(T e)
		{
			if (e == null)
			{
				throw new NullPointerException(
						"A NodeList must not contain a null element!");
			}
			else if (e.getClass() == GenericNodeList.this.getClass())
			{
				throw new IllegalArgumentException(
						"Must not set a single element to a NodeList");
			}
			else
			{
				i.set(e);
				current = e;
			}
		}
		
		@Override
		public void add(T e)
		{
			if (e == null)
				return;
			
			if (e.getClass() == GenericNodeList.this.getClass())
			{
				for (T n : (GenericNodeList<T>) e)
					i.add(n);
				current = null;
			}
			else
			{
				i.add(e);
				current = null;
			}
		}
		
		@Override
		public T get()
		{
			if (current == null)
				throw new IllegalStateException();
			return current;
		}
		
		@Override
		public void reset()
		{
			i = GenericNodeList.this.children.listIterator(start);
		}
	}
}
