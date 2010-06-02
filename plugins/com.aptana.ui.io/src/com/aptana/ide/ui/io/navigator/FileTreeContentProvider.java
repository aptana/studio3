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
			inputElement = CoreIOPlugin.getConnectionPointManager();
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
