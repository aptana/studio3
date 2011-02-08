/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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

package com.aptana.js.debug.ui.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchHistory;

import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSLaunchConfigurationHelper;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 * 
 */
@SuppressWarnings("restriction")
public final class StartPageManager {

	/**
	 * IStartPageChangeListener
	 * 
	 */
	public interface IStartPageChangeListener {
		void startPageChanged(IResource resource);
	}

	private ILaunchConfigurationType configType;
	private Map<String, String> locations = new HashMap<String, String>();
	private ListenerList listeners = new ListenerList();

	private static StartPageManager instance;

	/**
	 * 
	 */
	private StartPageManager() {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		configType = manager.getLaunchConfigurationType(ILaunchConfigurationConstants.ID_JS_APPLICATION);
		manager.addLaunchConfigurationListener(new ILaunchConfigurationListener() {

			public void launchConfigurationAdded(ILaunchConfiguration configuration) {
				launchConfigurationAddedInternal(configuration);
			}

			public void launchConfigurationChanged(ILaunchConfiguration configuration) {
				if (configuration instanceof ILaunchConfigurationWorkingCopy == false) {
					launchConfigurationRemovedInternal(configuration);
					launchConfigurationAddedInternal(configuration);
				}
			}

			public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
				launchConfigurationRemovedInternal(configuration);
			}

		});
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurations(configType);
			for (int i = 0; i < configs.length; i++) {
				launchConfigurationAddedInternal(configs[i]);
			}
		} catch (CoreException e) {
			JSDebugUIPlugin.log(e);
		}
	}

	public static StartPageManager getDefault() {
		if (instance == null) {
			instance = new StartPageManager();
		}
		return instance;
	}

	public void dispose() {
		locations.clear();
		// ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		// manager.removeLaunchConfigurationListener(this);

	}

	/**
	 * add listener
	 * 
	 * @param listener
	 */
	public void addListener(IStartPageChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * remove listener
	 * 
	 * @param listener
	 */
	public void removeListener(IStartPageChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @param resource
	 * @return
	 */
	public boolean isStartPage(IResource resource) {
		return locations.containsValue(resource.getFullPath().toPortableString());
	}

	/**
	 * set start page
	 * 
	 * @param resource
	 */
	public void setStartPage(IResource resource) {
		ILaunchConfiguration config = findLaunchConfiguration();
		if (config != null) {
			try {
				updateLaunchConfiguration(config, resource);
			} catch (CoreException e) {
				JSDebugUIPlugin.log(e);
			}
		}
	}

	/**
	 * remove start page
	 * 
	 * @param resource
	 */
	public void removeStartPage(IResource resource) {
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurations(configType);
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				if (config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE, -1) == ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE) {
					String location = config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH,
							StringUtil.EMPTY);
					if (resource.getFullPath().toPortableString().equals(location)) {
						updateLaunchConfiguration(config, null);
					}
				}
			}
		} catch (CoreException e) {
			JSDebugUIPlugin.log(e);
		}
	}

	private ILaunchConfiguration findLaunchConfiguration() {
		try {
			LaunchHistory history = DebugUIPlugin.getDefault().getLaunchConfigurationManager()
					.getLaunchHistory("org.eclipse.debug.ui.launchGroup.debug"); //$NON-NLS-1$
			if (history != null) {
				ILaunchConfiguration[] configs = history.getHistory();
				for (int i = 0; i < configs.length; i++) {
					ILaunchConfiguration config = configs[i];
					if (configType.equals(config.getType())) {
						return config;
					}
				}
			}
		} catch (CoreException e) {
			JSDebugUIPlugin.log(e);
		}
		return createConfiguration("Default"); //$NON-NLS-1$
	}

	private ILaunchConfiguration createConfiguration(String namePrefix) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, DebugPlugin.getDefault()
					.getLaunchManager().generateUniqueLaunchConfigurationNameFrom(namePrefix));
			JSLaunchConfigurationHelper.setDefaults(wc, null);
			config = wc.doSave();
		} catch (CoreException e) {
			JSDebugUIPlugin.log(e);
		}
		return config;
	}

	private void updateLaunchConfiguration(ILaunchConfiguration config, IResource resource) throws CoreException {
		ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
		if (resource != null) {
			wc.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
					ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE);
			wc.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, resource.getFullPath()
					.toPortableString());
		} else {
			wc.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
					ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE);
		}
		wc.doSave();
	}

	private void launchConfigurationAddedInternal(ILaunchConfiguration configuration) {
		try {
			if (configuration.getType().equals(configType)) {
				if (configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE, -1) == ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE) {
					String location = configuration.getAttribute(
							ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, StringUtil.EMPTY);
					locations.put(configuration.getName(), location);
					notifyListeners(location);
				}
			}
		} catch (CoreException e) {
			JSDebugUIPlugin.log("Could not access launch configuration", e); //$NON-NLS-1$
		}
	}

	private void launchConfigurationRemovedInternal(ILaunchConfiguration configuration) {
		try {
			if (configuration.getType().equals(configType)) {
				String location = (String) locations.remove(configuration.getName());
				if (location != null) {
					notifyListeners(location);
				}
			}
		} catch (CoreException e) {
			/* ignore */
		}
	}

	private void notifyListeners(String location) {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(location);
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; ++i) {
			((IStartPageChangeListener) list[i]).startPageChanged(resource);
		}
	}

}
