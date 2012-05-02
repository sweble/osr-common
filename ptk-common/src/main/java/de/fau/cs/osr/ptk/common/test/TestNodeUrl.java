package de.fau.cs.osr.ptk.common.test;

import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.LeafNode;

public class TestNodeUrl
		extends
			LeafNode

{
	private static final long serialVersionUID = 1L;
	
	// =========================================================================
	
	public TestNodeUrl()
	{
		super();
		
	}
	
	public TestNodeUrl(String protocol, String path)
	{
		super();
		setProtocol(protocol);
		setPath(path);
		
	}
	
	@Override
	public int getNodeType()
	{
		return TestNodes.NT_URL;
	}
	
	// =========================================================================
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
						return TestNodeUrl.this.getProtocol();
					case 1:
						return TestNodeUrl.this.getPath();
						
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
						return TestNodeUrl.this.setProtocol((String) value);
					case 1:
						return TestNodeUrl.this.setPath((String) value);
						
					default:
						throw new IndexOutOfBoundsException();
				}
			}
		};
	}
}
