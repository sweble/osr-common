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

package de.fau.cs.osr.ptk.printergen;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;

import de.fau.cs.osr.ptk.common.Visitor;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;
import de.fau.cs.osr.ptk.printergen.parser.Call;
import de.fau.cs.osr.ptk.printergen.parser.CtrlCall;
import de.fau.cs.osr.ptk.printergen.parser.CtrlCallEnd;
import de.fau.cs.osr.ptk.printergen.parser.CtrlContent;
import de.fau.cs.osr.ptk.printergen.parser.CtrlElif;
import de.fau.cs.osr.ptk.printergen.parser.CtrlElse;
import de.fau.cs.osr.ptk.printergen.parser.CtrlIf;
import de.fau.cs.osr.ptk.printergen.parser.CtrlIfEnd;
import de.fau.cs.osr.ptk.printergen.parser.Expr;
import de.fau.cs.osr.ptk.printergen.parser.Indent;
import de.fau.cs.osr.ptk.printergen.parser.Instruction;
import de.fau.cs.osr.ptk.printergen.parser.Newline;
import de.fau.cs.osr.ptk.printergen.parser.Parameter;
import de.fau.cs.osr.ptk.printergen.parser.PrinterTemplate;
import de.fau.cs.osr.ptk.printergen.parser.Stmt;
import de.fau.cs.osr.ptk.printergen.parser.Token;
import de.fau.cs.osr.ptk.printergen.parser.Visit;
import de.fau.cs.osr.utils.FmtFileNotFoundException;
import de.fau.cs.osr.utils.FmtInternalLogicError;
import de.fau.cs.osr.utils.StringUtils;

public final class PrinterTemplateVisitor
        extends
            Visitor
{
	private static String tmplClass = null;
	
	private static String tmplVisit = null;
	
	private static final SimpleDateFormat dateFormat =
	        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	// =========================================================================
	
	private final Log log;
	
	private final StringBuilder templates = new StringBuilder();
	
	private final Stack<String> indent = new Stack<String>();
	
	private final Stack<String> blockIndent = new Stack<String>();
	
	private final Map<String, Call> calls = new HashMap<String, Call>();
	
	private boolean wasIndentStmt = false;
	
	private boolean hadNewline = false;
	
	private boolean collapseWhitespace = false;
	
	private boolean isCall;
	
	// =========================================================================
	
	public PrinterTemplateVisitor(Log log)
	{
		super();
		this.log = log;
	}
	
	// =========================================================================
	
	public String visit(PrinterTemplate tmpl) throws IOException
	{
		if (tmplClass == null)
			tmplClass = loadTemplate("skeleton-tp");
		
		String lastGenerated = dateFormat.format(new Date());
		
		String name = tmpl.getIntro().getName();
		
		String header = tmpl.getIntro().getHeader();
		if (header == null)
			header = "";
		
		String body = tmpl.getIntro().getBody();
		if (body == null)
			body = "";
		
		String footer = tmpl.getIntro().getFooter();
		if (footer == null)
			footer = "";
		
		String packageDecl = "";
		if (tmpl.getIntro().getPackage() != null)
			packageDecl = "package " + tmpl.getIntro().getPackage() + ";";
		
		String implementsDecl = "";
		if (!tmpl.getIntro().getImplements().isEmpty())
		{
			implementsDecl = "implements ";
			
			int i = 0;
			for (String clazz : tmpl.getIntro().getImplements())
			{
				if (i++ > 0)
					implementsDecl += ", ";
				implementsDecl += clazz;
			}
		}
		
		extractCallMethods(tmpl.getTemplates());
		
		indent.push("\t\t");
		iterate(tmpl.getTemplates());
		
		NodeList params = tmpl.getIntro().getCtorParams();
		
		String ctorArgs = "";
		String ctorParams = "";
		String defaultCtor = "	public " + name + "(Writer writer)\n" +
		        "	{\n" +
		        "		super(writer);\n" +
		        "	}\n";
		
		if (params != null && !params.isEmpty())
		{
			defaultCtor = "";
			for (AstNode x : params)
			{
				Parameter p = (Parameter) x;
				ctorArgs += ", ";
				ctorArgs += p.getName();
				ctorParams += ", ";
				ctorParams += p.getType();
				ctorParams += " ";
				ctorParams += p.getName();
			}
		}
		
		String source = tmplClass;
		source = source.replace("${version}", "AstNodeGenerator");
		source = source.replace("${lastGenerated}", lastGenerated);
		source = source.replace("${name}", name);
		source = source.replace("${package}", packageDecl);
		source = source.replace("${implements}", implementsDecl);
		source = source.replace("${defaultCtor}", defaultCtor);
		source = source.replace("${ctorParams}", ctorParams);
		source = source.replace("${ctorArgs}", ctorArgs);
		source = source.replace("${header}", header);
		source = source.replace("${body}", body);
		source = source.replace("${footer}", footer);
		source = source.replace("${extends}", "Visitor");
		source = source.replace("${visits}", templates.toString());
		
		return source;
	}
	
	public void visit(Visit visit) throws IOException
	{
		if (tmplVisit == null)
			tmplVisit = loadTemplate("skeleton-tp-visit");
		
		isCall = false;
		collapseWhitespace = false;
		
		StringBuilder body = new StringBuilder();
		
		if (visit.getEol())
			body.append(newline(false));
		
		wasIndentStmt = false;
		for (AstNode n : visit.getBody())
		{
			if (!wasIndentStmt || !(n instanceof Newline))
			{
				body.append((String) dispatch(n));
			}
			else
			{
				wasIndentStmt = false;
			}
		}
		
		if (visit.getEol())
			body.append(newline(false));
		
		String type = visit.getArg().getType();
		String name = visit.getArg().getName();
		if (name == null)
			name = Character.toLowerCase(type.charAt(0)) + type.substring(1);
		
		String source = tmplVisit;
		source = source.replace("${type}", type);
		source = source.replace("${name}", name);
		source = source.replace("${body}", body.toString());
		
		templates.append(source);
	}
	
	public void visit(Call call) throws IOException
	{
	}
	
	public String visit(Indent indent)
	{
		((Instruction) indent.getInst()).setIndent(indent.getIndent());
		
		((Instruction) indent.getInst()).setIndented(true);
		
		wasIndentStmt = true;
		
		hadNewline = true;
		
		switch (((Instruction) indent.getInst()).getBlockMode())
		{
			case BlockOpen:
			{
				return newline(false) +
				        (String) dispatch(indent.getInst());
			}
			case BlockInside:
			{
				return (String) dispatch(indent.getInst());
			}
			case BlockEnd:
			{
				return (String) dispatch(indent.getInst()) +
				        newline(false);
			}
			case IsBlock:
			{
				String escapedIndent = StringUtils.escJava(
				        trimIndent(indent, indent.getIndent()));
				
				return newline(false) +
				        this.indent.peek() + "incIndent(\"" + escapedIndent + "\");\n" +
				        (String) dispatch(indent.getInst()) +
				        this.indent.peek() + "decIndent();\n" +
				        newline(false);
			}
			default:
				throw new FmtInternalLogicError();
		}
	}
	
	public String visit(Stmt stmt)
	{
		return String.format(
		        indent.peek() + "%s;\n",
		        stmt.getStmt());
	}
	
	public String visit(Expr expr)
	{
		if (wasIndentStmt)
		{
			return indent.peek() + "print(indentText(" + expr.getExpr() + "));\n";
		}
		else
		{
			return indent.peek() + "print(" + expr.getExpr() + ");\n";
		}
	}
	
	public String visit(CtrlCall ctrlCall)
	{
		blockIndent.push(ctrlCall.getIndent());
		
		Call call = calls.get(ctrlCall.getTarget());
		if (call == null)
			throw new ParseException(
			        "Target of call undefined: `%s'",
			        ctrlCall.getTarget());
		
		StringBuilder body = new StringBuilder();
		
		body.append(indent.peek() + "{\n");
		indent.push(indent.peek() + "\t");
		
		if (call.getEol() && ctrlCall.getIndent() == null)
			body.append(newline(false));
		
		isCall = true;
		
		Iterator<AstNode> template = call.getBody().iterator();
		while (true)
		{
			Indent isIndent = null;
			
			boolean doInsert = false;
			
			while (template.hasNext())
			{
				AstNode n = template.next();
				if (n instanceof CtrlContent)
				{
					doInsert = true;
					break;
				}
				else if (n instanceof Indent)
				{
					Indent indent = (Indent) n;
					if (indent.getInst() instanceof CtrlContent)
					{
						isIndent = indent;
						doInsert = true;
						break;
					}
				}
				
				if (wasIndentStmt && (n instanceof Newline))
				{
					wasIndentStmt = false;
				}
				else
				{
					body.append((String) dispatch(n));
				}
			}
			
			if (!doInsert)
				break;
			
			if (isIndent != null)
			{
				String escapedIndent = StringUtils.escJava(
				        trimIndent(isIndent, isIndent.getIndent()));
				
				body.append(
				        newline(false) +
				                this.indent.peek() + "incIndent(\"" + escapedIndent + "\");\n");
			}
			
			for (AstNode n : ctrlCall.getContent())
			{
				if (wasIndentStmt && (n instanceof Newline))
				{
					wasIndentStmt = false;
				}
				else
				{
					body.append((String) dispatch(n));
				}
			}
			
			if (isIndent != null)
			{
				wasIndentStmt = true;
				
				body.append(
				        this.indent.peek() + "decIndent();\n" +
				                newline(false));
			}
		}
		
		isCall = false;
		
		if (call.getEol() && ctrlCall.getIndent() == null)
			body.append(newline(false));
		
		blockIndent.pop();
		
		return body.toString();
	}
	
	public String visit(CtrlCallEnd n)
	{
		this.indent.pop();
		return this.indent.peek() + "}\n";
	}
	
	public void visit(CtrlContent n)
	{
		throw new ParseException("`{content}' not allowed outside call!");
	}
	
	public String visit(CtrlIf n)
	{
		blockIndent.push(n.getIndent());
		String result = indent.peek() + "if (" + n.getCond() + ") {\n";
		indent.push(indent.peek() + "\t");
		return result;
	}
	
	public String visit(CtrlElif n)
	{
		checkBlockIndentation(n);
		
		String oldIndent = indent.pop();
		String result = indent.peek() + "} else if (" + n.getCond() + ") {\n";
		indent.push(oldIndent);
		return result;
	}
	
	public String visit(CtrlElse n)
	{
		checkBlockIndentation(n);
		
		String oldIndent = indent.pop();
		String result = indent.peek() + "} else {\n";
		indent.push(oldIndent);
		return result;
	}
	
	public String visit(CtrlIfEnd n)
	{
		checkBlockIndentation(n);
		
		indent.pop();
		blockIndent.pop();
		return indent.peek() + "}\n";
	}
	
	public String visit(Newline newline)
	{
		return newline(true);
	}
	
	public String visit(Token token)
	{
		if (token.getName().equals("n"))
		{
			return newline(true);
		}
		else if (token.getName().equals("n"))
		{
			
			return indent.peek() + "print(\" \");\n";
		}
		else if (token.getName().equals("cws"))
		{
			if (isCall)
				throw new ParseException("Token `\\%cws;' must not be used inside call.");
			
			if (collapseWhitespace)
				throw new ParseException("Token `\\%cws;' must not be used twice in one visit.");
			
			collapseWhitespace = true;
			return "";
		}
		else
		{
			throw new ParseException("Unknown token: `%s'.", token.getName());
		}
	}
	
	public String visit(Text text)
	{
		String escapedText = StringUtils.escJava(
		        trimIndent(text, text.getContent()));
		
		hadNewline = false;
		
		return indent.peek() + "print(\"" + escapedText + "\");\n";
	}
	
	// =========================================================================
	
	private String newline(boolean force)
	{
		this.hadNewline = true;
		return indent.peek() + "printNewline(" + (force ? "true" : "false") + ");\n";
	}
	
	private void checkBlockIndentation(AstNode n)
	{
		String expected = blockIndent.peek();
		String actual = ((Instruction) n).getIndent();
		
		if (expected == null)
		{
			if (actual == null)
				return;
		}
		else if (expected.equals(actual))
		{
			return;
		}
		
		log.warn(n.getLocation() + ": Inconsistent indentation!");
	}
	
	private String trimIndent(AstNode n, String actual)
	{
		if (hadNewline && !blockIndent.isEmpty())
		{
			String expected = blockIndent.peek();
			if (expected != null)
			{
				if (actual.startsWith(expected))
				{
					actual = actual.substring(expected.length());
				}
				else
				{
					log.warn(n.getLocation() + ": Inconsistent indentation!");
				}
			}
		}
		return actual;
	}
	
	protected String loadTemplate(String name) throws IOException
	{
		String resource = name + ".tmpl";
		
		InputStream in = getClass().getResourceAsStream(resource);
		if (in == null)
			throw new FmtFileNotFoundException(
			        "Resource not found: `%s'.",
			        resource);
		
		return IOUtils.toString(in);
	}
	
	private void extractCallMethods(NodeList templates)
	{
		new CallMethodVisitor().go(templates);
	}
	
	public final class CallMethodVisitor
	        extends
	            Visitor
	{
		public void visit(NodeList n)
		{
			iterate(n);
		}
		
		public void visit(Visit n)
		{
		}
		
		public void visit(Call n)
		{
			calls.put(n.getName(), n);
		}
	}
}
