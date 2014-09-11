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

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;

import de.fau.cs.osr.ptk.common.jxpath.AstPropertyIterator.Property;

public class AstPropertyPointer
		extends
			NodePointer
{
	private static final long serialVersionUID = 1L;
	
	private Property property;
	
	// =========================================================================
	
	public AstPropertyPointer(NodePointer parent, Property prop)
	{
		super(parent);
		this.property = prop;
		
		//debug("AstPropertyPointer");
	}
	
	// =========================================================================
	
	@Override
	public boolean isLeaf()
	{
		return true;
	}
	
	@Override
	public boolean isActual()
	{
		return true;
	}
	
	@Override
	public boolean isCollection()
	{
		return false;
	}
	
	@Override
	public int getLength()
	{
		return 1;
	}
	
	@Override
	public QName getName()
	{
		return new QName(null, getName2());
	}
	
	@Override
	public Object getBaseValue()
	{
		return getImmediateNode();
	}
	
	@Override
	public Object getImmediateNode()
	{
		return property;
	}
	
	@Override
	public Object getValue()
	{
		return property.getValue();
	}
	
	@Override
	public boolean testNode(NodeTest nodeTest)
	{
		//debug("testNode", nodeTest);
		
		if (nodeTest != null)
		{
			if (nodeTest instanceof NodeTypeTest)
			{
				NodeTypeTest nodeTypeTest = (NodeTypeTest) nodeTest;
				return nodeTypeTest.getNodeType() == Compiler.NODE_TYPE_NODE;
			}
		}
		
		return false;
	}
	
	@Override
	public String asPath()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(parent.asPath());
		if (buffer.length() == 0 || buffer.charAt(buffer.length() - 1) != '/')
			buffer.append('/');
		
		buffer.append('@');
		buffer.append(getName2());
		
		return buffer.toString();
	}
	
	@Override
	public int hashCode()
	{
		return property.hashCode();
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
		AstPropertyPointer other = (AstPropertyPointer) obj;
		if (!property.equals(other.property))
			return false;
		return true;
	}
	
	@Override
	public void setValue(Object value)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public int compareChildNodePointers(
			NodePointer pointer1,
			NodePointer pointer2)
	{
		throw new UnsupportedOperationException(
				"Should not happen! Attributes don't have children.");
	}
	
	String getName2()
	{
		return property.getName();
	}
	
	// =========================================================================
	
	/*
	private void debug(String where, Object... params)
	{
		StringBuilder b = new StringBuilder();
		
		String path = asPath();
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
