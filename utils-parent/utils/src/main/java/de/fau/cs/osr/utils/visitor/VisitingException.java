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

/**
 * An exception that is thrown by a visitor if a visit() method threw an
 * exception. It can also be thrown from within a visit() method to wrap an
 * exception that would have to be caught and handled otherwise. If thrown from
 * within a visit() method, the visitor will intercept and re-throw the
 * exception.
 */
public class VisitingException
		extends
			RuntimeException
{
	private static final long serialVersionUID = 1L;

	private final Object node;

	public VisitingException(Throwable cause)
	{
		super(cause);
		this.node = null;
	}

	public VisitingException(String message, Throwable cause)
	{
		super(message, cause);
		this.node = null;
	}

	public VisitingException(Object node, Throwable cause)
	{
		super(cause);
		this.node = node;
	}

	public VisitingException(Object node, String message, Throwable cause)
	{
		super(message, cause);
		this.node = node;
	}

	public Object getNode()
	{
		return node;
	}
}
