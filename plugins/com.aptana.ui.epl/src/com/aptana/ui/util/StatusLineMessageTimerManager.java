/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package com.aptana.ui.util;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.aptana.ui.epl.UIEplPlugin;

/**
 * @author seva
 */
public class StatusLineMessageTimerManager
{
	static String message;
	static boolean isError;
	static IStatusLineManager statusLineManager = null;

	static IStatusLineManager getStatusLineManager()
	{
		try
		{
			return UIEplPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
					.getEditorSite().getActionBars().getStatusLineManager();
		}
		catch (NullPointerException npe)
		{
			return null;
		}
	}

	static void setMessage(String newMessage, boolean newIsError)
	{
		message = newMessage;
		isError = newIsError;
		if (isError)
		{
			statusLineManager.setErrorMessage(message);
			Display.getCurrent().beep();
		}
		else
		{
			statusLineManager.setMessage(message);
		}
	}

	public static void setErrorMessage(String message, long timeout, boolean isError)
	{
		statusLineManager = getStatusLineManager();
		if (statusLineManager != null)
		{
			setMessage(message, isError);
			TimerTask task = new MessageTimerTask(statusLineManager, message, isError);
			(new Timer()).schedule(task, timeout);
		}
	}

	static class MessageTimerTask extends TimerTask
	{
		String message;
		boolean isError;
		IStatusLineManager statusLineManager;

		public MessageTimerTask(IStatusLineManager statusLineManager, String message, boolean isError)
		{
			this.message = message;
			this.isError = isError;
			this.statusLineManager = statusLineManager;
		}

		public void run()
		{
			Display display = PlatformUI.getWorkbench().getDisplay();
			display.asyncExec(new Runnable()
			{
				public void run()
				{
					if (StatusLineMessageTimerManager.statusLineManager == statusLineManager
							&& message == StatusLineMessageTimerManager.message)
					{
						if (isError)
						{
							StatusLineMessageTimerManager.statusLineManager.setErrorMessage(""); //$NON-NLS-1$
						}
						else
						{
							StatusLineMessageTimerManager.statusLineManager.setMessage(""); //$NON-NLS-1$
						}
					}
				}
			});
		}
	}
}