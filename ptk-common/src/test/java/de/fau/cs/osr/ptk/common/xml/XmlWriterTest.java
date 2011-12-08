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
package de.fau.cs.osr.ptk.common.xml;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import de.fau.cs.osr.ptk.common.jxpath.Document;
import de.fau.cs.osr.ptk.common.jxpath.Section;

public class XmlWriterTest
{
	
	private static final AstNode AST =
			new Document(
					new NodeList(
							new Section(
									1,
									new NodeList(
											new Text("Section 1")), // title
									new NodeList(
											new Text("Section 1 Body")), // body
									"EOL1"),
							new Section(
									2,
									new NodeList(
											new Text("Section 2")), // title
									new NodeList(
											new Text("Section 2 Body")), // body
									"EOL2")));
	
	@Test
	public void test() throws IOException
	{
		ByteArrayOutputStream objBaos = new ByteArrayOutputStream();
		
		XmlWriter xmlw = new XmlWriter(objBaos);
		xmlw.go(AST);
		
		InputStream is = getClass().getResourceAsStream("/simple-serialized-ast.xml");
		String expected = IOUtils.toString(is, "UTF-8");
		
		String actual = new String(objBaos.toString("UTF-8"));
		
		Assert.assertEquals(expected, actual);
	}
	
}
