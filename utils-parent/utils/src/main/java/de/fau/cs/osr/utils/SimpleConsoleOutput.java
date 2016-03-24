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

public class SimpleConsoleOutput
{
	private boolean quiet;

	// =========================================================================

	public boolean isQuiet()
	{
		return quiet;
	}

	public void setQuiet(boolean quiet)
	{
		this.quiet = quiet;
	}

	// =========================================================================
	//  Print information to console
	// =========================================================================

	public void echoQuoted(String x)
	{
		if (!quiet)
			printQuoted(x);
	}

	public static void printQuoted(String x)
	{
		System.out.print("\"\"\"");
		System.out.print(x);
		System.out.println("\"\"\"");
	}

	public void echoSep(String title)
	{
		if (!quiet)
			printSep(title);
	}

	public static void printSep(String title)
	{
		System.out.println(formatSepLine(0, title));
	}

	public void echoSep(int indent, String title)
	{
		if (!quiet)
			printSep(indent, title);
	}

	public static void printSep(int indent, String title)
	{
		System.out.println(formatSepLine(indent, title));
	}

	public void echoBigSep(String title)
	{
		if (!quiet)
			printBigSep(title);
	}

	public static void printBigSep(String title)
	{
		System.out.println();
		String eq80 = StringUtils.strrep("=", 80);
		String sp76 = StringUtils.strrep(" ", 76);
		String spX = StringUtils.strrep(" ", Math.max(75 - title.length(), 1));
		System.out.println(eq80);
		System.out.println("==" + sp76 + "==");
		System.out.println("== " + title + spX + "==");
		System.out.println("==" + sp76 + "==");
		System.out.println(eq80);
		System.out.println();
	}

	public static String formatSepLine(int indent, String title)
	{
		String sep = StringUtils.strrep(' ', indent) + "--[ " + title + " ]";
		String line = sep + StringUtils.strrep("-", Math.max(80 - sep.length(), 2));
		return line;
	}
}
