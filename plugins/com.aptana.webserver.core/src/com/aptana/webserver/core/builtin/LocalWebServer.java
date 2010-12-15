/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

package com.aptana.webserver.core.builtin;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.DefaultServerIOEventDispatch;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.nio.protocol.BufferingHttpServiceHandler;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorException;
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
	private static final int SHUTDOWN_TIMEOUT = 2000;
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
		System.out.println(MessageFormat.format("Starting webserver at {0}:{1}", socketAddress.getAddress().getHostAddress(), Integer.toString(socketAddress.getPort()))); //$NON-NLS-1$
		HttpParams params = new BasicHttpParams();
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT)
			.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
			.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 16*1024)
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			WebServerCorePlugin.log(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println(MessageFormat.format("Stopped webserver at {0}:{1}", socketAddress.getAddress().getHostAddress(), Integer.toString(socketAddress.getPort()))); //$NON-NLS-1$
		}
	}

	private static EFSWebServerConfiguration createConfigurationForDocumentRoot(URI documentRoot) {
		EFSWebServerConfiguration configuration = new EFSWebServerConfiguration();
		configuration.setDocumentRoot(documentRoot);
		return configuration;
	}
}
