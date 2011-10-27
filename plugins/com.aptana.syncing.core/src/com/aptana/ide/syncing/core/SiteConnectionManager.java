/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.syncing.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ListenerList;

import com.aptana.core.epl.IMemento;
import com.aptana.core.epl.XMLMemento;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint15Constants;
import com.aptana.ide.core.io.IConnectionPointManager;
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
				}
			}
			catch (IOException e)
			{
				IdeLog.logError(SyncingPlugin.getDefault(), Messages.SiteConnectionManager_ERR_FailedToLoadConnections,
						e);
			}
			catch (CoreException e)
			{
				// could be an 1.5 exported file; try to migrate
				try
				{
					load15State(file);
				}
				catch (Exception e1)
				{
					IdeLog.logError(SyncingPlugin.getDefault(),
							Messages.SiteConnectionManager_ERR_FailedToLoadConnections, e1);
				}
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

	/**
	 * Migrating the connection settings in 1.5 to 2.0.
	 * 
	 * @param file
	 *            the settings file
	 * @throws IOException
	 */
	private void load15State(File file) throws IOException, CoreException
	{
		StringBuilder contents = new StringBuilder();

		BufferedReader input = null;
		try
		{
			input = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = input.readLine()) != null)
			{
				contents.append(line);
			}
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
				}
			}
		}

		String s = contents.toString();
		if (s.indexOf(IConnectionPoint15Constants.DELIMITER) < 0)
		{
			s = StringUtil.replace(s, "%%%%", IConnectionPoint15Constants.DELIMITER); //$NON-NLS-1$
			s = StringUtil.replace(s, "@@@@", IConnectionPoint15Constants.OBJ_DELIMITER); //$NON-NLS-1$
			s = StringUtil.replace(s, "~~~~", IConnectionPoint15Constants.SECTION_DELIMITER); //$NON-NLS-1$
			s = StringUtil.replace(s, "!!!!", IConnectionPoint15Constants.TYPE_DELIMITER); //$NON-NLS-1$
			s = StringUtil.replace(s, "}}}}", IConnectionPoint15Constants.FILE_DELIMITER); //$NON-NLS-1$
		}

		String[] sections = s.split(IConnectionPoint15Constants.SECTION_DELIMITER);
		if (sections.length > 0)
		{
			load15VirtualFileManagers(sections[0]);
		}

		if (sections.length > 1)
		{
			load15VirtualFileManagerSyncItems(sections[1]);
		}
	}

	private void load15VirtualFileManagers(String s) throws CoreException
	{
		Map<String, List<String>> dataTypes = new HashMap<String, List<String>>();

		String[] parts = s.split(IConnectionPoint15Constants.OBJ_DELIMITER);
		String[] itemParts;
		for (String item : parts)
		{
			itemParts = item.split(IConnectionPoint15Constants.TYPE_DELIMITER);

			if (itemParts.length == 2)
			{
				String type = itemParts[0];
				String data = itemParts[1];

				if ("null".equals(type)) { //$NON-NLS-1$
					continue;
				}

				List<String> list = dataTypes.get(type);
				if (list == null)
				{
					list = new ArrayList<String>();
					dataTypes.put(type, list);
				}
				list.add(data);
			}
		}

		Set<String> types = dataTypes.keySet();
		IConnectionPointManager manager = CoreIOPlugin.getConnectionPointManager();
		IConnectionPoint connectionPoint;
		for (String type : types)
		{
			List<String> connectionDatas = dataTypes.get(type);
			for (String connectionData : connectionDatas)
			{
				connectionPoint = manager.restore15ConnectionPoint(type, connectionData);
				if (connectionPoint != null)
				{
					manager.addConnectionPoint(connectionPoint);
				}
			}
		}
	}

	private void load15VirtualFileManagerSyncItems(String s)
	{
		String[] parts = s.split(IConnectionPoint15Constants.OBJ_DELIMITER);

		String[] itemParts;
		ISiteConnection connection;
		for (String item : parts)
		{
			itemParts = item.split(IConnectionPoint15Constants.TYPE_DELIMITER);

			if (itemParts.length == 2)
			{
				String type = itemParts[0];
				String data = itemParts[1];

				if ("null".equals(type)) { //$NON-NLS-1$
					continue;
				}

				connection = restore15Connection(data);
				if (connection != null && connection.getSource() != null && connection.getDestination() != null)
				{
					addSiteConnection(connection);
				}
			}
		}
	}

	private ISiteConnection restore15Connection(String data)
	{
		String[] args = data.split(IConnectionPoint15Constants.DELIMITER);

		if (args.length < 3)
		{
			return null;
		}

		ISiteConnection siteConnection = createSiteConnection();
		siteConnection.setName(args[0]);
		String sourceId = args[1];
		String destinationId = args[2];

		IConnectionPoint[] connectionPoints = CoreIOPlugin.getConnectionPointManager().getConnectionPoints();
		String id;
		for (IConnectionPoint connectionPoint : connectionPoints)
		{
			id = connectionPoint.getId();
			if (id.equals(sourceId))
			{
				siteConnection.setSource(connectionPoint);
			}
			else if (id.equals(destinationId))
			{
				siteConnection.setDestination(connectionPoint);
			}
		}

		return siteConnection;
	}
}
