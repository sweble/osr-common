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
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import xtc.parser.ParseError;
import xtc.parser.Result;
import xtc.parser.SemanticValue;
import de.fau.cs.osr.ptk.nodegen.parser.AstNodeGenParser;
import de.fau.cs.osr.ptk.nodegen.parser.RatsDocGrammar;
import de.fau.cs.osr.utils.FmtInternalLogicError;

/**
 * @goal generate
 * @phase generate-sources
 */
public class AstNodeGeneratorMojo
        extends
            AbstractMojo
{
	
	public static final String[] ratsExtensions = { "rats" };
	
	// =========================================================================
	
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	private MavenProjectProxy pproxy;
	
	/**
	 * Base directory in which to look for input files and directories.
	 * 
	 * @parameter expression="${project.basedir}/src/main/rats"
	 */
	private File baseDir;
	
	/**
	 * Output directory into which the .java files are written.
	 * 
	 * @parameter expression="${project.build.directory}/generated-sources/rats"
	 */
	private File outputDir;
	
	/**
	 * Input files from which parsers are generated. If this parameter is not
	 * configured the directory src/main/rats will be searched for Rats! grammar
	 * files.
	 * 
	 * Input files can be either Rats! grammar files or directories. Directories
	 * will be searched for Rats! grammar files recursively.
	 * 
	 * If a given file or directory is given with an absolute path, the file or
	 * directory is used as is. Otherwise, the path will be prefixed by a base
	 * directory.
	 * 
	 * The base directory is either a directory configured in the POM file (see
	 * parameter baseDir) or the project base directory (see
	 * ${project.basedir}).
	 * 
	 * @parameter
	 */
	private String[] ratsInputFiles;
	
	// =========================================================================
	
	private File projectBaseDir;
	
	// =========================================================================
	
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		pproxy = new MavenProjectProxy(project);
		
		// Set-up absolute project base directory.
		projectBaseDir = new File(pproxy.getBasedir().getAbsolutePath());
		
		// Set-up Rats! grammar base directory.
		if (!baseDir.isAbsolute())
			baseDir = new File(projectBaseDir, baseDir.getPath());
		
		// Prepare output directory and make it a source root
		this.outputDir = getAbsolutePath(this.outputDir,
		        projectBaseDir);
		pproxy.addCompileSourceRoot(outputDir.getPath());
		
		List<File> inputs;
		if (ratsInputFiles != null)
		{
			inputs = new ArrayList<File>(ratsInputFiles.length);
			for (String input : ratsInputFiles)
			{
				inputs.add(new File(input));
			}
		}
		else
		{
			inputs = new ArrayList<File>();
			inputs.add(baseDir);
			
			getLog().debug("Use grammar files found in base directory");
		}
		
		// Gather all grammar files
		Set<File> sources = new HashSet<File>();
		for (File input : inputs)
		{
			resolveInput(getAbsolutePath(input, baseDir), sources);
		}
		
		getLog().info("Parsing " + sources.size() + " grammar files");
		
		for (File source : sources)
		{
			try
			{
				generate(source);
			}
			catch (Exception e)
			{
				String msg = "Exception occured when processing file: " +
				        source.getAbsolutePath();
				
				getLog().error(msg);
				
				throw new MojoExecutionException(msg, e);
			}
		}
	}
	
	// =========================================================================
	
	protected void generate(File file)
	        throws
	            MojoExecutionException,
	            MojoFailureException,
	            IOException
	{
		getLog().debug("Generationg ast nodes from " + file.getName());
		
		String source = FileUtils.readFileToString(file);
		
		AstNodeGenParser parser =
		            new AstNodeGenParser(
		                    new StringReader(source),
		                    file.getAbsolutePath());
		
		parser.setLog(getLog());
		
		Result result = parser.pRatsDocGrammar(0);
		
		if (result.hasValue())
		{
			SemanticValue v = (SemanticValue) result;
			
			if (v.value instanceof RatsDocGrammar)
			{
				transform((RatsDocGrammar) v.value);
			}
			else
			{
				throw new FmtInternalLogicError();
			}
		}
		else
		{
			ParseError error = (ParseError) result;
			
			if (error.index == -1)
			{
				throw new ParseException(
				            "no information available.");
			}
			else
			{
				throw new ParseException(
				            error.msg,
				            parser.location(error.index));
			}
		}
	}
	
	private void transform(RatsDocGrammar value) throws IOException
	{
		RatsDocGrammarVisitor visitor =
		        new RatsDocGrammarVisitor();
		
		visitor.go(value);
		
		for (AstNodeSpec spec : visitor.getGeneratedNodes())
		{
			File outDir = outputDir;
			
			String packageName = spec.getPackageName();
			
			//			if (packageName == null)
			//				packageName = grammarInfo.getPackageName();
			
			if (packageName != null)
			{
				outDir = new File(outDir, packageName.replace('.', File.separatorChar));
			}
			
			// Assure that the output directory exists (our Rats! will fail)
			outDir.mkdirs();
			
			File outFile = new File(outDir, spec.getNodeName() + ".java");
			
			FileUtils.writeStringToFile(outFile, spec.getSource());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void resolveInput(File input, Set<File> sources)
	{
		if (input.isDirectory())
		{
			Iterator<File> fileIterator = FileUtils.iterateFiles(input,
			        ratsExtensions, true);
			
			while (fileIterator.hasNext())
			{
				sources.add(fileIterator.next());
			}
		}
		else
		{
			sources.add(input);
		}
	}
	
	protected File getAbsolutePath(File path, File baseDir)
	{
		if (path.isAbsolute())
		{
			return path;
		}
		else
		{
			return new File(baseDir, path.getPath());
		}
	}
	
	// =========================================================================
	
	protected void setProjectProxy(MavenProjectProxy proxy)
	{
		this.pproxy = proxy;
	}
	
	protected void setOutputDir(File outputDir)
	{
		this.outputDir = outputDir;
	}
}
