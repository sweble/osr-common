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

package de.fau.cs.osr.utils.visitor;

public class MultipleVisitMethodsMatchException
		extends
			RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	private final Class<?> vClass;
	
	private final Class<?> nClass;
	
	private final Class<?> arg0;
	
	private final Class<?> arg1;
	
	public MultipleVisitMethodsMatchException(
			Class<?> vClass,
			Class<?> nClass,
			Class<?> arg0,
			Class<?> arg1)
	{
		this.vClass = vClass;
		this.nClass = nClass;
		this.arg0 = arg0;
		this.arg1 = arg1;
	}
	
	public Class<?> getArg0()
	{
		return arg0;
	}
	
	public Class<?> getArg1()
	{
		return arg1;
	}
	
	@Override
	public String toString()
	{
		return String.format("" +
				"vClass: %s\n" +
				"nClass: %s\n" +
				"Candidate 1: visit(%s)\n" +
				"Candidate 2: visit(%s)\n",
				vClass.getName(),
				nClass.getName(),
				arg0.getName(),
				arg1.getName());
	}
}
