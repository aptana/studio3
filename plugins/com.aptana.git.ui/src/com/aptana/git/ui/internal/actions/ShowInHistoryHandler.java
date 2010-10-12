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
package com.aptana.git.ui.internal.actions;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.ui.IFileEditorInput;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;

public class ShowInHistoryHandler extends AbstractHandler
{

	private boolean enabled;

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		if (evaluationContext instanceof EvaluationContext)
		{
			IResource resource = getResource((EvaluationContext) evaluationContext);
			if (resource != null)
			{
				GitRepository repo = getGitRepositoryManager().getAttached(resource.getProject());
				if (repo != null)
				{
					enabled = true;
					return;
				}
			}
		}
		enabled = false;
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event == null)
		{
			return null;
		}
		Object context = event.getApplicationContext();
		if (context instanceof EvaluationContext)
		{
			IResource resource = getResource((EvaluationContext) context);
			if (resource != null)
			{
				IHistoryView view = TeamUI.getHistoryView();
				if (view != null)
				{
					view.showHistoryFor(resource);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private IResource getResource(EvaluationContext evContext)
	{
		Object input = evContext.getVariable("showInInput"); //$NON-NLS-1$
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fei = (IFileEditorInput) input;
			return fei.getFile();
		}

		Collection<Object> selectedFiles = (Collection<Object>) evContext.getDefaultVariable();
		for (Object selected : selectedFiles)
		{
			if (selected instanceof IResource)
			{
				return (IResource) selected;
			}
			else if (selected instanceof IAdaptable)
			{
				return (IResource) ((IAdaptable) selected).getAdapter(IResource.class);
			}

		}
		return null;
	}

}
