/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.navigator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.util.EclipseUtil;

/**
 * @author Max Stepanov
 */
public class DeferredTreeSelectionExpander extends JobChangeAdapter
{

	private DeferredTreeContentManager deferredTreeContentManager;
	private AbstractTreeViewer viewer;
	private TreePath treePath;
	private int currentSegment;
	private Job job;

	/**
	 * 
	 */
	public DeferredTreeSelectionExpander(DeferredTreeContentManager dtContentManager, AbstractTreeViewer treeViewer)
	{
		this.deferredTreeContentManager = dtContentManager;
		this.viewer = treeViewer;
		job = new UIJob("Expand tree job") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (treePath == null)
				{
					return Status.CANCEL_STATUS;
				}
				if (viewer.getExpandedState(subTreePath(treePath, currentSegment)))
				{
					deferredTreeContentManager.addUpdateCompleteListener(DeferredTreeSelectionExpander.this);
					TreePath subTreePath = subTreePath(treePath, ++currentSegment);
					while (viewer.getExpandedState(subTreePath))
					{
						// already expanded; go to the next level
						subTreePath = subTreePath(treePath, ++currentSegment);
					}
					viewer.expandToLevel(subTreePath, 1);
					if (!deferredTreeContentManager.isDeferredAdapter(treePath.getSegment(currentSegment - 1)))
					{
						DeferredTreeSelectionExpander.this.done(null);
					}
				}
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.setPriority(Job.INTERACTIVE);
	}

	public void setSelection(TreePath treePath)
	{
		this.treePath = treePath;
		if (treePath.getSegmentCount() > 1)
		{
			deferredTreeContentManager.addUpdateCompleteListener(this);
			viewer.expandToLevel(subTreePath(treePath, currentSegment = 1), 1);
			if (!deferredTreeContentManager.isDeferredAdapter(treePath.getSegment(currentSegment - 1)))
			{
				done(null);
			}
		}
		else
		{
			viewer.setSelection(new TreeSelection(treePath));
			treePath = null; // $codepro.audit.disable questionableAssignment
		}
	}

	public boolean isDone()
	{
		return treePath == null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void done(IJobChangeEvent event)
	{
		deferredTreeContentManager.addUpdateCompleteListener(null);
		if (treePath == null)
		{
			return;
		}
		viewer.reveal(subTreePath(treePath, currentSegment));
		if (currentSegment + 1 < treePath.getSegmentCount())
		{
			if (job.getState() == Job.RUNNING || job.cancel())
			{
				job.schedule(500);
			}
			return;
		}
		viewer.setSelection(new TreeSelection(treePath));
		treePath = null;
	}

	private static TreePath subTreePath(TreePath treePath, int segments)
	{
		Object[] list = new Object[segments];
		for (int i = 0; i < list.length; ++i)
		{
			list[i] = treePath.getSegment(i);
		}
		return new TreePath(list);
	}

}
