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

package de.fau.cs.osr.ptk.profiler;

import joptsimple.OptionException;
import de.fau.cs.osr.utils.FmtInternalLogicError;
import de.fau.cs.osr.utils.getopt.OptionEnum;
import de.fau.cs.osr.utils.getopt.Options;

public final class ParserProfiler
{
	private Options options;
	
	// =========================================================================
	
	public static void main(String[] args)
	{
		new ParserProfiler().run(args);
	}
	
	// =========================================================================
	
	private void run(String[] args)
	{
		TestType test = parseCommandLine(args);
		if (test == null)
			return;
		
		try
		{
			switch (test)
			{
				case parseAndPrint:
					new ParseAndPrintTest().run(options);
					System.exit(0);
					return;
					
				case speedTest:
					new ParserSpeedTest().run(options);
					System.exit(0);
					return;
					
				default:
					throw new FmtInternalLogicError();
			}
		}
		catch (OptionException e)
		{
			printArgsErrorMessage(e);
			System.exit(-1);
		}
		catch (TestFailedException e)
		{
			System.err.println("!!!! TEST FAILED !!!!");
			e.printStackTrace();
			System.exit(-1);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			System.exit(-1);
		}
	}
	
	// =========================================================================
	
	private TestType parseCommandLine(String[] args)
	{
		options = setUpCommandLineOptions();
		
		try
		{
			options.parse(args);
		}
		catch (OptionException e)
		{
			printArgsErrorMessage(e);
			return null;
		}
		
		if (options.has("help"))
		{
			printHelpMessage(options);
			return null;
		}
		
		try
		{
			return options.optionOneOf("test", TestType.class);
		}
		catch (OptionException e)
		{
			printArgsErrorMessage(e);
			return null;
		}
	}
	
	private Options setUpCommandLineOptions()
	{
		Options opts = new Options();
		
		opts.createOption('h', "help")
		        .withDescription("Print help message.")
		        .create();
		
		opts.createOption("test")
		        .withDescription("The test operation to perform. See list below option listing for possible tests.")
		        .withRequiredArg()
		        .withArgName("TEST")
		        .create();
		
		// test-specific arguments
		
		opts.createOption('p', "parser")
		        .withDescription("The parser class to test.")
		        .withRequiredArg()
		        .withArgName("CLASS")
		        .create();
		
		opts.createOption('b', "base")
		        .withDescription("The base directory under which the resources for the test cases can be found.")
		        .withRequiredArg()
		        .withArgName("DIR")
		        .create();
		
		opts.createOption('i', "in")
		        .withDescription("A directory relative to the base directory containing the input wikitext files.")
		        .withRequiredArg()
		        .withArgName("DIR")
		        .create();
		
		opts.createOption('r', "ref")
		        .withDescription("A directory relative to the base directory containing the reference files.")
		        .withRequiredArg()
		        .withArgName("DIR")
		        .create();
		
		opts.createOption("diff")
		        .withDescription("Path to an executable which will be used as diff tool.")
		        .withRequiredArg()
		        .withArgName("EXE")
		        .create();
		
		opts.createOption("viewer")
		        .withDescription("Path to an executable which will be used as text viewer.")
		        .withRequiredArg()
		        .withArgName("EXE")
		        .create();
		
		opts.createOption("printer")
		        .withDescription("The printer class to use.")
		        .withRequiredArg()
		        .withArgName("CLASS")
		        .create();
		
		opts.createOption('g', "generate-refs")
		        .withDescription("Dumps the result of a parse & print operation to a file which is put to the directory containing the reference files")
		        .create();
		
		opts.createOption("visitors")
		        .withDescription("A list of visitors which are applied to the AST in the order given on the command line.")
		        .withRequiredArg()
		        .withArgName("CLASSES")
		        .create();
		
		opts.createOption("generate-plot-data")
		        .withDescription("Generates a data file that can be used in plots")
		        .create();
		
		opts.createOption("numWarmUps")
		        .withDescription("The number of warm up iterations.")
		        .withRequiredArg()
		        .withArgName("N")
		        .create();
		
		opts.createOption("numIterations")
		        .withDescription("The number of iterations for each sample.")
		        .withRequiredArg()
		        .withArgName("INT")
		        .create();
		
		opts.createOption("numSamples")
		        .withDescription("The number of iterations for each sample.")
		        .withRequiredArg()
		        .withArgName("INT")
		        .create();
		
		return opts;
	}
	
	private void printHelpMessage(Options options)
	{
		options.help(System.out);
		
		System.out.println("The following tests can be performed:");
		for (TestType test : TestType.values())
		{
			System.out.println("- " + test.getOptionName());
		}
	}
	
	private void printArgsErrorMessage(OptionException e)
	{
		System.err.println(e.getMessage());
		System.err.println("Try `--help' for more information.");
	}
	
	// =========================================================================
	
	public static enum TestType implements OptionEnum
	{
		parseAndPrint("parseAndPrint"), speedTest("speedTest");
		
		// =====================================================================
		
		private final String optionName;
		
		private TestType(String optionName)
		{
			this.optionName = optionName;
		}
		
		@Override
		public String getOptionName()
		{
			return optionName;
		}
	}
}
