/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core.util;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IUniformResource;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.SocketUtil;
import com.aptana.debug.core.DebugCorePlugin;
import com.aptana.debug.core.IDebugScopes;

/**
 * @author Max Stepanov
 */
public final class DebugUtil
{
	private static final Set<String> LOCAL_HOSTS = CollectionsUtil.newSet("lo", "lo0", "localhost", "127.0.0.1"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
	private static final int DEFAULT_PORT = 8999;
	private static final int SOCKET_TIMEOUT = 30000;

	private DebugUtil()
	{
	}

	/**
	 * findAdapter
	 * 
	 * @param adaptableObject
	 * @param adapterType
	 * @return Object
	 */
	public static Object findAdapter(IAdaptable adaptableObject, Class<?> adapterType)
	{
		Object result = null;
		if (adaptableObject != null)
		{
			result = adaptableObject.getAdapter(adapterType);
			if (result == null)
			{
				ILaunch launch = (ILaunch) adaptableObject.getAdapter(ILaunch.class);
				if (launch != null)
				{
					for (IProcess process : launch.getProcesses())
					{
						result = process.getAdapter(adapterType);
						if (result != null)
						{
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
	public static String getPath(Object element)
	{
		if (element instanceof IUniformResource)
		{
			IUniformResource resource = (IUniformResource) element;
			IPath path = (IPath) resource.getAdapter(IPath.class);
			if (path == null)
			{
				IStorage storage = (IStorage) resource.getAdapter(IStorage.class);
				if (storage != null)
				{
					path = (IPath) storage.getAdapter(IPath.class);
				}
			}
			if (path != null)
			{
				return path.toOSString();
			}
			else
			{
				return resource.getURI().toString();
			}
		}
		if (element instanceof String)
		{
			try
			{
				element = new URI((String) element); // $codepro.audit.disable questionableAssignment
			}
			catch (URISyntaxException ignore)
			{
				ignore.getCause();
			}
		}
		if (element instanceof URI)
		{
			URI uri = (URI) element;
			if ("file".equals(uri.getScheme())) //$NON-NLS-1$
			{
				return uri.getSchemeSpecificPart();
			}
			return uri.toString();
		}
		return null;
	}

	public static int getDebuggerPort()
	{
		int port = SocketUtil.findFreePort(null);
		if (EclipseUtil.isDebugOptionEnabled((IDebugScopes.DEBUG)))
		{
			port = 2525;
		}
		if (port == -1)
		{
			port = DEFAULT_PORT;
		}
		return port;
	}

	public static ServerSocket allocateServerSocket(int port) throws IOException
	{
		ServerSocket socket = new ServerSocket(port);
		socket.setReuseAddress(true);
		if (!EclipseUtil.isDebugOptionEnabled((IDebugScopes.DEBUG)))
		{
			socket.setSoTimeout(SOCKET_TIMEOUT);
		}
		return socket;
	}

	/**
	 * Resolve and return the machine's IPs. The returned list does not hold the localhost.
	 * 
	 * @return The resolved IPs (all IPv4)
	 */
	public static List<String> getHostIPs()
	{
		List<String> hosts = new ArrayList<String>(5);
		try
		{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements())
			{
				NetworkInterface nextInterface = interfaces.nextElement();
				String interfaceName = nextInterface.getName();
				if (!LOCAL_HOSTS.contains(interfaceName.toLowerCase()))
				{
					Enumeration<InetAddress> inetAddresses = nextInterface.getInetAddresses();
					while (inetAddresses.hasMoreElements())
					{
						InetAddress address = inetAddresses.nextElement();
						if (!(address instanceof Inet6Address))
						{
							// We only collect IPv4 addressed.
							String addressString = address.toString();
							if (addressString.startsWith("/")) //$NON-NLS-1$
							{
								addressString = addressString.substring(1);
							}
							hosts.add(addressString);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(DebugCorePlugin.getDefault(), "Error resolving hosts", e); //$NON-NLS-1$
		}
		return hosts;
	}
}
