/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.portal.ui.dispatch.browserNotifications;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.util.EclipseUtil;
import com.aptana.portal.ui.IPortalPreferences;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * A browser notification class that listens to the project creation events and notify those changes.
 * 
 * @author pinnamuri
 */
public class ProjectCreationBrowserNotification extends AbstractBrowserNotification
{

	private IPropertyChangeListener propertyListener = new IPropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent event)
		{
			if (event.getProperty().equals(IPortalPreferences.RECENTLY_CREATED_PROJECT))
			{
				Job job = new UIJob("Notifying project create event") //$NON-NLS-1$
				{
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						// Notify the Portal's observers
						notifyTargets(IBrowserNotificationConstants.EVENT_ID_PROJECT_CREATE,
								IBrowserNotificationConstants.EVENT_TYPE_CHANGED, (String) null);
						return Status.OK_STATUS;
					}
				};
				EclipseUtil.setSystemForJob(job);
				job.schedule();
			}
		}
	};

	@Override
	public void start()
	{
		PortalUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(propertyListener);
	}

	@Override
	public void stop()
	{
		PortalUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(propertyListener);
	}

}
