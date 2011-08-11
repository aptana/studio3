/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.core.io.preferences.CloakingUtils;
import com.aptana.ide.syncing.ui.decorators.DecoratorUtils;
import com.aptana.ide.ui.io.Utils;

/**
 * Cloaks a specific file type so the files will be ignored during syncing.
 * 
 * @author Michael Xia (mxia@aptana.com)
 */
public class CloakAction implements IObjectActionDelegate, IViewActionDelegate
{

	private List<IFileStore> fSelectedFiles;

	public CloakAction()
	{
		fSelectedFiles = new ArrayList<IFileStore>();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(IAction)
	 */
	public void run(IAction action)
	{
		String expression;
		for (IFileStore fileStore : fSelectedFiles)
		{
			expression = getFileType(fileStore);
			CloakingUtils.addCloakFileType(expression);
		}

		DecoratorUtils.updateCloakDecorator();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		action.setEnabled(false);
		fSelectedFiles.clear();

		if (!(selection instanceof IStructuredSelection) || selection.isEmpty())
		{
			return;
		}

		Object[] elements = ((IStructuredSelection) selection).toArray();
		IFileStore fileStore;
		for (Object element : elements)
		{
			if (element instanceof IAdaptable)
			{
				fileStore = Utils.getFileStore((IAdaptable) element);
				if (fileStore != null)
				{
					if (!CloakingUtils.isFileCloaked(fileStore))
					{
						fSelectedFiles.add(fileStore);
					}
				}
			}
		}
		action.setEnabled(fSelectedFiles.size() > 0);
	}

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
	}

	public void init(IViewPart view)
	{
	}

	private static String getFileType(IFileStore fileStore)
	{
		String name = fileStore.getName();
		int index = name.lastIndexOf('.');
		if (index < 0)
		{
			return name;
		}
		return "*." + name.substring(index + 1); //$NON-NLS-1$
	}

	public void setSelection(ISelection selection)
	{
		fSelectedFiles.clear();

		if (!(selection instanceof IStructuredSelection) || selection.isEmpty())
		{
			return;
		}

		Object[] elements = ((IStructuredSelection) selection).toArray();
		IFileStore fileStore;
		for (Object element : elements)
		{
			if (element instanceof IAdaptable)
			{
				fileStore = Utils.getFileStore((IAdaptable) element);
				if (fileStore != null)
				{
					if (!CloakingUtils.isFileCloaked(fileStore))
					{
						fSelectedFiles.add(fileStore);
					}
				}
			}
		}
	}

}
