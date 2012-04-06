package de.fau.cs.osr.utils.getopt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import joptsimple.OptionSpec;

final class OptionState
{
	private final OptionSpec<?> spec;
	
	private final List<String> names;
	
	private final String propertyKey;
	
	private final boolean isFixed;
	
	private final String[] defaultValues;
	
	private final String delim;
	
	private final String argName;
	
	private final String description;
	
	// =========================================================================
	
	private boolean valid = false;
	
	// =========================================================================
	
	public OptionState(
			OptionSpec<?> spec,
			List<String> names,
			String propertyKey,
			boolean isFixed,
			String[] defaultValues,
			String delim,
			String argName,
			String description)
	{
		this.names = names;
		this.propertyKey = propertyKey;
		this.isFixed = isFixed;
		this.spec = spec;
		this.defaultValues = defaultValues;
		this.delim = delim;
		this.argName = argName;
		this.description = description;
	}
	
	// =========================================================================
	
	public boolean isValid()
	{
		return valid;
	}
	
	public void setValid(boolean valid)
	{
		this.valid = valid;
	}
	
	// =========================================================================
	
	public List<String> getNames()
	{
		return names;
	}
	
	public String getPropertyKey()
	{
		return propertyKey;
	}
	
	public OptionSpec<?> getSpec()
	{
		return spec;
	}
	
	public boolean isFixed()
	{
		return isFixed;
	}
	
	public String getSafeDefaultValue()
	{
		List<String> values = Arrays.asList(defaultValues);
		if (values != null && !values.isEmpty())
			return values.get(0);
		return null;
	}
	
	public List<String> getSafeDefaultValues()
	{
		if (defaultValues == null)
			return Collections.emptyList();
		return Arrays.asList(defaultValues);
	}
	
	public String getSafeDelim()
	{
		return delim;
	}
	
	public String getArgName()
	{
		return argName;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public List<String> formatNames()
	{
		List<String> result = new ArrayList<String>();
		
		if (names != null)
		{
			for (String n : names)
				result.add((n.length() == 1 ? "-" : "--") + n);
		}
		
		if (propertyKey != null)
			result.add(propertyKey);
		
		return result;
	}
}
