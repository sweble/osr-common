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

import xtc.tree.Location;

public abstract class AstAbstractInnerNode<T extends AstNode<T>>
		extends
			AstNodeImpl<T>
		implements
			AstInnerNode<T>
{
	private static final long serialVersionUID = 3931233748530723300L;

	protected AstAbstractInnerNode()
	{
	}

	protected AstAbstractInnerNode(Location arg0)
	{
		super(arg0);
	}

	@Override
	public <S extends T> S get(int index, Class<S> clazz)
	{
		@SuppressWarnings("unchecked")
		S value = (S) get(index);
		return value;
	}

	// =========================================================================

	public abstract String[] getChildNames();

	// =========================================================================

	@Override
	public void toString(Appendable out) throws IOException
	{
		out.append(getClass().getSimpleName());
		out.append('(');
		for (int i = 0; i < size(); ++i)
		{
			if (i != 0)
				out.append(", ");

			out.append('[');
			out.append(String.valueOf(i));
			out.append("] = ");
			T child = get(i);
			if (child == null)
				out.append("null");
			else
				child.toString(out);
		}
		out.append(')');
	}

	// =========================================================================

	public static abstract class AstInnerNode1<T extends AstNode<T>>
			extends
				AstAbstractInnerNode<T>
	{
		private static final long serialVersionUID = -8841086798545623538L;

		private T n0;

		protected AstInnerNode1(Uninitialized u)
		{
		}

		protected AstInnerNode1(Location arg0)
		{
			super(arg0);
		}

		protected AstInnerNode1(T n0)
		{
			super();
			set(0, n0);
		}

		protected AstInnerNode1(Location arg0, T n0)
		{
			super(arg0);
			set(0, n0);
		}

		@Override
		public final int size()
		{
			return 1;
		}

		@Override
		public T get(int index)
		{
			switch (index)
			{
				case 0:
					return n0;
				default:
					throw new IndexOutOfBoundsException(
							"Size: " + size() + ", Index: " + index);
			}
		}

		@Override
		public T set(int index, T n)
		{
			if (n == null)
				throw new NullPointerException("node may not be null");

			T o;
			switch (index)
			{
				case 0:
					o = n0;
					n0 = n;
					break;
				default:
					throw new IndexOutOfBoundsException(
							"Size: " + size() + ", Index: " + index);
			}

			return o;
		}

		@Override
		public T remove(int index)
		{
			return set(index, null);
		}
	}

	// =========================================================================

	public static abstract class AstInnerNode2<T extends AstNode<T>>
			extends
				AstAbstractInnerNode<T>
	{
		private static final long serialVersionUID = 6501151075140985136L;

		private T n0;

		private T n1;

		protected AstInnerNode2(Uninitialized u)
		{
		}

		protected AstInnerNode2(Location arg0)
		{
			super(arg0);
		}

		protected AstInnerNode2(T n0, T n1)
		{
			super();
			set(0, n0);
			set(1, n1);
		}

		protected AstInnerNode2(
				Location arg0,
				T n0,
				T n1)
		{
			super(arg0);
			set(0, n0);
			set(1, n1);
		}

		@Override
		public final int size()
		{
			return 2;
		}

		@Override
		public T get(int index)
		{
			switch (index)
			{
				case 0:
					return n0;
				case 1:
					return n1;
				default:
					throw new IndexOutOfBoundsException(
							"Size: " + size() + ", Index: " + index);
			}
		}

		@Override
		public T set(int index, T n)
		{
			if (n == null)
				throw new NullPointerException("node may not be null");

			T o;
			switch (index)
			{
				case 0:
					o = n0;
					n0 = n;
					break;
				case 1:
					o = n1;
					n1 = n;
					break;
				default:
					throw new IndexOutOfBoundsException(
							"Size: " + size() + ", Index: " + index);
			}

			return o;
		}

		@Override
		public T remove(int index)
		{
			return set(index, null);
		}
	}

	// =========================================================================

	public static abstract class AstInnerNode3<T extends AstNode<T>>
			extends
				AstAbstractInnerNode<T>
	{
		private static final long serialVersionUID = 7450920544821225168L;

		private T n0;

		private T n1;

		private T n2;

		protected AstInnerNode3(Uninitialized u)
		{
		}

		protected AstInnerNode3(Location arg0)
		{
			super(arg0);
		}

		protected AstInnerNode3(
				T n0,
				T n1,
				T n2)
		{
			super();
			set(0, n0);
			set(1, n1);
			set(2, n2);
		}

		protected AstInnerNode3(
				Location arg0,
				T n0,
				T n1,
				T n2)
		{
			super(arg0);
			set(0, n0);
			set(1, n1);
			set(2, n2);
		}

		@Override
		public final int size()
		{
			return 3;
		}

		@Override
		public T get(int index)
		{
			switch (index)
			{
				case 0:
					return n0;
				case 1:
					return n1;
				case 2:
					return n2;
				default:
					throw new IndexOutOfBoundsException(
							"Size: " + size() + ", Index: " + index);
			}
		}

		@Override
		public T set(int index, T n)
		{
			if (n == null)
				throw new NullPointerException("node may not be null");

			T o;
			switch (index)
			{
				case 0:
					o = n0;
					n0 = n;
					break;
				case 1:
					o = n1;
					n1 = n;
					break;
				case 2:
					o = n2;
					n2 = n;
					break;
				default:
					throw new IndexOutOfBoundsException(
							"Size: " + size() + ", Index: " + index);
			}

			return o;
		}

		@Override
		public T remove(int index)
		{
			return set(index, null);
		}
	}

	// =========================================================================

	public static abstract class AstInnerNode4<T extends AstNode<T>>
			extends
				AstAbstractInnerNode<T>
	{
		private static final long serialVersionUID = -6518009272746861741L;

		private T n0;

		private T n1;

		private T n2;

		private T n3;

		protected AstInnerNode4(Uninitialized u)
		{
		}

		protected AstInnerNode4(Location arg0)
		{
			super(arg0);
		}

		protected AstInnerNode4(
				T n0,
				T n1,
				T n2,
				T n3)
		{
			super();
			set(0, n0);
			set(1, n1);
			set(2, n2);
			set(3, n3);
		}

		protected AstInnerNode4(
				Location arg0,
				T n0,
				T n1,
				T n2,
				T n3)
		{
			super(arg0);
			set(0, n0);
			set(1, n1);
			set(2, n2);
			set(3, n3);
		}

		@Override
		public final int size()
		{
			return 4;
		}

		@Override
		public T get(int index)
		{
			switch (index)
			{
				case 0:
					return n0;
				case 1:
					return n1;
				case 2:
					return n2;
				case 3:
					return n3;
				default:
					throw new IndexOutOfBoundsException(
							"Size: " + size() + ", Index: " + index);
			}
		}

		@Override
		public T set(int index, T n)
		{
			if (n == null)
				throw new NullPointerException("node may not be null");

			T o;
			switch (index)
			{
				case 0:
					o = n0;
					n0 = n;
					break;
				case 1:
					o = n1;
					n1 = n;
					break;
				case 2:
					o = n2;
					n2 = n;
					break;
				case 3:
					o = n3;
					n3 = n;
					break;
				default:
					throw new IndexOutOfBoundsException(
							"Size: " + size() + ", Index: " + index);
			}

			return o;
		}

		@Override
		public T remove(int index)
		{
			return set(index, null);
		}
	}
}
