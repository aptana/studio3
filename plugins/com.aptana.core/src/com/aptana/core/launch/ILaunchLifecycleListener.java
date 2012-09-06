/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.launch;

import org.eclipse.debug.core.ILaunch;

/**
 * Interface which classes declared in the extension point: com.aptana.core.launchLifecycleListener must implement.
 */
public interface ILaunchLifecycleListener
{
	/**
	 * Called before a launch is actually done.
	 */
	void beforeLaunch(ILaunch launch);

	/**
	 * Called after a launch is terminated.
	 */
	void launchTerminated(ILaunch launch);

}
