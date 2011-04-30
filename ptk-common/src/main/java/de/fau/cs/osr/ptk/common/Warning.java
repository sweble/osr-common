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

package de.fau.cs.osr.ptk.common;

import java.io.Serializable;

import de.fau.cs.osr.ptk.common.ast.Span;

public abstract class Warning
        implements
            Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final Span span;
	
	private final String origin;
	
	private final String message;
	
	// =========================================================================
	
	public Warning()
	{
		this(new Span(),
		        "Unknown origin",
		        "Unknown warning.");
	}
	
	public Warning(Span span, String origin, String message)
	{
		this.span = span;
		this.origin = origin;
		this.message = message;
	}
	
	public Warning(Span span, Class<?> origin, String message)
	{
		this(span, origin.getSimpleName(), message);
	}
	
	// =========================================================================
	
	public Span getSpan()
	{
		return span;
	}
	
	public String getOrigin()
	{
		return origin;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	// =========================================================================
	
	@Override
	public String toString()
	{
		String span = spanToString();
		String message = messageToString();
		return "Warning: " + span + " : " + message;
	}
	
	protected String spanToString()
	{
		String span = "<no location>";
		if (this.span != null)
			span = this.span.toString();
		return span;
	}
	
	protected String messageToString()
	{
		String message = this.message;
		if (message == null || message.isEmpty())
			message = "<no message>";
		
		switch (message.charAt(message.length() - 1))
		{
			case '.':
			case '?':
			case '!':
				break;
			default:
				message += ".";
		}
		return message;
	}
	
	// =========================================================================
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((span == null) ? 0 : span.hashCode());
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
		Warning other = (Warning) obj;
		if (message == null)
		{
			if (other.message != null)
				return false;
		}
		else if (!message.equals(other.message))
			return false;
		if (origin == null)
		{
			if (other.origin != null)
				return false;
		}
		else if (!origin.equals(other.origin))
			return false;
		if (span == null)
		{
			if (other.span != null)
				return false;
		}
		else if (!span.equals(other.span))
			return false;
		return true;
	}
}
