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

		Node wa = (Node) a;
		Node wb = (Node) b;

		// Type
		if (wa.getNodeType() != wb.getNodeType())
			throw new ComparisonException(a, b);

		// Namespace and name
		if (!strEquals(wa.getNodeName(), wb.getNodeName()))
			throw new ComparisonException(a, b);
		if (!strEquals(wa.getNamespaceURI(), wb.getNamespaceURI()))
			throw new ComparisonException(a, b);
		if (!strEquals(wa.getLocalName(), wb.getLocalName()))
			throw new ComparisonException(a, b);

		// Value
		if (!strEquals(wa.getNodeValue(), wb.getNodeValue()))
			throw new ComparisonException(a, b);

		// Other
		if (!strEquals(wa.getBaseURI(), wb.getBaseURI()))
			throw new ComparisonException(a, b);

		// Ignored:
		//wa.getPrefix()

		// Attributes
		if (wa.hasAttributes() ^ wb.hasAttributes())
			throw new ComparisonException(a, b);
		if (wa.hasAttributes())
			compareAttributes(wa, wb, comparer);

		// Children
		if (wa.hasChildNodes() ^ wb.hasChildNodes())
			throw new ComparisonException(a, b);
		if (wa.hasChildNodes())
			compareChildNodes(wa, wb, comparer);

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
			throw new ComparisonException();
		if (!aa.getNodeValue().equals(ba.getNodeValue()))
			throw new ComparisonException();
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
			throw new ComparisonException();
	}

	private boolean strEquals(String a, String b)
	{
		return (a == null) ? (b == null) : a.equals(b);
	}
}
