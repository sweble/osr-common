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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import xtc.parser.ParseException;

public interface ParserInterface<T extends AstNode<T>>
{
	public Object parseArticle(String src, String title) throws IOException, ParseException;

	public List<AstVisitor<T>> getVisitors();

	public ParserInterface<T> addVisitor(AstVisitor<T> v);

	public ParserInterface<T> addVisitors(Collection<? extends AstVisitor<T>> v);
}
