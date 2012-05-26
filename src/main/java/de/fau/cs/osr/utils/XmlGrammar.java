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
	                "|[\uD800\\uDC00-\uDB7F\uDFFF]" + // [#x10000-#xEFFFF]
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

	// =========================================================================

	private static Pattern xmlName = null;

	private static Pattern xmlReference = null;

	// =========================================================================

	public static Pattern xmlName()
	{
		if (xmlName == null)
			xmlName = Pattern.compile(RE_XML_NAME);
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
			xmlReference = Pattern.compile(RE_XML_REFERENCE);
		return xmlReference;
	}
}
