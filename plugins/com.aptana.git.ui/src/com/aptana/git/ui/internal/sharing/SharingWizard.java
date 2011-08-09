/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.sharing;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.team.ui.IConfigurationWizard;
import org.eclipse.team.ui.IConfigurationWizardExtension;
import org.eclipse.ui.IWorkbench;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.ui.GitUIPlugin;

public class SharingWizard extends Wizard implements IConfigurationWizard, IConfigurationWizardExtension
{

	IProject[] projects;
	private ExistingOrNewPage existingPage;

	public SharingWizard()
	{
		setWindowTitle(Messages.SharingWizard_Title);
		setNeedsProgressMonitor(true);
	}

	public void init(IWorkbench workbench, IProject project)
	{
		init(workbench, new IProject[] { project });
	}

	public void init(IWorkbench workbench, IProject[] projects)
	{
		this.projects = new IProject[projects.length];
		System.arraycopy(projects, 0, this.projects, 0, projects.length);
	}
	
	public void addPages() {
		existingPage = new ExistingOrNewPage(this);
		addPage(existingPage);
	}

	public boolean performFinish() {
		final ConnectProviderOperation op = new ConnectProviderOperation(
				existingPage.getProjects());
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor)
						throws InvocationTargetException {
					try {
						op.run(monitor);
					} catch (CoreException ce) {
						throw new InvocationTargetException(ce);
					}
				}
			});
			return true;
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException) {
				e = e.getCause();
			}
			final IStatus status;
			if (e instanceof CoreException) {
				status = ((CoreException) e).getStatus();
				e = status.getException();
			} else {
				status = new Status(IStatus.ERROR, GitPlugin.getPluginId(), 1,
						Messages.SharingWizard_failed, e);
			}
			IdeLog.logError(GitUIPlugin.getDefault(), Messages.SharingWizard_failed, e, IDebugScopes.DEBUG);
			ErrorDialog.openError(getContainer().getShell(), getWindowTitle(),
					Messages.SharingWizard_failed, status, status.getSeverity());
			return false;
		}
	}

	@Override
	public boolean canFinish() {
		return existingPage.isPageComplete();
	}

}
