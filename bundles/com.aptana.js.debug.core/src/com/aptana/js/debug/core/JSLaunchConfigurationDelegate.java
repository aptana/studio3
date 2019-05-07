/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import com.aptana.core.IURIMapper;
import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.URLEncoder;
import com.aptana.debug.core.IActiveResourcePathGetterAdapter;
import com.aptana.debug.core.internal.Util;
import com.aptana.debug.core.util.DebugUtil;
import com.aptana.js.debug.core.internal.ProtocolLogger;
import com.aptana.js.debug.core.internal.browsers.BrowserUtil;
import com.aptana.js.debug.core.internal.browsers.Firefox;
import com.aptana.js.debug.core.internal.browsers.InternetExplorer;
import com.aptana.js.debug.core.internal.model.DebugConnection;
import com.aptana.js.debug.core.internal.model.JSDebugProcess;
import com.aptana.js.debug.core.internal.model.JSDebugTarget;
import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.IServer.State;
import com.aptana.webserver.core.SimpleWebServer;
import com.aptana.webserver.core.URLtoURIMapper;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.core.WorkspaceResolvingURIMapper;

/**
 * @author Max Stepanov
 */
public class JSLaunchConfigurationDelegate extends LaunchConfigurationDelegate
{

	public static interface Listener
	{
		public String checkFirefoxLocation();
	}

	protected static final IStatus launchBrowserPromptStatus = new Status(IStatus.INFO, JSDebugPlugin.PLUGIN_ID, 302,
			StringUtil.EMPTY, null);

	private static Listener checkFirefoxLocationListener;

	public static void setCheckFirefoxLocationListener(Listener listener)
	{
		checkFirefoxLocationListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration,
	 * java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException
	{

		IStatusHandler prompter = DebugPlugin.getDefault().getStatusHandler(promptStatus);
		boolean debug = ILaunchManager.DEBUG_MODE.equals(mode);

		// TODO remove when multiple debug targets supported
		if (debug)
		{
			IDebugTarget activeSession = null;
			for (IDebugTarget target : DebugPlugin.getDefault().getLaunchManager().getDebugTargets())
			{
				if (IJSDebugConstants.ID_DEBUG_MODEL.equals(target.getModelIdentifier()))
				{
					if (!target.isTerminated())
					{
						activeSession = target;
						break;
					}
				}
			}
			if (activeSession != null)
			{
				Object result = prompter.handleStatus(launchBrowserPromptStatus, null);
				if ((result instanceof Boolean) && (((Boolean) result).booleanValue()))
				{
					activeSession.terminate();
					while (!monitor.isCanceled() && !activeSession.isTerminated())
					{
						try
						{
							Thread.sleep(250);
						}
						catch (InterruptedException ignore)
						{
						}
					}

				}
				else
				{
					String errorMessage = Messages.JSLaunchConfigurationDelegate_MultipleJavaScriptDebugNotSupported
							+ Messages.JSLaunchConfigurationDelegate_PleaseTerminateActiveSession;
					throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, Status.ERROR,
							errorMessage, null));
				}
			}
		}

		/* Check browser */
		String browserExecutable = configuration.getAttribute(
				ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE, (String) null);
		if (browserExecutable == null || !new File(browserExecutable).exists())
		{
			boolean found = false;
			String name = configuration.getName();
			if (!StringUtil.isEmpty(name) && name.startsWith(JSLaunchConfigurationHelper.FIREFOX))
			{
				if (checkFirefoxLocationListener == null)
				{
					ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
					JSLaunchConfigurationHelper.setBrowserDefaults(wc, null);
					browserExecutable = wc.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE,
							(String) null);
				}
				else
				{
					browserExecutable = checkFirefoxLocationListener.checkFirefoxLocation();
				}
				if (StringUtil.isEmpty(browserExecutable))
				{
					return;
				}
				if ((new File(browserExecutable)).exists())
				{
					found = true;
					ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
					wc.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE, browserExecutable);
					wc.doSave();
				}
			}
			if (!found)
			{
				throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
						MessageFormat.format(Messages.JSLaunchConfigurationDelegate_WebBrowserDoesNotExist,
								browserExecutable), null));
			}
		}

		boolean debugCompatible = BrowserUtil.isBrowserDebugCompatible(browserExecutable);
		boolean debugAvailable = false;
		boolean advancedRun = configuration.getAttribute(
				ILaunchConfigurationConstants.CONFIGURATION_ADVANCED_RUN_ENABLED, false);

		if (debugCompatible && (debug || advancedRun))
		{
			monitor.subTask(Messages.JSLaunchConfigurationDelegate_CheckingBrowserForDebugger);
			debugAvailable = BrowserUtil.isBrowserDebugAvailable(browserExecutable);
			if (!debugAvailable)
			{
				if (!BrowserUtil.installDebugExtension(browserExecutable, prompter, monitor))
				{
					monitor.setCanceled(true);
					return;
				}
				debugAvailable = BrowserUtil.isBrowserDebugAvailable(browserExecutable);
			}
			if (debug && !debugAvailable)
			{
				throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
						Messages.JSLaunchConfigurationDelegate_DebuggerExtensionNotInstalled, null));
			}
		}

		/* Initialize launch URL, optionally start local HTTP server */
		int serverType = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE,
				ILaunchConfigurationConstants.DEFAULT_SERVER_TYPE);
		int startActionType = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
				ILaunchConfigurationConstants.DEFAULT_START_ACTION_TYPE);
		boolean appendProjectName = configuration.getAttribute(
				ILaunchConfigurationConstants.CONFIGURATION_APPEND_PROJECT_NAME, false);

		URL launchURL = null;
		IURIMapper urlMapper = null;
		try
		{
			IResource startResource = null;
			IPath startPath = null;
			switch (startActionType)
			{
				case ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE:
					startResource = getCurrentEditorResource();
					if (startResource == null)
					{
						startPath = getCurrentEditorPath();
						if (startPath == null)
						{
							launchURL = getCurrentEditorURL();
							if (launchURL == null)
							{
								monitor.setCanceled(true);
								return;
							}
						}
					}
					break;
				case ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE:
					String resourcePath = configuration.getAttribute(
							ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, (String) null);
					if (resourcePath != null && resourcePath.length() > 0)
					{
						startResource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(resourcePath));
					}
					break;
				case ILaunchConfigurationConstants.START_ACTION_START_URL:
					launchURL = new URL(configuration.getAttribute(
							ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_URL, StringUtil.EMPTY));
					break;
			}

			if (startResource == null && startPath == null && launchURL == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
						Messages.JSLaunchConfigurationDelegate_LaunchURLNotDefined, null));
			}

			if (launchURL != null)
			{
				// XXX: temporary solution for IE
				if (launchURL.toExternalForm().endsWith(".js") && InternetExplorer.isBrowserExecutable(browserExecutable)) { //$NON-NLS-1$
					throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, Status.ERROR,
							Messages.JSLaunchConfigurationDelegate_Cannot_Debug_JS_File, null));
				}
			}
			else if (serverType == ILaunchConfigurationConstants.SERVER_INTERNAL)
			{
				IServer server = WebServerCorePlugin.getDefault().getBuiltinWebServer();
				((SimpleWebServer) server).setDocumentRoot(EFSUtils.getFileStore(
						ResourcesPlugin.getWorkspace().getRoot()).toURI());
				startServerIfNecessary(server, mode, monitor);
				urlMapper = server;
			}
			else if (serverType == ILaunchConfigurationConstants.SERVER_MANAGED)
			{
				String serverName = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_NAME,
						(String) null);
				if (serverName != null)
				{
					IServer server = WebServerCorePlugin.getDefault().getServerManager().findServerByName(serverName);
					startServerIfNecessary(server, mode, monitor);
					urlMapper = server;
				}
				if (urlMapper == null)
				{
					throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
							MessageFormat.format(Messages.JSLaunchConfigurationDelegate_ServerNotFound0_Error,
									serverName), null));
				}
				if (startResource != null)
				{
					urlMapper = new WorkspaceResolvingURIMapper(urlMapper);
				}
			}
			else if (serverType == ILaunchConfigurationConstants.SERVER_EXTERNAL)
			{
				String externalBaseUrl = configuration.getAttribute(
						ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, StringUtil.EMPTY).trim();
				if (StringUtil.isEmpty(externalBaseUrl))
				{
					throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
							Messages.JSLaunchConfigurationDelegate_Empty_URL, null));
				}
				if (externalBaseUrl.charAt(externalBaseUrl.length() - 1) != '/')
				{
					externalBaseUrl = externalBaseUrl + '/';
				}
				if (startResource != null)
				{
					IFileStore rootFileStore = EFSUtils.getFileStore(appendProjectName ? ResourcesPlugin.getWorkspace()
							.getRoot() : startResource.getProject());
					urlMapper = new URLtoURIMapper(new URL(externalBaseUrl), rootFileStore.toURI());
				}
			}
			else
			{
				throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
						Messages.JSLaunchConfigurationDelegate_No_Server_Type, null));
			}

			if (urlMapper != null)
			{
				if (startResource != null)
				{
					launchURL = Util.toURL(urlMapper.resolve(EFSUtils.getFileStore(startResource)));
				}
				else if (startPath != null)
				{
					launchURL = Util.toURL(urlMapper.resolve(EFSUtils.getLocalFileStore(startPath.toFile())));
					if (launchURL == null)
					{
						launchURL = startPath.toFile().toURI().toURL();
					}
				}
			}
			else if (launchURL == null && startPath != null)
			{
				launchURL = startPath.toFile().toURI().toURL();
			}

			if (launchURL == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
						Messages.JSLaunchConfigurationDelegate_LaunchURLNotDefined, null));
			}
			String httpGetQuery = configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_HTTP_GET_QUERY, StringUtil.EMPTY);
			if (httpGetQuery != null && httpGetQuery.length() > 0 && launchURL.getQuery() == null
					&& launchURL.getRef() == null)
			{
				if (httpGetQuery.charAt(0) != '?')
				{
					httpGetQuery = '?' + httpGetQuery;
				}
				launchURL = new URL(launchURL, launchURL.getFile() + httpGetQuery);
			}
			launchURL = new URL(launchURL, URLEncoder.encode(launchURL.getPath(), launchURL.getQuery(),
					launchURL.getRef()));
		}
		catch (MalformedURLException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
					Messages.JSLaunchConfigurationDelegate_MalformedServerURL, e));
		}

		monitor.subTask(Messages.JSLaunchConfigurationDelegate_LaunchingBrowser);
		Process process = null;
		ArrayList<String> browserArgs = new ArrayList<String>();
		String browserCmdLine = configuration.getAttribute(
				ILaunchConfigurationConstants.CONFIGURATION_BROWSER_COMMAND_LINE, StringUtil.EMPTY);
		if (browserCmdLine != null && browserCmdLine.length() > 0)
		{
			for (String arg : browserCmdLine.split(" ")) { //$NON-NLS-1$
				if (arg.trim().length() > 0)
				{
					browserArgs.add(arg.trim());
				}
			}
		}
		ArrayList<String> args = new ArrayList<String>();

		if (debugAvailable)
		{

			int port = DebugUtil.getDebuggerPort();
			ServerSocket listenSocket = null;
			try
			{
				listenSocket = DebugUtil.allocateServerSocket(port);
			}
			catch (IOException e)
			{
				throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
						Messages.JSLaunchConfigurationDelegate_SocketConnectionError, e));
			}

			String debuggerLaunchUrl = BrowserUtil.DEBUGGER_LAUNCH_URL + Integer.toString(port);
			try
			{
				if (Platform.OS_MACOSX.equals(Platform.getOS()))
				{
					args.add("/usr/bin/open"); //$NON-NLS-1$
					args.add("-b"); //$NON-NLS-1$
					args.add(BrowserUtil.getMacOSXApplicationIdentifier(browserExecutable));
					args.add(debuggerLaunchUrl);

				}
				else if (InternetExplorer.isBrowserExecutable(browserExecutable))
				{
					args.add(browserExecutable);
					args.add(debuggerLaunchUrl);
				}
				else
				{
					args.add(browserExecutable);
					args.add(debuggerLaunchUrl);
				}
				if ("true".equals(Platform.getDebugOption("com.aptana.debug.core/debugger_debug"))) { //$NON-NLS-1$ //$NON-NLS-2$
					args = null;
				}

				if (args != null)
				{
					args.addAll(browserArgs);
					process = Runtime.getRuntime().exec((String[]) args.toArray(new String[args.size()]));
				}
			}
			catch (IOException e)
			{
				if (listenSocket != null)
				{
					try
					{
						listenSocket.close();
					}
					catch (IOException ignore)
					{
					}
					listenSocket = null;
				}
				throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
						Messages.JSLaunchConfigurationDelegate_LaunchProcessError, e));
			}

			// TODO: use separate thread
			Socket socket = null;
			try
			{
				monitor.subTask(MessageFormat.format(Messages.JSLaunchConfigurationDelegate_OpeningSocketOnPort, port));
				socket = listenSocket.accept();
			}
			catch (IOException e)
			{
				BrowserUtil.resetBrowserCache(browserExecutable);
				if (debug && !monitor.isCanceled())
				{
					throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
							Messages.JSLaunchConfigurationDelegate_SocketConnectionError, e));
				}
			}
			finally
			{
				if (listenSocket != null)
				{
					try
					{
						listenSocket.close();
					}
					catch (IOException ignore)
					{
					}
				}
			}
			if (socket != null)
			{
				monitor.subTask(Messages.JSLaunchConfigurationDelegate_InitializingDebugger);
				JSDebugTarget debugTarget = null;
				try
				{
					JSDebugProcess debugProcess = new JSDebugProcess(launch, browserExecutable, null);
					DebugConnection controller = DebugConnection.createConnection(socket, new ProtocolLogger(
							"jsdebugger", JSDebugPlugin.PLUGIN_ID)); //$NON-NLS-1$
					debugTarget = new JSDebugTarget(launch, debugProcess, urlMapper, controller, mode);
					monitor.subTask(MessageFormat.format(Messages.JSLaunchConfigurationDelegate_OpeningPage, launchURL));
					debugTarget.openURL(launchURL);
				}
				catch (CoreException e)
				{
					JSDebugPlugin.log(e);
					if (debugTarget != null)
					{
						debugTarget.terminate();
					}
					else
					{
						try
						{
							socket.close();
						}
						catch (IOException ignore)
						{
						}
					}
					throw e;
				}
			}
			else
			{
				DebugPlugin.newProcess(launch, process, browserExecutable);
			}
		}
		else if (ILaunchManager.RUN_MODE.equals(mode))
		{
			try
			{
				String launchPage = launchURL.toExternalForm();
				if (Platform.OS_MACOSX.equals(Platform.getOS()))
				{
					args.add("/usr/bin/open"); //$NON-NLS-1$
					args.add("-b"); //$NON-NLS-1$
					args.add(BrowserUtil.getMacOSXApplicationIdentifier(browserExecutable));
					args.add(launchPage);
				}
				else
				{
					args.add(browserExecutable);
					if (debugCompatible && Firefox.isBrowserExecutable(browserExecutable))
					{
						if (advancedRun)
						{
							args.add(Firefox.NEW_WINDOW);
							browserArgs.remove(Firefox.NEW_WINDOW);
							browserArgs.remove(Firefox.NEW_TAB);
						}
						else
						{
							if (browserArgs.contains(Firefox.NEW_WINDOW))
							{
								args.add(Firefox.NEW_WINDOW);
							}
							else
							{
								args.add(Firefox.NEW_TAB);
							}
							browserArgs.remove(Firefox.NEW_WINDOW);
							browserArgs.remove(Firefox.NEW_TAB);
						}
					}
					args.add(launchPage);
				}

				args.addAll(browserArgs);
				process = Runtime.getRuntime().exec((String[]) args.toArray(new String[args.size()]));

			}
			catch (IOException e)
			{
				throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
						Messages.JSLaunchConfigurationDelegate_LaunchProcessError, e));
			}
			DebugPlugin.newProcess(launch, process, browserExecutable);

		}
		else
		{
			throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
					MessageFormat.format(
							Messages.JSLaunchConfigurationDelegate_ConfiguredBrowserDoesNotSupportDebugging,
							browserExecutable), null));
		}
	}

	protected void startServerIfNecessary(IServer server, String mode, IProgressMonitor monitor) throws CoreException
	{
		if (ILaunchManager.RUN_MODE.equals(mode) && server.getState() != State.STARTED
				&& server.getState() != State.STARTING)
		{
			IStatus result = server.start(mode, monitor);
			if (!result.isOK())
			{
				throw new CoreException(result);
			}
		}
	}

	protected IResource getCurrentEditorResource() throws MalformedURLException
	{
		IActiveResourcePathGetterAdapter adapter = (IActiveResourcePathGetterAdapter) getContributedAdapter(IActiveResourcePathGetterAdapter.class);
		if (adapter != null)
		{
			return adapter.getActiveResource();
		}
		return null;
	}

	protected IPath getCurrentEditorPath() throws MalformedURLException
	{
		IActiveResourcePathGetterAdapter adapter = (IActiveResourcePathGetterAdapter) getContributedAdapter(IActiveResourcePathGetterAdapter.class);
		if (adapter != null)
		{
			return adapter.getActiveResourcePath();
		}
		return null;
	}

	protected URL getCurrentEditorURL() throws MalformedURLException
	{
		IActiveResourcePathGetterAdapter adapter = (IActiveResourcePathGetterAdapter) getContributedAdapter(IActiveResourcePathGetterAdapter.class);
		if (adapter != null)
		{
			return adapter.getActiveResourceURL();
		}
		return null;
	}

	protected Object getContributedAdapter(Class<?> clazz)
	{
		Object adapter = null;
		IAdapterManager manager = Platform.getAdapterManager();
		if (manager.hasAdapter(this, clazz.getName()))
		{
			adapter = manager.getAdapter(this, clazz.getName());
			if (adapter == null)
			{
				adapter = manager.loadAdapter(this, clazz.getName());
			}
		}
		return adapter;
	}
}
