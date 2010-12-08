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
package com.aptana.debug.internal.core.model;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.JSDebugPlugin;
import com.aptana.debug.internal.core.CompositeResourceResolver;

/**
 * @author Max Stepanov
 */
public class HttpServerProcess extends PlatformObject implements IProcess, IDebugEventSetListener {
	private ILaunch launch;
	private ILaunchListener launchListener;
	private IDebugTarget debugTarget;
	private String label;
	private Object server;
	private URL baseUrl;
	private CompositeResourceResolver resourceResolver;

	/**
	 * HttpServerProcess
	 * 
	 * @param launch
	 * @throws DebugException
	 */
	public HttpServerProcess(ILaunch launch) throws DebugException {
		super();
		this.launch = launch;

		this.launchListener = new ILaunchListener() {
			public void launchRemoved(ILaunch launch) {
				if (HttpServerProcess.this.launch == launch) {
					DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
				}
			}

			public void launchAdded(ILaunch launch) {
				if (!"run".equals(launch.getLaunchMode())) { //$NON-NLS-1$
					return;
				}
				/*
				 * Shutdown server on next launch if all other launch processes
				 * are terminated
				 */
				IProcess[] processes = HttpServerProcess.this.launch.getProcesses();
				boolean terminated = true;
				for (int i = 0; i < processes.length; ++i) {
					if (processes[i] != HttpServerProcess.this) {
						if (!processes[i].isTerminated()) {
							terminated = false;
							break;
						}
					}
				}
				if (terminated) {
					handleTerminate();
				}
			}

			public void launchChanged(ILaunch launch) {
			}
		};
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this.launchListener);

		/*
		String serverAddress = HttpServer.getServerAddress();
		int[] portRange = HttpServer.getPortRange();

		server = new HttpServer(new IHttpResourceResolver() {
			public IHttpResource getResource(RequestLineParser requestLine) throws HttpServerException {
				return resourceResolver.getResource(requestLine);
			}
		}, portRange[0], portRange[1]);

		try {
			server.start();
		} catch (IOException e) {
			throwDebugException(e);
		}
		try {
			baseUrl = new URL("http", serverAddress, server.getPort(), "/"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (MalformedURLException e) {
			JSDebugPlugin.log(e);
		}
		this.label = MessageFormat.format(Messages.HttpServerProcess_LocalHTTPServer_0, baseUrl.toExternalForm());
		*/

		launch.addProcess(this);
		fireCreationEvent();
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	/**
	 * getBaseURL
	 * 
	 * @return URL
	 */
	public URL getBaseURL() {
		return baseUrl;
	}

	/**
	 * setServerRoot
	 * 
	 * @param rootDir
	 */
	public void setServerRoot(File rootDir) {
		resourceResolver = new CompositeResourceResolver();
		resourceResolver.addPath("/", rootDir); //$NON-NLS-1$
	}

	/**
	 * setResourceResolver
	 * 
	 * @param resolver
	 */
	public void addServerPath(String path, File dir) {
		resourceResolver.addPath(path, dir);
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getLaunch()
	 */
	public ILaunch getLaunch() {
		return launch;
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getStreamsProxy()
	 */
	public IStreamsProxy getStreamsProxy() {
		return null;
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#setAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public void setAttribute(String key, String value) {
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getAttribute(java.lang.String)
	 */
	public String getAttribute(String key) {
		return null;
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getExitValue()
	 */
	public int getExitValue() throws DebugException {
		return 0;
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return !isTerminated();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return server == null;
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		try {
			shutdownServer();
		} finally {
			fireTerminateEvent();
		}
	}

	private void shutdownServer() throws DebugException {
		if (server == null) {
			return;
		}
		/*
		try {
			server.stop();
		} catch (IOException e) {
			throwDebugException(e);
		} finally {
			server = null;
		}
		*/
	}

	/**
	 * fireCreationEvent
	 */
	protected void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	/**
	 * fireTerminateEvent
	 */
	protected void fireTerminateEvent() {
		if (DebugPlugin.getDefault() != null) {
			fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
		}
	}

	/**
	 * fireEvent
	 * 
	 * @param event
	 */
	protected void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}

	/**
	 * throwDebugException
	 * 
	 * @param exception
	 * @throws DebugException
	 */
	protected void throwDebugException(Exception exception) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID,
				DebugException.REQUEST_FAILED, StringUtil.EMPTY, exception));
	}

	/**
	 * setDebugTarget
	 * 
	 * @param debugTarget
	 */
	protected void setDebugTarget(IDebugTarget debugTarget) {
		this.debugTarget = debugTarget;
	}

	/**
	 * getDebugTarget
	 * 
	 * @return IDebugTarget
	 */
	private IDebugTarget getDebugTarget() {
		if (debugTarget == null) {
			debugTarget = launch.getDebugTarget();
		}
		return debugTarget;
	}

	/**
	 * handleTerminate
	 */
	private void handleTerminate() {
		try {
			shutdownServer();
		} catch (DebugException e) {
			JSDebugPlugin.log(e);
		}
		fireTerminateEvent();
		DebugPlugin.getDefault().removeDebugEventListener(this);
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this.launchListener);
	}

	/**
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			DebugEvent event = events[i];
			switch (event.getKind()) {
			case DebugEvent.TERMINATE: {
				Object source = event.getSource();
				if (source.equals(getDebugTarget())) {
					handleTerminate();
				}
				break;
			}
			default:
			}
		}
	}
}
