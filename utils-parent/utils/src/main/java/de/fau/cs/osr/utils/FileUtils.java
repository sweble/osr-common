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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.common.base.Preconditions;

public final class FileUtils
{
	public static File fillTempFile(String result, String filePrefix) throws IOException
	{
		File resultFile = File.createTempFile(filePrefix + "---", ".txt");
		resultFile.deleteOnExit();

		BufferedWriter out = new BufferedWriter(new FileWriter(resultFile));
		out.write(result);
		out.close();

		return resultFile;
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

	/**
	 * Convert non-UNIX file separators to UNIX file separators '/'.
	 */
	public static String fileSeparatorToUnix(String path)
	{
		if (File.separatorChar != '/')
		{
			Preconditions.checkArgument(
					path.indexOf('/') == -1,
					"Test code doesn't work properly if path conatins a '/'.");

			path = path.replace(File.separatorChar, '/');
		}

		return path;
	}
}
