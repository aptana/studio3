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

import java.text.MessageFormat;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.actions.Messages;
import com.aptana.git.ui.internal.dialogs.BranchDialog;

public class DeleteBranchAction extends MenuAction
{

	private static final int TOOLTIP_LIFETIME = 3000;

	/**
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

		SortedSet<String> localBranches = new TreeSet<String>(repo.localBranches());
		int index = 0;
		for (final String branchName : localBranches)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index++);
			menuItem.setText(branchName);
			menuItem.setEnabled(!branchName.equals(repo.currentBranch()));
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					deleteBranch(repo, branchName);
				}
			});
		}
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

		BranchDialog dialog = new BranchDialog(Display.getDefault().getActiveShell(), repo, true, false);
		if (dialog.open() == Window.OK)
			deleteBranch(repo, dialog.getBranch());
	}

	protected void deleteBranch(final GitRepository repo, final String branchName)
	{
		Job job = new Job(NLS.bind("git branch -d {0}", branchName)) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				IStatus status = repo.deleteBranch(branchName);
				if (!status.isOK())
				{
					final IStatus theStatus = status;
					final boolean[] result = new boolean[1];
					// Failed, show reason why to user and ask if they want to force with -D
					Display.getDefault().syncExec(new Runnable()
					{

						public void run()
						{
							result[0] = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
									Messages.DeleteBranchAction_BranchDeletionFailed_Title, MessageFormat.format(
											Messages.DeleteBranchAction_BranchDeletionFailed_Msg, branchName, theStatus
													.getMessage()));
						}
					});
					if (result[0])
					{
						status = repo.deleteBranch(branchName, true); // re-run with force switch as user has consented
					}
					else
					{
						// Just return a bogus OK status, since we already bugged user about it
						return Status.OK_STATUS;
					}
				}
				if (status.isOK())
				{
					// Now show a tooltip "toast" for 3 seconds to announce success
					showSuccessToast(branchName);
				}
				return status;
			}
		};
		job.setUser(true);
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	private void showSuccessToast(final String branchName)
	{
		Job job = new UIJob("show toast") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				Display display = PlatformUI.getWorkbench().getDisplay();
				if (display == null)
				{
					display = Display.getDefault();
				}
				Shell aShell = display.getActiveShell();
				if (aShell == null)
				{
					aShell = new Shell(display);					
				}
				final Shell shell = aShell;
				String text = MessageFormat.format(Messages.DeleteBranchAction_BranchDelete_Msg, branchName);
				DefaultToolTip toolTip = new DefaultToolTip(shell)
				{
					@Override
					public Point getLocation(Point size, Event event)
					{
						final Rectangle workbenchWindowBounds = shell.getBounds();
						int xCoord = workbenchWindowBounds.x + workbenchWindowBounds.width - size.x - 10;
						int yCoord = workbenchWindowBounds.y + workbenchWindowBounds.height - size.y - 10;
						return new Point(xCoord, yCoord);
					}
				};
				toolTip.setHideDelay(TOOLTIP_LIFETIME);
				toolTip.setText(text);
				toolTip.show(new Point(0, 0));
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}
}
