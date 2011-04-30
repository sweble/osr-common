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

package de.fau.cs.osr.ptk.printergen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import xtc.parser.ParseError;
import xtc.parser.Result;
import xtc.parser.SemanticValue;
import de.fau.cs.osr.ptk.printergen.parser.AstTemplateParser;
import de.fau.cs.osr.ptk.printergen.parser.PrinterTemplate;
import de.fau.cs.osr.utils.FmtFileNotFoundException;
import de.fau.cs.osr.utils.FmtInternalLogicError;

public class TestGenerator
{
	@Test
	public void testExample() throws IOException
	{
		doTest("Example");
	}
	
	@Test
	public void testError2() throws IOException
	{
		doTest("Error2");
	}
	
	@Test
	public void testIfIndent() throws IOException
	{
		doTest("IfIndent");
	}
	
	@Test
	public void testCall() throws IOException
	{
		doTest("Call");
	}
	
	@Test
	public void testCtorParams() throws IOException
	{
		doTest("CtorParams");
	}
	
	private void doTest(String title) throws FmtFileNotFoundException, IOException
	{
		String source = loadFileFromResource("/input/" + title + ".apt");
		
		String actual = generate(source);
		
		InputStream in = getClass().getResourceAsStream("/expected/" + title + ".java.ref");
		if (in == null)
			missingExpected(title, actual);
		
		String expected = IOUtils.toString(in);
		
		actual = actual.replaceAll(" \\* Last generated:.*", "");
		expected = expected.replaceAll(" \\* Last generated:.*", "");
		
		Assert.assertEquals(expected, actual);
	}
	
	private String loadFileFromResource(String resource) throws FmtFileNotFoundException, IOException
	{
		InputStream in = getClass().getResourceAsStream(resource);
		if (in == null)
			throw new FmtFileNotFoundException(
			        "Resource not found: `%s'.",
			        resource);
		
		String source = IOUtils.toString(in);
		return source;
	}
	
	private void missingExpected(String title, String actual) throws IOException
	{
		File create;
		create = new File("").getAbsoluteFile();
		create = File.createTempFile(title + ".java.ref-", "", create);
		
		FileUtils.writeStringToFile(create, actual);
		Assert.fail(
		            "Reference file did not exist! " +
		                    "Wrote initial reference file to: " +
		                    create.getAbsolutePath());
	}
	
	protected String generate(String source) throws IOException
	{
		AstTemplateParser parser =
		        new AstTemplateParser(
		                new StringReader(source),
		                "-");
		
		Result result = parser.pAstTemplateGrammar(0);
		
		if (result.hasValue())
		{
			SemanticValue v = (SemanticValue) result;
			
			if (v.value instanceof PrinterTemplate)
			{
				return transform((PrinterTemplate) v.value);
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
	
	private String transform(PrinterTemplate tmpl) throws IOException
	{
		PrinterTemplateVisitor visitor =
		        new PrinterTemplateVisitor(new Log()
		        {
			        @Override
			        public void debug(CharSequence content)
			        {
				        System.err.println(content);
			        }
			        
			        @Override
			        public void debug(Throwable error)
			        {
				        error.printStackTrace();
			        }
			        
			        @Override
			        public void debug(CharSequence content, Throwable error)
			        {
				        System.err.println(content);
				        error.printStackTrace();
			        }
			        
			        @Override
			        public void error(CharSequence content)
			        {
				        System.err.println(content);
			        }
			        
			        @Override
			        public void error(Throwable error)
			        {
				        error.printStackTrace();
			        }
			        
			        @Override
			        public void error(CharSequence content, Throwable error)
			        {
				        System.err.println(content);
				        error.printStackTrace();
			        }
			        
			        @Override
			        public void info(CharSequence content)
			        {
				        System.err.println(content);
			        }
			        
			        @Override
			        public void info(Throwable error)
			        {
				        error.printStackTrace();
			        }
			        
			        @Override
			        public void info(CharSequence content, Throwable error)
			        {
				        System.err.println(content);
				        error.printStackTrace();
			        }
			        
			        @Override
			        public boolean isDebugEnabled()
			        {
				        return true;
			        }
			        
			        @Override
			        public boolean isErrorEnabled()
			        {
				        return true;
			        }
			        
			        @Override
			        public boolean isInfoEnabled()
			        {
				        return true;
			        }
			        
			        @Override
			        public boolean isWarnEnabled()
			        {
				        return true;
			        }
			        
			        @Override
			        public void warn(CharSequence content)
			        {
				        System.err.println(content);
			        }
			        
			        @Override
			        public void warn(Throwable error)
			        {
				        error.printStackTrace();
			        }
			        
			        @Override
			        public void warn(CharSequence content, Throwable error)
			        {
				        System.err.println(content);
				        error.printStackTrace();
			        }
		        });
		
		return (String) visitor.go(tmpl);
	}
}
