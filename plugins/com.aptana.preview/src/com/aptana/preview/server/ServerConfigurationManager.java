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

package com.aptana.preview.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.epl.IMemento;
import com.aptana.core.epl.XMLMemento;
import com.aptana.preview.Activator;

/**
 * @author Max Stepanov
 * 
 */
public final class ServerConfigurationManager {

	public static final String STATE_FILENAME = "webservers"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_ID = Activator.PLUGIN_ID + ".webServerTypes"; //$NON-NLS-1$
	private static final String TAG_TYPE = "type"; //$NON-NLS-1$
	protected static final String ATT_ID = "id"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_NAME = "name"; //$NON-NLS-1$

	private static final String ELEMENT_ROOT = "servers"; //$NON-NLS-1$
	private static final String ELEMENT_SERVER = "server"; //$NON-NLS-1$
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$

	private static ServerConfigurationManager instance;
	private Map<String, IConfigurationElement> configurationElements = new HashMap<String, IConfigurationElement>();
	private List<ConfigurationType> types = new ArrayList<ConfigurationType>();
	private List<AbstractWebServerConfiguration> serverConfigurations = Collections
			.synchronizedList(new ArrayList<AbstractWebServerConfiguration>());
	private List<IMemento> unresolvedElements = new ArrayList<IMemento>();

	public final class ConfigurationType {
		private String id;
		private String name;

		private ConfigurationType(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * 
	 */
	private ServerConfigurationManager() {
		readExtensionRegistry();
	}

	public static ServerConfigurationManager getInstance() {
		if (instance == null) {
			instance = new ServerConfigurationManager();
		}
		return instance;
	}

	private void readExtensionRegistry() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
				EXTENSION_POINT_ID);
		for (int i = 0; i < elements.length; ++i) {
			readElement(elements[i], TAG_TYPE);
		}
	}

	private void readElement(IConfigurationElement element, String elementName) {
		if (!elementName.equals(element.getName())) {
			return;
		}
		if (TAG_TYPE.equals(element.getName())) {
			String id = element.getAttribute(ATT_ID);
			if (id == null || id.length() == 0) {
				return;
			}
			String name = element.getAttribute(ATT_NAME);
			if (name == null || name.length() == 0) {
				return;
			}
			String clazz = element.getAttribute(ATT_CLASS);
			if (clazz == null || clazz.length() == 0) {
				return;
			}
			configurationElements.put(id, element);
			types.add(new ConfigurationType(id, name));
		}
	}

	/**
	 * loadState
	 * 
	 * @param path
	 */
	public void loadState(IPath path) {
		File file = path.toFile();
		if (file.exists()) {
			serverConfigurations.clear();

			FileReader reader = null;
			try {
				reader = new FileReader(file);
				XMLMemento memento = XMLMemento.createReadRoot(reader);
				for (IMemento child : memento.getChildren(ELEMENT_SERVER)) {
					AbstractWebServerConfiguration serverConfiguration = restoreServerConfiguration(child, null);
					if (serverConfiguration != null) {
						serverConfigurations.add(serverConfiguration);
					} else {
						unresolvedElements.add(child);
					}
				}
			} catch (IOException e) {
			} catch (CoreException e) {
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
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
	public void saveState(IPath path) {
		XMLMemento memento = XMLMemento.createWriteRoot(ELEMENT_ROOT);
		synchronized (serverConfigurations) {
			for (AbstractWebServerConfiguration serverConfiguration : serverConfigurations) {
				if (serverConfiguration.isPersistent()
						&& configurationElements.containsKey(serverConfiguration.getType())) {
					IMemento child = memento.createChild(ELEMENT_SERVER);
					child.putMemento(storeServerConfiguration(serverConfiguration));
				}
			}
		}
		synchronized (unresolvedElements) {
			for (IMemento child : unresolvedElements) {
				memento.copyChild(child);
			}
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(path.toFile());
			memento.save(writer);
		} catch (IOException e) {
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public List<ConfigurationType> getConfigurationTypes() {
		return Collections.unmodifiableList(types);
	}

	public AbstractWebServerConfiguration createServerConfiguration(String typeId) throws CoreException {
		AbstractWebServerConfiguration serverConfiguration = null;
		IConfigurationElement element = configurationElements.get(typeId);
		if (element != null) {
			Object object = element.createExecutableExtension(ATT_CLASS);
			if (object instanceof AbstractWebServerConfiguration) {
				serverConfiguration = (AbstractWebServerConfiguration) object;
			}
		}
		return serverConfiguration;
	}

	public void addServerConfiguration(AbstractWebServerConfiguration serverConfiguration) {
		if (serverConfiguration == null) {
			throw new IllegalArgumentException();
		}
		if (!serverConfigurations.contains(serverConfiguration)) {
			serverConfigurations.add(serverConfiguration);
		}
	}

	public void removeServerConfiguration(AbstractWebServerConfiguration serverConfiguration) {
		if (serverConfigurations.contains(serverConfiguration)) {
			serverConfigurations.remove(serverConfiguration);
		}
	}

	public List<AbstractWebServerConfiguration> getServerConfigurations() {
		return Collections.unmodifiableList(serverConfigurations);
	}

	public AbstractWebServerConfiguration findServerConfiguration(String name) {
		synchronized (serverConfigurations) {
			for (AbstractWebServerConfiguration i : serverConfigurations) {
				if (name.equals(i.getName())) {
					return i;
				}
			}
		}
		return null;
	}

	private IMemento storeServerConfiguration(AbstractWebServerConfiguration serverConfiguration) {
		IMemento saveMemento = XMLMemento.createWriteRoot(ELEMENT_ROOT).createChild(ELEMENT_SERVER);
		serverConfiguration.saveState(saveMemento);
		saveMemento.putString(ATTR_TYPE, serverConfiguration.getType());
		return saveMemento;
	}

	private AbstractWebServerConfiguration restoreServerConfiguration(IMemento memento, String id) throws CoreException {
		AbstractWebServerConfiguration serverConfiguration = null;
		String typeId = memento.getString(ATTR_TYPE);
		if (typeId != null) {
			IConfigurationElement element = configurationElements.get(typeId);
			if (element != null) {
				Object object = element.createExecutableExtension(ATT_CLASS);
				if (object instanceof AbstractWebServerConfiguration) {
					serverConfiguration = (AbstractWebServerConfiguration) object;
					serverConfiguration.loadState(memento);
				}
			}
		}
		return serverConfiguration;
	}

}
