/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Test;

import com.aptana.ui.util.UIUtils;

public class ColorManagerTest
{

	private static final int MAX_TIMEOUT_FOR_CONDITION = 10000;
	volatile int finished;
	Object lockFinished = new Object();
	boolean executed;
	Color color;

	@Before
	public void setUp() throws Exception
	{
		executed = false;
		finished = 0;
	}

	private class ThreadColor extends Thread
	{

		private ColorManager colorManager;
		private int i;

		public ThreadColor(ColorManager colorManager, int i)
		{
			this.colorManager = colorManager;
			this.i = i;
		}

		@Override
		public void run()
		{
			try
			{
				colorManager.getColor(new RGB(i, i, i));
			}
			finally
			{
				synchronized (lockFinished)
				{
					finished += 1;
				}
			}
		}
	}

	@Test
	public void testColorManager() throws Exception
	{
		UIUtils.assertUIThread(); // This test must be run from the UI-thread!
		Display display = Display.getCurrent();
		final ColorManager colorManager = new ColorManager();

		// Cache the color
		colorManager.getColor(new RGB(10, 10, 10));
		new Thread()
		{
			public void run()
			{
				color = colorManager.getColor(new RGB(10, 10, 10));
				executed = true;
			};
		}.start();
		waitForExecuted(display);
		assertEquals(colorManager.getColor(new RGB(10, 10, 10)), color);

		// Get from thread with cache in place
		new Thread()
		{
			public void run()
			{
				color = colorManager.getColor(new RGB(30, 30, 30));
				executed = true;
			};
		}.start();
		waitForExecuted(display);
		assertEquals(colorManager.getColor(new RGB(30, 30, 30)), color);

		// Get from thread without cache in place
		new Thread()
		{
			public void run()
			{
				colorManager.dispose();
				executed = true;
			};
		}.start();
		waitForExecuted(display);

		// Start lots of threads to get colors at the same time to see if we may have some problem there.
		List<Thread> threads = new ArrayList<Thread>();
		int count = 100;
		for (int i = 0; i < count; i++)
		{
			threads.add(new ThreadColor(colorManager, i));
		}

		for (Thread t : threads)
		{
			t.start();
		}
		waitForFinishedGettingColor(display, count, colorManager, false);
		assertEquals(count, colorManager._colorsByRGB.size()); // check cache size.

		// Now, create threads but keep disposing things.
		threads.clear();
		for (int i = 0; i < count; i++)
		{
			threads.add(new ThreadColor(colorManager, i));
		}

		for (Thread t : threads)
		{
			t.start();
		}
		waitForFinishedGettingColor(display, count, colorManager, true);
		colorManager.dispose();
	}

	private void waitForExecuted(Display display)
	{
		long initial = System.currentTimeMillis();
		while (!executed)
		{
			display.readAndDispatch();
			if (System.currentTimeMillis() - initial > 10000)
			{
				fail("Condition did not occurr in specified time.");
			}
		}
		executed = false;
	}

	private void waitForFinishedGettingColor(Display display, int count, ColorManager colorManager, boolean dispose)
	{
		long initial = System.currentTimeMillis();
		while (finished != count)
		{
			// Make it access the synchronized the colorManager concurrently with the threads.
			colorManager.getColor(new RGB(50, 50, 50));
			if (dispose)
			{
				colorManager.dispose();
			}
			display.readAndDispatch();
			if (System.currentTimeMillis() - initial > MAX_TIMEOUT_FOR_CONDITION)
			{
				fail("Condition did not occurr in specified time.");
			}
		}
		finished = 0;
	}

}
