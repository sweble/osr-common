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

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class TestStringTools
{
	@Test
	public void testEscHtml()
	{
		Assert.assertEquals(
				StringTools.escHtml("<>&'\""),
				"&lt;&gt;&amp;&#39;&quot;");
	}

	@Test
	public void testCamelcaseToUppercase()
	{
		Assert.assertEquals(
				StringTools.camelcaseToUppercase("camelCase"),
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
				StringTools.indent2(text, "    "));
	}

	@Test
	public void testXmlDecode()
	{
		Assert.assertEquals("", StringTools.xmlDecode("", resolver));

		Assert.assertEquals("ASDF", StringTools.xmlDecode("ASDF", resolver));

		Assert.assertEquals("&", StringTools.xmlDecode("&", resolver));

		// ----

		Assert.assertEquals("&MUHAHA;", StringTools.xmlDecode("&MUHAHA;", resolver));

		Assert.assertEquals("&...", StringTools.xmlDecode("&amp;...", resolver));

		Assert.assertEquals("...&", StringTools.xmlDecode("...&amp;", resolver));

		Assert.assertEquals("...&...", StringTools.xmlDecode("...&amp;...", resolver));

		Assert.assertEquals("...&amp;...", StringTools.xmlDecode("...&amp;amp;...", resolver));

		// ----

		Assert.assertEquals("&#--;", StringTools.xmlDecode("&#--;", resolver));

		Assert.assertEquals("&#4294967296;", StringTools.xmlDecode("&#4294967296;", resolver));

		Assert.assertEquals(" ...", StringTools.xmlDecode("&#32;...", resolver));

		Assert.assertEquals("... ", StringTools.xmlDecode("...&#32;", resolver));

		Assert.assertEquals("... ...", StringTools.xmlDecode("...&#32;...", resolver));

		Assert.assertEquals("...&#20;...", StringTools.xmlDecode("...&#2&#48;;...", resolver));

		// ----

		Assert.assertEquals("&#x--;", StringTools.xmlDecode("&#x--;", resolver));

		Assert.assertEquals("&#x100000000;", StringTools.xmlDecode("&#x100000000;", resolver));

		Assert.assertEquals(" ...", StringTools.xmlDecode("&#x20;...", resolver));

		Assert.assertEquals("... ", StringTools.xmlDecode("...&#x20;", resolver));

		Assert.assertEquals("... ...", StringTools.xmlDecode("...&#x20;...", resolver));

		Assert.assertEquals("...&#x20;...", StringTools.xmlDecode("...&#x2&#x30;;...", resolver));
	}

	@Test
	public void testUrlDecode()
	{
		Assert.assertEquals("", StringTools.urlDecode(""));

		Assert.assertEquals("ASDF", StringTools.urlDecode("ASDF"));

		Assert.assertEquals("%", StringTools.urlDecode("%"));

		// ---

		Assert.assertEquals("%-", StringTools.urlDecode("%-"));

		Assert.assertEquals("%--", StringTools.urlDecode("%--"));

		Assert.assertEquals("%gg", StringTools.urlDecode("%gg"));

		Assert.assertEquals("%0", StringTools.urlDecode("%0"));

		Assert.assertEquals("%00", StringTools.urlDecode("%00"));

		Assert.assertEquals("A", StringTools.urlDecode("%41"));

		Assert.assertEquals("Aa", StringTools.urlDecode("%41a"));

		Assert.assertEquals("aA", StringTools.urlDecode("a%41"));

		Assert.assertEquals("aAa", StringTools.urlDecode("a%41a"));
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

	@Test
	public void testTrimWithEmptyString() throws Exception
	{
		assertEquals("", StringTools.trim(""));
	}

	@Test
	public void testTrimUnderscoresWithEmptyString() throws Exception
	{
		assertEquals("", StringTools.trimUnderscores(""));
	}

	@Test
	public void testTrimUnderscoresWithOnlyUnderscores() throws Exception
	{
		assertEquals("", StringTools.trimUnderscores("_"));
	}
}
