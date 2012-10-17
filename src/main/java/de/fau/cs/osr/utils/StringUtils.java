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

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public final class StringUtils
{
	public static String escHtml(String text)
	{
		return escHtml(text, true);
	}
	
	public static String escHtml(String text, boolean forAttribute)
	{
		// StringEscapeUtils.escapeHtml(in) does not escape '\'' but a lot of 
		// other stuff that doesn't need escaping.
		
		if (text == null)
			return "";
		
		int n = text.length();
		StringBuilder sb = new StringBuilder(n * 4 / 3);
		for (int i = 0; i < n; i++)
		{
			char ch = text.charAt(i);
			switch (ch)
			{
				case ' ':
				case '\n':
				case '\t':
					sb.append(ch);
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					if (!forAttribute)
						break;
					sb.append(forAttribute ? "&gt;" : ">");
					break;
				case '&':
					sb.append("&amp;");
					break;
				case '\'':
					// &apos; cannot safely be used, see wikipedia
					sb.append("&#39;");
					break;
				case '"':
					if (!forAttribute)
						break;
					sb.append(forAttribute ? "&quot;" : "\"");
					break;
				default:
					if ((ch >= 0 && ch < 0x20) || (ch == 0xFE))
					{
						hexCharRef(sb, ch);
						break;
					}
					else if (Character.isHighSurrogate(ch))
					{
						++i;
						if (i < n)
						{
							char ch2 = text.charAt(i);
							if (Character.isLowSurrogate(ch2))
							{
								int codePoint = Character.toCodePoint(ch, ch2);
								switch (Character.getType(codePoint))
								{
									case Character.CONTROL:
									case Character.PRIVATE_USE:
									case Character.UNASSIGNED:
										hexCharRef(sb, codePoint);
										break;
									
									default:
										sb.append(ch);
										sb.append(ch2);
										break;
								}
								
								continue;
							}
						}
					}
					else if (!Character.isLowSurrogate(ch))
					{
						sb.append(ch);
						continue;
					}
					
					// No low surrogate followed or only low surrogate
					throw new IllegalArgumentException("String contains isolated surrogates!");
			}
		}
		
		return sb.toString();
	}
	
	// =========================================================================
	
	public static String escJava(String text)
	{
		return StringEscapeUtils.escapeJava(text);
	}
	
	// =========================================================================
	
	public static String hexCharRef(int codePoint)
	{
		return String.format("&#x%X;", codePoint);
	}
	
	public static void hexCharRef(StringBuilder sb, int codePoint)
	{
		sb.append("&#x");
		sb.append(Integer.toHexString(codePoint));
		sb.append(';');
	}
	
	public static String entityRef(String name)
	{
		return String.format("&%s;", name);
	}
	
	public static void entityRef(StringBuilder sb, String name)
	{
		sb.append('&');
		sb.append(name);
		sb.append(';');
	}
	
	// =========================================================================
	
	public static String join(Collection<?> c)
	{
		StringBuilder b = new StringBuilder();
		for (Object o : c)
			b.append(o.toString());
		return b.toString();
	}
	
	// =========================================================================
	
	public static String strrep(char c, int times)
	{
		return org.apache.commons.lang.StringUtils.repeat(
				Character.toString(c), times);
	}
	
	public static String strrep(String str, int times)
	{
		return org.apache.commons.lang.StringUtils.repeat(str, times);
	}
	
	// =========================================================================
	
	public static String crop(String str, int length)
	{
		return org.apache.commons.lang.StringUtils.abbreviate(str, length);
	}
	
	// =========================================================================
	
	/**
	 * Indents a text block using the given indent string.
	 * 
	 * @param text
	 *            The text to indent, may be null.
	 * @param indent
	 *            The string to put before the start of each line.
	 * @return The indented text.
	 */
	public static String indent(String text, String indent)
	{
		if (text == null)
			return "";
		
		int n = text.length();
		StringBuilder result = new StringBuilder(n * 2);
		result.append(indent);
		for (int i = 0; i < n; ++i)
		{
			char ch = text.charAt(i);
			result.append(ch);
			switch (ch)
			{
				case '\n':
					result.append(indent);
					break;
				
				case '\r':
					if (i + 1 < n && text.charAt(i + 1) == '\n')
					{
						result.append('\n');
						++i;
					}
					result.append(indent);
					break;
			}
		}
		
		return result.toString();
	}
	
	/**
	 * Indents all but the first line using the given indent string.
	 * 
	 * @param text
	 *            The text to indent, may be null.
	 * @param indent
	 *            The string to put before the start of each but the first line.
	 * @return The indented text.
	 */
	public static String indent2(String text, String indent)
	{
		if (text == null)
			return "";
		
		int n = text.length();
		StringBuilder result = new StringBuilder(n * 2);
		for (int i = 0; i < n; ++i)
		{
			char ch = text.charAt(i);
			result.append(ch);
			switch (ch)
			{
				case '\n':
					result.append(indent);
					break;
				
				case '\r':
					if (i + 1 < n && text.charAt(i + 1) == '\n')
					{
						result.append('\n');
						++i;
					}
					result.append(indent);
					break;
			}
		}
		
		return result.toString();
	}
	
	// =========================================================================
	
	/**
	 * Converts a name that's given in camel-case into upper-case, inserting
	 * underscores before upper-case letters.
	 * 
	 * @param camelCase
	 *            Name in camel-case notation.
	 * @return Name in upper-case notation.
	 */
	public static String camelcaseToUppercase(String camelCase)
	{
		int n = camelCase.length();
		StringBuilder upperCase = new StringBuilder(n * 4 / 3);
		for (int i = 0; i < n; ++i)
		{
			char ch = camelCase.charAt(i);
			if (Character.isUpperCase(ch))
			{
				upperCase.append('_');
				upperCase.append(ch);
			}
			else
			{
				upperCase.append(Character.toUpperCase(ch));
			}
		}
		
		return upperCase.toString();
	}
	
	public static String startWithUppercase(String name)
	{
		if (name == null)
			throw new NullPointerException();
		if (name.isEmpty())
			return name;
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	// =========================================================================
	
	public static boolean hasIsolatedSurrogates(String text)
	{
		final int length = text.length();
		for (int i = 0; i < length; ++i)
		{
			char ch = text.charAt(i);
			if (Character.isHighSurrogate(ch))
			{
				++i;
				if (i < length)
				{
					char ch2 = text.charAt(i);
					if (!Character.isLowSurrogate(ch2))
						return true;
				}
				else
					return true;
			}
			else if (Character.isLowSurrogate(ch))
				return true;
		}
		return false;
	}
	
	// =========================================================================
	
	public static boolean hasParagraphSeparators(String text)
	{
		final int length = text.length();
		for (int i = 0; i < length; ++i)
		{
			char ch = text.charAt(i);
			if (ch == '\n')
			{
				outer: for (++i; i < length; ++i)
				{
					char ch2 = text.charAt(i);
					switch (ch2)
					{
						case ' ':
						case '\t':
							break;
						case '\n':
							return true;
						default:
							break outer;
					}
				}
			}
		}
		return false;
	}
	
	// =========================================================================
	
	public static String trim(String text)
	{
		int from = 0;
		int length = text.length();
		
		while ((from < length) && (Character.isWhitespace(text.charAt(from))))
			++from;
		
		while ((from < length) && (Character.isWhitespace(text.charAt(length - 1))))
			--length;
		
		if (from > 0 || length < text.length())
		{
			return text.substring(from, length);
		}
		else
		{
			return text;
		}
	}
	
	public static String trimUnderscores(String text)
	{
		int from = 0;
		int length = text.length();
		while ((from < length) && (text.charAt(from) == '_'))
			++from;
		
		while ((from < length) && (text.charAt(length - 1) == '_'))
			--length;
		
		if (from > 0 || length < text.length())
		{
			return text.substring(from, length);
		}
		else
		{
			return text;
		}
	}
	
	public static String trimLeft(String text)
	{
		int from = 0;
		int length = text.length();
		
		while ((from < length) && (Character.isWhitespace(text.charAt(from))))
			++from;
		
		if (from > 0)
		{
			return text.substring(from, length);
		}
		else
		{
			return text;
		}
	}
	
	public static String trimRight(String text)
	{
		int length = text.length();
		
		while ((0 < length) && (Character.isWhitespace(text.charAt(length - 1))))
			--length;
		
		if (length < text.length())
		{
			return text.substring(0, length);
		}
		else
		{
			return text;
		}
	}
	
	// =========================================================================
	
	public static boolean isWhitespace(String text)
	{
		if (text == null)
			throw new NullPointerException();
		
		return org.apache.commons.lang.StringUtils.isWhitespace(text);
	}
	
	/**
	 * Collapses a sequence of multiple whitespace characters into a single
	 * space. Does NOT trim the string.
	 */
	public static String collapseWhitespace(String trim)
	{
		if (trim.isEmpty())
			return trim;
		
		StringBuilder b = new StringBuilder(trim.length());
		for (int i = 0; i < trim.length(); ++i)
		{
			char ch = trim.charAt(i);
			if (Character.isWhitespace(ch))
			{
				b.append(' ');
				int j = i + 1;
				while (j < trim.length() && Character.isWhitespace(trim.charAt(j)))
					++j;
				i = j - 1;
			}
			else
				b.append(ch);
		}
		return b.toString();
	}
	
	// =========================================================================
	
	public static String urlDecode(String text)
	{
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < text.length(); ++i)
		{
			char ch = text.charAt(i);
			if (ch == '%' && i + 2 < text.length())
			{
				String num = text.substring(i + 1, i + 3);
				try
				{
					int val = Integer.valueOf(num, 16);
					if (val >= 0x20 && val < 0x7F)
					{
						ch = (char) val;
						i += 2;
					}
				}
				catch (NumberFormatException e)
				{
				}
			}
			else if (ch == '+')
			{
				ch = ' ';
			}
			
			b.append(ch);
		}
		return b.toString();
	}
	
	public static String xmlDecode(String text, XmlEntityResolver resolver)
	{
		Pattern rx = XmlGrammar.xmlReference();
		
		int start = 0;
		StringBuilder b = new StringBuilder();
		while (true)
		{
			Matcher m = rx.matcher(text);
			if (m.find(start))
			{
				b.append(text.substring(start, m.start()));
				
				String resolved = null;
				if (m.group(1) != null)
				{
					resolved = resolver.resolveXmlEntity(m.group(1));
				}
				else
				{
					try
					{
						boolean decimal = m.group(2) != null;
						
						String num = decimal ? m.group(2) : m.group(3);
						
						int val = Integer.valueOf(num, decimal ? 10 : 16);
						if (val >= 0x20 && val != 0x7F)
							resolved = String.valueOf((char) val);
					}
					catch (NumberFormatException e)
					{
					}
				}
				
				if (resolved != null)
					b.append(resolved);
				else
					b.append(text.substring(m.start(), m.end()));
				
				start = m.end();
			}
			else
			{
				if (start < text.length())
					b.append(text.substring(start));
				
				break;
			}
		}
		return b.toString();
	}
}
