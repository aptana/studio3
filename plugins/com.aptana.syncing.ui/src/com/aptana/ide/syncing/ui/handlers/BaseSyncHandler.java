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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.EFS;
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

import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.ui.io.IUniformFileStoreEditorInput;

public abstract class BaseSyncHandler extends AbstractHandler
{

	private IAdaptable[] fSelectedResources;
	// a flag indicating if the selected elements belongs to the source or destination within a sync connection
	// by default, assume the selection is from source
	private boolean fSelectedFromSource = true;

	@Override
	public boolean isEnabled()
	{
		if (fSelectedResources == null || fSelectedResources.length == 0)
		{
			return false;
		}
		for (IAdaptable resource : fSelectedResources)
		{
			if (SiteConnectionUtils.findSitesForSource(resource).length > 0)
			{
				fSelectedFromSource = true;
				return true;
			}
			if (SiteConnectionUtils.findSitesWithDestination(resource).length > 0)
			{
				fSelectedFromSource = false;
				return true;
			}
		}
		return false;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		fSelectedResources = null;
		if (evaluationContext instanceof IEvaluationContext)
		{
			Object value = ((IEvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (value instanceof ISelection)
			{
				ISelection selections = (ISelection) value;
				if (!selections.isEmpty() && selections instanceof IStructuredSelection)
				{
					Object[] resources = ((IStructuredSelection) selections).toArray();
					List<IAdaptable> list = new ArrayList<IAdaptable>();
					for (Object resource : resources)
					{
						if (resource instanceof IAdaptable)
						{
							list.add((IAdaptable) resource);
						}
					}
					fSelectedResources = list.toArray(new IAdaptable[list.size()]);
				}
				else
				{
					// checks the active editor
					value = ((IEvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_EDITOR_NAME);
					if (value instanceof IEditorPart)
					{
						IAdaptable resource = null;
						IEditorInput editorInput = ((IEditorPart) value).getEditorInput();
						if (editorInput instanceof IFileEditorInput)
						{
							resource = ((IFileEditorInput) editorInput).getFile();
						}
						else if (editorInput instanceof IUniformFileStoreEditorInput)
						{
							try
							{
								resource = EFS.getStore(((IUniformFileStoreEditorInput) editorInput).getFileStore()
										.toURI());
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
								resource = EFS.getStore(((IURIEditorInput) editorInput).getURI());
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
								resource = EFS.getStore(URIUtil.toURI(((IPathEditorInput) editorInput).getPath()));
							}
							catch (CoreException e)
							{
								// ignores
							}
						}
						if (resource != null)
						{
							fSelectedResources = new IAdaptable[1];
							fSelectedResources[0] = resource;
						}
					}
				}
			}
		}
	}

	protected IAdaptable[] getSelectedResources()
	{
		return fSelectedResources;
	}

	protected boolean isSelectionFromSource()
	{
		return fSelectedFromSource;
	}
}
