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
package de.fau.cs.osr.utils.visitor;

public abstract class StackedVisitorInterface<T>
{
	/**
	 * Called before the visitation starts.
	 * 
	 * @param node
	 *            The node at which the visitation will start.
	 * @return Always returns the node. If an overridden version of
	 *         this method returns <code>null</code> the visitation will be
	 *         aborted.
	 */
	protected T before(T node)
	{
		return node;
	}
	
	/**
	 * Called after the visitation has finished. This method will not be called
	 * if before() returned false.
	 * 
	 * @param node
	 *            The node at which the visitation started.
	 * @param result
	 *            The result of the visitation. If the visit() method for the
	 *            given node doesn't return a value, <code>null</code> is
	 *            returned.
	 * @return Returns the result parameter.
	 */
	protected Object after(T node, Object result)
	{
		return result;
	}
}
