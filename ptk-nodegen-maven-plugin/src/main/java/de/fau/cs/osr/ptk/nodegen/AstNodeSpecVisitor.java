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

package de.fau.cs.osr.ptk.nodegen;

import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.ptk.common.ast.AstNodeInterface;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import de.fau.cs.osr.ptk.nodegen.parser.NameValue;
import de.fau.cs.osr.ptk.nodegen.parser.Section;

public final class AstNodeSpecVisitor
		extends
			AstVisitor
{
	private final AstNodeSpec astNodeSpec;
	
	// =========================================================================
	
	public AstNodeSpecVisitor(AstNodeSpec astNodeSpec)
	{
		this.astNodeSpec = astNodeSpec;
	}
	
	// =========================================================================
	
	public AstNodeSpec getAstNodeSpec()
	{
		return astNodeSpec;
	}
	
	// =========================================================================
	
	@Override
	protected Object visitNotFound(AstNodeInterface node)
	{
		throw new ParseException(
				"syntax error: unexpected `%s'.",
				node,
				node.toString());//node.getClass().getSimpleName());
	}
	
	// =========================================================================
	
	public void visit(NodeList n)
	{
		iterate(n);
	}
	
	public void visit(NameValue n)
	{
		String name = n.getName();
		if (astNodeSpec.getNodeName() == null && name.equals("Name"))
		{
			astNodeSpec.setNodeName(n.getValue());
		}
		else if (astNodeSpec.getClassExtends() == null && name.equals("Extends"))
		{
			astNodeSpec.setClassExtends(n.getValue());
		}
		else if (astNodeSpec.getNodeType() == null && name.equals("NodeType"))
		{
			astNodeSpec.setNodeType(n.getValue());
		}
		else if (name.equals("Implements"))
		{
			astNodeSpec.getClassImplements().add(n.getValue());
		}
		else if (name.equals("Constructor"))
		{
			astNodeSpec.getConstructors().add(n.getValue());
		}
		else if (name.equals("Import"))
		{
			astNodeSpec.getImports().add(n.getValue());
		}
		else
		{
			throw new ParseException(
					"unsupported field `%s'.",
					n,
					name);
		}
	}
	
	public void visit(Section n)
	{
		String name = n.getTitle();
		if (name.equals("Properties"))
		{
			new AstNodeSpecPropertiesVisitor().go(n.getBody());
		}
		else if (name.equals("Children"))
		{
			new AstNodeSpecChildrenVisitor().go(n.getBody());
		}
		else if (name.equals("Header"))
		{
			new AstNodeSpecHeaderVisitor().go(n.getBody());
		}
		else if (name.equals("Body"))
		{
			new AstNodeSpecBodyVisitor().go(n.getBody());
		}
		else
		{
			throw new ParseException(
					"unsupported section `%s'.",
					n,
					name);
		}
	}
	
	// =========================================================================
	
	public final class AstNodeSpecPropertiesVisitor
			extends
				AstVisitor
	{
		@Override
		public Object visitNotFound(AstNodeInterface node)
		{
			throw new ParseException(
					"syntax error: unexpected `%s'.",
					node,
					node.getClass().getSimpleName());
		}
		
		public void visit(NodeList n)
		{
			iterate(n);
		}
		
		public void visit(NameValue n)
		{
			astNodeSpec.getProperties().add(n);
		}
	}
	
	// =========================================================================
	
	public final class AstNodeSpecChildrenVisitor
			extends
				AstVisitor
	{
		@Override
		public Object visitNotFound(AstNodeInterface node)
		{
			throw new ParseException(
					"syntax error: unexpected `%s'.",
					node,
					node.getClass().getSimpleName());
		}
		
		public void visit(NodeList n)
		{
			iterate(n);
		}
		
		public void visit(NameValue n)
		{
			astNodeSpec.getChildren().add(n);
		}
	}
	
	// =========================================================================
	
	public final class AstNodeSpecHeaderVisitor
			extends
				AstVisitor
	{
		@Override
		public Object visitNotFound(AstNodeInterface node)
		{
			throw new ParseException(
					"syntax error: unexpected `%s'.",
					node,
					node.getClass().getSimpleName());
		}
		
		public void visit(Text n)
		{
			if (astNodeSpec.getExtraHeader() != null)
				throw new ParseException("Multiple header sections are not allowed!");
			
			astNodeSpec.setExtraHeader(n.getContent());
		}
	}
	
	// =========================================================================
	
	public final class AstNodeSpecBodyVisitor
			extends
				AstVisitor
	{
		@Override
		public Object visitNotFound(AstNodeInterface node)
		{
			throw new ParseException(
					"syntax error: unexpected `%s'.",
					node,
					node.getClass().getSimpleName());
		}
		
		public void visit(Text n)
		{
			if (astNodeSpec.getExtraBody() != null)
				throw new ParseException("Multiple body sections are not allowed!");
			
			astNodeSpec.setExtraBody(n.getContent());
		}
	}
}
