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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fau.cs.osr.utils.FmtIllegalArgumentException;

/**
 * 
 */
public class ParserTestResources
{
	File baseDirectory;
	
	// =========================================================================
	
	/**
	 * Initializes the resource management with the base directory below which
	 * all test resources can be found.
	 * 
	 * @param baseDirectory
	 *            The base directory below which all test resources can be
	 *            found.
	 */
	public ParserTestResources(File baseDirectory)
	{
		if (!baseDirectory.exists())
			throw new FmtIllegalArgumentException(
					"Given base directory `%s' does not exist",
					baseDirectory);
		
		if (!baseDirectory.isDirectory())
			throw new FmtIllegalArgumentException(
					"Given base directory `%s' is not a directory",
					baseDirectory);
		
		this.baseDirectory = baseDirectory;
		
	}
	
	// =========================================================================
	
	/**
	 * Gather test resources.
	 * 
	 * @param directory
	 *            Path below the base directory (see constructor) where the
	 *            resources can be found.
	 * @param glob
	 *            All files whose filename matches this regular expression will
	 *            be returned.
	 * @param recursive
	 *            Whether to recurse into sub directories.
	 * @return A list of the gathered files.
	 */
	public List<File> gather(
			String directory,
			String glob,
			final boolean recursive)
	{
		List<File> files = new LinkedList<File>();
		
		final Pattern p = Pattern.compile(glob);
		
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
		
		File dir = new File(baseDirectory, directory);
		
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
	
	public String stripBaseDirectoryAndFixPath(String text)
	{
		String p = Pattern.quote(baseDirectory.getAbsolutePath());
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
		if (!rebasedFile.exists() && !noThrow)
		{
			throw new FmtIllegalArgumentException(
					"Rebased file `%s' does not exist",
					rebasedFile.getAbsolutePath());
		}
		
		return rebasedFile;
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
