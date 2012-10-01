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

package de.fau.cs.osr.ptk.common.ast;

import java.io.IOException;

public class GenericParserEntity<T extends AstNodeInterface<T>>
		extends
			GenericLeafNode<T>
{
	private static final long serialVersionUID = 3182955812498375838L;
	
	public GenericParserEntity()
	{
	}
	
	public GenericParserEntity(int id)
	{
		setId(id);
	}
	
	// =========================================================================
	
	private int id;
	
	public int getId()
	{
		return id;
	}
	
	public int setId(int id)
	{
		int old = this.id;
		this.id = id;
		return old;
	}
	
	@Override
	public int getPropertyCount()
	{
		return 1;
	}
	
	@Override
	public AstNodePropertyIterator propertyIterator()
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
						return "id";
						
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
						return GenericParserEntity.this.getId();
						
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
						return GenericParserEntity.this.setId((Integer) value);
						
					default:
						throw new IndexOutOfBoundsException();
				}
			}
		};
	}
	
	// =========================================================================
	
	@Override
	public int getNodeType()
	{
		return NT_PARSER_ENTITY;
	}
	
	// =========================================================================
	
	@Override
	public void toString(Appendable out) throws IOException
	{
		out.append("GenericParserEntity(");
		out.append(String.valueOf(getId()));
		out.append(')');
	}
}
