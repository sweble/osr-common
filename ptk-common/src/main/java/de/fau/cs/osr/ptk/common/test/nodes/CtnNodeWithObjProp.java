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

package de.fau.cs.osr.ptk.common.test.nodes;

import de.fau.cs.osr.ptk.common.ast.AstLeafNodeImpl;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;

public final class CtnNodeWithObjProp
		extends
			AstLeafNodeImpl<CtnNode>
		implements
			CtnNode
{
	private static final long serialVersionUID = 1L;
	
	// =====================================================================
	
	protected CtnNodeWithObjProp()
	{
	}
	
	protected CtnNodeWithObjProp(Object prop)
	{
		setProp(prop);
	}
	
	// =====================================================================
	
	@Override
	public int getNodeType()
	{
		return NT_TEST_NODE_WITH_OBJ_PROP;
	}
	
	@Override
	public String getNodeName()
	{
		return "nwop";
	}
	
	// =====================================================================
	// Properties
	
	private Object prop;
	
	public final Object getProp()
	{
		return this.prop;
	}
	
	public final void setProp(Object prop)
	{
		this.prop = prop;
	}
	
	@Override
	public final int getPropertyCount()
	{
		return 1;
	}
	
	@Override
	public final AstNodePropertyIterator propertyIterator()
	{
		return new AstNodePropertyIterator()
		{
			@Override
			protected int getPropertyCount()
			{
				return 1;
			}
			
			@Override
			protected String getName(int index)
			{
				switch (index)
				{
					case 0:
						return "prop";
						
					default:
						throw new IndexOutOfBoundsException();
				}
			}
			
			@Override
			protected Object getValue(int index)
			{
				switch (index)
				{
					case 0:
						return CtnNodeWithObjProp.this.getProp();
						
					default:
						throw new IndexOutOfBoundsException();
				}
			}
			
			@Override
			protected Object setValue(int index, Object value)
			{
				switch (index)
				{
					case 0:
					{
						Object old = CtnNodeWithObjProp.this.getProp();
						CtnNodeWithObjProp.this.setProp(value);
						return old;
					}
					
					default:
						throw new IndexOutOfBoundsException();
				}
			}
		};
	}
}
