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

import java.io.PrintStream;

public class ConsoleProgressBar
{
	/** 0% */
	private long from;

	/** 100% */
	private long to;

	private long cur;

	private float progress;

	private int barLength;

	private int lineLength;

	private int indicatorStep;

	private String percentageFormatString;

	private PrintStream out;

	private int numRedraw = 0;

	private String[] indicator = new String[] { "-", "\\", "|", "/" };

	// =========================================================================

	public ConsoleProgressBar(long from, long to, int barLength)
	{
		this.lineLength = -1;

		setBounds(from, to, from);

		setBarLength(barLength);

		setIndicatorStep(1);
		setPercentageFormatString("%5.1f");

		setOut(System.out);
	}

	// =========================================================================

	public void setBounds(int from, int to)
	{
		checkBounds(from, to);
		this.from = from;
		this.to = to;
		update();
	}

	public void setBounds(long from, long to, long cur)
	{
		checkBounds(from, to);
		this.from = from;
		this.to = to;
		this.cur = cur;
		update();
	}

	public long getFrom()
	{
		return from;
	}

	public void setFrom(long from)
	{
		checkBounds(from, this.to);
		this.from = from;
		update();
	}

	public long getTo()
	{
		return to;
	}

	public void setTo(long to)
	{
		checkBounds(this.from, to);
		this.to = to;
		update();
	}

	public long getCur()
	{
		return cur;
	}

	public void setCur(long cur)
	{
		this.cur = cur;
		update();
	}

	protected void checkBounds(long from, long to)
	{
		if (to <= from)
			throw new FmtIllegalArgumentException("Illegal bounds!");
	}

	public PrintStream getOut()
	{
		return out;
	}

	public void setOut(PrintStream out)
	{
		this.out = out;
	}

	public float getProgress()
	{
		return progress;
	}

	// =========================================================================

	public int getBarLength()
	{
		return barLength;
	}

	public void setBarLength(int barLength)
	{
		invalidate();
		this.barLength = barLength;
	}

	public int getIndicatorStep()
	{
		return indicatorStep;
	}

	public void setIndicatorStep(int indicatorStep)
	{
		invalidate();
		this.indicatorStep = indicatorStep;
	}

	public String getPercentageFormatString()
	{
		return percentageFormatString;
	}

	public void setPercentageFormatString(String percentageFormatString)
	{
		invalidate();
		this.percentageFormatString = percentageFormatString;
	}

	private void invalidate()
	{
		this.numRedraw = 0;
	}

	// =========================================================================

	public void advance()
	{
		advance(1);
	}

	public void advance(long step)
	{
		goTo(this.cur + step);
	}

	public void goTo(long cur)
	{
		setCur(cur);
		internalRedraw();
	}

	private void internalRedraw()
	{
		if (this.numRedraw % this.indicatorStep == 0)
			redraw();

		++this.numRedraw;
	}

	// =========================================================================

	public void update()
	{
		this.progress = (this.cur - this.from) / (float) (this.to - this.from);
	}

	public void redraw()
	{
		String bar;
		String done;
		String space;

		if (this.progress <= 0.f)
		{
			done = "";
			space = StringUtils.strrep(' ', this.barLength);
		}
		else if (this.progress >= 1.f)
		{
			done = StringUtils.strrep('=', this.barLength);
			space = "";
		}
		else
		{
			int numDone = (int) (this.barLength * this.progress);
			done = StringUtils.strrep('=', numDone);
			space = StringUtils.strrep(' ', this.barLength - numDone);
		}

		int indPos =
				(this.numRedraw / this.indicatorStep) % this.indicator.length;

		bar = String.format(
				"|%s%s|%s " + this.percentageFormatString,
				done,
				space,
				this.indicator[indPos],
				this.progress * 100.f);

		int len = bar.length();
		if (len < this.lineLength)
			bar += StringUtils.strrep(' ', this.lineLength - len);

		this.lineLength = len;
		this.out.print(bar + '\r');
	}

	public void clear()
	{
		out.print(StringUtils.strrep(' ', this.lineLength) + '\r');
	}
}
