/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.internal.actions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.debug.core.ILaunchManager;

import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.IServer.State;

/**
 * @author cwilliams
 */
public class ServerTester extends PropertyTester
{
	/**
	 * Properties we can test.
	 */
	private static final String CAN_PROFILE = "canProfile"; //$NON-NLS-1$
	private static final String CAN_DEBUG = "canDebug"; //$NON-NLS-1$
	private static final String CAN_RUN = "canRun"; //$NON-NLS-1$
	private static final String CAN_EDIT = "canEdit"; //$NON-NLS-1$
	private static final String CAN_DELETE = "canDelete"; //$NON-NLS-1$
	private static final String CAN_RESTART = "canRestart"; //$NON-NLS-1$
	private static final String CAN_STOP = "canStop"; //$NON-NLS-1$

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
		if (CAN_RUN.equals(property))
		{
			if (server.getState() != State.STOPPED)
			{
				return false;
			}
			return server.getAvailableModes().contains(ILaunchManager.RUN_MODE) && server.canStart();
		}
		if (CAN_DEBUG.equals(property))
		{
			if (server.getState() != State.STOPPED)
			{
				return false;
			}
			return server.getAvailableModes().contains(ILaunchManager.DEBUG_MODE) && server.canStart();
		}
		if (CAN_PROFILE.equals(property))
		{
			if (server.getState() != State.STOPPED)
			{
				return false;
			}
			return server.getAvailableModes().contains(ILaunchManager.PROFILE_MODE) && server.canStart();
		}
		else if (CAN_STOP.equals(property))
		{
			return server.getState() == State.STARTED && server.canStop();
		}
		else if (CAN_RESTART.equals(property))
		{
			return server.getState() == State.STARTED && server.canRestart();
		}
		else if (CAN_DELETE.equals(property) || CAN_EDIT.equals(property))
		{
			// TODO Maybe we want to enforce a server is stopped before we can edit or delete?
			return true;
		}
		return false;
	}

}
