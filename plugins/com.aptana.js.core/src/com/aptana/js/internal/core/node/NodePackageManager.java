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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osgi.framework.Bundle;

import com.aptana.core.IMap;
import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.IProcessRunner;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessRunner;
import com.aptana.core.util.ProcessStatus;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.SudoManager;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;
import com.aptana.js.core.node.INodePackageManager;

/**
 * @author cwilliams
 */
public class NodePackageManager implements INodePackageManager
{

	private static final String TRUE = "true";

	private static final String JSON = "--json";

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
	private IPath npmPath;

	public NodePackageManager(INodeJS nodeJS)
	{
		this.nodeJS = nodeJS;
	}

	protected IPath findNPMOnPATH(IPath possible)
	{
		return ExecutableUtil.find(NPM, false, CollectionsUtil.newList(possible));
	}

	protected IProcessRunner getProcessRunner()
	{
		return new ProcessRunner();
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
			return getPath();
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
		return install(packageName, displayName, global, password, null, monitor);
	}

	public IStatus install(String packageName, String displayName, boolean global, char[] password,
			IPath workingDirectory, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 10);
		String globalPrefixPath = null;
		try
		{
			/*
			 * HACK for environments with npm config prefix value set : when sudo npm -g install command is used, the
			 * global prefix config value for the entire system overrides the global prefix value of the user. So, it
			 * always install into /usr/lib even though user set a custom value for NPM_CONFIG_PREFIX.
			 */
			sub.subTask("Checking global NPM prefix");
			IPath prefixPath = getConfigPrefixPath();
			if (prefixPath != null)
			{
				List<String> args = CollectionsUtil.newList(CONFIG, GET, PREFIX);
				// TODO: should cache this value as config prefix path ?
				IStatus npmStatus = runNpmConfig(args, password, global, workingDirectory, sub.newChild(1));
				if (npmStatus.isOK())
				{
					String prefix = npmStatus.getMessage();
					sub.subTask("Global NPM prefix is " + prefix);
					// If the sudo cache is timed out, then the password prompt and other details might appear in the
					// console. So we should strip them off to get the real npm prefix value.
					if (prefix.contains(SudoManager.PROMPT_MSG))
					{
						prefix = prefix.substring(prefix.indexOf(SudoManager.PROMPT_MSG)
								+ SudoManager.PROMPT_MSG.length());
					}

					// Set the global prefix path only if it is not the default value.
					if (!prefixPath.toOSString().equals(prefix))
					{
						sub.subTask("Global and user NPM prefix don't match, setting global prefix temporarily to: "
								+ prefixPath.toOSString());
						globalPrefixPath = prefix;
						setGlobalPrefixPath(password, workingDirectory, sub.newChild(1), prefixPath.toOSString());
					}
				}
				else
				{
					IdeLog.logWarning(JSCorePlugin.getDefault(),
							"Failed to get global prefix for NPM: " + npmStatus.getMessage());
				}
			}
			sub.setWorkRemaining(8);
			sub.subTask("Running npm install command");
			IStatus status = runNpmInstaller(packageName, displayName, global, password, workingDirectory, INSTALL,
					sub.newChild(6));
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
					if (lines.length > 0 && lines[lines.length - 1].contains(NPM_ERROR))
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
					sub.subTask("Resetting global NPM prefix");
					setGlobalPrefixPath(password, workingDirectory, sub.newChild(1), globalPrefixPath);
				}
				catch (CoreException e)
				{
					return e.getStatus();
				}
			}
			sub.done();
		}
	}

	private IStatus setGlobalPrefixPath(char[] password, IPath workingDirectory, IProgressMonitor monitor,
			String globalPrefixPath) throws CoreException
	{
		List<String> args = CollectionsUtil.newList(CONFIG, "set", PREFIX, globalPrefixPath); //$NON-NLS-1$
		return runNpmConfig(args, password, true, workingDirectory, monitor);
	}

	protected IStatus runNpmConfig(List<String> args, char[] password, boolean global, IPath workingDirectory,
			IProgressMonitor monitor) throws CoreException
	{
		List<String> sudoArgs = getNpmArguments(global, password);
		sudoArgs.addAll(args);
		return getProcessRunner().run(workingDirectory, ShellExecutable.getEnvironment(workingDirectory), password,
				sudoArgs, monitor);
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
			env.put(IProcessRunner.TEXT_TO_OBFUSCATE, password);
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

		String output;
		if (!status.isOK())
		{
			if (status.getCode() == 1 && status instanceof ProcessStatus)
			{
				ProcessStatus ps = (ProcessStatus) status;
				output = ps.getStdOut();
				// TODO What else can we do to validate that this output is OK?
			}
			else
			{
				throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
						Messages.NodePackageManager_FailedListingError, status)));
			}
		}
		else
		{
			output = status.getMessage();
		}

		// Need to parse the output!
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
				// that is a child of 'node_modules', i.e. "/usr/local/lib/node_modules/alloy"
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
			return !StringUtil.isEmpty(version); // Assume it's not installed if process returned OK, but had no entry
		}
		catch (CoreException e)
		{
			IdeLog.logInfo(JSCorePlugin.getDefault(), MessageFormat.format(
					"Error getting the installed version of package {0}; falling back to use ''npm list''", //$NON-NLS-1$
					packageName));
		}
		Set<String> listing = list(true);
		return listing.contains(packageName);
	}

	public IPath getModulesPath(String packageName) throws CoreException
	{
		IStatus status = runInBackground(PARSEABLE_ARG, LIST, packageName, GLOBAL_ARG);
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
		return getInstalledVersion(packageName, true, null);
	}

	public String getInstalledVersion(String packageName, boolean global, IPath workingDir) throws CoreException
	{
		IPath npmPath = checkedNPMPath();
		List<String> args = CollectionsUtil.newList(npmPath.toOSString(), "ls", packageName, COLOR, FALSE, JSON, TRUE); //$NON-NLS-1$
		if (global)
		{
			args.add(GLOBAL_ARG);
		}
		IStatus status = nodeJS.runInBackground(workingDir, ShellExecutable.getEnvironment(), args);
		if (!status.isOK())
		{
			throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodePackageManager_FailedToDetermineInstalledVersion, packageName, status.getMessage())));
		}
		try
		{
			String output = status.getMessage();
			JSONObject json = (JSONObject) new JSONParser().parse(output);
			if (!json.containsKey("dependencies"))
			{
				return null;
			}
			JSONObject dependencies = (JSONObject) json.get("dependencies");
			if (!dependencies.containsKey(packageName))
			{
				return null;
			}
			JSONObject pkg = (JSONObject) dependencies.get(packageName);
			return (String) pkg.get("version");
		}
		catch (ParseException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodePackageManager_FailedToDetermineInstalledVersion, packageName, e.getMessage())));
		}
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
					Messages.NodePackageManager_FailedToDetermineLatestVersion, packageName, status.getMessage())));
		}
		String message = status.getMessage().trim();
		Matcher m = VERSION_PATTERN.matcher(message);
		if (m.find())
		{
			return m.group(1);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String> getAvailableVersions(String packageName) throws CoreException
	{
		IPath npmPath = checkedNPMPath();

		Map<String, String> env = ShellExecutable.getEnvironment();
		List<String> args = CollectionsUtil.newList(npmPath.toOSString(),
				"view", packageName, "versions", COLOR, FALSE, JSON, TRUE);//$NON-NLS-1$ //$NON-NLS-2$
		args.addAll(proxySettings(env));

		IStatus status = nodeJS.runInBackground(null, env, args);
		if (!status.isOK())
		{
			throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodePackageManager_FailedToDetermineLatestVersion, packageName, status.getMessage())));
		}
		String message = status.getMessage().trim();
		try
		{
			return (List<String>) new JSONParser().parse(message);
		}
		catch (ParseException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, e.getMessage(), e));
		}
	}

	public String getConfigValue(String key) throws CoreException
	{
		// npm config get <key>
		IStatus status = runInBackground(CONFIG, GET, key);
		if (!status.isOK())
		{
			throw new CoreException(new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.NodePackageManager_ConfigFailure, key, status.getMessage())));
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
			List<String> args = getNpmArguments(global, password);
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
			environment.put(IProcessRunner.REDIRECT_ERROR_STREAM, StringUtil.EMPTY);

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

			return getProcessRunner().run(workingDirectory, environment, password, args, sub.newChild(100));
		}
		finally
		{
			sub.done();
		}
	}

	private List<String> getNpmArguments(boolean global, char[] sudoPassword) throws CoreException
	{
		IPath npmPath = checkedNPMPath();
		List<String> args = new ArrayList<String>(8);
		if (global)
		{
			SudoManager sudoMngr = new SudoManager();
			args.addAll(sudoMngr.getArguments(sudoPassword));
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
				String logMsg = MessageFormat.format("Failed to uninstall {0}.\n{1}", packageName, message); //$NON-NLS-1$
				IdeLog.logError(JSCorePlugin.getDefault(), logMsg);
				return new Status(IStatus.ERROR, JSCorePlugin.PLUGIN_ID, logMsg);
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
		// FIXME This caches the prefix value indefinitely. Is there any way to wipe the cache intelligently?
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

	public IStatus cleanNpmCache(char[] password, boolean runWithSudo, IProgressMonitor monitor)
	{
		List<String> args;
		try
		{
			args = getNpmArguments(runWithSudo, password);
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
		args.remove(GLOBAL_ARG);
		CollectionsUtil.addToList(args, "cache", "clean"); //$NON-NLS-1$ //$NON-NLS-2$
		String path = PlatformUtil.expandEnvironmentStrings("~"); //$NON-NLS-1$
		IPath userHome = Path.fromOSString(path);
		IStatus status = getProcessRunner().run(userHome, ShellExecutable.getEnvironment(), password, args, monitor);

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
		IPath path = getPath();
		if (path == null)
		{
			return false;
		}
		return path.toFile().isFile();
	}

	public synchronized IPath getPath()
	{
		if (npmPath == null)
		{
			IPath nodeParent = nodeJS.getPath().removeLastSegments(1);

			// Windows "npm" script is a sh script that tries to execute $basedir/node_modules/npm/bin/npm-cli.js under
			// "$basedir/node.exe" if it exists.
			// So for Windows, it would appear we'd need to run:
			// /path/to/node.exe /path/to/node/node_modules/npm/bin/npm-cli.js <args>
			if (PlatformUtil.isWindows())
			{
				npmPath = nodeParent.append(NODE_MODULES).append(NPM).append(BIN).append("npm-cli.js"); //$NON-NLS-1$
			}
			else
			{

				IPath possible = nodeParent.append(NPM);
				if (possible.toFile().exists())
				{
					// Typically node is co-located with npm (i.e. /usr/bin/npm and /usr/bin/node).
					npmPath = possible;
				}
				else
				{
					// However if installed from source they may live in separate locations.
					// So let's search the PATH for NPM
					npmPath = findNPMOnPATH(possible);
				}
			}
		}
		return npmPath;
	}

	public IStatus runInBackground(String... args) throws CoreException
	{
		List<String> newArgs = CollectionsUtil.newList(args);
		newArgs.add(0, checkedNPMPath().toOSString());
		return nodeJS.runInBackground(null, null, newArgs);
	}

	public IPath findNpmPackagePath(String executableName, boolean appendExtension, List<IPath> searchLocations,
			FileFilter filter)
	{
		return ExecutableUtil.find(executableName, true, searchLocations, filter);
	}
}
