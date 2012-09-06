/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.launch;

import org.eclipse.debug.core.ILaunch;

public class TestLaunchLifecycleListenerP2 implements ILaunchLifecycleListener
{

	public void beforeLaunch(ILaunch launch)
	{
		TestLaunchState.states.add("LAUNCH_LISTENER:BEFORE:P2");
	}

	public void launchTerminated(ILaunch launch)
	{
		TestLaunchState.states.add("LAUNCH_LISTENER:AFTER:P2");
	}

}
