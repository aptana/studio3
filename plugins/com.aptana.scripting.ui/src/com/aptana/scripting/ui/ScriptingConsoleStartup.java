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
