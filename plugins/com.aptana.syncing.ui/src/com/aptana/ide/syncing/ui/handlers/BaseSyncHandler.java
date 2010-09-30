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
package com.aptana.ide.syncing.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.expressions.EvaluationContext;
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
		if (evaluationContext instanceof EvaluationContext)
		{
			Object value = ((EvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
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
					value = ((EvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_EDITOR_NAME);
					if (value instanceof IEditorPart)
					{
						IAdaptable resource = null;
						IEditorInput editorInput = ((IEditorPart) value).getEditorInput();
						if (editorInput instanceof IFileEditorInput)
						{
							resource = ((IFileEditorInput) editorInput).getFile();
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
