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

import java.util.Arrays;

import joptsimple.OptionException;

public class ExpectedOneOfOptionException
        extends
            OptionException
{
	private static final long serialVersionUID = 2789906429506224185L;
	
	public ExpectedOneOfOptionException(String[] longOpts)
	{
		super(Arrays.asList(longOpts));
	}
	
	@Override
	public String getMessage()
	{
		return "One and only one of the following " +
		        "options has to be specified: " + multipleOptionMessage();
	}
}
