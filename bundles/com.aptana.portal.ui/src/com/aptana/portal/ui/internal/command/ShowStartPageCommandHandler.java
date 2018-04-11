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

import com.aptana.portal.ui.internal.startpage.StartPageUtil;

/**
 * A command handler for showing the Studio's start-page.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ShowStartPageCommandHandler extends AbstractHandler
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		StartPageUtil.showStartPage(true, null);
		return null;
	}
}
