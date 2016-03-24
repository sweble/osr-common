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

package de.fau.cs.osr.ptk.common.xml;

import java.io.Serializable;
import java.util.Arrays;

final class ArbitraryObj
		implements
			Serializable
{
	private static final long serialVersionUID = 1L;

	public Object nullValue = new Object();

	public int intValue = -42;

	public String strValue = null;

	public double[] doubleValues = null;

	// =====================================================================

	public void set()
	{
		nullValue = null;
		intValue = 42;
		strValue = "some string";
		doubleValues = new double[] { 3.1415, 2.7182 };
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArbitraryObj other = (ArbitraryObj) obj;
		if (!Arrays.equals(doubleValues, other.doubleValues))
			return false;
		if (intValue != other.intValue)
			return false;
		if (nullValue == null)
		{
			if (other.nullValue != null)
				return false;
		}
		else if (!nullValue.equals(other.nullValue))
			return false;
		if (strValue == null)
		{
			if (other.strValue != null)
				return false;
		}
		else if (!strValue.equals(other.strValue))
			return false;
		return true;
	}
}
