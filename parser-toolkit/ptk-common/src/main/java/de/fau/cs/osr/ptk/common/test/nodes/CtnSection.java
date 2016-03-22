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

import de.fau.cs.osr.ptk.common.ast.AstAbstractInnerNode.AstInnerNode2;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.Uninitialized;

public final class CtnSection
		extends
			AstInnerNode2<CtnNode>
		implements
			CtnNode
{
	private static final long serialVersionUID = 1L;
	
	// =====================================================================
	
	protected CtnSection()
	{
		super(Uninitialized.X);
	}
	
	protected CtnSection(int level, CtnTitle title, CtnBody body)
	{
		super(title, body);
		setLevel(level);
	}
	
	// =====================================================================
	
	@Override
	public int getNodeType()
	{
		return NT_TEST_SECTION;
	}
	
	@Override
	public String getNodeName()
	{
		return "section";
	}
	
	// =====================================================================
	// Properties
	
	private int level;
	
	public final int getLevel()
	{
		return this.level;
	}
	
	public final void setLevel(int level)
	{
		this.level = level;
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
						return "level";
						
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
						return CtnSection.this.getLevel();
						
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
						int old = CtnSection.this.getLevel();
						CtnSection.this.setLevel((Integer) value);
						return old;
					}
					default:
						throw new IndexOutOfBoundsException();
				}
			}
		};
	}
	
	// =====================================================================
	// Children
	
	public final void setTitle(CtnTitle title)
	{
		set(0, title);
	}
	
	public final CtnTitle getTitle()
	{
		return (CtnTitle) get(0);
	}
	
	public final void removeTitle()
	{
		setTitle(CtnTitle.NO_TITLE);
	}
	
	public final boolean hasTitle()
	{
		return getTitle() != CtnTitle.NO_TITLE;
	}
	
	public final void setBody(CtnBody body)
	{
		set(1, body);
	}
	
	public final CtnBody getBody()
	{
		return (CtnBody) get(1);
	}
	
	public final void removeBody()
	{
		setBody(CtnBody.NO_BODY);
	}
	
	public final boolean hasBody()
	{
		return getBody() != CtnBody.NO_BODY;
	}
	
	private static final String[] CHILD_NAMES = new String[] { "title", "body" };
	
	public final String[] getChildNames()
	{
		return CHILD_NAMES;
	}
	
}
