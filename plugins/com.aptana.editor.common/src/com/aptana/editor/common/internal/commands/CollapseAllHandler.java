/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.aptana.editor.common.outline.CommonOutlinePage;

public class CollapseAllHandler extends AbstractHandler
{

	public CollapseAllHandler()
	{
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof ContentOutline)
		{
			IPage page = ((ContentOutline) part).getCurrentPage();
			if (page instanceof CommonOutlinePage)
			{
				((CommonOutlinePage) page).collapseAll();
			}
		}
		return null;
	}
}
