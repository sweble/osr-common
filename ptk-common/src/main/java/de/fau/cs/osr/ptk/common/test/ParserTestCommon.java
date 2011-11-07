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
import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import de.fau.cs.osr.ptk.common.GenericPrinterInterface;

public class ParserTestCommon
{
	
	protected final ParserTestResources resources;
	
	protected final Pattern noRefReplace;
	
	protected final String noRefReplaceBy;
	
	protected final boolean randomRefName;
	
	// =========================================================================
	
	public ParserTestCommon(ParserTestResources resources)
	{
		this.resources = resources;
		this.noRefReplace = null;
		this.noRefReplaceBy = null;
		this.randomRefName = true;
	}
	
	public ParserTestCommon(
	        ParserTestResources resources,
	        String noRefReplace,
	        String noRefReplaceBy,
	        boolean randomRefName)
	{
		this.resources = resources;
		this.noRefReplace = Pattern.compile(noRefReplace);
		this.noRefReplaceBy = noRefReplaceBy;
		this.randomRefName = randomRefName;
	}
	
	// =========================================================================
	
	protected void printTest(Object what, GenericPrinterInterface printer, File reftextFile) throws IOException
	{
		String result = printToString(what, printer);
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
		
		testEquals(reftext, result, reference);
	}
	
	private void testEquals(FileContent reftext, String result, String reference)
	{
		if (!reference.equals(result))
			System.out.println("  FAILED!");
		Assert.assertEquals(reference, result);
	}
	
	public String printToString(Object what, GenericPrinterInterface printer) throws IOException
	{
		StringWriter writer = new StringWriter();
		
		printer.print(what, writer);
		
		String result = writer.toString();
		
		// For Windows builds:
		result = result.replace("\r\n", "\n");
		
		return resources.stripBaseDirectoryAndFixPath(result);
	}
	
}
