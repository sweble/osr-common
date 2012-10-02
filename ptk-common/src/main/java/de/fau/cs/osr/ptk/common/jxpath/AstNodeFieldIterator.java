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

package de.fau.cs.osr.ptk.common.jxpath;

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

import de.fau.cs.osr.ptk.common.ast.AstNode;

public class AstNodeFieldIterator
        implements
            NodeIterator
{
	/** A pointer to the node over whose children we are iterating. */
	private NodePointer parent;
	
	/** The actual node over whose children we are iterating. */
	private AstNode<?> node;
	
	/** The node test to apply to children. */
	private NodeTest test;
	
	/** The child we are currently pointing to. */
	private AstNode<?> child = null;
	
	/** Iteration direction. */
	private boolean reverse;
	
	/**
	 * Position in the <b>filtered</b> list of children. Position `0' has the
	 * special meaning, that the iterator has not yet been initialized.
	 */
	private int position = 0;
	
	private int positionInNode = -1;
	
	private int size;
	
	// =========================================================================
	
	/**
	 * @param parent
	 *            The node over whose children this iterator iterates.
	 * @param nodeTest
	 *            The test to apply while iterating over parent's children. Only
	 *            children who pass the test will be assigned an consectuvie
	 *            index and get returned.
	 * @param reverse
	 *            Run through the children in reverse direction.
	 * @param startWith
	 *            A pointer to a child node that will be considered the "base"
	 *            child and will have the index `0'. Children in front of this
	 *            node will have negative indices. <b>NOT YET SUPPORTED!</b>
	 */
	public AstNodeFieldIterator(
	        NodePointer parent,
	        NodeTest nodeTest,
	        boolean reverse,
	        NodePointer startWith)
	{
		this.parent = parent;
		this.node = (AstNode<?>) parent.getImmediateNode();
		this.test = nodeTest;
		this.reverse = reverse;
		this.size = node.size();
		
		if (startWith != null)
			throw new IllegalArgumentException("Not yet supported!");
		
		//debug("AstNodeFieldIterator");
	}
	
	// =========================================================================
	
	@Override
	public NodePointer getNodePointer()
	{
		try
		{
			// Position `0' means that the iterator has not yet been initialized
			// and therefore points to the start item (the first by default).
			if (position == 0)
				setPosition(1);
			
			if (child == null)
			{
				return null;
			}
			else
			{
				int i = getChildIndex();
				if (i < node.getChildNames().length)
				{
					return new AstNodeFieldPointer(parent, node, getChildIndex());
				}
				else
				{
					return new AstNodePointer(parent, child);
				}
			}
		}
		finally
		{
			//debug("getNodePointer", position, child);
		}
	}
	
	@Override
	public int getPosition()
	{
		//debug("setPosition", position);
		
		return position;
	}
	
	@Override
	public boolean setPosition(int position)
	{
		/* Can happen ... they don't filter these out of queries
		if (position <= 0)
			throw new IndexOutOfBoundsException();
		*/

		try
		{
			if (position <= 0)
				return false;
			
			while (this.position < position)
			{
				if (!next())
					return false;
			}
			while (this.position > position)
			{
				if (!previous())
					return false;
			}
			return true;
		}
		finally
		{
			//debug("setPosition", position, this.position, child);
		}
	}
	
	private boolean next()
	{
		if (positionInNode >= size)
		{
			return false;
		}
		else
		{
			++position;
			++positionInNode;
			
			while (!testChild())
				++positionInNode;
			
			return child != null;
		}
	}
	
	private boolean previous()
	{
		if (positionInNode < 0)
		{
			return false;
		}
		else
		{
			--position;
			--positionInNode;
			
			while (!testChild())
				--positionInNode;
			
			return child != null;
		}
	}
	
	// =========================================================================
	
	/**
	 * @return True when there is no node at `positionInNode' or if there is a
	 *         node which passes the test. False otherwise: There is a node, but
	 *         it fails the test. Basically tells you whether to continue the
	 *         search.
	 */
	private boolean testChild()
	{
		child = null;
		
		if (positionInNode < 0 || positionInNode >= size)
		{
			return true;
		}
		else if (test == null)
		{
			child = node.get(getChildIndex());
			return true;
		}
		else
		{
			if (test instanceof NodeNameTest)
			{
				NodeNameTest nodeNameTest = (NodeNameTest) test;
				
				QName testName = nodeNameTest.getNodeName();
				
				// We don't support prefixes
				if (testName.getPrefix() != null)
					return false;
				
				// Wildcards don't match when prefixes differ, so check for 
				// wildcards only after we made sure that we are not looking 
				// for a prefixed name.
				if (nodeNameTest.isWildcard())
				{
					child = node.get(getChildIndex());
					return true;
				}
				
				// Perform actual name check
				return testName(testName.getName());
			}
			else if (test instanceof NodeTypeTest)
			{
				NodeTypeTest nodeTypeTest = (NodeTypeTest) test;
				switch (nodeTypeTest.getNodeType())
				{
					case Compiler.NODE_TYPE_NODE:
						child = node.get(getChildIndex());
						return true;
						
					case Compiler.NODE_TYPE_TEXT:
					case Compiler.NODE_TYPE_COMMENT:
					case Compiler.NODE_TYPE_PI:
					default:
						return false;
				}
			}
			else
			{
				// Unhandled: ProcessingInstructionTest
				//   An AstNodeInterface<?> can never be a PI.
				
				return false;
			}
		}
	}
	
	private int getChildIndex()
	{
		return reverse ? size - 1 - positionInNode : positionInNode;
	}
	
	private boolean testName(String test)
	{
		String[] fixedNames = node.getChildNames();
		
		int i = getChildIndex();
		if (i < fixedNames.length)
		{
			if (namesEqual(fixedNames[i], test))
			{
				child = node.get(i);
				return true;
			}
			else
				return false;
		}
		else
		{
			child = node.get(i);
			if (child != null)
			{
				if (!namesEqual(child.getNodeName(), test))
				{
					child = null;
					return false;
				}
				else
					return true;
			}
			else
			{
				child = null;
				return false;
			}
		}
	}
	
	private static final boolean namesEqual(String s1, String s2)
	{
		return s1 == s2 || s1.equals(s2);
	}
	
	// =========================================================================
	
	/*
	private void debug(String where, Object... params)
	{
		StringBuilder b = new StringBuilder();
		
		String path = parent.asPath();
		b.append(String.format(
		        "@%8x : %s : %s%s.%s ; ",
		        System.identityHashCode(this),
		        path,
		        StringUtils.strrep(' ', 28 - path.length()),
		        getClass().getSimpleName(),
		        where));
		
		int i = 0;
		for (Object o : params)
		{
			if (i++ != 0)
				b.append(", ");
			b.append(o == null ? "null" : o.toString());
		}
		
		System.out.println(b.toString());
	}
	*/
}
