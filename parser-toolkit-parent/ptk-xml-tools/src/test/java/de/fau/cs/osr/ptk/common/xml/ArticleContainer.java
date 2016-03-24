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

package de.fau.cs.osr.ptk.common.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import de.fau.cs.osr.ptk.common.test.nodes.CtnDocument;

@XStreamAlias("article-container")
public class ArticleContainer
{
	@XStreamAsAttribute
	public final String xmlns = XMLNS;

	public static final String XMLNS = "http://sweble.org/projects/parser-toolkit/ptk-xml-tools";

	@XStreamAsAttribute
	@XStreamAlias("xmlns:ptk")
	public final String xmlns_ptk = XMLNS_PTK;

	public static final String XMLNS_PTK = "http://sweble.org/projects/parser-toolkit";

	@XStreamAlias("document")
	protected final CtnDocument doc;

	public ArticleContainer(CtnDocument doc)
	{
		this.doc = doc;
	}
}
