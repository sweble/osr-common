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


public class MovingAverage
{
	private int movingAvgBucketSizeMs;
	
	private boolean firstUpdateValid = false;
	
	private long firstUpdate = 0;
	
	private int firstBucket = 0;
	
	private long currentBucketSum = 0;
	
	private long currentBucketCount = 0;
	
	private final RingBuffer<Double> buffer;
	
	private double bufferSum = 0;
	
	private long bufferCount = 0;
	
	// =========================================================================
	
	public MovingAverage(int movingAvgWindowMs, int movingAvgBucketSizeMs)
	{
		this.movingAvgBucketSizeMs = movingAvgBucketSizeMs;
		this.buffer = new RingBuffer<Double>(movingAvgWindowMs / movingAvgBucketSizeMs);
	}
	
	// =========================================================================
	// For time measurement only
	
	public static long startTimeNs()
	{
		return System.nanoTime();
	}
	
	public void stopTime(long startTimeNs)
	{
		long now = System.nanoTime();
		update(now, delta(startTimeNs, now));
	}
	
	public void stopTime(long startTimeNs, double factor)
	{
		long now = System.nanoTime();
		update(now, (long) (delta(startTimeNs, now) * factor));
	}
	
	public double getAverageInSec()
	{
		// nano -> micro -> milli -> si
		return getAverage() / (1000. * 1000. * 1000.);
	}
	
	// =========================================================================
	// For measurement in general
	
	public synchronized double getAverage()
	{
		long count = bufferCount;
		double sum = bufferSum;
		if (currentBucketCount > 0)
		{
			sum += currentBucketSum / (double) currentBucketCount;
			count++;
		}
		if (count == 0)
			return 0;
		return sum / (double) count;
	}
	
	public synchronized void update(long nowNs, long value)
	{
		if (firstUpdateValid)
		{
			long sinceFirstUpdate = delta(firstUpdate, nowNs) / (1000L * 1000);
			int bucket = (int) (sinceFirstUpdate / movingAvgBucketSizeMs);
			int advance = bucket - firstBucket;
			if (advance > buffer.getCapacity())
			{
				reset();
			}
			else if (advance > 0)
			{
				firstBucket += advance;
				
				if (currentBucketCount > 0)
				{
					nextBucket(currentBucketSum / (double) currentBucketCount);
					advance -= 1;
					
					currentBucketSum = 0;
					currentBucketCount = 0;
				}
				
				for (int i = 0; i < advance; ++i)
					nextBucket(null);
			}
		}
		else
		{
			firstUpdate = nowNs;
			firstUpdateValid = true;
		}
		
		currentBucketSum += value;
		currentBucketCount++;
	}
	
	public synchronized void reset()
	{
		buffer.clear();
		bufferSum = 0;
		bufferCount = 0;
		currentBucketSum = 0;
		currentBucketCount = 0;
		firstUpdateValid = false;
		firstBucket = 0;
	}
	
	// =========================================================================
	
	private void nextBucket(Double average)
	{
		Double oldest = null;
		if (buffer.willOverwrite())
			oldest = buffer.getOldest();
		
		buffer.add(average);
		if (average != null)
		{
			bufferSum += average;
			bufferCount++;
		}
		if (oldest != null)
		{
			bufferSum -= oldest;
			bufferCount--;
		}
	}
	
	private long delta(long startTime, long now)
	{
		return now - startTime;
	}
}
