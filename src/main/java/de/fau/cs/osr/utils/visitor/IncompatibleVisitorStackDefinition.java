package de.fau.cs.osr.utils.visitor;

@SuppressWarnings("serial")
public class IncompatibleVisitorStackDefinition
		extends
			Exception
{
	public IncompatibleVisitorStackDefinition()
	{
		super();
	}
	
	public IncompatibleVisitorStackDefinition(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public IncompatibleVisitorStackDefinition(String message)
	{
		super(message);
	}
	
	public IncompatibleVisitorStackDefinition(Throwable cause)
	{
		super(cause);
	}
}
