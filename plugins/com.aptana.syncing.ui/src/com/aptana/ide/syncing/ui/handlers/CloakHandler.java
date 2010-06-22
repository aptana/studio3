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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.ide.core.io.preferences.CloakingUtils;
import com.aptana.ide.syncing.ui.actions.CloakAction;
import com.aptana.ide.syncing.ui.internal.SyncUtils;

public class CloakHandler extends BaseSyncHandler
{
	private List<IFileStore> fSelectedFiles;

	public CloakHandler()
	{
		fSelectedFiles = new ArrayList<IFileStore>();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		CloakAction action = new CloakAction();
		action.setActivePart(null, HandlerUtil.getActivePart(event));
		action.setSelection(HandlerUtil.getCurrentSelection(event));
		action.run(null);

		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return fSelectedFiles != null && fSelectedFiles.size() > 0;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		fSelectedFiles.clear();
		if (evaluationContext instanceof EvaluationContext)
		{
			Object value = ((EvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
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
							fileStore = SyncUtils.getFileStore((IAdaptable) element);
							if (fileStore != null)
							{
								if (!CloakingUtils.isFileCloaked(fileStore))
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
}
