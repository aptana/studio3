/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * @author Max Stepanov
 */
// TODO: rework using EFS
public abstract class UniformResourceStorage extends PlatformObject implements IStorage
{

	private long timestamp = -1;
	private long expires = -1;

	/**
	 * UniformResourceStorage
	 */
	protected UniformResourceStorage()
	{
		super();
	}

	/**
	 * getURI
	 * 
	 * @return URI
	 */
	public abstract URI getURI();

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		if (IUniformResource.class == adapter)
		{
			return new AbstractUniformResource()
			{
				/*
				 * (non-Javadoc)
				 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang .Class)
				 */
				public Object getAdapter(Class adapter)
				{
					if (IStorage.class == adapter)
					{
						return UniformResourceStorage.this;
					}
					return super.getAdapter(adapter);
				}

				/*
				 * (non-Javadoc)
				 * @see com.aptana.ide.core.resources.IUniformResource#getURI()
				 */
				public URI getURI()
				{
					return UniformResourceStorage.this.getURI();
				}

			};
		}

		return super.getAdapter(adapter);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof UniformResourceStorage && getURI().equals(((UniformResourceStorage) obj).getURI());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return getURI().hashCode();
	}

	/**
	 * isValid
	 * 
	 * @return boolean
	 */
	public boolean isValid()
	{
		if (timestamp == -1)
		{
			return false;
		}
		if (expires >= System.currentTimeMillis())
		{
			return true;
		}
		try
		{
			URLConnection connection = getURI().toURL().openConnection();
			if (connection instanceof HttpURLConnection)
			{
				connection.setIfModifiedSince(timestamp);
				((HttpURLConnection) connection).setRequestMethod("HEAD"); //$NON-NLS-1$
			}
			connection.connect();
			if (connection instanceof HttpURLConnection)
			{
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				long lastModified = httpConnection.getLastModified();
				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED
						|| (lastModified != 0 && timestamp >= lastModified))
				{
					expires = System.currentTimeMillis();
					long expiration = connection.getExpiration();
					long date = connection.getDate();
					if (expiration != 0 && date != 0 && expiration > date)
					{
						expires += (expiration - date);
					}
					else
					{
						expires += 10 * 1000; // 10 sec
					}
					return true;
				}
			}
		}
		catch (IOException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return false;
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#getContents()
	 */
	public InputStream getContents() throws CoreException
	{
		try
		{
			URLConnection connection = getURI().toURL().openConnection();
			connection.connect();
			expires = System.currentTimeMillis();
			long expiration = connection.getExpiration();
			long date = connection.getDate();
			if (expiration != 0 && date != 0 && expiration > date)
			{
				expires += (expiration - date);
			}
			else
			{
				expires += 10 * 1000; // 10 sec
			}
			timestamp = connection.getLastModified();
			return connection.getInputStream();
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.OK, "Open stream error", e)); //$NON-NLS-1$
		}
	}

	public boolean exists()
	{
		try
		{
			URLConnection connection = getURI().toURL().openConnection();
			if (connection instanceof HttpURLConnection)
			{
				((HttpURLConnection) connection).setRequestMethod("HEAD"); //$NON-NLS-1$
			}
			connection.connect();
			if (connection instanceof HttpURLConnection)
			{
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
				{
					return true;
				}
			}
		}
		catch (IOException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return false;
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#getFullPath()
	 */
	public IPath getFullPath()
	{
		return null;
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#getName()
	 */
	public String getName()
	{
		String name = getURI().getPath();
		if (name != null)
		{
			int index = name.lastIndexOf('/');
			if (index >= 0)
			{
				name = name.substring(index + 1);
			}
		}
		return name;
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#isReadOnly()
	 */
	public boolean isReadOnly()
	{
		return true;
	}
}
