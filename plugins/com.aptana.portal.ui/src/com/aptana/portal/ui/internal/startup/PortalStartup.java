package com.aptana.portal.ui.internal.startup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.progress.UIJob;

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
					portal.openPortal(null);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule(500);
	}
}
