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

import org.junit.Test;

public class MovingAverageTest
{
	@Test
	public void testName() throws Exception
	{
		long ms2ns = 1000 * 1000;
		MovingAverage ma = new MovingAverage(2 * 1000, 500);
		assertEquals(0., ma.getAverage(), 0.01);
		ma.update(0 * ms2ns, 10);
		assertEquals(10., ma.getAverage(), 0.01);

		ma.update(200 * ms2ns, 20);
		assertEquals(15., ma.getAverage(), 0.01);

		ma.update(750 * ms2ns, 45);
		assertEquals(30., ma.getAverage(), 0.01);

		ma.update(2600 * ms2ns, 5);
		assertEquals(25., ma.getAverage(), 0.01);
	}
}
