/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

import com.aptana.core.ShellExecutable;
import com.aptana.core.diagnostic.IDiagnosticLog;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;
import com.aptana.js.core.node.INodeJSService;
import com.aptana.js.core.node.INodePackageManager;

public class JSDiagnosticLog implements IDiagnosticLog
{

	public String getLog()
	{
		StringBuilder buf = new StringBuilder();

		INodeJSService service = JSCorePlugin.getDefault().getNodeJSService();
		INodeJS nodeJS = service.getValidExecutable();

		String version = "Not installed"; //$NON-NLS-1$
		if (nodeJS != null)
		{
			String nodeJSVersion = nodeJS.getVersion();
			if (!StringUtil.isEmpty(nodeJSVersion))
			{
				version = nodeJSVersion;
			}
		}
		buf.append("Node.JS Version: ").append(version).append(FileUtil.NEW_LINE); //$NON-NLS-1$

		INodePackageManager npm = nodeJS == null ? null : nodeJS.getNPM();
		String pathString = "Not installed"; //$NON-NLS-1$
		if (npm != null && npm.exists())
		{
			pathString = npm.getPath().toOSString();
		}
		buf.append("NPM Path: ").append(pathString).append(FileUtil.NEW_LINE); //$NON-NLS-1$
		if (npm != null && npm.exists())
		{
			try
			{
				String npmVersion = npm.getVersion();
				buf.append("NPM Version: ").append(npmVersion).append(FileUtil.NEW_LINE); //$NON-NLS-1$

				String highLevelPackages = getInstalledNpmPackages(npm);
				buf.append(highLevelPackages);
				buf.append(FileUtil.NEW_LINE);

				// Step1 : Get the installed version of a npm package - try to get titanium
				IStatus titaniumStatus = npm.runInBackground(INodePackageManager.GLOBAL_ARG, "ls", "titanium"); //$NON-NLS-1$ //$NON-NLS-2$
				String npmVersionOutput = titaniumStatus.getMessage();
				buf.append("npm -g ls titanium: ").append(npmVersionOutput).append(FileUtil.NEW_LINE); //$NON-NLS-1$

				// Step2: If Step1 fails for any reason, find the installed version of a package from the detailed list
				// of all npm packages.
				// The original command (npm -g ls -p) is slightly different than this one, but the content and intent
				// is the same.
				IStatus listStatus = npm.runInBackground(INodePackageManager.GLOBAL_ARG, "list"); //$NON-NLS-1$
				String listOutput = listStatus.getMessage();
				buf.append("Packages: ").append(listOutput).append(FileUtil.NEW_LINE); //$NON-NLS-1$

				// NPM config prefix values.
				buf.append("NPM_CONFIG_PREFIX env value: " + ShellExecutable.getEnvironment().get("NPM_CONFIG_PREFIX")) //$NON-NLS-1$ //$NON-NLS-2$
						.append(FileUtil.NEW_LINE);

				String configPrefixValue = JSCorePlugin.getDefault().getNodePackageManager().getConfigValue("prefix"); //$NON-NLS-1$
				buf.append("Npm config prefix value : ").append(configPrefixValue).append(FileUtil.NEW_LINE); //$NON-NLS-1$
			}
			catch (CoreException ignore)
			{
				// Shouldn't happen so long as we guarded to enforce NPM exists.
			}
		}
		return buf.toString();
	}

	private String getInstalledNpmPackages(INodePackageManager npm) throws CoreException
	{
		IStatus status = npm.runInBackground(INodePackageManager.GLOBAL_ARG, "list", "--depth=0"); //$NON-NLS-1$ //$NON-NLS-2$
		String listOutput = status.getMessage();

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
