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

public class UnrecognizedPropertyException
		extends
			OptionException
{
	private static final long serialVersionUID = 1L;
	
	// =========================================================================
	
	protected UnrecognizedPropertyException(String option)
	{
		super(Arrays.asList(option));
	}
	
	// =========================================================================
	
	@Override
	public String getMessage()
	{
		return String.format(
				"%s is not a known property key",
				singleOptionMessage());
	}
}
