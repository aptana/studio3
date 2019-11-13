/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.node;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IProcessRunner;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessRunner;
import com.aptana.core.util.ProcessStatus;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.SudoManager;
import com.aptana.ide.core.io.downloader.DownloadManager;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;
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
	private static final String REGISTRY_PATH_NODE_JS = "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Node.js"; //$NON-NLS-1$
	private static final String REG_NODEJS_INSTALL_KEY = "InstallPath"; //$NON-NLS-1$

	private static final String NODE_EXE = "node.exe"; //$NON-NLS-1$
	private static final String NPM_EXE = "npm.cmd"; //$NON-NLS-1$

	private static final String MAC_NODE_URL = "http://go.appcelerator.com/installer_nodejs_osx_10_17_0"; //$NON-NLS-1$
	private static final String WIN_NODE_URL = "http://go.appcelerator.com/installer_nodejs_windows_x64_10_17_0.msi"; //$NON-NLS-1$
	private static final String MAC_EXTENSION = ".pkg"; //$NON-NLS-1$
	private static final String WIN_EXTENSION = ".msi"; //$NON-NLS-1$

	private final Set<NodeJsListener> listeners;
	private final Map<IPath, INodeJS> nodeJsInstalls;

	private INodeJS fNodeExePath;

	public NodeJSService()
	{
		listeners = new LinkedHashSet<NodeJsListener>();
		nodeJsInstalls = new HashMap<IPath, INodeJS>();

		fNodeExePath = findValidExecutable();
	}

	private boolean isNpmInstalled()
	{
		IPath path = null;
		if (PlatformUtil.isWindows())
		{
			// Look in the registry!
			String installedPath = PlatformUtil.queryRegistryStringValue(REGISTRY_PATH_NODE_JS, REG_NODEJS_INSTALL_KEY);
			if (!StringUtil.isEmpty(installedPath))
			{
				IPath regPath = Path.fromOSString(installedPath).append(NPM_EXE);
				if (regPath.toFile().exists())
				{
					return true;
				}
			}

			// Look on the PATH and in standard locations
			path = ExecutableUtil.find(NPM_EXE, false, CollectionsUtil.newList(
					Path.fromOSString(PlatformUtil.expandEnvironmentStrings(PROGRAM_FILES_NODEJS_NODE_PATH)),
					Path.fromOSString(PlatformUtil.expandEnvironmentStrings(PROGRAM_FILES_X86_NODEJS_NODE_PATH))));
		}
		else
		{
			path = ExecutableUtil.find(NPM, false, CollectionsUtil.newList(Path.fromOSString(USR_LOCAL_BIN_NODE)));
		}

		if (path != null && path.toFile().exists())
		{
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.appcelerator.titanium.nodejs.core.INodeJSService#find()
	 */
	public INodeJS detectInstall()
	{
		IPath path = null;
		if (PlatformUtil.isWindows())
		{
			// Look in the registry!
			String installedPath = PlatformUtil.queryRegistryStringValue(REGISTRY_PATH_NODE_JS, REG_NODEJS_INSTALL_KEY);
			if (!StringUtil.isEmpty(installedPath))
			{
				IPath regPath = Path.fromOSString(installedPath).append(NODE_EXE);
				INodeJS install = getNodeJsInstall(regPath);
				if (install.exists())
				{
					return install;
				}
			}

			// Look on the PATH and in standard locations
			// @formatter:off
			path = ExecutableUtil.find(NODE_EXE, false,
					CollectionsUtil.newList(
							Path.fromOSString(PlatformUtil.expandEnvironmentStrings(PROGRAM_FILES_NODEJS_NODE_PATH)),
							Path.fromOSString(PlatformUtil.expandEnvironmentStrings(PROGRAM_FILES_X86_NODEJS_NODE_PATH))
							)
					);
			// @formatter:on
		}
		else
		{
			path = ExecutableUtil.find(NODE, false, CollectionsUtil.newList(Path.fromOSString(USR_LOCAL_BIN_NODE)));
		}

		if (path == null)
		{
			return null;
		}
		return getNodeJsInstall(path);
	}

	private synchronized INodeJS getNodeJsInstall(IPath path)
	{
		if (nodeJsInstalls.containsKey(path))
		{
			return nodeJsInstalls.get(path);
		}
		INodeJS nodeJs = new NodeJS(path);
		addListener(nodeJs);
		nodeJsInstalls.put(path, nodeJs);
		return nodeJs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.appcelerator.titanium.nodejs.core.INodeJSService#install(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus install(char[] password, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, Messages.NodeJSService_InstallingJobTitle, 100);
		// TODO This smells strongly of needing to use subclasses based on platform so I can avoid all these ifs.
		// TODO Allow for installing on Ubuntu? May need to build from source to get version we want...
		// Verify we're Windows or Mac
		if (!PlatformUtil.isWindows() && !PlatformUtil.isMac())
		{
			return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, Messages.NodeJSService_CannotInstallOnLinuxMsg);
		}

		// Grab the URL for the platform
		String rawURL = PlatformUtil.isWindows() ? WIN_NODE_URL : MAC_NODE_URL;
		String extension = PlatformUtil.isWindows() ? WIN_EXTENSION : MAC_EXTENSION;
		URI uri;
		try
		{
			uri = new URI(rawURL);
		}
		catch (URISyntaxException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(),
					MessageFormat.format("Bad Download URI for node: {0}", rawURL), e); //$NON-NLS-1$
			return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodeJSService_BadURLError, rawURL), e);
		}

		try
		{
			// download the installer
			File file = download(uri, extension, sub.newChild(90));
			// run the installer
			IStatus status;
			if (PlatformUtil.isWindows())
			{
				status = new ProcessRunner().runInBackground(Path.ROOT, "msiexec", "/i", file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				List<String> args = new SudoManager().getArguments(password);
				CollectionsUtil.addToList(args, "/usr/sbin/installer", "-pkg", file.getAbsolutePath(), "-target", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						"/"); //$NON-NLS-1$
				status = createProcessRunner().run(Path.ROOT, null, password, args, sub.newChild(95));
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
		fNodeExePath = findValidExecutable();
		return Status.OK_STATUS;
	}

	protected IProcessRunner createProcessRunner()
	{
		return new ProcessRunner();
	}

	/**
	 * Downloads a file from uri, return the {@link File} on disk where it was saved.
	 *
	 * @param uri
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private File download(URI uri, String extension, IProgressMonitor monitor) throws CoreException
	{
		DownloadManager manager = new DownloadManager();
		IPath path = Path.fromPortableString(uri.getPath());
		String name = path.lastSegment();
		File f = new File(FileUtil.getTempDirectory().toFile(), name + extension);
		f.deleteOnExit();
		manager.addURI(uri, f);
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
	 * @see com.appcelerator.titanium.nodejs.core.INodeJSService#getInstallFromPreferences()
	 */
	public INodeJS getInstallFromPreferences()
	{
		String pref = InstanceScope.INSTANCE.getNode(JSCorePlugin.PLUGIN_ID).get(
				com.aptana.js.core.preferences.IPreferenceConstants.NODEJS_EXECUTABLE_PATH, null);
		if (StringUtil.isEmpty(pref))
		{
			return null;
		}

		IPath path = Path.fromOSString(pref);
		if (path.toFile().isDirectory())
		{
			path = path.append(PlatformUtil.isWindows() ? NODE_EXE : NODE);
		}
		return getNodeJsInstall(path);
	}

	public INodeJS getValidExecutable()
	{
		return getValidExecutable(false);
	}

	public INodeJS getValidExecutable(boolean isforce)
	{
		if (isforce)
		{
			if (fNodeExePath != null)
			{
				nodeJsInstalls.remove(fNodeExePath.getPath());
				listeners.remove(fNodeExePath);

				// Let's invalidate the current nodejs
				fNodeExePath = null;

			}
			return findValidExecutable();
		}
		return fNodeExePath;
	}

	/*
	 * (non-Javadoc)
	 * @see com.appcelerator.titanium.nodejs.core.INodeJSService#validExecutable()
	 */
	private INodeJS findValidExecutable()
	{
		// try user's saved path
		INodeJS nodeExePath = getInstallFromPreferences();
		if (nodeExePath != null && nodeExePath.validate().isOK())
		{
			fNodeExePath = nodeExePath;
		}

		// Search PATH
		nodeExePath = detectInstall();
		if (nodeExePath != null && nodeExePath.validate().isOK())
		{
			fNodeExePath = nodeExePath;
		}

		return fNodeExePath;
	}

	public boolean isInstalled()
	{
		// Do a quick check whether npm executable is available on the machine.
		if (!isNpmInstalled())
		{
			return false;
		}

		INodeJS nodeExePath = getInstallFromPreferences();
		if (nodeExePath != null && nodeExePath.exists())
		{
			return true;
		}

		nodeExePath = detectInstall();
		return nodeExePath != null && nodeExePath.exists();
	}

	public void addListener(NodeJsListener listener)
	{
		listeners.add(listener);
	}

	public void removeListener(NodeJsListener listener)
	{
		listeners.remove(listener);
	}

	private void fireNodeJSInstalled()
	{
		// TODO pass along the INodeJS instance we installed!
		for (NodeJsListener listener : listeners)
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
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, INodeJS.ERR_DOES_NOT_EXIST, MessageFormat.format(
					Messages.NodeJSService_NoDirectory_0, path), null);
		}

		if (!path.append(LIB).toFile().isDirectory())
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodeJSService_InvalidLocation_0, LIB));
		}
		// TODO Any other things we want to check for to "prove" it's a NodeJS source install?

		return Status.OK_STATUS;
	}

	public IStatus acceptBinary(IPath nodeJSBinary)
	{
		return getNodeJsInstall(nodeJSBinary).validate();
	}
}
