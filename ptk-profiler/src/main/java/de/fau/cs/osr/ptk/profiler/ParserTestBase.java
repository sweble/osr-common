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

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import xtc.parser.ParseException;
import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.ptk.common.GenericPrinterInterface;
import de.fau.cs.osr.ptk.common.ParserInterface;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.test.FileContent;
import de.fau.cs.osr.ptk.common.test.ParserTestCommon;
import de.fau.cs.osr.ptk.common.test.ParserTestResources;

public class ParserTestBase
		extends
			ParserTestCommon
{
	private final Class<?> parser;
	
	// =========================================================================
	
	public ParserTestBase(ParserTestResources resources, Class<?> parserClass)
	{
		super(resources);
		this.parser = parserClass;
	}
	
	// =========================================================================
	
	public String parseAndPrint(
			final AstVisitor[] visitors,
			GenericPrinterInterface printer,
			File wikitextFile) throws IOException, ParseException
	{
		FileContent wikitext = new FileContent(wikitextFile);
		
		AstNode ast = parse(wikitext, visitors);
		
		return printToString(ast, printer);
	}
	
	public void parseAndPrintTest(
			final AstVisitor[] visitors,
			GenericPrinterInterface printer,
			File wikitextFile,
			File reftextFile) throws IOException, ParseException
	{
		FileContent wikitext = new FileContent(wikitextFile);
		AstNode ast = parse(wikitext, visitors);
		
		String result = printToString(ast, printer);
		if (!reftextFile.exists())
		{
			File create;
			if (noRefReplace != null)
			{
				String dir = reftextFile.getParentFile().getAbsolutePath();
				
				if (!noRefReplace.matcher(dir).find())
					Assert.fail(
							"Reference file did not exist! " +
									"FAILED TO WRITE REFERENCE FILE!");
				
				dir = noRefReplace.matcher(dir).replaceAll(noRefReplaceBy);
				create = new File(dir);
				
				System.out.println(create.getAbsolutePath());
			}
			else
			{
				create = new File("").getAbsoluteFile();
			}
			
			if (randomRefName)
			{
				create = File.createTempFile(reftextFile.getName() + "-", "", create);
			}
			else
			{
				create = new File(create, reftextFile.getName());
			}
			
			FileUtils.writeStringToFile(create, result);
			Assert.fail(
					"Reference file did not exist! " +
							"Wrote initial reference file to: " +
							create.getAbsolutePath());
		}
		
		FileContent reftext = new FileContent(reftextFile);
		String reference = reftext.getContent();
		
		testEquals(wikitext, reftext, result, reference);
	}
	
	// =========================================================================
	
	private void testEquals(
			FileContent wikitext,
			FileContent reftext,
			String result,
			String reference)
	{
		if (!reference.equals(result))
			System.out.println("  FAILED!");
		Assert.assertEquals(reference, result);
	}
	
	// =========================================================================
	
	public AstNode parse(FileContent wikitext, AstVisitor[] visitors)
		throws IOException,
		ParseException
	{
		ParserInterface parser = instantiateParser();
		
		if (visitors != null)
			parser.addVisitors(Arrays.asList(visitors));
		
		return parser.parseArticle(
				wikitext.getContent(),
				wikitext.getFile().getAbsolutePath());
	}
	
	private ParserInterface instantiateParser()
	{
		try
		{
			return (ParserInterface) this.parser.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
