/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.internal.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.aptana.core.logging.IdeLog;
import com.aptana.ui.UIPlugin;

/**
 * @author ashebanow
 */
public class ClearLogCommandHandler extends AbstractHandler
{

	/**
	 * 
	 */
	public ClearLogCommandHandler()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		String logFile = System.getProperty("osgi.logfile"); //$NON-NLS-1$

		if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), Messages.ClearLogConfirmTitle,
				Messages.ClearLogConfirmDescription))
		{
			return null;
		}

		FileOutputStream outputStream = null;
		try
		{
			outputStream = new FileOutputStream(new File(logFile));
		}
		catch (Exception e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
		}
		finally
		{
			if (outputStream != null)
			{
				try
				{
					outputStream.close();
				}
				catch (IOException e)
				{
					// ignores the exception
				}
			}
		}
		return null;
	}
}
