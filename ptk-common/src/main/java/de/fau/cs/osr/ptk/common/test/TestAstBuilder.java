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

import java.util.Collection;

import xtc.util.Pair;
import de.fau.cs.osr.ptk.common.ast.AstNodeInterface;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.GenericInnerNode.GenericInnerNode2;
import de.fau.cs.osr.ptk.common.ast.GenericLeafNode;
import de.fau.cs.osr.ptk.common.ast.GenericNodeList;
import de.fau.cs.osr.ptk.common.ast.GenericText;

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
	
	public static List astList()
	{
		return new List();
	}
	
	public static List astList(TestAstNode... children)
	{
		return new List(children);
	}
	
	public static SectionBuilder astSection()
	{
		return new SectionBuilder();
	}
	
	public static Title astTitle(TestAstNode... children)
	{
		return new Title(children);
	}
	
	public static Body astBody(TestAstNode... children)
	{
		return new Body(children);
	}
	
	public static Document astDoc(TestAstNode... children)
	{
		return new Document(children);
	}
	
	public static IdNode astId(int i)
	{
		return new IdNode(i);
	}
	
	public static UrlBuilder astUrl()
	{
		return new UrlBuilder();
	}
	
	// =========================================================================
	
	public static final class SectionBuilder
	{
		private int level = 0;
		
		private Title title = new Title(astText("Default section title"));
		
		private Body body = new Body(astText("Default section body"));
		
		public SectionBuilder withLevel(int level)
		{
			this.level = level;
			return this;
		}
		
		public SectionBuilder withTitle(Title title)
		{
			this.title = title;
			return this;
		}
		
		public SectionBuilder withTitle(TestAstNode... children)
		{
			this.title = new Title(children);
			return this;
		}
		
		public SectionBuilder withBody(Body body)
		{
			this.body = body;
			return this;
		}
		
		public SectionBuilder withBody(TestAstNode... children)
		{
			this.body = new Body(children);
			return this;
		}
		
		public Section build()
		{
			return new Section(level, title, body);
		}
	}
	
	public static final class UrlBuilder
	{
		private String protocol = "http";
		
		private String path = "example.org";
		
		public UrlBuilder withProtocol(String protocol)
		{
			this.protocol = protocol;
			return this;
		}
		
		public UrlBuilder withPath(String path)
		{
			this.path = path;
			return this;
		}
		
		public Url build()
		{
			return new Url(protocol, path);
		}
	}
	
	// =========================================================================
	
	public static final int AST_TEST_NODE = AstNodeInterface.NT_CUSTOM_BIT;
	
	public static final int NT_TEST_TEXT = AST_TEST_NODE + 1;
	
	public static final int NT_TEST_LIST = AST_TEST_NODE + 2;
	
	public static final int NT_TEST_SECTION = AST_TEST_NODE + 3;
	
	public static final int NT_TEST_TITLE = AST_TEST_NODE + 4;
	
	public static final int NT_TEST_BODY = AST_TEST_NODE + 5;
	
	public static final int NT_TEST_DOCUMENT = AST_TEST_NODE + 6;
	
	public static final int NT_TEST_URL = AST_TEST_NODE + 7;
	
	// =========================================================================
	
	public static interface TestAstNode
			extends
				AstNodeInterface<TestAstNode>
	{
	}
	
	public static final class Text
			extends
				GenericText<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		public Text()
		{
		}
		
		public Text(String text)
		{
			super(text);
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_TEXT;
		}
	}
	
	public static final class List
			extends
				GenericNodeList<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		public List()
		{
			super();
		}
		
		public List(Collection<? extends TestAstNode> list)
		{
			super(list);
		}
		
		public List(Pair<? extends TestAstNode> list)
		{
			super(list);
		}
		
		public List(TestAstNode car, Pair<? extends TestAstNode> cdr)
		{
			super(car, cdr);
		}
		
		public List(TestAstNode a, TestAstNode b, TestAstNode c, TestAstNode d)
		{
			super(a, b, c, d);
		}
		
		public List(TestAstNode a, TestAstNode b, TestAstNode c)
		{
			super(a, b, c);
		}
		
		public List(TestAstNode a, TestAstNode b)
		{
			super(a, b);
		}
		
		public List(TestAstNode... children)
		{
			super(children);
		}
		
		public List(TestAstNode child)
		{
			super(child);
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_LIST;
		}
	}
	
	public static final class Section
			extends
				GenericInnerNode2<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		public Section()
		{
		}
		
		public Section(int level, Title title, Body body)
		{
			super();
			this.level = level;
			setTitle(title);
			setBody(body);
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_SECTION;
		}
		
		// =====================================================================
		// Properties
		
		private int level;
		
		public final int getLevel()
		{
			return this.level;
		}
		
		public final int setLevel(int level)
		{
			int old = this.level;
			this.level = level;
			return old;
		}
		
		@Override
		public final int getPropertyCount()
		{
			return 1;
		}
		
		@Override
		public final AstNodePropertyIterator propertyIterator()
		{
			return new AstNodePropertyIterator()
			{
				@Override
				protected int getPropertyCount()
				{
					return 1;
				}
				
				@Override
				protected String getName(int index)
				{
					switch (index)
					{
						case 0:
							return "level";
							
						default:
							throw new IndexOutOfBoundsException();
					}
				}
				
				@Override
				protected Object getValue(int index)
				{
					switch (index)
					{
						case 0:
							return Section.this.getLevel();
							
						default:
							throw new IndexOutOfBoundsException();
					}
				}
				
				@Override
				protected Object setValue(int index, Object value)
				{
					switch (index)
					{
						case 0:
							return Section.this.setLevel((Integer) value);
							
						default:
							throw new IndexOutOfBoundsException();
					}
				}
			};
		}
		
		// =====================================================================
		// Children
		
		public final void setTitle(Title title)
		{
			set(0, title);
		}
		
		public final Title getTitle()
		{
			return (Title) get(0);
		}
		
		public final void setBody(Body body)
		{
			set(1, body);
		}
		
		public final Body getBody()
		{
			return (Body) get(1);
		}
		
		private static final String[] CHILD_NAMES = new String[] { "title", "body" };
		
		public final String[] getChildNames()
		{
			return CHILD_NAMES;
		}
		
	}
	
	public static final class Title
			extends
				GenericNodeList<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		public Title()
		{
			super();
		}
		
		public Title(Collection<? extends TestAstNode> list)
		{
			super(list);
		}
		
		public Title(Pair<? extends TestAstNode> list)
		{
			super(list);
		}
		
		public Title(TestAstNode car, Pair<? extends TestAstNode> cdr)
		{
			super(car, cdr);
		}
		
		public Title(TestAstNode a, TestAstNode b, TestAstNode c, TestAstNode d)
		{
			super(a, b, c, d);
		}
		
		public Title(TestAstNode a, TestAstNode b, TestAstNode c)
		{
			super(a, b, c);
		}
		
		public Title(TestAstNode a, TestAstNode b)
		{
			super(a, b);
		}
		
		public Title(TestAstNode... children)
		{
			super(children);
		}
		
		public Title(TestAstNode child)
		{
			super(child);
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_TITLE;
		}
	}
	
	public static final class Body
			extends
				GenericNodeList<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		public Body()
		{
			super();
		}
		
		public Body(Collection<? extends TestAstNode> list)
		{
			super(list);
		}
		
		public Body(Pair<? extends TestAstNode> list)
		{
			super(list);
		}
		
		public Body(TestAstNode car, Pair<? extends TestAstNode> cdr)
		{
			super(car, cdr);
		}
		
		public Body(TestAstNode a, TestAstNode b, TestAstNode c)
		{
			super(a, b, c);
		}
		
		public Body(TestAstNode a, TestAstNode b)
		{
			super(a, b);
		}
		
		public Body(TestAstNode... children)
		{
			super(children);
		}
		
		public Body(TestAstNode child)
		{
			super(child);
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_BODY;
		}
	}
	
	public static final class Document
			extends
				GenericNodeList<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		public Document()
		{
			super();
		}
		
		public Document(Collection<? extends TestAstNode> list)
		{
			super(list);
		}
		
		public Document(Pair<? extends TestAstNode> list)
		{
			super(list);
		}
		
		public Document(TestAstNode car, Pair<? extends TestAstNode> cdr)
		{
			super(car, cdr);
		}
		
		public Document(TestAstNode a, TestAstNode b, TestAstNode c)
		{
			super(a, b, c);
		}
		
		public Document(TestAstNode a, TestAstNode b)
		{
			super(a, b);
		}
		
		public Document(TestAstNode... children)
		{
			super(children);
		}
		
		public Document(TestAstNode child)
		{
			super(child);
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_DOCUMENT;
		}
	}
	
	public static final class IdNode
			extends
				GenericLeafNode<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		public int id = -1;
		
		public IdNode(int id)
		{
			this.id = id;
		}
		
		@Override
		public String toString()
		{
			return "IdNode [id=" + id + "]";
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IdNode other = (IdNode) obj;
			if (id != other.id)
				return false;
			return true;
		}
	}
	
	public static final class Url
			extends
				GenericLeafNode<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		public Url()
		{
		}
		
		public Url(String protocol, String path)
		{
			super();
			setProtocol(protocol);
			setPath(path);
			
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_URL;
		}
		
		// =====================================================================
		// Properties
		
		private String protocol;
		
		public final String getProtocol()
		{
			return this.protocol;
		}
		
		public final String setProtocol(String protocol)
		{
			String old = this.protocol;
			this.protocol = protocol;
			return old;
		}
		
		private String path;
		
		public final String getPath()
		{
			return this.path;
		}
		
		public final String setPath(String path)
		{
			String old = this.path;
			this.path = path;
			return old;
		}
		
		@Override
		public final int getPropertyCount()
		{
			return 2;
		}
		
		@Override
		public final AstNodePropertyIterator propertyIterator()
		{
			return new AstNodePropertyIterator()
			{
				@Override
				protected int getPropertyCount()
				{
					return 2;
				}
				
				@Override
				protected String getName(int index)
				{
					switch (index)
					{
						case 0:
							return "protocol";
						case 1:
							return "path";
							
						default:
							throw new IndexOutOfBoundsException();
					}
				}
				
				@Override
				protected Object getValue(int index)
				{
					switch (index)
					{
						case 0:
							return Url.this.getProtocol();
						case 1:
							return Url.this.getPath();
							
						default:
							throw new IndexOutOfBoundsException();
					}
				}
				
				@Override
				protected Object setValue(int index, Object value)
				{
					switch (index)
					{
						case 0:
							return Url.this.setProtocol((String) value);
						case 1:
							return Url.this.setPath((String) value);
							
						default:
							throw new IndexOutOfBoundsException();
					}
				}
			};
		}
	}
}
