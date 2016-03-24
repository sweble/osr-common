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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import joptsimple.OptionParser;
import de.fau.cs.osr.utils.FmtIllegalArgumentException;

public final class Options
{
	private final OptionParser optionParser = new OptionParser();

	private final Configuration config = new Configuration();

	private final Map<Class<?>, Converter<?>> converters =
			new HashMap<Class<?>, Converter<?>>();

	private boolean quiet = false;

	// =========================================================================

	public Options()
	{
		Converter<String> strConv = new Converter<String>()
		{
			@Override
			public String convert(String option, String value)
			{
				return value;
			}
		};
		addConverter(String.class, strConv);

		Converter<Integer> intConv = new Converter<Integer>()
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
		};
		addConverter(Integer.class, intConv);
		addConverter(int.class, intConv);

		Converter<Boolean> boolConv = new Converter<Boolean>()
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
		};
		addConverter(Boolean.class, boolConv);
		addConverter(boolean.class, boolConv);
	}

	// =========================================================================

	/**
	 * Start creation of an option with only a short name.
	 * 
	 * @param shortOpt
	 *            The short name of the option.
	 * @return An OptionBuilder object. You have to call .create() on this
	 *         object to actually create the option.
	 */
	public OptionBuilder createOption(char shortOpt)
	{
		return new OptionBuilder(config, optionParser).withShortOpt(shortOpt);
	}

	/**
	 * Start creation of an option with only a long name.
	 * 
	 * @param longOpt
	 *            The long name of the option.
	 * @return An OptionBuilder object. You have to call .create() on this
	 *         object to actually create the option.
	 */
	public OptionBuilder createOption(String longOpt)
	{
		return new OptionBuilder(config, optionParser).withLongOpt(longOpt);
	}

	/**
	 * Start creation of an option with both short and long name.
	 * 
	 * @param shortOpt
	 *            The short name of the option.
	 * @param longOpt
	 *            The long name of the option.
	 * @return An OptionBuilder object. You have to call .create() on this
	 *         object to actually create the option.
	 */
	public OptionBuilder createOption(char shortOpt, String longOpt)
	{
		return new OptionBuilder(config, optionParser)
				.withShortOpt(shortOpt)
				.withLongOpt(longOpt);
	}

	/**
	 * Start creation of an option that can only be specified in a properties
	 * file but not on the command line.
	 * 
	 * @param propertyKey
	 *            The key (name) of the property.
	 * @return An OptionBuilder object. You have to call .create() on this
	 *         object to actually create the option.
	 */

	public OptionBuilder createPropertyOnlyOption(String propertyKey)
	{
		return new OptionBuilder(config, optionParser)
				.withPropertyKey(propertyKey);
	}

	/**
	 * Create an option with a fixed value. The user cannot override this option
	 * on the command line or in a properties file.
	 * 
	 * @param name
	 *            The name of the option. The name is used as long name and
	 *            property key.
	 * @param value
	 *            The fixed value of this option.
	 */
	public void createFixedValueOption(String name, String value)
	{
		new OptionBuilder(config, optionParser)
				.withIsFixed()
				.withLongOpt(name)
				.withPropertyKey(name)
				.withRequiredArg()
				.withDefault(value)
				.create();
	}

	// =========================================================================

	/**
	 * Whether this Options object prints warning messages on the console.
	 */
	public boolean isQuiet()
	{
		return quiet;
	}

	/**
	 * Set whether this Options object shall print warning message on the
	 * console.
	 */
	public void setQuiet(boolean quiet)
	{
		this.quiet = quiet;
	}

	// =========================================================================

	/**
	 * Parse a command line after specifying all possible options.
	 */
	public void parse(String[] args)
	{
		config.setOptionSet(optionParser.parse(args));
	}

	/**
	 * Merge given properties.
	 */
	public void load(Properties properties)
	{
		config.loadProperties(properties);
	}

	/**
	 * Load options from input stream.
	 */
	public void load(FileInputStream inputStream) throws IOException
	{
		Properties p = new Properties();
		p.load(inputStream);
		load(p);
	}

	/**
	 * Load options from properties file.
	 */
	public void load(File file) throws IOException
	{
		FileInputStream is = new FileInputStream(file);
		try
		{
			load(is);
		}
		finally
		{
			is.close();
		}
	}

	/**
	 * Print a help message after specifying all possible options.
	 * 
	 * @param sink
	 *            Where to print the help message to.
	 * 
	 * @deprecated Use cmdLineHelp() instead.
	 */
	public void help(PrintStream sink)
	{
		cmdLineHelp(sink);
	}

	/**
	 * Print a help message after specifying all possible options.
	 * 
	 * @param sink
	 *            Where to print the help message to.
	 */
	public void cmdLineHelp(OutputStream sink)
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

	/**
	 * Print a help message after specifying all possible property keys.
	 * 
	 * @param out
	 *            Where to print the help message to.
	 */
	public void propertiesHelp(PrintStream out)
	{
		config.propertiesHelp(out);
	}

	/**
	 * Print a help message after specifying all possible fixed options.
	 * 
	 * @param out
	 *            Where to print the help message to.
	 */
	public void fixedOptionsHelp(PrintStream out)
	{
		config.fixedOptionsHelp(out);
	}

	/**
	 * Tell whether an options was specified on the command line or in a
	 * properties file. This method only returns true if the option was actually
	 * specified. If it was not specified but has a default value, this method
	 * still returns false.
	 * 
	 * @param option
	 *            The name of the options to query. This can be either the short
	 *            or long name or the property key of this option.
	 */
	public boolean has(String option)
	{
		return config.has(option);
	}

	/**
	 * Return the value of an option. First the value is searched on the command
	 * line. Then the properties are scanned. And finally, if the option has a
	 * default value, the default value will be returned. If none of the
	 * aforementioned sources can provide a value, null is returned.
	 * 
	 * @param option
	 *            The name of the options to query. This can be either the short
	 *            or long name or the property key of this option.
	 */
	public String value(String option)
	{
		return config.valueOf(option);
	}

	/**
	 * Return the value of an option. First the value is searched on the command
	 * line. Then the properties are scanned. If none of the aforementioned
	 * sources can provide a value, the default value given as parameter is
	 * returned.
	 * 
	 * @param option
	 *            The name of the options to query. This can be either the short
	 *            or long name or the property key of this option.
	 * @param default_
	 *            The default value to use if neither the command line nor
	 *            properties can provide a value.
	 */
	public String value(String option, String default_)
	{
		return config.valueOf(option, default_);
	}

	public <T> T value(String option, Class<T> clazz)
	{
		return convert(option, value(option), clazz);
	}

	public <T> T value(String option, Class<T> clazz, T default_)
	{
		T value = default_;
		if (has(option))
			value = convert(option, config.valueOfNoDefault(option), clazz);
		return value;
	}

	public String[] values(String option)
	{
		return config.valuesOf(option).toArray(new String[0]);
	}

	public List<String> getFreeArguments()
	{
		return config.nonOptionArguments();
	}

	public Properties propertySubset(String prefix)
	{
		return config.propertySubset(prefix);
	}

	// =========================================================================

	@SuppressWarnings("unchecked")
	public <T> Converter<T> addConverter(Class<T> clazz, Converter<T> converter)
	{
		return (Converter<T>) converters.put(clazz, converter);
	}

	// =========================================================================

	/**
	 * Assert that an expected short option was given on the command line. This
	 * method also fails if the option was not given but had a default value.
	 * 
	 * @throws MissingOptionException
	 */
	public void expected(char shortOpt) throws MissingOptionException
	{
		OptionState state = config.updateState(shortOpt);
		if (!config.has(String.valueOf(shortOpt)))
			throw new MissingOptionException(state.formatNames());
	}

	/**
	 * Assert that an expected long option was given on the command line or
	 * found in a properties file. This method also fails if the option was not
	 * given but had a default value.
	 * 
	 * @param name
	 *            The name of the long option or the property key.
	 * 
	 * @throws MissingOptionException
	 */
	public void expected(String name) throws MissingOptionException
	{
		OptionState state = config.updateState(name);
		if (!config.has(name))
			throw new MissingOptionException(state.formatNames());
	}

	/**
	 * Assert that exactly one of a set of expected long options was given on
	 * the command line or found in a properties file.
	 * 
	 * @param names
	 *            The names of the long options or the property keys.
	 * 
	 * @throws MissingOptionException
	 */
	public String expectedOneOf(String... names) throws ExpectedOneOfOptionException
	{
		int found = 0;
		String last = null;
		for (String name : names)
		{
			config.updateState(name);
			if (config.has(name))
			{
				++found;
				last = name;
			}
		}

		if (found != 1)
			throw new ExpectedOneOfOptionException(names);

		return last;
	}

	/**
	 * Indicate that a short option is optional. This becomes important when
	 * calling checkForInvalidOptions() to check for unexpected options on the
	 * command line.
	 */
	public void optional(char shortOpt)
	{
		config.updateState(shortOpt);
	}

	/**
	 * Indicate that a long option option is optional. This becomes important
	 * when calling checkForInvalidOptions() to check for unexpected options on
	 * the command line.
	 * 
	 * Note that while certain options can be required (see expected()) in
	 * properties files as well, all non-required properties are automatically
	 * optional. Therefore this method does not affect the options defined in
	 * properties files.
	 */
	public void optional(String longOpt)
	{
		config.updateState(longOpt);
	}

	/**
	 * Indicate that a short option is ignored. This becomes important when
	 * calling checkForInvalidOptions() to check for unexpected options on the
	 * command line.
	 * 
	 * This method also prints a warning to stderr if the option was given on
	 * the command line.
	 */
	public void ignore(char shortOpt)
	{
		ignore(shortOpt, false);
	}

	/**
	 * Indicate that a short option is ignored. This becomes important when
	 * calling checkForInvalidOptions() to check for unexpected options on the
	 * command line.
	 * 
	 * This method also prints a warning to stderr if the option was given on
	 * the command line.
	 * 
	 * @param quiet
	 *            Supress the warning message.
	 */
	public void ignore(char shortOpt, boolean quiet)
	{
		if (!quiet && !this.quiet && config.has(String.valueOf(shortOpt)))
			System.err.format("Option `%c' is ignored!\n", shortOpt);

		config.updateState(shortOpt);
	}

	/**
	 * Indicate that a long option or property key is ignored. This becomes
	 * important when calling checkForInvalidOptions() to check for unexpected
	 * options on the command line. It is not required for properties.
	 * 
	 * This method also prints a warning to stderr if the option was given on
	 * the command line or defined in a properties file.
	 */
	public void ignore(String name)
	{
		ignore(name, false);
	}

	/**
	 * Indicate that a long option or property key is ignored. This becomes
	 * important when calling checkForInvalidOptions() to check for unexpected
	 * options on the command line. It is not required for properties.
	 * 
	 * This method also prints a warning to stderr if the option was given on
	 * the command line or defined in a properties file.
	 * 
	 * @param quiet
	 *            Supress the warning message.
	 */
	public void ignore(String name, boolean quiet)
	{
		if (!quiet && !this.quiet && config.has(name))
			System.err.format("Option `%s' is ignored!\n", name);

		config.updateState(name);
	}

	/**
	 * Assert that a given short option's value is one of given set.
	 * 
	 * @throws IllegalOptionArgumentException
	 */
	public <E extends Enum<?>> E optionOneOf(char shortOpt, Class<E> enum_) throws IllegalOptionArgumentException
	{
		return optionOneOf(String.valueOf(shortOpt), enum_);
	}

	/**
	 * Assert that a given long option or property's value is one of given set.
	 * 
	 * @throws IllegalOptionArgumentException
	 */
	@SuppressWarnings("unchecked")
	public <E extends Enum<?>> E optionOneOf(String name, Class<E> enum_) throws IllegalOptionArgumentException
	{
		OptionState state = config.updateState(name);

		String value = (String) config.valueOf(name);
		if (value == null)
			throw new MissingOptionException(state.formatNames());

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
			throw new IllegalOptionArgumentException(state.formatNames(), value);

		return actualValue;
	}

	public void checkForInvalidOptions() throws IllegalOptionException
	{
		config.checkForInvalidOptions();
	}

	// =========================================================================

	protected <T> T convert(String option, String value, Class<T> clazz)
	{
		@SuppressWarnings("unchecked")
		Converter<T> converter = (Converter<T>) converters.get(clazz);
		if (converter == null)
			throw new UnknownConversionException(option, clazz);

		return converter.convert(option, value);
	}
}
