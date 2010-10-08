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
package com.aptana.git.ui.actions;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.Launcher;
import com.aptana.git.ui.internal.dialogs.BranchDialog;

public class MergeBranchAction extends MenuAction
{

	/*
	 * Fills the fly-out menu
	 */
	public void fillMenu(Menu menu)
	{
		IResource resource = getSelectedResource();
		if (resource == null)
			return;

		final GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
			return;

		Set<String> branches = repo.allBranches();
		int index = 0;
		for (final String branchName : branches)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index++);
			menuItem.setText(branchName);
			menuItem.setEnabled(!branchName.equals(repo.currentBranch()));
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					mergeBranch(repo, branchName);
				}
			});
		}
	}

	protected void mergeBranch(final GitRepository repo, final String branchName)
	{
		Job job = new Job(NLS.bind("git merge {0}", branchName)) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
					ILaunch launch = Launcher.launch(GitExecutable.instance().path().toOSString(), repo.workingDirectory(), subMonitor.newChild(75), "merge", //$NON-NLS-1$
							branchName);
					while (!launch.isTerminated())
					{
						Thread.sleep(50);
					}
					repo.index().refresh(subMonitor.newChild(25));
				}
				catch (CoreException e)
				{
					GitUIPlugin.logError(e);
					return e.getStatus();
				}
				catch (Throwable e)
				{
					GitUIPlugin.logError(e.getMessage(), e);
					return new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), e.getMessage());
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule();
	}

	public void run(IAction action)
	{
		// Called when keybinding is used
		IResource resource = getSelectedResource();
		if (resource == null)
			return;

		final GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
		if (repo == null)
			return;

		BranchDialog dialog = new BranchDialog(Display.getDefault().getActiveShell(), repo, true, true);
		if (dialog.open() == Window.OK)
			mergeBranch(repo, dialog.getBranch());
	}

}
