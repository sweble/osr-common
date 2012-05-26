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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.ComparisonFailure;

import de.fau.cs.osr.ptk.common.AstPrinterInterface;
import de.fau.cs.osr.ptk.common.ParserInterface;
import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.ptk.common.test.ParserTestCommon;
import de.fau.cs.osr.ptk.common.test.ParserTestResources;
import de.fau.cs.osr.utils.FmtIllegalArgumentException;
import de.fau.cs.osr.utils.getopt.MissingOptionException;
import de.fau.cs.osr.utils.getopt.Options;

public class ParseAndPrintTest
{
	private Options commandLine;
	
	private ParserTestResources resources;
	
	private ParserTestCommon common;
	
	private File baseDirectory;
	
	private List<AstVisitor> visitors;
	
	private AstPrinterInterface printer;
	
	// =========================================================================
	
	public void run(Options options) throws TestFailedException
	{
		this.commandLine = options;
		
		verifyCommandLine();
		
		setUpTestResources();
		
		setUpParserTestCommon();
		
		loadVisitors();
		
		loadPrinter();
		
		testParseAndPrint();
	}
	
	// =========================================================================
	
	private void verifyCommandLine()
	{
		commandLine.expected("parser");
		
		commandLine.optional("visitors");
		
		commandLine.expected("base");
		
		commandLine.expected("in");
		
		commandLine.expected("printer");
		
		commandLine.optional("generate-refs");
		if (commandLine.has("generate-refs"))
		{
			commandLine.expected("ref");
			
			commandLine.ignore("diff");
			
			commandLine.optional("viewer");
		}
		else
		{
			commandLine.optional("ref");
			
			// we only ask for `diff' if actually have to diff stuff
			commandLine.optional("diff");
			
			commandLine.ignore("viewer");
		}
		
		commandLine.checkForInvalidOptions();
	}
	
	// =========================================================================
	
	private void setUpParserTestCommon()
	{
		String parserClassName = commandLine.value("parser");
		
		Class<?> parserClass = Helpers.loadClass(parserClassName, "parser");
		
		Object parser = Helpers.instantiateClass(parserClassName, parserClass, "parser");
		
		if (!(parser instanceof ParserInterface))
		{
			throw new FmtIllegalArgumentException(
			        "Not a valid parser class `%s' (does not inherit from IParser).",
			        parserClassName);
		}
		
		common = new ParserTestCommon(resources, parserClass);
	}
	
	private void setUpTestResources()
	{
		String base = commandLine.value("base");
		
		baseDirectory = new File(base);
		
		Helpers.checkDirectory(base, "Base", baseDirectory);
		
		resources = new ParserTestResources(baseDirectory);
	}
	
	private void loadVisitors()
	{
		visitors = new LinkedList<AstVisitor>();
		
		if (!commandLine.has("visitors"))
			return;
		
		LinkedList<String> visitorNames = new LinkedList<String>();
		
		for (String visitor : commandLine.value("visitors").split("[, ]"))
		{
			if (!visitor.isEmpty())
				visitorNames.add(visitor);
		}
		
		for (String visitorName : visitorNames)
		{
			Class<?> visitorClass = Helpers.loadClass(visitorName, "visitor");
			
			Object visitor = Helpers.instantiateClass(visitorName, visitorClass, "visitor");
			
			if (!(visitor instanceof AstVisitor))
			{
				throw new FmtIllegalArgumentException(
				            "Not a valid visitor class `%s' (does not inherit from Visitor).",
				            visitorName);
			}
			
			visitors.add((AstVisitor) visitor);
		}
	}
	
	private void loadPrinter()
	{
		String printerName = commandLine.value("printer");
		
		Class<?> printerClass = Helpers.loadClass(printerName, "printer");
		
		Object printer_ = Helpers.instantiateClass(printerName, printerClass, "printer");
		
		if (!(printer_ instanceof AstPrinterInterface))
		{
			throw new FmtIllegalArgumentException(
			            "Not a valid printer class `%s' (does not inherit from IAstPrinter).",
			            printerName);
		}
		
		printer = (AstPrinterInterface) printer_;
	}
	
	// =========================================================================
	
	public void testParseAndPrint() throws TestFailedException
	{
		System.out.println("Running parsing and printing test ...");
		
		String in = commandLine.value("in");
		Helpers.checkDirectory(baseDirectory, in, "Input");
		
		String ref = null;
		if (commandLine.has("ref"))
		{
			ref = commandLine.value("ref");
			if (ref.isEmpty())
			{
				ref = null;
			}
			else
			{
				Helpers.checkDirectory(baseDirectory, ref, "Reference");
			}
		}
		
		String extension = printer.getPrintoutType();
		if ((ref == null) && (!"wikitext".equals(extension)))
			throw new FmtIllegalArgumentException(
			        "If the type of printout produced is not `wikitext', " +
			                "the `ref' option must be given!. " +
			                "The type of printout produced is `%s'. ",
			        extension);
		
		final List<File> input = resources.gather(in, "*.wikitext", true);
		
		boolean generateRefs = commandLine.has("generate-refs");
		
		for (File wikitextFile : input)
		{
			System.out.format("Test %s...\n", wikitextFile.getName());
			
			File reftextFile = wikitextFile;
			if (ref != null)
				reftextFile = ParserTestResources.rebase(
				            wikitextFile,
				            in,
				            ref,
				            extension,
				            generateRefs);
			
			try
			{
				if (generateRefs)
				{
					String reftext = common.parseAndPrint(
					        visitors.toArray(new AstVisitor[0]),
					        printer,
					        wikitextFile);
					
					if (!reftextFile.exists())
						generateReftextFile(reftext, reftextFile);
				}
				else
				{
					common.parseAndPrintTest(
					        visitors.toArray(new AstVisitor[0]),
					        printer,
					        wikitextFile,
					        reftextFile);
				}
			}
			catch (ComparisonFailure e)
			{
				showDiff(
				        e.getActual(),
				        e.getExpected(),
				        reftextFile,
				        true);
			}
			catch (Exception e)
			{
				throw new TestFailedException("Test failed!", e);
			}
		}
	}
	
	protected void generateReftextFile(String reftext, File reftextFile) throws IOException
	{
		String shortPath = reftextFile.getAbsolutePath();
		String prefix = baseDirectory.getAbsolutePath();
		
		shortPath = shortPath.replace(prefix, "");
		
		System.out.format("Generating reference file: %s\n", shortPath);
		
		org.apache.commons.io.FileUtils.writeStringToFile(reftextFile, reftext);
		
		previewFile(reftext, reftextFile, true);
	}
	
	private void previewFile(String reftext, File reftextFile, boolean wait)
	{
		if (!commandLine.has("viewer"))
			throw new MissingOptionException(Arrays.asList("viewer"));
		
		String diffProgram = commandLine.value("viewer");
		
		if (reftextFile == null)
			reftextFile = Helpers.fillTempFile(reftext, "reference");
		
		Process p;
		try
		{
			p = Runtime.getRuntime().exec(
			                new String[] {
			                        diffProgram,
			                        reftextFile.getAbsolutePath() });
			
			if (wait)
				p.waitFor();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Execution of diff tool failed", e);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException("Diff tool failed", e);
		}
	}
	
	protected void showDiff(String result, String reference, File referenceFile, boolean wait)
	{
		if (!commandLine.has("diff"))
			throw new MissingOptionException(Arrays.asList("diff"));
		
		String diffProgram = commandLine.value("diff");
		
		File resultFile;
		resultFile = Helpers.fillTempFile(result, "result");
		
		if (referenceFile == null)
			referenceFile = Helpers.fillTempFile(reference, "reference");
		
		Process p;
		try
		{
			p = Runtime.getRuntime().exec(
			                new String[] {
			                        diffProgram,
			                        resultFile.getAbsolutePath(),
			                        referenceFile.getAbsolutePath() });
			
			if (wait)
				p.waitFor();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Execution of diff tool failed", e);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException("Diff tool failed", e);
		}
	}
	
}
