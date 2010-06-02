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
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Max Stepanov
 *
 */
public class DeferredTreeSelectionExpander extends JobChangeAdapter {

	private DeferredTreeContentManager deferredTreeContentManager;
	private AbstractTreeViewer viewer;
	private TreePath treePath;
	private int currentSegment;
	private Job job;
	
	/**
	 * 
	 */
	public DeferredTreeSelectionExpander(DeferredTreeContentManager dtContentManager, AbstractTreeViewer treeViewer) {
		this.deferredTreeContentManager = dtContentManager;
		this.viewer = treeViewer;
		job = new UIJob("Expand tree job") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (treePath == null) {
					return Status.CANCEL_STATUS;
				}
				if (viewer.getExpandedState(subTreePath(treePath, currentSegment))) {
					deferredTreeContentManager.addUpdateCompleteListener(DeferredTreeSelectionExpander.this);
					TreePath subTreePath = subTreePath(treePath, ++currentSegment);
					while (viewer.getExpandedState(subTreePath)) {
					    // already expanded; go to the next level
					    subTreePath = subTreePath(treePath, ++currentSegment);
					}
					viewer.expandToLevel(subTreePath, 1);
		            if (!deferredTreeContentManager.isDeferredAdapter(treePath.getSegment(currentSegment - 1))) {
		                DeferredTreeSelectionExpander.this.done(null);
		            }
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
	}

	public void setSelection(TreePath treePath) {
		this.treePath = treePath;
		if (treePath.getSegmentCount() > 1) {
			deferredTreeContentManager.addUpdateCompleteListener(this);
			viewer.expandToLevel(subTreePath(treePath, currentSegment = 1), 1);
			if (!deferredTreeContentManager.isDeferredAdapter(treePath.getSegment(currentSegment - 1))) {
			    done(null);
			}
		} else {
			viewer.setSelection(new TreeSelection(treePath));
			treePath = null;
		}
	}
	
	public boolean isDone() {
		return treePath == null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void done(IJobChangeEvent event) {
		deferredTreeContentManager.addUpdateCompleteListener(null);
		if (treePath == null) {
			return;
		}
		viewer.reveal(subTreePath(treePath, currentSegment));
		if (currentSegment+1 < treePath.getSegmentCount()) {
			if (job.getState() == Job.RUNNING || job.cancel()) {
				job.schedule(500);
			}
			return;
		}
		viewer.setSelection(new TreeSelection(treePath));
		treePath = null;
	}
	
	private static TreePath subTreePath(TreePath treePath, int segments) {
		Object[] list = new Object[segments];
		for (int i = 0; i < list.length; ++i) {
			list[i] = treePath.getSegment(i);
		}
		return new TreePath(list);
	}
	
}
