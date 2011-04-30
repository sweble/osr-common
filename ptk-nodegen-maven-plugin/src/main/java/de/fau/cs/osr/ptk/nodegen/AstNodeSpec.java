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

import java.util.LinkedList;
import java.util.List;

import de.fau.cs.osr.ptk.nodegen.parser.NameValue;

final class AstNodeSpec
{
	private String nodeName;
	
	private String nodeType;
	
	private String classExtends;
	
	private String classJavadoc;
	
	private String packageName;
	
	private String source;
	
	private String extraHeader;
	
	private String extraBody;
	
	private final String moduleName;
	
	private final List<String> classImplements = new LinkedList<String>();
	
	private final List<String> constructors = new LinkedList<String>();
	
	private final List<NameValue> children = new LinkedList<NameValue>();
	
	private final List<NameValue> properties = new LinkedList<NameValue>();
	
	private final List<String> imports = new LinkedList<String>();
	
	// =========================================================================
	
	public AstNodeSpec(String moduleName)
	{
		this.moduleName = moduleName;
	}
	
	// =========================================================================
	
	public String getNodeName()
	{
		return nodeName;
	}
	
	public void setNodeName(String nodeName)
	{
		this.nodeName = nodeName;
	}
	
	public String getNodeType()
	{
		return nodeType;
	}
	
	public void setNodeType(String nodeType)
	{
		this.nodeType = nodeType;
	}
	
	public String getClassExtends()
	{
		return classExtends;
	}
	
	public void setClassExtends(String classExtends)
	{
		this.classExtends = classExtends;
	}
	
	public String getClassJavadoc()
	{
		return classJavadoc;
	}
	
	public void setClassJavadoc(String classJavadoc)
	{
		this.classJavadoc = classJavadoc;
	}
	
	public String getPackageName()
	{
		return packageName;
	}
	
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	
	public String getSource()
	{
		return source;
	}
	
	public void setSource(String source)
	{
		this.source = source;
	}
	
	public String getExtraHeader()
	{
		return extraHeader;
	}
	
	public void setExtraHeader(String extraHeader)
	{
		this.extraHeader = extraHeader;
	}
	
	public String getExtraBody()
	{
		return extraBody;
	}
	
	public void setExtraBody(String extraBody)
	{
		this.extraBody = extraBody;
	}
	
	public String getModuleName()
	{
		return moduleName;
	}
	
	public List<String> getClassImplements()
	{
		return classImplements;
	}
	
	public List<String> getConstructors()
	{
		return constructors;
	}
	
	public List<NameValue> getChildren()
	{
		return children;
	}
	
	public List<NameValue> getProperties()
	{
		return properties;
	}
	
	public List<String> getImports()
	{
		return imports;
	}
}
