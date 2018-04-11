/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable declareAsInterface

package com.aptana.filesystem.secureftp;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.epl.IMemento;
import com.aptana.core.io.vfs.IConnectionFileManager;
import com.aptana.filesystem.secureftp.internal.SFTPConnectionFileManager;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.ConnectionPoint;
import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * @author Max Stepanov
 */
public class SFTPConnectionPoint extends ConnectionPoint implements ISFTPConnectionPoint
{

	public static final String TYPE = TYPE_SFTP;

	private static final String ELEMENT_HOST = "host"; //$NON-NLS-1$
	private static final String ELEMENT_PORT = "port"; //$NON-NLS-1$
	private static final String ELEMENT_PATH = "path"; //$NON-NLS-1$
	private static final String ELEMENT_LOGIN = "login"; //$NON-NLS-1$
	private static final String ELEMENT_PRIVATE_KEY_FILE = "privateKeyFile"; //$NON-NLS-1$
	private static final String ELEMENT_TRANSFER_TYPE = "transferType"; //$NON-NLS-1$
	private static final String ELEMENT_ENCODING = "encoding"; //$NON-NLS-1$
	private static final String ELEMENT_COMPRESSION = "compression"; //$NON-NLS-1$

	private String host;
	private int port = ISFTPConstants.SFTP_PORT_DEFAULT;
	private IPath path = Path.ROOT;
	private String login = ""; //$NON-NLS-1$
	private IPath privateKeyFile;
	private char[] password;
	private String transferType = ISFTPConstants.TRANSFER_TYPE_AUTO;
	private String encoding = ISFTPConstants.ENCODING_DEFAULT;
	private String compression = ISFTPConstants.COMPRESSION_AUTO;

	private ISFTPConnectionFileManager connectionFileManager;

	/**
	 * Default constructor
	 */
	public SFTPConnectionPoint()
	{
		super(TYPE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#loadState(com.aptana.ide.core.io.epl.IMemento)
	 */
	@Override
	protected void loadState(IMemento memento)
	{
		super.loadState(memento);
		IMemento child = memento.getChild(ELEMENT_HOST);
		if (child != null)
		{
			host = child.getTextData();
		}
		child = memento.getChild(ELEMENT_PORT);
		if (child != null)
		{
			try
			{
				port = Integer.parseInt(child.getTextData());
			}
			catch (NumberFormatException ignore)
			{
				ignore.getCause();
			}
		}
		child = memento.getChild(ELEMENT_PATH);
		if (child != null)
		{
			String text = child.getTextData();
			if (text != null)
			{
				path = Path.fromPortableString(text);
			}
		}
		child = memento.getChild(ELEMENT_LOGIN);
		if (child != null)
		{
			login = child.getTextData();
		}
		child = memento.getChild(ELEMENT_PRIVATE_KEY_FILE);
		if (child != null)
		{
			String text = child.getTextData();
			if (text != null)
			{
				privateKeyFile = Path.fromPortableString(text);
			}
		}
		child = memento.getChild(ELEMENT_TRANSFER_TYPE);
		if (child != null)
		{
			transferType = child.getTextData();
		}
		child = memento.getChild(ELEMENT_ENCODING);
		if (child != null)
		{
			encoding = child.getTextData();
		}
		child = memento.getChild(ELEMENT_COMPRESSION);
		if (child != null)
		{
			compression = child.getTextData();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#saveState(com.aptana.ide.core.io.epl.IMemento)
	 */
	@Override
	protected void saveState(IMemento memento)
	{
		super.saveState(memento);
		memento.createChild(ELEMENT_HOST).putTextData(host);
		if (ISFTPConstants.SFTP_PORT_DEFAULT != port)
		{
			memento.createChild(ELEMENT_PORT).putTextData(Integer.toString(port));
		}
		if (!Path.ROOT.equals(path))
		{
			memento.createChild(ELEMENT_PATH).putTextData(path.toPortableString());
		}
		if (login.length() != 0)
		{
			memento.createChild(ELEMENT_LOGIN).putTextData(login);
		}
		if (privateKeyFile != null && !privateKeyFile.isEmpty())
		{
			memento.createChild(ELEMENT_PRIVATE_KEY_FILE).putTextData(privateKeyFile.toPortableString());
		}
		if (!ISFTPConstants.TRANSFER_TYPE_AUTO.equals(transferType))
		{
			memento.createChild(ELEMENT_TRANSFER_TYPE).putTextData(transferType);
		}
		if (!ISFTPConstants.ENCODING_DEFAULT.equals(encoding))
		{
			memento.createChild(ELEMENT_ENCODING).putTextData(encoding);
		}
		if (!ISFTPConstants.COMPRESSION_AUTO.equals(encoding))
		{
			memento.createChild(ELEMENT_COMPRESSION).putTextData(compression);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#getHost()
	 */
	public String getHost()
	{
		return host;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#setHost(java.lang.String)
	 */
	public void setHost(String host)
	{
		this.host = host;
		notifyChanged();
		resetConnectionFileManager();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#getPort()
	 */
	public int getPort()
	{
		return port;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#setPort(int)
	 */
	public void setPort(int port)
	{
		this.port = port;
		notifyChanged();
		resetConnectionFileManager();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#getPath()
	 */
	public IPath getPath()
	{
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#setPath(org.eclipse.core.runtime.IPath)
	 */
	public void setPath(IPath path)
	{
		this.path = path;
		notifyChanged();
		resetConnectionFileManager();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#getLogin()
	 */
	public String getLogin()
	{
		return login;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#setLogin(java.lang.String)
	 */
	public void setLogin(String login)
	{
		this.login = login;
		notifyChanged();
		resetConnectionFileManager();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#getPassword()
	 */
	public char[] getPassword()
	{
		return password;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.ftp.IBaseRemoteConnectionPoint#setPassword(char[])
	 */
	public void setPassword(char[] password)
	{
		this.password = password;
		notifyChanged();
		resetConnectionFileManager();
	}

	/**
	 * @return the transferType
	 */
	public String getTransferType()
	{
		return transferType;
	}

	/**
	 * @param transferType
	 *            the transferType to set
	 */
	public void setTransferType(String transferType)
	{
		this.transferType = transferType;
		notifyChanged();
		resetConnectionFileManager();
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding()
	{
		return encoding;
	}

	/**
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
		notifyChanged();
		resetConnectionFileManager();
	}

	/**
	 * @return the key file path
	 */
	public IPath getKeyFilePath()
	{
		return privateKeyFile;
	}

	/**
	 * @param keyFilePath
	 *            the key file path to set
	 */
	public void setKeyFilePath(IPath keyFilePath)
	{
		this.privateKeyFile = keyFilePath;
		notifyChanged();
		resetConnectionFileManager();
	}

	/**
	 * @return the compression
	 */
	public String getCompression()
	{
		return compression;
	}

	/**
	 * @param compression
	 *            the compression to set
	 */
	public void setCompression(String compression)
	{
		this.compression = compression;
		notifyChanged();
		resetConnectionFileManager();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#connect(boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void connect(boolean force, IProgressMonitor monitor) throws CoreException
	{
		if (!force && isConnected())
		{
			return;
		}
		ConnectionContext context = CoreIOPlugin.getConnectionContext(this);
		if (context != null)
		{
			CoreIOPlugin.setConnectionContext(connectionFileManager, context);
		}
		getConnectionFileManager().connect(monitor);
		super.connect(force, monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#disconnect(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void disconnect(IProgressMonitor monitor) throws CoreException
	{
		if (isConnected())
		{
			getConnectionFileManager().disconnect(monitor);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#isConnected()
	 */
	@Override
	public synchronized boolean isConnected()
	{
		return connectionFileManager != null && connectionFileManager.isConnected();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#canDisconnect()
	 */
	@Override
	public boolean canDisconnect()
	{
		return isConnected() && true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (IConnectionFileManager.class.equals(adapter))
		{
			return getConnectionFileManager();
		}
		return super.getAdapter(adapter);
	}

	private synchronized void resetConnectionFileManager()
	{
		connectionFileManager = null;
	}

	private synchronized IConnectionFileManager getConnectionFileManager()
	{
		if (connectionFileManager == null)
		{
			// find contributed first
			connectionFileManager = (ISFTPConnectionFileManager) super.getAdapter(ISFTPConnectionFileManager.class);
			if (connectionFileManager == null
					&& Platform.getAdapterManager().hasAdapter(this, ISFTPConnectionFileManager.class.getName()))
			{
				connectionFileManager = (ISFTPConnectionFileManager) Platform.getAdapterManager().loadAdapter(this,
						ISFTPConnectionFileManager.class.getName());
			}
			if (connectionFileManager == null)
			{
				connectionFileManager = new SFTPConnectionFileManager();
			}
			ConnectionContext context = CoreIOPlugin.getConnectionContext(this);
			if (context != null)
			{
				CoreIOPlugin.setConnectionContext(connectionFileManager, context);
			}
			IPath keyFilePath = (privateKeyFile != null && !privateKeyFile.isEmpty()) ? privateKeyFile : null;
			connectionFileManager.init(host, port, path, keyFilePath, login, password, transferType, encoding,
					compression);
		}
		return connectionFileManager;
	}
}
