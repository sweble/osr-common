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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

import de.fau.cs.osr.utils.XmlGrammar;

public class TestXmlGrammar
{
	@Test
	public void testNonBmpChars()
	{
		Pattern p = Pattern.compile(XmlGrammar.RE_XML_NAME_START_CHAR);
		Matcher m = p.matcher("\uD800\uDC00");
		Assert.assertTrue(m.matches());
	}
	
	@Test
	public void testXmlName()
	{
		Assert.assertTrue(XmlGrammar.xmlName().matcher("a-z").matches());
		Assert.assertFalse(XmlGrammar.xmlName().matcher("-z").matches());
		Assert.assertTrue(XmlGrammar.xmlName().matcher("\uD800\uDC00-\uDB7F\uDFFE").matches());
		Assert.assertFalse(XmlGrammar.xmlName().matcher(" caption").matches());
		Assert.assertFalse(XmlGrammar.xmlName().matcher("caption ").matches());
	}
	
	@Test
	public void testXmlReference()
	{
		Matcher m = XmlGrammar.xmlReference().matcher("0: &amp; 1: &#38; 2: &#x26;");
		
		Assert.assertTrue(m.find(0));
		Assert.assertEquals("amp", m.group(1));
		
		Assert.assertTrue(m.find(m.end()));
		Assert.assertEquals("38", m.group(2));
		
		Assert.assertTrue(m.find(m.end()));
		Assert.assertEquals("26", m.group(3));
	}
}
