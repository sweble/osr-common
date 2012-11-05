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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class PrinterBase
{
	public PrinterBase(Writer writer)
	{
		this.out = new PrintWriter(writer);
	}
	
	// =========================================================================
	
	private final Stack<State> stateStack = new Stack<State>();
	
	private static final class State
	{
		public PrintWriter out;
		
		public int indent;
		
		public int hadNewlines;
		
		public int needNewlines;
		
		public int eatNewlines;
		
		public State(
				PrintWriter out,
				int indent,
				int hadNewlines,
				int needNewlines,
				int eatNewlines)
		{
			this.out = out;
			this.indent = indent;
			this.hadNewlines = hadNewlines;
			this.needNewlines = needNewlines;
			this.eatNewlines = eatNewlines;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + eatNewlines;
			result = prime * result + hadNewlines;
			result = prime * result + indent;
			result = prime * result + needNewlines;
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			if (eatNewlines != other.eatNewlines)
				return false;
			if (hadNewlines != other.hadNewlines)
				return false;
			if (indent != other.indent)
				return false;
			if (needNewlines != other.needNewlines)
				return false;
			return true;
		}
	}
	
	public State push()
	{
		State state = getState();
		stateStack.push(state);
		return state;
	}
	
	public State pop()
	{
		return stateStack.pop();
	}
	
	public State restore()
	{
		State state = stateStack.pop();
		setState(state);
		return state;
	}
	
	private State getState()
	{
		return new State(out, indent, hadNewlines, needNewlines, eatNewlines);
	}
	
	public void setState(State state)
	{
		this.out = state.out;
		this.indent = state.indent;
		this.hadNewlines = state.hadNewlines;
		this.needNewlines = state.needNewlines;
		this.eatNewlines = state.eatNewlines;
	}
	
	public void setStateNotOut(State state)
	{
		this.indent = state.indent;
		this.hadNewlines = state.hadNewlines;
		this.needNewlines = state.needNewlines;
		this.eatNewlines = state.eatNewlines;
	}
	
	// =========================================================================
	
	public final class OutputBuffer
	{
		private StringWriter w = new StringWriter();
		
		private State stateOnStart;
		
		private State stateOnStop;
		
		public OutputBuffer()
		{
			stateOnStart = push();
			out = new PrintWriter(w);
		}
		
		public void stop()
		{
			if (isStopped())
				throw new UnsupportedOperationException("Already stopped!");
			stateOnStop = getState();
			restore();
		}
		
		public boolean isStopped()
		{
			return stateOnStop != null;
		}
		
		public String getBuffer()
		{
			return w.toString();
		}
		
		public void flush()
		{
			if (!isStopped())
				stop();
			setStateNotOut(stateOnStop);
			out.append(getBuffer());
		}
		
		public State getStateOnStart()
		{
			return stateOnStart;
		}
		
		public State getStateOnStop()
		{
			return stateOnStop;
		}
	}
	
	public OutputBuffer outputBufferStart()
	{
		return new OutputBuffer();
	}
	
	// =========================================================================
	
	private final ArrayList<String> indentStrings =
			new ArrayList<String>(Arrays.asList(""));
	
	private PrintWriter out;
	
	private String indentString = "\t";
	
	private int indent = 0;
	
	private int hadNewlines = 1;
	
	private int needNewlines = 0;
	
	private int eatNewlines = 0;
	
	// =========================================================================
	
	public void incIndent()
	{
		++indent;
		while (indentStrings.size() <= indent)
			indentStrings.add(indentStrings.get(indentStrings.size() - 1) + indentString);
	}
	
	public void decIndent()
	{
		assert indent > 0;
		--indent;
	}
	
	public void indent()
	{
		if (eatNewlines > 0)
		{
			--eatNewlines;
		}
		else
		{
			needNewlines(1);
			if (indent > 0)
				print(indentStrings.get(indent));
		}
	}
	
	public void indent(String text)
	{
		indent();
		print(text);
	}
	
	public void indent(char ch)
	{
		indent();
		print(ch);
	}
	
	public void indentln(char ch)
	{
		indent();
		println(ch);
	}
	
	public void indentln(String text)
	{
		indent();
		println(text);
	}
	
	public void indentAtBol()
	{
		if (atBol())
			indent();
	}
	
	public void indentAtBol(String text)
	{
		indentAtBol();
		print(text);
	}
	
	public void indentAtBol(char ch)
	{
		indentAtBol();
		print(ch);
	}
	
	public void indentlnAtBol(char ch)
	{
		indentAtBol();
		println(ch);
	}
	
	public void indentlnAtBol(String text)
	{
		indentAtBol();
		println(text);
	}
	
	/**
	 * Don't print already requested newlines.
	 */
	public void ignoreNewlines()
	{
		needNewlines = 0;
	}
	
	/**
	 * Ignore the next upcoming newline or indent.
	 */
	public void eatNewlinesAndIndents(int i)
	{
		eatNewlines += i;
	}
	
	public void clearEatNewlinesAndIndents()
	{
		eatNewlines = 0;
	}
	
	public void print(char ch)
	{
		if (ch == '\n')
		{
			println();
		}
		else
		{
			flush();
			out.print(ch);
			hadNewlines = 0;
			eatNewlines = 0;
		}
	}
	
	public void print(String text)
	{
		int from = 0;
		int length = text.length();
		while (from < length)
		{
			boolean hadNewline = false;
			
			// Find the newline
			int to = from;
			int lineSepLength = 1;
			for (; to < length; ++to)
			{
				char ch = text.charAt(to);
				if (ch == '\r') // Mac
				{
					hadNewline = true;
					if (to + 1 < length && text.charAt(to) == '\n' ) // Windows
						lineSepLength = 2;
					break;
				}
				else if (ch == '\n') // Unix
				{
					hadNewline = true;
					break;
				}
			}
			
			if (to > from)
			{
				flush();
				out.print(text.substring(from, to));
				hadNewlines = 0;
				eatNewlines = 0;
			}
			
			if (hadNewline)
				println();
			
			from = to + lineSepLength;
		}
	}
	
	public void verbatim(String text)
	{
		flush();
		out.print(text);
		hadNewlines = (text.isEmpty() || text.charAt(text.length() - 1) != '\n') ? 0 : 1;
	}
	
	public void println()
	{
		needNewlines(1);
	}
	
	public void println(char ch)
	{
		print(ch);
		needNewlines(1);
	}
	
	public void println(String text)
	{
		print(text);
		needNewlines(1);
	}
	
	public void println(Object o)
	{
		print(o.toString());
		needNewlines(1);
	}
	
	public void needNewlines(int i)
	{
		if (i > needNewlines)
			needNewlines = i;
	}
	
	public void capNewlines(int min, int max)
	{
		needNewlines = Math.max(min, Math.min(max, needNewlines));
	}
	
	public boolean atBol()
	{
		return needNewlines > eatNewlines || hadNewlines > 0;
	}
	
	public void forceln()
	{
		int newlines = needNewlines - hadNewlines;
		while (newlines-- > 0)
		{
			if (eatNewlines > 0)
			{
				--eatNewlines;
			}
			else
			{
				out.println();
				++hadNewlines;
			}
		}
		needNewlines = 0;
	}
	
	public void flush()
	{
		forceln();
	}
	
	// =========================================================================
	
	private final HashMap<Memoize, Memoize> cache = new HashMap<Memoize, Memoize>();
	
	private int reuse = 0;
	
	private boolean memoize = true;
	
	// =========================================================================
	
	public void setMemoize(boolean memoize)
	{
		this.memoize = memoize;
	}
	
	public boolean isMemoize()
	{
		return memoize;
	}
	
	// =========================================================================
	
	public Memoize memoizeStart(Object node)
	{
		if (!memoize)
		{
			return new Memoize((Object) null, (State) null);
		}
		else
		{
			Memoize m = cache.get(new Memoize(node, getState()));
			if (m == null)
			{
				return new Memoize(node, outputBufferStart());
			}
			else
			{
				++reuse;
				m.getOutputBuffer().flush();
				return null;
			}
		}
	}
	
	public void memoizeStop(Memoize m)
	{
		if (memoize)
		{
			m.getOutputBuffer().flush();
			cache.put(m, m);
		}
	}
	
	public void printMemoizationStats()
	{
		System.out.format(
				"% 6d / % 6d / %2.2f\n",
				cache.size(),
				reuse,
				(float) reuse / (float) cache.size());
	}
	
	// =========================================================================
	
	public static final class Memoize
	{
		private final Object o;
		
		private final State state;
		
		private final OutputBuffer outputBuffer;
		
		public Memoize(Object node, State state)
		{
			this.o = node;
			this.state = state;
			this.outputBuffer = null;
		}
		
		public Memoize(Object node, OutputBuffer outputBuffer)
		{
			this.o = node;
			this.state = outputBuffer.getStateOnStart();
			this.outputBuffer = outputBuffer;
		}
		
		public OutputBuffer getOutputBuffer()
		{
			return outputBuffer;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((o == null) ? 0 : o.hashCode());
			result = prime * result + ((state == null) ? 0 : state.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Memoize other = (Memoize) obj;
			if (o == null)
			{
				if (other.o != null)
					return false;
			}
			else if (o != other.o)
				return false;
			if (state == null)
			{
				if (other.state != null)
					return false;
			}
			else if (!state.equals(other.state))
				return false;
			return true;
		}
	}
}
