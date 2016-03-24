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

public final class CtnUrl
		extends
			AstLeafNodeImpl<CtnNode>
		implements
			CtnNode
{
	private static final long serialVersionUID = 1L;

	// =========================================================================

	protected CtnUrl()
	{
	}

	protected CtnUrl(String protocol, String path)
	{
		super();
		setProtocol(protocol);
		setPath(path);

	}

	// =========================================================================

	@Override
	public int getNodeType()
	{
		return NT_TEST_URL;
	}

	@Override
	public String getNodeName()
	{
		return "url";
	}

	// =====================================================================
	// Properties

	private String protocol;

	public final String getProtocol()
	{
		return this.protocol;
	}

	public final void setProtocol(String protocol)
	{
		if (protocol == null)
			throw new NullPointerException();
		this.protocol = protocol;
	}

	private String path;

	public final String getPath()
	{
		return this.path;
	}

	public final void setPath(String path)
	{
		if (path == null)
			throw new NullPointerException();
		this.path = path;
	}

	@Override
	public final int getPropertyCount()
	{
		return 2 + getSuperPropertyCount();
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
				return CtnUrl.this.getPropertyCount();
			}

			@Override
			protected String getName(int index)
			{
				switch (index)
				{
					case 0:
						return "protocol";
					case 1:
						return "path";

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
						return CtnUrl.this.getProtocol();
					case 1:
						return CtnUrl.this.getPath();

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
						String old = CtnUrl.this.getProtocol();
						CtnUrl.this.setProtocol((String) value);
						return old;
					}
					case 1:
					{
						String old = CtnUrl.this.getPath();
						CtnUrl.this.setPath((String) value);
						return old;
					}

					default:
						throw new IndexOutOfBoundsException();
				}
			}
		};
	}
}
