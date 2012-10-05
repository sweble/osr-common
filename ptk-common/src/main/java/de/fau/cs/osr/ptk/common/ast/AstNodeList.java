package de.fau.cs.osr.ptk.common.ast;

public interface AstNodeList<T extends AstNode<T>>
		extends
			AstInnerNode<T>
{
	public abstract void exchange(AstNodeList<T> other);
}
