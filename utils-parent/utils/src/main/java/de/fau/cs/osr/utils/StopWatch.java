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

/**
 * A class to help benchmark code. It simulates a stop watch.
 */
public class StopWatch
{
	private long startTime = -1;

	private long stopTime = -1;

	private boolean running = false;

	// =========================================================================

	public StopWatch start()
	{
		if (!running)
		{
			startTime = System.currentTimeMillis();
			running = true;
		}
		return this;
	}

	public StopWatch restart()
	{
		startTime = System.currentTimeMillis();
		running = true;
		return this;
	}

	public StopWatch stop()
	{
		if (running)
		{
			stopTime = System.currentTimeMillis();
			running = false;
		}
		return this;
	}

	/**
	 * Returns elapsed time in milliseconds or 0 if the watch has never been
	 * started.
	 */
	public long getElapsedTime()
	{
		if (startTime == -1)
		{
			return 0;
		}
		if (running)
		{
			return System.currentTimeMillis() - startTime;
		}
		else
		{
			return stopTime - startTime;
		}
	}

	public boolean isRunning()
	{
		return running;
	}

	public StopWatch reset()
	{
		startTime = -1;
		stopTime = -1;
		running = false;
		return this;
	}
}
