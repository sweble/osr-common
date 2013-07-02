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

import de.fau.cs.osr.ptk.common.ast.AstNodeList;
import de.fau.cs.osr.ptk.common.ast.AstNodeListImpl;

public interface CtnBody
		extends
			CtnNode,
			AstNodeList<CtnNode>
{
	public static final CtnBody.CtnNoBody NO_BODY = new CtnNoBody();
	
	public static final CtnBody.CtnEmptyBody EMPTY = new CtnEmptyBody();
	
	// =========================================================================
	
	public static final class CtnNoBody
			extends
				CtnEmptyImmutableNode
			implements
				CtnBody
	{
		private static final long serialVersionUID = -1064749733891892633L;
		
		protected CtnNoBody()
		{
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_BODY;
		}
		
		@Override
		public String getNodeName()
		{
			return "body";
		}
		
		@Override
		public void exchange(AstNodeList<CtnNode> other)
		{
			throw new UnsupportedOperationException(genMsg());
		}
	}
	
	// =========================================================================
	
	public static final class CtnEmptyBody
			extends
				CtnEmptyImmutableNode
			implements
				CtnBody
	{
		private static final long serialVersionUID = -1064749733891892633L;
		
		protected CtnEmptyBody()
		{
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_BODY;
		}
		
		@Override
		public String getNodeName()
		{
			return "body";
		}
		
		@Override
		public void exchange(AstNodeList<CtnNode> other)
		{
			throw new UnsupportedOperationException(genMsg());
		}
	}
	
	// =========================================================================
	
	public static final class CtnBodyImpl
			extends
				AstNodeListImpl<CtnNode>
			implements
				CtnBody
	{
		private static final long serialVersionUID = 1L;
		
		protected CtnBodyImpl()
		{
		}
		
		protected CtnBodyImpl(CtnNode... children)
		{
			super(children);
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_BODY;
		}
		
		@Override
		public String getNodeName()
		{
			return "body";
		}
	}
}
