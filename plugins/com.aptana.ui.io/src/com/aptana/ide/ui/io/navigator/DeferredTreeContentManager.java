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

import com.aptana.ide.ui.io.CoreIOImages;

/**
 * @author Max Stepanov
 *
 */
public class DeferredTreeContentManager extends org.eclipse.ui.progress.DeferredTreeContentManager {

	private AbstractTreeViewer treeViewer;
	
	/**
	 * @param viewer
	 */
	public DeferredTreeContentManager(AbstractTreeViewer viewer) {
		super(viewer);
		this.treeViewer = viewer;
	}

	/**
	 * @param viewer
	 * @param site
	 */
	public DeferredTreeContentManager(AbstractTreeViewer viewer, IWorkbenchPartSite site) {
		super(viewer, site);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.DeferredTreeContentManager#createPendingUpdateAdapter()
	 */
	@Override
	protected PendingUpdateAdapter createPendingUpdateAdapter() {
		return new CustomPendingUpdateAdapter();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.DeferredTreeContentManager#createElementCollector(java.lang.Object, org.eclipse.ui.progress.PendingUpdateAdapter)
	 */
	@Override
	protected IElementCollector createElementCollector(Object parent, PendingUpdateAdapter placeholder) {
		if (placeholder instanceof CustomPendingUpdateAdapter) {
			((CustomPendingUpdateAdapter) placeholder).parent = parent;
		}
		return super.createElementCollector(parent, placeholder);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.DeferredTreeContentManager#runClearPlaceholderJob(org.eclipse.ui.progress.PendingUpdateAdapter)
	 */
	@Override
	protected void runClearPlaceholderJob(final PendingUpdateAdapter placeholder) {
		super.runClearPlaceholderJob(placeholder);
		if (!PlatformUI.isWorkbenchRunning() || !(placeholder instanceof CustomPendingUpdateAdapter)) {
			return;
		}
		WorkbenchJob parentUpdateJob = new WorkbenchJob("Rerent Update") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				Control control = treeViewer.getControl();
				if (control.isDisposed()) {
					return Status.CANCEL_STATUS;
				}
				treeViewer.update(((CustomPendingUpdateAdapter) placeholder).parent, null);
				return Status.OK_STATUS;
			}
			
		};
		parentUpdateJob.setSystem(true);
		parentUpdateJob.schedule();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.DeferredTreeContentManager#getFetchJobName(java.lang.Object, org.eclipse.ui.progress.IDeferredWorkbenchAdapter)
	 */
	@Override
	protected String getFetchJobName(Object parent, IDeferredWorkbenchAdapter adapter) {
		// TODO: use full path
		return super.getFetchJobName(parent, adapter);
	}

	private class CustomPendingUpdateAdapter extends PendingUpdateAdapter {

		protected Object parent;
		
		/* (non-Javadoc)
		 * @see org.eclipse.ui.progress.PendingUpdateAdapter#getImageDescriptor(java.lang.Object)
		 */
		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return CoreIOImages.getImageDescriptor(CoreIOImages.IMG_OBJS_PENDING);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.progress.PendingUpdateAdapter#getParent(java.lang.Object)
		 */
		@Override
		public Object getParent(Object o) {
			if (o == this) {
				return parent;
			}
			return super.getParent(o);
		}

	}
}
