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

public abstract class VisitorBase<T>
		extends
			VisitorInterface<T>
{
	private final VisitorLogic<T> logic;

	// =========================================================================

	public VisitorBase()
	{
		this.logic = new VisitorLogic<T>(this);
	}

	public VisitorBase(VisitorLogic<T> logic)
	{
		this.logic = logic;
	}

	// =========================================================================

	@Override
	protected abstract Object dispatch(T node, Object result);

	/**
	 * Called before the visitation starts.
	 *
	 * @param node
	 *            The node at which the visitation will start.
	 * @return Always returns <code>true</code>. If an overridden version of
	 *         this method returns <code>false</code> the visitation will be
	 *         aborted.
	 */
	@Override
	protected T before(T node)
	{
		return node;
	}

	/**
	 * Called after the visitation has finished. This method will not be called
	 * if before() returned <code>null</code>.
	 *
	 * @param node
	 *            The node at which the visitation started.
	 * @param result
	 *            The result of the visitation. If the visit() method for the
	 *            given node doesn't return a value, <code>null</code> is
	 *            returned.
	 * @return Returns the result parameter.
	 */
	@Override
	protected Object after(T node, Object result)
	{
		return result;
	}

	/**
	 * This method is called if no suitable visit() method could be found. If
	 * not overridden, this method will throw an UnvisitableException.
	 *
	 * @param node
	 *            The node that should have been visited.
	 * @return The result of the visitation.
	 */
	@Override
	protected Object visitNotFound(T node)
	{
		throw new VisitNotFoundException(this, node);
	}

	@Override
	protected Object handleVisitingException(T node, Throwable cause)
	{
		throw new VisitingException(node, cause);
	}

	// =========================================================================

	/**
	 * Start visitation at the given node.
	 *
	 * @param node
	 *            The node at which the visitation will start.
	 * @return The result of the visitation. If the visit() method for the given
	 *         node doesn't return a value, <code>null</code> is returned.
	 */
	public Object go(T node)
	{
		Object result = before(node);
		if (node==null)
			return null;

		result = dispatch(node, result);
		return after(node, result);
	}

	// =========================================================================

	protected final Object resolveAndVisit(T node, Object result)
	{
		return logic.resolveAndVisit(node, result);
	}
}
