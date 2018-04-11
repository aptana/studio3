/**
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jetty.util.epl;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * Helper class with the API used in Jetty to log things (and redirect to the Aptana logging). Uses the CorePlugin to
 * actually do the logging (as this plugin doesn't really have an activator as it's just used as a library).
 * 
 * @author Fabio Zadrozny
 */
@SuppressWarnings({ "nls" })
public class RedirectToAptanaLog
{
	private static RedirectToAptanaLog singleton;

	private RedirectToAptanaLog()
	{
	}

	public static synchronized RedirectToAptanaLog getSingleton()
	{
		if (singleton == null)
		{
			singleton = new RedirectToAptanaLog();
		}
		return singleton;
	}

	public void ignore(Throwable e)
	{
		IdeLog.logTrace(CorePlugin.getDefault(), e.getMessage(), e, "com.aptana.jetty.util.epl");
	}

	public void warn(Throwable e)
	{
		IdeLog.logWarning(CorePlugin.getDefault(), e.getMessage(), e, "com.aptana.jetty.util.epl");
	}

	public void warn(String string)
	{
		IdeLog.logWarning(CorePlugin.getDefault(), string, "com.aptana.jetty.util.epl");
	}

	public void debug(Throwable e)
	{
		IdeLog.logTrace(CorePlugin.getDefault(), e.getMessage(), e, "com.aptana.jetty.util.epl");
	}

	public boolean isDebugEnabled()
	{
		return IdeLog.isTraceEnabled(CorePlugin.getDefault(), "com.aptana.jetty.util.epl");
	}

}
