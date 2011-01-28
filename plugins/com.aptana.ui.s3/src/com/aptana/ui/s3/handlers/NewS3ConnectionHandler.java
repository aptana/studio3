/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.s3.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.ui.s3.actions.NewS3ConnectionAction;

public class NewS3ConnectionHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		NewS3ConnectionAction action = new NewS3ConnectionAction();
		action.setActivePart(null, HandlerUtil.getActivePart(event));
		action.run(null);
		return null;
	}
}
