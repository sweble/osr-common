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

import org.apache.commons.io.FileUtils;

import de.fau.cs.osr.utils.FmtFileNotFoundException;
import de.fau.cs.osr.utils.FmtIllegalArgumentException;

public class FileContent
{
	private File file;
	
	private String content;
	
	// =========================================================================
	
	public FileContent(File file) throws IOException
	{
		loadFile(file, "UTF-8");
	}
	
	public FileContent(File file, String encoding) throws IOException
	{
		loadFile(file, encoding);
	}
	
	// =========================================================================
	
	protected void loadFile(File file, String encoding) throws IOException
	{
		if (encoding == null)
			throw new FmtIllegalArgumentException(
					"Argument `encoding' must not be null");
		
		if (file == null)
			throw new FmtIllegalArgumentException(
					"Argument `file' must not be null");
		
		if (!file.exists())
			throw new FmtFileNotFoundException(
					"File not found: `%s'",
					file.getAbsolutePath());
		
		this.file = file;
		this.content = FileUtils.readFileToString(file, encoding);
	}
	
	// =========================================================================
	
	public File getFile()
	{
		return file;
	}
	
	public void setFile(File file)
	{
		this.file = file;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public void setContent(String content)
	{
		this.content = content;
	}
}
