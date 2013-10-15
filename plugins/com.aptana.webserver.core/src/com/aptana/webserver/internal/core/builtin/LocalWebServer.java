/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable disallowSleepInsideWhile
// $codepro.audit.disable disallowYieldUsage

package com.aptana.webserver.internal.core.builtin;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.DefaultHttpServerIODispatch;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.protocol.BasicAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestHandlerRegistry;
import org.apache.http.nio.protocol.HttpAsyncService;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.SocketUtil;
import com.aptana.webserver.core.SimpleWebServer;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.internal.core.preferences.WebServerPreferences;

/**
 * Local HTTP server used for preview, JS debugging?
 * 
 * @author Max Stepanov
 */
public class LocalWebServer extends SimpleWebServer
{

	private static final String NAME = "Built-in server"; //$NON-NLS-1$
	private static final int SOCKET_TIMEOUT = 10000;
	private static final long STARTUP_TIMEOUT = 10000;
	private static final long SHUTDOWN_TIMEOUT = 2000;
	private static final int SOCKET_BUFFER_SIZE = 16 * 1024; // $codepro.audit.disable
																// multiplicationOrDivisionByPowersOf2
	private static final int WORKER_COUNT = 2;

	private Thread thread;
	private ListeningIOReactor reactor;

	protected int port;
	private String hostName;
	private InetAddress host;

	public LocalWebServer(URI documentRoot)
	{
		this(WebServerPreferences.getServerAddress(), WebServerPreferences.getPortRange(), documentRoot);
	}

	public LocalWebServer(InetAddress host, int[] portRange, URI documentRoot)
	{
		super();
		Assert.isLegal(documentRoot != null, "DocumentRoot should be set"); //$NON-NLS-1$
		setDocumentRoot(documentRoot);
		setName(NAME);
		this.host = host;
		this.port = SocketUtil.findFreePort(host, portRange[0], portRange[1]);
		if (this.port <= 0)
		{
			this.port = SocketUtil.findFreePort(host); // default to any free port
		}
		try
		{
			this.hostName = host.getHostAddress();
			setBaseURL(new URL("http", hostName, port, "/")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(WebServerCorePlugin.getDefault(), e);
		}
	}

	private void testConnection(URL url) throws CoreException
	{
		CoreException exception = null;
		for (int trial = 0; trial < 3; ++trial)
		{
			try
			{
				URLConnection connection = url.openConnection(); // $codepro.audit.disable variableDeclaredInLoop
				connection.connect();
				connection.getContentType();
				return;
			}
			catch (IOException e)
			{
				exception = new CoreException(new Status(IStatus.ERROR, WebServerCorePlugin.PLUGIN_ID,
						"Testing WebServer connection failed", e)); //$NON-NLS-1$
			}
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}
		if (exception != null)
		{
			throw exception;
		}
	}

	private void startServer(final InetAddress host, final int port)
	{
		updateState(State.STARTING);
		thread = new Thread()
		{
			@Override
			public void run()
			{
				runServer(new InetSocketAddress(host, port), new BasicAsyncRequestHandler(
						new LocalWebServerHttpRequestHandler(LocalWebServer.this)));
			}
		};
		thread.setDaemon(true);
		thread.start();
		Thread.yield();
		long startTime = System.currentTimeMillis();
		while (thread.isAlive() && (System.currentTimeMillis() - startTime) < STARTUP_TIMEOUT)
		{
			if (reactor != null && reactor.getStatus() == IOReactorStatus.ACTIVE)
			{
				updateState(State.STARTED);
				break;
			}
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				updateState(State.UNKNOWN);
				break;
			}
		}
	}

	private void runServer(InetSocketAddress socketAddress, HttpAsyncRequestHandler httpRequestHandler)
	{
		HttpParams params = new BasicHttpParams();
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT)
				.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
				.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, SOCKET_BUFFER_SIZE)
				.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
				.setParameter(CoreProtocolPNames.ORIGIN_SERVER,
						"HttpComponents/" + EclipseUtil.getPluginVersion("org.apache.httpcomponents.httpcore")); //$NON-NLS-1$ //$NON-NLS-2$

		BasicHttpProcessor httpProcessor = new BasicHttpProcessor();
		httpProcessor.addInterceptor(new ResponseDate());
		httpProcessor.addInterceptor(new ResponseServer());
		httpProcessor.addInterceptor(new ResponseContent());
		httpProcessor.addInterceptor(new ResponseConnControl());

		HttpAsyncRequestHandlerRegistry handlerRegistry = new HttpAsyncRequestHandlerRegistry();
		handlerRegistry.register("*", httpRequestHandler); //$NON-NLS-1$

		HttpAsyncService serviceHandler = new HttpAsyncService(httpProcessor, new DefaultConnectionReuseStrategy(),
				handlerRegistry, params);
		// serviceHandler.setEventListener(new LocalWebServerLogger());

		IOReactorConfig config = new IOReactorConfig();
		config.setIoThreadCount(WORKER_COUNT);
		config.setConnectTimeout(SOCKET_TIMEOUT);
		config.setTcpNoDelay(true);
		config.setSoKeepalive(true);

		DefaultHttpServerIODispatch eventDispatch = new DefaultHttpServerIODispatch(serviceHandler, params);
		try
		{
			reactor = new DefaultListeningIOReactor(config);
			reactor.listen(socketAddress);
			reactor.execute(eventDispatch);
		}
		catch (InterruptedIOException e)
		{
			return;
		}
		catch (IOReactorException e)
		{
			IdeLog.logWarning(WebServerCorePlugin.getDefault(), e);
		}
		catch (IOException e)
		{
			IdeLog.logError(WebServerCorePlugin.getDefault(), e);
		}
	}

	public IStatus stop(boolean force, IProgressMonitor monitor)
	{
		if (thread != null && thread.isAlive())
		{
			if (reactor != null)
			{
				try
				{
					reactor.shutdown(SHUTDOWN_TIMEOUT);
				}
				catch (IOException ignore)
				{
					IdeLog.logWarning(WebServerCorePlugin.getDefault(),
							"An error occurred shutting down the built-in preview server", ignore); //$NON-NLS-1$
				}
			}
			thread.interrupt();
			try
			{
				thread.join(SHUTDOWN_TIMEOUT);
			}
			catch (InterruptedException e)
			{
				// ignore
			}
		}
		return Status.OK_STATUS;
	}

	public IStatus start(String mode, IProgressMonitor monitor)
	{
		if (!ILaunchManager.RUN_MODE.equals(mode))
		{
			return new Status(IStatus.ERROR, WebServerCorePlugin.PLUGIN_ID, Messages.LocalWebServer_ServerModeError);
		}
		try
		{
			startServer(host, port);
			testConnection(getBaseURL());
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	public boolean isPersistent()
	{
		return false;
	}

	public String getMode()
	{
		// We only support run mode...
		return ILaunchManager.RUN_MODE;
	}

	@Override
	public State getState()
	{
		return fState;
	}

	public ILaunch getLaunch()
	{
		// This server doesn't use launches.
		return null;
	}

	public IProcess[] getProcesses()
	{
		// We don't use launches or IProcesses.
		return new IProcess[0];
	}

	public InetAddress getHost()
	{
		return host;
	}

	public String getHostname()
	{
		return hostName;
	}

	public int getPort()
	{
		return port;
	}
}
