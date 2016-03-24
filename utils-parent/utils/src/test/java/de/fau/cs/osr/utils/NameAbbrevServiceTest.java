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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

@SuppressWarnings("rawtypes")
public class NameAbbrevServiceTest
{
	private final Class clazz1A = de.fau.cs.osr.utils.test1.ClassA.class;

	private final Class clazz1B = de.fau.cs.osr.utils.test1.ClassB.class;

	private final Class clazz2B = de.fau.cs.osr.utils.test2.ClassB.class;

	private final Class clazz2C = de.fau.cs.osr.utils.test2.ClassC.class;

	private final String abbrev1A = "ClassA";

	private final String abbrev1B = "ClassB";

	private final String abbrev2B = "de.fau.cs.osr.utils.test2.ClassB";

	private final String abbrev2C = "ClassC";

	// =========================================================================

	@Test
	public void testAbbrevAndResolution() throws Exception
	{
		NameAbbrevService s = new NameAbbrevService(new String[] {
				"de.fau.cs.osr.utils.test1",
				"de.fau.cs.osr.utils.test2" });

		assertThat(s.abbrev(clazz1A), equalTo(abbrev1A));
		assertThat(s.abbrev(clazz1B), equalTo(abbrev1B));
		assertThat(s.abbrev(clazz2B), equalTo(abbrev2B));
		assertThat(s.abbrev(clazz2C), equalTo(abbrev2C));

		assertThat(s.resolve(abbrev1A), equalTo(clazz1A));
		assertThat(s.resolve(abbrev1B), equalTo(clazz1B));
		assertThat(s.resolve(abbrev2B), equalTo(clazz2B));
		assertThat(s.resolve(abbrev2C), equalTo(clazz2C));
	}

	@Test
	public void testAbbrevOrderBHasSameResolutionAsAbbrevOrderA() throws Exception
	{
		NameAbbrevService s = new NameAbbrevService(new String[] {
				"de.fau.cs.osr.utils.test1",
				"de.fau.cs.osr.utils.test2" });

		assertThat(s.abbrev(clazz2B), equalTo(abbrev2B));
		assertThat(s.abbrev(clazz2C), equalTo(abbrev2C));
		assertThat(s.abbrev(clazz1A), equalTo(abbrev1A));
		assertThat(s.abbrev(clazz1B), equalTo(abbrev1B));

		assertThat(s.resolve(abbrev2B), equalTo(clazz2B));
		assertThat(s.resolve(abbrev2C), equalTo(clazz2C));
		assertThat(s.resolve(abbrev1A), equalTo(clazz1A));
		assertThat(s.resolve(abbrev1B), equalTo(clazz1B));
	}

	@Test
	public void testOtherPackgeOrderHasDifferentResolution() throws Exception
	{
		Class clazz1B = de.fau.cs.osr.utils.test1.ClassB.class;
		Class clazz2B = de.fau.cs.osr.utils.test2.ClassB.class;

		String abbrev1B = "de.fau.cs.osr.utils.test1.ClassB";
		String abbrev2B = "ClassB";

		NameAbbrevService s = new NameAbbrevService(new String[] {
				"de.fau.cs.osr.utils.test2",
				"de.fau.cs.osr.utils.test1" });

		assertThat(s.abbrev(clazz1B), equalTo(abbrev1B));
		assertThat(s.abbrev(clazz2B), equalTo(abbrev2B));

		assertThat(s.resolve(abbrev1B), equalTo(clazz1B));
		assertThat(s.resolve(abbrev2B), equalTo(clazz2B));
	}
}
