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

import de.fau.cs.osr.utils.StringTools;

public abstract class AstStringNodeImpl<T extends AstNode<T>>
		extends
			AstLeafNodeImpl<T>
		implements
			AstStringNode<T>
{
	private static final long serialVersionUID = -3995972757553601033L;

	protected AstStringNodeImpl(Uninitialized u)
	{
	}

	protected AstStringNodeImpl(String content)
	{
		setContent(content);
	}

	// =========================================================================

	private String content;

	@Override
	public String getContent()
	{
		return content;
	}

	@Override
	public void setContent(String content)
	{
		if (content == null)
			throw new NullPointerException();
		this.content = content;
	}

	@Override
	public int getPropertyCount()
	{
		return 1;
	}

	@Override
	public AstNodePropertyIterator propertyIterator()
	{
		return new StringContentNodePropertyIterator();
	}

	protected class StringContentNodePropertyIterator
			extends
				AstNodePropertyIterator
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
					return "content";

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
					return AstStringNodeImpl.this.getContent();

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
					String old = AstStringNodeImpl.this.getContent();
					AstStringNodeImpl.this.setContent((String) value);
					return old;
				}

				default:
					throw new IndexOutOfBoundsException();
			}
		}
	}

	// =========================================================================

	@Override
	public void toString(Appendable out) throws IOException
	{
		out.append(getClass().getSimpleName());
		out.append("(\"");
		out.append(StringTools.escJava(getContent()));
		out.append("\")");
	}
}
