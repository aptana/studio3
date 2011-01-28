/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import org.eclipse.ui.IStartup;

/**
 * EarlyStartup
 */
public class EarlyStartup implements IStartup
{
	/**
	 * EarlyStartup
	 */
	public EarlyStartup()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup()
	{
		// Force plugin to start
		ScriptingActivator.getDefault();
	}
}
