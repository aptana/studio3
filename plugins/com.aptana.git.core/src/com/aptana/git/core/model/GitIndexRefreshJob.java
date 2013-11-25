/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.git.core.GitPlugin;

class GitIndexRefreshJob extends Job
{

	/**
	 * How long should we wait to run? This lets us gather up requests until we have a 200ms quiet period.
	 */
	private static final long UPDATE_DELAY = 200;

	/**
	 * List of refresh requests. This basically just serves to queue up all requests.
	 */
	private HashSet<IPath> fRequests;

	/**
	 * The index we're updating/refreshing.
	 */
	private GitIndex index;

	/**
	 * The boolean flag that determines if we've received an unprocessed request to refresh the entire index.
	 */
	private AtomicBoolean refreshAll;

	public GitIndexRefreshJob(GitIndex index)
	{
		super(Messages.GitIndexRefreshJob_Name);
		EclipseUtil.setSystemForJob(this);
		this.index = index;
		fRequests = new HashSet<IPath>(3);
		refreshAll = new AtomicBoolean(false);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		MultiStatus errors = new MultiStatus(GitPlugin.PLUGIN_ID, 1, Messages.GitIndexRefreshJob_ErrorMsg, null);
		try
		{
			monitor.beginTask("", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
			while (true)
			{
				if (monitor.isCanceled())
				{
					throw new OperationCanceledException();
				}

				// FIXME What if the refresh fails? Should we retry? Can we?
				IStatus status;
				if (refreshAll.compareAndSet(true, false))
				{
					// Clear all the requests out, because we'll refresh everything anyways
					synchronized (fRequests)
					{
						fRequests = new HashSet<IPath>(3);
					}
					// refresh everything
					status = index.refresh(monitor);
				}
				else
				{
					// We were asked to refresh only some files.
					// Take all the requests off the queue
					List<IPath> copy;
					synchronized (fRequests)
					{
						if (fRequests.isEmpty())
						{
							break;
						}
						copy = new ArrayList<IPath>(fRequests);
						fRequests = new HashSet<IPath>(3);
					}

					// Now refresh all the paths we had
					status = index.refresh(true, copy, monitor);
				}
				if (!status.isOK())
				{
					errors.merge(status);
				}
				// be polite to other threads (no effect on some platforms)
				Thread.yield();
			}
		}
		finally
		{
			monitor.done();
		}
		if (!errors.isOK())
		{
			// Log this, but don't actually return an error status, or it bubbles up to UI in error dialog
			IdeLog.log(GitPlugin.getDefault(), errors);
		}
		return Status.OK_STATUS;
	}

	public boolean shouldRun()
	{
		synchronized (fRequests)
		{
			return refreshAll.get() || !fRequests.isEmpty();
		}
	}

	public void refresh(Collection<IPath> paths)
	{
		if (CollectionsUtil.isEmpty(paths))
		{
			return;
		}
		// TODO If the requests grow large enough, just flip the refreshAll flag?
		synchronized (fRequests)
		{
			fRequests.addAll(paths);
		}
		schedule(UPDATE_DELAY);
	}

	public boolean belongsTo(Object family)
	{
		return family == index;
	}

	public void refreshAll()
	{
		refreshAll.set(true);
		schedule(UPDATE_DELAY);
	}
}
