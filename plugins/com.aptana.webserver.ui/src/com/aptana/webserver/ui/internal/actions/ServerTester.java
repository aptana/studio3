/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.internal.actions;

import org.eclipse.core.expressions.PropertyTester;

import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.IServer.State;

public class ServerTester extends PropertyTester
{

	public ServerTester()
	{
	}

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if (receiver == null || !(receiver instanceof IServer))
		{
			return false;
		}
		IServer server = (IServer) receiver;
		if ("canRun".equals(property) || "canDebug".equals(property) || "canProfile".equals(property)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		{
			// FIXME Can we somehow get the args to know if we mean to run, profile or debug?
			return server.getState() == State.STOPPED;
		}
		else if ("canStop".equals(property)) //$NON-NLS-1$
		{
			return server.getState() == State.STARTED;
		}
		else if ("canRestart".equals(property)) //$NON-NLS-1$
		{
			return server.getState() == State.STARTED;
		}
		else if ("canDelete".equals(property) || "canEdit".equals(property)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			// TODO Maybe we want to enforce a server is stopped before we can edit or delete?
			return true;
		}
		return false;
	}

}
