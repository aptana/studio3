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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.eclipse.ui.progress.WorkbenchJob;

import com.aptana.core.util.EclipseUtil;
import com.aptana.ide.ui.io.CoreIOImages;

/**
 * @author Max Stepanov
 */
public class DeferredTreeContentManager extends org.eclipse.ui.progress.DeferredTreeContentManager
{

	private AbstractTreeViewer treeViewer; // $codepro.audit.disable hidingInheritedFields

	/**
	 * @param viewer
	 */
	public DeferredTreeContentManager(AbstractTreeViewer viewer)
	{
		super(viewer);
		this.treeViewer = viewer;
	}

	/**
	 * @param viewer
	 * @param site
	 */
	public DeferredTreeContentManager(AbstractTreeViewer viewer, IWorkbenchPartSite site)
	{
		super(viewer, site);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.progress.DeferredTreeContentManager#createPendingUpdateAdapter()
	 */
	@Override
	protected PendingUpdateAdapter createPendingUpdateAdapter()
	{
		return new CustomPendingUpdateAdapter();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.progress.DeferredTreeContentManager#createElementCollector(java.lang.Object,
	 * org.eclipse.ui.progress.PendingUpdateAdapter)
	 */
	@Override
	protected IElementCollector createElementCollector(Object parent, PendingUpdateAdapter placeholder)
	{
		if (placeholder instanceof CustomPendingUpdateAdapter)
		{
			((CustomPendingUpdateAdapter) placeholder).parent = parent;
		}
		return super.createElementCollector(parent, placeholder);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.progress.DeferredTreeContentManager#runClearPlaceholderJob(org.eclipse.ui.progress.
	 * PendingUpdateAdapter)
	 */
	@Override
	protected void runClearPlaceholderJob(final PendingUpdateAdapter placeholder)
	{
		super.runClearPlaceholderJob(placeholder);
		if (!PlatformUI.isWorkbenchRunning() || !(placeholder instanceof CustomPendingUpdateAdapter))
		{
			return;
		}
		WorkbenchJob parentUpdateJob = new WorkbenchJob("Rerent Update") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				Control control = treeViewer.getControl();
				if (control.isDisposed())
				{
					return Status.CANCEL_STATUS;
				}
				treeViewer.update(((CustomPendingUpdateAdapter) placeholder).parent, null);
				return Status.OK_STATUS;
			}

		};
		EclipseUtil.setSystemForJob(parentUpdateJob);
		parentUpdateJob.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.progress.DeferredTreeContentManager#getFetchJobName(java.lang.Object,
	 * org.eclipse.ui.progress.IDeferredWorkbenchAdapter)
	 */
	@Override
	protected String getFetchJobName(Object parent, IDeferredWorkbenchAdapter adapter)
	{
		// TODO: use full path
		return super.getFetchJobName(parent, adapter);
	}

	private static class CustomPendingUpdateAdapter extends PendingUpdateAdapter
	{

		protected Object parent;

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.progress.PendingUpdateAdapter#getImageDescriptor(java.lang.Object)
		 */
		@Override
		public ImageDescriptor getImageDescriptor(Object object)
		{
			return CoreIOImages.getImageDescriptor(CoreIOImages.IMG_OBJS_PENDING);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.progress.PendingUpdateAdapter#getParent(java.lang.Object)
		 */
		@Override
		public Object getParent(Object o)
		{
			if (this.equals(o))
			{
				return parent;
			}
			return super.getParent(o);
		}

	}
}
