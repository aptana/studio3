/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.resources;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.filewatcher.FileWatcher;
import com.aptana.filewatcher.FileWatcherPlugin;

/**
 * The consumer of refresh requests.
 */
public class RefreshThread extends Thread
{
	private BlockingQueue<RefreshMap> toRefresh = new PriorityBlockingQueue<RefreshMap>();
	private volatile boolean terminateRequested;

	public void refresh(IResource resource, int depth)
	{
		try
		{
			toRefresh.put(new RefreshMap(resource, depth));
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(FileWatcherPlugin.getDefault(),
					MessageFormat.format("Failed to add resource to refresh list: {0}", resource), e); //$NON-NLS-1$
		}
	}

	protected boolean notificationsFrozen()
	{
		return !FileWatcher.shouldNotify();
	}

	@SuppressWarnings("restriction")
	@Override
	public void run()
	{
		try
		{
			while (!terminateRequested)
			{
				// Using a blocking queue, we want to batch up requests
				final Map<IResource, Integer> items = new HashMap<IResource, Integer>();
				RefreshMap map = toRefresh.take(); // block until we have at least one item
				items.put(map.resource, map.depth); // add it to our "batch"
				sleep(200); // wait 200 ms to potentially let other requests queue up
				// Now grab all the items out of the queue and add to the batch
				while (!toRefresh.isEmpty())
				{
					addToMap(items, toRefresh.take());
				}

				// wait until notifications are not frozen (in case it's temporarily turned off)...
				while (notificationsFrozen())
				{
					sleep(1000);
				}

				// Now actually refresh all the items in batch mode...
				WorkspaceJob job = new WorkspaceJob(Messages.RefreshThread_JobTitle)
				{

					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
					{
						for (Map.Entry<IResource, Integer> entry : items.entrySet())
						{
							doRefresh(entry.getKey(), entry.getValue());
						}
						return Status.OK_STATUS;
					}
				};
				job.run(new NullProgressMonitor());
			}
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}

	private void addToMap(Map<IResource, Integer> toRefresh, RefreshMap newItem)
	{
		IResource resource = newItem.resource;
		int depth = newItem.depth;
		if (toRefresh.containsKey(resource))
		{
			Integer oldDepth = toRefresh.get(resource);
			if (oldDepth < depth)
			{
				toRefresh.put(resource, depth);
			}
		}
		else
		{
			for (IResource refreshing : toRefresh.keySet())
			{
				if (refreshing instanceof IContainer)
				{
					IContainer container = (IContainer) refreshing;
					if (resource.getLocation() != null && container.getLocation() != null
							&& container.getLocation().isPrefixOf(resource.getLocation()))
					{
						// We already have an ancestor in the map. If it's refreshing infinitely don't add this
						// resource
						if (toRefresh.get(container) == IResource.DEPTH_INFINITE)
						{
							return;
						}
					}
				}
			}
			// FIXME http://jira.appcelerator.org/browse/APSTUD-4056 If we're inserting a container and doing depth 1+, remove subfiles/dirs from map?
			toRefresh.put(resource, depth);
		}
	}

	private void doRefresh(IResource resource, int depth)
	{
		if (resource.getType() == IResource.PROJECT)
		{
			// Check to see if this project exists in the new branch! If not, auto-close the
			// project, or
			// just not refresh it?
			IPath path = resource.getLocation();
			if (path == null || !path.toFile().exists())
			{
				// Close the project, this actually causes the .project file to get generated,
				// though!
				try
				{
					if (resource.getProject().exists())
					{
						resource.getProject().close(new NullProgressMonitor());
					}
				}
				catch (CoreException e)
				{
					if (e.getStatus().getSeverity() > IStatus.WARNING)
					{
						IdeLog.logError(FileWatcherPlugin.getDefault(), e.getMessage(), e);
					}
				}
				if (path != null)
				{
					File projectFile = path.toFile();
					if (projectFile != null)
					{
						File dotProject = new File(projectFile, IProjectDescription.DESCRIPTION_FILE_NAME);
						if (dotProject.delete())
						{
							projectFile.delete();
						}
					}
				}
			}
		}
		try
		{
			resource.refreshLocal(depth, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			IdeLog.logError(FileWatcherPlugin.getDefault(), e.getMessage(), e);
		}
	}

	public void terminate()
	{
		terminateRequested = true;
	}

	/**
	 * Small class to map a resource to the depth which to refresh it.
	 */
	private static class RefreshMap implements Comparable<RefreshMap>
	{
		IResource resource;
		int depth;

		RefreshMap(IResource resource, int depth)
		{
			this.resource = resource;
			this.depth = depth;
		}

		public int compareTo(RefreshMap o)
		{
			IPath location = resource.getLocation();
			IPath other = o.resource.getLocation();
			return (location == null ? StringUtil.EMPTY : location.toOSString())
					.compareTo((other == null ? StringUtil.EMPTY : other.toOSString()));
		}
	}
}