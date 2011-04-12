/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.webserver.core.builtin;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.DefaultServerIOEventDispatch;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.nio.protocol.BufferingHttpServiceHandler;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.SocketUtil;
import com.aptana.webserver.core.EFSWebServerConfiguration;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.core.preferences.WebServerPreferences;

/**
 * @author Max Stepanov
 *
 */
public class LocalWebServer {

	private static final int SOCKET_TIMEOUT = 10000;
	private static final long STARTUP_TIMEOUT = 5000;
	private static final long SHUTDOWN_TIMEOUT = 2000;
	private static final int SOCKET_BUFFER_SIZE = 16*1024;
	private static final int WORKER_COUNT = 2;
	
	private final EFSWebServerConfiguration configuration;
	private Thread thread;
	private ListeningIOReactor reactor;
	
	public LocalWebServer(URI documentRoot) throws CoreException {
		this(createConfigurationForDocumentRoot(documentRoot));
	}
	
	public LocalWebServer(EFSWebServerConfiguration configuration) throws CoreException {
		Assert.isLegal(configuration.getDocumentRoot() != null, "DocumentRoot should be set"); //$NON-NLS-1$
		this.configuration = configuration;
		InetAddress host = WebServerPreferences.getServerAddress();
		int[] portRange = WebServerPreferences.getPortRange();
		int port = SocketUtil.findFreePort(host, portRange[0], portRange[1]);
		if (port <= 0) {
			port = SocketUtil.findFreePort(host); // default to any free port
		}
		try {
			configuration.setBaseURL(new URL("http", host.getHostAddress(), port, "/")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (MalformedURLException e) {
			WebServerCorePlugin.log(e);
		}
		startServer(host, port, configuration);
	}
	
	/**
	 * Stop the server and dispose any resources associated with it
	 */
	public void dispose() {
		stopServer();
	}
	
	public EFSWebServerConfiguration getConfiguration() {
		return configuration;
	}
	
	private void startServer(final InetAddress host, final int port, final EFSWebServerConfiguration configuration) {
		thread = new Thread() {
			@Override
			public void run() {
				runServer(new InetSocketAddress(host, port), new LocalWebServerHttpRequestHandler(configuration));
			}
		};
		thread.setDaemon(true);
		thread.start();
		Thread.yield();
		long startTime = System.currentTimeMillis();
		while( thread.isAlive() && (System.currentTimeMillis() - startTime) < STARTUP_TIMEOUT ) {
			if (reactor != null && reactor.getStatus() == IOReactorStatus.ACTIVE) {
				break;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignore) {
			}
		}
	}
	
	private void stopServer() {
		if (thread != null && thread.isAlive()) {
			if (reactor != null) {
				try {
					reactor.shutdown(SHUTDOWN_TIMEOUT);
				} catch (IOException ignore) {
				}
			}
			thread.interrupt();
			try {
				thread.join(SHUTDOWN_TIMEOUT);
			} catch (InterruptedException ignore) {
			}
		}
	}
	
	private void runServer(InetSocketAddress socketAddress, HttpRequestHandler httpRequestHandler) {
		HttpParams params = new BasicHttpParams();
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT)
			.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
			.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, SOCKET_BUFFER_SIZE)
			.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
			.setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/"+EclipseUtil.getPluginVersion("org.apache.httpcomponents.httpcore")); //$NON-NLS-1$ //$NON-NLS-2$
		
		BasicHttpProcessor httpProcessor = new BasicHttpProcessor();
		httpProcessor.addInterceptor(new ResponseDate());
		httpProcessor.addInterceptor(new ResponseServer());
		httpProcessor.addInterceptor(new ResponseContent());
		httpProcessor.addInterceptor(new ResponseConnControl());

        HttpRequestHandlerRegistry handlerRegistry = new HttpRequestHandlerRegistry();
        handlerRegistry.register("*", httpRequestHandler); //$NON-NLS-1$

        BufferingHttpServiceHandler serviceHandler = new BufferingHttpServiceHandler(
                httpProcessor,
                new DefaultHttpResponseFactory(),
                new DefaultConnectionReuseStrategy(),
                params);
        serviceHandler.setHandlerResolver(handlerRegistry);
        serviceHandler.setEventListener(new LocalWebServerLogger());
        
        IOEventDispatch eventDispatch = new DefaultServerIOEventDispatch(serviceHandler, params);
        try {
			reactor = new DefaultListeningIOReactor(WORKER_COUNT, params);
			reactor.listen(socketAddress);
			reactor.execute(eventDispatch);
        } catch (InterruptedIOException e) {
        	return;
		} catch (IOReactorException e) {
			WebServerCorePlugin.log(e);
		} catch (IOException e) {
			WebServerCorePlugin.log(e);
		}
	}

	private static EFSWebServerConfiguration createConfigurationForDocumentRoot(URI documentRoot) {
		EFSWebServerConfiguration configuration = new EFSWebServerConfiguration();
		configuration.setDocumentRoot(documentRoot);
		return configuration;
	}
}
