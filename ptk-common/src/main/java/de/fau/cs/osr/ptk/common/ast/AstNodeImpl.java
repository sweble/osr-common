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
import java.util.AbstractList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import xtc.tree.Locatable;
import xtc.tree.Location;
import xtc.util.Pair;
import de.fau.cs.osr.utils.WrappedException;

/**
 * The parent node of all AST (abstract/attributed syntax tree) nodes.
 * 
 * FIXME: Get rid of Locatable interface somehow ...
 */
public abstract class AstNodeImpl<T extends AstNode<T>>
		extends
			AbstractList<T>
		implements
			AstNode<T>
{
	private static final long serialVersionUID = 3333532331617925714L;
	
	// =========================================================================
	
	private HashMap<String, Object> attributes;
	
	private AstLocation location;
	
	// =========================================================================
	
	protected AstNodeImpl()
	{
	}
	
	protected AstNodeImpl(AstLocation location)
	{
		setNativeLocation(location);
	}
	
	protected AstNodeImpl(Location location)
	{
		setLocation(location);
	}
	
	// =========================================================================
	
	/**
	 * Returns an integer value that identifies the node type. It's the
	 * programmers responsibility to make sure these values are unique.
	 */
	@Override
	public int getNodeType()
	{
		return NT_UNTYPED;
	}
	
	/**
	 * Returns <code>true</code> if the given node type equals the node type
	 * returned by getNodeType().
	 */
	@Override
	public boolean isNodeType(int testType)
	{
		return getNodeType() == testType;
	}
	
	/**
	 * Returns the fully qualified name of this node's class.
	 */
	@Override
	public final String getNodeTypeName()
	{
		return getClass().getName();
	}
	
	/**
	 * Returns the name of this node. The name is the simple name of the node's
	 * class.
	 */
	@Override
	public String getNodeName()
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
	public Object getProperty(String name, Object default_)
	{
		AstNodePropertyIterator i = propertyIterator();
		while (i.next())
		{
			if (i.getName().equals(name))
				return i.getValue();
		}
		return default_;
	}
	
	@Override
	public boolean hasProperty(String name)
	{
		AstNodePropertyIterator i = propertyIterator();
		while (i.next())
		{
			if (i.getName().equals(name))
				return true;
		}
		return false;
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
	public final Location getLocation()
	{
		if (location == null)
			return null;
		return location.toXtcLocation();
	}
	
	@Override
	public final void setLocation(Location location)
	{
		setNativeLocation(new AstLocation(location));
	}
	
	@Override
	public final void setLocation(Locatable locatable)
	{
		if (locatable.hasLocation())
			setLocation(locatable.getLocation());
	}
	
	// =========================================================================
	
	@Override
	public final AstLocation getNativeLocation()
	{
		return location;
	}
	
	@Override
	public final void setNativeLocation(AstLocation location)
	{
		this.location = location;
	}
	
	// =========================================================================
	// Implementation of the AbstractList interface
	
	@Override
	public int size()
	{
		return 0;
	}
	
	@Override
	public T get(int index)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <S extends T> S get(int index, Class<S> clazz)
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
	@Override
	public boolean addAll(Pair<? extends T> p)
	{
		throw new UnsupportedOperationException();
	}
	
	// =========================================================================
	// Introspection
	
	/**
	 * Determine whether this node can have a variable number of children and
	 * implements the {@link List} interface (or parts of it).
	 */
	@Override
	public boolean isList()
	{
		return false;
	}
	
	/**
	 * Returns the names of the children. This method may only be called for
	 * nodes with a fixed number of children (isList() returns false).
	 */
	@Override
	public String[] getChildNames()
	{
		return EMPTY_CHILD_NAMES;
	}
	
	// =========================================================================
	
	@Override
	public void toString(Appendable out) throws IOException
	{
		out.append(getClass().getSimpleName());
		out.append('(');
		
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
	public Object clone() throws CloneNotSupportedException
	{
		@SuppressWarnings("unchecked")
		AstNodeImpl<T> n = (AstNodeImpl<T>) super.clone();
		
		// not necessary, Location is immutable
		//n.location = new Location(n.location);
		
		if (n.attributes != null)
			n.attributes = new HashMap<String, Object>(n.attributes);
		
		if (hasProperties())
		{
			AstNodePropertyIterator i = propertyIterator();
			AstNodePropertyIterator j = n.propertyIterator();
			while (i.next() & j.next())
				// don't short-circuit!
				j.setValue(i.getValue());
		}
		
		return n;
	}
	
	@SuppressWarnings("unchecked")
	public AstNodeImpl<T> cloneWrapException()
	{
		try
		{
			return (AstNodeImpl<T>) this.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new WrappedException(e);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public AstNodeImpl<T> deepClone() throws CloneNotSupportedException
	{
		AstNodeImpl<T> n = (AstNodeImpl<T>) clone();
		
		if (isList())
		{
			Iterator<T> i = iterator();
			while (i.hasNext())
				n.add((T) i.next().deepClone());
		}
		else
		{
			for (int i = 0; i < size(); ++i)
				n.set(i, (T) get(i).deepClone());
		}
		
		return n;
	}
	
	public AstNodeImpl<T> deepCloneWrapException()
	{
		try
		{
			return this.deepClone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new WrappedException(e);
		}
	}
	
	// =========================================================================
	
	/* hashCode is omitted intentionally. It's hard to implement a meaningful
	 * hashCode method that does NOT recurse into the subtree. But recursing the
	 * whole subtree defeats the purpose of a hash function, which should be
	 * fast to allow quick look-ups.
	 */
	
	@Override
	public boolean equals(Object obj)
	{
		// Type checking
		if (this == obj)
			return true;
		/*
		if (!super.equals(obj))
			return false;
		*/
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		AstNodeImpl<T> other = (AstNodeImpl<T>) obj;
		
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
		Iterator<T> i1 = iterator();
		Iterator<T> i2 = other.iterator();
		while (i1.hasNext() && i2.hasNext())
		{
			T n1 = i1.next();
			T n2 = i2.next();
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
}
