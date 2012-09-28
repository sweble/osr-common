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
 * <li>This list never contains another {@link NodeList} as node.</li>
 * </ul>
 * 
 * In order to assure these invariants, all functions adding nodes to this list
 * will discard null references and will recursively flatten any
 * {@link NodeList}.
 */
public class NodeList
        extends
            InnerNode
{
	private static final long serialVersionUID = -3855416846550776026L;
	
	// =========================================================================
	
	private LinkedList<AstNodeInterface> children = new LinkedList<AstNodeInterface>();
	
	// =========================================================================
	
	public NodeList()
	{
	}
	
	public NodeList(AstNodeInterface child)
	{
		add(child);
	}
	
	public NodeList(AstNodeInterface car, Pair<? extends AstNodeInterface> cdr)
	{
		add(car);
		addAll(cdr);
	}
	
	public NodeList(AstNodeInterface a, AstNodeInterface b)
	{
		add(a);
		add(b);
	}
	
	public NodeList(AstNodeInterface a, AstNodeInterface b, AstNodeInterface c)
	{
		add(a);
		add(b);
		add(c);
	}
	
	public NodeList(AstNodeInterface a, AstNodeInterface b, AstNodeInterface c, AstNodeInterface d)
	{
		add(a);
		add(b);
		add(c);
		add(d);
	}
	
	public NodeList(Pair<? extends AstNodeInterface> list)
	{
		addAll(list);
	}
	
	public NodeList(Collection<? extends AstNodeInterface> list)
	{
		addAll(list);
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
	public Iterator<AstNodeInterface> iterator()
	{
		return children.iterator();
	}
	
	@Override
	public Object[] toArray()
	{
		return children.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a)
	{
		return children.toArray(a);
	}
	
	// Modification Operations
	
	@Override
	public boolean add(AstNodeInterface e)
	{
		if (e == null)
		{
			return false;
		}
		else if (e.getNodeType() == NT_NODE_LIST)
		{
			return children.addAll(((NodeList) e).children);
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
	public boolean addAll(Collection<? extends AstNodeInterface> c)
	{
		boolean changed = false;
		for (AstNodeInterface n : c)
			changed |= add(n);
		return changed;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends AstNodeInterface> c)
	{
		final LinkedList<AstNodeInterface> insert = new LinkedList<AstNodeInterface>();
		for (AstNodeInterface n : c)
		{
			if (n == null)
			{
				continue;
			}
			else if (n.getNodeType() == NT_NODE_LIST)
			{
				insert.addAll(((NodeList) n).children);
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
	public AstNodeInterface get(int index)
	{
		return children.get(index);
	}
	
	@Override
	public AstNodeInterface set(int index, AstNodeInterface value)
	{
		if (value == null)
		{
			throw new NullPointerException(
			        "A NodeList must not contain a null element!");
		}
		else if (value.getNodeType() == NT_NODE_LIST)
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
	public void add(int index, AstNodeInterface element)
	{
		if (element == null)
			return;
		
		else if (element.getNodeType() == NT_NODE_LIST)
		{
			addAll(index, element);
		}
		else
		{
			children.add(index, element);
		}
	}
	
	@Override
	public AstNodeInterface remove(int index)
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
	public ListIterator<AstNodeInterface> listIterator()
	{
		return new ChildListIterator();
	}
	
	@Override
	public ListIterator<AstNodeInterface> listIterator(int index)
	{
		return new ChildListIterator(index);
	}
	
	// View
	
	@Override
	public List<AstNodeInterface> subList(int fromIndex, int toIndex)
	{
		return children.subList(fromIndex, toIndex);
	}
	
	// =========================================================================
	// Extension of the List interface for Pair
	
	@Override
	public boolean addAll(Pair<? extends AstNodeInterface> p)
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
		for (AstNodeInterface node : this)
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
	                AstChildIterator
	{
		private ListIterator<AstNodeInterface> i;
		
		private AstNodeInterface current = null;
		
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
		public AstNodeInterface next()
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
		public AstNodeInterface previous()
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
		public void set(AstNodeInterface e)
		{
			if (e == null)
			{
				throw new NullPointerException(
				        "A NodeList must not contain a null element!");
			}
			else if (e.getNodeType() == NT_NODE_LIST)
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
		public void add(AstNodeInterface e)
		{
			if (e == null)
				return;
			
			if (e.getNodeType() == NT_NODE_LIST)
			{
				for (AstNodeInterface n : (NodeList) e)
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
		public AstNodeInterface get()
		{
			if (current == null)
				throw new IllegalStateException();
			return current;
		}
		
		@Override
		public void reset()
		{
			i = NodeList.this.children.listIterator(start);
		}
	}
}
