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

import java.io.IOException;
import java.util.LinkedList;

import de.fau.cs.osr.ptk.common.Visitor;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import de.fau.cs.osr.ptk.nodegen.parser.Item;
import de.fau.cs.osr.ptk.nodegen.parser.Itemize;
import de.fau.cs.osr.ptk.nodegen.parser.NameValue;
import de.fau.cs.osr.ptk.nodegen.parser.RatsDoc;
import de.fau.cs.osr.ptk.nodegen.parser.RatsDocGrammar;
import de.fau.cs.osr.ptk.nodegen.parser.Section;

public final class RatsDocGrammarVisitor
        extends
            Visitor
{
	private StringBuilder javadoc;
	
	private AstNodeSpecVisitor nodeSpecVisitor;
	
	private RatsDocGrammar ratsGrammar;
	
	private final LinkedList<AstNodeSpec> generatedNodes =
	        new LinkedList<AstNodeSpec>();
	
	// =========================================================================
	
	public LinkedList<AstNodeSpec> getGeneratedNodes()
	{
		return generatedNodes;
	}
	
	// =========================================================================
	
	@Override
	protected boolean before(AstNode node)
	{
		javadoc = new StringBuilder();
		return super.before(node);
	}
	
	@Override
	protected Object after(AstNode node, Object result)
	{
		finishAstNodeSpec();
		return super.after(node, result);
	}
	
	// =========================================================================
	
	public void visit(RatsDocGrammar n)
	{
		ratsGrammar = n;
		iterate(n);
	}
	
	public void visit(RatsDoc n)
	{
		iterate(n);
	}
	
	public void visit(NodeList n)
	{
		iterate(n);
	}
	
	public void visit(Text t)
	{
		javadoc.append("<p>");
		javadoc.append("");
		javadoc.append(t.getContent().replace("\n\n", "\n<p>"));
		javadoc.append("</p>\n");
	}
	
	public void visit(NameValue n)
	{
		javadoc.append("<dl><dt>");
		javadoc.append(n.getName());
		javadoc.append(":</dt><dd>");
		javadoc.append(n.getValue());
		javadoc.append("</dd></dl>\n");
	}
	
	public void visit(Itemize n)
	{
		javadoc.append("<ul>\n");
		iterate(n);
		javadoc.append("</ul>\n");
	}
	
	public void visit(Item n)
	{
		javadoc.append("<li>");
		iterate(n);
		javadoc.append("</li>\n");
	}
	
	public void visit(Section n) throws IOException
	{
		if (n.getTitle().equals("AST node"))
		{
			if (n.getLevel() != 2)
				throw new ParseException(
				        "Section `AST node' only allowed as second level section.",
				        n);
			
			if (nodeSpecVisitor != null)
				throw new ParseException(
				        "Only one `AST node' section allowed per Level 1 section.",
				        n);
			
			nodeSpecVisitor = new AstNodeSpecVisitor(
			        new AstNodeSpec(ratsGrammar.getModuleName()));
			
			nodeSpecVisitor.go(n.getBody());
		}
		else
		{
			if (n.getLevel() == 1)
			{
				finishAstNodeSpec();
				
				javadoc = new StringBuilder();
			}
			
			javadoc.append("<h");
			javadoc.append(n.getLevel());
			javadoc.append(">");
			javadoc.append(n.getTitle());
			javadoc.append("</h");
			javadoc.append(n.getLevel());
			javadoc.append(">\n");
			iterate(n);
		}
	}
	
	// =========================================================================
	
	private void finishAstNodeSpec()
	{
		if (nodeSpecVisitor != null)
		{
			AstNodeSpec spec = nodeSpecVisitor.getAstNodeSpec();
			
			spec.setClassJavadoc(javadoc.toString());
			
			try
			{
				AstNodeWriter.generate(spec);
				generatedNodes.add(spec);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
			
			nodeSpecVisitor = null;
		}
	}
}
