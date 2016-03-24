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

package de.fau.cs.osr.ptk.common.comparer;

import de.fau.cs.osr.ptk.common.AstEntityMap;
import de.fau.cs.osr.utils.ComparisonException;
import de.fau.cs.osr.utils.DeepComparer;
import de.fau.cs.osr.utils.DeepComparerDelegate;

public class AstEntityMapComparerDelegate
		implements
			DeepComparerDelegate
{
	@Override
	public boolean compare(Object _a, Object _b, DeepComparer comparer) throws ComparisonException
	{
		if (!(_a instanceof AstEntityMap))
			return false;

		if (!(_b instanceof AstEntityMap))
			throw new ComparisonException(_a, _b);

		AstEntityMap<?> a = (AstEntityMap<?>) _a;
		AstEntityMap<?> b = (AstEntityMap<?>) _b;

		comparer.compare(a.getMap(), b.getMap());
		return true;
	}
}
