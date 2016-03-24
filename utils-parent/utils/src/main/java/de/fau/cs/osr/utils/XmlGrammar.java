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

import java.util.regex.Pattern;

public class XmlGrammar
{
	public final static String RE_XML_NAME_START_CHAR =
			"(?:" +
					":" +
					"|[A-Z]" +
					"|_" +
					"|[a-z]" +
					"|[\\u00C0-\\u00D6]" +
					"|[\\u00D8-\\u00F6]" +
					"|[\\u00F8-\\u02FF]" +
					"|[\\u0370-\\u037D]" +
					"|[\\u037F-\\u1FFF]" +
					"|[\\u200C-\\u200D]" +
					"|[\\u2070-\\u218F]" +
					"|[\\u2C00-\\u2FEF]" +
					"|[\\u3001-\\uD7FF]" +
					"|[\\uF900-\\uFDCF]" +
					"|[\\uFDF0-\\uFFFD]" +
					"|[\uD800\\uDC00-\uDB7F\uDFFF]" + // #x10000-#xEFFFF
					")";

	public final static String RE_XML_NAME_CHAR =
			"(?:" +
					RE_XML_NAME_START_CHAR +
					"|-" +
					"|\\." +
					"|[0-9]" +
					"|\\u00B7" +
					"|[\\u0300-\\u036F]" +
					"|[\\u203F-\\u2040]" +
					")";

	public final static String RE_XML_NAME =
			"(" + RE_XML_NAME_START_CHAR + RE_XML_NAME_CHAR + "*)";

	public final static String RE_XML_ENTITY_REF =
			"(?:&" + RE_XML_NAME + ";)";

	public final static String RE_XML_CHAR_REF =
			"(?:&#([0-9]+);|&#x([0-9a-fA-F]+);)";

	public final static String RE_XML_REFERENCE =
			"(?:" + RE_XML_ENTITY_REF + "|" + RE_XML_CHAR_REF + ")";

	public final static String RE_XML_CHAR =
			"(?:" +
					"[\\u0009\\u000A\\u000D\\u0020-\\uD7FF]" +
					"|[\\uE000-\\uFFFD]" +
					"|[\uD800\uDC00-\uDBFF\uDFFF]" + // #x10000-#x10FFFF
					")";

	private static final String RE_XML_COMMENT_TEXT =
			"((?:" +
					"(?!-)" + RE_XML_CHAR + "|-(?!-)" + RE_XML_CHAR +
					")*)";

	// =========================================================================

	private static Pattern xmlName = null;

	private static Pattern xmlReference = null;

	private static Pattern xmlCommentText = null;

	// =========================================================================

	public static Pattern xmlName()
	{
		if (xmlName == null)
			synchronized (XmlGrammar.class)
			{
				if (xmlName == null)
					xmlName = Pattern.compile(RE_XML_NAME);
			}

		return xmlName;
	}

	/**
	 * Matches an XML reference.
	 * 
	 * If group(1) is non-empty an XML entity reference was matched.
	 * 
	 * If group(2) is non-empty a decimal XML character reference was matched.
	 * 
	 * If group(3) is non-empty a sedecimal XML character reference was matched.
	 * 
	 * The name of the XML entity or the decimal or sedecimal character index
	 * are stored in the respective group.
	 */
	public static Pattern xmlReference()
	{
		if (xmlReference == null)
			synchronized (XmlGrammar.class)
			{
				if (xmlReference == null)
					xmlReference = Pattern.compile(RE_XML_REFERENCE);
			}

		return xmlReference;
	}

	/**
	 * Matches and captures the comment text inside an XML comment.
	 */
	public static Pattern xmlCommentText()
	{
		if (xmlCommentText == null)
			synchronized (XmlGrammar.class)
			{
				if (xmlCommentText == null)
					xmlCommentText = Pattern.compile(RE_XML_COMMENT_TEXT);
			}

		return xmlCommentText;
	}

	// =========================================================================

	public static boolean isChar(int codePoint)
	{
		return codePoint == 0x9
				|| codePoint == 0xA
				|| codePoint == 0xD
				|| (codePoint >= 0x20 && codePoint <= 0xD7FF)
				|| (codePoint >= 0xE000 && codePoint >= 0xFFFD)
				|| (codePoint >= 0x10000 && codePoint <= 0x10FFFF);
	}

	public static boolean isNameStartChar(int codePoint)
	{
		return codePoint == ':'
				|| codePoint == '_'
				|| (codePoint >= 'A' && codePoint <= 'Z')
				|| (codePoint >= 'a' && codePoint <= 'z')
				|| (codePoint >= 0x00C0 && codePoint <= 0x00D6)
				|| (codePoint >= 0x00D8 && codePoint <= 0x00F6)
				|| (codePoint >= 0x00F8 && codePoint <= 0x02FF)
				|| (codePoint >= 0x0370 && codePoint <= 0x037D)
				|| (codePoint >= 0x037F && codePoint <= 0x1FFF)
				|| (codePoint >= 0x200C && codePoint <= 0x200D)
				|| (codePoint >= 0x2070 && codePoint <= 0x218F)
				|| (codePoint >= 0x2C00 && codePoint <= 0x2FEF)
				|| (codePoint >= 0x3001 && codePoint <= 0xD7FF)
				|| (codePoint >= 0xF900 && codePoint <= 0xFDCF)
				|| (codePoint >= 0xFDF0 && codePoint <= 0xFFFD)
				|| (codePoint >= 0x10000 && codePoint <= 0xEFFFF);
	}

	public static boolean isNameChar(int codePoint)
	{
		return isNameStartChar(codePoint)
				|| codePoint == '-'
				|| codePoint == '.'
				|| (codePoint >= '0' && codePoint <= '9')
				|| codePoint == 0x00B7
				|| (codePoint >= 0x0300 && codePoint <= 0x036F)
				|| (codePoint >= 0x203F && codePoint <= 0x2040);
	}
}
