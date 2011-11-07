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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.fau.cs.osr.ptk.common.ParserInterface;
import de.fau.cs.osr.ptk.common.test.FileContent;
import de.fau.cs.osr.ptk.common.test.ParserTestResources;
import de.fau.cs.osr.utils.ConsoleProgressBar;
import de.fau.cs.osr.utils.FmtIllegalArgumentException;
import de.fau.cs.osr.utils.StopWatch;
import de.fau.cs.osr.utils.getopt.Options;

public class ParserSpeedTest
{
	private Options commandLine;
	
	private ParserTestResources resources;
	
	private ParserTestBase common;
	
	private File baseDirectory;
	
	private boolean generatePlotData;
	
	// =========================================================================
	
	public void run(Options options) throws TestFailedException
	{
		this.commandLine = options;
		
		verifyCommandLine();
		
		setUpTestResources();
		
		setUpParserTestBase();
		
		testSpeed();
	}
	
	// =========================================================================
	
	private void verifyCommandLine()
	{
		commandLine.expected("parser");
		
		commandLine.expected("base");
		
		commandLine.expected("in");
		
		commandLine.optional("generate-plot-data");
		
		commandLine.optional("numWarmUps");
		
		commandLine.optional("numIterations");
		
		commandLine.optional("numSamples");
		
		commandLine.checkForInvalidOptions();
	}
	
	// =========================================================================
	
	private void setUpParserTestBase()
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
		
		common = new ParserTestBase(resources, parserClass);
	}
	
	private void setUpTestResources()
	{
		String base = commandLine.value("base");
		
		baseDirectory = new File(base);
		
		Helpers.checkDirectory(base, "Base", baseDirectory);
		
		resources = new ParserTestResources(baseDirectory);
	}
	
	// =========================================================================
	
	public void testSpeed() throws TestFailedException
	{
		if (commandLine.has("generate-plot-data"))
			generatePlotData = true;
		
		if (!generatePlotData)
			System.out.println("Running speed test ...");
		else
			System.err.println("Running speed test ...");
		
		List<FileContent> wikitext = loadInputFiles();
		
		List<FileContent> wikitextBySize = new LinkedList<FileContent>(wikitext);
		Collections.sort(wikitextBySize, new Comparator<FileContent>()
		{
			@Override
			public int compare(FileContent o1, FileContent o2)
			{
				int a = o1.getContent().length();
				int b = o2.getContent().length();
				return (a < b) ? -1 : ((a == b) ? 0 : +1);
			}
		});
		
		int warmUpRounds = 1;
		if (commandLine.has("numWarmUps"))
			warmUpRounds = Integer.parseInt(commandLine.value("numWarmUps"));
		
		int numIterations = 1;
		if (commandLine.has("numIterations"))
			numIterations = Integer.parseInt(commandLine.value("numIterations"));
		
		int numSamples = 1;
		if (commandLine.has("numSamples"))
			numSamples = Integer.parseInt(commandLine.value("numSamples"));
		
		if (!generatePlotData)
			System.out.println("Warming up ...");
		else
			System.err.println("Warming up ...");
		
		warmUp(wikitext, warmUpRounds);
		
		if (!generatePlotData)
			System.out.println();
		else
			System.err.println();
		
		if (!generatePlotData)
		{
			System.out.println();
			System.out.println("Measuring global average ...");
			
			measureGloablAverage(wikitext, numIterations, numSamples);
			
			System.out.println();
		}
		
		if (!generatePlotData)
		{
			System.out.println();
			System.out.println("Measuring individual articles ...");
		}
		else
		{
			System.err.println();
			System.err.println("Measuring individual articles ...");
		}
		
		measureIndividualArticles(wikitextBySize, numIterations * 2, numSamples);
		
		if (!generatePlotData)
			System.out.println();
		else
			System.err.println();
	}
	
	private void warmUp(List<FileContent> wikitext, int warmUpRounds) throws TestFailedException
	{
		ConsoleProgressBar progressBar =
		        new ConsoleProgressBar(0, warmUpRounds, 40);
		
		progressBar.setOut(generatePlotData ? System.err : System.out);
		progressBar.redraw();
		
		for (int i = 0; i < warmUpRounds; ++i)
		{
			parseAll(wikitext);
			progressBar.advance();
		}
	}
	
	private void measureGloablAverage(List<FileContent> wikitext, int numIterations, int numSamples) throws TestFailedException
	{
		ConsoleProgressBar progressBar =
		        new ConsoleProgressBar(0, numSamples, 40);
		
		progressBar.setOut(generatePlotData ? System.err : System.out);
		progressBar.redraw();
		
		StopWatch watch = new StopWatch();
		
		double[] samples = new double[numSamples];
		
		double average = 0;
		
		int factor = wikitext.size() * numIterations;
		
		for (int sample = 0; sample < numSamples; ++sample)
		{
			watch.start();
			{
				for (int iter = 0; iter < numIterations; ++iter)
				{
					parseAll(wikitext);
				}
			}
			watch.stop();
			
			double delta = watch.getElapsedTime() / (double) factor;
			samples[sample] = delta;
			average += delta;
			
			progressBar.advance();
		}
		
		average /= numSamples;
		
		double stddev = computeStdDev(samples, average);
		
		if (!generatePlotData)
		{
			System.out.println("AVG: " + average);
			System.out.println("SD:  " + stddev);
		}
	}
	
	private void measureIndividualArticles(List<FileContent> wikitext, final int numIterations, final int numSamples) throws TestFailedException
	{
		ConsoleProgressBar progressBar =
		        new ConsoleProgressBar(0, wikitext.size(), 40);
		
		progressBar.setOut(generatePlotData ? System.err : System.out);
		progressBar.redraw();
		
		StopWatch watch = new StopWatch();
		
		for (FileContent w : wikitext)
		{
			double[] samples = new double[numSamples];
			
			double average = 0;
			
			for (int sample = 0; sample < numSamples; ++sample)
			{
				watch.start();
				{
					try
					{
						for (int iter = 0; iter < numIterations; ++iter)
							common.parse(w, null);
					}
					catch (Exception e)
					{
						throw new TestFailedException("Test failed for `" + w.getFile().getName() + "'!", e);
					}
				}
				watch.stop();
				
				double delta = watch.getElapsedTime() / (double) numIterations;
				samples[sample] = delta;
				average += delta;
			}
			
			average /= numSamples;
			
			double stddev = computeStdDev(samples, average);
			
			int size = w.getContent().length();
			double ratio = average / size;
			
			if (!generatePlotData)
			{
				//System.out.println("Article: " + w.getFile().getName());
				//System.out.println("Size: " + size);
				//System.out.println("A/S:  " + ratio);
				//System.out.println("AVG:  " + average);
				//System.out.println("SD:   " + stddev);
			}
			else
			{
				System.out.print(w.getFile().getName());
				System.out.print(' ');
				System.out.print(size);
				System.out.print(' ');
				System.out.print(ratio);
				System.out.print(' ');
				System.out.print(average);
				System.out.print(' ');
				System.out.print(stddev);
				System.out.println();
			}
			
			progressBar.advance();
		}
	}
	
	private double computeStdDev(double[] samples, double average)
	{
		double stddev = 0;
		
		for (int sample = 0; sample < samples.length; ++sample)
		{
			double t = samples[sample] - average;
			stddev += t * t;
		}
		
		return Math.sqrt(stddev);
	}
	
	private void parseAll(List<FileContent> wikitext) throws TestFailedException
	{
		for (FileContent w : wikitext)
		{
			try
			{
				common.parse(w, null);
			}
			catch (Exception e)
			{
				w.getFile().renameTo(new File(w.getFile().getAbsolutePath() + ".disabled"));
				System.err.println("Test failed for `" + w.getFile().getName() + "'!");
				//throw new TestFailedException("Test failed for `" + w.getFile().getName() + "'!", e);
			}
		}
	}
	
	private List<FileContent> loadInputFiles() throws TestFailedException
	{
		String in = commandLine.value("in");
		Helpers.checkDirectory(baseDirectory, in, "Input");
		
		final List<File> input = resources.gather(in, "*.wikitext", true);
		
		final List<FileContent> wikitext = new LinkedList<FileContent>();
		
		for (File wikitextFile : input)
		{
			try
			{
				wikitext.add(new FileContent(wikitextFile));
			}
			catch (Exception e)
			{
				throw new TestFailedException("Cannot load file `" + wikitextFile.getName() + "'!", e);
			}
		}
		return wikitext;
	}
}
