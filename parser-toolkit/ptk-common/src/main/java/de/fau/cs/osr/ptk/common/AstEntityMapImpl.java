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

package de.fau.cs.osr.ptk.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.fau.cs.osr.ptk.common.ast.AstNode;

public class AstEntityMapImpl<T extends AstNode<T>>
		implements
			Serializable,
			AstEntityMap<T>
{
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, T> entityMap =
			new HashMap<Integer, T>();
	
	// =========================================================================
	
	@Override
	public int registerEntity(T entity)
	{
		int id = entityMap.size();
		entityMap.put(id, entity);
		return id;
	}
	
	@Override
	public T getEntity(int id)
	{
		return entityMap.get(id);
	}
	
	@Override
	public Set<Entry<Integer, T>> getEntities()
	{
		return Collections.unmodifiableSet(entityMap.entrySet());
	}
	
	@Override
	public Map<Integer, T> getMap()
	{
		return Collections.unmodifiableMap(entityMap);
	}
	
	@Override
	public boolean isEmpty()
	{
		return this.entityMap.isEmpty();
	}
	
	// =========================================================================
	
	@Override
	public String toString()
	{
		return "EntityMap " + entityMap;
	}
	
	// =========================================================================
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entityMap == null) ? 0 : entityMap.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AstEntityMapImpl<?> other = (AstEntityMapImpl<?>) obj;
		if (entityMap == null)
		{
			if (other.entityMap != null)
				return false;
		}
		else if (!entityMap.equals(other.entityMap))
			return false;
		return true;
	}
}
