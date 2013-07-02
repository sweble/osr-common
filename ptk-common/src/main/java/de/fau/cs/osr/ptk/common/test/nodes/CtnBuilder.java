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

package de.fau.cs.osr.ptk.common.test.nodes;

public class CtnBuilder
{
	public static CtnText astText()
	{
		return new CtnText("Default text");
	}
	
	public static CtnText astText(String text)
	{
		return new CtnText(text);
	}
	
	public static CtnNodeList astList()
	{
		return new CtnNodeList();
	}
	
	public static CtnNodeList astList(CtnNode... children)
	{
		return new CtnNodeList(children);
	}
	
	public static SectionBuilder astSection()
	{
		return new SectionBuilder();
	}
	
	public static CtnTitle astTitle(CtnNode... children)
	{
		return new CtnTitle.CtnTitleImpl(children);
	}
	
	public static CtnBody astBody(CtnNode... children)
	{
		return new CtnBody.CtnBodyImpl(children);
	}
	
	public static CtnDocument astDoc(CtnNode... children)
	{
		return new CtnDocument(children);
	}
	
	public static CtnIdNode astId(int i)
	{
		return new CtnIdNode(i);
	}
	
	public static UrlBuilder astUrl()
	{
		return new UrlBuilder();
	}
	
	public static CtnNodeWithObjProp astObjProp(Object prop)
	{
		return new CtnNodeWithObjProp(prop);
	}
	
	public static CtnNodeWithPropAndContent astPropContent(
			Object prop,
			String content)
	{
		return new CtnNodeWithPropAndContent(prop, content);
	}
	
	// =========================================================================
	
	public static final class SectionBuilder
	{
		private int level = 0;
		
		private CtnTitle title = new CtnTitle.CtnTitleImpl(astText("Default section title"));
		
		private CtnBody body = new CtnBody.CtnBodyImpl(astText("Default section body"));
		
		public SectionBuilder withLevel(int level)
		{
			this.level = level;
			return this;
		}
		
		public SectionBuilder withTitle(CtnTitle title)
		{
			this.title = title;
			return this;
		}
		
		public SectionBuilder withTitle(CtnNode... children)
		{
			this.title = new CtnTitle.CtnTitleImpl(children);
			return this;
		}
		
		public SectionBuilder withBody(CtnBody body)
		{
			this.body = body;
			return this;
		}
		
		public SectionBuilder withBody(CtnNode... children)
		{
			this.body = new CtnBody.CtnBodyImpl(children);
			return this;
		}
		
		public CtnSection build()
		{
			return new CtnSection(level, title, body);
		}
	}
	
	public static final class UrlBuilder
	{
		private String protocol = "http";
		
		private String path = "example.org";
		
		public UrlBuilder withProtocol(String protocol)
		{
			this.protocol = protocol;
			return this;
		}
		
		public UrlBuilder withPath(String path)
		{
			this.path = path;
			return this;
		}
		
		public CtnUrl build()
		{
			return new CtnUrl(protocol, path);
		}
	}
}
