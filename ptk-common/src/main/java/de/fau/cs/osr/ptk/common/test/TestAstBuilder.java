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

package de.fau.cs.osr.ptk.common.test;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;

public class TestAstBuilder
{
	public static Text astText()
	{
		return new Text("Default text");
	}
	
	public static Text astText(String text)
	{
		return new Text(text);
	}
	
	public static TestSectionBuilder astSection()
	{
		return new TestSectionBuilder();
	}
	
	public static TestUrlBuilder astUrl()
	{
		return new TestUrlBuilder();
	}
	
	// =========================================================================
	
	public static final class TestSectionBuilder
	{
		private int level = 0;
		
		private NodeList title = new NodeList(astText("Default section title"));
		
		private NodeList body = new NodeList(astText("Default section body"));
		
		public TestSectionBuilder withLevel(int level)
		{
			this.level = level;
			return this;
		}
		
		public TestSectionBuilder withTitle(NodeList title)
		{
			this.title = title;
			return this;
		}
		
		public TestSectionBuilder withTitle(AstNode... children)
		{
			this.title = new NodeList();
			for (AstNode c : children)
				this.title.add(c);
			return this;
		}
		
		public TestSectionBuilder withBody(NodeList body)
		{
			this.body = body;
			return this;
		}
		
		public TestSectionBuilder withBody(AstNode... children)
		{
			this.body = new NodeList();
			for (AstNode c : children)
				this.body.add(c);
			return this;
		}
		
		public TestNodeSection build()
		{
			return new TestNodeSection(level, title, body);
		}
	}
	
	// =========================================================================
	
	public static final class TestUrlBuilder
	{
		private String protocol = "http";
		
		private String path = "example.org";
		
		public TestUrlBuilder withProtocol(String protocol)
		{
			this.protocol = protocol;
			return this;
		}
		
		public TestUrlBuilder withPath(String path)
		{
			this.path = path;
			return this;
		}
		
		public TestNodeUrl build()
		{
			return new TestNodeUrl(protocol, path);
		}
	}
}
