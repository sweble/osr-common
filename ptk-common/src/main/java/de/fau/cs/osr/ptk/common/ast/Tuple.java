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

public abstract class Tuple
		extends
			AstNode
{
	private static final long serialVersionUID = 4530863809977418257L;
	
	public static final class Tuple1<T1>
			extends
				Tuple
	{
		private static final long serialVersionUID = -7736773346527242367L;
		
		public T1 _1;
		
		Tuple1()
		{
		}
		
		Tuple1(T1 _1)
		{
			this._1 = _1;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
			return result;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tuple1 other = (Tuple1) obj;
			if (_1 == null)
			{
				if (other._1 != null)
					return false;
			}
			else if (!_1.equals(other._1))
				return false;
			return true;
		}
		
		@Override
		public void toString(Appendable out) throws IOException
		{
			out.append("(_1 = ");
			writeArg(out, _1);
			out.append(')');
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TUPLE_1;
		}
		
		@Override
		public int size()
		{
			return 0;
		}
	}
	
	// =========================================================================
	
	public static final class Tuple2<T1, T2>
			extends
				Tuple
	{
		private static final long serialVersionUID = -7736773346527242367L;
		
		public T1 _1;
		
		public T2 _2;
		
		Tuple2()
		{
		}
		
		Tuple2(T1 _1, T2 _2)
		{
			this._1 = _1;
			this._2 = _2;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
			result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
			return result;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tuple2<Object, Object> other = (Tuple2<Object, Object>) obj;
			if (_1 == null)
			{
				if (other._1 != null)
					return false;
			}
			else if (!_1.equals(other._1))
				return false;
			if (_2 == null)
			{
				if (other._2 != null)
					return false;
			}
			else if (!_2.equals(other._2))
				return false;
			return true;
		}
		
		@Override
		public void toString(Appendable out) throws IOException
		{
			out.append("(_1 = ");
			writeArg(out, _1);
			out.append(", _2 = ");
			writeArg(out, _2);
			out.append(')');
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TUPLE_2;
		}
		
		@Override
		public int size()
		{
			return 0;
		}
	}
	
	// =========================================================================
	
	public static final class Tuple3<T1, T2, T3>
			extends
				Tuple
	{
		private static final long serialVersionUID = -4734123596843998884L;
		
		public T1 _1;
		
		public T2 _2;
		
		public T3 _3;
		
		Tuple3()
		{
		}
		
		Tuple3(T1 _1, T2 _2, T3 _3)
		{
			this._1 = _1;
			this._2 = _2;
			this._3 = _3;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
			result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
			result = prime * result + ((_3 == null) ? 0 : _3.hashCode());
			return result;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tuple3<Object, Object, Object> other =
					(Tuple3<Object, Object, Object>) obj;
			if (_1 == null)
			{
				if (other._1 != null)
					return false;
			}
			else if (!_1.equals(other._1))
				return false;
			if (_2 == null)
			{
				if (other._2 != null)
					return false;
			}
			else if (!_2.equals(other._2))
				return false;
			if (_3 == null)
			{
				if (other._3 != null)
					return false;
			}
			else if (!_3.equals(other._3))
				return false;
			return true;
		}
		
		@Override
		public void toString(Appendable out) throws IOException
		{
			out.append("(_1 = ");
			writeArg(out, _1);
			out.append(", _2 = ");
			writeArg(out, _2);
			out.append(", _3 = ");
			writeArg(out, _3);
			out.append(')');
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TUPLE_3;
		}
		
		@Override
		public int size()
		{
			return 0;
		}
	}
	
	// =========================================================================
	
	public static final class Tuple4<T1, T2, T3, T4>
			extends
				Tuple
	{
		private static final long serialVersionUID = -4734123596843998884L;
		
		public T1 _1;
		
		public T2 _2;
		
		public T3 _3;
		
		public T4 _4;
		
		Tuple4()
		{
		}
		
		Tuple4(T1 _1, T2 _2, T3 _3, T4 _4)
		{
			this._1 = _1;
			this._2 = _2;
			this._3 = _3;
			this._4 = _4;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
			result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
			result = prime * result + ((_3 == null) ? 0 : _3.hashCode());
			result = prime * result + ((_4 == null) ? 0 : _4.hashCode());
			return result;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tuple4 other = (Tuple4) obj;
			if (_1 == null)
			{
				if (other._1 != null)
					return false;
			}
			else if (!_1.equals(other._1))
				return false;
			if (_2 == null)
			{
				if (other._2 != null)
					return false;
			}
			else if (!_2.equals(other._2))
				return false;
			if (_3 == null)
			{
				if (other._3 != null)
					return false;
			}
			else if (!_3.equals(other._3))
				return false;
			if (_4 == null)
			{
				if (other._4 != null)
					return false;
			}
			else if (!_4.equals(other._4))
				return false;
			return true;
		}
		
		@Override
		public void toString(Appendable out) throws IOException
		{
			out.append("(_1 = ");
			writeArg(out, _1);
			out.append(", _2 = ");
			writeArg(out, _2);
			out.append(", _3 = ");
			writeArg(out, _3);
			out.append(", _4 = ");
			writeArg(out, _4);
			out.append(')');
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TUPLE_4;
		}
		
		@Override
		public int size()
		{
			return 0;
		}
	}
	
	// =========================================================================
	
	public static final class Tuple5<T1, T2, T3, T4, T5>
			extends
				Tuple
	{
		private static final long serialVersionUID = -4734123596843998884L;
		
		public T1 _1;
		
		public T2 _2;
		
		public T3 _3;
		
		public T4 _4;
		
		public T5 _5;
		
		Tuple5()
		{
		}
		
		Tuple5(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5)
		{
			this._1 = _1;
			this._2 = _2;
			this._3 = _3;
			this._4 = _4;
			this._5 = _5;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
			result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
			result = prime * result + ((_3 == null) ? 0 : _3.hashCode());
			result = prime * result + ((_4 == null) ? 0 : _4.hashCode());
			result = prime * result + ((_5 == null) ? 0 : _5.hashCode());
			return result;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tuple5 other = (Tuple5) obj;
			if (_1 == null)
			{
				if (other._1 != null)
					return false;
			}
			else if (!_1.equals(other._1))
				return false;
			if (_2 == null)
			{
				if (other._2 != null)
					return false;
			}
			else if (!_2.equals(other._2))
				return false;
			if (_3 == null)
			{
				if (other._3 != null)
					return false;
			}
			else if (!_3.equals(other._3))
				return false;
			if (_4 == null)
			{
				if (other._4 != null)
					return false;
			}
			else if (!_4.equals(other._4))
				return false;
			if (_5 == null)
			{
				if (other._5 != null)
					return false;
			}
			else if (!_5.equals(other._5))
				return false;
			return true;
		}
		
		@Override
		public void toString(Appendable out) throws IOException
		{
			out.append("(_1 = ");
			writeArg(out, _1);
			out.append(", _2 = ");
			writeArg(out, _2);
			out.append(", _3 = ");
			writeArg(out, _3);
			out.append(", _4 = ");
			writeArg(out, _4);
			out.append(", _5 = ");
			writeArg(out, _5);
			out.append(')');
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TUPLE_5;
		}
		
		@Override
		public int size()
		{
			return 0;
		}
	}
	
	// =========================================================================
	
	private static void writeArg(Appendable out, Object o) throws IOException
	{
		if (o == null)
			out.append(null);
		else if (o instanceof AstNodeInterface)
			((AstNodeInterface) o).toString(out);
		else
			out.append(o.toString());
	}
	
	// =========================================================================
	
	public static <T1> Tuple1<T1> from(T1 _1)
	{
		return new Tuple1<T1>(_1);
	}
	
	public static <T1, T2> Tuple2<T1, T2> from(T1 _1, T2 _2)
	{
		return new Tuple2<T1, T2>(_1, _2);
	}
	
	public static <T1, T2, T3> Tuple3<T1, T2, T3> from(T1 _1, T2 _2, T3 _3)
	{
		return new Tuple3<T1, T2, T3>(_1, _2, _3);
	}
	
	public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> from(
			T1 _1,
			T2 _2,
			T3 _3,
			T4 _4)
	{
		return new Tuple4<T1, T2, T3, T4>(_1, _2, _3, _4);
	}
	
	public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> from(
			T1 _1,
			T2 _2,
			T3 _3,
			T4 _4,
			T5 _5)
	{
		return new Tuple5<T1, T2, T3, T4, T5>(_1, _2, _3, _4, _5);
	}
}
