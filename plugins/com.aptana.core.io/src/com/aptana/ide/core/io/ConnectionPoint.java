/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.ide.core.io;

import java.net.URI;
import java.util.UUID;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;

import com.aptana.core.epl.IMemento;
import com.aptana.core.io.vfs.VirtualConnectionManager;
import com.aptana.core.util.StringUtil;
import com.aptana.usage.FeatureEvent;
import com.aptana.usage.StudioAnalytics;

/**
 * Base class for all connection points
 * 
 * @author Max Stepanov
 */
public abstract class ConnectionPoint extends PlatformObject implements IConnectionPoint, IExecutableExtension
{

	private static final String ELEMENT_NAME = "name"; //$NON-NLS-1$

	private String id;
	private String type;
	private boolean dirty;

	private String name;

	/**
	 * 
	 */
	protected ConnectionPoint(String type)
	{
		this.type = type;
		setId(UUID.randomUUID().toString());
	}

	/**
	 * 
	 */
	protected ConnectionPoint()
	{
		this(StringUtil.EMPTY);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement
	 * , java.lang.String, java.lang.Object)
	 */
	public final void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		setType(config.getAttribute(ConnectionPointManager.ATT_ID));
	}

	protected boolean isPersistent()
	{
		return true;
	}

	protected void loadState(IMemento memento)
	{
		IMemento child = memento.getChild(ELEMENT_NAME);
		if (child != null)
		{
			name = child.getTextData();
		}
	}

	protected void saveState(IMemento memento)
	{
		memento.createChild(ELEMENT_NAME).putTextData(name);
	}

	/**
	 * @return the id
	 */
	public final String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public final void setId(String id)
	{
		this.id = id;
		VirtualConnectionManager.getInstance().register(this);
	}

	/**
	 * @return the type
	 */
	public final String getType()
	{
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	private final void setType(String type)
	{
		this.type = type;
	}

	protected final void notifyChanged()
	{
		dirty = true;
	}

	/* package */final boolean isChanged()
	{
		try
		{
			return dirty;
		}
		finally
		{
			dirty = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getName()
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
		notifyChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getRootURI()
	 */
	public URI getRootURI()
	{
		return VirtualConnectionManager.getInstance().getConnectionVirtualURI(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getRoot()
	 */
	public IFileStore getRoot() throws CoreException
	{
		return EFS.getStore(getRootURI());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#connect(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void connect(IProgressMonitor monitor) throws CoreException
	{
		connect(false, monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#connect(boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void connect(boolean force, IProgressMonitor monitor) throws CoreException
	{
		StudioAnalytics.getInstance().sendEvent(new FeatureEvent("remote.connect." + getType(), null));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#isConnected()
	 */
	public boolean isConnected()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#canDisconnect()
	 */
	public boolean canDisconnect()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#disconnect(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void disconnect(IProgressMonitor monitor) throws CoreException
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (IFileStore.class.equals(adapter))
		{
			try
			{
				return getRoot();
			}
			catch (CoreException e)
			{
				return null;
			}
		}
		return super.getAdapter(adapter);
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
