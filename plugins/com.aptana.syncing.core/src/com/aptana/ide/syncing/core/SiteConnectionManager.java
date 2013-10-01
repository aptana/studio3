/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.syncing.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ListenerList;

import com.aptana.core.epl.IMemento;
import com.aptana.core.epl.XMLMemento;
import com.aptana.core.logging.IdeLog;
import com.aptana.ide.syncing.core.events.ISiteConnectionListener;
import com.aptana.ide.syncing.core.events.SiteConnectionEvent;

/**
 * @author Max Stepanov
 */
/* package */final class SiteConnectionManager implements ISiteConnectionManager
{

	protected static final String STATE_FILENAME = "sites"; //$NON-NLS-1$

	private static final String ELEMENT_ROOT = "sites"; //$NON-NLS-1$
	private static final String ELEMENT_SITE = "site"; //$NON-NLS-1$

	private static SiteConnectionManager instance;

	private List<SiteConnection> connections = Collections.synchronizedList(new ArrayList<SiteConnection>());
	private boolean dirty = false;

	private ListenerList listeners = new ListenerList();

	/**
	 * 
	 */
	private SiteConnectionManager()
	{
	}

	public static SiteConnectionManager getInstance()
	{
		if (instance == null)
		{
			instance = new SiteConnectionManager();
		}
		return instance;
	}

	/**
	 * loadState
	 * 
	 * @param path
	 */
	/* package */void loadState(IPath path)
	{
		File file = path.toFile();
		if (file.exists())
		{
			connections.clear();

			addConnectionsFrom(path);
		}
	}

	/**
	 * saveState
	 * 
	 * @param path
	 */
	/* package */void saveState(IPath path)
	{
		XMLMemento memento = XMLMemento.createWriteRoot(ELEMENT_ROOT);
		synchronized (connections)
		{
			for (SiteConnection siteConnection : connections)
			{
				IMemento child = memento.createChild(ELEMENT_SITE);
				child.putMemento(storeConnection(siteConnection));
			}
		}
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(path.toFile());
			memento.save(writer);
			isChanged();
		}
		catch (IOException e)
		{
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

	public List<ISiteConnection> addConnectionsFrom(IPath path)
	{
		List<ISiteConnection> newConnections = readConnectionsFrom(path);
		for (ISiteConnection connection : newConnections)
		{
			connections.add((SiteConnection) connection);
		}

		return newConnections;
	}

	public List<ISiteConnection> readConnectionsFrom(IPath path)
	{
		List<ISiteConnection> newConnections = new ArrayList<ISiteConnection>();
		File file = path.toFile();
		if (file.exists())
		{
			FileReader reader = null;
			try
			{
				reader = new FileReader(file);
				XMLMemento memento = XMLMemento.createReadRoot(reader);
				for (IMemento child : memento.getChildren(ELEMENT_SITE))
				{
					SiteConnection siteConnection = restoreConnection(child);
					if (siteConnection != null && siteConnection.isValid())
					{
						newConnections.add(siteConnection);
					}
					else
					{
						IdeLog.logWarning(SyncingPlugin.getDefault(),
								"Failed to load the site connection due to either source or destination being invalid", //$NON-NLS-1$
								IDebugScopes.DEBUG);
					}
				}
			}
			catch (Exception e)
			{
				IdeLog.logError(SyncingPlugin.getDefault(), Messages.SiteConnectionManager_ERR_FailedToLoadConnections,
						e);
			}
			finally
			{
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException e)
					{
					}
				}
			}
		}
		return newConnections;
	}

	private IMemento storeConnection(SiteConnection siteConnection)
	{
		IMemento saveMemento = XMLMemento.createWriteRoot(ELEMENT_ROOT).createChild(ELEMENT_SITE);
		siteConnection.saveState(saveMemento);
		return saveMemento;
	}

	private SiteConnection restoreConnection(IMemento memento)
	{
		SiteConnection siteConnection = new SiteConnection();
		siteConnection.loadState(memento);
		return siteConnection;
	}

	/**
	 * isChanged
	 * 
	 * @return
	 */
	public boolean isChanged()
	{
		for (SiteConnection siteConnection : connections)
		{
			if (siteConnection.isChanged())
			{
				dirty = true;
			}
		}
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
	 * @see
	 * com.aptana.ide.syncing.core.ISiteConnectionManager#addSiteConnection(com.aptana.ide.syncing.core.ISiteConnection)
	 */
	public void addSiteConnection(ISiteConnection siteConnection)
	{
		if (!(siteConnection instanceof SiteConnection))
		{
			throw new IllegalArgumentException();
		}
		if (!connections.contains(siteConnection))
		{
			connections.add((SiteConnection) siteConnection);
			dirty = true;
			broadcastEvent(new SiteConnectionEvent(this, SiteConnectionEvent.POST_ADD, siteConnection));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ide.syncing.core.ISiteConnectionManager#removeSiteConnection(com.aptana.ide.syncing.core.ISiteConnection
	 * )
	 */
	public void removeSiteConnection(ISiteConnection siteConnection)
	{
		if (connections.contains(siteConnection))
		{
			connections.remove(siteConnection);
			dirty = true;
			broadcastEvent(new SiteConnectionEvent(this, SiteConnectionEvent.POST_DELETE, siteConnection));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ide.syncing.core.ISiteConnectionManager#siteConnectionChanged(com.aptana.ide.syncing.core.ISiteConnection
	 * )
	 */
	public void siteConnectionChanged(ISiteConnection siteConnection)
	{
		if (connections.contains(siteConnection))
		{
			dirty = true;
			broadcastEvent(new SiteConnectionEvent(this, SiteConnectionEvent.POST_CHANGE, siteConnection));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ide.syncing.core.ISiteConnectionManager#cloneSiteConnection(com.aptana.ide.syncing.core.ISiteConnection
	 * )
	 */
	public ISiteConnection cloneSiteConnection(ISiteConnection siteConnection) throws CoreException
	{
		if (!(siteConnection instanceof SiteConnection))
		{
			throw new IllegalArgumentException();
		}
		if (siteConnection == DefaultSiteConnection.getInstance())
		{
			// special handling for cloning the default site connection
			ISiteConnection clone = new SiteConnection();
			clone.setName(siteConnection.getName());
			clone.setSource(siteConnection.getSource());
			clone.setDestination(siteConnection.getDestination());
			return clone;
		}
		return restoreConnection(storeConnection((SiteConnection) siteConnection));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#createSiteConnection()
	 */
	public ISiteConnection createSiteConnection()
	{
		return new SiteConnection();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#getSiteConnections()
	 */
	public ISiteConnection[] getSiteConnections()
	{
		return connections.toArray(new ISiteConnection[connections.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#addListener(com.aptana.ide.syncing.core.events.
	 * ISiteConnectionListener)
	 */
	public void addListener(ISiteConnectionListener listener)
	{
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.syncing.core.ISiteConnectionManager#removeListener(com.aptana.ide.syncing.core.events.
	 * ISiteConnectionListener)
	 */
	public void removeListener(ISiteConnectionListener listener)
	{
		listeners.add(listener);
	}

	private void broadcastEvent(SiteConnectionEvent event)
	{
		final Object[] list = listeners.getListeners();
		for (Object listener : list)
		{
			((ISiteConnectionListener) listener).siteConnectionChanged(event);
		}
	}
}
