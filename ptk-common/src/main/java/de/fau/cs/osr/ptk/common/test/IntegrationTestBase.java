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
package de.fau.cs.osr.ptk.common.test;

import static de.fau.cs.osr.ptk.common.test.ParserTestResources.fileSeparatorToUnix;
import static de.fau.cs.osr.ptk.common.test.ParserTestResources.lineEndToUnix;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import xtc.parser.ParseException;
import de.fau.cs.osr.ptk.common.AstPrinterInterface;
import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.ptk.common.ParserInterface;
import de.fau.cs.osr.ptk.common.ast.AstNode;

public abstract class IntegrationTestBase
{
	private static ParserTestResources resources;
	
	private static Pattern inputPathToRefPathSearch;
	
	private static String inputPathToRefPathReplaceWith;
	
	// =========================================================================
	
	static
	{
		URL url = IntegrationTestBase.class.getResource("/");
		
		resources = new ParserTestResources(new File(url.getFile()));
	}
	
	// =========================================================================
	
	public static void setResources(ParserTestResources resources)
	{
		IntegrationTestBase.resources = resources;
	}
	
	protected static ParserTestResources getResources()
	{
		return resources;
	}
	
	/**
	 * Get a list of resources that can be directly returned from a method
	 * annotated with @Parameters.
	 * 
	 * <b>Important:</b> Always use the UNIX file separator '/'.
	 * 
	 * @param inputSubDir
	 *            Where to find the resources.
	 * @param filterRx
	 *            Only resources whose filenames match this regular expression
	 *            will be gathered.
	 * @param recursive
	 *            Whether to recurse in sub directories.
	 * @return The list of gathered resources.
	 */
	protected static List<Object[]> gather(
			String inputSubDir,
			String filterRx,
			boolean recursive)
	{
		LinkedList<Object[]> inputs = new LinkedList<Object[]>();
		
		for (File file : getResources().gather(inputSubDir, filterRx, recursive))
			inputs.add(new Object[] { file.getName(), file });
		
		return inputs;
	}
	
	// =========================================================================
	
	private final boolean randomRefName;
	
	private final boolean putInitialRefFilesIntoRefFileDir;
	
	// =========================================================================
	
	public IntegrationTestBase()
	{
		this.randomRefName = false;
		this.putInitialRefFilesIntoRefFileDir = false;
	}
	
	/**
	 * @param randomRefName
	 *            If no reference file exists, a reference file is generated
	 *            with a random name.
	 * @param putInitialRefFilesIntoRefFileDir
	 *            If no reference file exists, a reference file is generated
	 *            from the actual result. This file is then placed into the
	 *            working directory (which is usually the project directory).
	 *            However, it can also be placed into the directory for
	 *            reference files. This is <b>dangerous</b>! You might forget
	 *            that you first have to look over the initial reference file to
	 *            assure that the actual result was correct!
	 */
	public IntegrationTestBase(
			boolean randomRefName,
			boolean putInitialRefFilesIntoRefFileDir)
	{
		this.randomRefName = randomRefName;
		this.putInitialRefFilesIntoRefFileDir = putInitialRefFilesIntoRefFileDir;
	}
	
	// =========================================================================
	
	/**
	 * Parse an input file, use the specified visitors for post-processing,
	 * print the resulting AST as text using the given printer instance and
	 * compare the result with a file with the expected output.
	 * 
	 * The expected output has to be located in a file that has the same name as
	 * the input file but possibly resides in a different sub-directory.
	 * 
	 * <b>Important:</b> Always use the UNIX file separator '/'.
	 */
	protected void parsePrintAndCompare(
			File inputFile,
			AstVisitor[] visitors,
			String inputSubDir,
			String expectedSubDir,
			AstPrinterInterface printer) throws IOException, ParseException
	{
		AstNode ast = parse(inputFile, visitors);
		
		String actual = printToString(ast, printer);
		
		File expectedFile = ParserTestResources.rebase(
				inputFile,
				inputSubDir,
				expectedSubDir,
				printer.getPrintoutType(),
				true /* don't throw if file doesn't exist */);
		
		compareWithExpectedOrGenerateExpectedFromActual(expectedFile, actual);
	}
	
	/**
	 * Override and return parser to use in tests.
	 */
	protected abstract ParserInterface instantiateParser();
	
	// =========================================================================
	
	private AstNode parse(File inputFile, AstVisitor[] visitors) throws IOException, ParseException
	{
		ParserInterface parser = instantiateParser();
		
		if (visitors != null)
			parser.addVisitors(Arrays.asList(visitors));
		
		FileContent inputFileContent = new FileContent(inputFile);
		
		return parser.parseArticle(
				inputFileContent.getContent(),
				inputFile.getAbsolutePath());
	}
	
	public String printToString(AstNode ast, AstPrinterInterface printer) throws IOException
	{
		StringWriter writer = new StringWriter();
		
		printer.print(ast, writer);
		
		String result = writer.toString();
		
		// We always operate with UNIX line end '\n':
		result = ParserTestResources.lineEndToUnix(result);
		
		return resources.stripBaseDirectoryAndFixPath(result);
	}
	
	private void compareWithExpectedOrGenerateExpectedFromActual(
			File expectedFile,
			String actual) throws IOException
	{
		if (!expectedFile.exists())
		{
			File create;
			if (putInitialRefFilesIntoRefFileDir)
			{
				String dir = expectedFile.getParentFile().getAbsolutePath();
				
				// We always operate with UNIX separator '/':
				fileSeparatorToUnix(dir);
				
				if (inputPathToRefPathSearch == null)
					inputPathToRefPathSearch = Pattern.compile(
							"(.*?)" + Pattern.quote("/target/test-classes/"));
				
				if (!inputPathToRefPathSearch.matcher(dir).find())
					Assert.fail(
							"Reference file did not exist! " +
									"FAILED TO WRITE REFERENCE FILE!");
				
				if (inputPathToRefPathReplaceWith == null)
					inputPathToRefPathReplaceWith =
							"$1" + Pattern.quote("/src/test/resources/");
				
				dir = inputPathToRefPathSearch.matcher(dir).replaceAll(inputPathToRefPathReplaceWith);
				create = new File(dir);
			}
			else
			{
				create = new File("").getAbsoluteFile();
			}
			
			if (randomRefName)
			{
				create = File.createTempFile(expectedFile.getName() + "-", "", create);
			}
			else
			{
				create = new File(create, expectedFile.getName());
			}
			
			FileUtils.writeStringToFile(create, actual);
			Assert.fail(
					"Reference file did not exist! " +
							"Wrote initial reference file to: " +
							create.getAbsolutePath());
		}
		else
		{
			FileContent reftext = new FileContent(expectedFile);
			
			// We always operate with UNIX line end '\n':
			String reference = lineEndToUnix(reftext.getContent());
			
			Assert.assertEquals(reference, actual);
		}
	}
}
