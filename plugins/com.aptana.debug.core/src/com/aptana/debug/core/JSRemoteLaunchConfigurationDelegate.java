/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.aptana.core.util.StringUtil;
import com.aptana.debug.internal.core.LocalResourceMapper;
import com.aptana.debug.internal.core.model.DebugConnection;
import com.aptana.debug.internal.core.model.HttpServerProcess;
import com.aptana.debug.internal.core.model.JSDebugProcess;
import com.aptana.debug.internal.core.model.JSDebugTarget;

/**
 * @author Max Stepanov
 */
public class JSRemoteLaunchConfigurationDelegate extends JSLaunchConfigurationDelegate {

	private static final Pattern HOST_PATTERN = Pattern
			.compile("^((\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(\\w+(\\.\\w+)*))(:(\\d{4,7}))?$"); //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.
	 * eclipse.debug.core.ILaunchConfiguration, java.lang.String,
	 * org.eclipse.debug.core.ILaunch,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		boolean debug = "debug".equals(mode); //$NON-NLS-1$

		String serverHost = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_HOST,
				(String) null);
		if (serverHost == null || !HOST_PATTERN.matcher(serverHost).matches()) {
			throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK, MessageFormat.format("Invalid server host", serverHost), null)); //$NON-NLS-1$
		}

		/* Initialize launch URL, optionally start local HTTP server */
		int serverType = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE,
				ILaunchConfigurationConstants.DEFAULT_SERVER_TYPE);
		int startActionType = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
				ILaunchConfigurationConstants.DEFAULT_START_ACTION_TYPE);

		LocalResourceMapper resourceMapper = null;
		HttpServerProcess httpServer = null;
		boolean launchHttpServer = false;
		URL baseURL = null;
		try {
			if (serverType == ILaunchConfigurationConstants.SERVER_INTERNAL) {
				if (startActionType != ILaunchConfigurationConstants.START_ACTION_START_URL) {
					launchHttpServer = true;
				} /* else => do not launch server for direct URLs */
			} else {
				baseURL = new URL(configuration.getAttribute(
						ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, StringUtil.EMPTY));
				resourceMapper = new LocalResourceMapper();
				resourceMapper.addMapping(baseURL, ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile());
			}
		} catch (MalformedURLException e) {
			throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
					Messages.JSLaunchConfigurationDelegate_MalformedServerURL, e));
		}

		try {
			URL launchURL = null;
			try {
				if (startActionType == ILaunchConfigurationConstants.START_ACTION_START_URL) {
					launchURL = new URL(configuration.getAttribute(
							ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_URL, StringUtil.EMPTY));
				} else {
					IResource resource = null;
					if (startActionType == ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE) {
						resource = getCurrentEditorResource();
					} else if (startActionType == ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE) {
						String resourcePath = configuration.getAttribute(
								ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, (String) null);
						if (resourcePath != null && resourcePath.length() > 0) {
							resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(resourcePath));
						}
					}
					if (resource != null) {
						if (baseURL == null && launchHttpServer) {
							monitor.subTask(Messages.JSLaunchConfigurationDelegate_LaunchingHTTPServer);
							/*
							IHttpServerProviderAdapter httpServerProvider = (IHttpServerProviderAdapter) getContributedAdapter(IHttpServerProviderAdapter.class);
							IServer server = null;
							if (httpServerProvider != null) {
								server = httpServerProvider.getServer(resource);
							}
							*/

							File root = resource.getProject().getLocation().toFile();
							/*if (server != null) {
								baseURL = new URL(server.getHost());
							} else */{
								httpServer = new HttpServerProcess(launch);
								httpServer.setServerRoot(root);
								baseURL = httpServer.getBaseURL();
							}

							resourceMapper = new LocalResourceMapper();
							resourceMapper.addMapping(baseURL, root);
							JSLaunchConfigurationHelper.setResourceMapping(configuration, baseURL, resourceMapper,
									httpServer);

							launchURL = new URL(baseURL, resource.getProjectRelativePath().makeRelative()
									.toPortableString());
						} else if (baseURL != null) {
							IProject project = resource.getProject();
							resourceMapper.addMapping(baseURL, project.getLocation().toFile());
							launchURL = new URL(baseURL, resource.getProjectRelativePath().makeRelative()
									.toPortableString());
						} else {
							launchURL = resource.getLocation().toFile().toURI().toURL();
						}
					} else if (startActionType == ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE) {
						IPath path = getCurrentEditorPath();
						if (path != null) {
							launchURL = path.toFile().toURI().toURL();
						} else {
							launchURL = getCurrentEditorURL();
						}
					}
				}

				if (launchURL == null) {
					throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
							Messages.JSLaunchConfigurationDelegate_LaunchURLNotDefined, null));
				}

				String httpGetQuery = configuration.getAttribute(
						ILaunchConfigurationConstants.CONFIGURATION_HTTP_GET_QUERY, StringUtil.EMPTY);
				if (httpGetQuery != null && httpGetQuery.length() > 0 && launchURL.getQuery() == null
						&& launchURL.getRef() == null) {
					if (httpGetQuery.charAt(0) != '?') {
						httpGetQuery = '?' + httpGetQuery;
					}
					launchURL = new URL(launchURL, launchURL.getFile() + httpGetQuery);
				}
			} catch (MalformedURLException e) {
				throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
						Messages.JSLaunchConfigurationDelegate_MalformedLaunchURL, e));
			}

			monitor.subTask(Messages.JSRemoteLaunchConfigurationDelegate_ConnectingServer);

			Socket socket = null;
			try {
				String host = serverHost;
				int port = DEFAULT_PORT;
				int index = serverHost.indexOf(':');
				if (index > 0) {
					host = serverHost.substring(0, index);
					try {
						port = Integer.parseInt(serverHost.substring(index + 1));
					} catch (NumberFormatException e) {
						port = 0;
					}
				}
				socket = new Socket();
				socket.connect(new InetSocketAddress(host, port), DebugConnection.SOCKET_TIMEOUT);
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
						Messages.JSLaunchConfigurationDelegate_SocketConnectionError, e));
			}

			monitor.subTask(Messages.JSLaunchConfigurationDelegate_InitializingDebugger);
			JSDebugTarget debugTarget = null;
			try {
				JSDebugProcess debugProcess = new JSDebugProcess(launch,
						Messages.JSRemoteLaunchConfigurationDelegate_Server, null);
				DebugConnection controller = DebugConnection.createConnection(socket);
				debugTarget = new JSDebugTarget(launch, debugProcess, httpServer, resourceMapper, controller, debug);
				monitor.subTask(MessageFormat.format(Messages.JSLaunchConfigurationDelegate_OpeningPage, launchURL));
				debugTarget.openURL(launchURL);
			} catch (CoreException e) {
				JSDebugPlugin.log(e);
				if (debugTarget != null) {
					debugTarget.terminate();
				} else {
					try {
						socket.close();
					} catch (IOException ignore) {
					}
				}
				throw e;
			}
		} catch (CoreException e) {
			/* Shutdown HTTP server on error if launched */
			if (httpServer != null) {
				launch.removeProcess(httpServer);
				try {
					httpServer.terminate();
				} catch (DebugException e1) {
					JSDebugPlugin.log(e1);
				}
			}
			throw e;
		}
	}

}
