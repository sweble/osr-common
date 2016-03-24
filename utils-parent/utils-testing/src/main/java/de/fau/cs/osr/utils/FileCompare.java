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

package de.fau.cs.osr.utils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Assert;

public class FileCompare
{
	private static final Object IGNORE_TEST = "IGNORE THIS TEST";

	private static Pattern inputPathToRefPathSearch;

	private static String inputPathToRefPathReplaceWith;

	private final TestResourcesFixture resources;

	private final boolean randomRefName;

	private final boolean putInitialRefFilesIntoRefFileDir;

	// =========================================================================

	/**
	 * This constructor generates a file comparison object that writes generated
	 * reference files to the current working directory. The generated files
	 * contain random elements to prevent collisions.
	 */
	public FileCompare(TestResourcesFixture resources)
	{
		this.resources = resources;
		this.randomRefName = false;
		this.putInitialRefFilesIntoRefFileDir = false;
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
			boolean randomRefName,
			boolean putInitialRefFilesIntoRefFileDir)
	{
		this.resources = resources;
		this.randomRefName = randomRefName;
		this.putInitialRefFilesIntoRefFileDir = putInitialRefFilesIntoRefFileDir;
	}

	// =========================================================================

	public void compareWithExpectedOrGenerateExpectedFromActual(
			File expectedFile,
			String actual) throws IOException
	{
		actual = fixActualText(actual);

		checkReferenceFile(expectedFile, actual);

		assertEquals(expectedFile, actual);
	}

	// =========================================================================

	public String fixActualText(String actual)
	{
		return resources.stripBaseDirectoryAndFixPath(
				FileUtils.lineEndToUnix(actual));
	}

	private void checkReferenceFile(
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
				FileUtils.fileSeparatorToUnix(dir);

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

			org.apache.commons.io.FileUtils.writeStringToFile(create, actual, "UTF8");
			Assert.fail(
					"Reference file did not exist! " +
							"Wrote initial reference file to: " +
							create.getAbsolutePath());
		}
	}

	public void assertEquals(File expectedFile, String actual) throws IOException
	{
		FileContent reftext = new FileContent(expectedFile);

		String trimmed = reftext.getContent().trim();
		int i = trimmed.indexOf('\n');
		int j = trimmed.indexOf('\r');
		if ((i == -1) || (j != -1 && j < i))
			i = j;
		if (i != -1)
			trimmed = trimmed.substring(0, i);
		if (trimmed.equals(IGNORE_TEST))
			return;

		// We always operate with UNIX line end '\n':
		String reference = FileUtils.lineEndToUnix(reftext.getContent());

		/* UGLY DANGEROUS INTERMEDIATE EVIL HACK*
		if (!reference.equals(actual))
			FileUtils.writeStringToFile(expectedFile, actual);
		/**/

		Assert.assertEquals(reference, actual);
	}
}
