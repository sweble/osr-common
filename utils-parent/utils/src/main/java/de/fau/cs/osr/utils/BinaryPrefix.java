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

public class BinaryPrefix
{
	private final long normValue;

	private final double doubleValue;

	private final String prefix;

	private final long factor;

	private final static String prefixes[] = new String[] {
			"", "Ki", "Mi", "Gi", "Ti", "Pi", "Ei" };

	public BinaryPrefix(long value)
	{
		int i;
		long factor = 1;
		for (i = 0; i < prefixes.length - 1; ++i)
		{
			if (value < factor * 1024)
				break;
			factor *= 1024;
		}

		this.factor = factor;

		this.normValue = value / factor;
		this.doubleValue = value / (double) factor;

		this.prefix = prefixes[i];
	}

	public long getValue()
	{
		return normValue;
	}

	public double getDoubleValue()
	{
		return doubleValue;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public long getFactor()
	{
		return factor;
	}

	public String makeUnit(String unit)
	{
		return prefix + unit;
	}

	public String makePaddedUnit(String unit)
	{
		return ((factor == 1) ? "  " : "") + prefix + unit;
	}
}
