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
package com.aptana.portal.ui.dispatch.browserNotifications;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * A browser notification class that listens to the eclipse editors and notify about changes in the recently opened
 * files history.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
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
				IWorkbenchPage activePage = PortalUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
						.getActivePage();
				if (activePage != null)
				{
					activePage.addPartListener(partListener);
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
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
					IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
					if (activePage != null)
					{
						activePage.removePartListener(partListener);
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
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
