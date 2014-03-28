/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.node;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.util.IProcessRunner;
import com.aptana.core.util.ProcessRunner;
import com.aptana.core.util.VersionUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;
import com.aptana.js.core.node.INodePackageManager;

/**
 * @author cwilliams
 */
class NodeJS implements INodeJS
{

	private final IPath fPath;
	private String fVersion;
	private NodePackageManager fNodePackageManager;

	NodeJS(IPath path)
	{
		// TODO Enforce non-null?
		this.fPath = path;
		fNodePackageManager = new NodePackageManager(this);
	}

	public INodePackageManager getNPM()
	{
		return fNodePackageManager;
	}

	public IPath getPath()
	{
		return fPath;
	}

	public synchronized String getVersion()
	{
		if (fVersion != null || fPath == null)
		{
			return fVersion;
		}
		IStatus status = createProcessRunner().runInBackground(fPath.toOSString(), "-v"); //$NON-NLS-1$
		if (status != null)
		{
			fVersion = status.getMessage();
		}
		return fVersion;
	}

	protected IProcessRunner createProcessRunner()
	{
		return new ProcessRunner();
	}

	public boolean exists()
	{
		return fPath != null && fPath.toFile().isFile();
	}

	public IStatus runInBackground(IPath workingDir, Map<String, String> environment, List<String> args)
	{
		args.add(0, getPath().toOSString());
		return createProcessRunner().runInBackground(workingDir, environment, args.toArray(new String[args.size()]));
	}

	public IStatus validate()
	{
		if (fPath == null)
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, Messages.NodeJSService_NullPathError);
		}

		if (!exists())
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, ERR_DOES_NOT_EXIST, MessageFormat.format(
					Messages.NodeJSService_FileDoesntExistError, fPath), null);
		}

		String version = getVersion();
		if (version == null)
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, ERR_NOT_EXECUTABLE, MessageFormat.format(
					Messages.NodeJSService_CouldntGetVersionError, fPath), null);
		}

		int index = version.indexOf('v');
		if (index != -1)
		{
			version = version.substring(index + 1); // eliminate 'v'
		}

		if (VersionUtil.compareVersions(version, MIN_NODE_VERSION) >= 0)
		{
			return Status.OK_STATUS;
		}

		return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, ERR_INVALID_VERSION, MessageFormat.format(
				Messages.NodeJSService_InvalidVersionError, fPath, version, MIN_NODE_VERSION), null);
	}

}
