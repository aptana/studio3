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
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

/**
 * @author seva
 */
public class StatusLineMessageTimerManager
{
	static String message;
	static boolean isError;
	static IStatusLineManager statusLineManager = null;
	private static MessageTimerTask task;

	static IStatusLineManager getStatusLineManager()
	{
		try
		{
			IWorkbenchPartSite site = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActivePart().getSite();
			return ((IViewSite) site).getActionBars().getStatusLineManager();
		}
		catch (Exception e)
		{
			// try to get the IStatusLineManager through an active editor
			try
			{
				return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
						.getEditorSite().getActionBars().getStatusLineManager();
			}
			catch (Exception e1)
			{
			}
		}
		return null;
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
			if (task != null)
			{
				task.cancel();
			}
			task = new MessageTimerTask(statusLineManager, message, isError);
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
			if (PlatformUI.getWorkbench().isClosing())
			{
				return;
			}
			Display display = PlatformUI.getWorkbench().getDisplay();
			if (display.isDisposed())
			{
				return;
			}
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