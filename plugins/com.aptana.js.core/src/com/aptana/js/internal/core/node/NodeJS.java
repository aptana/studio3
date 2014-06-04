/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.node;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.datalocation.Location;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.IProcessRunner;
import com.aptana.core.util.ProcessRunner;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.TarUtil;
import com.aptana.core.util.VersionUtil;
import com.aptana.ide.core.io.downloader.DownloadManager;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;
import com.aptana.js.core.node.INodePackageManager;
import com.aptana.js.core.preferences.IPreferenceConstants;

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
		IStatus result = createProcessRunner().runInBackground(path.toOSString(), "-v"); //$NON-NLS-1$
		version = result.getMessage();
		return version;
	}

	protected IProcessRunner createProcessRunner()
	{
		return new ProcessRunner();
	}

	public boolean exists()
	{
		return path != null && path.toFile().isFile();
	}

	public IStatus downloadSource(IProgressMonitor monitor)
	{
		// Check if it already exists.
		IPath path = getSourcePath();
		if (path != null && path.toFile().isDirectory())
		{
			return Status.OK_STATUS;
		}

		try
		{
			String version = getVersion();
			if (StringUtil.isEmpty(version))
			{
				return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID,
						"Can't download source for unknown version of Node.JS");
			}
			String url = MessageFormat.format("http://nodejs.org/dist/{0}/node-{0}.tar.gz", version);
			DownloadManager manager = new DownloadManager();
			manager.addURI(URI.create(url));
			IStatus result = manager.start(monitor);
			if (result.isOK())
			{
				List<IPath> files = manager.getContentsLocations();
				// FIXME Is this the right place to store this? It's across workspaces. It may be read-only!
				Location config = Platform.getConfigurationLocation();
				if (config.isReadOnly())
				{
					config = Platform.getUserLocation(); // fall back to user?
				}
				try
				{
					if (config.lock())
					{

						URL locationURL = config.getDataArea("com.aptana.js.core/node");
						File locationFile = new File(locationURL.getFile());
						locationFile.mkdirs();
						// FIXME Can we get progress on the untar?
						TarUtil.extractTGZFile(files.get(0), Path.fromOSString(locationFile.getAbsolutePath()));
					}
					else
					{
						// wait until lock is available or fail
					}
				}
				finally
				{
					config.release();
				}

			}
			return result;
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
		catch (IOException e)
		{
			return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, e.getMessage(), e);
		}
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

	public IPath getSourcePath()
	{
		// FIXME can't we search for this ourselves?
		String value = Platform.getPreferencesService().getString(JSCorePlugin.PLUGIN_ID,
				IPreferenceConstants.NODEJS_SOURCE_PATH, null, null);
		if (!StringUtil.isEmpty(value))
		{
			return Path.fromOSString(value);
		}
		// TODO Look in the place we download it to
		try
		{
			String version = getVersion();
			if (!StringUtil.isEmpty(version))
			{
				// FIXME Is this the right place to store this? It's across workspaces. It may be read-only!
				Location config = Platform.getConfigurationLocation();
				if (config.isReadOnly())
				{
					config = Platform.getUserLocation(); // fall back to user?
				}

				URL locationURL = config.getDataArea("com.aptana.js.core/node/node-" + version);
				File locationFile = new File(locationURL.getFile());
				if (locationFile.isDirectory())
				{
					return Path.fromOSString(locationFile.getAbsolutePath());
				}
			}
		}
		catch (IOException e)
		{
			// ignore
		}
		return null;
	}
}
