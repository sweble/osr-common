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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import xtc.tree.Locatable;
import xtc.tree.Location;
import xtc.util.Pair;
import de.fau.cs.osr.ptk.common.ast.AstAbstractInnerNode.AstInnerNode2;
import de.fau.cs.osr.ptk.common.ast.AstLeafNodeImpl;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodeList;
import de.fau.cs.osr.ptk.common.ast.AstNodeListImpl;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.AstStringNodeImpl;
import de.fau.cs.osr.ptk.common.ast.AstText;
import de.fau.cs.osr.ptk.common.ast.Uninitialized;
import de.fau.cs.osr.ptk.common.serialization.NodeFactory;
import de.fau.cs.osr.ptk.common.serialization.SimpleNodeFactory;

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
	
	public static NodeList astList()
	{
		return new NodeList();
	}
	
	public static NodeList astList(TestAstNode... children)
	{
		return new NodeList(children);
	}
	
	public static SectionBuilder astSection()
	{
		return new SectionBuilder();
	}
	
	public static Title astTitle(TestAstNode... children)
	{
		return new Title.TitleImpl(children);
	}
	
	public static Body astBody(TestAstNode... children)
	{
		return new Body.BodyImpl(children);
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
	
	public static NodeWithObjProp astObjProp(Object prop)
	{
		return new NodeWithObjProp(prop);
	}
	
	public static NodeWithPropAndContent astPropContent(
			Object prop,
			String content)
	{
		return new NodeWithPropAndContent(prop, content);
	}
	
	// =========================================================================
	
	private static TestNodeFactory factory = null;
	
	private static final class TestNodeFactory
			extends
				SimpleNodeFactory<TestAstNode>
	{
		private final Map<Class<?>, TestAstNode> prototypes =
				new HashMap<Class<?>, TestAstNode>();
		
		private final Map<NodeFactory.NamedMemberId, Object> defaultValueImmutables =
				new HashMap<NodeFactory.NamedMemberId, Object>();
		
		public TestNodeFactory()
		{
			prototypes.put(Text.class, new Text());
			prototypes.put(NodeList.class, new NodeList());
			prototypes.put(Section.class, new Section());
			prototypes.put(Title.class, new Title.TitleImpl());
			prototypes.put(Body.class, new Body.BodyImpl());
			prototypes.put(Document.class, new Document());
			prototypes.put(Url.class, new Url());
			prototypes.put(NodeWithObjProp.class, new NodeWithObjProp());
			prototypes.put(NodeWithPropAndContent.class, new NodeWithPropAndContent());
			
			defaultValueImmutables.put(new NamedMemberId(Url.class, "protocol"), "");
			defaultValueImmutables.put(new NamedMemberId(NodeWithObjProp.class, "prop"), null);
			defaultValueImmutables.put(new NamedMemberId(NodeWithPropAndContent.class, "prop"), null);
			defaultValueImmutables.put(new NamedMemberId(Section.class, "body"), Body.NO_BODY);
			defaultValueImmutables.put(new NamedMemberId(Section.class, "title"), Title.NO_TITLE);
		}
		
		@Override
		public TestAstNode instantiateNode(Class<?> clazz)
		{
			TestAstNode p = prototypes.get(clazz);
			try
			{
				if (p != null)
					return (TestAstNode) p.clone();
			}
			catch (CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
			return super.instantiateNode(clazz);
		}
		
		@Override
		public TestAstNode instantiateDefaultChild(
				NodeFactory.NamedMemberId id, Class<?> type)
		{
			TestAstNode p = (TestAstNode) defaultValueImmutables.get(id);
			if (p != null)
				return p;
			if (defaultValueImmutables.containsKey(id))
				return null;
			return super.instantiateDefaultChild(id, type);
		}
		
		@Override
		public Object instantiateDefaultProperty(
				NodeFactory.NamedMemberId id, Class<?> type)
		{
			Object p = defaultValueImmutables.get(id);
			if (p != null)
				return p;
			if (defaultValueImmutables.containsKey(id))
				return null;
			return super.instantiateDefaultProperty(id, type);
		}
	}
	
	public static NodeFactory<TestAstNode> getFactory()
	{
		if (factory == null)
			factory = new TestNodeFactory();
		return factory;
	}
	
	// =========================================================================
	
	public static final class SectionBuilder
	{
		private int level = 0;
		
		private Title title = new Title.TitleImpl(astText("Default section title"));
		
		private Body body = new Body.BodyImpl(astText("Default section body"));
		
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
			this.title = new Title.TitleImpl(children);
			return this;
		}
		
		public SectionBuilder withBody(Body body)
		{
			this.body = body;
			return this;
		}
		
		public SectionBuilder withBody(TestAstNode... children)
		{
			this.body = new Body.BodyImpl(children);
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
	
	public static final int AST_TEST_NODE = AstNode.NT_CUSTOM_BIT;
	
	public static final int NT_TEST_SECTION = AST_TEST_NODE + 3;
	
	public static final int NT_TEST_TITLE = AST_TEST_NODE + 4;
	
	public static final int NT_TEST_BODY = AST_TEST_NODE + 5;
	
	public static final int NT_TEST_DOCUMENT = AST_TEST_NODE + 6;
	
	public static final int NT_TEST_URL = AST_TEST_NODE + 7;
	
	public static final int NT_TEST_NODE_WITH_OBJ_PROP = AST_TEST_NODE + 8;
	
	public static final int NT_TEST_NODE_WITH_PROP_AND_CONTENT = AST_TEST_NODE + 9;
	
	// =========================================================================
	
	public static interface TestAstNode
			extends
				AstNode<TestAstNode>
	{
	}
	
	public static final class Text
			extends
				AstText<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		protected Text()
		{
			super(Uninitialized.X);
		}
		
		public Text(String text)
		{
			super(text);
		}
	}
	
	public static final class NodeList
			extends
				AstNodeListImpl<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		public NodeList()
		{
		}
		
		public NodeList(Collection<? extends TestAstNode> list)
		{
			super(list);
		}
		
		public NodeList(Pair<? extends TestAstNode> list)
		{
			super(list);
		}
		
		public NodeList(TestAstNode car, Pair<? extends TestAstNode> cdr)
		{
			super(car, cdr);
		}
		
		public NodeList(
				TestAstNode a,
				TestAstNode b,
				TestAstNode c,
				TestAstNode d)
		{
			super(a, b, c, d);
		}
		
		public NodeList(TestAstNode a, TestAstNode b, TestAstNode c)
		{
			super(a, b, c);
		}
		
		public NodeList(TestAstNode a, TestAstNode b)
		{
			super(a, b);
		}
		
		public NodeList(TestAstNode... children)
		{
			super(children);
		}
		
		public NodeList(TestAstNode child)
		{
			super(child);
		}
	}
	
	public static final class Section
			extends
				AstInnerNode2<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		protected Section()
		{
			super(Uninitialized.X);
		}
		
		public Section(int level, Title title, Body body)
		{
			super(title, body);
			setLevel(level);
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
		
		public final void setLevel(int level)
		{
			this.level = level;
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
						{
							int old = Section.this.getLevel();
							Section.this.setLevel((Integer) value);
							return old;
						}
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
		
		public final void removeTitle()
		{
			setTitle(Title.NO_TITLE);
		}
		
		public final boolean hasTitle()
		{
			return getTitle() != Title.NO_TITLE;
		}
		
		public final void setBody(Body body)
		{
			set(1, body);
		}
		
		public final Body getBody()
		{
			return (Body) get(1);
		}
		
		public final void removeBody()
		{
			setBody(Body.NO_BODY);
		}
		
		public final boolean hasBody()
		{
			return getBody() != Body.NO_BODY;
		}
		
		private static final String[] CHILD_NAMES = new String[] { "title", "body" };
		
		public final String[] getChildNames()
		{
			return CHILD_NAMES;
		}
		
	}
	
	/**
	 * Copyright 2011 The Open Source Research Group, University of
	 * Erlangen-Nürnberg
	 * 
	 * Licensed under the Apache License, Version 2.0 (the "License"); you may
	 * not use this file except in compliance with the License. You may obtain a
	 * copy of the License at
	 * 
	 * http://www.apache.org/licenses/LICENSE-2.0
	 * 
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations
	 * under the License.
	 */
	
	public static abstract class EmptyImmutableNode
			implements
				TestAstNode
	{
		private static final long serialVersionUID = -8143436141986490761L;
		
		// =========================================================================
		
		protected String genMsg()
		{
			return "You are operating on an immutable " + getNodeName() + " object!";
		}
		
		// =========================================================================
		
		@Override
		public boolean hasLocation()
		{
			return false;
		}
		
		@Override
		public Location getLocation()
		{
			return null;
		}
		
		@Override
		public void setLocation(Location location)
		{
			// This is called by the parser, can't prevent that ...
		}
		
		@Override
		public void setLocation(Locatable locatable)
		{
			// This is called by the parser, can't prevent that ...
		}
		
		// =========================================================================
		
		@Override
		public boolean hasAttributes()
		{
			return false;
		}
		
		@Override
		public Map<String, Object> getAttributes()
		{
			return Collections.emptyMap();
		}
		
		@Override
		public void setAttributes(Map<String, Object> attrs)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public void clearAttributes()
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean hasAttribute(String name)
		{
			return false;
		}
		
		@Override
		public Object getAttribute(String name)
		{
			return null;
		}
		
		@Override
		public Object setAttribute(String name, Object value)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public Object removeAttribute(String name)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public int getIntAttribute(String name)
		{
			return 0;
		}
		
		@Override
		public Integer setIntAttribute(String name, Integer value)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean getBooleanAttribute(String name)
		{
			return false;
		}
		
		@Override
		public boolean setBooleanAttribute(String name, boolean value)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public String getStringAttribute(String name)
		{
			return null;
		}
		
		@Override
		public String setStringAttribute(String name, String value)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		// =========================================================================
		
		@Override
		public boolean hasProperties()
		{
			return false;
		}
		
		@Override
		public int getPropertyCount()
		{
			return 0;
		}
		
		@Override
		public Object getProperty(String name)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public Object getProperty(String name, Object default_)
		{
			return default_;
		}
		
		@Override
		public boolean hasProperty(String name)
		{
			return false;
		}
		
		@Override
		public Object setProperty(String name, Object value)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public AstNodePropertyIterator propertyIterator()
		{
			return new AstNodePropertyIterator()
			{
				@Override
				protected Object setValue(int index, Object value)
				{
					throw new UnsupportedOperationException(genMsg());
				}
				
				@Override
				protected Object getValue(int index)
				{
					throw new UnsupportedOperationException(genMsg());
				}
				
				@Override
				protected int getPropertyCount()
				{
					return 0;
				}
				
				@Override
				protected String getName(int index)
				{
					throw new UnsupportedOperationException(genMsg());
				}
			};
		}
		
		// =========================================================================
		
		@Override
		public abstract int getNodeType();
		
		@Override
		public boolean isNodeType(int testType)
		{
			return getNodeType() == testType;
		}
		
		@Override
		public final String getNodeTypeName()
		{
			return getClass().getName();
		}
		
		@Override
		public abstract String getNodeName();
		
		@Override
		public de.fau.cs.osr.ptk.common.ast.AstLocation getNativeLocation()
		{
			return null;
		}
		
		@Override
		public void setNativeLocation(
				de.fau.cs.osr.ptk.common.ast.AstLocation location)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean addAll(Pair<? extends TestAstNode> p)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean isList()
		{
			return false;
		}
		
		@Override
		public String[] getChildNames()
		{
			return EMPTY_CHILD_NAMES;
		}
		
		@Override
		public void toString(Appendable out) throws IOException
		{
			out.append(getClass().getSimpleName());
			out.append("()");
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public AstNode<TestAstNode> cloneWrapException()
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public AstNode<TestAstNode> deepClone() throws CloneNotSupportedException
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public AstNode<TestAstNode> deepCloneWrapException()
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		// =========================================================================
		
		@Override
		public void add(int index, TestAstNode element)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean add(TestAstNode e)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean addAll(Collection<? extends TestAstNode> c)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean addAll(int index, Collection<? extends TestAstNode> c)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public void clear()
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean contains(Object o)
		{
			return false;
		}
		
		@Override
		public boolean containsAll(Collection<?> c)
		{
			return false;
		}
		
		@Override
		public TestAstNode get(int index)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public <S extends TestAstNode> S get(int index, Class<S> clazz)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public int indexOf(Object o)
		{
			return -1;
		}
		
		@Override
		public boolean isEmpty()
		{
			return true;
		}
		
		@Override
		public Iterator<TestAstNode> iterator()
		{
			return new Iterator<TestAstNode>()
			{
				@Override
				public void remove()
				{
					throw new UnsupportedOperationException(genMsg());
				}
				
				@Override
				public TestAstNode next()
				{
					throw new UnsupportedOperationException(genMsg());
				}
				
				@Override
				public boolean hasNext()
				{
					return false;
				}
			};
		}
		
		@Override
		public int lastIndexOf(Object o)
		{
			return -1;
		}
		
		@Override
		public ListIterator<TestAstNode> listIterator()
		{
			return new NullNodeListIterator();
		}
		
		@Override
		public ListIterator<TestAstNode> listIterator(int index)
		{
			return new NullNodeListIterator();
		}
		
		@Override
		public TestAstNode remove(int index)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean remove(Object o)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean removeAll(Collection<?> c)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public boolean retainAll(Collection<?> c)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public TestAstNode set(int index, TestAstNode element)
		{
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public int size()
		{
			return 0;
		}
		
		@Override
		public List<TestAstNode> subList(int fromIndex, int toIndex)
		{
			if (fromIndex == toIndex)
				return Collections.emptyList();
			throw new UnsupportedOperationException(genMsg());
		}
		
		@Override
		public Object[] toArray()
		{
			return new Object[] {};
		}
		
		@Override
		public <T> T[] toArray(T[] a)
		{
			if (a.length > 0)
				a[0] = null;
			return a;
		}
		
		// =========================================================================
		
		private final class NullNodeListIterator
				implements
					ListIterator<TestAstNode>
		{
			@Override
			public void set(TestAstNode arg0)
			{
				throw new UnsupportedOperationException(genMsg());
			}
			
			@Override
			public void remove()
			{
				throw new UnsupportedOperationException(genMsg());
			}
			
			@Override
			public int previousIndex()
			{
				return -1;
			}
			
			@Override
			public TestAstNode previous()
			{
				throw new UnsupportedOperationException(genMsg());
			}
			
			@Override
			public int nextIndex()
			{
				return 0;
			}
			
			@Override
			public TestAstNode next()
			{
				throw new UnsupportedOperationException(genMsg());
			}
			
			@Override
			public boolean hasPrevious()
			{
				return false;
			}
			
			@Override
			public boolean hasNext()
			{
				return false;
			}
			
			@Override
			public void add(TestAstNode arg0)
			{
				throw new UnsupportedOperationException(genMsg());
			}
		}
		
		// =========================================================================
		
		@Override
		public String toString()
		{
			return "---";
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == null)
				return false;
			return (obj.getClass() == getClass());
		}
	}
	
	public static interface Title
			extends
				TestAstNode,
				AstNodeList<TestAstNode>
	{
		public static final NoTitle NO_TITLE = new NoTitle();
		
		public static final EmptyTitle EMPTY = new EmptyTitle();
		
		public static final class NoTitle
				extends
					EmptyImmutableNode
				implements
					Title
		{
			private static final long serialVersionUID = -1064749733891892633L;
			
			@Override
			public int getNodeType()
			{
				return NT_TEST_TITLE;
			}
			
			@Override
			public String getNodeName()
			{
				return Title.class.getSimpleName();
			}
			
			@Override
			public void exchange(AstNodeList<TestAstNode> other)
			{
				throw new UnsupportedOperationException(genMsg());
			}
		}
		
		public static final class EmptyTitle
				extends
					EmptyImmutableNode
				implements
					Title
		{
			private static final long serialVersionUID = -1064749733891892633L;
			
			@Override
			public int getNodeType()
			{
				return NT_TEST_TITLE;
			}
			
			@Override
			public String getNodeName()
			{
				return Title.class.getSimpleName();
			}
			
			@Override
			public void exchange(AstNodeList<TestAstNode> other)
			{
				throw new UnsupportedOperationException(genMsg());
			}
		}
		
		public static final class TitleImpl
				extends
					AstNodeListImpl<TestAstNode>
				implements
					Title
		{
			private static final long serialVersionUID = 1L;
			
			protected TitleImpl()
			{
			}
			
			public TitleImpl(TestAstNode... children)
			{
				super(children);
			}
			
			@Override
			public int getNodeType()
			{
				return NT_TEST_TITLE;
			}
			
			@Override
			public String getNodeName()
			{
				return Title.class.getSimpleName();
			}
		}
	}
	
	public static interface Body
			extends
				TestAstNode,
				AstNodeList<TestAstNode>
	{
		public static final NoBody NO_BODY = new NoBody();
		
		public static final EmptyBody EMPTY = new EmptyBody();
		
		public static final class NoBody
				extends
					EmptyImmutableNode
				implements
					Body
		{
			private static final long serialVersionUID = -1064749733891892633L;
			
			@Override
			public int getNodeType()
			{
				return NT_TEST_BODY;
			}
			
			@Override
			public String getNodeName()
			{
				return Body.class.getSimpleName();
			}
			
			@Override
			public void exchange(AstNodeList<TestAstNode> other)
			{
				throw new UnsupportedOperationException(genMsg());
			}
		}
		
		public static final class EmptyBody
				extends
					EmptyImmutableNode
				implements
					Body
		{
			private static final long serialVersionUID = -1064749733891892633L;
			
			@Override
			public int getNodeType()
			{
				return NT_TEST_BODY;
			}
			
			@Override
			public String getNodeName()
			{
				return Body.class.getSimpleName();
			}
			
			@Override
			public void exchange(AstNodeList<TestAstNode> other)
			{
				throw new UnsupportedOperationException(genMsg());
			}
		}
		
		public static final class BodyImpl
				extends
					AstNodeListImpl<TestAstNode>
				implements
					Body
		{
			private static final long serialVersionUID = 1L;
			
			protected BodyImpl()
			{
			}
			
			public BodyImpl(TestAstNode... children)
			{
				super(children);
			}
			
			@Override
			public int getNodeType()
			{
				return NT_TEST_BODY;
			}
			
			@Override
			public String getNodeName()
			{
				return Body.class.getSimpleName();
			}
		}
	}
	
	public static final class Document
			extends
				AstNodeListImpl<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		public Document()
		{
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
				AstLeafNodeImpl<TestAstNode>
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
				AstLeafNodeImpl<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		protected Url()
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
		
		public final void setProtocol(String protocol)
		{
			if (protocol == null)
				throw new NullPointerException();
			this.protocol = protocol;
		}
		
		private String path;
		
		public final String getPath()
		{
			return this.path;
		}
		
		public final void setPath(String path)
		{
			if (path == null)
				throw new NullPointerException();
			this.path = path;
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
						{
							String old = Url.this.getProtocol();
							Url.this.setProtocol((String) value);
							return old;
						}
						case 1:
						{
							String old = Url.this.getPath();
							Url.this.setPath((String) value);
							return old;
						}
						
						default:
							throw new IndexOutOfBoundsException();
					}
				}
			};
		}
	}
	
	public static final class NodeWithObjProp
			extends
				AstLeafNodeImpl<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		protected NodeWithObjProp()
		{
		}
		
		public NodeWithObjProp(Object prop)
		{
			setProp(prop);
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_NODE_WITH_OBJ_PROP;
		}
		
		// =====================================================================
		// Properties
		
		private Object prop;
		
		public final Object getProp()
		{
			return this.prop;
		}
		
		public final void setProp(Object prop)
		{
			this.prop = prop;
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
							return "prop";
							
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
							return NodeWithObjProp.this.getProp();
							
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
						{
							Object old = NodeWithObjProp.this.getProp();
							NodeWithObjProp.this.setProp(value);
							return old;
						}
						
						default:
							throw new IndexOutOfBoundsException();
					}
				}
			};
		}
	}
	
	public static final class NodeWithPropAndContent
			extends
				AstStringNodeImpl<TestAstNode>
			implements
				TestAstNode
	{
		private static final long serialVersionUID = 1L;
		
		protected NodeWithPropAndContent()
		{
			super(Uninitialized.X);
		}
		
		public NodeWithPropAndContent(Object prop, String content)
		{
			super(content);
			setProp(prop);
		}
		
		@Override
		public int getNodeType()
		{
			return NT_TEST_NODE_WITH_PROP_AND_CONTENT;
		}
		
		// =====================================================================
		// Properties
		
		private Object prop;
		
		public final Object getProp()
		{
			return this.prop;
		}
		
		public final void setProp(Object prop)
		{
			this.prop = prop;
		}
		
		@Override
		public final int getPropertyCount()
		{
			return 1 + getSuperPropertyCount();
		}
		
		public final int getSuperPropertyCount()
		{
			return super.getPropertyCount();
		}
		
		@Override
		public final AstNodePropertyIterator propertyIterator()
		{
			return new StringContentNodePropertyIterator()
			{
				@Override
				protected int getPropertyCount()
				{
					return NodeWithPropAndContent.this.getPropertyCount();
				}
				
				@Override
				protected String getName(int index)
				{
					switch (index - getSuperPropertyCount())
					{
						case 0:
							return "prop";
							
						default:
							return super.getName(index);
					}
				}
				
				@Override
				protected Object getValue(int index)
				{
					switch (index - getSuperPropertyCount())
					{
						case 0:
							return NodeWithPropAndContent.this.getProp();
							
						default:
							return super.getValue(index);
					}
				}
				
				@Override
				protected Object setValue(int index, Object value)
				{
					switch (index - getSuperPropertyCount())
					{
						case 0:
						{
							Object old = NodeWithPropAndContent.this.getProp();
							NodeWithPropAndContent.this.setProp(value);
							return old;
						}
						
						default:
							return super.setValue(index, value);
					}
				}
			};
		}
	}
}
