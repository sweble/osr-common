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

package de.fau.cs.osr.ptk.common;

import de.fau.cs.osr.ptk.common.ast.AstNodeInterface;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;

public class AstComparer
{
	private final boolean compareAttributes;
	
	private final boolean compareLocation;
	
	// =========================================================================
	
	protected AstComparer(boolean compareAttributes, boolean compareLocation)
	{
		this.compareAttributes = compareAttributes;
		this.compareLocation = compareLocation;
	}
	
	// =========================================================================
	
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
	 * @return True if both subtrees are equal, false otherwise.
	 */
	public static boolean compare(
			AstNodeInterface rootA,
			AstNodeInterface rootB,
			boolean compareAttributes,
			boolean compareLocation)
	{
		return new AstComparer(compareAttributes, compareLocation).compareIntern(rootA, rootB);
	}
	
	// =========================================================================
	
	private boolean compareIntern(AstNodeInterface a, AstNodeInterface b)
	{
		if (a == b)
			return true;
		
		if ((a == null) != (b == null))
			return false;
		
		if (a == null)
			return true;
		
		if (a.getClass() != b.getClass())
			return false;
		
		if (compareLocation)
		{
			if (a.getNativeLocation() == null)
			{
				if (b.getNativeLocation() != null)
					return false;
			}
			else if (!a.getNativeLocation().equals(b.getNativeLocation()))
				return false;
		}
		
		// Compare attributes
		if (compareAttributes)
		{
			if (!a.getAttributes().equals(b.getAttributes()))
				return false;
		}
		
		// Compare properties
		{
			AstNodePropertyIterator i = a.propertyIterator();
			AstNodePropertyIterator j = b.propertyIterator();
			while (i.next())
			{
				// Should not be necessary, but it's here for safety
				if (!j.next())
					return false;
				
				// Should not be necessary, but it's here for safety
				if (!i.getName().equals(j.getName()))
					return false;
				
				if (!compareValues(i.getValue(), j.getValue()))
					return false;
			}
			
			// Should not be necessary, but it's here for safety
			if (j.next())
				return false;
		}
		
		// Compare children
		{
			String[] acn = a.getChildNames();
			String[] bcn = b.getChildNames();
			
			// Should not be necessary, but it's here for safety
			if (acn.length != bcn.length)
				return false;
			
			for (int i = 0; i < acn.length; ++i)
			{
				// Should not be necessary, but it's here for safety
				if (!acn[i].equals(bcn[i]))
					return false;
				
				compareIntern(a.get(i), b.get(i));
			}
		}
		
		// Subtree is equal
		return true;
	}
	
	private boolean compareValues(Object a, Object b)
	{
		if (a != null)
		{
			return a.equals(b);
		}
		else if (b == null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
