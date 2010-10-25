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
