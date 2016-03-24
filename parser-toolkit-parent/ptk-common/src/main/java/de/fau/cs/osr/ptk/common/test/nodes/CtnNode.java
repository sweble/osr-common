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

import de.fau.cs.osr.ptk.common.ast.AstNode;

/**
 * Common Test Node
 */
public interface CtnNode
		extends
			AstNode<CtnNode>
{
	public static final int AST_TEST_NODE = AstNode.NT_CUSTOM_BIT;

	public static final int NT_TEST_DOCUMENT = AST_TEST_NODE + 1;

	public static final int NT_ID_NODE = AST_TEST_NODE + 2;

	public static final int NT_TEST_SECTION = AST_TEST_NODE + 3;

	public static final int NT_TEST_TITLE = AST_TEST_NODE + 4;

	public static final int NT_TEST_BODY = AST_TEST_NODE + 5;

	public static final int NT_TEST_URL = AST_TEST_NODE + 6;

	public static final int NT_TEST_NODE_WITH_OBJ_PROP = AST_TEST_NODE + 7;

	public static final int NT_TEST_NODE_WITH_PROP_AND_CONTENT = AST_TEST_NODE + 8;
}
