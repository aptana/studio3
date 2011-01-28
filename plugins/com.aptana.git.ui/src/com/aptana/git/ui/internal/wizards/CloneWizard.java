/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.wizards;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.aptana.git.ui.CloneJob;

public class CloneWizard extends Wizard implements IImportWizard
{

	private RepositorySelectionPage cloneSource;

	@Override
	public boolean performFinish()
	{
		Job job = new CloneJob(cloneSource.getSource(), cloneSource.getDestination());
		job.schedule();
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		cloneSource = new RepositorySelectionPage();
	}

	@Override
	public void addPages()
	{
		addPage(cloneSource);
	}
}
