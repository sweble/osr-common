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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fau.cs.osr.utils.FmtIllegalArgumentException;
import de.fau.cs.osr.utils.StringUtils;

public class TestResourcesFixture
{
	private final File baseDirectory;
	
	// =========================================================================
	
	/**
	 * @param baseDirectory
	 *            Path to a directory in which the resources will be gathered.
	 * 
	 * @throws FileNotFoundException
	 *             Thrown if the given path doesn't exist or doesn't point to a
	 *             directory.
	 */
	public TestResourcesFixture(File baseDirectory) throws FileNotFoundException
	{
		if (!baseDirectory.exists())
			throw new FileNotFoundException(String.format(
					"Given base directory `%s' does not exist",
					baseDirectory));
		
		if (!baseDirectory.isDirectory())
			throw new FileNotFoundException(String.format(
					"Given base directory `%s' is not a directory",
					baseDirectory));
		
		this.baseDirectory = baseDirectory;
	}
	
	/**
	 * @param classLoader
	 *            A class loader used to locate the index file.
	 * @param pathToIndex
	 *            Resource path to a directory in which the resources will be
	 *            gathered. The directory must contain an index file (the
	 *            content of the file doesn't matter).
	 * 
	 * @throws FileNotFoundException
	 *             Thrown if the index file cannot be found in the directory
	 *             pointed to by pathToIndex.
	 */
	public TestResourcesFixture(ClassLoader classLoader, String pathToIndex) throws FileNotFoundException
	{
		this.baseDirectory = findBaseDirectory(classLoader, pathToIndex);
	}
	
	private static File findBaseDirectory(ClassLoader cl, String pathToIndex) throws FileNotFoundException
	{
		URL url = cl.getResource(String.format("%s/index", pathToIndex));
		if (url == null)
			throw new FileNotFoundException(String.format(
					"Cannot find index file in resource path: `%s'",
					pathToIndex));
		
		String path = StringUtils.decodeUsingDefaultCharset(url.getFile());
		return new File(path).getParentFile();
	}
	
	// =========================================================================
	
	public File getBaseDirectory()
	{
		return baseDirectory;
	}
	
	// =========================================================================
	
	/**
	 * Recursively gather files which match a certain pattern.
	 * 
	 * @param subDirectory
	 *            A sub-directory inside baseDirectory. The directory must
	 *            exist.
	 * @param pattern
	 *            Only file names that match this pattern will be returned.
	 * @param recursive
	 *            Look in sub-directories for matches.
	 * @return A list of files that match the criteria.
	 * @throws IllegalArgumentException
	 *             Thrown if one of the given directory paths doesn't exist or
	 *             does not point to a directory.
	 */
	public List<File> gather(
			String subDirectory,
			String pattern,
			final boolean recursive)
	{
		List<File> files = new LinkedList<File>();
		
		final Pattern p = Pattern.compile(pattern);
		
		FilenameFilter filter = new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				Matcher matcher = p.matcher(name);
				if (matcher.matches())
					return true;
				else if (recursive)
					return new File(dir, name).isDirectory();
				else
					return false;
			}
		};
		
		File dir = new File(this.baseDirectory, subDirectory);
		
		if (!dir.exists())
			throw new FmtIllegalArgumentException(
					"Given directory `%s' does not exist",
					dir.getAbsolutePath());
		
		if (!dir.isDirectory())
			throw new FmtIllegalArgumentException(
					"Given directory `%s' is not a directory",
					dir.getAbsolutePath());
		
		gatherRecursive(dir, filter, files);
		Collections.sort(files);
		
		return files;
	}
	
	private static void gatherRecursive(
			File directory,
			FilenameFilter filter,
			List<File> gathered)
	{
		String[] files = directory.list(filter);
		if (files != null)
		{
			for (String fileName : files)
			{
				File file = new File(directory, fileName);
				
				if (file.isDirectory())
				{
					gatherRecursive(file, filter, gathered);
				}
				else
				{
					gathered.add(file);
				}
			}
		}
	}
	
	// =========================================================================
	
	/**
	 * Fix paths found inside a text.
	 * 
	 * Some tests produce texts which then are compared to reference text files
	 * to make sure the output is correct. However, sometimes these texts
	 * contain file system paths in the format
	 * <code>/some/path:line:message</code> that are specific to the execution
	 * environment. Given the specific part of such paths this method removes
	 * the specific part and turns any OS dependent path separator into a unix
	 * separator.
	 * 
	 * @param text
	 *            The original text.
	 * @return The fixed text.
	 */
	public String stripBaseDirectoryAndFixPath(String text)
	{
		String p = Pattern.quote(this.baseDirectory.getAbsolutePath());
		Pattern rx = Pattern.compile(p + "(.*?:)");
		StringBuilder b = new StringBuilder();
		Matcher m = rx.matcher(text);
		
		int begin = 0;
		while (m.find(begin))
		{
			b.append(text.substring(begin, m.start()));
			b.append(m.group(1).replace(File.separatorChar, '/'));
			begin = m.end();
		}
		
		if (begin < text.length())
			b.append(text.substring(begin, text.length()));
		
		return b.toString();
	}
	
	// =========================================================================
	
	/**
	 * Change a path so that the filename it points to is located in a different
	 * directory.
	 * 
	 * <b>Important:</b> Always use the UNIX file separator '/'.
	 * 
	 * @param file
	 *            The file whose filename will be moved to a different directory
	 *            and whose extension will be altered.
	 * @param base
	 *            The part of the original path that will be changed.
	 * @param rebase
	 *            The part of a path which will replace <code>base</code>.
	 */
	public static File rebase(File file, String base, String rebase)
	{
		String unixPath = fileSeparatorToUnix(file.getAbsolutePath());
		File rebasedFile = new File(unixPath.replace(base, rebase));
		
		if (!rebasedFile.exists())
			throw new FmtIllegalArgumentException(
					"Rebased file `%s' does not exist",
					rebasedFile.getAbsolutePath());
		
		return rebasedFile;
	}
	
	/**
	 * Change a path so that the filename it points to is located in a different
	 * directory. Also change the extension of the filename.
	 * 
	 * <b>Important:</b> Always use the UNIX file separator '/'.
	 * 
	 * @param file
	 *            The file whose filename will be moved to a different directory
	 *            and whose extension will be altered.
	 * @param base
	 *            The part of the original path that will be changed.
	 * @param rebase
	 *            The part of a path which will replace <code>base</code>.
	 * @param ext
	 *            The new extension of the file.
	 * @return The rebased path.
	 * @throws IllegalArgumentException
	 *             Thrown if the rebased path doesn't exist.
	 */
	public static File rebase(File file, String base, String rebase, String ext)
	{
		return rebase(file, base, rebase, ext, false);
	}
	
	/**
	 * Change a path so that the filename it points to is located in a different
	 * directory. Also change the extension of the filename.
	 * 
	 * <b>Important:</b> Always use the UNIX file separator '/'.
	 * 
	 * @param file
	 *            The file whose filename will be moved to a different directory
	 *            and whose extension will be altered.
	 * @param base
	 *            The part of the original path that will be changed.
	 * @param rebase
	 *            The part of a path which will replace <code>base</code>.
	 * @param ext
	 *            The new extension of the file.
	 * @param noThrow
	 *            If true this method will not throw an exception if the rebased
	 *            file doesn't exist.
	 * @return The rebased path.
	 * @throws IllegalArgumentException
	 *             Thrown if the rebased path doesn't exist and noThrow is
	 *             false.
	 */
	public static File rebase(
			File file,
			String base,
			String rebase,
			String ext,
			boolean noThrow)
	{
		String unixPath = fileSeparatorToUnix(file.getAbsolutePath());
		String rebased = unixPath.replace(base, rebase);
		
		int i = rebased.lastIndexOf('.');
		rebased = i < 0 ?
				rebased + "." + ext :
				rebased.substring(0, i + 1) + ext;
		
		File rebasedFile = new File(rebased);
		if (rebasedFile.exists() || noThrow)
			return rebasedFile;
		
		throw new FmtIllegalArgumentException(
				"Rebased file `%s' does not exist",
				rebasedFile.getAbsolutePath());
	}
	
	// =========================================================================
	
	/**
	 * Convert non-UNIX file separators to UNIX file separators '/'.
	 */
	public static String fileSeparatorToUnix(String path)
	{
		if (File.separatorChar != '/')
		{
			assertTrue(
					"Test code doesn't work properly if path conatins a '/'.",
					path.indexOf('/') == -1);
			
			path = path.replace(File.separatorChar, '/');
		}
		
		return path;
	}
	
	/**
	 * Convert non-UNIX line endings into UNIX line endings '\n'.
	 */
	public static String lineEndToUnix(String result)
	{
		result = result.replace("\r\n", "\n");
		result = result.replace("\r", "\n");
		return result;
	}
}
