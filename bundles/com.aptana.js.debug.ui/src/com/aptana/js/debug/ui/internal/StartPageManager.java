/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

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

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSLaunchConfigurationHelper;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public final class StartPageManager {

	/**
	 * IStartPageChangeListener
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
				if (!(configuration instanceof ILaunchConfigurationWorkingCopy)) {
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
			for (ILaunchConfiguration config : configs) {
				launchConfigurationAddedInternal(config);
			}
		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
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
				IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
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
			for (ILaunchConfiguration config : configs) {
				if (config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE, -1) == ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE) {
					String location = config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH,
							StringUtil.EMPTY);
					if (resource.getFullPath().toPortableString().equals(location)) {
						updateLaunchConfiguration(config, null);
					}
				}
			}
		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
	}

	private ILaunchConfiguration findLaunchConfiguration() {
		try {
			LaunchHistory history = DebugUIPlugin.getDefault().getLaunchConfigurationManager()
					.getLaunchHistory("org.eclipse.debug.ui.launchGroup.debug"); //$NON-NLS-1$
			if (history != null) {
				ILaunchConfiguration[] configs = history.getHistory();
				for (ILaunchConfiguration config : configs) {
					if (configType.equals(config.getType())) {
						return config;
					}
				}
			}
		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
		return createConfiguration(Messages.StartPageManager_DefaultConfigurationName);
	}

	private ILaunchConfiguration createConfiguration(String namePrefix) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, DebugPlugin.getDefault()
					.getLaunchManager().generateLaunchConfigurationName(namePrefix));
			JSLaunchConfigurationHelper.setDefaults(wc, null);
			config = wc.doSave();
		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
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
			IdeLog.logError(JSDebugUIPlugin.getDefault(), "Could not access launch configuration", e); //$NON-NLS-1$
		}
	}

	private void launchConfigurationRemovedInternal(ILaunchConfiguration configuration) {
		try {
			if (configuration.exists() && configuration.getType().equals(configType)) {
				String location = (String) locations.remove(configuration.getName());
				if (location != null) {
					notifyListeners(location);
				}
			}
		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
	}

	private void notifyListeners(String location) {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(location);
		for (Object listener : listeners.getListeners()) {
			((IStartPageChangeListener) listener).startPageChanged(resource);
		}
	}

}
