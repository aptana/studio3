/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.IDebugScopes;
import com.aptana.git.ui.internal.actions.DisconnectHandler;

public class GitUtil
{

	private static final String GIT_FOLDER = ".git"; //$NON-NLS-1$

	public static void disconnectProjectFromGit(final IProject project)
	{
		DisconnectHandler disconnect = new DisconnectHandler(new JobChangeAdapter()
		{

			@Override
			public void done(IJobChangeEvent event)
			{
				IFolder gitFolder = project.getFolder(GIT_FOLDER);
				if (gitFolder.exists())
				{
					try
					{
						gitFolder.delete(true, new NullProgressMonitor());
					}
					catch (CoreException e)
					{
						IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.ACTION);
					}
				}
			}
		});
		List<IResource> selection = new ArrayList<IResource>();
		selection.add(project);
		disconnect.setSelectedResources(selection);
		try
		{
			disconnect.execute(new ExecutionEvent());
		}
		catch (ExecutionException e)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.ACTION);
		}
	}
}
