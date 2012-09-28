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

package de.fau.cs.osr.ptk.common.jxpath;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

import de.fau.cs.osr.ptk.common.ast.AstNodeInterface;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.NoSuchPropertyException;

public class AstPropertyIterator
        implements
            NodeIterator
{
	private NodePointer parent;
	
	private Property[] properties;
	
	private int position = 0;
	
	// =========================================================================
	
	public AstPropertyIterator(NodePointer parent, QName qname)
	{
		this.parent = parent;
		
		AstNodeInterface node = (AstNodeInterface) parent.getNode();
		
		properties = null;
		if (qname.getPrefix() == null)
		{
			String name = qname.getName();
			if (name.equals("*"))
			{
				Map<String, Object> props = node.getAttributes();
				
				int size = node.getPropertyCount() + props.size();
				properties = new Property[size];
				
				int i = 0;
				
				AstNodePropertyIterator j = node.propertyIterator();
				while (j.next())
					properties[i++] = new Property(node, j.getName(), j.getValue());
				
				for (Entry<String, Object> prop : props.entrySet())
					properties[i++] = new Property(node, prop);
			}
			else
			{
				Object value = null;
				try
				{
					value = node.getProperty(name);
				}
				catch (NoSuchPropertyException e)
				{
					value = node.getAttribute(name);
				}
				
				if (value != null)
					properties = new Property[] { new Property(node, name, value) };
			}
		}
		
		if (properties == null)
			properties = new Property[0];
		
		//debug("AstPropertyIterator", qname, Arrays.toString(properties));
	}
	
	@Override
	public NodePointer getNodePointer()
	{
		Property prop = null;
		try
		{
			if (position == 0)
			{
				if (!setPosition(1))
					return null;
			}
			
			prop = properties[position - 1];
			return new AstPropertyPointer(parent, prop);
		}
		finally
		{
			//debug("getNodePointer", position, prop);
		}
	}
	
	@Override
	public int getPosition()
	{
		//debug("getPosition", position);
		return position;
	}
	
	@Override
	public boolean setPosition(int position)
	{
		try
		{
			if (position >= 1 && position <= properties.length)
			{
				this.position = position;
				return true;
			}
			else
				return false;
		}
		finally
		{
			//debug("setPosition", position, this.position, properties.length);
		}
	}
	
	// =========================================================================
	
	public static final class Property
	{
		private final AstNodeInterface owner;
		
		private final String name;
		
		private final Object value;
		
		public Property(AstNodeInterface owner, String name, Object value)
		{
			this.owner = owner;
			this.name = name;
			this.value = value;
		}
		
		public Property(AstNodeInterface owner, Entry<String, Object> entry)
		{
			this.owner = owner;
			this.name = entry.getKey();
			this.value = entry.getValue();
		}
		
		public String getName()
		{
			return name;
		}
		
		public Object getValue()
		{
			return value;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((owner == null) ? 0 : owner.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Property other = (Property) obj;
			if (name == null)
			{
				if (other.name != null)
					return false;
			}
			else if (!name.equals(other.name))
				return false;
			if (owner == null)
			{
				if (other.owner != null)
					return false;
			}
			else if (!owner.equals(other.owner))
				return false;
			if (value == null)
			{
				if (other.value != null)
					return false;
			}
			else if (!value.equals(other.value))
				return false;
			return true;
		}
		
		@Override
		public String toString()
		{
			return "AstNodeProperty [name=" + name + ", value=" + value + "]";
		}
	}
	
	// =========================================================================
	
	/*
	private void debug(String where, Object... params)
	{
		StringBuilder b = new StringBuilder();
		
		String path = parent.asPath();
		b.append(String.format(
		        "@%8x : %s : %s%s.%s ; ",
		        System.identityHashCode(this),
		        path,
		        StringUtils.strrep(' ', 28 - path.length()),
		        getClass().getSimpleName(),
		        where));
		
		int i = 0;
		for (Object o : params)
		{
			if (i++ != 0)
				b.append(", ");
			b.append(o == null ? "null" : o.toString());
		}
		
		System.out.println(b.toString());
	}
	*/
}
