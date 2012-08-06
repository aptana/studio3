/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.aptana.ui.util.UIUtils;

/**
 * A dialog that allows selecting the files that will get overwritten in some process, such as a project creation that
 * involves a template.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class OverwriteFilesSelectionDialog extends ListSelectionDialog
{

	/**
	 * @param keySet
	 */
	public OverwriteFilesSelectionDialog(Set<IPath> files, String message)
	{
		super(UIUtils.getActiveShell(), files, ArrayContentProvider.getInstance(), new LabelProvider(), message);
		setInitialSelections(files.toArray(new Object[files.size()]));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.ListSelectionDialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText(Messages.OverwriteFilesSelectionDialog_overwriteFilesTitle);
	}

	private static class LabelProvider extends WorkbenchLabelProvider
	{

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.model.WorkbenchLabelProvider#decorateText(java.lang.String, java.lang.Object)
		 */
		@Override
		protected String decorateText(String input, Object element)
		{
			// Add the full path for the element
			IPath resource = (IPath) element;
			return resource.toOSString();
		}
	}
}
