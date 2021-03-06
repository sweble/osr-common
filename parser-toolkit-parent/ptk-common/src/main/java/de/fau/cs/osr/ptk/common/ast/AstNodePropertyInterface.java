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

public interface AstNodePropertyInterface
{
	public boolean hasProperties();

	public int getPropertyCount();

	public Object getProperty(String name);

	public Object getProperty(String name, Object default_);

	public boolean hasProperty(String name);

	public Object setProperty(String name, Object value);

	/**
	 * Return an iterator which iterates over this node's properties. Though
	 * order is not important for properties, this method <b>must always</b>
	 * iterate through the properties in the <b>exact same order</b>.
	 * 
	 * @return An iterator which iterates over this node's properties.
	 */
	public AstNodePropertyIterator propertyIterator();
}
