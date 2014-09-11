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

package de.fau.cs.osr.utils;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class Tuple3<T1, T2, T3>
		implements
			Cloneable,
			Serializable
{
	public T1 _1;
	
	public T2 _2;
	
	public T3 _3;
	
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
	public String toString()
	{
		return "Tuple3 [_1=" + _1 + ", _2=" + _2 + ", _3=" + _3 + "]";
	}
}
