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

package de.fau.cs.osr.ptk.common;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.utils.StringUtils;

public class DeepAstComparer
{
	public static class DeepComparisonException
			extends
				ComparisonException
	{
		private static final long serialVersionUID = 1L;
		
		private final Object a;
		
		private final Object b;
		
		public DeepComparisonException(Object a, Object b)
		{
			this.a = a;
			this.b = b;
		}
		
		public DeepComparisonException(
				ComparisonException e,
				AstNode<?> a,
				AstNode<?> b)
		{
			super(e);
			this.a = a;
			this.b = b;
		}
		
		public Object getObjectA()
		{
			return a;
		}
		
		public Object getObjectB()
		{
			return b;
		}
		
		public Writer toString(Writer w) throws IOException
		{
			if (getCause() != null)
				((ComparisonException) getCause()).toString(w);
			w.append(String.format(
					"Values during deep comparison differed (%s vs. %s):\n",
					(a == null ? "-" : a.getClass().getName()),
					(b == null ? "-" : b.getClass().getName())));
			printValue("a", a, w);
			printValue("b", b, w);
			return w;
		}
		
		private void printValue(String which, Object value, Writer w) throws IOException
		{
			w.append("\t");
			w.append(which);
			w.append(": ");
			if (value == null)
				w.append("null\n");
			else
			{
				w.append("\"\"\"");
				w.append(StringUtils.escJava(value.toString()));
				w.append("\"\"\"\n");
			}
		}
	}
	
	public static class AttributeComparisonException
			extends
				ComparisonException
	{
		private static final long serialVersionUID = 1L;
		
		private final String attributeName;
		
		private final Object aAttributeValue;
		
		private final Object bAttributeValue;
		
		public AttributeComparisonException(
				AstNode<?> a,
				AstNode<?> b,
				String attributeName,
				Object aAttributeValue,
				Object bAttributeValue)
		{
			this(null, a, b, attributeName, aAttributeValue, bAttributeValue);
		}
		
		public AttributeComparisonException(
				ComparisonException e,
				AstNode<?> a,
				AstNode<?> b,
				String attributeName,
				Object aAttributeValue,
				Object bAttributeValue)
		{
			super(e, a, b, AstDifference.ATTRIBUTE_VALUE_DIFFERS);
			this.attributeName = attributeName;
			this.aAttributeValue = aAttributeValue;
			this.bAttributeValue = bAttributeValue;
		}
		
		public String getAttributeName()
		{
			return attributeName;
		}
		
		public Object getAttributeAValue()
		{
			return aAttributeValue;
		}
		
		public Object getAttributeBValue()
		{
			return bAttributeValue;
		}
		
		public Writer toString(Writer w) throws IOException
		{
			if (getCause() != null)
				((ComparisonException) getCause()).toString(w);
			w.append(String.format(
					"Value of attribute `%s' differs between nodes of type %s:\n",
					attributeName,
					getA().getNodeName()));
			printValue("a", aAttributeValue, w);
			printValue("b", bAttributeValue, w);
			return w;
		}
		
		private void printValue(String which, Object value, Writer w) throws IOException
		{
			w.append("\t");
			w.append(which);
			w.append(": ");
			if (value == null)
				w.append("null\n");
			else
			{
				w.append("\"\"\"");
				w.append(StringUtils.escJava(value.toString()));
				w.append("\"\"\"\n");
			}
		}
	}
	
	public static class PropertyComparisonException
			extends
				ComparisonException
	{
		private static final long serialVersionUID = 1L;
		
		private final String propertyName;
		
		private final Object aPropertyValue;
		
		private final Object bPropertyValue;
		
		public PropertyComparisonException(
				ComparisonException e,
				AstNode<?> a,
				AstNode<?> b,
				String propertyName,
				Object aPropertyValue,
				Object bPropertyValue)
		{
			super(e, a, b, AstDifference.PROPERTY_VALUE_DIFFERS);
			this.propertyName = propertyName;
			this.aPropertyValue = aPropertyValue;
			this.bPropertyValue = bPropertyValue;
		}
		
		public String getPropertyName()
		{
			return propertyName;
		}
		
		public Object getPropertyAValue()
		{
			return aPropertyValue;
		}
		
		public Object getPropertyBValue()
		{
			return bPropertyValue;
		}
		
		public Writer toString(Writer w) throws IOException
		{
			if (getCause() != null)
				((ComparisonException) getCause()).toString(w);
			w.append(String.format(
					"Value of property `%s' differs between nodes of type %s:\n",
					propertyName,
					getA().getNodeName()));
			printValue("a", aPropertyValue, w);
			printValue("b", bPropertyValue, w);
			return w;
		}
		
		private void printValue(String which, Object value, Writer w) throws IOException
		{
			w.append("\t");
			w.append(which);
			w.append(": ");
			if (value == null)
				w.append("null\n");
			else
			{
				w.append("\"\"\"");
				w.append(StringUtils.escJava(value.toString()));
				w.append("\"\"\"\n");
			}
		}
	}
	
	public static class ComparisonOfChildrenFailedException
			extends
				ComparisonException
	{
		private static final long serialVersionUID = 1L;
		
		private final int childIndex;
		
		private final String childName;
		
		public ComparisonOfChildrenFailedException(
				AstNode<?> a,
				AstNode<?> b,
				int childIndex,
				ComparisonException e)
		{
			super(e, a, b);
			this.childIndex = childIndex;
			this.childName = null;
		}
		
		public ComparisonOfChildrenFailedException(
				AstNode<?> a,
				AstNode<?> b,
				int childIndex,
				String childName,
				ComparisonException e)
		{
			super(e, a, b);
			this.childIndex = childIndex;
			this.childName = childName;
		}
		
		public int getChildIndex()
		{
			return childIndex;
		}
		
		public String getChildName()
		{
			return childName;
		}
		
		public Writer toString(Writer w) throws IOException
		{
			((ComparisonException) getCause()).toString(w);
			if (childName == null)
			{
				w.append(String.format(
						"The %dth child nodes of two list nodes of type %s differ.\n",
						childIndex,
						getA().getNodeName()));
			}
			else
			{
				w.append(String.format(
						"The %dth child nodes (`%s') of two list nodes of type %s differ.\n",
						childIndex,
						childName,
						getA().getNodeName()));
			}
			return w;
		}
	}
	
	public static class ComparisonException
			extends
				Exception
	{
		private static final long serialVersionUID = 1L;
		
		private final AstNode<?> a;
		
		private final AstNode<?> b;
		
		private final AstDifference reason;
		
		public ComparisonException()
		{
			this(null, null, AstDifference.DEEP_COMPARISON_FAILED);
		}
		
		public ComparisonException(ComparisonException e)
		{
			this(e, null, null, AstDifference.DEEP_COMPARISON_FAILED);
		}
		
		public ComparisonException(
				AstNode<?> a,
				AstNode<?> b,
				AstDifference reason)
		{
			this.a = a;
			this.b = b;
			this.reason = reason;
		}
		
		protected ComparisonException(
				ComparisonException e,
				AstNode<?> a,
				AstNode<?> b)
		{
			this(e, a, b, AstDifference.CHILDREN_DIFFER);
		}
		
		protected ComparisonException(
				ComparisonException e,
				AstNode<?> a,
				AstNode<?> b,
				AstDifference reason)
		{
			super(e);
			this.a = a;
			this.b = b;
			this.reason = reason;
		}
		
		public AstNode<?> getA()
		{
			return a;
		}
		
		public AstNode<?> getB()
		{
			return b;
		}
		
		public AstDifference getReason()
		{
			return reason;
		}
		
		protected Writer toString(Writer w) throws IOException
		{
			w.append(String.format(
					"Two nodes of type %s differ: %s\n",
					getA().getNodeName(),
					reason.getReason()));
			return w;
		}
		
		@Override
		public String toString()
		{
			try
			{
				return toString(new StringWriter()).toString();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public static enum AstDifference
	{
		NULL_VS_NON_NULL
		{
			@Override
			public String getReason()
			{
				return "One node is null the other is non-null";
			}
		},
		NODE_TYPES_DIFFER
		{
			@Override
			public String getReason()
			{
				return "The two nodes have different type";
			}
		},
		LOCATION_DIFFERS
		{
			@Override
			public String getReason()
			{
				return "The two nodes have different locations";
			}
		},
		NUMBER_OF_CHILDREN_DIFFERS
		{
			@Override
			public String getReason()
			{
				return "The two nodes' number of children differs";
			}
		},
		PROPERTY_VALUE_DIFFERS
		{
			@Override
			public String getReason()
			{
				return "The value of a property differs between the two nodes";
			}
		},
		CHILDREN_DIFFER
		{
			@Override
			public String getReason()
			{
				return "One of the child nodes differs between the two nodes";
			}
		},
		NUMBER_OF_ATTRIBUTES_DIFFERS
		{
			@Override
			public String getReason()
			{
				return "The two nodes' number of attributes differs";
			}
		},
		ATTRIBUTE_VALUE_DIFFERS
		{
			@Override
			public String getReason()
			{
				return "The value of an attribute differs between the two nodes";
			}
		},
		DEEP_COMPARISON_FAILED
		{
			@Override
			public String getReason()
			{
				return "Deep comparison of two values failed";
			}
		};
		
		public abstract String getReason();
	}
	
	// =========================================================================
	
	private final boolean compareAttributes;
	
	private final boolean compareLocation;
	
	// =========================================================================
	
	protected DeepAstComparer(boolean compareAttributes, boolean compareLocation)
	{
		this.compareAttributes = compareAttributes;
		this.compareLocation = compareLocation;
	}
	
	// =========================================================================
	
	/**
	 * Compare two AST subtrees for equality.
	 * 
	 * Property and attribute values are compared using the <code>equals</code>
	 * method.
	 * 
	 * @param rootA
	 *            First subtree.
	 * @param rootB
	 *            Second subtree.
	 * @param compareAttributes
	 *            Whether to include the node attributes in the comparison.
	 * @return True if both subtrees are equal, false otherwise.
	 * @throws ComparisonException
	 */
	public static void compare(
			AstNode<?> rootA,
			AstNode<?> rootB,
			boolean compareAttributes,
			boolean compareLocation) throws ComparisonException
	{
		new DeepAstComparer(compareAttributes, compareLocation).compareIntern(rootA, rootB);
	}
	
	// =========================================================================
	
	private void compareIntern(AstNode<?> a, AstNode<?> b) throws ComparisonException
	{
		if (a == b)
			return;
		
		if ((a == null) != (b == null))
			throw new ComparisonException(a, b, AstDifference.NULL_VS_NON_NULL);
		
		if (a == null)
			return;
		
		if (a.getClass() != b.getClass())
			throw new ComparisonException(a, b, AstDifference.NODE_TYPES_DIFFER);
		
		if (compareLocation)
		{
			if (a.getNativeLocation() == null)
			{
				if (b.getNativeLocation() != null)
					throw new ComparisonException(a, b, AstDifference.LOCATION_DIFFERS);
			}
			else if (!a.getNativeLocation().equals(b.getNativeLocation()))
				throw new ComparisonException(a, b, AstDifference.LOCATION_DIFFERS);
		}
		
		// Compare attributes
		if (compareAttributes)
		{
			compareAttributes(a, b);
		}
		
		// Compare properties
		{
			AstNodePropertyIterator i = a.propertyIterator();
			AstNodePropertyIterator j = b.propertyIterator();
			while (i.next())
			{
				// Should not be necessary, but it's here for safety
				if (!j.next())
					throw new InternalError();
				
				// Should not be necessary, but it's here for safety
				if (!i.getName().equals(j.getName()))
					throw new InternalError();
				
				try
				{
					compareValues(i.getValue(), j.getValue());
				}
				catch (ComparisonException e)
				{
					throw new PropertyComparisonException(e, a, b, i.getName(), i.getValue(), j.getValue());
				}
			}
			
			// Should not be necessary, but it's here for safety
			if (j.next())
				throw new InternalError();
		}
		
		// Compare children
		if (a.isList())
		{
			if (a.size() != b.size())
				throw new ComparisonException(a, b, AstDifference.NUMBER_OF_CHILDREN_DIFFERS);
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Iterator<AstNode> i = (Iterator) a.iterator();
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Iterator<AstNode> j = (Iterator) b.iterator();
			
			int k = 0;
			while (i.hasNext() & j.hasNext())
			{
				try
				{
					compareIntern(i.next(), j.next());
					++k;
				}
				catch (ComparisonException e)
				{
					throw new ComparisonOfChildrenFailedException(a, b, k, e);
				}
			}
		}
		else
		{
			String[] acn = a.getChildNames();
			String[] bcn = b.getChildNames();
			
			// Should not be necessary, but it's here for safety
			if (acn.length != bcn.length)
				throw new InternalError();
			
			for (int i = 0; i < acn.length; ++i)
			{
				// Should not be necessary, but it's here for safety
				if (!acn[i].equals(bcn[i]))
					throw new InternalError();
				
				try
				{
					compareIntern(a.get(i), b.get(i));
				}
				catch (ComparisonException e)
				{
					throw new ComparisonOfChildrenFailedException(a, b, i, acn[i], e);
				}
			}
		}
		
		// Subtree is equal
	}
	
	private void compareValues(Object a, Object b) throws DeepComparisonException
	{
		if (a == b)
			return;
		if (a == null || b == null)
			throw new DeepComparisonException(a, b);
		
		if (a.getClass() != b.getClass())
			throw new DeepComparisonException(a, b);
		
		if (a.getClass().isArray())
			compareArrays((Object[]) a, (Object[]) b);
		
		if (a instanceof Collection)
			compareCollections((Collection<?>) a, (Collection<?>) b);
		
		if (a instanceof Map)
			compareMaps((Map<?, ?>) a, (Map<?, ?>) b);
		
		if (a instanceof AstNode)
			compareAstNodes((AstNode<?>) a, (AstNode<?>) b);
		
		if (!a.equals(b))
			throw new DeepComparisonException(a, b);
	}
	
	private void compareMaps(Map<?, ?> a, Map<?, ?> b) throws DeepComparisonException
	{
		if (b == a)
			return;
		
		if (b.size() != a.size())
			throw new DeepComparisonException(a, b);
		
		Iterator<?> i = a.entrySet().iterator();
		while (i.hasNext())
		{
			Entry<?, ?> e = (Entry<?, ?>) i.next();
			Object key = e.getKey();
			Object value = e.getValue();
			if (value == null)
			{
				if (!(b.get(key) == null && b.containsKey(key)))
					throw new DeepComparisonException(a, b);
			}
			else
			{
				compareValues(value, b.get(key));
			}
		}
	}
	
	private void compareCollections(Collection<?> a, Collection<?> b) throws DeepComparisonException
	{
		if (b == a)
			return;
		
		Iterator<?> e1 = a.iterator();
		Iterator<?> e2 = b.iterator();
		while (e1.hasNext() && e2.hasNext())
		{
			Object o1 = e1.next();
			Object o2 = e2.next();
			if ((o1 == null) != (o2 == null))
				throw new DeepComparisonException(a, b);
			compareValues(o1, o2);
		}
		if (e1.hasNext() || e2.hasNext())
			throw new DeepComparisonException(a, b);
	}
	
	private void compareAstNodes(AstNode<?> a, AstNode<?> b) throws DeepComparisonException
	{
		try
		{
			compareIntern(a, b);
		}
		catch (ComparisonException e)
		{
			throw new DeepComparisonException(e, a, b);
		}
	}
	
	private void compareArrays(Object[] a, Object[] b) throws DeepComparisonException
	{
		if (a == b)
			return;
		if (a == null || b == null)
			throw new DeepComparisonException(a, b);
		
		int length = a.length;
		if (b.length != length)
			throw new DeepComparisonException(a, b);
		
		for (int i = 0; i < length; i++)
		{
			Object ac = a[i];
			Object bc = b[i];
			compareValues(ac, bc);
		}
	}
	
	private void compareAttributes(AstNode<?> na, AstNode<?> nb) throws ComparisonException
	{
		Map<String, Object> a = na.getAttributes();
		Map<String, Object> b = nb.getAttributes();
		
		if (b == a)
			return;
		
		if (b.size() != a.size())
			throw new ComparisonException(na, nb, AstDifference.NUMBER_OF_ATTRIBUTES_DIFFERS);
		
		Iterator<Entry<String, Object>> i = a.entrySet().iterator();
		while (i.hasNext())
		{
			Entry<String, Object> e = i.next();
			String key = e.getKey();
			Object value = e.getValue();
			if (value == null)
			{
				if (!(b.get(key) == null && b.containsKey(key)))
					throw new AttributeComparisonException(na, nb, key, null, b.get(key));
			}
			else
			{
				try
				{
					compareValues(value, b.get(key));
				}
				catch (ComparisonException ce)
				{
					throw new AttributeComparisonException(ce, na, nb, key, value, b.get(key));
				}
			}
		}
		
		return;
	}
}
