package com.aptana.editor.common.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.aptana.editor.common.outline.CommonOutlinePage;

public class ExpandLevelHandler extends AbstractHandler
{

	private static final String LEVEL = "level"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// assumes to expand to level 1 if not specified
		int level = 1;
		String levelStr = event.getParameter(LEVEL);
		if (levelStr != null)
		{
			level = Integer.parseInt(levelStr);
		}

		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof ContentOutline)
		{
			IPage page = ((ContentOutline) part).getCurrentPage();
			if (page instanceof CommonOutlinePage)
			{
				CommonOutlinePage outlinePage = (CommonOutlinePage) page;
				// we want to expand to the specified level and collapse everything below
				outlinePage.collapseAll();
				outlinePage.expandToLevel(level);
			}
		}
		return null;
	}
}
