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

import java.util.concurrent.TimeoutException;

public class TimeoutProcess
		extends
			Thread
{
	private final Process process;

	private Integer exit;

	// =========================================================================

	protected TimeoutProcess(Process process)
	{
		this.process = process;
	}

	// =========================================================================

	public static int waitFor(Process process, int millis) throws TimeoutException, InterruptedException
	{
		TimeoutProcess waiter = new TimeoutProcess(process);
		waiter.start();

		try
		{
			waiter.join(millis);
			if (waiter.exit == null)
				throw new TimeoutException();

			return waiter.exit;
		}
		catch (InterruptedException e)
		{
			waiter.interrupt();
			Thread.currentThread().interrupt();
			throw e;
		}
		finally
		{
			process.destroy();
		}
	}

	// =========================================================================

	public void run()
	{
		try
		{
			exit = process.waitFor();
		}
		catch (InterruptedException e)
		{
			// Ignore
		}
	}

	public Integer getExit()
	{
		return exit;
	}
}
