package com.aptana.explorer.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.explorer.internal.ui.GitProjectView;

public class ToggleChangedFilesFilter extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPart part = HandlerUtil.getActivePartChecked(event);
		if (part instanceof GitProjectView)
		{
			GitProjectView view = (GitProjectView) part;
			view.toggleChangedFilesFilter();
		}
		return null;
	}

}
