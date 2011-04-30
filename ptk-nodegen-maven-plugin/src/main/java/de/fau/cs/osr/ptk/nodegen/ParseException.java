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

package de.fau.cs.osr.ptk.nodegen;

import xtc.tree.Location;
import de.fau.cs.osr.ptk.common.ast.AstNode;

public final class ParseException
        extends
        	RuntimeException
{
	private static final long serialVersionUID = 8307056975303464132L;
	
	// =========================================================================
	
	public ParseException(String message, Object... arguments)
	{
		super(String.format(message, arguments));
	}
	
	public ParseException(String message, AstNode node, Object... arguments)
	{
		super(makeMessage(message, node.getLocation(), arguments));
	}
	
	public ParseException(String message, Location location, Object... arguments)
	{
		super(makeMessage(message, location, arguments));
	}
	
	public ParseException(Throwable cause, String message, AstNode node, Object... arguments)
	{
		super(makeMessage(message, node.getLocation(), arguments), cause);
	}
	
	// =========================================================================
	
	private static String makeMessage(String message, Location location, Object[] arguments)
	{
		return String.format(
		        "%s: %s",
		        location.toString(),
		        String.format(message, arguments));
	}
}
