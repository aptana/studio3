/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.core.resources.IUniformResource;
import com.aptana.core.util.SocketUtil;

/**
 * @author Max Stepanov
 */
public final class DebugUtil {

	private static final int DEFAULT_PORT = 8999;

	private static final int SOCKET_TIMEOUT = 30000;

	private DebugUtil() {
	}

	/**
	 * findAdapter
	 * 
	 * @param adaptableObject
	 * @param adapterType
	 * @return Object
	 */
	public static Object findAdapter(IAdaptable adaptableObject, Class<?> adapterType) {
		Object result = null;
		if (adaptableObject != null) {
			result = adaptableObject.getAdapter(adapterType);
			if (result == null) {
				ILaunch launch = (ILaunch) adaptableObject.getAdapter(ILaunch.class);
				if (launch != null) {
					for (IProcess process : launch.getProcesses()) {
						result = process.getAdapter(adapterType);
						if (result != null) {
							break;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns raw path string for the provided element which could be an uniform resource, URI or plain String.
	 * 
	 * @param element
	 * @return path
	 */
	public static String getPath(Object element) {
		if (element instanceof IUniformResource) {
			IUniformResource resource = (IUniformResource) element;
			IPath path = (IPath) resource.getAdapter(IPath.class);
			if (path == null) {
				IStorage storage = (IStorage) resource.getAdapter(IStorage.class);
				if (storage != null) {
					path = (IPath) storage.getAdapter(IPath.class);
				}
			}
			if (path != null) {
				return path.toOSString();
			} else {
				return resource.getURI().toString();
			}
		}
		if (element instanceof String) {
			try {
				element = new URI((String) element); // $codepro.audit.disable questionableAssignment
			} catch (URISyntaxException ignore) {
				ignore.getCause();
			}
		}
		if (element instanceof URI) {
			URI uri = (URI) element;
			if ("file".equals(uri.getScheme())) //$NON-NLS-1$
			{
				return uri.getSchemeSpecificPart();
			}
			return uri.toString();
		}
		return null;
	}

	public static int getDebuggerPort() {
		int port = SocketUtil.findFreePort(null);
		if ("true".equals(Platform.getDebugOption("com.aptana.debug.core/debugger_debug"))) { //$NON-NLS-1$ //$NON-NLS-2$
			port = 2525;
		}
		if (port == -1) {
			port = DEFAULT_PORT;
		}
		return port;
	}

	public static ServerSocket allocateServerSocket(int port) throws IOException {
		ServerSocket socket = new ServerSocket(port);
		socket.setReuseAddress(true);
		if (!"true".equals(Platform.getDebugOption("com.aptana.debug.core/debugger_debug"))) { //$NON-NLS-1$ //$NON-NLS-2$
			socket.setSoTimeout(SOCKET_TIMEOUT);
		}
		return socket;
	}

}
