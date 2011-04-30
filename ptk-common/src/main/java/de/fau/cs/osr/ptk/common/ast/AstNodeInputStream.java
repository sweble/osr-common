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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import de.fau.cs.osr.utils.Utils;

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
public class AstNodeInputStream
        extends
            DataInputStream
{
	private final ArrayList<String> cache = new ArrayList<String>(4096);
	
	private final ObjectInputStream objectInputStream;
	
	// =========================================================================
	
	public AstNodeInputStream(InputStream is) throws IOException
	{
		super(is);
		
		objectInputStream = new ObjectInputStream(is);
	}
	
	// =========================================================================
	
	public AstNode readNode() throws IOException, ClassNotFoundException
	{
		return checkObjId(AstNodeOutputStream.ASTNODE_ID) ?
		        readNodeObj() : null;
	}
	
	public Location readLocation() throws IOException
	{
		return checkObjId(AstNodeOutputStream.LOCATION_ID) ?
		        readLocationObj() : null;
	}
	
	public String readString() throws IOException
	{
		int objId = readInt();
		if (objId == AstNodeOutputStream.NULL_ID)
			return null;
		
		int id = objId & ~AstNodeOutputStream.SWITCH_MASK;
		if (id == AstNodeOutputStream.STRING_ID)
			return readStringObj(objId);
		
		throw new FormatException("Wrong object ID " + id +
		        ", expected ID " + AstNodeOutputStream.STRING_ID);
	}
	
	public Object readObject() throws IOException, ClassNotFoundException
	{
		int objId = readInt();
		switch (objId & ~AstNodeOutputStream.SWITCH_MASK)
		{
			case AstNodeOutputStream.NULL_ID:
				return null;
				
			case AstNodeOutputStream.OBJECT_ID:
				return readObj();
				
			case AstNodeOutputStream.STRING_ID:
				return readStringObj(objId);
				
			case AstNodeOutputStream.BYTE_ID:
				return new Byte(readByte());
				
			case AstNodeOutputStream.SHORT_ID:
				return new Short(readShort());
				
			case AstNodeOutputStream.INTEGER_ID:
				return new Integer(readInt());
				
			case AstNodeOutputStream.LONG_ID:
				return new Long(readLong());
				
			case AstNodeOutputStream.FLOAT_ID:
				return new Float(readFloat());
				
			case AstNodeOutputStream.DOUBLE_ID:
				return new Double(readDouble());
				
			case AstNodeOutputStream.ASTNODE_ID:
				return readNodeObj();
				
			case AstNodeOutputStream.LOCATION_ID:
				return readLocationObj();
				
			default:
				throw new FormatException("Unknown object ID");
		}
	}
	
	// =========================================================================
	
	private boolean checkObjId(int expectedObjId) throws IOException
	{
		int objId = readInt();
		if (objId == AstNodeOutputStream.NULL_ID)
			return false;
		
		objId &= ~AstNodeOutputStream.SWITCH_MASK;
		if (objId == expectedObjId)
			return true;
		
		throw new FormatException("Wrong object ID " + objId +
		        ", expected ID " + expectedObjId);
	}
	
	private Location readLocationObj() throws IOException
	{
		String file = readString();
		int line = readInt();
		int column = readInt();
		return new Location(file, line, column);
	}
	
	private AstNode readNodeObj() throws IOException, ClassNotFoundException
	{
		String className = readClassName();
		Class<?> clazz = Class.forName(className);
		AstNode node = (AstNode) Utils.getInstance(clazz);
		node.deserializeFrom(this);
		return node;
	}
	
	private Object readObj() throws IOException, ClassNotFoundException
	{
		Object o = objectInputStream.readObject();
		return o;
	}
	
	private String readStringObj(int objId) throws IOException
	{
		return readCached(objId);
	}
	
	private String readClassName() throws IOException
	{
		int objId = readInt();
		return readCached(objId);
	}
	
	private String readCached(int objId) throws IOException, FormatException
	{
		if ((objId & AstNodeOutputStream.CACHED_BIT) != 0)
		{
			int strId = readInt();
			if (strId < 0 || strId >= cache.size())
				throw new FormatException("String ID out of bounds: " + strId);
			return cache.get(strId);
		}
		else
		{
			String str = readUTF();
			cache.add(str);
			return str;
		}
	}
	
	// =========================================================================
	
	public class FormatException
	        extends
	            IOException
	{
		private static final long serialVersionUID = 1L;
		
		public FormatException()
		{
			super();
		}
		
		public FormatException(String message, Throwable cause)
		{
			super(message, cause);
		}
		
		public FormatException(String message)
		{
			super(message);
		}
		
		public FormatException(Throwable cause)
		{
			super(cause);
		}
	}
}
