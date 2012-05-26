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
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import xtc.tree.Locatable;
import xtc.util.Pair;

/**
 * The parent node of all AST (abstract/attributed syntax tree) nodes.
 * 
 * FIXME: Get rid of Locatable interface somehow ...
 * 
 * @see InnerNode
 * @see NodeList
 * @see LeafNode
 */
public abstract class AstNode
        implements
            List<AstNode>,
            AstNodePropertyInterface,
            AstNodeAttributeInterface,
            Locatable,
            Cloneable,
            Serializable
{
	private static final long serialVersionUID = 3333532331617925714L;
	
	// =========================================================================
	
	public static final int NT_CUSTOM_BIT = 0x10000;
	
	public static final int NT_UNTYPED = -1;
	
	public static final int NT_NODE_LIST = 0x002;
	
	public static final int NT_PARSER_ENTITY = 0x005;
	
	public static final int NT_TUPLE_1 = 0x101;
	
	public static final int NT_TUPLE_2 = 0x102;
	
	public static final int NT_TUPLE_3 = 0x103;
	
	public static final int NT_TUPLE_4 = 0x104;
	
	public static final int NT_TUPLE_5 = 0x105;
	
	public static final int NT_TEXT = 0x1001;
	
	// =========================================================================
	
	protected static final String[] EMPTY_CHILD_NAMES = new String[0];
	
	// =========================================================================
	
	private HashMap<String, Object> attributes;
	
	private Location location;
	
	// =========================================================================
	
	public AstNode()
	{
	}
	
	public AstNode(Location location)
	{
		setNativeLocation(location);
	}
	
	public AstNode(xtc.tree.Location location)
	{
		setLocation(location);
	}
	
	// =========================================================================
	
	/**
	 * Returns an integer value that identifies the node type. It's the
	 * programmers responsibility to make sure these values are unique.
	 */
	public int getNodeType()
	{
		return NT_UNTYPED;
	}
	
	/**
	 * Returns <code>true</code> if the given node type equals the node type
	 * returned by getNodeType().
	 */
	public boolean isNodeType(int testType)
	{
		return getNodeType() == testType;
	}
	
	/**
	 * Returns the fully qualified name of this node's class.
	 */
	public final String getNodeTypeName()
	{
		return getClass().getName();
	}
	
	/**
	 * Returns the name of this node. The name is the simple name of the node's
	 * class.
	 */
	public final String getNodeName()
	{
		return getClass().getSimpleName();
	}
	
	// =========================================================================
	// Implementation of AstNodeAttributeInterface
	
	@Override
	public final boolean hasAttributes()
	{
		return attributes != null && !attributes.isEmpty();
	}
	
	@Override
	public final Map<String, Object> getAttributes()
	{
		if (attributes == null)
			return Collections.emptyMap();
		
		return Collections.unmodifiableMap(attributes);
	}
	
	@Override
	public final void setAttributes(Map<String, Object> attrs)
	{
		this.attributes = new HashMap<String, Object>(attrs);
	}
	
	@Override
	public final void clearAttributes()
	{
		this.attributes = null;
	}
	
	@Override
	public final boolean hasAttribute(String name)
	{
		if (attributes == null)
			return false;
		
		return attributes.containsKey(name);
	}
	
	@Override
	public final Object getAttribute(String name)
	{
		if (attributes == null)
			return null;
		
		return attributes.get(name);
	}
	
	@Override
	public final Object setAttribute(String name, Object value)
	{
		if (attributes == null)
			attributes = new HashMap<String, Object>();
		
		return attributes.put(name, value);
	}
	
	@Override
	public final Object removeAttribute(String name)
	{
		if (attributes == null)
			return null;
		
		Object value = attributes.remove(name);
		if (attributes.isEmpty())
			attributes = null;
		
		return value;
	}
	
	@Override
	public final int getIntAttribute(String name)
	{
		if (attributes == null)
			return 0;
		
		return (Integer) attributes.get(name);
	}
	
	@Override
	public final Integer setIntAttribute(String name, Integer value)
	{
		if (attributes == null)
			attributes = new HashMap<String, Object>();
		
		return (Integer) attributes.put(name, value);
	}
	
	@Override
	public final boolean getBooleanAttribute(String name)
	{
		if (attributes == null)
			return false;
		
		Object o = attributes.get(name);
		
		if (null == o)
			return false;
		
		return (Boolean) o;
	}
	
	@Override
	public final boolean setBooleanAttribute(String name, boolean value)
	{
		if (attributes == null)
			attributes = new HashMap<String, Object>();
		
		Boolean o = (Boolean) attributes.put(name, value);
		
		if (o == null)
			return false;
		
		return (Boolean) o;
	}
	
	@Override
	public final String getStringAttribute(String name)
	{
		if (attributes == null)
			return null;
		
		return (String) attributes.get(name);
	}
	
	@Override
	public final String setStringAttribute(String name, String value)
	{
		if (attributes == null)
			attributes = new HashMap<String, Object>();
		
		return (String) attributes.put(name, value);
	}
	
	// =========================================================================
	// Implementation of AstNodePropertyInterface
	
	@Override
	public final boolean hasProperties()
	{
		return getPropertyCount() > 0;
	}
	
	@Override
	public int getPropertyCount()
	{
		return 0;
	}
	
	@Override
	public Object getProperty(String name)
	{
		AstNodePropertyIterator i = propertyIterator();
		while (i.next())
		{
			if (i.getName().equals(name))
				return i.getValue();
		}
		throw new NoSuchPropertyException();
	}
	
	@Override
	public Object setProperty(String name, Object value)
	{
		AstNodePropertyIterator i = propertyIterator();
		while (i.next())
		{
			if (i.getName().equals(name))
				return i.getValue();
		}
		throw new NoSuchPropertyException();
	}
	
	@Override
	public AstNodePropertyIterator propertyIterator()
	{
		return new AstNodePropertyIterator()
		{
			@Override
			protected String getName(int index)
			{
				throw new IndexOutOfBoundsException();
			}
			
			@Override
			protected Object getValue(int index)
			{
				throw new IndexOutOfBoundsException();
			}
			
			@Override
			protected Object setValue(int index, Object value)
			{
				throw new IndexOutOfBoundsException();
			}
			
			@Override
			protected int getPropertyCount()
			{
				return 0;
			}
		};
	}
	
	// =========================================================================
	// Implementation of Locatable interface
	
	@Override
	public final boolean hasLocation()
	{
		return location != null;
	}
	
	@Override
	public final xtc.tree.Location getLocation()
	{
		return location.toXtcLocation();
	}
	
	@Override
	public final void setLocation(xtc.tree.Location location)
	{
		setNativeLocation(new Location(location));
	}
	
	@Override
	public final void setLocation(Locatable locatable)
	{
		if (locatable.hasLocation())
			setLocation(locatable.getLocation());
	}
	
	// =========================================================================
	
	public final Location getNativeLocation()
	{
		return location;
	}
	
	public final void setNativeLocation(Location location)
	{
		this.location = location;
	}
	
	// =========================================================================
	// Implementation of the List interface
	
	@Override
	public int size()
	{
		return 0;
	}
	
	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}
	
	@Override
	public boolean contains(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Iterator<AstNode> iterator()
	{
		return new ChildIterator();
	}
	
	@Override
	public Object[] toArray()
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T[] toArray(T[] a)
	{
		throw new UnsupportedOperationException();
	}
	
	// Modification Operations
	
	@Override
	public boolean add(AstNode e)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	// Bulk Modification Operations
	
	@Override
	public boolean containsAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(Collection<? extends AstNode> c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends AstNode> c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}
	
	// Comparison and hashing
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return equals((AstNode) obj);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + propertyHash();
		result = prime * result + childrenHash();
		return result;
	}
	
	// Positional Access Operations
	
	@Override
	public AstNode get(int index)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public AstNode set(int index, AstNode value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void add(int index, AstNode element)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public AstNode remove(int index)
	{
		throw new UnsupportedOperationException();
	}
	
	// Search Operations
	
	@Override
	public int indexOf(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int lastIndexOf(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	// List Iterators
	
	@Override
	public ListIterator<AstNode> listIterator()
	{
		return new ChildListIterator();
	}
	
	@Override
	public ListIterator<AstNode> listIterator(int index)
	{
		return new ChildListIterator(index);
	}
	
	// View
	
	@Override
	public List<AstNode> subList(int fromIndex, int toIndex)
	{
		throw new UnsupportedOperationException();
	}
	
	// =========================================================================
	// Extension of the List interface for Pair
	
	/**
	 * Appends all items from the given list to list of children.
	 * 
	 * @return Returns <code>true</code> if the list of children has changed.
	 */
	public boolean addAll(Pair<? extends AstNode> p)
	{
		throw new UnsupportedOperationException();
	}
	
	// =========================================================================
	// Introspection
	
	/**
	 * Determine whether this node can have a variable number of children and
	 * implements the {@link List} interface (or parts of it).
	 */
	public boolean isList()
	{
		return false;
	}
	
	/**
	 * Returns the names of the children. This method may only be called for
	 * nodes with a fixed number of children (isList() returns false).
	 */
	public String[] getChildNames()
	{
		return EMPTY_CHILD_NAMES;
	}
	
	// =========================================================================
	
	public void toString(Appendable out) throws IOException
	{
		out.append(getClass().getSimpleName());
		out.append('(');
		
		boolean first = true;
		for (AstNode node : this)
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
		
		out.append(')');
	}
	
	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		
		try
		{
			toString(buf);
		}
		catch (IOException x)
		{
			assert false;
		}
		
		return buf.toString();
	}
	
	// =========================================================================
	
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		AstNode n = (AstNode) super.clone();
		n.location = new Location(n.location);
		n.attributes = new HashMap<String, Object>(n.attributes);
		return n;
	}
	
	// =========================================================================
	
	/**
	 * @deprecated
	 */
	public void serializeTo(AstNodeOutputStream os) throws IOException
	{
		defaultSerializeTo(os);
	}
	
	/**
	 * @deprecated
	 */
	public void deserializeFrom(AstNodeInputStream is) throws IOException, ClassNotFoundException
	{
		defaultDeserializeFrom(is);
	}
	
	/**
	 * @deprecated
	 */
	protected final void defaultSerializeTo(AstNodeOutputStream os) throws IOException
	{
		os.writeObject(location);
		serializeAttributes(os);
		serializeProperties(os);
		serializeChildren(os);
	}
	
	/**
	 * @deprecated
	 */
	protected final void defaultDeserializeFrom(AstNodeInputStream is) throws IOException, ClassNotFoundException
	{
		location = (Location) is.readObject();
		deserializeAttributes(is);
		deserializeProperties(is);
		deserializeChildren(is);
	}
	
	/**
	 * @deprecated
	 */
	protected final void serializeAttributes(AstNodeOutputStream os) throws IOException
	{
		if (attributes != null && !attributes.isEmpty())
		{
			@SuppressWarnings("unchecked")
			Entry<String, Object>[] props =
			        (Entry<String, Object>[]) attributes.entrySet().toArray(
			                new Entry[0]);
			
			Arrays.sort(props, new Comparator<Entry<String, Object>>()
			{
				@Override
				public int compare(Entry<String, Object> o1, Entry<String, Object> o2)
				{
					return o1.getKey().compareTo(o2.getKey());
				}
			});
			
			os.writeInt(props.length);
			for (Entry<String, Object> prop : props)
			{
				os.writeUTF(prop.getKey());
				os.writeObject(prop.getValue());
			}
		}
		else
		{
			os.writeInt(0);
		}
	}
	
	/**
	 * @deprecated
	 */
	protected final void serializeProperties(AstNodeOutputStream os) throws IOException
	{
		AstNodePropertyIterator i = propertyIterator();
		while (i.next())
			os.writeObject(i.getValue());
	}
	
	/**
	 * @deprecated
	 */
	protected final void serializeChildren(AstNodeOutputStream os) throws IOException
	{
		os.writeInt(size());
		for (AstNode child : this)
			os.writeNode(child);
	}
	
	/**
	 * @deprecated
	 */
	protected final void deserializeAttributes(AstNodeInputStream is) throws IOException, ClassNotFoundException
	{
		attributes = null;
		
		int propCount = is.readInt();
		for (int i = 0; i < propCount; ++i)
		{
			String key = is.readUTF();
			Object value = is.readObject();
			setAttribute(key, value);
		}
	}
	
	/**
	 * @deprecated
	 */
	protected final void deserializeProperties(AstNodeInputStream is) throws IOException, ClassNotFoundException
	{
		AstNodePropertyIterator i = propertyIterator();
		while (i.next())
			i.setValue(is.readObject());
	}
	
	/**
	 * @deprecated
	 */
	protected final void deserializeChildren(AstNodeInputStream is) throws IOException, ClassNotFoundException
	{
		int size = is.readInt();
		if (isList())
		{
			for (int i = 0; i < size; ++i)
				add(is.readNode());
		}
		else
		{
			for (int i = 0; i < size; ++i)
				set(i, is.readNode());
		}
	}
	
	// =========================================================================
	
	public final int propertyHash()
	{
		int hash = 0;
		AstNodePropertyIterator i = propertyIterator();
		while (i.next())
		{
			hash += i.getName().hashCode();
			hash += ((i.getValue() == null) ? 0 : i.getValue().hashCode());
		}
		return hash;
	}
	
	public final int childrenHash()
	{
		int hash = 0;
		Iterator<AstNode> i = iterator();
		while (i.hasNext())
		{
			AstNode n = i.next();
			hash += (n == null) ? 0 : n.hashCode();
		}
		return hash;
	}
	
	public final boolean equals(AstNode other)
	{
		// Check location
		if (location == null)
		{
			if (other.location != null)
				return false;
		}
		else if (!location.equals(other.location))
			return false;
		
		// Check attributes
		if (attributes == null)
		{
			if (other.attributes != null)
				return false;
		}
		else if (!attributes.equals(other.attributes))
			return false;
		
		// Check properties
		AstNodePropertyIterator p1 = propertyIterator();
		AstNodePropertyIterator p2 = other.propertyIterator();
		while (p1.next() & p2.next()) // Don't short-circuit!
		{
			Object v1 = p1.getValue();
			Object v2 = p2.getValue();
			if (v1 == null)
			{
				if (v2 != null)
					return false;
			}
			else if (!v1.equals(v2))
				return false;
		}
		// Nodes of the same type have the same set of properties
		
		// Check children
		Iterator<AstNode> i1 = iterator();
		Iterator<AstNode> i2 = other.iterator();
		while (i1.hasNext() && i2.hasNext())
		{
			AstNode n1 = i1.next();
			AstNode n2 = i2.next();
			if (n1 == null)
			{
				if (n2 != null)
					return false;
			}
			else if (!n1.equals(n2))
				return false;
		}
		if (i1.hasNext() != i2.hasNext())
			return false;
		
		return true;
	}
	
	// =========================================================================
	
	private final class ChildIterator
	        implements
	            Iterator<AstNode>
	{
		final protected int size = size();
		
		protected int cursor = 0;
		
		@Override
		public boolean hasNext()
		{
			return cursor < size;
		}
		
		@Override
		public AstNode next()
		{
			if (hasNext())
				return get(cursor++);
			throw new NoSuchElementException();
		}
		
		@Override
		public void remove()
		{
			throw new UnsupportedOperationException(
			        "remove() not implemented for this iterator");
		}
	}
	
	// =========================================================================
	
	private final class ChildListIterator
	        implements
	            AstChildIterator
	{
		final protected int size = size();
		
		protected int cursor;
		
		protected int current;
		
		protected int start;
		
		public ChildListIterator()
		{
			start = 0;
			cursor = 0;
			current = -1;
		}
		
		public ChildListIterator(int index)
		{
			start = index;
			cursor = index;
			current = -1;
		}
		
		@Override
		public void reset()
		{
			cursor = start;
			current = -1;
		}
		
		@Override
		public void set(AstNode e)
		{
			if (current == -1)
				throw new IllegalStateException();
			AstNode.this.set(current, e);
		}
		
		@Override
		public AstNode get()
		{
			if (current == -1)
				throw new IllegalStateException();
			return AstNode.this.get(current);
		}
		
		@Override
		public boolean hasPrevious()
		{
			return cursor > start;
		}
		
		@Override
		public AstNode previous()
		{
			if (!hasPrevious())
				throw new NoSuchElementException();
			current = --cursor;
			return AstNode.this.get(current);
		}
		
		@Override
		public int previousIndex()
		{
			return cursor - 1;
		}
		
		@Override
		public boolean hasNext()
		{
			return cursor < size;
		}
		
		@Override
		public int nextIndex()
		{
			return cursor;
		}
		
		@Override
		public AstNode next()
		{
			if (!hasNext())
				throw new NoSuchElementException();
			current = cursor++;
			return AstNode.this.get(current);
		}
		
		@Override
		public void add(AstNode e)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}
