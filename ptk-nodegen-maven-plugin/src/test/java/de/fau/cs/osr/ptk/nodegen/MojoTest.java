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

package de.fau.cs.osr.ptk.nodegen;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class MojoTest
{
	private File outDir;
	
	private MavenProjectProxy proxy;
	
	// =========================================================================
	
	public MojoTest()
	{
		proxy = new MavenProjectProxy();
		proxy.setBasedir(new File("."));
	}
	
	// =========================================================================
	
	@Test
	public void testEmptyRatsDoc() throws MojoExecutionException, MojoFailureException, IOException
	{
		test("/input/EmptyRatsDoc.rats");
	}
	
	@Test
	public void testNoRatsDoc() throws MojoExecutionException, MojoFailureException, IOException
	{
		test("/input/NoRatsDoc.rats");
	}
	
	@Test
	public void testMisc() throws MojoExecutionException, MojoFailureException, IOException
	{
		test("/input/Misc.rats", 
				"NameValue.java", "Itemize.java", "Item.java", 
				"RatsDocGrammar.java", "RatsDoc.java", "Section.java");
	}
	
	@Test
	public void testReservedWord() throws MojoExecutionException, MojoFailureException, IOException
	{
		test("/input/ReservedWords.rats", "Intro.java");
	}
	
	@Test
	public void testImplements() throws MojoExecutionException, MojoFailureException, IOException
	{
		test("/input/Implements.rats", "ImplementsNode.java");
	}
	
	@Test
	public void testNodeType() throws MojoExecutionException, MojoFailureException, IOException
	{
		test("/input/NodeType.rats", "NodeTypeNode.java");
	}
	
	// =========================================================================
	
	private void test(String input, String... expectedNodes) throws IOException, MojoExecutionException, MojoFailureException, FileNotFoundException
	{
		outDir = makeTempDir();
		System.out.println("Writing results to " + outDir);
		
		generate(input);
		
		FileFilter fileFilter =
		        new FileFilter()
		        {
			        public boolean accept(File file)
			        {
				        return file.isFile() && file.getName().endsWith(".java");
			        }
		        };
		
		File[] files = outDir.listFiles(fileFilter);
		Assert.assertNotNull(files);
		Assert.assertEquals(expectedNodes.length, files.length);
		
		for (File actualFile : files)
		{
			String filename = actualFile.getName();
			
			boolean found = false;
			for (String expectedNode : expectedNodes)
			{
				if (expectedNode.equals(filename))
				{
					found = true;
					break;
				}
			}
			Assert.assertTrue("Unexpected node generated: `" + filename + "'", found);
			
			System.out.println("  Comparing result for ast node: " + filename);
			
			final String regex = " \\* Last generated: [^.]*\\.\n";
			
			String actual = FileUtils.readFileToString(actualFile);
			actual = actual.replaceAll(regex, "");
			
			String expectedPath = "/expected/" + filename + ".ref";
			URL expectedUrl = getClass().getResource(expectedPath);
			if (expectedUrl == null)
				missingExpected(actualFile, actual);
			
			File expectedFile = new File(expectedUrl.getFile());
			String expected = FileUtils.readFileToString(expectedFile);
			expected = expected.replaceAll(regex, "");
			
			Assert.assertEquals(expected, actual);
		}
	}
	
	private void missingExpected(File actualFile, String actual) throws IOException
	{
		File create;
		create = new File("").getAbsoluteFile();
		create = File.createTempFile(actualFile.getName() + "-", "", create);
		
		FileUtils.writeStringToFile(create, actual);
		Assert.fail(
		            "Reference file did not exist! " +
		                    "Wrote initial reference file to: " +
		                    create.getAbsolutePath());
	}
	
	private void generate(String input) throws MojoExecutionException, MojoFailureException, IOException
	{
		System.out.println("Generating ast nodes for grammar: " + input);
		
		URL url = getClass().getResource(input);
		if (url == null)
			throw new FileNotFoundException(input);
		
		File source = new File(url.getFile());
		AstNodeGeneratorMojo mojo = new AstNodeGeneratorMojo();
		mojo.setProjectProxy(proxy);
		mojo.setOutputDir(outDir);
		mojo.generate(source);
	}
	
	public File makeTempDir() throws IOException
	{
		final File temp =
		        File.createTempFile("temp", Long.toString(System.nanoTime()));
		
		if (!(temp.delete()))
			throw new IOException(
			        "Could not delete temp file: " + temp.getAbsolutePath());
		
		if (!(temp.mkdir()))
			throw new IOException(
			        "Could not create temp directory: " + temp.getAbsolutePath());
		
		return temp;
	}
	
}
