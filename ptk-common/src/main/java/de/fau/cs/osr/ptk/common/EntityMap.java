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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.fau.cs.osr.ptk.common.ast.AstNode;

public class EntityMap
		implements
			Serializable
{
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, AstNode> entityMap =
			new HashMap<Integer, AstNode>();
	
	// =========================================================================
	
	public int registerEntity(AstNode entity)
	{
		int id = entityMap.size();
		entityMap.put(id, entity);
		return id;
	}
	
	public AstNode getEntity(int id)
	{
		return entityMap.get(id);
	}
	
	public Set<Entry<Integer, AstNode>> getEntities()
	{
		return entityMap.entrySet();
	}
	
	public Map<Integer, AstNode> getMap()
	{
		return entityMap;
	}
	
	// =========================================================================
	
	@Override
	public String toString()
	{
		return "EntityMap " + entityMap;
	}
}
