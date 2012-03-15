/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.navigator.resources.ResourceDragAdapterAssistant;

import com.aptana.ide.ui.io.FileSystemUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileDragAdapterAssistant extends ResourceDragAdapterAssistant
{

	private static final Transfer[] SUPPORTED_TRANSFERS = new Transfer[] { FileTransfer.getInstance() };

	@Override
	public Transfer[] getSupportedTransferTypes()
	{
		return SUPPORTED_TRANSFERS;
	}

	@Override
	public boolean setDragData(DragSourceEvent anEvent, IStructuredSelection aSelection)
	{
		boolean result = super.setDragData(anEvent, aSelection);
		if (result)
		{
			return true;
		}

		IFileStore[] fileStores = getSelectedFiles(aSelection);
		if (fileStores.length > 0)
		{
			if (LocalSelectionTransfer.getTransfer().isSupportedType(anEvent.dataType))
			{
				anEvent.data = fileStores;
				return true;
			}

			if (FileTransfer.getInstance().isSupportedType(anEvent.dataType))
			{
				List<String> filenames = new ArrayList<String>();
				File file;
				for (IFileStore fileStore : fileStores)
				{
					try
					{
						file = fileStore.toLocalFile(EFS.CACHE, null);
						if (file != null)
						{
							filenames.add(file.getAbsolutePath());
						}
					}
					catch (CoreException e)
					{
					}
				}
				if (filenames.isEmpty())
				{
					return false;
				}
				anEvent.data = filenames.toArray(new String[filenames.size()]);
				return true;
			}
		}
		return false;
	}

	private IFileStore[] getSelectedFiles(IStructuredSelection aSelection)
	{
		Set<IFileStore> files = new LinkedHashSet<IFileStore>();
		IFileStore file;
		Object[] selectedElements = aSelection.toArray();
		for (Object selected : selectedElements)
		{
			file = FileSystemUtils.getFileStore(selected);
			if (file != null)
			{
				files.add(file);
			}
		}
		return files.toArray(new IFileStore[files.size()]);
	}
}
