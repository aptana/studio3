/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.browserNotifications;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.util.EclipseUtil;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * A browser notification class that listens to the eclipse editors and notify about changes in the recently opened
 * files history.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class RecentFilesBrowserNotification extends AbstractBrowserNotification
{
	private IPartListener partListener;

	/**
	 * Constructor.
	 */
	public RecentFilesBrowserNotification()
	{
		partListener = new PartListener();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.portal.dispatch.AbstractBrowserNotification#start()
	 */
	@Override
	public void start()
	{
		isListening = true;
		Job job = new UIJob("register RecentlyOpenedFilesFunction workspace listener") //$NON-NLS-1$
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				IWorkbenchWindow window = PortalUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
				if (window != null)
				{
					IWorkbenchPage activePage = window.getActivePage();
					if (activePage != null)
					{
						activePage.addPartListener(partListener);
					}
				}
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.portal.dispatch.AbstractBrowserNotification#stop()
	 */
	@Override
	public void stop()
	{
		isListening = false;
		Job job = new UIJob("unregister RecentlyOpenedFilesFunction workspace listener") //$NON-NLS-1$
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{

				IWorkbench workbench = PortalUIPlugin.getDefault().getWorkbench();
				if (!workbench.isClosing())
				{
					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
					if (window != null)
					{
						IWorkbenchPage activePage = window.getActivePage();
						if (activePage != null)
						{
							activePage.removePartListener(partListener);
						}
					}
				}
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	/**
	 * Listen to the workbench part to trigger a browser update every time a new file is being opened.
	 */
	private class PartListener implements IPartListener
	{
		public void partActivated(IWorkbenchPart part)
		{
		}

		public void partBroughtToTop(IWorkbenchPart part)
		{
		}

		public void partClosed(IWorkbenchPart part)
		{
		}

		public void partDeactivated(IWorkbenchPart part)
		{
		}

		public void partOpened(IWorkbenchPart part)
		{
			if (!isListening)
			{
				IWorkbenchPage activePage = PortalUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
						.getActivePage();
				if (activePage != null)
				{
					activePage.removePartListener(this);
				}
				return;
			}
			// Notify the Portal's observers
			notifyTargets(IBrowserNotificationConstants.EVENT_ID_RECENT_FILES,
					IBrowserNotificationConstants.EVENT_TYPE_CHANGED, (String) null);
		}
	}
}
