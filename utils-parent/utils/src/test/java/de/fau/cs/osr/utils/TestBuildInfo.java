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

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

public class TestBuildInfo
{
	// FIXME: This test is useless in this form!
	@Test
	@Ignore
	public void test() throws IOException
	{
		ClassLoader cl;

		cl = getClass().getClassLoader();

		System.out.println(BuildInfo.build(
				"de.fau.cs.osr.utils",
				"utils",
				cl));
	}
}
