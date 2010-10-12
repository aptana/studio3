/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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

import com.aptana.git.core.GitPlugin;

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
			GitPlugin.logError(Messages.SharingWizard_failed, e);
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
