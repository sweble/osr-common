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

package de.fau.cs.osr.ptk.common.ast;

import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import xtc.util.Pair;
import de.fau.cs.osr.ptk.common.test.nodes.CtnIdNode;
import de.fau.cs.osr.ptk.common.test.nodes.CtnNodeList;
import de.fau.cs.osr.ptk.common.test.nodes.CtnNode;

public class NodeListTest
{
	private static final boolean VERBOSE = false;
	
	// =========================================================================
	
	@Test
	public void testNodeList()
	{
		CtnNodeList l = new CtnNodeList();
		checkNodeList(0, l);
		assertTrue(l.isList());
		assertTrue(l.isEmpty());
	}
	
	@Test
	public void testNodeList_AstNode()
	{
		{
			CtnNodeList l = new CtnNodeList((CtnNode) null);
			checkNodeList(0, l);
		}
		{
			CtnNodeList l = new CtnNodeList(astId(0));
			checkNodeList(1, l);
			assertFalse(l.isEmpty());
		}
	}
	
	@Test
	public void testNodeList_AstNode_PairOfAstNode()
	{
		{
			Pair<CtnNode> list = makeSimpleList3(1);
			
			CtnNodeList l = new CtnNodeList(astId(0), (Pair<CtnNode>) list);
			
			checkNodeList(4, l);
		}
		{
			Pair<CtnNode> list = makeSimpleList3(0);
			
			CtnNodeList l = new CtnNodeList(null, (Pair<CtnNode>) list);
			
			checkNodeList(3, l);
		}
		{
			testForException(new Runnable()
			{
				@Override
				public void run()
				{
					new CtnNodeList(astId(0), (Pair<CtnNode>) null);
				}
			}, NullPointerException.class);
		}
	}
	
	@Test
	public void testNodeList_AstNode_AstNode()
	{
		{
			CtnNodeList l = new CtnNodeList(null, (CtnNode) null);
			checkNodeList(0, l);
		}
		{
			CtnNodeList l = new CtnNodeList(
					new CtnNodeList(makeSimpleList3(0)),
					new CtnNodeList(makeNestedList5(3)));
			
			checkNodeList(8, l);
		}
	}
	
	@Test
	public void testNodeList_PairOfAstNode()
	{
		{
			Pair<CtnNode> list = makeSimpleList3(0);
			
			CtnNodeList l = new CtnNodeList((Pair<CtnNode>) list);
			
			checkNodeList(3, l);
		}
		{
			testForException(new Runnable()
			{
				@Override
				public void run()
				{
					new CtnNodeList((Pair<CtnNode>) null);
				}
			}, NullPointerException.class);
		}
	}
	
	// =========================================================================
	
	@Test
	public void testSet()
	{
		{
			CtnNodeList l = new CtnNodeList(makeSimpleList3(3));
			l.set(2, astId(2));
			l.set(0, astId(0));
			l.set(1, astId(1));
			checkNodeList(3, l);
		}
		{
			testForException(new Runnable()
			{
				@Override
				public void run()
				{
					new CtnNodeList(makeSimpleList3(0)).set(0, new CtnNodeList());
				}
			}, IllegalArgumentException.class);
		}
		{
			testForException(new Runnable()
			{
				@Override
				public void run()
				{
					new CtnNodeList(makeSimpleList3(0)).set(0, null);
				}
			}, NullPointerException.class);
		}
	}
	
	@Test
	public void testLastIndexOf()
	{
		{
			CtnNodeList l = new CtnNodeList(
					new CtnNodeList(makeSimpleList3(3)),
					makeNestedList5(10));
			
			assertEquals(l.lastIndexOf(null), -1);
			assertEquals(l.lastIndexOf(astId(7)), -1);
			assertEquals(l.lastIndexOf(astId(5)), 2);
			assertEquals(l.lastIndexOf(astId(3)), 0);
			assertEquals(l.lastIndexOf(astId(10)), 3);
			assertEquals(l.lastIndexOf(astId(14)), 7);
		}
	}
	
	@Test
	public void testAppend()
	{
		{
			CtnNodeList l = new CtnNodeList(
					new CtnNodeList(makeSimpleList3(0)),
					makeNestedList5(3));
			
			l.add(null);
			l.add(astId(8));
			l.add(null);
			checkNodeList(9, l);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAppendAll()
	{
		{
			CtnNodeList l = new CtnNodeList(
					new CtnNodeList(makeSimpleList3(0)),
					makeNestedList5(3));
			
			l.addAll(Pair.EMPTY);
			checkNodeList(8, l);
		}
		{
			CtnNodeList l = new CtnNodeList(
					new CtnNodeList(makeSimpleList3(0)),
					makeNestedList5(3));
			
			Pair<CtnNode> l2 = makeSimpleList3(7);
			l2.set(0, null);
			
			l.addAll(l2);
			checkNodeList(10, l);
		}
		{
			CtnNodeList l = new CtnNodeList(
					new CtnNodeList(makeSimpleList3(0)),
					makeNestedList5(3));
			
			Pair<CtnNode> l2 = makeSimpleList3(7);
			l2.set(0, null);
			l2.set(1, null);
			l2.set(2, null);
			
			l.addAll(l2);
			checkNodeList(8, l);
		}
		{
			CtnNodeList l = new CtnNodeList(
					new CtnNodeList(makeSimpleList3(0)),
					makeNestedList5(3));
			
			Pair<CtnNode> l2 = makeSimpleList3(0);
			l2.set(0, null);
			l2.set(1, null);
			l2.set(2, new CtnNodeList(makeNestedList5(8)));
			
			l.addAll(l2);
			checkNodeList(13, l);
		}
		{
			CtnNodeList l = new CtnNodeList(
					new CtnNodeList(makeSimpleList3(0)),
					makeNestedList5(3));
			
			Pair<CtnNode> l2 = makeSimpleList3(8);
			l2.set(2, null);
			l.addAll(l2);
			l.set(9, astId(9));
			
			checkNodeList(10, l);
		}
		{
			CtnNodeList l = new CtnNodeList(
					new CtnNodeList(makeSimpleList3(0)),
					makeNestedList5(3));
			
			Pair<CtnNode> l2 = makeSimpleList3(7);
			l2.set(2, null);
			l2.set(0, null);
			
			l.addAll(l2);
			checkNodeList(9, l);
		}
	}
	
	@Test
	public void testPrepend()
	{
		{
			CtnNodeList l = new CtnNodeList(makeSimpleList3(0));
			
			//l.prepend(null);
			prepend(l, null);
			
			checkNodeList(3, l);
		}
		{
			CtnNodeList l = new CtnNodeList(makeSimpleList3(1));
			
			//l.prepend(astId(0));
			prepend(l, astId(0));
			
			checkNodeList(4, l);
		}
		{
			CtnNodeList l = new CtnNodeList(makeNestedList5(5));
			
			//l.prepend(new List(makeNestedList5(0)));
			prepend(l, new CtnNodeList(makeNestedList5(0)));
			
			checkNodeList(10, l);
		}
	}
	
	private void prepend(CtnNode n, CtnNode l)
	{
		n.add(0, l);
	}
	
	@Test
	@Ignore
	public void testPrependAll()
	{
		{
			testForException(new Runnable()
			{
				@Override
				public void run()
				{
					//new List(makeSimpleList3(0)).prependAll((Pair<TestAstNode>) null);
					prependAll(new CtnNodeList(makeSimpleList3(0)), (Pair<CtnNode>) null);
				}
			}, NullPointerException.class);
		}
		{
			CtnNodeList l = new CtnNodeList(makeNestedList5(5));
			
			//l.prependAll(makeNestedList5(0));
			prependAll(l, makeNestedList5(0));
			
			checkNodeList(10, l);
		}
	}
	
	private void prependAll(CtnNode n, Pair<CtnNode> l)
	{
		//n.addAll(0, l);
	}
	
	@Test
	public void testClear()
	{
		{
			CtnNodeList l = new CtnNodeList(
					new CtnNodeList(makeSimpleList3(0)),
					makeNestedList5(3));
			
			l.clear();
			checkNodeList(0, l);
		}
	}
	
	// =========================================================================
	
	private void checkNodeList(int size, CtnNodeList l)
	{
		assertEquals(size, l.size());
		for (int i = 0; i < l.size(); ++i)
		{
			int id = ((CtnIdNode) l.get(i)).id;
			if (VERBOSE)
				System.out.println(id);
			assertEquals(id, i);
		}
	}
	
	private Pair<CtnNode> makeSimpleList3(int i)
	{
		Pair<CtnNode> list = new Pair<CtnNode>(astId(i++));
		list.add(astId(i++));
		list.add(astId(i++));
		return list;
	}
	
	private Pair<CtnNode> makeNestedList5(int i)
	{
		Pair<CtnNode> list = new Pair<CtnNode>(astId(i++));
		{
			Pair<CtnNode> nestedList = new Pair<CtnNode>(astId(i++));
			nestedList.add(astId(i++));
			nestedList.add(astId(i++));
			list.add(new CtnNodeList(nestedList));
		}
		list.add(astId(i++));
		return list;
	}
	
	private <T extends Throwable> void testForException(
			Runnable test,
			Class<T> clazz)
	{
		try
		{
			test.run();
			assertTrue(false);
		}
		catch (Throwable e)
		{
			assertTrue(e.getClass().equals(clazz));
		}
	}
}
