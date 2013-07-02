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

import java.util.HashMap;
import java.util.Map;

import de.fau.cs.osr.ptk.common.serialization.NodeFactory;
import de.fau.cs.osr.ptk.common.serialization.SimpleNodeFactory;

public class CtnFactory
		extends
			SimpleNodeFactory<CtnNode>
{
	private static CtnFactory factory = null;
	
	public static NodeFactory<CtnNode> get()
	{
		if (factory == null)
			factory = new CtnFactory();
		return factory;
	}
	
	// =========================================================================
	
	private final Map<Class<?>, CtnNode> prototypes =
			new HashMap<Class<?>, CtnNode>();
	
	private final Map<NodeFactory.NamedMemberId, Object> defaultValueImmutables =
			new HashMap<NodeFactory.NamedMemberId, Object>();
	
	// =========================================================================
	
	public CtnFactory()
	{
		addPrototype(new CtnText());
		addPrototype(new CtnNodeList());
		addPrototype(new CtnSection());
		addPrototype(new CtnDocument());
		addPrototype(new CtnUrl());
		addPrototype(new CtnNodeWithObjProp());
		addPrototype(new CtnNodeWithPropAndContent());
		addPrototype(new CtnTitle.CtnTitleImpl(), CtnTitle.class);
		addPrototype(new CtnBody.CtnBodyImpl(), CtnBody.class);
		
		addDvi(CtnUrl.class, "protocol", "");
		addDvi(CtnSection.class, "body", CtnBody.NO_BODY);
		addDvi(CtnSection.class, "title", CtnTitle.NO_TITLE);
		addDvi(CtnNodeWithObjProp.class, "prop", null);
		addDvi(CtnNodeWithPropAndContent.class, "prop", null);
	}
	
	private void addPrototype(CtnNode prototype)
	{
		prototypes.put(prototype.getClass(), prototype);
	}
	
	private void addPrototype(
			CtnNode prototype,
			Class<?> clazz)
	{
		prototypes.put(clazz, prototype);
	}
	
	private void addDvi(
			Class<?> clazz,
			String memberName,
			Object defaultValue)
	{
		defaultValueImmutables.put(new NamedMemberId(clazz, memberName), defaultValue);
	}
	
	// =========================================================================
	
	// =========================================================================
	
	@Override
	public CtnNode instantiateNode(Class<?> clazz)
	{
		CtnNode p = prototypes.get(clazz);
		try
		{
			if (p != null)
				return (CtnNode) p.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return super.instantiateNode(clazz);
	}
	
	@Override
	public CtnNode instantiateDefaultChild(
			NodeFactory.NamedMemberId id, Class<?> type)
	{
		CtnNode p = (CtnNode) defaultValueImmutables.get(id);
		if (p != null)
			return p;
		if (defaultValueImmutables.containsKey(id))
			return null;
		return super.instantiateDefaultChild(id, type);
	}
	
	@Override
	public Object instantiateDefaultProperty(
			NodeFactory.NamedMemberId id, Class<?> type)
	{
		Object p = defaultValueImmutables.get(id);
		if (p != null)
			return p;
		if (defaultValueImmutables.containsKey(id))
			return null;
		return super.instantiateDefaultProperty(id, type);
	}
}
