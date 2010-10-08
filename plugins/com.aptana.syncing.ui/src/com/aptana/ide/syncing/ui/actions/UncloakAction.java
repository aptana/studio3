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
import com.aptana.ide.syncing.ui.internal.SyncUtils;

/**
 * Cloaks a specific file type so the files will be ignored during syncing.
 * 
 * @author Michael Xia (mxia@aptana.com)
 */
public class UncloakAction implements IObjectActionDelegate, IViewActionDelegate
{

	private List<IFileStore> fSelectedFiles;

	public UncloakAction()
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
			CloakingUtils.removeCloakFileType(expression);
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
				fileStore = SyncUtils.getFileStore((IAdaptable) element);
				if (fileStore != null)
				{
					if (CloakingUtils.isFileCloaked(fileStore))
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
		int index = name.lastIndexOf("."); //$NON-NLS-1$
		if (index < 0)
		{
			return name;
		}
		return "*." + name.substring(index + 1); //$NON-NLS-1$
	}

	public void setSelection(ISelection selection)
	{
		fSelectedFiles.clear();

		Object[] elements = ((IStructuredSelection) selection).toArray();
		IFileStore fileStore;
		for (Object element : elements)
		{
			if (element instanceof IAdaptable)
			{
				fileStore = SyncUtils.getFileStore((IAdaptable) element);
				if (fileStore != null)
				{
					if (CloakingUtils.isFileCloaked(fileStore))
					{
						fSelectedFiles.add(fileStore);
					}
				}
			}
		}
	}
}
