/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core;

import org.eclipse.core.runtime.IPath;

import com.aptana.core.ShellExecutable;
import com.aptana.core.diagnostic.IDiagnosticLog;
import com.aptana.core.util.ProcessUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJSService;
import com.aptana.js.core.node.INodePackageManager;

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

		INodePackageManager npm = JSCorePlugin.getDefault().getNodePackageManager();
		IPath npmPath = npm.findNPM();
		String pathString = "Not installed"; //$NON-NLS-1$
		if (npmPath != null)
		{
			pathString = npmPath.toOSString();
		}
		buf.append("NPM Path: ").append(pathString).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		if (npmPath != null)
		{
			String npmVersion = ProcessUtil.outputForCommand(npmPath.toOSString(), null,
					ShellExecutable.getEnvironment(), "-v"); //$NON-NLS-1$
			buf.append("NPM Version: ").append(npmVersion).append('\n'); //$NON-NLS-1$

			String highLevelPackages = getInstalledNpmPackages(npmPath);
			buf.append(highLevelPackages);
			buf.append('\n');

			String listOutput = ProcessUtil.outputForCommand(npmPath.toOSString(), null,
					ShellExecutable.getEnvironment(), INodePackageManager.GLOBAL_ARG, "list"); //$NON-NLS-1$
			buf.append("Packages: ").append(listOutput).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return buf.toString();
	}

	private String getInstalledNpmPackages(IPath npmPath)
	{
		String listOutput = ProcessUtil.outputForCommand(npmPath.toOSString(), null, ShellExecutable.getEnvironment(),
				INodePackageManager.GLOBAL_ARG, "list", "--depth=0"); //$NON-NLS-1$ //$NON-NLS-2$

		// Hack : An issue in npm list command show errors or warnings because of depth option. Filter them out of the
		// output.
		int errIndex = listOutput.indexOf("npm ERR!"); //$NON-NLS-1$
		if (errIndex != -1)
		{
			listOutput = listOutput.substring(0, errIndex);
		}
		return listOutput;
	}

}
