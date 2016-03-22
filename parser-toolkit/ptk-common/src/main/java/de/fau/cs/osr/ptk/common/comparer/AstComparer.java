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

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.utils.ComparisonException;
import de.fau.cs.osr.utils.DeepComparer;
import de.fau.cs.osr.utils.DeepComparerDelegate;

public class AstComparer
{
	/**
	 * Compare two AST subtrees for equality.
	 * 
	 * Property and attribute values are compared using the <code>equals</code>
	 * method.
	 * 
	 * @param rootA
	 *            First subtree.
	 * @param rootB
	 *            Second subtree.
	 * @param compareAttributes
	 *            Whether to include the node attributes in the comparison.
	 */
	public static void compareAndThrow(
			AstNode<?> rootA,
			AstNode<?> rootB,
			boolean compareAttributes,
			boolean compareLocation) throws ComparisonException
	{
		makeComparer(compareAttributes, compareLocation).compare(rootA, rootB);
	}
	
	public static boolean compareNoThrow(
			AstNode<?> rootA,
			AstNode<?> rootB,
			boolean compareAttributes,
			boolean compareLocation)
	{
		try
		{
			compareAndThrow(rootA, rootB, compareAttributes, compareLocation);
			return true;
		}
		catch (ComparisonException e)
		{
			return false;
		}
	}
	
	public static DeepComparer makeComparer(
			boolean compareAttributes,
			boolean compareLocation)
	{
		DeepComparerDelegate entityMapDelegate = new AstEntityMapComparerDelegate();
		
		DeepComparerDelegate nodeDelegate = new AstNodeComparerDelegate(
				compareAttributes,
				compareLocation);
		
		DeepComparer comparer = new DeepComparer();
		comparer.addComparer(entityMapDelegate);
		comparer.addComparer(nodeDelegate);
		return comparer;
	}
}
