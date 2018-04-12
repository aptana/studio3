/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.templates;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.util.ZipUtil;
import com.aptana.projects.internal.wizards.Messages;
import com.aptana.projects.internal.wizards.OverwriteFilesSelectionDialog;

/**
 * Attempts to handle prompting for conflicts when we're extracting a zip using
 * {@link ZipUtil#extract(java.io.File, IPath, com.aptana.core.util.ZipUtil.Conflict, org.eclipse.core.runtime.IProgressMonitor)}
 * 
 * @author cwilliams
 */
public class ConflictStatusHandler implements IStatusHandler
{

	@SuppressWarnings("unchecked")
	public Object handleStatus(IStatus status, Object source) throws CoreException
	{
		final Object[] result = new Object[] { null };
		if (source instanceof Set<?>)
		{
			final Set<IPath> conflicts = (Set<IPath>) source;
			// Check if we had any conflicts. If so, display a dialog to let the user mark which
			// files he/she wishes to keep, and which would be overwritten by the Zip's content.
			if (!conflicts.isEmpty())
			{
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						OverwriteFilesSelectionDialog overwriteFilesSelectionDialog = new OverwriteFilesSelectionDialog(
								conflicts, Messages.NewProjectWizard_filesOverwriteMessage);
						if (overwriteFilesSelectionDialog.open() == Window.OK)
						{
							result[0] = overwriteFilesSelectionDialog.getResult();
						}

					}
				});
			}
		}

		return result[0];
	}
}
