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

package de.fau.cs.osr.ptk.common.jxpath;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This is not a real test case. It's just some code to get acquainted with
 * XPath idiosyncrasies.
 */
public class DomXPathTest
{
	private static final boolean QUIET = true;

	private static TransformerFactory transformerFactory = null;

	private static Transformer transformer = null;

	@Test
	@Ignore
	public void test() throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		InputStream is = getClass().getResourceAsStream("/simple.xml");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);

		JXPathContext context = JXPathContext.newContext(doc);

		printResult(context, "/*//*");
	}

	private void printResult(JXPathContext context, String xpath) throws TransformerException
	{
		if (!QUIET)
		{
			System.out.println();
			System.out.println("Query: " + xpath);
		}

		for (Iterator<?> i = context.iteratePointers(xpath); i.hasNext();)
		{
			NodePointer o = (NodePointer) i.next();

			if (!QUIET)
			{
				print((Node) o.getImmediateNode());
			}
		}
	}

	private void print(Node node) throws TransformerException
	{
		if (transformerFactory == null)
		{
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
		}

		StringWriter x = new StringWriter();

		DOMSource source = new DOMSource(node);
		StreamResult result = new StreamResult(x);
		transformer.transform(source, result);

		System.out.println(x.toString().replaceAll("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
	}
}
