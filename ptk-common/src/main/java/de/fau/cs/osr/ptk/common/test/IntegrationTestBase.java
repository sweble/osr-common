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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import xtc.parser.ParseException;
import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.ptk.common.ParserInterface;
import de.fau.cs.osr.ptk.common.PrinterInterface;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.utils.WrappedException;

public abstract class IntegrationTestBase<T extends AstNode<T>>
{
	private static TestResourcesFixture resources;
	
	// =========================================================================
	
	static
	{
		URL url = IntegrationTestBase.class.getResource("/");
		
		try
		{
			resources = new TestResourcesFixture(new File(url.getFile()));
		}
		catch (FileNotFoundException e)
		{
			throw new WrappedException(e);
		}
	}
	
	// =========================================================================
	
	public static void setResources(TestResourcesFixture resources)
	{
		IntegrationTestBase.resources = resources;
	}
	
	protected static TestResourcesFixture getResources()
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
			AstVisitor<T>[] visitors,
			String inputSubDir,
			String expectedSubDir,
			PrinterInterface printer) throws IOException, ParseException
	{
		Object ast = parse(inputFile, visitors);
		
		String actual = printToString(ast, printer);
		
		File expectedFile = TestResourcesFixture.rebase(
				inputFile,
				inputSubDir,
				expectedSubDir,
				printer.getPrintoutType(),
				true /* don't throw if file doesn't exist */);
		
		FileCompare cmp = new FileCompare(resources);
		cmp.compareWithExpectedOrGenerateExpectedFromActual(expectedFile, actual);
	}
	
	/**
	 * Override and return parser to use in tests.
	 */
	protected abstract ParserInterface<T> instantiateParser();
	
	// =========================================================================
	
	private Object parse(File inputFile, AstVisitor<T>... visitors) throws IOException, ParseException
	{
		ParserInterface<T> parser = instantiateParser();
		
		for (AstVisitor<T> visitor : visitors)
		{
			parser.addVisitor(visitor);
		}
		
		FileContent inputFileContent = new FileContent(inputFile);
		
		return parser.parseArticle(
				inputFileContent.getContent(),
				inputFile.getAbsolutePath());
	}
	
	public String printToString(Object ast, PrinterInterface printer) throws IOException
	{
		StringWriter writer = new StringWriter();
		
		printer.print(ast, writer);
		
		String result = writer.toString();
		
		// We always operate with UNIX line end '\n':
		result = TestResourcesFixture.lineEndToUnix(result);
		
		return resources.stripBaseDirectoryAndFixPath(result);
	}
}
