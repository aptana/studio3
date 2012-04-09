/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.aptana.core.logging.IdeLog;
import com.aptana.ui.UIPlugin;

/**
 * @author Max Stepanov
 */
public class ProjectListSelectionDialog extends ElementListSelectionDialog {

	/**
	 * @param parent
	 */
	public ProjectListSelectionDialog(Shell parent) {
		super(parent, WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		setTitle(Messages.ProjectSelectionDialog_Title);
		setMessage(Messages.ProjectSelectionDialog_Message);
		final List<Object> list = new ArrayList<Object>();
		try {
			ResourcesPlugin.getWorkspace().getRoot().accept(new IResourceProxyVisitor() {
				public boolean visit(IResourceProxy proxy) throws CoreException {
					if (proxy.getType() == IResource.ROOT) {
						return true;
					}
					if (proxy.isAccessible()) {
						list.add(proxy.requestResource());
					}
					return false;
				}
			}, 0);
		} catch (CoreException e) {
			IdeLog.logError(UIPlugin.getDefault(), e);
		}
		setElements(list.toArray());
	}
}
