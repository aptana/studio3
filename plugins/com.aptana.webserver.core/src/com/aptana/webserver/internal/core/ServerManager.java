/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryImport

package com.aptana.webserver.internal.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;

import com.aptana.core.IFilter;
import com.aptana.core.epl.IMemento;
import com.aptana.core.epl.XMLMemento;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.IServerChangeListener;
import com.aptana.webserver.core.IServerManager;
import com.aptana.webserver.core.IServerType;
import com.aptana.webserver.core.ServerChangeEvent;
import com.aptana.webserver.core.ServerChangeEvent.Kind;
import com.aptana.webserver.core.WebServerCorePlugin;

/**
 * @author Max Stepanov
 */
public final class ServerManager implements IServerManager
{

	public static final String STATE_FILENAME = "webservers"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_ID = WebServerCorePlugin.PLUGIN_ID + ".webServerTypes"; //$NON-NLS-1$
	private static final String TAG_TYPE = "type"; //$NON-NLS-1$
	public static final String ATT_ID = "id"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	public static final String ATT_NAME = "name"; //$NON-NLS-1$

	private static final String ELEMENT_ROOT = "servers"; //$NON-NLS-1$
	private static final String ELEMENT_SERVER = "server"; //$NON-NLS-1$
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$

	private Map<String, IConfigurationElement> configurationElements = new HashMap<String, IConfigurationElement>();
	private List<IServerType> types = new ArrayList<IServerType>();
	private List<IServer> serverConfigurations = Collections.synchronizedList(new ArrayList<IServer>());
	private List<IMemento> unresolvedElements = new ArrayList<IMemento>();
	private ListenerList listeners = new ListenerList(ListenerList.IDENTITY);

	public ServerManager()
	{
		readExtensionRegistry();
	}

	private void readExtensionRegistry()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
				EXTENSION_POINT_ID);
		for (int i = 0; i < elements.length; ++i)
		{
			readElement(elements[i], TAG_TYPE);
		}
	}

	private void readElement(IConfigurationElement element, String elementName)
	{
		if (!elementName.equals(element.getName()))
		{
			return;
		}
		if (TAG_TYPE.equals(element.getName()))
		{
			String id = element.getAttribute(ATT_ID);
			if (id == null || id.length() == 0)
			{
				return;
			}
			String name = element.getAttribute(ATT_NAME);
			if (name == null || name.length() == 0)
			{
				return;
			}
			String clazz = element.getAttribute(ATT_CLASS);
			if (clazz == null || clazz.length() == 0)
			{
				return;
			}
			configurationElements.put(id, element);
			types.add(new ServerType(id, name));
		}
	}

	/**
	 * loadState
	 * 
	 * @param path
	 */
	public void loadState(IPath path)
	{
		File file = path.toFile();
		if (file.exists())
		{
			serverConfigurations.clear();

			FileReader reader = null;
			try
			{
				reader = new FileReader(file);
				XMLMemento memento = XMLMemento.createReadRoot(reader);
				for (IMemento child : memento.getChildren(ELEMENT_SERVER))
				{
					IServer serverConfiguration = restoreServerConfiguration(child, null);
					if (serverConfiguration != null)
					{
						serverConfigurations.add(serverConfiguration);
					}
					else
					{
						unresolvedElements.add(child);
					}
				}
			}
			catch (IOException e)
			{
				IdeLog.logError(WebServerCorePlugin.getDefault(), e);
			}
			catch (CoreException e)
			{
				IdeLog.logError(WebServerCorePlugin.getDefault(), e);
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
						IdeLog.logError(WebServerCorePlugin.getDefault(), e);
					}
				}
			}
		}
	}

	/**
	 * saveState
	 * 
	 * @param path
	 */
	public void saveState(IPath path)
	{
		XMLMemento memento = XMLMemento.createWriteRoot(ELEMENT_ROOT);
		synchronized (serverConfigurations)
		{
			for (IServer serverConfiguration : serverConfigurations)
			{
				if (serverConfiguration.isPersistent()
						&& configurationElements.containsKey(serverConfiguration.getId()))
				{
					IMemento child = memento.createChild(ELEMENT_SERVER);
					child.putMemento(storeServerConfiguration(serverConfiguration));
				}
			}
		}
		synchronized (unresolvedElements)
		{
			for (IMemento child : unresolvedElements)
			{
				memento.copyChild(child);
			}
		}
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(path.toFile());
			memento.save(writer);
		}
		catch (IOException e)
		{
			IdeLog.logError(WebServerCorePlugin.getDefault(), e);
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
					IdeLog.logError(WebServerCorePlugin.getDefault(), e);
				}
			}
		}
		for (IServer serverConfiguration : serverConfigurations)
		{
			notifyListeners(Kind.UPDATED, serverConfiguration);
		}
	}

	public void fireServerChangeEvent(IServer server)
	{
		notifyListeners(Kind.UPDATED, server);
	}

	public List<IServerType> getServerTypes()
	{
		return Collections.unmodifiableList(types);
	}

	public IServer createServer(String typeId) throws CoreException
	{
		IServer serverConfiguration = null;
		IConfigurationElement element = configurationElements.get(typeId);
		if (element != null)
		{
			Object object = element.createExecutableExtension(ATT_CLASS);
			if (object instanceof IServer)
			{
				serverConfiguration = (IServer) object;
			}
		}
		return serverConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.webserver.core.IServerManager#addServerConfiguration(com.aptana.webserver.core.
	 * AbstractWebServerConfiguration)
	 */
	public void add(IServer serverConfiguration)
	{
		Assert.isLegal(serverConfiguration != null);
		if (!serverConfigurations.contains(serverConfiguration))
		{
			serverConfigurations.add(serverConfiguration);
			notifyListeners(Kind.ADDED, serverConfiguration);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.webserver.core.IServerManager#removeServerConfiguration(com.aptana.webserver.core.
	 * AbstractWebServerConfiguration)
	 */
	public void remove(IServer serverConfiguration)
	{
		if (serverConfigurations.remove(serverConfiguration))
		{
			notifyListeners(Kind.REMOVED, serverConfiguration);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.webserver.core.IServerManager#getServerConfigurations()
	 */
	public List<IServer> getServers()
	{
		return Collections.unmodifiableList(serverConfigurations);
	}

	public IServer findServerByName(final String name)
	{
		if (StringUtil.isEmpty(name))
		{
			return null;
		}
		IServer server = WebServerCorePlugin.getDefault().getBuiltinWebServer();
		if (name.equals(server.getName()))
		{
			return server;
		}

		List<IServer> matches = getServers(new IFilter<IServer>()
		{
			public boolean include(IServer item)
			{
				return name.equals(item.getName());
			}
		});
		if (matches.isEmpty())
		{
			return null;
		}
		return matches.get(0);
	}

	public List<IServer> getServers(IFilter<IServer> filter)
	{
		List<IServer> matches;
		synchronized (serverConfigurations)
		{
			matches = CollectionsUtil.filter(serverConfigurations, filter);
		}
		return Collections.unmodifiableList(matches);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.webserver.core.IServerManager#addServerConfigurationChangeListener(com.aptana.webserver.core.
	 * IServerConfigurationChangeListener)
	 */
	public void addServerChangeListener(IServerChangeListener listener)
	{
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.webserver.core.IServerManager#removeServerConfigurationChangeListener(com.aptana.webserver.core.
	 * IServerConfigurationChangeListener)
	 */
	public void removeServerChangeListener(IServerChangeListener listener)
	{
		listeners.remove(listener);
	}

	private IMemento storeServerConfiguration(IServer serverConfiguration)
	{
		IMemento saveMemento = XMLMemento.createWriteRoot(ELEMENT_ROOT).createChild(ELEMENT_SERVER);
		serverConfiguration.saveState(saveMemento);
		saveMemento.putString(ATTR_TYPE, serverConfiguration.getType().getId());
		return saveMemento;
	}

	private IServer restoreServerConfiguration(IMemento memento, String id) throws CoreException
	{
		IServer serverConfiguration = null;
		String typeId = memento.getString(ATTR_TYPE);
		if (typeId != null)
		{
			IConfigurationElement element = configurationElements.get(typeId);
			if (element != null)
			{
				Object object = element.createExecutableExtension(ATT_CLASS);
				if (object instanceof IServer)
				{
					serverConfiguration = (IServer) object;
					serverConfiguration.loadState(memento);
				}
			}
		}
		return serverConfiguration;
	}

	private void notifyListeners(Kind kind, IServer serverConfiguration)
	{
		final ServerChangeEvent event = new ServerChangeEvent(kind, serverConfiguration);
		for (Object i : listeners.getListeners())
		{
			final IServerChangeListener listener = (IServerChangeListener) i;
			SafeRunner.run(new ISafeRunnable()
			{
				public void run()
				{
					listener.configurationChanged(event);
				}

				public void handleException(Throwable exception)
				{
					IdeLog.logError(WebServerCorePlugin.getDefault(), exception);
				}
			});
		}
	}
}
