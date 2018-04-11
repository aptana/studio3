/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IURIEditorInput;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.ide.ui.io.IUniformFileStoreEditorInput;
import com.aptana.ide.ui.io.Utils;

public abstract class BaseCloakHandler extends BaseSyncHandler
{

	private List<IFileStore> fSelectedFiles;

	protected BaseCloakHandler()
	{
		fSelectedFiles = new ArrayList<IFileStore>();
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		fSelectedFiles.clear();
		if (evaluationContext instanceof IEvaluationContext)
		{
			Object activePart = ((IEvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_PART_NAME);
			if (activePart instanceof IEditorPart)
			{
				IEditorInput editorInput = ((IEditorPart) activePart).getEditorInput();
				if (editorInput instanceof IFileEditorInput)
				{
					fSelectedFiles.add(EFSUtils.getFileStore(((IFileEditorInput) editorInput).getFile()));
				}
				else if (editorInput instanceof IUniformFileStoreEditorInput)
				{
					try
					{
						fSelectedFiles.add(EFS.getStore(((IUniformFileStoreEditorInput) editorInput).getFileStore()
								.toURI()));
					}
					catch (CoreException e)
					{
						// ignores
					}
				}
				else if (editorInput instanceof IURIEditorInput)
				{
					try
					{
						fSelectedFiles.add(EFS.getStore(((IURIEditorInput) editorInput).getURI()));
					}
					catch (CoreException e)
					{
						// ignores
					}
				}
				else if (editorInput instanceof IPathEditorInput)
				{
					try
					{
						fSelectedFiles.add(EFS.getStore(URIUtil.toURI(((IPathEditorInput) editorInput).getPath())));
					}
					catch (CoreException e)
					{
						// ignores
					}
				}
			}
			else
			{
				Object value = ((IEvaluationContext) evaluationContext)
						.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
				if (value instanceof ISelection)
				{
					ISelection selections = (ISelection) value;
					if (!selections.isEmpty() && selections instanceof IStructuredSelection)
					{
						IFileStore fileStore;
						Object[] elements = ((IStructuredSelection) selections).toArray();
						for (Object element : elements)
						{
							if (element instanceof IAdaptable)
							{
								fileStore = Utils.getFileStore((IAdaptable) element);
								if (fileStore != null)
								{
									fSelectedFiles.add(fileStore);
								}
							}
						}
					}
				}
			}
		}
	}

	protected IFileStore[] getSelectedFiles()
	{
		return fSelectedFiles.toArray(new IFileStore[fSelectedFiles.size()]);
	}
}
