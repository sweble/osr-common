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

import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.AstStringNodeImpl;
import de.fau.cs.osr.ptk.common.ast.Uninitialized;

public final class CtnNodeWithPropAndContent
		extends
			AstStringNodeImpl<CtnNode>
		implements
			CtnNode
{
	private static final long serialVersionUID = 1L;
	
	// =====================================================================
	
	protected CtnNodeWithPropAndContent()
	{
		super(Uninitialized.X);
	}
	
	public CtnNodeWithPropAndContent(Object prop, String content)
	{
		super(content);
		setProp(prop);
	}
	
	// =====================================================================
	
	@Override
	public int getNodeType()
	{
		return NT_TEST_NODE_WITH_PROP_AND_CONTENT;
	}
	
	@Override
	public String getNodeName()
	{
		return "nwpac";
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
		return 1 + getSuperPropertyCount();
	}
	
	public final int getSuperPropertyCount()
	{
		return super.getPropertyCount();
	}
	
	@Override
	public final AstNodePropertyIterator propertyIterator()
	{
		return new StringContentNodePropertyIterator()
		{
			@Override
			protected int getPropertyCount()
			{
				return CtnNodeWithPropAndContent.this.getPropertyCount();
			}
			
			@Override
			protected String getName(int index)
			{
				switch (index - getSuperPropertyCount())
				{
					case 0:
						return "prop";
						
					default:
						return super.getName(index);
				}
			}
			
			@Override
			protected Object getValue(int index)
			{
				switch (index - getSuperPropertyCount())
				{
					case 0:
						return CtnNodeWithPropAndContent.this.getProp();
						
					default:
						return super.getValue(index);
				}
			}
			
			@Override
			protected Object setValue(int index, Object value)
			{
				switch (index - getSuperPropertyCount())
				{
					case 0:
					{
						Object old = CtnNodeWithPropAndContent.this.getProp();
						CtnNodeWithPropAndContent.this.setProp(value);
						return old;
					}
					
					default:
						return super.setValue(index, value);
				}
			}
		};
	}
}
