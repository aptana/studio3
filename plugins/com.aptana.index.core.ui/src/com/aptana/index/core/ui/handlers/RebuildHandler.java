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
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;

import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexProjectJob;

public class RebuildHandler extends AbstractHandler
{
	private List<IProject> _projects;

	/**
	 * RebuildHandler
	 */
	public RebuildHandler()
	{
		this._projects = new ArrayList<IProject>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IndexManager manager = IndexManager.getInstance();

		for (IProject p : this._projects)
		{
			// remove project index
			manager.removeIndex(p.getLocationURI());

			// and then re-build it
			new IndexProjectJob(p).schedule();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
	 */
	@Override
	public boolean isEnabled()
	{
		return this._projects.isEmpty() == false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#setEnabled(java.lang.Object)
	 */
	@Override
	public void setEnabled(Object evaluationContext)
	{
		// clear cached selection
		this._projects.clear();

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
						if (object instanceof IProject)
						{
							this._projects.add((IProject) object);
						}
					}
				}
			}
		}
	}
}
