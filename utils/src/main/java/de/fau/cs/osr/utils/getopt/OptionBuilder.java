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

import java.util.ArrayList;
import java.util.List;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;

public final class OptionBuilder
{
	private char shortOpt = 0;
	
	private String longOpt = null;
	
	private String description = null;
	
	private String argName = null;
	
	private boolean optionalArg = false;
	
	private boolean requiredArg = false;
	
	private char valueSep = 0;
	
	private String propertyKey = null;
	
	private boolean isFixed = false;
	
	private String[] defaultValues = null;
	
	// =========================================================================
	
	private final Configuration config;
	
	private final OptionParser optionParser;
	
	public OptionBuilder(Configuration config, OptionParser optionParser)
	{
		this.config = config;
		this.optionParser = optionParser;
	}
	
	// =========================================================================
	
	public void create() throws IllegalArgumentException
	{
		List<String> names = new ArrayList<String>();
		
		OptionSpec<?> spec = null;
		
		String delim = "\u0000";
		
		boolean hasCmdLine = (shortOpt != 0 || longOpt != null);
		if (!hasCmdLine && propertyKey == null)
			throw new IllegalArgumentException(
					"must specify either short option, long option or property key");
		
		if (!isFixed && hasCmdLine)
		{
			if (shortOpt != 0)
				names.add(String.valueOf(shortOpt));
			
			if (longOpt != null)
				names.add(longOpt);
			
			OptionSpecBuilder builder = optionParser.acceptsAll(names, description);
			spec = builder;
			
			ArgumentAcceptingOptionSpec<String> aaSpec = null;
			if (optionalArg)
			{
				aaSpec = builder.withOptionalArg();
			}
			else if (requiredArg)
			{
				aaSpec = builder.withRequiredArg();
			}
			
			if (aaSpec != null)
			{
				spec = aaSpec;
				
				if (argName != null)
					aaSpec.describedAs(argName);
				
				if (valueSep != 0)
				{
					aaSpec.withValuesSeparatedBy(valueSep);
					delim = String.valueOf(valueSep);
				}
				
				if (defaultValues != null)
					aaSpec.defaultsTo(defaultValues);
			}
		}
		
		config.add(new OptionState(
				spec,
				names,
				propertyKey,
				isFixed,
				defaultValues,
				delim,
				argName,
				description));
	}
	
	// =========================================================================
	
	public OptionBuilder withShortOpt(char shortOpt)
	{
		this.shortOpt = shortOpt;
		return this;
	}
	
	public OptionBuilder withLongOpt(String longOpt)
	{
		this.longOpt = longOpt;
		return this;
	}
	
	public OptionBuilder withDescription(String description)
	{
		this.description = description;
		return this;
	}
	
	public OptionBuilder withValueSeparator(char valueSep)
	{
		this.valueSep = valueSep;
		return this;
	}
	
	public OptionBuilder withRequiredArg()
	{
		this.requiredArg = true;
		this.optionalArg = false;
		return this;
	}
	
	public OptionBuilder hasOptionalArg()
	{
		this.requiredArg = false;
		this.optionalArg = true;
		return this;
	}
	
	public OptionBuilder withArgName(String name)
	{
		this.argName = name;
		return this;
	}
	
	public OptionBuilder withPropertyKey(String propertyKey)
	{
		this.propertyKey = propertyKey;
		return this;
	}
	
	public OptionBuilder withDefault(String[] values)
	{
		this.defaultValues = values;
		return this;
	}
	
	public OptionBuilder withDefault(String value, String... values)
	{
		int len = 1;
		if (values != null)
			len += values.length;
		this.defaultValues = new String[len];
		
		int i = 0;
		this.defaultValues[i++] = value;
		if (values != null)
		{
			for (String v : values)
				this.defaultValues[i++] = v;
		}
		return this;
	}
	
	protected OptionBuilder withIsFixed()
	{
		this.isFixed = true;
		return this;
	}
}
