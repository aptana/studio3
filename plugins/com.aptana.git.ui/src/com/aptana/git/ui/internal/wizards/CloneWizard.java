/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

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
					job.run(monitor);
				}
			});
		}
		catch (InvocationTargetException e)
		{
			GitUIPlugin.logError(e);
		}
		catch (InterruptedException e)
		{
			GitUIPlugin.logError(e);
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
