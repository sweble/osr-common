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

package de.fau.cs.osr.ptk.common.ast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Native AST serialization has been deprecated for the following reasons:
 * <ul>
 * <li>While native serialization is faster than java serialization, the output
 * of native serialization is twice as big as the output of java serialization.</li>
 * <li>Native deserialization is much slower than java deserialization.</li>
 * <li>Java serialization is well understood and widely used.</li>
 * </ul>
 * 
 * @deprecated
 */
public class AstNodeOutputStream
        extends
            DataOutputStream
{
	// General Types
	
	public final static int NULL_ID = 0x00;
	
	public final static int OBJECT_ID = 0x01;
	
	// Strings
	
	public final static int STRING_ID = 0x10;
	
	// Primitive Datatypes
	
	public final static int BYTE_ID = 0x20;
	
	public final static int SHORT_ID = 0x21;
	
	public final static int INTEGER_ID = 0x22;
	
	public final static int LONG_ID = 0x23;
	
	public final static int FLOAT_ID = 0x24;
	
	public final static int DOUBLE_ID = 0x25;
	
	// Special Types
	
	public final static int ASTNODE_ID = 0x100;
	
	public final static int LOCATION_ID = 0x101;
	
	// Switches
	
	public final static int SWITCH_MASK = 0xFFFF0000;
	
	public final static int SWITCH_SHIFT = 16;
	
	public final static int CACHED_BIT = 0x01 << SWITCH_SHIFT;
	
	// Count up on breaking change
	public final static int MAJOR_VERSION = 1;
	
	// Count up on non-breaking change
	public final static int MINOR_VERSION = 1;
	
	private final Map<String, Integer> cache = new HashMap<String, Integer>(4096);
	
	private final ObjectOutputStream objectOutputStream;
	
	// =========================================================================
	
	public AstNodeOutputStream(OutputStream os) throws IOException
	{
		super(os);
		
		objectOutputStream = new ObjectOutputStream(os);
	}
	
	// =========================================================================
	
	public void writeByte(Byte o) throws IOException
	{
		writeInt(BYTE_ID);
		writeByte((byte) o);
	}
	
	public void writeShort(Short o) throws IOException
	{
		writeInt(SHORT_ID);
		writeShort((short) o);
	}
	
	public void writeInt(Integer o) throws IOException
	{
		writeInt(INTEGER_ID);
		writeInt((int) o);
	}
	
	public void writeLong(Long o) throws IOException
	{
		writeInt(LONG_ID);
		writeLong((long) o);
	}
	
	public void writeFloat(Float o) throws IOException
	{
		writeInt(FLOAT_ID);
		writeFloat((float) o);
	}
	
	public void writeDouble(Double o) throws IOException
	{
		writeInt(DOUBLE_ID);
		writeDouble((double) o);
	}
	
	public void writeString(String o) throws IOException
	{
		writeCached(o);
	}
	
	public void writeNode(AstNode n) throws IOException
	{
		if (n == null)
		{
			writeNullObj();
		}
		else
		{
			writeInt(ASTNODE_ID);
			writeClassName(n);
			n.serializeTo(this);
		}
	}
	
	private void writeLocation(Location o) throws IOException
	{
		if (o == null)
		{
			writeNullObj();
		}
		else
		{
			writeInt(LOCATION_ID);
			writeString(o.getFile());
			writeInt(o.getLine());
			writeInt(o.getColumn());
		}
	}
	
	public void writeObject(Object o) throws IOException
	{
		if (o == null)
		{
			writeNullObj();
		}
		else if (o instanceof String)
		{
			writeString((String) o);
		}
		else if (o instanceof Number)
		{
			if (o instanceof Integer)
			{
				writeInt((Integer) o);
			}
			else if (o instanceof Float)
			{
				writeFloat((Float) o);
			}
			else if (o instanceof Long)
			{
				writeLong((Long) o);
			}
			else if (o instanceof Byte)
			{
				writeByte((Byte) o);
			}
			else if (o instanceof Short)
			{
				writeShort((Short) o);
			}
			else if (o instanceof Double)
			{
				writeDouble((Double) o);
			}
			else
			{
				// No special serialization code. Do Java serialization.
				writeObj(o);
			}
		}
		else if (o instanceof AstNode)
		{
			writeNode((AstNode) o);
		}
		else if (o instanceof Location)
		{
			writeLocation((Location) o);
		}
		else
		{
			// No special serialization code. Do Java serialization.
			writeObj(o);
		}
	}
	
	// =========================================================================
	
	private void writeNullObj() throws IOException
	{
		writeInt(NULL_ID);
	}
	
	private void writeCached(String str) throws IOException
	{
		if (str == null)
		{
			writeNullObj();
		}
		else
		{
			Integer id = cache.get(str);
			if (id != null)
			{
				writeInt(STRING_ID | CACHED_BIT);
				writeInt((int) id);
			}
			else
			{
				writeInt(STRING_ID);
				cache.put(str, cache.size());
				writeUTF(str);
			}
		}
	}
	
	private void writeClassName(AstNode n) throws IOException
	{
		writeCached(n.getClass().getName());
	}
	
	private void writeObj(Object o) throws IOException
	{
		writeInt(OBJECT_ID);
		objectOutputStream.writeObject(o);
	}
	
	// =========================================================================
	
	public Set<String> getCachedString()
	{
		return cache.keySet();
	}
}
