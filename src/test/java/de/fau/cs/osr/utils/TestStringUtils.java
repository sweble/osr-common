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

import junit.framework.Assert;

import org.junit.Test;

public class TestStringUtils
{
	@Test
	public void testEscHtml()
	{
		Assert.assertEquals(
				StringUtils.escHtml("<>&'\""),
				"&lt;&gt;&amp;&#39;&quot;");
	}
	
	@Test
	public void testCamelcaseToUppercase()
	{
		Assert.assertEquals(
				StringUtils.camelcaseToUppercase("camelCase"),
				"CAMEL_CASE");
	}
	
	@Test
	public void testIndent()
	{
		String text =
				"text text text\n" +
						"text text text\r\n" +
						"text text text\r" +
						"text text text.";
		
		String indent = "    ";
		
		Assert.assertEquals(
				text.replaceAll("(\r\n|\r|\n)", "$1" + indent),
				StringUtils.indent2(text, "    "));
	}
	
	@Test
	public void testXmlDecode()
	{
		Assert.assertEquals("", StringUtils.xmlDecode("", resolver));
		
		Assert.assertEquals("ASDF", StringUtils.xmlDecode("ASDF", resolver));
		
		Assert.assertEquals("&", StringUtils.xmlDecode("&", resolver));
		
		// ----
		
		Assert.assertEquals("&MUHAHA;", StringUtils.xmlDecode("&MUHAHA;", resolver));
		
		Assert.assertEquals("&...", StringUtils.xmlDecode("&amp;...", resolver));
		
		Assert.assertEquals("...&", StringUtils.xmlDecode("...&amp;", resolver));
		
		Assert.assertEquals("...&...", StringUtils.xmlDecode("...&amp;...", resolver));
		
		Assert.assertEquals("...&amp;...", StringUtils.xmlDecode("...&amp;amp;...", resolver));
		
		// ----
		
		Assert.assertEquals("&#--;", StringUtils.xmlDecode("&#--;", resolver));
		
		Assert.assertEquals("&#4294967296;", StringUtils.xmlDecode("&#4294967296;", resolver));
		
		Assert.assertEquals(" ...", StringUtils.xmlDecode("&#32;...", resolver));
		
		Assert.assertEquals("... ", StringUtils.xmlDecode("...&#32;", resolver));
		
		Assert.assertEquals("... ...", StringUtils.xmlDecode("...&#32;...", resolver));
		
		Assert.assertEquals("...&#20;...", StringUtils.xmlDecode("...&#2&#48;;...", resolver));
		
		// ----
		
		Assert.assertEquals("&#x--;", StringUtils.xmlDecode("&#x--;", resolver));
		
		Assert.assertEquals("&#x100000000;", StringUtils.xmlDecode("&#x100000000;", resolver));
		
		Assert.assertEquals(" ...", StringUtils.xmlDecode("&#x20;...", resolver));
		
		Assert.assertEquals("... ", StringUtils.xmlDecode("...&#x20;", resolver));
		
		Assert.assertEquals("... ...", StringUtils.xmlDecode("...&#x20;...", resolver));
		
		Assert.assertEquals("...&#x20;...", StringUtils.xmlDecode("...&#x2&#x30;;...", resolver));
	}
	
	@Test
	public void testUrlDecode()
	{
		Assert.assertEquals("", StringUtils.urlDecode(""));
		
		Assert.assertEquals("ASDF", StringUtils.urlDecode("ASDF"));
		
		Assert.assertEquals("%", StringUtils.urlDecode("%"));
		
		// ---
		
		Assert.assertEquals("%-", StringUtils.urlDecode("%-"));
		
		Assert.assertEquals("%--", StringUtils.urlDecode("%--"));
		
		Assert.assertEquals("%gg", StringUtils.urlDecode("%gg"));
		
		Assert.assertEquals("%0", StringUtils.urlDecode("%0"));
		
		Assert.assertEquals("%00", StringUtils.urlDecode("%00"));
		
		Assert.assertEquals("A", StringUtils.urlDecode("%41"));
		
		Assert.assertEquals("Aa", StringUtils.urlDecode("%41a"));
		
		Assert.assertEquals("aA", StringUtils.urlDecode("a%41"));
		
		Assert.assertEquals("aAa", StringUtils.urlDecode("a%41a"));
	}
	
	// =========================================================================
	
	private static final XmlEntityResolver resolver = new XmlEntityResolver()
	{
		public String resolveXmlEntity(String name)
		{
			if ("amp".equalsIgnoreCase(name))
			{
				return "&";
			}
			else if ("lt".equalsIgnoreCase(name))
			{
				return "<";
			}
			else if ("gt".equalsIgnoreCase(name))
			{
				return ">";
			}
			else if ("nbsp".equalsIgnoreCase(name))
			{
				return "\u00A0";
			}
			else if ("middot".equalsIgnoreCase(name))
			{
				return "\u00B7";
			}
			else if ("mdash".equalsIgnoreCase(name))
			{
				return "\u2014";
			}
			else if ("ndash".equalsIgnoreCase(name))
			{
				return "\u2013";
			}
			else if ("equiv".equalsIgnoreCase(name))
			{
				return "\u2261";
			}
			else
			{
				return null;
			}
		}
	};
}
