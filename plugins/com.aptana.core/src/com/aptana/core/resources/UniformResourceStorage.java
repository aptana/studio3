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

/**
 * @author Max Stepanov
 *
 */
public abstract class UniformResourceStorage extends PlatformObject implements IStorage {

	private long timestamp = -1;
	private long expires = -1;
	
	/**
	 * UniformResourceStorage
	 */
	protected UniformResourceStorage() {
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
	public Object getAdapter(Class adapter) {
		if ( IUniformResource.class == adapter )
		{
			return new AbstractUniformResource() {
				/* (non-Javadoc)
				 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
				 */
				public Object getAdapter(Class adapter) {
					if ( IStorage.class == adapter )
					{
						return UniformResourceStorage.this;
					}
					return super.getAdapter(adapter);
				}

				/* (non-Javadoc)
				 * @see com.aptana.ide.core.resources.IUniformResource#getURI()
				 */
				public URI getURI() {
					return UniformResourceStorage.this.getURI();
				}
			
			};
		}

		return super.getAdapter(adapter);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return obj instanceof UniformResourceStorage && getURI().equals(((UniformResourceStorage)obj).getURI());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getURI().hashCode();
	}

	/**
	 * isValid
	 * 
	 * @return boolean
	 */
	protected boolean isValid()
	{
		if (timestamp == -1) {
			return false;
		}
		if (expires >= System.currentTimeMillis()) {
			return true;
		}
		try {
			URLConnection connection = getURI().toURL().openConnection();
			if (connection instanceof HttpURLConnection) {
				connection.setIfModifiedSince(timestamp);
				((HttpURLConnection)connection).setRequestMethod("HEAD"); //$NON-NLS-1$
			}
			connection.connect();
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				long lastModified = httpConnection.getLastModified();
				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED
						|| (lastModified != 0 && timestamp >= lastModified)) {
					expires = System.currentTimeMillis();
					long expiration = connection.getExpiration();
					long date = connection.getDate();
					if (expiration != 0 && date != 0 && expiration > date) {
						expires += (expiration - date);
					} else {
						expires += 10*1000; // 10 sec
					}
					return true;
				}				
			}
		} catch (IOException e) {
			CorePlugin.log(e);
		}
		return false;
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#getContents()
	 */
	public InputStream getContents() throws CoreException {
		try {
			URLConnection connection = getURI().toURL().openConnection();
			connection.connect();
			expires = System.currentTimeMillis();
			long expiration = connection.getExpiration();
			long date = connection.getDate();
			if (expiration != 0 && date != 0 && expiration > date) {
				expires += (expiration - date);
			} else {
				expires += 10*1000; // 10 sec
			}
			timestamp = connection.getLastModified();
			return connection.getInputStream();
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.OK, "Open stream error", e)); //$NON-NLS-1$
		}
	}
	
	public boolean exists() {
		try {
			URLConnection connection = getURI().toURL().openConnection();
			if (connection instanceof HttpURLConnection) {
				((HttpURLConnection)connection).setRequestMethod("HEAD"); //$NON-NLS-1$
			}
			connection.connect();
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					return true;
				}				
			}
		} catch (IOException e) {
			CorePlugin.log(e);
		}
		return false;
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#getFullPath()
	 */
	public IPath getFullPath() {
		return null;
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#getName()
	 */
	public String getName() {
		String name = getURI().getPath();
		if(name != null)
		{
			int index = name.lastIndexOf('/');
			if ( index >= 0 )
			{
				name = name.substring(index+1);
			}
		}
		return name;
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#isReadOnly()
	 */
	public boolean isReadOnly() {
		return true;
	}
}
