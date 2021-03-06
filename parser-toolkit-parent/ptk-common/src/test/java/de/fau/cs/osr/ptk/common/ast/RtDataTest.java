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

import static de.fau.cs.osr.ptk.common.ast.RtData.SEP;
import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.ctnSection;
import static de.fau.cs.osr.ptk.common.test.nodes.CtnBuilder.ctnText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.fau.cs.osr.ptk.common.test.nodes.CtnSection;
import de.fau.cs.osr.ptk.common.test.nodes.CtnText;

public class RtDataTest
{

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testRtdObjectHasRightSize() throws Exception
	{
		assertEquals(2, new RtData(2).size());

		CtnText text = ctnText();
		assertEquals(text.size() + 1, new RtData(text).size());

		CtnSection section = ctnSection().build();
		assertEquals(section.size() + 1, new RtData(section).size());
	}

	@Test
	public void testNewRtdObjectIsMadeUpOfEmptyFields() throws Exception
	{
		RtData rtd = new RtData(2);
		for (int i = 0; i < rtd.size(); ++i)
		{
			assertNotNull(rtd.getField(i));
			assertEquals(0, rtd.getField(i).length);
		}
	}

	@Test
	public void testComplexCtorsHaveRightSize() throws Exception
	{
		CtnSection section = ctnSection().build();
		RtData rtd = new RtData(section, "1");
		assertEquals(3, rtd.size());
	}

	@Test
	public void testNullInCtorDoesNotMeanNullInRtd() throws Exception
	{
		CtnSection section = ctnSection().build();
		RtData rtd = new RtData(section, "1", SEP, null, SEP, "3");
		for (int i = 0; i < rtd.size(); ++i)
			assertNotNull(rtd.getField(i));
	}

	@Test
	public void testRtdIsNicelyFormatted() throws Exception
	{
		assertEquals(
				"RTD[ \"1\" <o> \"2.1, 2.2\" <o> \"\" ]",
				new RtData(3, "1", SEP, "2.1", ", ", "2.2", SEP, null).toString());
	}

	@Test
	public void testTooManySepRaiseException() throws Exception
	{
		thrown.expect(IndexOutOfBoundsException.class);
		thrown.expectMessage("too many");
		new RtData(2, SEP, SEP, SEP);
	}

	@Test
	public void testEmptyStringResultsInEmptyField() throws Exception
	{
		assertEquals(0, new RtData(1, "").getField(0).length);
	}

	@Test
	public void testJoiningTextWorks() throws Exception
	{
		Object[] field0 = new RtData(1, "1", "2", "3").getField(0);
		assertEquals(1, field0.length);
		assertEquals("123", field0[0]);
	}

	@Test
	public void testPrependToEmptyRtData() throws Exception
	{
		RtData rtd = new RtData(2);
		rtd.prepend("1");
		assertEquals("RTD[ \"1\" <o> \"\" ]", rtd.toString());
	}

	@Test
	public void testPrependToRtData() throws Exception
	{
		RtData rtd = new RtData(2, "1");
		rtd.prepend("2");
		assertEquals("RTD[ \"21\" <o> \"\" ]", rtd.toString());
	}

	@Test
	public void testPrependToRtDataWithObjects() throws Exception
	{
		RtData rtd = new RtData(2, ctnText("<Some Node>"));
		rtd.prepend("1");
		assertEquals("RTD[ \"1<Some Node>\" <o> \"\" ]", rtd.toString());
	}
}
