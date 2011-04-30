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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import de.fau.cs.osr.ptk.nodegen.parser.NameValue;
import de.fau.cs.osr.utils.FmtFileNotFoundException;
import de.fau.cs.osr.utils.StringUtils;

public class AstNodeWriter
{
	private static String tmplReflChildren = null;
	
	private static String tmplReflProperties = null;
	
	private static String tmplChild = null;
	
	private static String tmplConstructor = null;
	
	private static String tmplNodeType = null;
	
	private static String tmplProperty = null;
	
	private static String tmplClass = null;
	
	private static final SimpleDateFormat dateFormat =
	        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	// =========================================================================
	
	private final AstNodeSpec spec;
	
	private HashMap<String, String> parameterTypeMap;
	
	private Set<String> reservedWords = new HashSet<String>(Arrays.asList(new String[] {
	        "abstract", "continue", "for", "new", "switch", "assert",
	        "default", "goto", "package", "synchronized", "boolean", "do",
	        "if", "private", "this", "break", "double", "implements",
	        "protected", "throw", "byte", "else", "import", "public",
	        "throws", "case", "enum", "instanceof", "return", "transient",
	        "catch", "extends", "int", "short", "try", "char", "final",
	        "interface", "static", "void", "class", "finally", "long",
	        "strictfp", "volatile", "const", "float", "native", "super",
	        "while"
	}));
	
	// =========================================================================
	
	public static void generate(AstNodeSpec spec) throws IOException
	{
		new AstNodeWriter(spec).generateClass();
	}
	
	// =========================================================================
	
	public AstNodeWriter(AstNodeSpec spec)
	{
		this.spec = spec;
	}
	
	// =========================================================================
	
	private void generateClass() throws IOException
	{
		if (tmplClass == null)
			tmplClass = loadTemplate("skeleton-astnode");
		
		String lastGenerated = dateFormat.format(new Date());
		
		String packageName = null;
		int i = spec.getModuleName().lastIndexOf('.');
		if (i > 0)
			packageName = spec.getModuleName().substring(0, i);
		
		String packageDecl = "";
		if (packageName != null)
			packageDecl = "package " + packageName + ";";
		
		String javadoc = formatJavadoc(spec.getClassJavadoc());
		
		String extraHeader = spec.getExtraHeader();
		if (extraHeader == null)
			extraHeader = "";
		
		String extraBody = spec.getExtraBody();
		if (extraBody == null)
		{
			extraBody = "";
		}
		else
		{
			extraBody = StringUtils.indent2(extraBody, "\t");
		}
		
		String source = tmplClass;
		source = source.replace("${version}", "AstNodeGenerator");
		source = source.replace("${lastGenerated}", lastGenerated);
		source = source.replace("${package}", packageDecl);
		source = source.replace("${imports}", generateImports());
		source = source.replace("${extraHeader}", extraHeader);
		source = source.replace("${javadoc}", javadoc);
		source = source.replace("${name}", spec.getNodeName());
		source = source.replace("${extends}", spec.getClassExtends());
		source = source.replace("${implements}", generateImplements());
		source = source.replace("${constructors}", generateConstructors());
		source = source.replace("${nodeType}", generateNodeType());
		source = source.replace("${properties}", generateProperties());
		source = source.replace("${propertiesReflection}", generatePropertiesReflection());
		source = source.replace("${children}", generateChildren());
		source = source.replace("${childrenReflection}", generateChildrenReflection());
		source = source.replace("${extraBody}", extraBody);
		
		spec.setPackageName(packageName);
		
		spec.setSource(source);
	}
	
	private String generateImports()
	{
		StringBuilder imports = new StringBuilder();
		
		imports.append("import de.fau.cs.osr.ptk.common.ast.*;\n");
		
		boolean i1 = spec.getChildren() != null && !spec.getChildren().isEmpty();
		boolean i2 = spec.getProperties() != null && !spec.getProperties().isEmpty();
		
		if (i2)
		{
			//imports.append("import java.util.ArrayList;\n");
		}
		if (i1 || i2)
		{
			//imports.append("import java.util.Arrays;\n");
			//imports.append("import java.util.Collection;\n");
			//imports.append("import java.util.List;\n");
		}
		
		for (String type : spec.getImports())
		{
			imports.append("import ");
			imports.append(type.trim());
			imports.append(";\n");
		}
		
		return imports.toString();
	}
	
	private CharSequence generateImplements()
	{
		if (spec.getClassImplements().isEmpty())
			return "";
		
		StringBuilder implements_ = new StringBuilder("implements ");
		
		int i = 0;
		for (String implement : spec.getClassImplements())
		{
			if (i > 0)
				implements_.append(", ");
			implements_.append(implement);
		}
		
		return implements_.toString();
	}
	
	private String generateConstructors() throws IOException
	{
		StringBuilder constructors = new StringBuilder();
		
		if (tmplConstructor == null)
			tmplConstructor = loadTemplate("skeleton-astnode-constructor");
		
		buildTypeMap();
		
		constructors.append(generateConstructor(null));
		for (String parameterList : spec.getConstructors())
			constructors.append(generateConstructor(parameterList));
		
		return constructors.toString();
	}
	
	private String generateNodeType() throws IOException
	{
		if (spec.getNodeType() != null)
		{
			if (tmplNodeType == null)
				tmplNodeType = loadTemplate("skeleton-astnode-nodetype");
			
			return tmplNodeType.replace("${nodeType}", spec.getNodeType());
		}
		else
		{
			return "";
		}
	}
	
	private String generateConstructor(String parameterList)
	{
		String name = spec.getNodeName();
		
		String parameters = "";
		
		String[] parameterNames;
		if (parameterList != null)
		{
			parameterNames = parameterList.split("\\s*,\\s*");
			if (parameterNames.length > 1 || (parameterNames.length == 1 && parameterNames[0] != null && !parameterNames[0].isEmpty()))
				parameters = generateCtorParameters(parameterNames);
		}
		else
		{
			parameterNames = new String[0];
		}
		
		String setters =
		        generateSetterInvocations(parameterNames);
		
		String superInvocation =
		        generateSuperInvocation(parameterNames);
		
		String body = superInvocation + setters;
		
		String source = tmplConstructor;
		source = source.replace("${name}", name);
		source = source.replace("${parameters}", parameters);
		source = source.replace("${body}", body);
		
		return source;
	}
	
	private String generateCtorParameters(String[] parameterNames)
	{
		StringBuilder parameters = new StringBuilder();
		
		boolean first = true;
		for (String parameter : parameterNames)
		{
			if (!first)
				parameters.append(", ");
			
			String safeParameterName = parameter;
			if (reservedWords.contains(parameter))
				safeParameterName = parameter + "_";
			
			parameters.append(parameterTypeMap.get(parameter));
			parameters.append(' ');
			parameters.append(safeParameterName);
			
			first = false;
		}
		
		return parameters.toString();
	}
	
	private String generateSetterInvocations(String[] parameterNames)
	{
		StringBuilder propertySetters = new StringBuilder();
		
		for (String parameter : parameterNames)
		{
			for (NameValue property : spec.getProperties())
			{
				if (parameter.equals(property.getName()))
				{
					String safeParameterName = parameter;
					if (reservedWords.contains(parameter))
						safeParameterName = parameter + "_";
					
					propertySetters.append("\t\tset");
					propertySetters.append(StringUtils.startWithUppercase(parameter));
					propertySetters.append('(');
					propertySetters.append(safeParameterName);
					propertySetters.append(");\n");
				}
			}
		}
		
		return propertySetters.toString();
	}
	
	private String generateSuperInvocation(String[] parameterNames)
	{
		StringBuilder superInvocation = new StringBuilder();
		
		List<String> superParameters =
		        new ArrayList<String>(spec.getChildren().size());
		
		if (spec.getClassExtends().equals("ContentNode") ||
		        spec.getClassExtends().equals("StringContentNode"))
		{
			if (Arrays.binarySearch(parameterNames, "content") >= 0)
				superParameters.add("content");
		}
		else
		{
			for (int i = 0; i < spec.getChildren().size(); ++i)
				superParameters.add(null);
			
			for (String parameter : parameterNames)
			{
				for (int i = 0; i < spec.getChildren().size(); ++i)
				{
					if (spec.getChildren().get(i).getName().equals(parameter))
					{
						superParameters.set(i, parameter);
						break;
					}
				}
			}
		}
		
		superInvocation.append("\t\tsuper(");
		
		int i = 0;
		for (String parameter : superParameters)
		{
			if (i != 0)
				superInvocation.append(", ");
			
			if (parameter == null)
			{
				String type = spec.getChildren().get(i).getValue();
				if (type.equals("AstNode"))
				{
					superInvocation.append("(AstNode) null");
				}
				else
				{
					superInvocation.append("new ");
					superInvocation.append(type);
					superInvocation.append("()");
				}
			}
			else
			{
				String safeParameterName = parameter;
				if (reservedWords.contains(parameter))
					safeParameterName = parameter + "_";
				
				superInvocation.append(safeParameterName);
			}
			
			++i;
		}
		
		superInvocation.append(");\n");
		
		return superInvocation.toString();
	}
	
	private String generateProperties() throws IOException
	{
		StringBuilder properties = new StringBuilder();
		
		for (NameValue property : spec.getProperties())
		{
			String name = property.getName();
			String type = property.getValue();
			
			String safeName = name;
			if (reservedWords.contains(name))
				safeName = name + "_";
			
			if (tmplProperty == null)
				tmplProperty = loadTemplate("skeleton-astnode-property");
			
			String source = tmplProperty;
			source = source.replace("${name}", name);
			source = source.replace("${safeName}", safeName);
			source = source.replace("${name:underscore}", StringUtils.camelcaseToUppercase(name));
			source = source.replace("${name:firstUc}", StringUtils.startWithUppercase(name));
			source = source.replace("${type}", type);
			
			properties.append(source);
		}
		
		return properties.toString();
	}
	
	private String generatePropertiesReflection() throws IOException
	{
		StringBuilder combined = new StringBuilder();
		
		if (spec.getProperties() != null && !spec.getProperties().isEmpty())
		{
			if (tmplReflProperties == null)
				tmplReflProperties = loadTemplate("skeleton-astnode-reflection-properties");
			
			StringBuilder getNameCases = new StringBuilder();
			StringBuilder getValueCases = new StringBuilder();
			StringBuilder setValueCases = new StringBuilder();
			
			int i = 0;
			for (NameValue property : spec.getProperties())
			{
				String name = property.getName();
				String type = property.getValue();
				String ucName = StringUtils.startWithUppercase(name);
				
				getNameCases.append("\t\t\t\t\tcase ");
				getNameCases.append(i);
				getNameCases.append(":\n\t\t\t\t\t\treturn \"");
				getNameCases.append(name);
				getNameCases.append("\";\n");
				
				getValueCases.append("\t\t\t\t\tcase ");
				getValueCases.append(i);
				getValueCases.append(":\n\t\t\t\t\t\treturn ${className}.this.get");
				getValueCases.append(ucName);
				getValueCases.append("();\n");
				
				setValueCases.append("\t\t\t\t\tcase ");
				setValueCases.append(i);
				setValueCases.append(":\n\t\t\t\t\t\treturn ${className}.this.set");
				setValueCases.append(ucName);
				setValueCases.append("((");
				setValueCases.append(toObjectType(type));
				setValueCases.append(") value);\n");
				
				++i;
			}
			
			String source = tmplReflProperties;
			source = source.replace("${getNameCases}", getNameCases.toString());
			source = source.replace("${getValueCases}", getValueCases.toString());
			source = source.replace("${setValueCases}", setValueCases.toString());
			source = source.replace("${propertyCount}", String.valueOf(spec.getProperties().size()));
			
			combined.append(source);
		}
		
		String result = combined.toString();
		
		result = result.replace("${className}", spec.getNodeName());
		
		return result;
	}
	
	private String generateChildren() throws IOException
	{
		StringBuilder children = new StringBuilder();
		
		if (tmplChild == null)
			tmplChild = loadTemplate("skeleton-astnode-child");
		
		int i = 0;
		for (NameValue child : spec.getChildren())
		{
			String name = child.getName();
			String type = child.getValue();
			String index = Integer.toString(i);
			
			String safeName = name;
			if (reservedWords.contains(name))
				safeName = name + "_";
			
			String source = tmplChild;
			source = source.replace("${name}", name);
			source = source.replace("${safeName}", safeName);
			source = source.replace("${name:firstUc}", StringUtils.startWithUppercase(name));
			source = source.replace("${type}", type);
			source = source.replace("${index}", index);
			
			children.append(source);
			
			++i;
		}
		
		return children.toString();
	}
	
	private String generateChildrenReflection() throws IOException
	{
		StringBuilder combined = new StringBuilder();
		
		if (spec.getChildren() != null && !spec.getChildren().isEmpty())
		{
			if (tmplReflChildren == null)
				tmplReflChildren = loadTemplate("skeleton-astnode-reflection-children");
			
			StringBuilder childNames = new StringBuilder();
			
			int i = 0;
			for (NameValue child : spec.getChildren())
			{
				if (i++ > 0)
					childNames.append(", ");
				
				childNames.append('"');
				childNames.append(child.getName());
				childNames.append('"');
			}
			
			String source = tmplReflChildren;
			source = source.replace("${childNames}", childNames.toString());
			
			combined.append(source);
		}
		
		return combined.toString();
	}
	
	// =========================================================================
	
	private String formatJavadoc(String classJavadoc)
	{
		if (classJavadoc.isEmpty())
			return "";
		
		StringBuilder javadoc = new StringBuilder();
		
		for (String line : classJavadoc.split("\\s*\n\\s*"))
		{
			javadoc.append(" * ");
			javadoc.append(line);
			javadoc.append("\n");
		}
		
		return javadoc.toString();
	}
	
	private void buildTypeMap()
	{
		if (parameterTypeMap == null)
		{
			parameterTypeMap = new HashMap<String, String>();
			
			if (spec.getClassExtends().equals("ContentNode"))
				parameterTypeMap.put("content", "NodeList");
			
			if (spec.getClassExtends().equals("StringContentNode"))
				parameterTypeMap.put("content", "String");
			
			for (NameValue property : spec.getProperties())
				parameterTypeMap.put(property.getName(), property.getValue());
			
			for (NameValue child : spec.getChildren())
				parameterTypeMap.put(child.getName(), child.getValue());
		}
	}
	
	private String loadTemplate(String name) throws IOException
	{
		String resource = name + ".tmpl";
		
		InputStream in = getClass().getResourceAsStream(resource);
		if (in == null)
			throw new FmtFileNotFoundException(
			        "Resource not found: `%s'.",
			        resource);
		
		return IOUtils.toString(in);
	}
	
	/*
	private String makeUnderscore(String name)
	{
		String nameUnderscore;
		StringBuilder nameUnderscoreX = new StringBuilder();
		for (Character ch : name.toCharArray())
		{
			if (Character.isUpperCase(ch))
			{
				nameUnderscoreX.append('_');
				nameUnderscoreX.append(ch);
			}
			else
			{
				nameUnderscoreX.append(Character.toUpperCase(ch));
			}
		}
		nameUnderscore = nameUnderscoreX.toString();
		return nameUnderscore;
	}
	
	private String makeFirstUc(String name)
	{
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	*/

	private Object toObjectType(String type)
	{
		if ("byte".equals(type))
			return "Byte";
		else if ("short".equals(type))
			return "Short";
		else if ("int".equals(type))
			return "Integer";
		else if ("long".equals(type))
			return "Long";
		else if ("float".equals(type))
			return "Float";
		else if ("double".equals(type))
			return "Double";
		else if ("boolean".equals(type))
			return "Boolean";
		else if ("char".equals(type))
			return "Character";
		else
			return type;
	}
}
