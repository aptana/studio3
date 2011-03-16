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
