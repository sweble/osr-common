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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import joptsimple.OptionException;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestOptions
{
	private Options options;
	
	private Properties properties;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	// =========================================================================
	
	@Before
	public void initialize()
	{
		options = new Options();
		
		properties = new Properties();
		properties.setProperty("some.opt", "some opt value");
	}
	
	// =========================================================================
	
	@Test
	public void testUnspecifiedOptionNotAvailable() throws Exception
	{
		options.createOption("some-opt")
				.create();
		
		Assert.assertFalse(options.has("some-opt"));
	}
	
	// =========================================================================
	
	@Test
	public void testOptionValueRetrievableByLongOption()
	{
		options.createOption('d', "dump")
				.withRequiredArg()
				.create();
		
		options.parse(new String[] { "--dump=bla" });
		
		Assert.assertTrue(options.has("dump"));
		Assert.assertEquals("bla", options.value("dump"));
	}
	
	@Test
	public void testOptionValueRetrievableByShortOption()
	{
		options.createOption('d')
				.withRequiredArg()
				.create();
		
		options.parse(new String[] { "-d", "bla" });
		
		Assert.assertTrue(options.has("d"));
		Assert.assertEquals("bla", options.value("d"));
	}
	
	@Test
	public void testDefaultOptionRetrievable()
	{
		options.createOption('d')
				.withRequiredArg()
				.withDefault("bla")
				.create();
		
		options.parse(new String[] {});
		
		Assert.assertFalse(options.has("d"));
		Assert.assertEquals("bla", options.value("d"));
	}
	
	@Test
	public void testPropertyAvailableAsKeyAndLongOpt() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.withRequiredArg()
				.create();
		
		options.load(properties);
		
		Assert.assertTrue(options.has("some-opt"));
		Assert.assertTrue(options.has("some.opt"));
		Assert.assertEquals(properties.getProperty("some.opt"), options.value("some.opt"));
		Assert.assertEquals(properties.getProperty("some.opt"), options.value("some-opt"));
	}
	
	@Test
	public void testCmdLineArgAvailableAsKeyAndLongOpt() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.withRequiredArg()
				.create();
		
		final String value = "cmd line value";
		options.parse(new String[] { "--some-opt=" + value });
		
		Assert.assertTrue(options.has("some-opt"));
		Assert.assertTrue(options.has("some.opt"));
		Assert.assertEquals(value, options.value("some.opt"));
		Assert.assertEquals(value, options.value("some-opt"));
	}
	
	@Test
	public void testCmdLineOverridesProps() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.withRequiredArg()
				.create();
		
		final String value = "cmd line value";
		options.parse(new String[] { "--some-opt=" + value });
		
		options.load(properties);
		
		Assert.assertTrue(options.has("some-opt"));
		Assert.assertTrue(options.has("some.opt"));
		Assert.assertEquals(value, options.value("some.opt"));
		Assert.assertEquals(value, options.value("some-opt"));
	}
	
	// =========================================================================
	
	@Test
	public void testUnknownCmdLineOptRaisesException() throws Exception
	{
		options.createOption("some-opt")
				.create();
		
		thrown.expect(OptionException.class);
		thrown.expectMessage("'blub' is not a recognized option");
		
		options.parse(new String[] { "--blub" });
	}
	
	@Test
	public void testUnknownPropertyKeyRaisesException() throws Exception
	{
		options.createOption("some-other-opt")
				.create();
		
		thrown.expect(UnrecognizedPropertyException.class);
		thrown.expectMessage("'some.opt' is not a known property key");
		
		options.load(properties);
	}
	
	@Test
	public void testUnexpectedOptionRaisesException() throws Exception
	{
		options.createOption("some-opt")
				.create();
		
		options.parse(new String[] { "--some-opt=hallo" });
		
		Assert.assertTrue(options.has("some-opt"));
		
		thrown.expect(IllegalOptionException.class);
		thrown.expectMessage("'--some-opt'");
		
		options.checkForInvalidOptions();
	}
	
	@Test
	public void testUnexpectedPropertyDoesNotRaiseException() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.create();
		
		options.load(properties);
		
		Assert.assertTrue(options.has("some-opt"));
		options.checkForInvalidOptions();
	}
	
	@Test
	public void testGivenOptionalCmdLineOptDoesNotRaiseException() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.create();
		
		options.parse(new String[] { "--some-opt=hallo" });
		
		Assert.assertTrue(options.has("some-opt"));
		options.optional("some-opt");
		options.checkForInvalidOptions();
	}
	
	@Test
	public void testUnspecifiedOptionalCmdLineOptDoesNotRaiseException() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.create();
		
		options.parse(new String[] {});
		
		Assert.assertFalse(options.has("some-opt"));
		options.optional("some-opt");
		options.checkForInvalidOptions();
	}
	
	@Test
	public void testGivenRequiredCmdLineOptDoesNotRaiseException() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.create();
		
		options.parse(new String[] { "--some-opt" });
		
		Assert.assertTrue(options.has("some-opt"));
		options.expected("some-opt");
		options.checkForInvalidOptions();
	}
	
	@Test
	public void testMissingRequiredCmdLineOptRaisesException() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.create();
		
		options.parse(new String[] {});
		
		Assert.assertFalse(options.has("some-opt"));
		
		thrown.expect(MissingOptionException.class);
		thrown.expectMessage("'--some-opt'");
		
		options.expected("some-opt");
	}
	
	@Test
	public void testPropertySatisfiesRequiredOption() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.create();
		
		options.load(properties);
		
		Assert.assertTrue(options.has("some-opt"));
		
		options.expected("some-opt");
	}
	
	@Test
	public void testIgnoredButGivenCmdLineOptCausesWarning() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.create();
		
		options.parse(new String[] { "--some-opt" });
		
		Assert.assertTrue(options.has("some-opt"));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos));
		options.ignore("some-opt");
		baos.close();
		Assert.assertTrue(baos.toString().contains("some-opt"));
		
		options.checkForInvalidOptions();
	}
	
	@Test
	public void testIgnoredButGivenPropertyCausesWarning() throws Exception
	{
		options.createOption("some-opt")
				.withPropertyKey("some.opt")
				.create();
		
		options.parse(new String[] { "--some-opt" });
		
		Assert.assertTrue(options.has("some-opt"));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos));
		options.ignore("some-opt");
		baos.close();
		Assert.assertTrue(baos.toString().contains("some-opt"));
		
		options.checkForInvalidOptions();
	}
	
	// =========================================================================
	
	@Test
	public void testFixedValueIsNotRecognizedAsCmdLineArg() throws Exception
	{
		options.createFixedValueOption("some-opt", "100");
		
		thrown.expect(OptionException.class);
		thrown.expectMessage("'some-opt'");
		options.parse(new String[] { "--some-opt" });
	}
	
	@Test
	public void testFixedValueIsNotRecognizedAsProperty() throws Exception
	{
		options.createFixedValueOption("some.opt", "100");
		
		thrown.expect(UnrecognizedPropertyException.class);
		thrown.expectMessage("'some.opt'");
		options.load(properties);
	}
	
	@Test
	public void testFixedValueIsNotReportedByHasButHasValue() throws Exception
	{
		options.createFixedValueOption("some.opt", "100");
		
		Assert.assertFalse(options.has("some.opt"));
		Assert.assertEquals("100", options.value("some.opt"));
	}
	
	// =========================================================================
	
	@Test
	public void testPropertySubset() throws Exception
	{
		options.createOption("some-number")
				.withArgName("N")
				.withDescription("Some numbers' description")
				.withDefault("42")
				.withRequiredArg()
				.withPropertyKey("subset.some-number")
				.create();
		
		options.createOption("some-other-number")
				.withPropertyKey("some-other-subset.some-other-number")
				.withRequiredArg()
				.create();
		
		options.parse(new String[] { "--some-number=8", "--some-other-number=42" });
		
		Properties p = options.propertySubset("subset");
		Assert.assertEquals(1, p.size());
		Assert.assertEquals("8", p.getProperty("some-number"));
	}
	
	// =========================================================================
	
	@Test
	@Ignore
	public void testHelpMessage() throws Exception
	{
		/* TODO: We have to test:
		         - options.cmdLineHelp(System.out);
		         - options.propertiesHelp(System.out);
		         - options.fixedOptionsHelp(System.out);
		*/
	}
}
