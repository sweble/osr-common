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

package de.fau.cs.osr.ptk.common.jxpath;

import java.util.Locale;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;

import de.fau.cs.osr.ptk.common.ast.AstNodeInterface;

public class AstNodePointerFactory
		implements
			NodePointerFactory
{
	// We have to beat Collection (order == 10)
	public static final int AST_NODE_POINTER_FACTORY_ORDER = 9;
	
	@Override
	public int getOrder()
	{
		return AST_NODE_POINTER_FACTORY_ORDER;
	}
	
	@Override
	public NodePointer createNodePointer(
			QName name,
			Object bean,
			Locale locale)
	{
		return bean instanceof AstNodeInterface ? new AstNodePointer((AstNodeInterface) bean) : null;
	}
	
	@Override
	public NodePointer createNodePointer(
			NodePointer parent,
			QName name,
			Object bean)
	{
		return bean instanceof AstNodeInterface ? new AstNodePointer(parent, (AstNodeInterface) bean) : null;
	}
}
