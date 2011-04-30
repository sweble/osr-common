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
import java.io.IOException;

import de.fau.cs.osr.utils.FileUtils;
import de.fau.cs.osr.utils.FmtIllegalArgumentException;

public class Helpers
{
	public static Object instantiateClass(String parserClassName, Class<?> parserClass, String classTpyeName)
	{
		try
		{
			return parserClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new FmtIllegalArgumentException(
			        e,
			        "Cannot instantiate %s class `%s'.",
			        classTpyeName,
			        parserClassName);
		}
		catch (IllegalAccessException e)
		{
			throw new FmtIllegalArgumentException(
			        e,
			        "Cannot instantiate %s class `%s'.",
			        classTpyeName,
			        parserClassName);
		}
	}
	
	public static Class<?> loadClass(String parserClassName, String classTypeName)
	{
		Class<?> parserClass;
		try
		{
			parserClass = Class.forName(parserClassName);
		}
		catch (ClassNotFoundException e)
		{
			throw new FmtIllegalArgumentException(
			        "Cannot load %s class `%s'.",
			        classTypeName,
			        parserClassName);
		}
		return parserClass;
	}
	
	public static void checkDirectory(File base, String dir, String directoryTypeName)
	{
		File file = new File(base, dir);
		checkDirectory(dir, directoryTypeName, file);
	}
	
	public static void checkDirectory(String dir, String directoryTypeName, File file)
	{
		if (!file.exists())
			throw new FmtIllegalArgumentException(
			            "%s directory does not exist: `%s'",
			            directoryTypeName,
			            dir);
		
		if (!file.isDirectory())
			throw new FmtIllegalArgumentException(
			        "%s directory `%s' is not a directory.",
			        directoryTypeName,
			        dir);
		
	}
	
	public static File fillTempFile(String result, String filePrefix)
	{
		try
		{
			return FileUtils.fillTempFile(result, filePrefix);
		}
		catch (IOException e)
		{
			throw new RuntimeException(
			            "Failed to set-up temporary file for diff operation (" + filePrefix + ")", e);
		}
	}
}
