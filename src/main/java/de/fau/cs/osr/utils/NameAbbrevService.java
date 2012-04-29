package de.fau.cs.osr.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

/**
 * If one has a known subset of classes and therefore a known set of class
 * names, one can abbreviate or even leave out the package name without
 * introducing ambiguities.
 * 
 * Given a set of package names, this class can abbreviate names of classes from
 * one of those packages and also retrieve the proper Class<?> object for
 * abbreviated names.
 */
public class NameAbbrevService
{
	private final List<String> packages = new ArrayList<String>();
	
	private final boolean strict;
	
	/** Maps Class<?> (Key) -> String (Value) */
	private BidiMap cache;
	
	// =========================================================================
	
	/**
	 * The order of packages is vitally important to the process. If resolve()
	 * is called with a different order of package names than abbrev(), some
	 * abbreviated might get resolved to the wrong Class<?>!
	 */
	public NameAbbrevService(String... packageNames)
	{
		this(true, packageNames);
	}
	
	/**
	 * The order of packages is vitally important to the process. If resolve()
	 * is called with a different order of package names than abbrev(), some
	 * abbreviated might get resolved to the wrong Class<?>!
	 */
	public NameAbbrevService(boolean strict, String... packageNames)
	{
		this.strict = strict;
		
		packages.add("java.lang");
		packages.addAll(Arrays.asList(packageNames));
		
		cache = new DualHashBidiMap();
		cache.put(byte.class, "byte");
		cache.put(short.class, "short");
		cache.put(int.class, "int");
		cache.put(long.class, "long");
		cache.put(float.class, "float");
		cache.put(double.class, "double");
		cache.put(boolean.class, "boolean");
		cache.put(char.class, "char");
	}
	
	// =========================================================================
	
	/**
	 * Return the abbreviated variant of the given class' full name.
	 * 
	 * @throws IllegalArgumentException
	 *             Thrown if the given class is not part of a packge from the
	 *             package list.
	 */
	public String abbrev(Class<?> clazz)
	{
		String shortName = (String) cache.get(clazz);
		if (shortName != null)
			return shortName;
		
		// clazz.getSimpleName(); doesn't work for nested classes!
		String simpleName = clazz.getName();
		{
			int i = simpleName.lastIndexOf('.');
			if (i >= 0)
				simpleName = simpleName.substring(i + 1);
		}
		
		// Maybe the abbreviated name was already used for another class of the 
		// same name.
		if (cache.containsValue(simpleName))
		{
			// Cannot abbreviate any more :(
			shortName = clazz.getName();
			cache.put(clazz, shortName);
			return shortName;
		}
		
		final String dotSimpleName = "." + simpleName;
		for (String pkg : packages)
		{
			try
			{
				Class<?> otherClazz = Class.forName(pkg + dotSimpleName);
				// At this point a class with this simple name has not been 
				// abbreviated. The first package that contains a class with 
				// this simple name will be the one we abbreviate. All others 
				// have to use the full name.
				cache.put(otherClazz, simpleName);
				
				if (otherClazz != clazz)
				{
					// Cannot abbreviate any more :(
					shortName = clazz.getName();
					cache.put(clazz, shortName);
					return shortName;
				}
				else
				{
					shortName = simpleName;
					return shortName;
				}
			}
			catch (ClassNotFoundException e)
			{
			}
		}
		
		if (!strict)
			return clazz.getName();
		
		throw new IllegalArgumentException("Given class is not part of the package list: " + clazz.getName());
	}
	
	/**
	 * Resolves an abbreviated class name to the corresponding Class<?> object.
	 * 
	 * @throws ClassNotFoundException
	 *             Thrown if the abbreviated class cannot be found in any
	 *             package of the package list.
	 */
	public Class<?> resolve(String abbrev) throws ClassNotFoundException
	{
		Class<?> clazz = (Class<?>) cache.getKey(abbrev);
		if (clazz != null)
			return clazz;
		
		if (abbrev.indexOf('.') >= 0)
		{
			// Full name was given
			clazz = Class.forName(abbrev);
			cache.put(clazz, abbrev);
			return clazz;
		}
		
		final String dotSimpleName = "." + abbrev;
		for (String pkg : packages)
		{
			try
			{
				clazz = Class.forName(pkg + dotSimpleName);
				cache.put(clazz, abbrev);
				return clazz;
			}
			catch (ClassNotFoundException e)
			{
			}
		}
		
		throw new ClassNotFoundException("Given abbreviated class name was "
				+ "not found in any package of the package list");
	}
}
