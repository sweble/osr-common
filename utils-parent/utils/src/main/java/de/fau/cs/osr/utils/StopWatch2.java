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

public class StopWatch2
{
	public static StopWatch2 buildAndStart()
	{
		StopWatch2 sw = new StopWatch2();
		sw.start();
		return sw;
	}

	// =========================================================================

	private long startNanos;

	private boolean running;

	// =========================================================================

	public StopWatch2()
	{
		running = false;
	}

	// =========================================================================

	public long start()
	{
		if (running)
			throw new IllegalStateException();
		startNanos = System.nanoTime();
		running = true;
		return startNanos;
	}

	public long stop()
	{
		if (!running)
			throw new IllegalStateException();
		long start = startNanos;
		long stop = System.nanoTime();
		startNanos = stop;
		running = false;
		return stop - start;
	}

	public long stopAndRestart()
	{
		long delta = stop();
		running = true;
		return delta;
	}

	public void reset()
	{
		running = false;
	}
}
