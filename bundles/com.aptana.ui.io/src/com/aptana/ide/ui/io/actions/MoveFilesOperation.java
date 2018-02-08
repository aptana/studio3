/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.actions;

import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.preferences.CloakingUtils;
import com.aptana.ide.ui.io.IOUIPlugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class MoveFilesOperation extends CopyFilesOperation
{

	public MoveFilesOperation(Shell shell)
	{
		super(shell);
	}

	@Override
	protected boolean copyFile(IFileStore sourceStore, IFileStore destinationStore, IProgressMonitor monitor)
	{
		if (sourceStore == null || CloakingUtils.isFileCloaked(sourceStore))
		{
			return false;
		}

		boolean success = true;
		monitor.subTask(MessageFormat.format(Messages.MoveFilesOperation_Subtask_Moving, sourceStore.getName(),
				destinationStore.getName()));
		try
		{
			sourceStore.move(destinationStore, EFS.OVERWRITE, monitor);
		}
		catch (CoreException e)
		{
			IdeLog.logError(IOUIPlugin.getDefault(),
					MessageFormat.format(Messages.MoveFilesOperation_ERR_FailedToMove, sourceStore, destinationStore),
					e);
			success = false;
		}
		return success;
	}
}
