/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.UIPlugin;

public abstract class AbstractAccountPageProvider implements IAccountPageProvider, IExecutableExtension
{

	private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$

	private int priority = 50;

	public int getPriority()
	{
		return priority;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		String priorityStr = config.getAttribute(ATTR_PRIORITY);
		if (!StringUtil.isEmpty(priorityStr))
		{
			try
			{
				priority = Integer.parseInt(priorityStr);
			}
			catch (NumberFormatException e)
			{
				IdeLog.logWarning(
						UIPlugin.getDefault(),
						MessageFormat
								.format("Unable to parse the priority value ({0}) for the account page provider as an integer; defaulting to 50", //$NON-NLS-1$
										priorityStr));
			}
		}
	}
}
