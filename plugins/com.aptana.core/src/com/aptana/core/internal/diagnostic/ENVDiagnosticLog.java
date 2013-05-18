/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.diagnostic;

import java.util.Map;

import com.aptana.core.ShellExecutable;
import com.aptana.core.diagnostic.IDiagnosticLog;
import com.aptana.core.util.CollectionsUtil;

/**
 * @author cwilliams
 */
public class ENVDiagnosticLog implements IDiagnosticLog
{

	public String getLog()
	{
		Map<String, String> env = ShellExecutable.getEnvironment();
		StringBuilder builder = new StringBuilder("ENV:\n"); //$NON-NLS-1$
		if (!CollectionsUtil.isEmpty(env))
		{
			for (Map.Entry<String, String> entry : env.entrySet())
			{
				builder.append(entry.toString()).append("\n"); //$NON-NLS-1$
			}
		}
		return builder.toString();
	}

}
