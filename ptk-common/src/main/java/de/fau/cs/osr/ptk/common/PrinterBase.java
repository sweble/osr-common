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

package de.fau.cs.osr.ptk.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import de.fau.cs.osr.ptk.common.ast.AstNodeInterface;
import de.fau.cs.osr.utils.StringUtils;

public class PrinterBase
        extends
            AstVisitor
{
	private PrintWriter out;
	
	private Stack<String> indent = new Stack<String>();
	
	/** At the start of the document we somewhat had a "newline" */
	private boolean needIndent = true;
	
	// =========================================================================
	
	protected PrinterBase(Writer writer)
	{
		this.out = new PrintWriter(writer);
		this.indent.push("");
	}
	
	@Override
	protected Object after(AstNodeInterface node, Object result)
	{
		this.out.close();
		return super.after(node, result);
	}
	
	// =========================================================================
	
	protected void incIndent(String inc)
	{
		indent.push(indent.peek() + inc);
	}
	
	protected void decIndent()
	{
		indent.pop();
	}
	
	protected void print(String text)
	{
		if (text == null)
			return;
		
		if (needIndent)
		{
			out.append(indent.peek());
			needIndent = false;
		}
		
		out.append(text);
	}
	
	protected void print(int number)
	{
		print(String.valueOf(number));
	}
	
	protected void printNewline(boolean force)
	{
		// we already had a newline -> collapse
		if (!needIndent || force)
		{
			out.append("\n");
			needIndent = true;
		}
	}
	
	protected String indentText(String text)
	{
		return StringUtils.indent2(text, indent.peek());
	}
	
	// =========================================================================
	
	protected static String load(File file) throws IOException
	{
		return FileUtils.readFileToString(file);
	}
	
	protected String loadFromResource(String resourceName) throws IOException
	{
		InputStream in = getClass().getResourceAsStream(resourceName);
		if (in == null)
			throw new FileNotFoundException("Resource not found: `" + resourceName + "'.");
		
		return IOUtils.toString(in);
	}
	
	protected static String camelCaseToUppercase(String name)
	{
		return StringUtils.camelcaseToUppercase(name);
	}
	
	protected static String startWithUppercase(String name)
	{
		return StringUtils.startWithUppercase(name);
	}
	
	protected static String escHtml(String text)
	{
		return StringUtils.escHtml(text);
	}
	
	protected static String escJavaHtml(String text)
	{
		return StringUtils.escHtml(StringUtils.escJava(text));
	}
	
	protected static String strrep(char ch, int rep)
	{
		return StringUtils.strrep(ch, rep);
	}
	
	protected static String strrep(String str, int rep)
	{
		return StringUtils.strrep(str, rep);
	}
}
