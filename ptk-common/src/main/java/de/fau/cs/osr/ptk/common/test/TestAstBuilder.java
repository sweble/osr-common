package de.fau.cs.osr.ptk.common.test;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;

public class TestAstBuilder
{
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
		
		private NodeList title = new NodeList(new Text("Default section title"));
		
		private NodeList body = new NodeList(new Text("Default section body"));
		
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
