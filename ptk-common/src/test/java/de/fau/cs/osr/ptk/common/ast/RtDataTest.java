package de.fau.cs.osr.ptk.common.ast;

import static de.fau.cs.osr.ptk.common.ast.RtDataPtk.*;
import static de.fau.cs.osr.ptk.common.test.TestAstBuilder.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.fau.cs.osr.ptk.common.test.TestNodeSection;

public class RtDataTest
{
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testRtdObjectHasRightSize() throws Exception
	{
		assertEquals(2, new RtDataPtk(2).size());
		
		Text text = astText();
		assertEquals(text.size() + 1, new RtDataPtk(text).size());
		
		TestNodeSection section = astSection().build();
		assertEquals(section.size() + 1, new RtDataPtk(section).size());
	}
	
	@Test
	public void testNewRtdObjectIsMadeUpOfEmptyFields() throws Exception
	{
		RtDataPtk rtd = new RtDataPtk(2);
		for (int i = 0; i < rtd.size(); ++i)
		{
			assertNotNull(rtd.getField(i));
			assertEquals(0, rtd.getField(i).length);
		}
	}
	
	@Test
	public void testComplexCtorsHaveRightSize() throws Exception
	{
		TestNodeSection section = astSection().build();
		RtDataPtk rtd = new RtDataPtk(section, "1");
		assertEquals(3, rtd.size());
	}
	
	@Test
	public void testNullInCtorDoesNotMeanNullInRtd() throws Exception
	{
		TestNodeSection section = astSection().build();
		RtDataPtk rtd = new RtDataPtk(section, "1", SEP, null, SEP, "3");
		for (int i = 0; i < rtd.size(); ++i)
			assertNotNull(rtd.getField(i));
	}
	
	@Test
	public void testRtdIsNicelyFormatted() throws Exception
	{
		assertEquals(
				"RtData: [ \"1\" O \"2.12.2\" O \"\" ]",
				new RtDataPtk(3, "1", SEP, "2.1", "2.2", SEP, null).toString());
	}
	
	@Test
	public void testTooManySEPraiseException() throws Exception
	{
		thrown.expect(IndexOutOfBoundsException.class);
		thrown.expectMessage("too many");
		new RtDataPtk(3, SEP, SEP, SEP);
	}
	
	@Test
	public void testEmptyStringResultsInEmptyField() throws Exception
	{
		assertEquals(0, new RtDataPtk(1, "").getField(0).length);
	}
	
	@Test
	public void testJoiningTextWorks() throws Exception
	{
		Object[] field0 = new RtDataPtk(1, "1", "2", "3").getField(0);
		assertEquals(1, field0.length);
		assertEquals("123", field0[0]);
	}
	
	@Test
	public void testPrependToEmptyRtData() throws Exception
	{
		RtDataPtk rtd = new RtDataPtk(2);
		rtd.prepend("1");
		assertEquals("RtData: [ \"1\" O \"\" ]", rtd.toString());
	}
	
	@Test
	public void testPrependToRtData() throws Exception
	{
		RtDataPtk rtd = new RtDataPtk(2, "1");
		rtd.prepend("2");
		assertEquals("RtData: [ \"21\" O \"\" ]", rtd.toString());
	}
	
	@Test
	public void testPrependToRtDataWithObjects() throws Exception
	{
		RtDataPtk rtd = new RtDataPtk(2, astUrl().build());
		rtd.prepend("1");
		assertEquals("RtData: [ \"1TestNodeUrl()\" O \"\" ]", rtd.toString());
	}
}
