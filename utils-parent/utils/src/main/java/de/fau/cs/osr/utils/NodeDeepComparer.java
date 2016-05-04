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
package de.fau.cs.osr.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NodeDeepComparer
		extends
			DeepComparer
{
	/**
	 * Compare two objects for equality.
	 * 
	 * @param rootA
	 *            First object.
	 * @param rootB
	 *            Second object.
	 * @throws ComparisonException
	 *             Thrown if the two trees differ.
	 */
	public static void compareAndThrow(Object rootA, Object rootB) throws ComparisonException
	{
		new NodeDeepComparer().compare(rootA, rootB);
	}

	/**
	 * @return Returns {@code true} if both objects are equal, {@code false}
	 *         otherwise.
	 */
	public static boolean compareNoThrow(Object rootA, Object rootB)
	{
		try
		{
			new NodeDeepComparer().compare(rootA, rootB);
			return true;
		}
		catch (ComparisonException e)
		{
			System.err.println(e);
			return false;
		}
	}

	// =========================================================================

	public NodeDeepComparer()
	{
		addComparer(new DeepComparerDelegate()
		{
			@Override
			public boolean compare(Object a, Object b, DeepComparer comparer) throws ComparisonException
			{
				return compareNode(a, b, comparer);
			}
		});
	}

	// =========================================================================

	private boolean compareNode(Object a, Object b, DeepComparer comparer) throws ComparisonException
	{
		if (!(a instanceof Node && b instanceof Node))
			return false;

		Node na = (Node) a;
		Node nb = (Node) b;

		// Type
		if (na.getNodeType() != nb.getNodeType())
			throw new NodeComparisonException(na, nb, NodeDifference.NODE_TYPES_DIFFER);

		// Namespace and name
		if (!strEquals(na.getNodeName(), nb.getNodeName()))
			throw new NodeComparisonException(na, nb, NodeDifference.NODE_NAMES_DIFFER);
		if (!strEquals(na.getNamespaceURI(), nb.getNamespaceURI()))
			throw new NodeComparisonException(na, nb, NodeDifference.NODE_NAMESPACE_URIS_DIFFER);
		if (!strEquals(na.getLocalName(), nb.getLocalName()))
			throw new NodeComparisonException(na, nb, NodeDifference.NODE_LOCAL_NAMES_DIFFER);

		// Value
		if (!strEquals(na.getNodeValue(), nb.getNodeValue()))
			throw new NodeComparisonException(na, nb, NodeDifference.NODE_VALUES_DIFFER);

		// Other
		if (!strEquals(na.getBaseURI(), nb.getBaseURI()))
			throw new NodeComparisonException(na, nb, NodeDifference.NODE_BASE_URIS_DIFFER);

		// Ignored:
		//wa.getPrefix()

		// Attributes
		if (na.hasAttributes() ^ nb.hasAttributes())
			throw new NodeComparisonException(na, nb, NodeDifference.NUMBER_OF_ATTRIBUTES_DIFFERS);
		if (na.hasAttributes())
			compareAttributes(na, nb, comparer);

		// Children
		if (na.hasChildNodes() ^ nb.hasChildNodes())
			throw new NodeComparisonException(na, nb, NodeDifference.NUMBER_OF_CHILDREN_DIFFERS);
		if (na.hasChildNodes())
			compareChildNodes(na, nb, comparer);

		return true;
	}

	private void compareAttributes(Node a, Node b, DeepComparer comparer) throws ComparisonException
	{
		NamedNodeMap aas = a.getAttributes();
		NamedNodeMap bas = b.getAttributes();

		for (int i = 0; i < aas.getLength(); ++i)
			compareAttribute(aas.item(i), bas);

		for (int i = 0; i < bas.getLength(); ++i)
			compareAttribute(bas.item(i), aas);
	}

	private void compareAttribute(Node aa, NamedNodeMap bas) throws ComparisonException
	{
		if ("xmlns".equals(aa.getNodeName()) || "xmlns".equals(aa.getPrefix()))
			return;

		Node ba = ((aa.getLocalName() != null) && (aa.getNamespaceURI() != null)) ?
				bas.getNamedItemNS(aa.getNamespaceURI(), aa.getLocalName()) :
				bas.getNamedItem(aa.getNodeName());
		if (ba == null)
			throw new NodeComparisonException(aa, null, NodeDifference.NUMBER_OF_ATTRIBUTES_DIFFERS);
		if (!aa.getNodeValue().equals(ba.getNodeValue()))
			throw new NodeComparisonException(aa, ba, NodeDifference.ATTRIBUTE_VALUE_DIFFERS);
	}

	private void compareChildNodes(Node a, Node b, DeepComparer comparer) throws ComparisonException
	{
		Node ac = a.getFirstChild();
		Node bc = b.getFirstChild();
		while ((ac != null) && (bc != null))
		{
			comparer.compare(ac, bc);
			ac = ac.getNextSibling();
			bc = bc.getNextSibling();
		}
		if (ac != bc)
			// Both must be null at this point
			throw new NodeComparisonException(ac, bc, NodeDifference.NUMBER_OF_CHILDREN_DIFFERS);
	}

	private boolean strEquals(String a, String b)
	{
		return (a == null) ? (b == null) : a.equals(b);
	}

	// =========================================================================

	public static final class NodeComparisonException
			extends
				ComparisonException
	{
		private static final long serialVersionUID = 1L;

		private final NodeDifference reason;

		public NodeComparisonException()
		{
			this(null, null, NodeDifference.DEEP_COMPARISON_FAILED);
		}

		public NodeComparisonException(NodeComparisonException e)
		{
			this(e, null, null, NodeDifference.DEEP_COMPARISON_FAILED);
		}

		public NodeComparisonException(Node a, Node b, NodeDifference reason)
		{
			super(a, b);
			this.reason = reason;
		}

		protected NodeComparisonException(
				NodeComparisonException e,
				Node a,
				Node b)
		{
			this(e, a, b, NodeDifference.CHILDREN_DIFFER);
		}

		protected NodeComparisonException(
				NodeComparisonException e,
				Node a,
				Node b,
				NodeDifference reason)
		{
			super(e, a, b);
			this.reason = reason;
		}

		public Node getA()
		{
			return (Node) super.getA();
		}

		public Node getB()
		{
			return (Node) super.getB();
		}

		protected Writer toString(Writer w) throws IOException
		{
			w.append("Two nodes differ: ");
			w.append(reason.getReason());
			w.append("\n");
			writeNode(w, getA());
			w.append("\n");
			writeNode(w, getB());
			return w;
		}

		private void writeNode(Writer w, Node n) throws IOException
		{
			if (n == null)
			{
				w.append("null");
			}
			else
			{
				w.append(n.getNodeName());
				w.append("[");
				w.append(String.valueOf(n.getNodeType()));
				w.append("]");
				if ((n.getLocalName() != null) || (n.getNamespaceURI() != null))
				{
					w.append(" (");
					if (n.getNamespaceURI() != null)
					{
						w.append("{");
						w.append(n.getNamespaceURI());
						w.append("}");
					}
					if (n.getLocalName() != null)
						w.append(n.getLocalName());
					w.append(")");
				}
				if (n.getNodeValue() != null)
				{
					w.append(" = ");
					w.append(n.getNodeValue());
				}
			}
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

	public static enum NodeDifference
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
		NODE_NAMES_DIFFER
		{
			@Override
			public String getReason()
			{
				return "The two nodes have different node names";
			}
		},
		NODE_NAMESPACE_URIS_DIFFER
		{
			@Override
			public String getReason()
			{
				return "The two nodes have different namespace URIs";
			}
		},
		NODE_LOCAL_NAMES_DIFFER
		{
			@Override
			public String getReason()
			{
				return "The two nodes have different local names";
			}
		},
		NODE_VALUES_DIFFER
		{
			@Override
			public String getReason()
			{
				return "The two nodes have different values";
			}
		},
		NODE_BASE_URIS_DIFFER
		{
			@Override
			public String getReason()
			{
				return "The two nodes have different base URIs";
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
}
