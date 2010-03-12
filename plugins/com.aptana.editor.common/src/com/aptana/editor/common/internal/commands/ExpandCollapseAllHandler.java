package com.aptana.editor.common.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.aptana.editor.common.outline.CommonOutlinePage;

public class ExpandCollapseAllHandler extends AbstractHandler
{

	// a toggling flag to indicate what the next action should be (i.e. expand or collapse)
	private boolean fExpandToggle;

	public ExpandCollapseAllHandler()
	{
		fExpandToggle = true;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof ContentOutline)
		{
			IPage page = ((ContentOutline) part).getCurrentPage();
			if (page instanceof CommonOutlinePage)
			{
				CommonOutlinePage outlinePage = (CommonOutlinePage) page;
				if (fExpandToggle)
				{
					outlinePage.expandAll();
				}
				else
				{
					outlinePage.collapseAll();
				}
				fExpandToggle = !fExpandToggle;
			}
		}
		return null;
	}
}
