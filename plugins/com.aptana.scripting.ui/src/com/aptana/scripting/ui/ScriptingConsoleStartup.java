/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui;

import org.eclipse.ui.IStartup;

public class ScriptingConsoleStartup implements IStartup{


	/**
	 * earlyStartup
	 */
	public void earlyStartup()
	{
		// force scripting console to be registered with the console manager
		ScriptingConsole.getInstance();
	}

}
