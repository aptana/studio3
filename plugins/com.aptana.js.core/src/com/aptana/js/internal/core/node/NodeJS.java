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

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.VersionUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;
import com.aptana.js.core.node.INodePackageManager;

/**
 * @author cwilliams
 */
public class NodeJS implements INodeJS
{

	private final IPath path;
	private String version;
	private NodePackageManager fNodePackageManager;

	NodeJS(IPath path)
	{
		// TODO Enforce non-null?
		this.path = path;
		fNodePackageManager = new NodePackageManager(this);
	}

	public INodePackageManager getNPM()
	{
		return fNodePackageManager;
	}

	public IPath getPath()
	{
		return path;
	}

	public synchronized String getVersion()
	{
		if (version != null || path == null)
		{
			return version;
		}
		version = ProcessUtil.outputForCommand(path.toOSString(), null, "-v"); //$NON-NLS-1$
		return version;
	}

	public boolean exists()
	{
		return path != null && path.toFile().isFile();
	}

	public IStatus runInBackground(String... args)
	{
		return ProcessUtil.runInBackground(getPath().toOSString(), null, ShellExecutable.getEnvironment(), args);
	}

	public IStatus runInBackground(IPath workingDir, Map<String, String> environment, List<String> args)
	{
		return ProcessUtil.runInBackground(getPath().toOSString(), workingDir, environment,
				args.toArray(new String[args.size()]));
	}

	public IStatus validate()
	{
		if (path == null)
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, Messages.NodeJSService_NullPathError);
		}

		if (!exists())
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, ERR_DOES_NOT_EXIST, MessageFormat.format(
					Messages.NodeJSService_FileDoesntExistError, path), null);
		}

		String version = getVersion();
		if (version == null)
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, ERR_NOT_EXECUTABLE, MessageFormat.format(
					Messages.NodeJSService_CouldntGetVersionError, path), null);
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
				Messages.NodeJSService_InvalidVersionError, path, version, MIN_NODE_VERSION), null);
	}

}
