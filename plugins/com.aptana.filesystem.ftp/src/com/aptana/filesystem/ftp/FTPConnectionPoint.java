/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable declareAsInterface
// $codepro.audit.disable emptyCatchClause

package com.aptana.filesystem.ftp;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.epl.IMemento;
import com.aptana.core.io.vfs.IConnectionFileManager;
import com.aptana.filesystem.ftp.internal.FTPConnectionFileManager;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.ConnectionPoint;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint15Constants;

/**
 * @author Max Stepanov
 */
public class FTPConnectionPoint extends ConnectionPoint implements IBaseFTPConnectionPoint
{

	public static final String TYPE = TYPE_FTP;

	private static final String ELEMENT_HOST = "host"; //$NON-NLS-1$
	private static final String ELEMENT_PORT = "port"; //$NON-NLS-1$
	private static final String ELEMENT_PATH = "path"; //$NON-NLS-1$
	private static final String ELEMENT_LOGIN = "login"; //$NON-NLS-1$
	private static final String ELEMENT_PASSIVE = "passive"; //$NON-NLS-1$
	private static final String ELEMENT_TRANSFER_TYPE = "transferType"; //$NON-NLS-1$
	private static final String ELEMENT_ENCODING = "encoding"; //$NON-NLS-1$
	private static final String ELEMENT_TIMEZONE = "timezone"; //$NON-NLS-1$

	private String host;
	private int port = IFTPConstants.FTP_PORT_DEFAULT;
	private IPath path = Path.ROOT;
	private String login = ""; //$NON-NLS-1$
	private char[] password;
	private boolean passiveMode = true;
	private String transferType = IFTPConstants.TRANSFER_TYPE_AUTO;
	private String encoding = IFTPConstants.ENCODING_DEFAULT;
	private String timezone = null;

	private IFTPConnectionFileManager connectionFileManager;

	/**
	 * Default constructor
	 */
	public FTPConnectionPoint()
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
			catch (NumberFormatException e)
			{
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
		child = memento.getChild(ELEMENT_PASSIVE);
		if (child != null)
		{
			passiveMode = Boolean.parseBoolean(child.getTextData());
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
		child = memento.getChild(ELEMENT_TIMEZONE);
		if (child != null)
		{
			timezone = child.getTextData();
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
		if (IFTPConstants.FTP_PORT_DEFAULT != port)
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
		memento.createChild(ELEMENT_PASSIVE).putTextData(Boolean.toString(passiveMode));
		if (!IFTPConstants.TRANSFER_TYPE_AUTO.equals(transferType))
		{
			memento.createChild(ELEMENT_TRANSFER_TYPE).putTextData(transferType);
		}
		if (!IFTPConstants.ENCODING_DEFAULT.equals(encoding))
		{
			memento.createChild(ELEMENT_ENCODING).putTextData(encoding);
		}
		if (timezone != null && timezone.length() != 0)
		{
			memento.createChild(ELEMENT_TIMEZONE).putTextData(timezone);
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
		this.path = path.isEmpty() ? Path.ROOT : path;
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
	 * @return the passiveMode
	 */
	public boolean isPassiveMode()
	{
		return passiveMode;
	}

	/**
	 * @param passiveMode
	 *            the passiveMode to set
	 */
	public void setPassiveMode(boolean passiveMode)
	{
		this.passiveMode = passiveMode;
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
	 * @return the timezone
	 */
	public String getTimezone()
	{
		return timezone;
	}

	/**
	 * @param timezone
	 *            the timezone to set
	 */
	public void setTimezone(String timezone)
	{
		this.timezone = timezone;
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
			if (context != null)
			{
				CoreIOPlugin.setConnectionContext(connectionFileManager, context);
			}
			connectionFileManager
					.init(host, port, path, login, password, passiveMode, transferType, encoding, timezone);
		}
		return connectionFileManager;
	}

	@Override
	public boolean load15Data(String data)
	{
		String[] items = data.split(IConnectionPoint15Constants.DELIMITER);

		if (items.length < 7)
		{
			return false;
		}

		setName(items[0]);
		setHost(items[1]);
		if (items[2] == null || items[2].equals("")) { //$NON-NLS-1$
			setPath(Path.ROOT);
		}
		else
		{
			setPath(new Path(items[2]));
		}
		setLogin(items[3]);
		setPassword(items[4].toCharArray());
		setPassiveMode(items[5].equals(Boolean.TRUE.toString()));
		setId(items[6]);

		if (items.length >= 10)
		{
			setPort(Integer.parseInt(items[9]));
		}
		return true;
	}
}
