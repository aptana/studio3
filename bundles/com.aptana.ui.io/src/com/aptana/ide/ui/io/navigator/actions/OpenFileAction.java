/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.core.CoreStrings;
import com.aptana.ide.ui.io.Utils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class OpenFileAction extends BaseSelectionListenerAction
{

	private List<IFileStore> fFileStores;

	public OpenFileAction()
	{
		super(CoreStrings.OPEN);
		fFileStores = new ArrayList<IFileStore>();
	}

	public void run()
	{
		for (IFileStore fileStore : fFileStores)
		{
			EditorUtils.openFileInEditor(fileStore, null);
		}
	}

	public boolean updateSelection(IStructuredSelection selection)
	{
		fFileStores.clear();

		if (selection != null && !selection.isEmpty())
		{
			Object[] elements = selection.toArray();
			IFileStore fileStore;
			for (Object element : elements)
			{
				if (element instanceof IAdaptable)
				{
					fileStore = Utils.getFileStore((IAdaptable) element);
					if (fileStore != null)
					{
						fFileStores.add(fileStore);
					}
				}
			}
		}

		return super.updateSelection(selection) && fFileStores.size() > 0;
	}
}
