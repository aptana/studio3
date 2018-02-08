/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.navigator;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * @author Max Stepanov
 *
 */
public class FileTreeContentProvider implements ITreeContentProvider {

    protected static final String SELECTION_EXPANDER_KEY = "deferred_selection_expander"; //$NON-NLS-1$
    protected static final String CONTENT_PROVIDER_KEY = "deferred_content_provider"; //$NON-NLS-1$

	private static final Object[] EMPTY = new Object[0];
	
	private ITreeContentProvider delegateContentProvider;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (delegateContentProvider != null) {
			return delegateContentProvider.getChildren(parentElement);
		}
		return EMPTY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (delegateContentProvider != null) {
			return delegateContentProvider.getParent(element);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (delegateContentProvider != null) {
			return delegateContentProvider.hasChildren(element);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IWorkspaceRoot) {
			inputElement = CoreIOPlugin.getConnectionPointManager(); // $codepro.audit.disable questionableAssignment
		}
		if (delegateContentProvider != null) {
			return delegateContentProvider.getElements(inputElement);
		}
		return EMPTY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		if (delegateContentProvider != null) {
			delegateContentProvider.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (delegateContentProvider == null && viewer instanceof AbstractTreeViewer) {
			AbstractTreeViewer treeViewer = (AbstractTreeViewer) viewer;
			delegateContentProvider = (ITreeContentProvider) viewer.getData(CONTENT_PROVIDER_KEY);
			if (delegateContentProvider == null) {
    			DeferredTreeContentManager contentManager = new DeferredTreeContentManager(treeViewer);
    			delegateContentProvider = new FileTreeDeferredContentProvider(contentManager);
                treeViewer.setData(CONTENT_PROVIDER_KEY, delegateContentProvider);
    			DeferredTreeSelectionExpander selectionExpander = new DeferredTreeSelectionExpander(contentManager, treeViewer);
                treeViewer.setData(SELECTION_EXPANDER_KEY, selectionExpander);
			}
		}
	}

}
