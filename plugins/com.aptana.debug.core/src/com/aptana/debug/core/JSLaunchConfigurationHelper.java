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
package com.aptana.debug.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.preferences.IJSDebugPreferenceNames;
import com.aptana.debug.internal.core.BrowserUtil;
import com.aptana.debug.internal.core.LocalResourceMapper;
import com.aptana.debug.internal.core.browsers.Firefox;
import com.aptana.debug.internal.core.browsers.InternetExplorer;
import com.aptana.debug.internal.core.model.HttpServerProcess;

/**
 * @author Max Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public final class JSLaunchConfigurationHelper {

	public static final String FIREFOX = "Firefox"; //$NON-NLS-1$
	public static final String INTERNET_EXPLORER = "Internet Explorer"; //$NON-NLS-1$
	public static final String SAFARI = "Safari"; //$NON-NLS-1$

	private JSLaunchConfigurationHelper() {
	}

	/**
	 * setDefaults
	 * 
	 * @param configuration
	 */
	public static void setDefaults(ILaunchConfigurationWorkingCopy configuration, String nature) {
		setBrowserDefaults(configuration, nature);
		setServerDefaults(configuration);
		setHttpDefaults(configuration);
		setDebugDefaults(configuration);
		setAdvancedDefaults(configuration);
	}

	/**
	 * setBrowserDefaults
	 * 
	 * @param configuration
	 */
	@SuppressWarnings("rawtypes")
	public static void setBrowserDefaults(ILaunchConfigurationWorkingCopy configuration, String nature) {
		String browser = StringUtil.EMPTY;
		if (nature == null) {
			try {
				nature = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_NATURE,
						(String) null);
			} catch (CoreException e) {
			}
		}
		do {
			if (Platform.OS_WIN32.equals(Platform.getOS())) {
				/* Firefox */
				if (FIREFOX.equals(nature) || (nature == null)) {
					String[] locations = ILaunchConfigurationConstants.DEFAULT_BROWSER_WINDOWS_FIREFOX;
					for (int i = 0; i < locations.length; ++i) {
						String location = PlatformUtil.expandEnvironmentStrings(locations[i]);
						File file = new File(location);
						if (file.exists() && !file.isDirectory()) {
							browser = location;
							break;
						}
					}
					if (browser.length() != 0) {
						break;
					}
				}

				/* IE */
				if (INTERNET_EXPLORER.equals(nature) || (nature == null)) {
					String path = PlatformUtil.expandEnvironmentStrings(ILaunchConfigurationConstants.DEFAULT_BROWSER_WINDOWS_IE);
					if (new File(path).exists()) {
						browser = path;
						break;
					}
				}
			} else if (Platform.OS_MACOSX.equals(Platform.getOS())) {
				/* Firefox */
				if (FIREFOX.equals(nature) || (nature == null)) {
					String[] locations = ILaunchConfigurationConstants.DEFAULT_BROWSER_MACOSX_FIREFOX;
					for (int i = 0; i < locations.length; ++i) {
						String location = PlatformUtil.expandEnvironmentStrings(locations[i]);
						File file = new File(location);
						if (file.exists() && file.isDirectory()) {
							browser = location;
							break;
						}
					}
					if (browser.length() != 0) {
						break;
					}
				}

				/* Safari */
				if (SAFARI.equals(nature) || (nature == null)) {
					String path = ILaunchConfigurationConstants.DEFAULT_BROWSER_MACOSX_SAFARI;
					if (new File(path).exists()) {
						browser = path;
						break;
					}
				}
			} else if (Platform.OS_LINUX.equals(Platform.getOS())) {
				/* Firefox */
				if (FIREFOX.equals(nature) || (nature == null)) {
					String[] locations = ILaunchConfigurationConstants.DEFAULT_BROWSER_LINUX_FIREFOX;
					for (int i = 0; i < locations.length; ++i) {
						String location = PlatformUtil.expandEnvironmentStrings(locations[i]);
						File file = new File(location);
						if (file.exists() && file.isFile()) {
							browser = location;
							break;
						}
					}
					if (browser.length() != 0) {
						break;
					}
				}
			}

			/* Check configured browsers */
			Enumeration enumeration = (Enumeration) new JSLaunchConfigurationHelper()
					.getContributedAdapter(Enumeration.class);
			if (enumeration != null) {
				while (enumeration.hasMoreElements()) {
					String path = (String) enumeration.nextElement();
					/* Firefox */
					if (FIREFOX.equals(nature) || (nature == null)) {
						if (Firefox.isBrowserExecutable(path)) {
							browser = path;
							break;
						}
					}
					/* IE */
					if (INTERNET_EXPLORER.equals(nature) || (nature == null)) {
						if (InternetExplorer.isBrowserExecutable(path)) {
							browser = path;
							break;
						}
					}
					/* Safari */
					if (SAFARI.equals(nature) || (nature == null)) {
						if (path.toLowerCase().indexOf("safari") != -1) { //$NON-NLS-1$
							browser = path;
							break;
						}
					}
				}
			}

		} while (false);

		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE, browser);
		if (nature != null) {
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_NATURE, nature);
		}
	}

	/**
	 * setDebugDefaults
	 * 
	 * @param configuration
	 */
	public static void setServerDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
				ILaunchConfigurationConstants.DEFAULT_START_ACTION_TYPE);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE,
				ILaunchConfigurationConstants.DEFAULT_SERVER_TYPE);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, StringUtil.EMPTY);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_URL, StringUtil.EMPTY);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, StringUtil.EMPTY);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_APPEND_PROJECT_NAME, false);
	}

	/**
	 * setDebugDefaults
	 * 
	 * @param configuration
	 */
	public static void setDebugDefaults(ILaunchConfigurationWorkingCopy configuration) {
		Preferences store = JSDebugPlugin.getDefault().getPluginPreferences();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_OVERRIDE_DEBUG_PREFERENCES, false);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE, store
				.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE));
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_EXCEPTIONS, store
				.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_EXCEPTIONS));
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ERRORS, store
				.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ERRORS));
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS, store
				.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD));
	}

	/**
	 * setAdvancedDefaults
	 * 
	 * @param configuration
	 */
	public static void setAdvancedDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_ADVANCED_RUN_ENABLED, false);
	}

	/**
	 * setHttpDefaults
	 * 
	 * @param configuration
	 */
	public static void setHttpDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_HTTP_GET_QUERY, StringUtil.EMPTY);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_HTTP_POST_DATA, StringUtil.EMPTY);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_HTTP_POST_CONTENT_TYPE,
				StringUtil.EMPTY);
	}

	/**
	 * isBrowserDebugCompatible
	 * 
	 * @param browser
	 * @return boolean
	 */
	public static boolean isBrowserDebugCompatible(String browser) {
		if (browser != null && BrowserUtil.isBrowserDebugCompatible(browser)) {
			return new File(browser).exists();
		}

		return false;
	}

	/**
	 * getContributedAdapter
	 * 
	 * @param object
	 * @param clazz
	 * @return Object
	 */
	private static Object getContributedAdapter(Object object, Class<?> clazz) {
		Object adapter = null;
		IAdapterManager manager = Platform.getAdapterManager();
		if (manager.hasAdapter(object, clazz.getName())) {
			adapter = manager.getAdapter(object, clazz.getName());
			if (adapter == null) {
				adapter = manager.loadAdapter(object, clazz.getName());
			}
		}
		return adapter;
	}

	/**
	 * getContributedAdapter
	 * 
	 * @param clazz
	 * @return Object
	 */
	private Object getContributedAdapter(Class<?> clazz) {
		return getContributedAdapter(this, clazz);
	}

	public static URL getBaseURL(ILaunchConfiguration configuration, IResource resource) throws CoreException {
		int serverType = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE,
				ILaunchConfigurationConstants.DEFAULT_SERVER_TYPE);
		int startActionType = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
				ILaunchConfigurationConstants.DEFAULT_START_ACTION_TYPE);
		boolean appendProjectName = configuration.getAttribute(
				ILaunchConfigurationConstants.CONFIGURATION_APPEND_PROJECT_NAME, false);

		URL baseURL = null;

		try {
			if (serverType == ILaunchConfigurationConstants.SERVER_EXTERNAL
					|| serverType == ILaunchConfigurationConstants.SERVER_MANAGED) {
				String externalBaseUrl = null;
				if (serverType == ILaunchConfigurationConstants.SERVER_EXTERNAL) {
					externalBaseUrl = configuration.getAttribute(
							ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, StringUtil.EMPTY).trim();
				} else {/*
					String serverId = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_ID,
							(String) null);
					String host = null;
					IServer server = ServerCore.getServerManager().findServer(serverId);
					if (server != null) {
						host = server.getHost();
						if (host == null) {
							host = "localhost"; //$NON-NLS-1$
						}
					}
					if (host == null) {
						throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
								Messages.JSLaunchConfigurationHelper_Host_Not_Specified, null));
					}
					externalBaseUrl = MessageFormat.format("http://{0}/", host); //$NON-NLS-1$
					*/
				}
				if (StringUtil.isEmpty(externalBaseUrl)) {
					throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
							Messages.JSLaunchConfigurationHelper_Empty_Server_URL, null));
				}
				if (externalBaseUrl.charAt(externalBaseUrl.length() - 1) != '/') {
					externalBaseUrl = externalBaseUrl + '/';
				}
				baseURL = new URL(externalBaseUrl);

			} else if (serverType == ILaunchConfigurationConstants.SERVER_INTERNAL) {
				if (startActionType != ILaunchConfigurationConstants.START_ACTION_START_URL && resource != null) {
					/*IHttpServerProviderAdapter httpServerProvider = (IHttpServerProviderAdapter) getContributedAdapter(
							new JSLaunchConfigurationDelegate(), IHttpServerProviderAdapter.class);
					if (httpServerProvider != null) {
						IServer server = httpServerProvider.getServer(resource);
						if (server != null) {
							baseURL = new URL(MessageFormat.format("http://{0}/", server.getHost())); //$NON-NLS-1$
							IPath documentRoot = server.getDocumentRoot();
							if (documentRoot != null
									&& documentRoot.equals(ResourcesPlugin.getWorkspace().getRoot().getLocation())) {
								appendProjectName = true;
							}
						}
					}
					*/
				}
			}
			if (baseURL != null) {
				if (appendProjectName) {
					IProject project = resource.getProject();
					baseURL = new URL(baseURL, project.getName() + '/');
				}
			}
		} catch (MalformedURLException e) {
			throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
					Messages.JSLaunchConfigurationHelper_Malformed_URL, e));
		}

		return baseURL;
	}

	public static URL getLaunchURL(ILaunchConfiguration configuration, IResource resource) throws CoreException {
		int startActionType = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
				ILaunchConfigurationConstants.DEFAULT_START_ACTION_TYPE);

		URL launchURL = null;

		try {
			if (startActionType == ILaunchConfigurationConstants.START_ACTION_START_URL) {
				launchURL = new URL(configuration.getAttribute(
						ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_URL, StringUtil.EMPTY));
				resource = null;
			} else if (startActionType == ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE) {
				String resourcePath = configuration.getAttribute(
						ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, (String) null);
				if (resourcePath != null && resourcePath.length() > 0) {
					resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(resourcePath));
				} else {
					resource = null;
				}
			}
			if (resource != null) {
				URL baseURL = getBaseURL(configuration, resource);

				if (baseURL != null) {
					LocalResourceMapper resourceMapper = new LocalResourceMapper();
					IPath location = resource.getProject().getLocation();
					if (location == null) { // sample location ?
						location = resource.getWorkspace().getRoot().getLocation().append(
								resource.getProject().getFullPath());
					}
					resourceMapper.addMapping(baseURL, location.toFile());
					setResourceMapping(configuration, baseURL, resourceMapper, null);

					URI locationURI = resource.getLocationURI();
					if (locationURI == null) { // sample location ?
						locationURI = resource.getWorkspace().getRoot().getLocation().append(resource.getFullPath())
								.toFile().toURI();
					}
					launchURL = resourceMapper.resolveLocalURI(locationURI).toURL();
				} else {
					IPath location = resource.getLocation();
					if (location == null) { // sample location ?
						location = resource.getWorkspace().getRoot().getLocation().append(resource.getFullPath());
					}
					launchURL = location.toFile().toURI().toURL();
				}
			}

			if (launchURL != null) {
				String httpGetQuery = configuration.getAttribute(
						ILaunchConfigurationConstants.CONFIGURATION_HTTP_GET_QUERY, StringUtil.EMPTY);
				if (httpGetQuery != null && httpGetQuery.length() > 0 && launchURL.getQuery() == null
						&& launchURL.getRef() == null) {
					if (httpGetQuery.charAt(0) != '?') {
						httpGetQuery = '?' + httpGetQuery;
					}
					launchURL = new URL(launchURL, launchURL.getFile() + httpGetQuery);
				}
			}
		} catch (MalformedURLException e) {
			throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
					Messages.JSLaunchConfigurationHelper_Malformed_URL, e));
		}

		return launchURL;
	}

	public static void initializeLaunchAttributes(ILaunchConfiguration configuration, ILaunch launch)
			throws CoreException {
		if (configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_OVERRIDE_DEBUG_PREFERENCES, false)) {
			launch.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE, Boolean
					.toString(configuration.getAttribute(
							ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE, false)));
			launch.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_EXCEPTIONS, Boolean
					.toString(configuration.getAttribute(
							ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_EXCEPTIONS, false)));
			launch.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ERRORS, Boolean
					.toString(configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ERRORS,
							false)));
			launch.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS, Boolean
					.toString(configuration.getAttribute(
							ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS, false)));
		} else {
			Preferences store = JSDebugPlugin.getDefault().getPluginPreferences();
			launch.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE, Boolean
					.toString(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE)));
			launch.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_EXCEPTIONS, Boolean
					.toString(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_EXCEPTIONS)));
			launch.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ERRORS, Boolean.toString(store
					.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ERRORS)));
			launch.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS, Boolean
					.toString(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD)));

		}
	}

	/* package */static void setResourceMapping(ILaunchConfiguration configuration, URL baseURL,
			LocalResourceMapper resourceMapper, HttpServerProcess server) throws CoreException {
		String[] list = JSDebugOptionsManager.parseList(configuration.getAttribute(
				ILaunchConfigurationConstants.CONFIGURATION_SERVER_PATHS_MAPPING, StringUtil.EMPTY));
		for (int i = 0, length = list.length; i < length;) {
			String serverPath = list[i++];
			String workspacePath = list[i++];
			boolean enabled = !"0".equals(list[i++]); //$NON-NLS-1$
			if (enabled) {
				try {
					URL url = new URL(baseURL, new Path(serverPath).makeRelative().toPortableString());
					IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(workspacePath));
					if (resource != null) {
						File file = resource.getLocation().toFile();
						resourceMapper.addMapping(url, file);
						if (server != null) {
							server.addServerPath(baseURL.toURI().relativize(url.toURI()).toASCIIString(), file);
						}
					}
				} catch (MalformedURLException e) {
					JSDebugPlugin.log(e);
				} catch (URISyntaxException e) {
					JSDebugPlugin.log(e);
				}
			}
		}
	}

}
