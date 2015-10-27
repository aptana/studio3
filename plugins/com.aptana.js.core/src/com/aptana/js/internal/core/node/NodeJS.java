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
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
	private final NodePackageManager fNodePackageManager;

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
		version = doGetNodeVersion();
		return version;
	}

	private String doGetNodeVersion()
	{
		IStatus result = createProcessRunner().runInBackground(path.toOSString(), "-v"); //$NON-NLS-1$
		return result.getMessage();
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
			String url = MessageFormat.format("http://nodejs.org/dist/{0}/node-{0}.tar.gz", version); //$NON-NLS-1$
			DownloadManager manager = createDownloadManager();
			manager.addURI(URI.create(url));
			IStatus result = manager.start(monitor);
			if (result.isOK())
			{
				List<IPath> files = manager.getContentsLocations();
				// FIXME Is this the right place to store this? It's across workspaces. It may be read-only!
				Location config = getConfigurationLocation();
				if (config.isReadOnly())
				{
					config = getUserLocation(); // fall back to user?
				}
				try
				{
					if (config.lock())
					{

						URL locationURL = config.getDataArea("com.aptana.js.core/node"); //$NON-NLS-1$
						File locationFile = toFile(locationURL);
						locationFile.mkdirs();
						// FIXME Can we get progress on the untar?
						// untar will add the remaining "node-v0.10.30" style directory to the path.
						return extractTGZFile(files, locationFile);
					}
					else
					{
						// FIXME wait until lock is available, or allow "resuming" by checking for already downloaded
						// tar file?
						return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID,
								"Unable to acquire write lock on destination: " + config.getURL());
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
		catch (URISyntaxException e)
		{
			return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, e.getMessage(), e);
		}
	}

	protected IStatus extractTGZFile(List<IPath> files, File locationFile)
	{
		return TarUtil.extractTGZFile(files.get(0), Path.fromOSString(locationFile.getAbsolutePath()));
	}

	protected DownloadManager createDownloadManager()
	{
		return new DownloadManager();
	}

	public IStatus runInBackground(String... args)
	{
		List<String> allArgs = new ArrayList<String>();
		allArgs.add(getPath().toOSString());
		allArgs.addAll(Arrays.asList(args));
		return createProcessRunner().runInBackground(ShellExecutable.getEnvironment(),
				allArgs.toArray(new String[allArgs.size()]));
	}

	public IStatus runInBackground(IPath workingDir, Map<String, String> environment, List<String> args)
	{
		List<String> allArgs = new ArrayList<String>();
		allArgs.add(getPath().toOSString());
		allArgs.addAll(args);
		return createProcessRunner().runInBackground(workingDir, environment,
				allArgs.toArray(new String[allArgs.size()]));
	}

	public IStatus validate()
	{
		if (path == null)
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, Messages.NodeJSService_NullPathError);
		}

		if (!exists())
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, ERR_DOES_NOT_EXIST,
					MessageFormat.format(Messages.NodeJSService_FileDoesntExistError, path), null);
		}

		String version = getVersion();
		if (version == null)
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, ERR_NOT_EXECUTABLE,
					MessageFormat.format(Messages.NodeJSService_CouldntGetVersionError, path), null);
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

		return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, ERR_INVALID_VERSION,
				MessageFormat.format(Messages.NodeJSService_InvalidVersionError, path, version, MIN_NODE_VERSION),
				null);
	}

	public IPath getSourcePath()
	{
		// FIXME can't we search for this ourselves?
		String value = getSourcePathFromPrefs();
		if (!StringUtil.isEmpty(value))
		{
			return Path.fromOSString(value);
		}
		// Look in the place we download it to
		try
		{
			String version = getVersion();
			if (!StringUtil.isEmpty(version))
			{
				// Is this the right place to store this? It's across workspaces. It may be read-only!
				Location config = getConfigurationLocation();
				if (config.isReadOnly())
				{
					config = getUserLocation(); // fall back to user?
				}

				URL locationURL = config.getDataArea("com.aptana.js.core/node/node-" + version); //$NON-NLS-1$
				File locationFile = toFile(locationURL);
				if (locationFile.isDirectory())
				{
					return Path.fromOSString(locationFile.getAbsolutePath());
				}
			}
		}
		catch (URISyntaxException e)
		{
			// ignore
		}
		catch (IOException e)
		{
			// ignore
		}
		return null;
	}

	protected File toFile(URL locationURL) throws URISyntaxException
	{
		return new File(locationURL.toURI());
	}

	protected Location getUserLocation()
	{
		return Platform.getUserLocation();
	}

	protected Location getConfigurationLocation()
	{
		return Platform.getConfigurationLocation();
	}

	protected String getSourcePathFromPrefs()
	{
		return Platform.getPreferencesService().getString(JSCorePlugin.PLUGIN_ID,
				IPreferenceConstants.NODEJS_SOURCE_PATH, null, null);
	}

	public synchronized void nodeJSInstalled()
	{
		version = doGetNodeVersion();
	}
}
