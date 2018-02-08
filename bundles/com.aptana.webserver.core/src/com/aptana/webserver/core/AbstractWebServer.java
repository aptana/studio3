/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions
package com.aptana.webserver.core;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;

import com.aptana.core.epl.IMemento;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.webserver.internal.core.ServerManager;
import com.aptana.webserver.internal.core.ServerType;

/**
 * TODO Merge with SimpleWebServer?
 * 
 * @author Max Stepanov
 */
public abstract class AbstractWebServer implements IExecutableExtension, IServer
{

	protected static final String ELEMENT_NAME = "name"; //$NON-NLS-1$

	private IServerType type;
	private String name;
	protected State fState;

	protected AbstractWebServer()
	{
		this.fState = State.STOPPED;
	}

	public void loadState(IMemento memento)
	{
		IMemento child = memento.getChild(ELEMENT_NAME);
		if (child != null)
		{
			name = child.getTextData();
		}
	}

	public void saveState(IMemento memento)
	{
		memento.createChild(ELEMENT_NAME).putTextData(name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.webserver.core.Iserver#isPersistent()
	 */
	public boolean isPersistent()
	{
		return true;
	}

	/*
	 * @see
	 * org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement
	 * , java.lang.String, java.lang.Object)
	 */
	public final void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		type = new ServerType(config.getAttribute(ServerManager.ATT_ID), config.getAttribute(ServerManager.ATT_NAME));
	}

	public final IServerType getType()
	{
		return type;
	}

	public final String getId()
	{
		return type == null ? null : type.getId();
	}

	public final String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name)
	{
		this.name = name;
	}

	public State getState()
	{
		return this.fState;
	}

	protected void updateState(State state)
	{
		if (ObjectUtil.areNotEqual(state, this.fState))
		{
			this.fState = state;
			fireServerChangedEvent();
		}
	}

	protected void fireServerChangedEvent()
	{
		ServerManager manager = (ServerManager) WebServerCorePlugin.getDefault().getServerManager();
		manager.fireServerChangeEvent(this);
	}

	/**
	 * Standard impl of restart justc alls stop and then start. Subclasses should override if there's a quicker
	 * implementation.
	 */
	public IStatus restart(String mode, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		IStatus status = stop(true, sub.newChild(30));
		if (!status.isOK())
		{
			return status;
		}
		return start(mode, sub.newChild(70));
	}

	public Set<String> getAvailableModes()
	{
		return CollectionsUtil.newSet(ILaunchManager.RUN_MODE);
	}

	public boolean canRestart()
	{
		return true;
	}

	public boolean canStart()
	{
		return true;
	}

	public boolean canStop()
	{
		return true;
	}

	public IStatus start(String mode, ILaunch launch, IProgressMonitor monitor)
	{
		return Status.OK_STATUS;
	}
}
