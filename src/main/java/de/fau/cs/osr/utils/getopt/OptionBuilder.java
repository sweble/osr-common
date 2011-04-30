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
	
	// =========================================================================
	
	private final Options getOpt;
	
	public OptionBuilder(Options getOpt)
	{
		super();
		this.getOpt = getOpt;
	}
	
	// =========================================================================
	
	public void create() throws IllegalArgumentException
	{
		if (shortOpt == 0 && longOpt == null)
			throw new IllegalArgumentException(
			        "must specify either short option or long option");
		
		List<String> names = new ArrayList<String>();
		
		if (shortOpt != 0)
			names.add(String.valueOf(shortOpt));
		
		if (longOpt != null)
			names.add(longOpt);
		
		OptionSpecBuilder builder =
		        getOpt.acceptsAll(
		                names, description);
		
		ArgumentAcceptingOptionSpec<String> spec = null;
		
		if (optionalArg)
		{
			spec = builder.withOptionalArg();
		}
		else if (requiredArg)
		{
			spec = builder.withRequiredArg();
		}
		
		if (spec != null)
		{
			if (argName != null)
				spec.describedAs(argName);
			
			if (valueSep != 0)
				spec.withValuesSeparatedBy(valueSep);
		}
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
}
