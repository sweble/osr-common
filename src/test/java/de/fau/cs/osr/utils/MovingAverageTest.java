package de.fau.cs.osr.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fau.cs.osr.utils.MovingAverage;

public class MovingAverageTest
{
	@Test
	public void testName() throws Exception
	{
		long ms2ns = 1000 * 1000;
		MovingAverage ma = new MovingAverage(2 * 1000, 500);
		assertEquals(0., ma.getAverage(), 0.01);
		ma.update(00 * ms2ns, 10);
		assertEquals(10., ma.getAverage(), 0.01);
		
		ma.update(200 * ms2ns, 20);
		assertEquals(15., ma.getAverage(), 0.01);
		
		ma.update(750 * ms2ns, 45);
		assertEquals(30., ma.getAverage(), 0.01);
		
		ma.update(2600 * ms2ns, 5);
		assertEquals(10., ma.getAverage(), 0.01);
	}
}
