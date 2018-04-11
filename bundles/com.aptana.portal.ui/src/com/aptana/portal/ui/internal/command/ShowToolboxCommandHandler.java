/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.internal.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.aptana.portal.ui.browser.PortalBrowserEditor;
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
		Portal.getInstance().openPortal(null, PortalBrowserEditor.WEB_BROWSER_EDITOR_ID);
		return null;
	}
}
