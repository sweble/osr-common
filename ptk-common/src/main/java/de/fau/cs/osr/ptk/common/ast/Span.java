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

import java.io.Serializable;

import xtc.tree.Location;
import de.fau.cs.osr.utils.StringUtils;

public final class Span
		implements
			Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final String content;
	
	private final AstLocation from;
	
	private final AstLocation to;
	
	// =========================================================================
	
	public Span()
	{
		content = null;
		from = new AstLocation("Unknown file", -1, -1);
		to = from;
	}
	
	public Span(AstLocation from, AstLocation to, String content)
	{
		this.content = content;
		this.from = from;
		this.to = to;
	}
	
	public Span(AstLocation from, String content)
	{
		this.content = content;
		this.from = from;
		this.to = from;
	}
	
	public Span(Location from, Location to, String content)
	{
		this.content = content;
		this.from = new AstLocation(from);
		this.to = new AstLocation(to);
	}
	
	public Span(Location from, String content)
	{
		this.content = content;
		this.from = new AstLocation(from);
		this.to = this.from;
	}
	
	// =========================================================================
	
	public String getContent()
	{
		return content;
	}
	
	public AstLocation getFrom()
	{
		return from;
	}
	
	public AstLocation getTo()
	{
		return to;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		Span other = (Span) obj;
		if (content == null)
		{
			if (other.content != null)
				return false;
		}
		else if (!content.equals(other.content))
			return false;
		if (from == null)
		{
			if (other.from != null)
				return false;
		}
		else if (!from.equals(other.from))
			return false;
		if (to == null)
		{
			if (other.to != null)
				return false;
		}
		else if (!to.equals(other.to))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		
		if (from == null)
		{
			buf.append("<no location>");
		}
		else
		{
			String file = from.getFile();
			if (file == null || file.isEmpty())
				file = "<no file>";
			
			buf.append(file);
			buf.append(':');
			buf.append(from.getLine());
			buf.append(':');
			buf.append(from.getColumn());
			if (to != null && to != from)
			{
				buf.append(" - ");
				if (from.getLine() != to.getLine())
				{
					buf.append(to.getLine());
					buf.append(':');
				}
				buf.append(to.getColumn());
			}
		}
		
		if (content != null)
		{
			buf.append(": \"");
			buf.append(StringUtils.escJava(StringUtils.crop(content, 32)));
			buf.append('"');
		}
		else
		{
			buf.append(": <no content>");
		}
		
		return buf.toString();
	}
}
