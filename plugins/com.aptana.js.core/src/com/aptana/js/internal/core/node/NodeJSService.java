/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.node;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessStatus;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.VersionUtil;
import com.aptana.ide.core.io.downloader.DownloadManager;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJSService;

/**
 * A service for finding the path to an installed NodeJS executable, installing one, or verifying a given path as a
 * valid NodeJS executable.
 * 
 * @author cwilliams
 */
public class NodeJSService implements INodeJSService
{

	private static final String LIB = "lib"; //$NON-NLS-1$

	private static final String APPDATA_NPM_PATH = "%APPDATA%\\npm"; //$NON-NLS-1$

	/**
	 * common locations for node installation!
	 */
	private static final String PROGRAM_FILES_X86_NODEJS_NODE_PATH = "%ProgramFiles(x86)%\\nodejs"; //$NON-NLS-1$
	private static final String PROGRAM_FILES_NODEJS_NODE_PATH = "%PROGRAMFILES%\\nodejs"; //$NON-NLS-1$
	private static final String USR_LOCAL_BIN_NODE = "/usr/local/bin"; //$NON-NLS-1$

	private static final String NODE_EXE = "node.exe"; //$NON-NLS-1$

	private static final String MAC_NODE_URL = "http://go.aptana.com/installer_nodejs_osx"; //$NON-NLS-1$
	private static final String WIN_NODE_URL = "http://go.aptana.com/installer_nodejs_windows"; //$NON-NLS-1$
	private static final String MAC_EXTENSION = ".pkg"; //$NON-NLS-1$
	private static final String WIN_EXTENSION = ".msi"; //$NON-NLS-1$

	private Set<Listener> listeners;

	public NodeJSService()
	{
		listeners = new LinkedHashSet<Listener>();
	}

	/*
	 * (non-Javadoc)
	 * @see com.appcelerator.titanium.nodejs.core.INodeJSService#find()
	 */
	public IPath find()
	{
		if (PlatformUtil.isWindows())
		{
			// Look in the registry!
			String installedPath = PlatformUtil.queryRegistryStringValue("HKEY_CURRENT_USER\\Software\\Node.js", //$NON-NLS-1$
					"InstallPath"); //$NON-NLS-1$
			if (!StringUtil.isEmpty(installedPath))
			{
				IPath regPath = Path.fromOSString(installedPath).append(NODE_EXE);
				if (regPath.toFile().isFile())
				{
					return regPath;
				}
			}

			// Look on the PATH and in standard locations
			// @formatter:off
			return ExecutableUtil.find(NODE_EXE, false, 
					CollectionsUtil.newList(
							Path.fromOSString(PlatformUtil.expandEnvironmentStrings(PROGRAM_FILES_NODEJS_NODE_PATH)),
							Path.fromOSString(PlatformUtil.expandEnvironmentStrings(PROGRAM_FILES_X86_NODEJS_NODE_PATH))
							)
					);
			// @formatter:on
		}

		return ExecutableUtil.find(NODE, false, CollectionsUtil.newList(Path.fromOSString(USR_LOCAL_BIN_NODE)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.appcelerator.titanium.nodejs.core.INodeJSService#install(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus install(char[] password, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, Messages.NodeJSService_InstallingJobTitle, 100);
		// TODO This smells strongly of needing to use subclasses based on platform so I can avoid all these ifs.
		// Verify we're Windows or Mac
		if (!PlatformUtil.isWindows() && !PlatformUtil.isMac())
		{
			return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, Messages.NodeJSService_CannotInstallOnLinuxMsg);
		}

		// Grab the URL for the platform
		String rawURL = PlatformUtil.isWindows() ? WIN_NODE_URL : MAC_NODE_URL;
		String extension = PlatformUtil.isWindows() ? WIN_EXTENSION : MAC_EXTENSION;
		URL url;
		try
		{
			url = new URL(rawURL);
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(),
					MessageFormat.format("Bad Download URL for node: {0}", rawURL), e); //$NON-NLS-1$
			return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodeJSService_BadURLError, rawURL), e);
		}

		try
		{
			// download the installer
			File file = download(url, extension, sub.newChild(90));
			// run the installer
			IStatus status;
			if (PlatformUtil.isWindows())
			{
				status = ProcessUtil.runInBackground("msiexec", Path.ROOT, "/i", file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				status = ProcessUtil.run("sudo", Path.ROOT,//$NON-NLS-1$
						password, null, sub.newChild(95), "-S", "--",//$NON-NLS-1$ //$NON-NLS-2$
						"/usr/sbin/installer", "-pkg", file.getAbsolutePath(), "-target", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						"/"); //$NON-NLS-1$
			}
			// Report the status from the installer.
			if (!status.isOK())
			{
				String message;
				if (status instanceof ProcessStatus)
				{
					message = ((ProcessStatus) status).getStdErr();
				}
				else
				{
					message = status.getMessage();
				}
				IdeLog.logError(JSCorePlugin.getDefault(), "Failed to install NodeJS: " + message); //$NON-NLS-1$
				return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, Messages.NodeJSService_InstallFailedError);
			}
			if (PlatformUtil.isWindows())
			{
				// We have just installed NodeJS and any of its dependent package
				// installations might fail as the PATH would still not be updated with NodeJS location.
				// FIXME : Hacking to set the environment PATH with the installed NodeJS location.
				ShellExecutable.updatePathEnvironment(PROGRAM_FILES_NODEJS_NODE_PATH,
						PROGRAM_FILES_X86_NODEJS_NODE_PATH, APPDATA_NPM_PATH);
			}
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, e.getMessage(), e);
		}
		sub.done();
		fireNodeJSInstalled();
		return Status.OK_STATUS;
	}

	/**
	 * Downloads a file from url, return the {@link File} on disk where it was saved.
	 * 
	 * @param url
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private File download(URL url, String extension, IProgressMonitor monitor) throws CoreException
	{
		DownloadManager manager = new DownloadManager();
		IPath path = Path.fromPortableString(url.getPath());
		String name = path.lastSegment();
		File f = new File(FileUtil.getTempDirectory().toFile(), name + extension);
		f.deleteOnExit();
		manager.addURL(url, f);
		IStatus status = manager.start(monitor);
		if (!status.isOK())
		{
			throw new CoreException(status);
		}
		List<IPath> locations = manager.getContentsLocations();
		return locations.get(0).toFile();
	}

	/*
	 * (non-Javadoc)
	 * @see com.appcelerator.titanium.nodejs.core.INodeJSService#acceptBinary(org.eclipse.core.runtime.IPath)
	 */
	public IStatus acceptBinary(IPath path)
	{
		if (path == null)
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, Messages.NodeJSService_NullPathError);
		}

		if (!path.toFile().isFile())
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, INodeJSService.ERR_DOES_NOT_EXIST,
					MessageFormat.format(Messages.NodeJSService_FileDoesntExistError, path), null);
		}

		String version = getVersion(path);
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

		return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, INodeJSService.ERR_INVALID_VERSION,
				MessageFormat.format(Messages.NodeJSService_InvalidVersionError, path, version, MIN_NODE_VERSION), null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.appcelerator.titanium.nodejs.core.INodeJSService#savedPath()
	 */
	public IPath getSavedPath()
	{
		String pref = EclipseUtil.instanceScope().getNode(JSCorePlugin.PLUGIN_ID)
				.get(com.aptana.js.core.preferences.IPreferenceConstants.NODEJS_EXECUTABLE_PATH, null);
		if (!StringUtil.isEmpty(pref))
		{
			IPath path = Path.fromOSString(pref);
			if (path.toFile().isDirectory())
			{
				path = path.append(PlatformUtil.isWindows() ? NODE_EXE : NODE);
			}
			return path;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.appcelerator.titanium.nodejs.core.INodeJSService#validExecutable()
	 */
	public IPath getValidExecutable()
	{
		// try user's saved path
		IPath nodeExePath = getSavedPath();
		if (acceptBinary(nodeExePath).isOK())
		{
			return nodeExePath;
		}

		// Search PATH
		nodeExePath = find();
		if (acceptBinary(nodeExePath).isOK())
		{
			return nodeExePath;
		}

		return null;
	}

	private boolean isValidFile(IPath nodePath)
	{
		return nodePath != null && nodePath.toFile().isFile();
	}

	public boolean isInstalled()
	{
		IPath nodeExePath = getSavedPath();

		if (isValidFile(nodeExePath))
		{
			return true;
		}
		nodeExePath = find();
		return isValidFile(nodeExePath);
	}

	public void addListener(Listener listener)
	{
		listeners.add(listener);
	}

	public void removeListener(Listener listener)
	{
		listeners.remove(listener);
	}

	private void fireNodeJSInstalled()
	{
		for (Listener listener : listeners)
		{
			listener.nodeJSInstalled();
		}
	}

	public IStatus validateSourcePath(IPath path)
	{
		if (path == null || path.isEmpty())
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, Messages.NodeJSService_EmptySourcePath);
		}

		if (!path.toFile().isDirectory())
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, INodeJSService.ERR_DOES_NOT_EXIST,
					MessageFormat.format(Messages.NodeJSService_NoDirectory_0, path), null);
		}

		if (!path.append(LIB).toFile().isDirectory())
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodeJSService_InvalidLocation_0, LIB));
		}
		// TODO Any other things we want to check for to "prove" it's a NodeJS source install?

		return Status.OK_STATUS;
	}

	public String getVersion(IPath path)
	{
		if (path == null)
		{
			return null;
		}
		return ProcessUtil.outputForCommand(path.toOSString(), null, "-v"); //$NON-NLS-1$
	}
}
