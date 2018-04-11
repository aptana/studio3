/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.internal.startpage;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.portal.ui.IDebugScopes;
import com.aptana.portal.ui.PortalUIPlugin;

/**
 * Startup for the Studio's start page.
 * 
 * @author Ingo, Shalom
 */
public class StartPageStartup implements IStartup
{
	public void earlyStartup()
	{
		if (EclipseUtil.isTesting())
		{
			// not to open the start page when in testing mode
			return;
		}

		Job job = new UIJob(Messages.StartPageStartup_launchingStartPage)
		{
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				/**
				 * If we are launching with files on the command line, don't display the start page
				 */
				String[] args = Platform.getCommandLineArgs();
				List<File> files = FileUtil.gatherFilesFromCommandLineArguments(args);
				if (files != null && files.size() > 0)
				{
					IdeLog.logInfo(PortalUIPlugin.getDefault(),
							"Skipping Studio's start page: files on the command line.", IDebugScopes.START_PAGE); //$NON-NLS-1$
					for (File file : files)
					{
						IdeLog.logInfo(PortalUIPlugin.getDefault(),
								MessageFormat.format("Loading:{0}", file.getAbsolutePath())); //$NON-NLS-1$
					}
					return Status.CANCEL_STATUS;
				}

				/**
				 * Don't display the start page if we've chosen not to see it on startup
				 */
				if (EclipseUtil.isSystemPropertyEnabled(IStartPageUISystemProperties.HIDE_START_PAGE))
				{
					IdeLog.logInfo(PortalUIPlugin.getDefault(),
							"Skipping Studio's start page: command line flag invoked", IDebugScopes.START_PAGE); //$NON-NLS-1$
					return Status.CANCEL_STATUS;
				}
				// Show the start page only if it's forced by a system property argument, or if the Studio was just
				// installed or updated.
				if (EclipseUtil.isSystemPropertyEnabled(IStartPageUISystemProperties.FORCE_START_PAGE)
						|| StartPageUtil.shouldShowStartPage())
				{
					StartPageUtil.showStartPage(true);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule(500L);
	}
}
