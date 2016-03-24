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

package de.fau.cs.osr.ptk.common;

public abstract class ParserContext
{
	/**
	 * Initializes the root parser context. An implementation of this method has
	 * to initialize <b>all</b> fields of the context and must not rely on
	 * default initializations since context class instances are reused.
	 */
	public abstract void clear();

	/**
	 * Initializes any non-root parser context. An implementation of this method
	 * has to initialize <b>all</b> fields of the context and must not rely on
	 * default initializations since context class instances are reused.
	 */
	public void init(ParserContext parent)
	{
		clear();
	}
}
