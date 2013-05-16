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

import static de.fau.cs.osr.ptk.common.test.TestAstBuilder.*;
import static org.junit.Assert.*;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import de.fau.cs.osr.ptk.common.DeepAstComparer;
import de.fau.cs.osr.ptk.common.test.TestAstBuilder.List;
import de.fau.cs.osr.ptk.common.test.TestAstBuilder.Section;
import de.fau.cs.osr.ptk.common.test.TestAstBuilder.TestAstNode;
import de.fau.cs.osr.ptk.common.test.TestAstBuilder.Text;
import de.fau.cs.osr.utils.NameAbbrevService;

public class XmlWriterTest
{
	private XmlWriter<TestAstNode> writer;
	
	private XmlReader<TestAstNode> reader;
	
	@Before
	public void before()
	{
		this.writer = new XmlWriter<TestAstNode>(
				TestAstNode.class,
				List.class,
				Text.class);
		
		this.reader = new XmlReader<TestAstNode>(
				TestAstNode.class,
				List.class,
				Text.class);
	}
	
	@Test
	public void testSerializationAndDeserialization() throws Exception
	{
		Section in = astSection()
				.withLevel(1)
				.withBody(
						astText(),
						astUrl().build())
				.build();
		
		in.setAttribute("int attr", 5);
		
		in.setAttribute("string attr", "string value");
		
		in.setAttribute("array attr", new Double[] { 3.1415, 2.7182 });
		
		in.setAttribute("bad ass array attr", new double[][] { { 3.1415, 2.7182 }, { 3.1415 * 2, 2.7182 * 2 } });
		
		in.setAttribute("arbitrary object", new ArbitraryNode());
		
		in.setAttribute("null valued attr", null);
		
		// -------
		
		NameAbbrevService as = new NameAbbrevService(
				"de.fau.cs.osr.ptk.common.test",
				"de.fau.cs.osr.ptk.common.xml");
		
		String xml = serialize(in, as);
		
		TestAstNode out = deserialize(xml, as);
		
		//System.out.println(serialize(out, as));
		
		// -------
		
		assertTrue(DeepAstComparer.compare(in, out, false, true));
		
		// We have to compare the attribuets manually. The arrays contained in 
		// the attribute won't be compared correctly by the .equals call.
		deepCompareMaps(in.getAttributes(), out.getAttributes());
	}
	
	// =========================================================================
	
	private String serialize(TestAstNode ast, NameAbbrevService as) throws Exception
	{
		StringWriter sw = new StringWriter();
		writer.serialize(ast, sw, as);
		return sw.toString();
	}
	
	private TestAstNode deserialize(String xml, NameAbbrevService as) throws Exception
	{
		return reader.deserialize(new StringReader(xml), as);
	}
	
	private boolean deepCompareMaps(
			Map<String, Object> a,
			Map<String, Object> b)
	{
		Iterator<Entry<String, Object>> i = a.entrySet().iterator();
		while (i.hasNext())
		{
			Entry<String, Object> e = i.next();
			String key = e.getKey();
			Object value = e.getValue();
			if (value == null)
			{
				if (b.get(key) != null || !b.containsKey(key))
					return false;
			}
			else if (value.getClass().isArray())
			{
				Arrays.deepEquals((Object[]) value, (Object[]) b.get(key));
			}
			else
			{
				if (!value.equals(b.get(key)))
					return false;
			}
		}
		
		return true;
	}
	
	// =========================================================================
	
	protected static final class ArbitraryNode
			implements
				Serializable
	{
		private static final long serialVersionUID = 1L;
		
		public Object nullValue = null;
		
		public int intValue = 42;
		
		public String strValue = "some string";
		
		public double[] doubleValues = { 3.1415, 2.7182 };
		
		// =====================================================================
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ArbitraryNode other = (ArbitraryNode) obj;
			if (!Arrays.equals(doubleValues, other.doubleValues))
				return false;
			if (intValue != other.intValue)
				return false;
			if (nullValue == null)
			{
				if (other.nullValue != null)
					return false;
			}
			else if (!nullValue.equals(other.nullValue))
				return false;
			if (strValue == null)
			{
				if (other.strValue != null)
					return false;
			}
			else if (!strValue.equals(other.strValue))
				return false;
			return true;
		}
	}
}
