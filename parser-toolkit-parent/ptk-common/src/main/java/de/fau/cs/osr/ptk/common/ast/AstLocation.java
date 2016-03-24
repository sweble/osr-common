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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import de.fau.cs.osr.utils.WrappedException;

public class AstLocation
		implements
			Serializable
{
	private static final long serialVersionUID = 1L;

	private final String file;

	private final int line;

	private final int column;

	// =========================================================================

	public AstLocation()
	{
		this.file = "";
		this.line = -1;
		this.column = -1;
	}

	public AstLocation(String file, int line, int column)
	{
		this.file = file;
		this.line = line;
		this.column = column;
	}

	public AstLocation(AstLocation location)
	{
		this.file = location.file;
		this.line = location.line;
		this.column = location.column;
	}

	public AstLocation(xtc.tree.Location location)
	{
		this.file = location.file;
		this.line = location.line;
		this.column = location.column;
	}

	// =========================================================================

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + line;
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
		AstLocation other = (AstLocation) obj;
		if (column != other.column)
			return false;
		if (file == null)
		{
			if (other.file != null)
				return false;
		}
		else if (!file.equals(other.file))
			return false;
		if (line != other.line)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		String file = getFile();
		if (file == null)
			return getLine() + ":" + getColumn();

		try
		{
			return URLEncoder.encode(file, "UTF-8") + ":" + getLine() + ":" + getColumn();
		}
		catch (UnsupportedEncodingException e)
		{
			throw new WrappedException(e);
		}
	}

	public static AstLocation valueOf(String s)
	{
		int line;
		int column;
		String file = null;

		int i = s.indexOf(':');
		if (i == -1)
			return null;

		int j = s.indexOf(':', i + 1);
		if (j == -1)
		{
			line = Integer.parseInt(s.substring(0, i));
			column = Integer.parseInt(s.substring(i + 1));
		}
		else
		{
			try
			{
				file = URLDecoder.decode(s.substring(0, i), "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				throw new WrappedException(e);
			}
			line = Integer.parseInt(s.substring(i + 1, j));
			column = Integer.parseInt(s.substring(j + 1));
		}

		return new AstLocation(file, line, column);
	}

	// =========================================================================

	public boolean isValid()
	{
		return line != -1;
	}

	public String getFile()
	{
		return file;
	}

	public int getLine()
	{
		return line;
	}

	public int getColumn()
	{
		return column;
	}

	public xtc.tree.Location toXtcLocation()
	{
		return new xtc.tree.Location(file, line, column);
	}
}
