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

public class SpeedMeter
{
	private static final int MAX_BUFFER_SIZE = 1 << 16;

	private final double windowSizeInSeconds;

	private final AccumulatingRingBufferLong measurements;

	private final AccumulatingRingBufferLong timestamps;

	// =========================================================================

	public SpeedMeter(
			double windowSizeInSeconds,
			double expectedMeasurementsPerSecond)
	{
		this(windowSizeInSeconds,
				(int) (windowSizeInSeconds * expectedMeasurementsPerSecond));
	}

	public SpeedMeter(
			double windowSizeInSeconds,
			int bufferSize)
	{
		if (bufferSize <= 0)
			throw new IllegalArgumentException();
		if (bufferSize > MAX_BUFFER_SIZE)
			throw new IllegalArgumentException();
		this.windowSizeInSeconds = windowSizeInSeconds;
		this.measurements = new AccumulatingRingBufferLong(bufferSize);
		this.timestamps = new AccumulatingRingBufferLong(bufferSize);
	}

	// =========================================================================

	public void addValue(long value)
	{
		addValue(value, System.nanoTime());
	}

	public void addValue(long value, long nanoTime)
	{
		while (timestamps.getN() > 0)
		{
			double age = (nanoTime - timestamps.getOldest()) / 1.e9;
			if (age < windowSizeInSeconds)
				break;

			timestamps.removeOldestN(1);
			measurements.removeOldestN(1);
		}

		timestamps.add(nanoTime);
		measurements.add(value);
	}

	public void addValueIfNew(long value, long nowNano)
	{
		if ((measurements.getN() == 0) || (measurements.getNewest() != value))
			addValue(value, nowNano);
	}

	public double speed()
	{
		return speed(timestamps.getNewest());
	}

	public double speed(long nowNanos)
	{
		long deltaX = measurements.getNewest() - measurements.getOldest();
		long deltaT = nowNanos - timestamps.getOldest();
		return deltaX / (deltaT / 1.e9);
	}

	public double average()
	{
		return measurements.getSum().doubleValue() / measurements.getN();
	}

	public boolean hasMeasurements()
	{
		return measurements.getN() > 0;
	}
}
