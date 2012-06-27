/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui;

import org.eclipse.ui.IStartup;

import com.aptana.scripting.ScriptingActivator;

public class ScriptingConsoleStartup implements IStartup
{

	/**
	 * earlyStartup
	 */
	public void earlyStartup()
	{
		// Force core plugin to load
		ScriptingActivator.getDefault();
		// force scripting console to be registered with the console manager
		ScriptingConsole.getInstance();
	}

}
