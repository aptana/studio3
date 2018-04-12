/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.internal.runtime.RuntimeLog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.junit.Before;
import org.junit.Test;

import com.aptana.theme.internal.ThemeManager;
import com.aptana.ui.util.UIUtils;

@SuppressWarnings("restriction")
public class ThemeManagerTest implements ILogListener
{

	private static final int MAX_TIMEOUT_FOR_CONDITION = 10000;
	private boolean executed;
	private List<IStatus> loggedMessages;

	@Before
	public void setUp() throws Exception
	{
		executed = false;
		loggedMessages = new ArrayList<IStatus>();
	}

	/**
	 * APSTUD-7392: We're checking here that it's possible to set the theme from a background thread. The problem is
	 * that as the themeing is initialized lazily, it's possible that it ends up being initialized by a background
	 * thread when getting a color if that's the first thread to get the themeing (i.e.: on get it calls a set if null).
	 * The problem is that it sends notifications that update the UI when preferences are changed and we end up with a
	 * bunch of Invalid thread access -- which we're fixing in this issue :)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testThemeManager() throws Exception
	{
		UIUtils.assertUIThread(); // This test must be run from the UI-thread!
		Display display = Display.getCurrent();
		final ThemeManager themeManager = ThemeManager.instance();
		Theme initialTheme = themeManager.getCurrentTheme();

		try
		{
			Thread thread = new Thread()
			{

				@Override
				public void run()
				{
					RuntimeLog.addLogListener(ThemeManagerTest.this);
					try
					{
						Set<String> themeNames = themeManager.getThemeNames();
						Iterator<String> it = themeNames.iterator();
						String theme1 = it.next();
						String theme2 = it.next();
						themeManager.setCurrentTheme(themeManager.getTheme(theme1));
						themeManager.setCurrentTheme(themeManager.getTheme(theme2));
					}
					finally
					{
						RuntimeLog.removeLogListener(ThemeManagerTest.this);
						executed = true;
					}
				}
			};
			thread.start();
			waitForExecuted(display);

			// Check that nothing was logged when setting the current theme.
			for (IStatus s : this.loggedMessages)
			{
				if (s.getSeverity() >= IStatus.ERROR)
				{
					if (s.getException() instanceof PartInitException)
					{
						PartInitException e = (PartInitException) s.getException();
						if (e.getMessage().startsWith("Unable to open editor, unknown editor ID:"))
						{
							continue;
						}
					}
					throw new RuntimeException(s.getException());
				}
			}
		}
		finally
		{
			themeManager.setCurrentTheme(initialTheme);
		}

	}

	private void waitForExecuted(Display display)
	{
		long initial = System.currentTimeMillis();
		while (!executed)
		{
			try
			{
				display.readAndDispatch();
			}
			catch (Exception e)
			{
				// ignore
			}
			if (System.currentTimeMillis() - initial > MAX_TIMEOUT_FOR_CONDITION)
			{
				fail("Condition did not occurr in specified time.");
			}
		}
		executed = false;
	}

	public void logging(IStatus status, String plugin)
	{
		loggedMessages.add(status);
	}

}
