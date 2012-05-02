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
