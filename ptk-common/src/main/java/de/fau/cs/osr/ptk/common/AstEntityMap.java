package de.fau.cs.osr.ptk.common;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.fau.cs.osr.ptk.common.ast.AstNode;

public interface AstEntityMap<T extends AstNode<T>>
{
	
	public abstract int registerEntity(T entity);
	
	public abstract T getEntity(int id);
	
	public abstract Set<Entry<Integer, T>> getEntities();
	
	public abstract Map<Integer, T> getMap();
	
	public abstract boolean isEmpty();
	
}
