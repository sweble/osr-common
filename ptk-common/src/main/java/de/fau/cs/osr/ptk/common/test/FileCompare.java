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
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

public class FileCompare
{
	protected final TestResourcesFixture resources;
	
	protected final Pattern noRefReplace;
	
	protected final String noRefReplaceBy;
	
	protected final boolean randomRefName;
	
	// =========================================================================
	
	/**
	 * This constructor generates a file comparison object that writes generated
	 * reference files to the current working directory. The generated files
	 * contain random elements to prevent collisions.
	 */
	public FileCompare(TestResourcesFixture resources)
	{
		this.resources = resources;
		this.noRefReplace = null;
		this.noRefReplaceBy = null;
		this.randomRefName = true;
	}
	
	/**
	 * This constructor generates a file comparison object that tries to write
	 * generated reference files back into the source tree.
	 * 
	 * This is done by replacing <code>pathPatternToReplace</code> with
	 * <code>pathToReplacePatternWith</code> in the path to the reference file
	 * (which initially would be located in the output directory of the
	 * project).
	 */
	public FileCompare(
			TestResourcesFixture resources,
			String pathPatternToReplace,
			String pathToReplacePatternWith,
			boolean randomRefName)
	{
		this.resources = resources;
		this.noRefReplace = Pattern.compile(pathPatternToReplace);
		this.noRefReplaceBy = pathToReplacePatternWith;
		this.randomRefName = randomRefName;
	}
	
	// =========================================================================
	
	public void printTest(File expectedFile, String actual) throws IOException
	{
		actual = fixActualText(actual);
		
		checkReferenceFile(expectedFile, actual);
		
		assertEquals(expectedFile, actual);
	}
	
	private String fixActualText(String actual)
	{
		// For Windows builds:
		actual = actual.replace("\r\n", "\n");
		
		return resources.stripBaseDirectoryAndFixPath(actual);
	}
	
	private void checkReferenceFile(File expectedFile, String actual) throws IOException
	{
		if (!expectedFile.exists())
		{
			File create;
			if (noRefReplace != null)
			{
				String dir = expectedFile.getParentFile().getAbsolutePath();
				
				if (!noRefReplace.matcher(dir).find())
					Assert.fail("FAILED TO WRITE REFERENCE FILE!");
				
				dir = noRefReplace.matcher(dir).replaceAll(noRefReplaceBy);
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
	}
	
	private void assertEquals(File expectedFile, String actual) throws IOException
	{
		FileContent reftext = new FileContent(expectedFile);
		String reference = reftext.getContent();
		
		Assert.assertEquals(reference, actual);
	}
}
