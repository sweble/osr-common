package de.fau.cs.osr.utils;

public class WrappedException
		extends
			RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public WrappedException()
	{
		super();
	}
	
	public WrappedException(
			String message,
			Throwable cause,
			boolean enableSuppression,
			boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public WrappedException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public WrappedException(String message)
	{
		super(message);
	}
	
	public WrappedException(Throwable cause)
	{
		super(cause);
	}
}
