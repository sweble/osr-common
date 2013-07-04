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

import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.junit.Assert;
import org.junit.Test;

import de.fau.cs.osr.ptk.common.jxpath.AstPropertyIterator.Property;
import de.fau.cs.osr.ptk.common.test.nodes.CtnNode;
import de.fau.cs.osr.ptk.common.test.nodes.CtnSection;

public class XPathTest
{
	private static final boolean QUIET = true;
	
	// =========================================================================
	
	private static final CtnSection AST1 =
			astSection()
					.withLevel(0)
					.withTitle(astText("1st"))
					.withBody(
							astText("2nd"),
							astId(3),
							astSection()
									.withLevel(1)
									.withTitle()
									.withBody()
									.build())
					.build();
	
	private static final CtnNode AST2 =
			astDoc(
					astSection()
							.withLevel(1)
							.withTitle()
							.withBody()
							.build(),
					astSection()
							.withLevel(2)
							.withTitle()
							.withBody()
							.build());
	
	private static final CtnNode AST3 =
			astDoc(
					astText("1"),
					astId(2));
	
	// =========================================================================
	
	private JXPathContext context;
	
	// =========================================================================
	
	public XPathTest()
	{
		JXPathContextReferenceImpl.addNodePointerFactory(
				new AstNodePointerFactory());
	}
	
	// =========================================================================
	
	@Test
	public void test01()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/body/*[2]",
				AST1.getBody().get(1));
	}
	
	@Test
	public void test02()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/@*",
				AST1.getProperty("level"));
	}
	
	@Test
	public void test03()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/*/*[2]",
				AST1.getBody().get(1));
	}
	
	@Test
	public void test04()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/body[last()-1]");
	}
	
	@Test
	public void test05()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/body/*[last()]",
				AST1.getBody().get(2));
	}
	
	@Test
	public void test06()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/body/text/@content",
				AST1.getBody().get(0).getProperty("content"));
	}
	
	@Test
	public void test07()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/body/body[last()-1]");
	}
	
	@Test
	public void test08()
	{
		context = JXPathContext.newContext(AST1);
		
		runTest("//@level",
				AST1.getProperty("level"),
				AST1.getBody().get(2).getProperty("level"));
	}
	
	@Test
	public void test09()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/descendant-or-self::node()",
				AST1,
				AST1.getTitle(),
				AST1.getTitle().get(0),
				AST1.getBody(),
				AST1.getBody().get(0),
				AST1.getBody().get(1),
				AST1.getBody().get(2),
				AST1.getBody().get(2).get(0),
				AST1.getBody().get(2).get(1));
	}
	
	@Test
	public void test10()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/descendant-or-self::node()[@level]",
				AST1,
				AST1.getBody().get(2));
	}
	
	@Test
	public void test11()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/descendant-or-self::node()/*",
				AST1.getTitle(),
				AST1.getTitle().get(0),
				AST1.getBody(),
				AST1.getBody().get(0),
				AST1.getBody().get(1),
				AST1.getBody().get(2),
				AST1.getBody().get(2).get(0),
				AST1.getBody().get(2).get(1));
	}
	
	@Test
	public void test12()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/descendant-or-self::node()/*[@level]",
				AST1.getBody().get(2));
	}
	
	@Test
	public void test13()
	{
		context = JXPathContext.newContext(AST1);
		runTest("//*[@level]",
				AST1.getBody().get(2));
	}
	
	@Test
	public void test14()
	{
		context = JXPathContext.newContext(AST1);
		runTest("/body[last()]",
				AST1.getBody());
	}
	
	@Test
	public void testPredicate01()
	{
		context = JXPathContext.newContext(AST2);
		runTest("/*[@level]",
				AST2.get(0),
				AST2.get(1));
	}
	
	@Test
	public void testPredicate02()
	{
		context = JXPathContext.newContext(AST2);
		runTest("/*/@level",
				AST2.get(0).getProperty("level"),
				AST2.get(1).getProperty("level"));
	}
	
	@Test
	public void testPredicate03()
	{
		context = JXPathContext.newContext(AST3);
		runTest("//*[@content]",
				AST3.get(0));
	}
	
	// =========================================================================
	
	public void runTest(String xpath, Object... expected)
	{
		if (!QUIET)
		{
			System.out.println();
			System.out.println("Query: " + xpath);
		}
		
		Map<Object, List<Object>> e = new HashMap<Object, List<Object>>();
		for (Object o : expected)
		{
			List<Object> l = e.get(o);
			if (l == null)
				l = new ArrayList<Object>();
			
			l.add(o);
			e.put(o, l);
		}
		
		Map<Object, List<Object>> a = new HashMap<Object, List<Object>>();
		for (Iterator<?> i = context.iterate(xpath); i.hasNext();)
		{
			Object o = i.next();
			
			if (!QUIET)
				System.out.format("  - %s\n", o.toString());
			
			if (o instanceof Property)
				o = ((Property) o).getValue();
			
			List<Object> l = a.get(o);
			if (l == null)
				l = new ArrayList<Object>();
			
			l.add(o);
			a.put(o, l);
		}
		
		Assert.assertEquals(e.size(), a.size());
		contains(e, a);
		contains(a, e);
	}
	
	private void contains(
			Map<Object, List<Object>> e,
			Map<Object, List<Object>> a)
	{
		for (Entry<Object, List<Object>> x : e.entrySet())
		{
			Assert.assertNotNull(a.get(x.getKey()));
			Assert.assertEquals(a.get(x.getKey()).size(), x.getValue().size());
			for (Object o : x.getValue())
				Assert.assertTrue(a.get(o).contains(o));
		}
	}
}
