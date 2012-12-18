/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.internal;

import org.eclipse.core.runtime.IPath;

import com.aptana.core.diagnostic.IDiagnosticLog;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJSService;

public class JSDiagnosticLog implements IDiagnosticLog
{

	public String getLog()
	{
		StringBuilder buf = new StringBuilder();

		INodeJSService service = JSCorePlugin.getDefault().getNodeJSService();
		IPath path = service.getValidExecutable();

		String version = service.getVersion(path);
		if (version == null)
		{
			version = "Not installed"; //$NON-NLS-1$
		}
		buf.append("Node.JS Version: ").append(version).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

		return buf.toString();
	}

}
