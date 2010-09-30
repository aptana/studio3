package com.aptana.portal.ui.internal.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.aptana.portal.ui.internal.Portal;

/**
 * A command handler for showing the developer-toolbox from the Help menu.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class ShowToolboxCommandHandler extends AbstractHandler
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Portal.getInstance().openPortal(null);
		return null;
	}
}
