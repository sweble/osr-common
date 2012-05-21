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

package de.fau.cs.osr.ptk.common.xml;

import javax.xml.stream.Location;
import javax.xml.stream.events.XMLEvent;

public class DeserializationException
		extends
			Exception
{
	private static final long serialVersionUID = 1L;
	
	// =========================================================================
	
	public DeserializationException(Throwable cause)
	{
		super(cause);
	}
	
	public DeserializationException(
			XMLEvent event,
			String message,
			Throwable cause)
	{
		super(makeMessage(event, message), cause);
	}
	
	public DeserializationException(XMLEvent event, String message)
	{
		super(makeMessage(event, message));
	}
	
	// =========================================================================
	
	private static String makeMessage(XMLEvent event, String message)
	{
		if (event == null || event.getLocation() == null)
			return message;
		
		Location l = event.getLocation();
		
		return String.format(
				"Line %d, Col %d: %s",
				l.getLineNumber(),
				l.getColumnNumber(),
				message);
	}
}
