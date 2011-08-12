/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.filesystem.ftp;

import java.net.URI;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.io.efs.VirtualFile;
import com.aptana.core.util.StringUtil;
import com.aptana.filesystem.ftp.internal.FTPConnectionFileManager;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;

public class FTPFileSystem extends FileSystem
{

	/**
	 * used to retain a cache of connection managers for the same host/username/password/port
	 */
	private static Map<String, IFTPConnectionFileManager> fgConnectionManagers = new WeakHashMap<String, IFTPConnectionFileManager>();

	public FTPFileSystem()
	{
	}

	@Override
	public IFileStore getStore(URI uri)
	{
		String host = uri.getHost();
		int port = uri.getPort();
		String path = uri.getPath();
		String userInfo = uri.getUserInfo();
		String login = StringUtil.EMPTY;
		char[] password = StringUtil.EMPTY.toCharArray();
		if (userInfo != null && userInfo.length() > 0)
		{
			if (userInfo.contains(":")) //$NON-NLS-1$
			{
				String[] parts = userInfo.split(":"); //$NON-NLS-1$
				login = parts[0];
				if (parts.length > 1)
				{
					password = parts[1].toCharArray();
				}
			}
			else
			{
				login = userInfo;
			}
		}
		// If no username, use anonymous
		if (login == null || login.length() == 0)
		{
			login = IFTPConstants.LOGIN_ANONYMOUS;
		}
		// if no port specified, use default FTP port.
		if (port == -1)
		{
			port = IFTPConstants.FTP_PORT_DEFAULT;
		}

		IFTPConnectionFileManager connectionFileManager = getConnectionFileManager(host, port, path, login, password);
		return new VirtualFile(connectionFileManager, uri, Path.fromPortableString(path));
	}

	/**
	 * @param host
	 * @param port
	 * @param path
	 * @param login
	 * @param password
	 * @return
	 */
	protected synchronized IFTPConnectionFileManager getConnectionFileManager(String host, int port, String path,
			String login, char[] password)
	{
		// Re-use the same connection file manager for the same host/username. keep it in a cache somewhere...
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(login);
		if (password != null && password.length > 0)
		{
			stringBuilder.append(':');
			stringBuilder.append(password);
		}
		stringBuilder.append('@');
		stringBuilder.append(host);
		stringBuilder.append(':');
		stringBuilder.append(port);
		String key = stringBuilder.toString();
		IFTPConnectionFileManager connectionFileManager = fgConnectionManagers.get(key);
		if (connectionFileManager == null)
		{
			connectionFileManager = (IFTPConnectionFileManager) super.getAdapter(IFTPConnectionFileManager.class);
			if (connectionFileManager == null
					&& Platform.getAdapterManager().hasAdapter(this, IFTPConnectionFileManager.class.getName()))
			{
				connectionFileManager = (IFTPConnectionFileManager) Platform.getAdapterManager().loadAdapter(this,
						IFTPConnectionFileManager.class.getName());
			}
			if (connectionFileManager == null)
			{
				connectionFileManager = new FTPConnectionFileManager();
			}
			ConnectionContext context = CoreIOPlugin.getConnectionContext(this);
			if (context == null)
			{
				context = new ConnectionContext();
			}
			// Don't prompt for password, just quickly connect
			context.put(ConnectionContext.NO_PASSWORD_PROMPT, true);
			context.put(ConnectionContext.QUICK_CONNECT, true);
			CoreIOPlugin.setConnectionContext(connectionFileManager, context);
			connectionFileManager.init(host, port, Path.ROOT, login, password, true,
					IFTPConstants.TRANSFER_TYPE_BINARY, IFTPConstants.ENCODING_DEFAULT, null);

			fgConnectionManagers.put(key, connectionFileManager);
		}

		return connectionFileManager;
	}

}
