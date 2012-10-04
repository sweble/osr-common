package de.fau.cs.osr.utils.visitor;

public class MultipleVisitMethodsMatchException
		extends
			RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	private final Class<?> arg0;
	
	private final Class<?> arg1;
	
	public MultipleVisitMethodsMatchException(Class<?> arg0, Class<?> arg1)
	{
		this.arg0 = arg0;
		this.arg1 = arg1;
	}
	
	public Class<?> getArg0()
	{
		return arg0;
	}
	
	public Class<?> getArg1()
	{
		return arg1;
	}
}
