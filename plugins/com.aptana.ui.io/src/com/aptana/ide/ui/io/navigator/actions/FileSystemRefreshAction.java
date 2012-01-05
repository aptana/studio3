/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.core.CoreStrings;
import com.aptana.ide.ui.io.IOUIPlugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemRefreshAction extends BaseSelectionListenerAction
{

	private Object[] fSelectedElements;

	public FileSystemRefreshAction()
	{
		super(CoreStrings.REFRESH);
		setImageDescriptor(IOUIPlugin.getImageDescriptor("/icons/full/etool16/refresh.png")); //$NON-NLS-1$
		setToolTipText(Messages.FileSystemRefreshAction_ToolTip);
	}

	public void run()
	{
		if (fSelectedElements == null)
		{
			return;
		}
		for (Object element : fSelectedElements)
		{
			IOUIPlugin.refreshNavigatorView(element);
		}
	}

	protected boolean updateSelection(IStructuredSelection selection)
	{
		fSelectedElements = null;
		if (selection != null && !selection.isEmpty())
		{
			fSelectedElements = selection.toArray();
		}
		return super.updateSelection(selection) && fSelectedElements != null;
	}
}
