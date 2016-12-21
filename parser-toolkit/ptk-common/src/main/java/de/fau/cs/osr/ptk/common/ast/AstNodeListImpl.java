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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import xtc.util.Pair;

/**
 * List of nodes using a Pair<AstNodeInterface> as underlying data structure.
 * 
 * Invariants: <br />
 * <ul>
 * <li>This list never contains {@code null} as node.</li>
 * <li>This list never contains another {@link AstNodeListImpl} as node.</li>
 * </ul>
 * 
 * In order to assure these invariants, all functions adding nodes to this list
 * will discard null references and will recursively flatten any
 * {@link AstNodeListImpl}.
 */
public class AstNodeListImpl<T extends AstNode<T>>
		extends
			AstAbstractInnerNode<T>
		implements
			AstNodeList<T>
{
	private static final long serialVersionUID = -3855416846550776026L;
	
	// =========================================================================
	
	private ArrayList<T> children = new ArrayList<T>();
	
	// =========================================================================
	
	public AstNodeListImpl()
	{
	}
	
	public AstNodeListImpl(T child)
	{
		add(child);
	}
	
	public AstNodeListImpl(T car, Pair<? extends T> cdr)
	{
		add(car);
		addAll(cdr);
	}
	
	public AstNodeListImpl(T a, T b)
	{
		add(a);
		add(b);
	}
	
	public AstNodeListImpl(T a, T b, T c)
	{
		add(a);
		add(b);
		add(c);
	}
	
	public AstNodeListImpl(T a, T b, T c, T d)
	{
		add(a);
		add(b);
		add(c);
		add(d);
	}
	
	public AstNodeListImpl(Pair<? extends T> list)
	{
		addAll(list);
	}
	
	public AstNodeListImpl(Collection<? extends T> list)
	{
		addAll(list);
	}
	
	public AstNodeListImpl(T... children)
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
		return addIntern(children, e);
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
		return addAllIntern(children, c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		return addAllIntern(children, index, c);
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
		else
		{
			switch (value.getNodeType())
			{
				case AstNode.NT_NODE_LIST:
					throw new IllegalArgumentException(
							"Must not set a single element to a NodeList");
					
				case AstNode.NT_TEXT:
					return setTextIntern(children, index, (AstStringNode<T>) value);
					
				default:
					return children.set(index, value);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <S extends AstNode<S>> S setTextIntern(
			ArrayList<S> list,
			int index,
			AstStringNode<S> text)
	{
		if (text.getContent().isEmpty())
		{
			return list.remove(index);
		}
		else
		{
			if (index > 0 && !text.hasAttributes())
			{
				S prev = list.get(index - 1);
				if (prev.getNodeType() == AstNode.NT_TEXT && !prev.hasAttributes())
				{
					try
					{
						list.set(index - 1, (S) mergeTextNodes((AstStringNode<S>) prev, text));
						return list.remove(index);
					}
					catch (CloneNotSupportedException e)
					{
						// Just set, don't merge
					}
				}
			}
			return list.set(index, (S) text);
		}
	}
	
	@Override
	public void add(int index, T element)
	{
		addIntern(children, index, element);
	}
	
	private static <S extends AstNode<S>> boolean addIntern(
			ArrayList<S> list,
			S element)
	{
		return addIntern(list, list.size(), element);
	}
	
	private static <S extends AstNode<S>> boolean addIntern(
			ArrayList<S> list,
			int index,
			S element)
	{
		if (element == null)
		{
			return false;
		}
		else
		{
			switch (element.getNodeType())
			{
				case AstNode.NT_NODE_LIST:
					return addAllIntern(list, index, element);
					
				case AstNode.NT_TEXT:
					return addTextIntern(list, index, (AstStringNode<S>) element);
					
				default:
					list.add(index, element);
					return true;
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean addTextIntern(
			ArrayList list,
			int index,
			AstStringNode text)
	{
		if (text.getContent().isEmpty())
		{
			return false;
		}
		else
		{
			if (index > 0 && !text.hasAttributes())
			{
				AstNode prev = (AstNode) list.get(index - 1);
				if (prev.getNodeType() == AstNode.NT_TEXT && !prev.hasAttributes())
				{
					try
					{
						list.set(index - 1, mergeTextNodes((AstStringNode) prev, text));
						return true;
					}
					catch (CloneNotSupportedException e)
					{
						// Just add, don't merge
					}
				}
			}
			
			list.add(index, text);
			return true;
		}
	}
	
	private static <S extends AstNode<S>> AstStringNode<S> mergeTextNodes(
			AstStringNode<S> prev,
			AstStringNode<S> text) throws CloneNotSupportedException
	{
		@SuppressWarnings("unchecked")
		AstStringNode<S> merged = (AstStringNode<S>) prev.clone();
		merged.setContent(merged.getContent() + text.getContent());
		return merged;
	}
	
	private static <S extends AstNode<S>> boolean addAllIntern(
			ArrayList<S> list,
			Collection<? extends S> c)
	{
		boolean changed = false;
		for (S n : c)
			changed |= addIntern(list, n);
		return changed;
	}
	
	private static <S extends AstNode<S>> boolean addAllIntern(
			ArrayList<S> list,
			int index,
			Collection<? extends S> c)
	{
		boolean changed = false;
		for (S n : c)
		{
			int oldSize = list.size();
			changed |= addIntern(list, index, n);
			index += list.size() - oldSize;
		}
		return changed;
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
			
			node.toString(out);
		}
		
		out.append(']');
	}
	
	// =========================================================================
	
	@Override
	public void exchange(AstNodeList<T> other)
	{
		if (other instanceof AstNodeListImpl)
		{
			AstNodeListImpl<T> other2 = (AstNodeListImpl<T>) other;
			ArrayList<T> tmp = this.children;
			this.children = other2.children;
			other2.children = tmp;
		}
		else
		{
			AstNodeListImpl<T> tmp = new AstNodeListImpl<T>(other);
			this.exchange(tmp);
		}
	}
	
	// =========================================================================
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object clone() throws CloneNotSupportedException
	{
		AstNodeListImpl clone = (AstNodeListImpl) super.clone();
		clone.children = new ArrayList(children);
		return clone;
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
			else
			{
				switch (e.getNodeType())
				{
					case AstNode.NT_NODE_LIST:
						throw new IllegalArgumentException(
								"Must not set a single element to a NodeList");
						
					case AstNode.NT_TEXT:
						setTextIntern((AstStringNode<T>) e);
						break;
					
					default:
						i.set(e);
						current = e;
						break;
				}
			}
		}
		
		/**
		 * This whole method breaks the set() interface established by the
		 * ListIterator class!
		 */
		@SuppressWarnings("unchecked")
		private void setTextIntern(AstStringNode<T> text)
		{
			if (current == null)
				throw new IllegalStateException();
			
			if (text.getContent().isEmpty())
			{
				i.remove();
				current = null;
			}
			else
			{
				if (i.hasPrevious() && !text.hasAttributes())
				{
					i.previous();
					if (i.hasPrevious())
					{
						T prev = i.previous();
						if (prev.getNodeType() == AstNode.NT_TEXT && !prev.hasAttributes())
						{
							try
							{
								i.set((T) mergeTextNodes((AstStringNode<T>) prev, text));
								// set() protocol requires that the iterator doesn't 
								// move. When deleting something that's not 
								// possible. So we move to the merged text element.
								i.next();
								i.next();
								i.remove();
								i.previous();
								// use own method to set current!
								next();
								return;
							}
							catch (CloneNotSupportedException e)
							{
								// Just set, don't merge
							}
						}
						i.next();
					}
					i.next();
				}
				i.set((T) text);
			}
		}
		
		@Override
		public void add(T e)
		{
			if (e != null)
			{
				current = null;
				switch (e.getNodeType())
				{
					case AstNode.NT_NODE_LIST:
						for (T n : (AstNodeList<T>) e)
							i.add(n);
						break;
					
					case AstNode.NT_TEXT:
						addTextIntern((AstStringNode<T>) e);
						break;
					
					default:
						i.add(e);
						break;
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		private void addTextIntern(AstStringNode<T> text)
		{
			if (!text.getContent().isEmpty())
			{
				if (i.hasPrevious() && !text.hasAttributes())
				{
					T prev = i.previous();
					if (prev.getNodeType() == AstNode.NT_TEXT && !prev.hasAttributes())
					{
						try
						{
							i.set((T) mergeTextNodes((AstStringNode<T>) prev, text));
							i.next();
							return;
						}
						catch (CloneNotSupportedException e)
						{
							// Just add, don't merge
						}
					}
				}
				i.add((T) text);
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
			i = AstNodeListImpl.this.children.listIterator(start);
		}
	}
}
