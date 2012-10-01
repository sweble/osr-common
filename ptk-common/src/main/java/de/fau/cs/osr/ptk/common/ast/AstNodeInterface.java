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
import java.util.List;

import xtc.tree.Locatable;
import xtc.util.Pair;

public interface AstNodeInterface<T extends AstNodeInterface<T>>
		extends
			AstNodeAttributeInterface,
			AstNodePropertyInterface,
			Locatable,
			Cloneable,
			Serializable,
			List<T>
{
	
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
	
	/**
	 * Returns an integer value that identifies the node type. It's the
	 * programmers responsibility to make sure these values are unique.
	 */
	public int getNodeType();
	
	/**
	 * Returns <code>true</code> if the given node type equals the node type
	 * returned by getNodeType().
	 */
	public boolean isNodeType(int testType);
	
	/**
	 * Returns the fully qualified name of this node's class.
	 */
	public String getNodeTypeName();
	
	/**
	 * Returns the name of this node. The name is the simple name of the node's
	 * class.
	 */
	public String getNodeName();
	
	// =========================================================================
	// native location
	
	public Location getNativeLocation();
	
	public void setNativeLocation(Location location);
	
	// =========================================================================
	
	/**
	 * Appends all items from the given list to list of children.
	 * 
	 * @return Returns <code>true</code> if the list of children has changed.
	 */
	public boolean addAll(Pair<? extends T> p);
	
	/**
	 * Determine whether this node can have a variable number of children and
	 * implements the {@link List} interface (or parts of it).
	 */
	public boolean isList();
	
	/**
	 * Returns the names of the children. This method may only be called for
	 * nodes with a fixed number of children (isList() returns false).
	 */
	public String[] getChildNames();
	
	// =========================================================================
	// Object
	
	public void toString(Appendable out) throws IOException;
	
	public boolean equals(Object obj);
	
	// =========================================================================
	
	public Object clone() throws CloneNotSupportedException;
	
	public AstNodeInterface<T> cloneWrapException();
	
	/**
	 * Only children and children's children are cloned by this method. Property
	 * and attribute values are just copied.
	 */
	public AstNodeInterface<T> deepClone() throws CloneNotSupportedException;
	
	public AstNodeInterface<T> deepCloneWrapException();
	
}
