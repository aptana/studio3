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
package com.aptana.index.core.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;

import com.aptana.ide.core.io.efs.EFSUtils;

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

		if (evaluationContext instanceof EvaluationContext)
		{
			EvaluationContext context = (EvaluationContext) evaluationContext;
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
