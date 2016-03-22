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

package de.fau.cs.osr.ptk.common.comparer;

public enum AstDifference
{
	NULL_VS_NON_NULL
	{
		@Override
		public String getReason()
		{
			return "One node is null the other is non-null";
		}
	},
	NODE_TYPES_DIFFER
	{
		@Override
		public String getReason()
		{
			return "The two nodes have different type";
		}
	},
	LOCATION_DIFFERS
	{
		@Override
		public String getReason()
		{
			return "The two nodes have different locations";
		}
	},
	NUMBER_OF_CHILDREN_DIFFERS
	{
		@Override
		public String getReason()
		{
			return "The two nodes' number of children differs";
		}
	},
	PROPERTY_VALUE_DIFFERS
	{
		@Override
		public String getReason()
		{
			return "The value of a property differs between the two nodes";
		}
	},
	CHILDREN_DIFFER
	{
		@Override
		public String getReason()
		{
			return "One of the child nodes differs between the two nodes";
		}
	},
	NUMBER_OF_ATTRIBUTES_DIFFERS
	{
		@Override
		public String getReason()
		{
			return "The two nodes' number of attributes differs";
		}
	},
	ATTRIBUTE_VALUE_DIFFERS
	{
		@Override
		public String getReason()
		{
			return "The value of an attribute differs between the two nodes";
		}
	},
	DEEP_COMPARISON_FAILED
	{
		@Override
		public String getReason()
		{
			return "Deep comparison of two values failed";
		}
	};
	
	public abstract String getReason();
}
