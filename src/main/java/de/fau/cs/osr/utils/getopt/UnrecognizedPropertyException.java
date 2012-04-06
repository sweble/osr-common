package de.fau.cs.osr.utils.getopt;

import java.util.Arrays;

import joptsimple.OptionException;

public class UnrecognizedPropertyException
		extends
			OptionException
{
	private static final long serialVersionUID = 1L;
	
	// =========================================================================
	
	protected UnrecognizedPropertyException(String option)
	{
		super(Arrays.asList(option));
	}
	
	// =========================================================================
	
	@Override
	public String getMessage()
	{
		return String.format(
				"%s is not a known property key",
				singleOptionMessage());
	}
}
