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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import joptsimple.OptionSet;
import joptsimple.internal.ColumnarData;
import de.fau.cs.osr.utils.FmtIllegalArgumentException;

public class Configuration
{
	private final Map<String, OptionState> options = new HashMap<String, OptionState>();
	
	private final Map<String, OptionState> properties = new HashMap<String, OptionState>();
	
	private final Properties propSet = new Properties();
	
	private OptionSet optSet;
	
	// =========================================================================
	
	protected void add(OptionState optionState)
	{
		for (String name : optionState.getNames())
			options.put(name, optionState);
		
		properties.put(optionState.getPropertyKey(), optionState);
	}
	
	public void setOptionSet(OptionSet optSet)
	{
		this.optSet = optSet;
	}
	
	public void loadProperties(Properties p)
	{
		for (Object key : p.keySet())
		{
			OptionState option = properties.get(key);
			if (option == null || option.isFixed())
				throw new UnrecognizedPropertyException((String) key);
		}
		propSet.putAll(p);
	}
	
	// =========================================================================
	
	protected OptionState updateState(char shortOpt)
	{
		OptionState state = getOptByShort(shortOpt);
		state.setValid(true);
		return state;
	}
	
	protected OptionState updateState(String longOpt)
	{
		OptionState state = getOptByName(longOpt);
		state.setValid(true);
		return state;
	}
	
	protected void checkForInvalidOptions() throws IllegalOptionException
	{
		if (optSet == null)
			return;
		for (OptionState state : options.values())
		{
			if (optSet.has(state.getSpec()) && !state.isValid())
				throw new IllegalOptionException(state.formatNames());
		}
	}
	
	private OptionState getOptByShort(char shortOpt)
	{
		OptionState state = options.get(String.valueOf(shortOpt));
		if (state == null)
			throw new FmtIllegalArgumentException(
					"Short option `%s' was not specified.",
					String.valueOf(shortOpt));
		return state;
	}
	
	private OptionState getOptByName(String name)
	{
		OptionState state = get(name);
		if (state == null)
			throw new FmtIllegalArgumentException(
					"Option or property key `%s' was not specified.",
					name);
		return state;
	}
	
	private OptionState get(String option)
	{
		OptionState o = options.get(option);
		if (o != null)
			return o;
		return properties.get(option);
	}
	
	// =========================================================================
	
	public boolean has(String option)
	{
		OptionState o = getOptByName(option);
		if (optSet != null && optSet.has(o.getSpec()))
			return true;
		if (o.getPropertyKey() != null && propSet.containsKey(o.getPropertyKey()))
			return true;
		return false;
	}
	
	public String valueOf(String option)
	{
		OptionState o = getOptByName(option);
		String value = valueOfNoDefault(o);
		if (value != null)
			return value;
		return o.getSafeDefaultValue();
	}
	
	public String valueOf(String option, String default_)
	{
		String value = valueOfNoDefault(option);
		if (value != null)
			return value;
		return default_;
	}
	
	public String valueOfNoDefault(String option)
	{
		return valueOfNoDefault(getOptByName(option));
	}
	
	private String valueOfNoDefault(OptionState o)
	{
		String value = null;
		if (optSet != null && o.getSpec() != null)
		{
			value = (String) optSet.valueOf(o.getSpec());
			if (value != null)
				return value;
		}
		if (o.getPropertyKey() != null)
		{
			value = propSet.getProperty(o.getPropertyKey());
			if (value != null)
				return value;
		}
		return null;
	}
	
	public List<String> valuesOf(String option)
	{
		OptionState o = getOptByName(option);
		List<String> values = valuesOfNoDefault(o);
		if (values != null)
			return values;
		return o.getSafeDefaultValues();
	}
	
	@SuppressWarnings("unchecked")
	private List<String> valuesOfNoDefault(OptionState o)
	{
		List<String> values = null;
		if (optSet != null)
		{
			values = (List<String>) optSet.valuesOf(o.getSpec());
			if (values != null)
				return values;
		}
		if (o.getPropertyKey() != null)
		{
			values = propToList(o);
			if (values != null)
				return values;
		}
		return null;
	}
	
	private List<String> propToList(OptionState o)
	{
		String value = propSet.getProperty(o.getPropertyKey());
		StringTokenizer lexer = new StringTokenizer(value, o.getSafeDelim());
		List<String> values = new ArrayList<String>();
		while (lexer.hasMoreTokens())
			values.add(lexer.nextToken());
		return values;
	}
	
	public List<String> nonOptionArguments()
	{
		if (optSet != null)
			return optSet.nonOptionArguments();
		return Collections.emptyList();
	}
	
	public Properties propertySubset(String prefix)
	{
		if (!prefix.endsWith("."))
			prefix = prefix + ".";
		int beginIndex = prefix.length();
		
		Properties subset = new Properties();
		for (OptionState o : properties.values())
		{
			String key = o.getPropertyKey();
			if (key == null)
				continue;
			
			if (!key.startsWith(prefix))
				continue;
			
			String value = valueOfNoDefault(o);
			if (value == null)
				value = o.getSafeDefaultValue();
			
			if (value != null)
			{
				String newKey = key.substring(beginIndex);
				subset.setProperty(newKey, value);
			}
		}
		return subset;
	}
	
	// =========================================================================
	
	public void propertiesHelp(PrintStream out)
	{
		ColumnarData grid = new ColumnarData("Property key", "Description");
		
		ArrayList<OptionState> list = new ArrayList<OptionState>();
		for (OptionState s : properties.values())
		{
			if (s.getPropertyKey() != null)
				list.add(s);
		}
		
		Collections.sort(list, new Comparator<OptionState>()
		{
			@Override
			public int compare(OptionState o1, OptionState o2)
			{
				return o1.getPropertyKey().compareTo(o2.getPropertyKey());
			}
		});
		
		for (OptionState o : list)
		{
			if (o.isFixed())
				continue;
			
			// -- left column
			
			StringBuilder name = new StringBuilder();
			name.append(o.getPropertyKey());
			if (o.getArgName() != null && !o.getArgName().isEmpty())
			{
				name.append("=<");
				name.append(o.getArgName());
				name.append('>');
			}
			
			// -- right column
			
			StringBuilder desc = new StringBuilder();
			desc.append(o.getDescription());
			
			// -- right column - see also
			
			boolean sa = false;
			if (o.getNames() != null && !o.getNames().isEmpty())
			{
				desc.append(" (see also: ");
				boolean first = true;
				for (String n : o.getNames())
				{
					if (!first)
						desc.append(", ");
					desc.append(n.length() == 1 ? "-" : "--");
					desc.append(n);
					first = false;
				}
				sa = true;
			}
			
			// -- right column - default values
			
			List<String> defaults = o.getSafeDefaultValues();
			if (defaults != null && !defaults.isEmpty())
			{
				desc.append(sa ? "; " : " (");
				desc.append("default: ");
				boolean first = true;
				boolean hasDelim = !o.getSafeDelim().equals("\u0000");
				for (String d : defaults)
				{
					if (!first && hasDelim)
						desc.append(o.getSafeDelim());
					desc.append(d);
					first = false;
				}
				desc.append(")");
			}
			else if (sa)
			{
				desc.append(")");
			}
			
			grid.addRow(name.toString(), desc.toString());
		}
		
		out.print(grid.format());
	}
	
	// =========================================================================
	
	public void fixedOptionsHelp(PrintStream out)
	{
		ColumnarData grid = new ColumnarData("Name", "Description");
		
		ArrayList<OptionState> list = new ArrayList<OptionState>();
		for (OptionState s : properties.values())
		{
			if (s.isFixed())
				list.add(s);
		}
		
		Collections.sort(list, new Comparator<OptionState>()
		{
			@Override
			public int compare(OptionState o1, OptionState o2)
			{
				return o1.getPropertyKey().compareTo(o2.getPropertyKey());
			}
		});
		
		for (OptionState o : list)
		{
			if (!o.isFixed())
				continue;
			
			// -- left column
			
			StringBuilder name = new StringBuilder();
			name.append(o.getPropertyKey());
			
			// -- left column: value
			
			List<String> defaults = o.getSafeDefaultValues();
			if (defaults != null && !defaults.isEmpty())
			{
				name.append("=");
				boolean first = true;
				boolean hasDelim = !o.getSafeDelim().equals("\u0000");
				for (String d : defaults)
				{
					if (!first && hasDelim)
						name.append(o.getSafeDelim());
					name.append(d);
					first = false;
				}
			}
			
			// -- right column
			
			String desc = o.getDescription() != null ? o.getDescription() : "-";
			
			// -- right column - default values
			
			grid.addRow(name.toString(), desc);
		}
		
		out.print(grid.format());
	}
}
