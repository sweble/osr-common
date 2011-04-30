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

package de.fau.cs.osr.utils.getopt;

import junit.framework.Assert;

import org.junit.Test;

public class TestOptions
{
	@Test
	public void test()
	{
		Options options = new Options();
		
		options.createOption('d', "dump")
		        .withDescription("The dump file to read (default: dump.xml).")
		        .withRequiredArg()
		        .withArgName("FILE")
		        .create();
		
		options.parse(new String[] { "--dump=bla" });
		
		Assert.assertEquals(options.value("dump"), "bla");
	}
}
