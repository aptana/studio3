/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.node;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.framework.Bundle;

import com.aptana.core.IMap;
import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessStatus;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;
import com.aptana.js.core.node.INodePackageManager;

/**
 * @author cwilliams
 */
public class NodePackageManager implements INodePackageManager
{

	/**
	 * The error string that appears in the npm command output.
	 */
	private static final String NPM_ERROR = "ERR!"; //$NON-NLS-1$

	/**
	 * Config value holding location where we install bianries/modules.
	 */
	private static final String PREFIX = "prefix"; //$NON-NLS-1$

	/**
	 * ENV variable that can override prefix config value.
	 */
	private static final String NPM_CONFIG_PREFIX = "NPM_CONFIG_PREFIX"; //$NON-NLS-1$

	/**
	 * Folder where modules live.
	 */
	private static final String NODE_MODULES = "node_modules"; //$NON-NLS-1$

	private static final String BIN = "bin"; //$NON-NLS-1$
	private static final String LIB = "lib"; //$NON-NLS-1$

	/**
	 * Argument to {@code COLOR} switch/config option so that ANSI colors aren't used in output.
	 */
	private static final String FALSE = "false"; //$NON-NLS-1$

	/**
	 * Special switch/config option to set ANSI color option. Set to {@code FALSE} to disable ANSI color output.
	 */
	private static final String COLOR = "--color"; //$NON-NLS-1$

	private static final Pattern VERSION_PATTERN = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+)"); //$NON-NLS-1$

	/**
	 * Binary script name.
	 */
	private static final String NPM = "npm"; //$NON-NLS-1$

	/**
	 * Commands
	 */
	private static final String INSTALL = "install"; //$NON-NLS-1$
	private static final String LIST = "list"; //$NON-NLS-1$
	private static final String REMOVE = "remove"; //$NON-NLS-1$
	private static final String CONFIG = "config"; //$NON-NLS-1$
	private static final String GET = "get"; //$NON-NLS-1$

	/**
	 * Cached value for NPM's "prefix" config value (where modules get installed).
	 */
	private IPath fConfigPrefixPath;

	/**
	 * The installation of Node we're tied to.
	 */
	private final INodeJS nodeJS;

	/**
	 * Where the NPM binary script should live.
	 */
	private final IPath npmPath;

	/**
	 * The local installation path of npm modules directory we're tied to.
	 */
	private final IPath LOCAL_NPM_INSTALL_PATH;

	public NodePackageManager(INodeJS nodeJS)
	{
		this.nodeJS = nodeJS;

		// TODO Is there any way a user can install NPM in non-standard location relative to node?

		// Windows "npm" script is a sh script that tries to execute $basedir/node_modules/npm/bin/npm-cli.js under
		// "$basedir/node.exe" if it exists.
		// So for Windows, it would appear we'd need to run:
		// /path/to/node.exe /path/to/node/node_modules/npm/bin/npm-cli.js <args>
		if (PlatformUtil.isWindows())
		{
			npmPath = nodeJS.getPath().removeLastSegments(1).append(NODE_MODULES).append(NPM).append(BIN)
					.append("npm-cli.js"); //$NON-NLS-1$
			// Irrespective of whatever the local directory is set, Windows always installs the local npm packages
			// directly under user home directory.
			LOCAL_NPM_INSTALL_PATH = Path.fromOSString(PlatformUtil.expandEnvironmentStrings("%APPDATA%")); //$NON-NLS-1$
		}
		else
		{
			// For Mac/Linux, we just need to run:
			// /path/to/node /path/to/npm <args>
			npmPath = nodeJS.getPath().removeLastSegments(1).append(NPM);
			LOCAL_NPM_INSTALL_PATH = Path.fromOSString(PlatformUtil.expandEnvironmentStrings("~/.titanium")); //$NON-NLS-1$
		}
	}

	/**
	 * A method that grabs the path to the NPM script to run under node. If the file doesn't exist we throw a
	 * CoreException.
	 * 
	 * @return
	 * @throws CoreException
	 */
	private IPath checkedNPMPath() throws CoreException
	{
		if (exists())
		{
			return npmPath;
		}

		throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID,
				Messages.NodePackageManager_ERR_NPMNotInstalled));
	}

	/*
	 * (non-Javadoc)
	 * @see com.appcelerator.titanium.nodejs.core.INodePackageManager#install(com.appcelerator.titanium.nodejs.core.
	 * NPMInstallerCommand, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus install(String packageName, String displayName, boolean global, char[] password,
			IProgressMonitor monitor)
	{
		return install(packageName, displayName, global, password, LOCAL_NPM_INSTALL_PATH, monitor);
	}

	public IStatus install(String packageName, String displayName, boolean global, char[] password,
			IPath workingDirectory, IProgressMonitor monitor)
	{
		String globalPrefixPath = null;
		try
		{
			// TODO: Don't think we need to check npm config prefix value any more. Verify ?
			IStatus status = runNpmInstaller(packageName, displayName, global, password, workingDirectory, INSTALL,
					monitor);
			if (status.getSeverity() == IStatus.CANCEL)
			{
				return Status.OK_STATUS;
			}
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
				IdeLog.logError(JSCorePlugin.getDefault(),
						MessageFormat.format("Failed to install {0}.\n\n{1}", packageName, message)); //$NON-NLS-1$
				return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
						Messages.NodePackageManager_FailedInstallError, packageName));
			}
			else if (status instanceof ProcessStatus)
			{
				String error = ((ProcessStatus) status).getStdErr();
				if (!StringUtil.isEmpty(error))
				{
					String[] lines = error.split("\n"); //$NON-NLS-1$
					if (lines.length > 0 && lines[lines.length - 1].contains(NPM_ERROR)) //$NON-NLS-1$
					{
						IdeLog.logError(JSCorePlugin.getDefault(),
								MessageFormat.format("Failed to install {0}.\n\n{1}", packageName, error)); //$NON-NLS-1$
						return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
								Messages.NodePackageManager_FailedInstallError, packageName));
					}
				}
			}

			return status;
		}
		catch (CoreException ce)
		{
			return ce.getStatus();
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, e.getMessage(), e);
		}
		finally
		{
			// Set the global npm prefix path to its original value.
			if (!StringUtil.isEmpty(globalPrefixPath))
			{
				try
				{
					setGlobalPrefixPath(password, workingDirectory, monitor, globalPrefixPath);
				}
				catch (CoreException e)
				{
					return e.getStatus();
				}
			}
		}
	}

	private IStatus setGlobalPrefixPath(char[] password, IPath workingDirectory, IProgressMonitor monitor,
			String globalPrefixPath) throws CoreException
	{
		List<String> args = CollectionsUtil.newList(CONFIG, "set", PREFIX, globalPrefixPath); //$NON-NLS-1$
		return runNpmConfig(args, password, true, workingDirectory, monitor);
	}

	private IStatus runNpmConfig(List<String> args, char[] password, boolean global, IPath workingDirectory,
			IProgressMonitor monitor) throws CoreException
	{
		List<String> sudoArgs = getNpmSudoArgs(global);
		sudoArgs.addAll(args);
		return ProcessUtil.run(CollectionsUtil.getFirstElement(sudoArgs), workingDirectory, password,
				ShellExecutable.getEnvironment(workingDirectory), monitor,
				CollectionsUtil.toArray(sudoArgs, 1, sudoArgs.size()));
	}

	/**
	 * This will return a list of arguments for proxy settings (if we have any, otherwise an empty list).
	 * 
	 * @param env
	 *            The environment map. Passed in so we can flag passwords to obfuscate (in other words, we may modify
	 *            the map)
	 */
	private List<String> proxySettings(Map<String, String> env)
	{
		IProxyService service = JSCorePlugin.getDefault().getProxyService();
		if (service == null || !service.isProxiesEnabled())
		{
			return Collections.emptyList();
		}

		List<String> proxyArgs = new ArrayList<String>(4);
		IProxyData httpData = service.getProxyData(IProxyData.HTTP_PROXY_TYPE);
		if (httpData != null && httpData.getHost() != null)
		{
			CollectionsUtil.addToList(proxyArgs, "--proxy", buildProxyURL(httpData, env)); //$NON-NLS-1$
		}
		IProxyData httpsData = service.getProxyData(IProxyData.HTTPS_PROXY_TYPE);
		if (httpsData != null && httpsData.getHost() != null)
		{
			CollectionsUtil.addToList(proxyArgs, "--https-proxy", buildProxyURL(httpsData, env)); //$NON-NLS-1$
		}
		return proxyArgs;
	}

	/**
	 * Given proxy data, we try to convert that back into a full URL
	 * 
	 * @param data
	 *            The {@link IProxyData} we're converting into a URL string.
	 * @param env
	 *            The environment map. Passed in so we can flag passwords to obfuscate (in other words, we may modify
	 *            the map)
	 * @return
	 */
	private String buildProxyURL(IProxyData data, Map<String, String> env)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("http://"); //$NON-NLS-1$
		if (!StringUtil.isEmpty(data.getUserId()))
		{
			builder.append(data.getUserId());
			builder.append(':');
			String password = data.getPassword();
			builder.append(password);
			builder.append('@');
			env.put(ProcessUtil.TEXT_TO_OBFUSCATE, password);
		}
		builder.append(data.getHost());
		if (data.getPort() != -1)
		{
			builder.append(':');
			builder.append(data.getPort());
		}
		return builder.toString();
	}

	public Set<String> list(boolean global) throws CoreException
	{
		IStatus status;
		if (global)
		{
			status = runInBackground(GLOBAL_ARG, PARSEABLE_ARG, LIST);
		}
		else
		{
			status = runInBackground(PARSEABLE_ARG, LIST);
		}

		if (!status.isOK())
		{
			throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID,
					Messages.NodePackageManager_FailedListingError));
		}

		// Need to parse the output!
		String output = status.getMessage();
		String[] lines = StringUtil.LINE_SPLITTER.split(output);
		List<IPath> paths = CollectionsUtil.map(CollectionsUtil.newSet(lines), new IMap<String, IPath>()
		{
			public IPath map(String item)
			{
				return Path.fromOSString(item);
			}
		});
		Set<String> installed = new HashSet<String>(paths.size());
		for (IPath path : paths)
		{
			try
			{
				// The paths we get are locations on disk. We can tell a module's name by looking for a path
				// that is a child of 'nod_modules', i.e. "/usr/local/lib/node_modules/alloy"
				int count = path.segmentCount();
				if (count >= 2 && NODE_MODULES.equals(path.segment(count - 2)))
				{
					installed.add(path.lastSegment());
				}
			}
			catch (Exception e)
			{
				// There is a chance that npm throw warnings if there are any partial installations
				// and npm might fail while trying to parse those warnings.
				if (!path.toOSString().contains("npm WARN")) //$NON-NLS-1$
				{
					throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, e.getMessage()));
				}
			}
		}
		return installed;
	}

	public boolean isInstalled(String packageName) throws CoreException
	{
		try
		{
			String version = getInstalledVersion(packageName);
			if (!StringUtil.isEmpty(version))
			{
				return true;
			}
		}
		catch (CoreException e)
		{
			IdeLog.logInfo(JSCorePlugin.getDefault(), MessageFormat.format(
					"Error getting the installed version of package {0}; falling back to use ''npm list''", //$NON-NLS-1$
					packageName));
		}
		Set<String> listing = list(false);
		return listing.contains(packageName);
	}

	public IPath getModulesPath(String packageName) throws CoreException
	{
		IStatus status = runInBackground(PARSEABLE_ARG, LIST, packageName);
		if (!status.isOK())
		{
			throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodePackageManager_FailedListPackageError, packageName)));
		}
		String message = status.getMessage();
		String[] lines = message.split("\n"); //$NON-NLS-1$
		return Path.fromOSString(lines[lines.length - 1]);
	}

	public String getInstalledVersion(String packageName) throws CoreException
	{
		return getInstalledVersion(packageName, false, LOCAL_NPM_INSTALL_PATH);
	}

	public String getInstalledVersion(String packageName, boolean global, IPath workingDir) throws CoreException
	{
		IPath npmPath = checkedNPMPath();
		List<String> args = CollectionsUtil.newList(npmPath.toOSString(), "ls", packageName, COLOR, FALSE); //$NON-NLS-1$
		if (global)
		{
			args.add(GLOBAL_ARG);
		}
		IStatus status = nodeJS.runInBackground(workingDir, ShellExecutable.getEnvironment(), args);
		if (!status.isOK())
		{
			throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodePackageManager_FailedToDetermineInstalledVersion, packageName)));
		}
		String output = status.getMessage();
		int index = output.indexOf(packageName + '@');
		if (index != -1)
		{
			output = output.substring(index + packageName.length() + 1);
			int space = output.indexOf(' ');
			if (space != -1)
			{
				output = output.substring(0, space);
			}
			return output;
		}
		return null;
	}

	public String getLatestVersionAvailable(String packageName) throws CoreException
	{
		// get the latest version
		// npm view titanium version
		IPath npmPath = checkedNPMPath();

		Map<String, String> env = ShellExecutable.getEnvironment();
		List<String> args = CollectionsUtil.newList(npmPath.toOSString(), "view", packageName, "version");//$NON-NLS-1$ //$NON-NLS-2$
		args.addAll(proxySettings(env));

		IStatus status = nodeJS.runInBackground(null, env, args);
		if (!status.isOK())
		{
			throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodePackageManager_FailedToDetermineLatestVersion, packageName)));
		}
		String message = status.getMessage().trim();
		Matcher m = VERSION_PATTERN.matcher(message);
		if (m.find())
		{
			return m.group(1);
		}
		return null;
	}

	public String getConfigValue(String key) throws CoreException
	{
		// npm config get <key>
		IStatus status = runInBackground(CONFIG, GET, key);
		if (!status.isOK())
		{
			throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodePackageManager_ConfigFailure, key)));
		}
		return status.getMessage().trim();
	}

	private IStatus runNpmInstaller(String packageName, String displayName, boolean global, char[] password,
			IPath workingDirectory, String command, IProgressMonitor monitor) throws CoreException, IOException,
			InterruptedException
	{
		SubMonitor sub = SubMonitor.convert(monitor,
				MessageFormat.format(Messages.NodePackageManager_InstallingTaskName, displayName), 100);
		try
		{
			List<String> args = getNpmSudoArgs(global);
			CollectionsUtil.addToList(args, command, packageName, COLOR, FALSE);

			Map<String, String> environment;
			if (PlatformUtil.isWindows())
			{
				environment = new HashMap<String, String>(System.getenv());
			}
			else
			{
				environment = ShellExecutable.getEnvironment();
			}
			args.addAll(proxySettings(environment));
			environment.put(ProcessUtil.REDIRECT_ERROR_STREAM, StringUtil.EMPTY);

			// HACK for TISTUD-4101
			if (PlatformUtil.isWindows())
			{
				IPath pythonExe = ExecutableUtil.find("pythonw.exe", false, null); //$NON-NLS-1$
				if (pythonExe == null)
				{
					// Add python to PATH
					Bundle bundle = Platform.getBundle("com.appcelerator.titanium.python.win32"); //$NON-NLS-1$
					if (bundle != null)
					{
						// Windows is wonderful, it sometimes stores in "Path" and "PATH" doesn't work
						String pathName = "PATH"; //$NON-NLS-1$
						if (!environment.containsKey(pathName))
						{
							pathName = "Path"; //$NON-NLS-1$
						}
						String path = environment.get(pathName);

						IPath relative = new Path("."); //$NON-NLS-1$
						URL bundleURL = FileLocator.find(bundle, relative, null);
						URL fileURL = FileLocator.toFileURL(bundleURL);
						File f = new File(fileURL.getPath());
						if (f.exists())
						{
							path = path + File.pathSeparator + new File(f, "python").getCanonicalPath(); //$NON-NLS-1$
							environment.put(pathName, path);
						}
					}
				}
			}

			return ProcessUtil.run(CollectionsUtil.getFirstElement(args), workingDirectory, password, environment,
					monitor, CollectionsUtil.toArray(args, 1, args.size()));
		}
		finally
		{
			sub.done();
		}
	}

	private List<String> getNpmSudoArgs(boolean global) throws CoreException
	{
		IPath npmPath = checkedNPMPath();
		List<String> args = new ArrayList<String>(8);
		if (global)
		{
			if (!PlatformUtil.isWindows())
			{
				args.add("sudo"); //$NON-NLS-1$
				args.add("-S"); //$NON-NLS-1$
				args.add("--"); //$NON-NLS-1$
			}
			args.add(nodeJS.getPath().toOSString());
			args.add(npmPath.toOSString());
			args.add(GLOBAL_ARG);
		}
		else
		{
			args.add(nodeJS.getPath().toOSString());
			args.add(npmPath.toOSString());
		}
		return args;
	}

	public IStatus uninstall(String packageName, String displayName, boolean global, char[] password,
			IProgressMonitor monitor) throws CoreException
	{
		try
		{
			IStatus status = runNpmInstaller(packageName, displayName, global, password, null, REMOVE, monitor);
			if (status.getSeverity() == IStatus.CANCEL)
			{
				return Status.OK_STATUS;
			}
			if (!status.isOK())
			{
				String message = status.getMessage();
				IdeLog.logError(JSCorePlugin.getDefault(),
						MessageFormat.format("Failed to uninstall {0}.\n{1}", packageName, message)); //$NON-NLS-1$
				return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
						Messages.NodePackageManager_FailedInstallError, packageName));
			}
			return status;
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, e.getMessage(), e);
		}

	}

	// When in global mode, executables are linked into {prefix}/bin on Unix, or directly into {prefix} on Windows.
	public IPath getBinariesPath() throws CoreException
	{
		IPath prefix = getConfigPrefixPath();
		if (prefix == null)
		{
			return null;
		}

		if (PlatformUtil.isWindows())
		{
			return prefix;
		}
		return prefix.append(BIN);
	}

	// Global installs on Unix systems go to {prefix}/lib/node_modules. Global installs on Windows go to
	// {prefix}/node_modules (that is, no lib folder.)
	public IPath getModulesPath() throws CoreException
	{
		IPath prefix = getConfigPrefixPath();
		if (prefix == null)
		{
			return null;
		}

		if (PlatformUtil.isWindows())
		{
			return prefix.append(NODE_MODULES);
		}
		return prefix.append(LIB).append(NODE_MODULES);
	}

	public synchronized IPath getConfigPrefixPath() throws CoreException
	{
		if (fConfigPrefixPath == null)
		{
			String npmConfigPrefixPath = ShellExecutable.getEnvironment().get(NPM_CONFIG_PREFIX);
			if (npmConfigPrefixPath != null)
			{
				fConfigPrefixPath = Path.fromOSString(npmConfigPrefixPath);
			}
			else
			{
				fConfigPrefixPath = Path.fromOSString(getConfigValue(PREFIX));
			}
		}
		return fConfigPrefixPath;
	}

	public IStatus cleanNpmCache(char[] password, boolean global, IProgressMonitor monitor) throws CoreException
	{
		List<String> args = getNpmSudoArgs(global);
		args.remove(GLOBAL_ARG);
		CollectionsUtil.addToList(args, "cache", "clean"); //$NON-NLS-1$ //$NON-NLS-2$
		IStatus status = ProcessUtil.run(CollectionsUtil.getFirstElement(args), null, password,
				ShellExecutable.getEnvironment(), monitor, CollectionsUtil.toArray(args, 1, args.size()));

		String cacheCleanOutput = status.getMessage();
		if (!status.isOK() || cacheCleanOutput.contains(NPM_ERROR))
		{
			return new Status(Status.ERROR, JSCorePlugin.PLUGIN_ID, cacheCleanOutput);
		}
		return status;
	}

	public String getVersion() throws CoreException
	{
		IStatus status = runInBackground("-v"); //$NON-NLS-1$
		if (!status.isOK())
		{
			throw new CoreException(status);
		}
		return status.getMessage();
	}

	public boolean exists()
	{
		return npmPath.toFile().isFile();
	}

	public IPath getPath()
	{
		return npmPath;
	}

	public IStatus runInBackground(String... args) throws CoreException
	{
		List<String> newArgs = CollectionsUtil.newList(args);
		newArgs.add(0, checkedNPMPath().toOSString());
		return nodeJS.runInBackground(LOCAL_NPM_INSTALL_PATH, null, newArgs);
	}

	public IPath findNpmPackagePath(String executableName, boolean appendExtension, List<IPath> searchLocations,
			FileFilter filter)
	{
		if (searchLocations == null)
		{
			searchLocations = new ArrayList<IPath>();
		}
		searchLocations.add(LOCAL_NPM_INSTALL_PATH.append("node_modules").append(".bin")); //$NON-NLS-1$ //$NON-NLS-2$
		return ExecutableUtil.find(executableName, true, searchLocations, filter);
	}
}
