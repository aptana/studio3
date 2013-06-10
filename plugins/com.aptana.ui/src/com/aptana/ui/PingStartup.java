/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import org.eclipse.ui.IStartup;

import com.aptana.usage.UsagePlugin;

public class PingStartup implements IStartup
{

	public void earlyStartup()
	{
		// force usage plugin to start, which will send a ping!
		UsagePlugin.getDefault();
	}

}
