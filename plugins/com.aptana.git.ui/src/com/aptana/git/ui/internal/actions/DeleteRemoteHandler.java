/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.util.EclipseUtil;
import com.aptana.git.core.model.GitRepository;
import com.aptana.ui.MenuDialogItem;
import com.aptana.ui.QuickMenuDialog;
import com.aptana.ui.util.UIUtils;

public class DeleteRemoteHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return null;
		}

		List<MenuDialogItem> listOfMaps = new ArrayList<MenuDialogItem>();
		for (String remoteName : repo.remotes())
		{
			listOfMaps.add(new MenuDialogItem(remoteName));
		}
		QuickMenuDialog dialog = new QuickMenuDialog(getShell(), Messages.DeleteRemoteHandler_PopupTitle);
		dialog.setInput(listOfMaps);
		if (dialog.open() != -1)
		{
			MenuDialogItem item = listOfMaps.get(dialog.getReturnCode());
			deleteRemote(repo, item.getText());
		}
		return null;
	}

	public static void deleteRemote(final GitRepository repo, final String remoteName)
	{
		Job job = new Job(NLS.bind(Messages.DeleteRemoteHandler_JobName, remoteName))
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				IStatus status = repo.removeRemote(remoteName);
				if (status.isOK())
				{
					// Now show a tooltip "toast" for 3 seconds to announce success
					showSuccessToast(remoteName);
				}
				return status;
			}
		};
		job.setUser(true);
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	private static void showSuccessToast(final String remoteName)
	{
		Job job = new UIJob("show toast") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				final Shell shell = UIUtils.getActiveShell();
				String text = MessageFormat.format(Messages.DeleteRemoteAction_RemoteDeleted_Msg, remoteName);
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
				toolTip.setHideDelay(UIUtils.DEFAULT_TOOLTIP_TIME);
				toolTip.setText(text);
				toolTip.show(new Point(0, 0));
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}
}
