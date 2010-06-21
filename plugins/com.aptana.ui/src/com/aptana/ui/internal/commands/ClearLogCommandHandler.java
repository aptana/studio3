/**
 * 
 */
package com.aptana.ui.internal.commands;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

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
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		String logFile = System.getProperty("osgi.logfile"); //$NON-NLS-1$

		if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), Messages.ClearLogConfirmTitle,
				Messages.ClearLogConfirmDescription))
		{
			return null;
		}

		try
		{
			File file = new File(logFile);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.close();
		}
		catch (Exception e)
		{
			UIPlugin.logError(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
