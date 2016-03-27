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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DualHashBidiMap
{
	private final HashMap<Object, Object> normal;

	private final HashMap<Object, Object> reverse;

	public DualHashBidiMap()
	{
		this.normal = new HashMap<Object, Object>();
		this.reverse = new HashMap<Object, Object>();
	}

	public DualHashBidiMap(DualHashBidiMap other)
	{
		this();
		putAll(other.normal);
	}

	public void put(Object key, Object value)
	{
		this.normal.put(key, value);
		this.reverse.put(value, key);
	}

	private void putAll(Map<Object, Object> items)
	{
		for (Entry<Object, Object> e : items.entrySet())
			put(e.getKey(), e.getValue());
	}

	public Object get(Object key)
	{
		return this.normal.get(key);
	}

	public Object getKey(Object value)
	{
		return this.reverse.get(value);
	}
}
