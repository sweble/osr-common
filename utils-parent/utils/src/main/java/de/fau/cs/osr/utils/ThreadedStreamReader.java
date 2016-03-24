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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class ThreadedStreamReader
		extends
			Thread
{
	private final InputStream is;

	private final String type;

	private final StringWriter sink;

	// =========================================================================

	public ThreadedStreamReader(InputStream is, String type, StringWriter sink)
	{
		this.is = is;
		this.type = type;
		this.sink = sink;
		start();
	}

	// =========================================================================

	@Override
	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
				sink.write(type + "> " + line + "\n");
		}
		catch (IOException e)
		{
		}
	}
}
