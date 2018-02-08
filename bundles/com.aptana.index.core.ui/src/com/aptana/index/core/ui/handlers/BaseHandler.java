/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;

import com.aptana.core.io.efs.EFSUtils;

abstract class BaseHandler extends AbstractHandler
{
	private List<IFileStore> _fileStores;

	/**
	 * BaseHandler
	 */
	public BaseHandler()
	{
		this._fileStores = new ArrayList<IFileStore>();
	}

	/**
	 * addFileStore
	 * 
	 * @param fileStore
	 */
	private void addFileStore(IFileStore fileStore)
	{
		this._fileStores.add(fileStore);
	}

	/**
	 * clearFileStores
	 */
	private void clearFileStores()
	{
		this._fileStores.clear();
	}

	/**
	 * getFileStores
	 * 
	 * @return
	 */
	protected List<IFileStore> getFileStores()
	{
		return this._fileStores;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
	 */
	@Override
	public boolean isEnabled()
	{
		return this._fileStores.isEmpty() == false;
	}

	/**
	 * isValid
	 * 
	 * @return
	 */
	protected abstract boolean isValid(IFileStore fileStore);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#setEnabled(java.lang.Object)
	 */
	@Override
	public void setEnabled(Object evaluationContext)
	{
		// clear cached selection
		this.clearFileStores();

		if (evaluationContext instanceof IEvaluationContext)
		{
			IEvaluationContext context = (IEvaluationContext) evaluationContext;
			Object value = context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);

			if (value instanceof ISelection)
			{
				ISelection selection = (ISelection) value;

				if (selection instanceof IStructuredSelection && selection.isEmpty() == false)
				{
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;

					for (Object object : structuredSelection.toArray())
					{
						if (object instanceof IProject || object instanceof IFolder || object instanceof IFile)
						{
							IResource resource = (IResource) object;
							IFileStore fileStore = EFSUtils.getFileStore(resource);

							if (this.isValid(fileStore))
							{
								this.addFileStore(fileStore);
							}
						}
					}
				}
			}
		}
	}
}
