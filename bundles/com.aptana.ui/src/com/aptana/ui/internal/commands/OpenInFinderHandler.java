/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.internal.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IURIEditorInput;

import com.aptana.core.util.URIUtil;

public class OpenInFinderHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event == null)
		{
			return Boolean.FALSE;
		}
		Object context = event.getApplicationContext();
		if (!(context instanceof IEvaluationContext))
		{
			return Boolean.FALSE;
		}

		IEvaluationContext evContext = (IEvaluationContext) event.getApplicationContext();
		Object input = evContext.getVariable(ISources.SHOW_IN_INPUT);
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fei = (IFileEditorInput) input;
			return URIUtil.open(fei.getFile().getLocationURI());
		}

		if (input instanceof IURIEditorInput)
		{
			IURIEditorInput uriInput = (IURIEditorInput) input;
			return URIUtil.open(uriInput.getURI());
		}

		boolean result = Boolean.TRUE;
		@SuppressWarnings("unchecked")
		List<Object> selectedFiles = (List<Object>) evContext.getDefaultVariable();
		if (selectedFiles.isEmpty())
		{
			return Boolean.FALSE;
		}

		for (Object selected : selectedFiles)
		{
			IResource resource = null;
			if (selected instanceof IAdaptable)
			{
				resource = (IResource) ((IAdaptable) selected).getAdapter(IResource.class);
				if (resource != null)
				{
					result = result && URIUtil.open(resource.getLocationURI());
				}
				else
				{
					IFileStore fileStore = (IFileStore) ((IAdaptable) selected).getAdapter(IFileStore.class);
					try
					{
						if (fileStore != null && fileStore.toLocalFile(EFS.NONE, null) != null)
						{
							result = result && URIUtil.open(fileStore.toURI());
						}
					}
					catch (CoreException e)
					{
					}
				}
			}
		}
		return result;
	}

}
