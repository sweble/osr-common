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

package de.fau.cs.osr.utils.getopt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import de.fau.cs.osr.utils.FmtIllegalArgumentException;

public final class Options
{
	private OptionParser optionParser = new OptionParser();
	
	private Map<String, OptionState> options = new HashMap<String, OptionState>();
	
	private OptionSet commandLine;
	
	private boolean quiet = false;
	
	private final Map<Class<?>, Converter<?>> converters =
	        new HashMap<Class<?>, Converter<?>>();
	
	// =========================================================================
	
	public Options()
	{
		addConverter(String.class, new Converter<String>()
		        {
			        @Override
			        public String convert(String option, String value)
			        {
				        return value;
			        }
		        });
		
		addConverter(Integer.class, new Converter<Integer>()
		        {
			        @Override
			        public Integer convert(String option, String value)
			        {
				        try
				        {
					        return Integer.parseInt(value);
				        }
				        catch (NumberFormatException e)
				        {
					        throw new FailedConversionException(
					                option,
					                value,
					                Integer.class);
				        }
			        }
		        });
		
		addConverter(Boolean.class, new Converter<Boolean>()
		        {
			        @Override
			        public Boolean convert(String option, String value)
			        {
				        if (value == null)
					        return true;
				        
				        String cmp = value.trim().toLowerCase();
				        return cmp.equals("true") ||
				                cmp.equals("yes") ||
				                cmp.equals("1");
			        }
		        });
	}
	
	// =========================================================================
	
	public OptionBuilder createOption(char shortOpt)
	{
		return new OptionBuilder(this).withShortOpt(shortOpt);
	}
	
	public OptionBuilder createOption(String longOpt)
	{
		return new OptionBuilder(this).withLongOpt(longOpt);
	}
	
	public OptionBuilder createOption(char shortOpt, String longOpt)
	{
		return new OptionBuilder(this)
		        .withShortOpt(shortOpt)
		        .withLongOpt(longOpt);
	}
	
	// =========================================================================
	
	public boolean isQuiet()
	{
		return quiet;
	}
	
	public void setQuiet(boolean quiet)
	{
		this.quiet = quiet;
	}
	
	// =========================================================================
	
	public void parse(String[] args)
	{
		commandLine = optionParser.parse(args);
	}
	
	public void help(OutputStream sink)
	{
		try
		{
			optionParser.printHelpOn(sink);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Printing help message failed", e);
		}
	}
	
	public boolean has(String option)
	{
		return commandLine.has(option);
	}
	
	public String value(String option)
	{
		return (String) commandLine.valueOf(option);
	}
	
	public <T> T value(String option, Class<T> clazz, T default_)
	{
		T value = default_;
		if (has(option))
			value = convert(option, value(option), clazz);
		return value;
	}
	
	public List<String> getFreeArguments()
	{
		return commandLine.nonOptionArguments();
	}
	
	public <T> T convert(String option, String value, Class<T> clazz)
	{
		@SuppressWarnings("unchecked")
		Converter<T> converter = (Converter<T>) converters.get(clazz);
		if (converter == null)
			throw new UnknownConversionException(option, clazz);
		
		return converter.convert(option, value);
	}
	
	public String[] values(String option)
	{
		return commandLine.valuesOf(option).toArray(new String[0]);
	}
	
	// =========================================================================
	
	@SuppressWarnings("unchecked")
	public <T> Converter<T> addConverter(Class<T> clazz, Converter<T> converter)
	{
		return (Converter<T>) converters.put(clazz, converter);
	}
	
	// =========================================================================
	
	OptionSpecBuilder acceptsAll(List<String> names, String description)
	{
		OptionState option = new OptionState();
		for (String name : names)
		{
			option.addName(name);
			options.put(name, option);
		}
		return optionParser.acceptsAll(names, description);
	}
	
	// =========================================================================
	
	public void expected(char shortOpt)
	{
		OptionState state = updateState(shortOpt);
		if (!commandLine.has(String.valueOf(shortOpt)))
			throw new MissingOptionException(state.getNames());
	}
	
	public void expected(String longOpt) throws MissingOptionException
	{
		OptionState state = updateState(longOpt);
		if (!commandLine.has(longOpt))
			throw new MissingOptionException(state.getNames());
	}
	
	public String expectedOneOf(String... longOpts) throws ExpectedOneOfOptionException
	{
		int found = 0;
		String last = null;
		for (String longOpt : longOpts)
		{
			updateState(longOpt);
			if (commandLine.has(longOpt))
			{
				++found;
				last = longOpt;
			}
		}
		
		if (found != 1)
			throw new ExpectedOneOfOptionException(longOpts);
		
		return last;
	}
	
	public void optional(char shortOpt)
	{
		updateState(shortOpt);
	}
	
	public void optional(String longOpt)
	{
		updateState(longOpt);
	}
	
	public void ignore(char shortOpt)
	{
		ignore(shortOpt, false);
	}
	
	public void ignore(char shortOpt, boolean quiet)
	{
		if (!quiet && !this.quiet)
			System.err.format("Option `%c' is ignored!\n", shortOpt);
		
		updateState(shortOpt);
	}
	
	public void ignore(String longOpt)
	{
		ignore(longOpt, false);
	}
	
	public void ignore(String longOpt, boolean quiet)
	{
		if (!quiet && !this.quiet)
			System.err.format("Option `%s' is ignored!\n", longOpt);
		
		updateState(longOpt);
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Enum<?>> E optionOneOf(String longOpt, Class<E> enum_) throws IllegalOptionArgumentException
	{
		OptionState state = updateState(longOpt);
		
		String value = (String) commandLine.valueOf(longOpt);
		if (value == null)
			throw new MissingOptionException(state.getNames());
		
		E[] values;
		try
		{
			values = (E[]) enum_.getMethod("values").invoke(null);
		}
		catch (Exception e)
		{
			throw new FmtIllegalArgumentException(
			        e,
			        "Argument `enum_' of illegal type");
		}
		
		E actualValue = null;
		for (E valueType : values)
		{
			if (!(valueType instanceof OptionEnum))
				throw new FmtIllegalArgumentException(
				        "Argument `enum_' does not inherit `OptionEnum'");
			
			if (((OptionEnum) valueType).getOptionName().equals(value))
			{
				actualValue = valueType;
				break;
			}
		}
		
		if (actualValue == null)
			throw new IllegalOptionArgumentException(state.getNames(), value);
		
		return actualValue;
	}
	
	// =========================================================================
	
	protected OptionState updateState(char shortOpt)
	{
		OptionState state = options.get(String.valueOf(shortOpt));
		if (state == null)
			throw new FmtIllegalArgumentException(
			        "Short option `%s' was not specified.",
			        String.valueOf(shortOpt));
		
		state.setValid(true);
		return state;
	}
	
	protected OptionState updateState(String longOpt)
	{
		OptionState state = options.get(longOpt);
		if (state == null)
			throw new FmtIllegalArgumentException(
			        "Long option `%s' was not specified.",
			        longOpt);
		
		state.setValid(true);
		return state;
	}
	
	// =========================================================================
	
	public void checkForInvalidOptions() throws IllegalOptionException
	{
		for (OptionState state : options.values())
		{
			if (commandLine.has(state.getNames().get(0)) && !state.isValid())
				throw new IllegalOptionException(state.getNames());
		}
	}
	
	// =========================================================================
	
	private static final class OptionState
	{
		private boolean valid = false;
		
		private List<String> names = new ArrayList<String>();
		
		public boolean isValid()
		{
			return valid;
		}
		
		public List<String> getNames()
		{
			return names;
		}
		
		public void addName(String name)
		{
			names.add(name);
		}
		
		public void setValid(boolean valid)
		{
			this.valid = valid;
		}
	}
}
