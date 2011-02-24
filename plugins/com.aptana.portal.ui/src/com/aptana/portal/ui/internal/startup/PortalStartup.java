/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.internal.startup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.progress.UIJob;

import com.aptana.portal.ui.browser.PortalBrowserEditor;
import com.aptana.portal.ui.internal.Portal;

/**
 * Aptana portal browser startup.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class PortalStartup implements IStartup
{
	public void earlyStartup()
	{
		Job job = new UIJob("Launching Aptana Developer Toolbox...") { //$NON-NLS-1$

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				Portal portal = Portal.getInstance();
				if (portal.shouldOpenPortal())
				{
					portal.openPortal(null, PortalBrowserEditor.WEB_BROWSER_EDITOR_ID);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule(500);
	}
}
