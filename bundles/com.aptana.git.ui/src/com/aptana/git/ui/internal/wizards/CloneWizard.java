/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ProcessStatus;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.ui.CloneJob;
import com.aptana.git.ui.GitUIPlugin;

public class CloneWizard extends Wizard implements IImportWizard
{

	private RepositorySelectionPage cloneSource;

	@Override
	public boolean performFinish()
	{
		final String sourceURI = cloneSource.getSource();
		final String dest = cloneSource.getDestination();
		try
		{
			getContainer().run(true, true, new IRunnableWithProgress()
			{

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					CloneJob job = new CloneJob(sourceURI, dest);
					IStatus status = job.run(monitor);
					if (!status.isOK())
					{
						if (status instanceof ProcessStatus)
						{
							ProcessStatus ps = (ProcessStatus) status;
							String stderr = ps.getStdErr();
							throw new InvocationTargetException(new CoreException(new Status(status.getSeverity(),
									status.getPlugin(), stderr)));
						}
						throw new InvocationTargetException(new CoreException(status));
					}
				}
			});
		}
		catch (InvocationTargetException e)
		{
			if (e.getCause() instanceof CoreException)
			{
				CoreException ce = (CoreException) e.getCause();
				MessageDialog.openError(getShell(), Messages.CloneWizard_CloneFailedTitle, ce.getMessage());
			}
			else
			{
				IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		cloneSource = new RepositorySelectionPage();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages()
	{
		addPage(cloneSource);
	}
}
