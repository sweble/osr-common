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

package de.fau.cs.osr.ptk.common.serialization;

import de.fau.cs.osr.ptk.common.ast.AstNode;

public interface NodeFactory<T extends AstNode<T>>
{
	public T instantiateNode(Class<?> clazz);

	public T instantiateDefaultChild(NamedMemberId id, Class<?> childType);

	public Object instantiateDefaultProperty(NamedMemberId id, Class<?> type);

	// =========================================================================

	public static final class NamedMemberId
	{
		public final Class<?> nodeType;

		public final String memberName;

		// =====================================================================

		/**
		 * You must not pass null values to either of the parameters.
		 */
		public NamedMemberId(Class<?> nodeType, String memberName)
		{
			assert nodeType != null && memberName != null;

			this.nodeType = nodeType;
			this.memberName = memberName;
		}

		@Override
		public int hashCode()
		{
			return memberName.hashCode() ^ nodeType.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			assert obj != null && getClass() == obj.getClass();

			// Let's optimize a bit even if it breaks the contract of equals...
			NamedMemberId other = (NamedMemberId) obj;
			return memberName.equals(other.memberName) && (nodeType == other.nodeType);
		}
	}
}
