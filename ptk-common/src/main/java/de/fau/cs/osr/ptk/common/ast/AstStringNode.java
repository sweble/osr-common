package de.fau.cs.osr.ptk.common.ast;

public interface AstStringNode<T extends AstNode<T>>
		extends
			AstLeafNode<T>
{
	public abstract String getContent();
	
	public abstract String setContent(String content);
}
