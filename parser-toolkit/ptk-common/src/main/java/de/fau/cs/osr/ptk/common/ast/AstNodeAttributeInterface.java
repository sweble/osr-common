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

import java.util.Map;

public interface AstNodeAttributeInterface
{
	/**
	 * Tell whether this node has any attributes attached.
	 * 
	 * @return <code>True</code> if this node has any attributes attached,
	 *         otherwise <code>false</code>.
	 */
	public abstract boolean hasAttributes();
	
	/**
	 * Retrieve the attributes as an unmodifiable map.
	 * 
	 * @return The map of attributes attached to this node.
	 */
	public abstract Map<String, Object> getAttributes();
	
	/**
	 * Set the attributes for this node.
	 * 
	 * @param attrs
	 *            The attributes to attach to this node.
	 */
	public void setAttributes(Map<String, Object> attrs);
	
	/**
	 * Removes all attributes from this node.
	 */
	public void clearAttributes();
	
	// =========================================================================
	
	/**
	 * Check whether a certain attribute is attached to this node.
	 * 
	 * @param name
	 *            The name of the attribute to test.
	 * 
	 * @return <code>True</code> if an attribute with the respective name is
	 *         attached to this node, <code>false</code> otherwise.
	 */
	public abstract boolean hasAttribute(String name);
	
	/**
	 * Retrieve the value of an attribute.
	 * 
	 * @param name
	 *            The name of the attribute to retrieve.
	 * 
	 * @return The value of the respective attribute or <code>null</code> if the
	 *         attribute does not exist.
	 */
	public abstract Object getAttribute(String name);
	
	/**
	 * Assign an attribute to this node. Attaches a name associated with an
	 * object to this node.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The value associated with the attribute.
	 * 
	 * @return Returns the value previously associated with the attribute or
	 *         null if the attribute does not exist yet.
	 */
	public abstract Object setAttribute(String name, Object value);
	
	/**
	 * Remove an attribute from this node.
	 * 
	 * @param name
	 *            The name of the attribute to remove.
	 * 
	 * @return The value of the removed attribute.
	 */
	public abstract Object removeAttribute(String name);
	
	// =========================================================================
	
	/**
	 * Retrieve the value of an integer attribute.
	 * 
	 * @param name
	 *            The name of the integer attribute to retrieve.
	 * 
	 * @return The integer value of the respective attribute or <code>0</code>
	 *         if the attribute does not exist.
	 */
	public abstract int getIntAttribute(String name);
	
	/**
	 * Assign an integer attribute to this node. Attaches a name associated with
	 * an integer object to this node.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The integer value associated with the attribute.
	 * 
	 * @return Returns the value previously associated with the attribute or
	 *         null if the attribute does not exist yet.
	 */
	public abstract Integer setIntAttribute(String name, Integer value);
	
	/**
	 * Retrieve the value of a boolean attribute.
	 * 
	 * @param name
	 *            The name of the boolean attribute to retrieve.
	 * 
	 * @return The boolean value of the respective attribute or
	 *         <code>false</code> if the attribute does not exist.
	 */
	public abstract boolean getBooleanAttribute(String name);
	
	/**
	 * Assign a boolean attribute to this node. Attaches a name associated with
	 * a boolean object to this node.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The boolean value associated with the attribute.
	 * 
	 * @return Returns the value previously associated with the attribute or
	 *         <code>null</code> if the attribute does not exist yet.
	 */
	public abstract boolean setBooleanAttribute(String name, boolean value);
	
	/**
	 * Retrieve the value of a string attribute.
	 * 
	 * @param name
	 *            The name of the string attribute to retrieve.
	 * 
	 * @return The string value of the respective attribute or <code>null</code>
	 *         if the attribute does not exist.
	 */
	public abstract String getStringAttribute(String name);
	
	/**
	 * Assign an string attribute to this node. Attaches a name associated with
	 * an String object to this node.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The String value associated with the attribute.
	 * 
	 * @return Returns the value previously associated with the attribute or
	 *         null if the attribute does not exist yet.
	 */
	public abstract String setStringAttribute(String name, String value);
}
