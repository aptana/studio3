/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.IDebugScopes;
import com.aptana.core.logging.IdeLog;

public class EclipseUtil
{
	private static final String DEV_VERSION = "0.0.0.qualifier"; //$NON-NLS-1$

	/**
	 * Default prefix for Studio
	 */
	private static final String APTANA_STUDIO_PREFIX = "Aptana"; //$NON-NLS-1$

	/**
	 * Default product name
	 */
	private static final String APTANA_STUDIO = MessageFormat.format("{0} Studio", APTANA_STUDIO_PREFIX); //$NON-NLS-1$

	private static final Pattern VERSION_PATTERN = Pattern.compile("Version: (.*)\n"); //$NON-NLS-1$
	private static final Pattern VERSION_4_4_PATTERN = Pattern.compile("Version: \\{1\\} \\((.*)\\)\n"); //$NON-NLS-1$
	private static final Pattern BUILD_PATTERN = Pattern.compile("build: (.*)\n"); //$NON-NLS-1$
	private static final Pattern BUILD_BRANCH_PATTERN = Pattern.compile("Build: ([\\w\\-\\d]+) \\(origin/(\\w+)\\)\n"); //$NON-NLS-1$

	protected static final class LauncherFilter implements FilenameFilter
	{

		public boolean accept(File dir, String name)
		{
			IPath path = Path.fromOSString(dir.getAbsolutePath()).append(name);
			name = path.removeFileExtension().lastSegment();
			String ext = path.getFileExtension();
			if (Platform.OS_MACOSX.equals(Platform.getOS()))
			{
				if (!"app".equals(ext)) //$NON-NLS-1$
				{
					return false;
				}
			}
			for (String launcherName : LAUNCHER_NAMES)
			{
				if (launcherName.equalsIgnoreCase(name))
				{
					return true;
				}
			}
			return false;
		}
	}

	public static final String STANDALONE_PLUGIN_ID = "com.aptana.rcp"; //$NON-NLS-1$

	private static final String TYCHO_HEADLESS = "org.eclipse.tycho.surefire.osgibooter.headlesstest"; //$NON-NLS-1$
	private static final String TYCHO_UI = "org.eclipse.tycho.surefire.osgibooter.uitest"; //$NON-NLS-1$
	
	@SuppressWarnings("nls")
	private static final String[] UNIT_TEST_IDS = {
			"org.eclipse.pde.junit.runtime.uitestapplication",
			"org.eclipse.test.coretestapplication",
			"org.eclipse.test.uitestapplication",
			"org.eclipse.pde.junit.runtime.legacytestapplication",
			"org.eclipse.pde.junit.runtime.coretestapplication",
			"org.eclipse.pde.junit.runtime.coretestapplicationnonmain",
			"org.eclipse.pde.junit.runtime.nonuithreadtestapplication",
			TYCHO_HEADLESS,
			TYCHO_UI
	};
	@SuppressWarnings("nls")
	static final String[] LAUNCHER_NAMES = { "Eclipse", "AptanaStudio3", "Aptana Studio 3", "TitaniumStudio",
			"Titanium Studio" };

	private static Boolean isTesting;

	private static String rcpPluginId = STANDALONE_PLUGIN_ID;
	// uses branding plugin for retrieving the version
	private static String versionPluginId = "com.aptana.branding"; //$NON-NLS-1$

	private static String fgPrefix;
	private static Boolean fgNewAPI;

	private EclipseUtil()
	{
	}

	/**
	 * Determines if the specified debug option is on and set to true
	 * 
	 * @param option
	 * @return
	 */
	public static boolean isDebugOptionEnabled(String option)
	{
		return Boolean.valueOf(Platform.getDebugOption(option));
	}

	/**
	 * Determines if the specified application/platform option has been enabled
	 * 
	 * @param option
	 * @return
	 */
	public static boolean isSystemPropertyEnabled(String option)
	{
		return getSystemProperty(option) != null;
	}

	/**
	 * Returns specified application/platform option. If not specified, returns null.
	 * 
	 * @param option
	 * @return
	 */
	public static String getSystemProperty(String option)
	{
		if (option == null)
		{
			return null;
		}
		return System.getProperty(option);
	}

	/**
	 * Returns specified application/platform option. If not specified, returns null.
	 * 
	 * @param option
	 * @return
	 */
	public static String getSystemProperty(String option, String defaultValue)
	{
		if (option == null)
		{
			return null;
		}
		return System.getProperty(option, defaultValue);
	}

	/**
	 * Is the current plugin actually loaded (needed for unit testing)
	 * 
	 * @param plugin
	 * @return boolean
	 */
	public static boolean isPluginLoaded(Plugin plugin)
	{
		return plugin != null && plugin.getBundle() != null;
	}

	/**
	 * Retrieves the bundle version of a plugin.
	 * 
	 * @param plugin
	 *            the plugin to retrieve from
	 * @return the bundle version, or null if not found.
	 */
	public static String getPluginVersion(Plugin plugin)
	{
		if (!isPluginLoaded(plugin))
		{
			return null;
		}
		return getPluginVersion(plugin.getBundle());
	}

	/**
	 * Retrieves the bundle version of a plugin based on its id.
	 * 
	 * @param pluginId
	 *            the id of the plugin
	 * @return the bundle version, or null if not found.
	 */
	public static String getPluginVersion(String pluginId)
	{
		if (pluginId == null)
		{
			return null;
		}

		return getPluginVersion(Platform.getBundle(pluginId));
	}

	private static String getPluginVersion(Bundle bundle)
	{
		if (bundle == null)
		{
			return null;
		}
		return bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION).toString(); // $codepro.audit.disable
																								// com.instantiations.assist.eclipse.analysis.unnecessaryToString
	}

	public static String getProductName()
	{
		IProduct product = Platform.getProduct();
		if (product != null)
		{
			String name = product.getName();
			if (!StringUtil.isEmpty(name))
			{
				return name;
			}
		}
		return APTANA_STUDIO;
	}

	/**
	 * Retrieves the product version from the Platform aboutText property
	 * 
	 * @return
	 */
	public static String getProductVersion()
	{
		try
		{
			String aboutText = getProductProperty("aboutText"); //$NON-NLS-1$
			if (!StringUtil.isEmpty(aboutText))
			{
				Matcher m = VERSION_4_4_PATTERN.matcher(aboutText);
				if (m.find())
				{
					return Version.parseVersion(m.group(1)).toString();
				}

				// Try version pattern from before 4.4
				m = VERSION_PATTERN.matcher(aboutText);
				if (m.find())
				{
					return m.group(1);
				}

				// fall back to trying to match build #
				m = BUILD_PATTERN.matcher(aboutText);
				if (m.find())
				{
					String version = m.group(1);
					if (!DEV_VERSION.equals(version))
					{
						return version;
					}
				}
			}
		}
		catch (Exception e)
		{
			// ignore
		}

		// falls back to the branding plugin version
		return getStudioVersion();
	}

	/**
	 * @return the current version of Studio plugins
	 */
	public static String getStudioVersion()
	{
		Bundle bundle = Platform.getBundle(versionPluginId);
		if (bundle != null)
		{
			return bundle.getVersion().toString();
		}
		return StringUtil.EMPTY;
	}

	public synchronized static String getStudioPrefix()
	{
		// Cache this value!
		if (fgPrefix == null)
		{
			fgPrefix = APTANA_STUDIO_PREFIX;
			String name = getProductProperty("studioPrefix"); //$NON-NLS-1$
			if (!StringUtil.isEmpty(name))
			{
				fgPrefix = name;
			}
		}
		return fgPrefix;
	}

	private static String getProductProperty(String propName)
	{
		IProduct product = Platform.getProduct();
		if (product == null)
		{
			return null;
		}
		return product.getProperty(propName);
	}

	/**
	 * Determines if the IDE is running as a standalone app versus as a plugin
	 * 
	 * @return
	 */
	public static boolean isStandalone()
	{
		return getPluginVersion(rcpPluginId) != null;
	}

	/**
	 * Determines if the IDE is running in a unit test
	 * 
	 * @return
	 */
	public static boolean isTesting()
	{
		if (isTesting != null)
		{
			return isTesting;
		}
		String application = getApplicationId();
		if (application != null)
		{
			for (String id : UNIT_TEST_IDS)
			{
				if (id.equals(application))
				{
					isTesting = Boolean.TRUE;
					return isTesting;
				}
			}
		}
		String[] args = getCommandLineArgs();
		isTesting = ArrayUtil.contains(args, "-testLoaderClass"); //$NON-NLS-1$
		return isTesting;
	}
	
	static String getApplicationId()
	{
		String application = getEnvironmentOrSystemProperty("eclipse.application"); //$NON-NLS-1$
		if (application != null)
		{
			return application;
		}
		application = getCommandLineArgValue("-application"); //$NON-NLS-1$
		return application;
	}
	
	private static String getCommandLineArgValue(String switchName)
	{
		String[] args = getCommandLineArgs();
		for (int i = 0; i < args.length; ++i)
		{
			if (switchName.equals(args[i]) && (i + 1) < args.length) //$NON-NLS-1$
			{
				return args[i + 1];
			}
		}
		return null;
	}

	private static String getEnvironmentOrSystemProperty(String propName)
	{
		String value = null;
		EnvironmentInfo info = getEnvironmentInfo();
		if (info != null)
		{
			value = info.getProperty(propName);
		}
		if (value != null)
		{
			return value;
		}
		return System.getProperty(propName);
	}
	
	private static String[] getCommandLineArgs()
	{
		EnvironmentInfo info = getEnvironmentInfo();
		if (info != null)
		{
			return info.getCommandLineArgs();
		}
		String cmdline = getEnvironmentOrSystemProperty("eclipse.commands");
		if (cmdline != null && cmdline.length() > 0)
		{
			return cmdline.split("\n"); //$NON-NLS-1$
		}
		return ArrayUtil.NO_STRINGS;
	}

	/**
	 * Returns path to application launcher executable
	 * 
	 * @return
	 */
	public static IPath getApplicationLauncher()
	{
		return getApplicationLauncher(false);
	}

	/**
	 * Returns path to application launcher executable
	 * 
	 * @param asSplashLauncher
	 * @return
	 */
	public static IPath getApplicationLauncher(boolean asSplashLauncher)
	{
		String launcherName = getCommandLineArgValue("-launcher"); //$NON-NLS-1$
		IPath launcher = launcherName == null ? null : Path.fromOSString(launcherName);
		if (launcher == null)
		{
			Location location = Platform.getInstallLocation();
			if (location != null)
			{
				launcher = new Path(location.getURL().getFile());
				if (launcher.toFile().isDirectory())
				{
					String[] executableFiles = launcher.toFile().list(new LauncherFilter());
					if (executableFiles.length > 0)
					{
						launcher = launcher.append(executableFiles[0]);
					}
				}
			}
		}
		if (launcher == null || !launcher.toFile().exists())
		{
			return null;
		}
		if (PlatformUtil.isMac() && asSplashLauncher)
		{
			launcher = new Path(PlatformUtil.getApplicationExecutable(launcher.toOSString()).getAbsolutePath());
		}
		return launcher;
	}

	public static void setRCPPluginId(String pluginId)
	{
		if (!StringUtil.isEmpty(pluginId))
		{
			rcpPluginId = pluginId;
		}
	}

	public static void setVersionPluginId(String pluginId)
	{
		if (!StringUtil.isEmpty(pluginId))
		{
			versionPluginId = pluginId;
		}
	}

	/**
	 * Checks to see if user has turned on showing system jobs to user, etc.
	 * 
	 * @return
	 */
	public static boolean showSystemJobs()
	{
		return Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID,
				ICorePreferenceConstants.PREF_SHOW_SYSTEM_JOBS, false, null);
	}

	public static void setSystemForJob(Job job)
	{
		try
		{
			job.setSystem(!showSystemJobs());
		}
		catch (Exception e)
		{
			// ignore
		}
	}

	/**
	 * Set the debugging state of the platform
	 */
	public static void setPlatformDebugging(boolean debugEnabled)
	{
		// Platform only sees a null value as "false" for this, so we need to hack around to set a null value
		// depending on what API version we're using. 4.3 and lower throw exceptions if we try to set the property to
		// null.
		final String propertyName = "osgi.debug"; //$NON-NLS-1$
		if (!debugEnabled && !isNewOSGIAPI())
		{
			// Can't set a null property on EnivronmentInfo in 4.3 and lower. So we need to hack using reflection
			// against old API
			try
			{
				Class klazz = Class.forName("org.eclipse.osgi.framework.internal.core.FrameworkProperties"); //$NON-NLS-1$
				Method m = klazz.getMethod("clearProperty", String.class); //$NON-NLS-1$
				m.invoke(null, propertyName);
				return;
			}
			catch (ClassNotFoundException cnfe)
			{
				// assume it's because we're on a 4.4+ build where we follow with the logic below...
			}
			catch (Exception e)
			{
				IdeLog.logError(CorePlugin.getDefault(), e);
			}
		}
		EnvironmentInfo info = getEnvironmentInfo();
		if (info != null)
		{
			if (debugEnabled)
			{
				info.setProperty(propertyName, Boolean.toString(debugEnabled));
			}
			else
			{
				info.setProperty(propertyName, null);
			}
		}
	}

	protected static EnvironmentInfo getEnvironmentInfo()
	{
		BundleContext context = CorePlugin.getDefault().getContext();
		if (context == null)
		{
			return null;
		}
		ServiceReference<EnvironmentInfo> ref = context.getServiceReference(EnvironmentInfo.class);
		if (ref == null)
		{
			return null;
		}
		return context.getService(ref);
	}

	/**
	 * The 3.10.0 version of the OSGI bundle made a lot of breaking changes to internals (that we unfortunately used).
	 * 
	 * @return
	 */
	private synchronized static boolean isNewOSGIAPI()
	{
		if (fgNewAPI == null)
		{
			Bundle b = Platform.getBundle("org.eclipse.osgi"); //$NON-NLS-1$
			Version v = b.getVersion();
			fgNewAPI = v.compareTo(Version.parseVersion("3.9.100")) >= 0; //$NON-NLS-1$
		}
		return fgNewAPI;
	}

	/**
	 * Returns a list of all possible trace items across all plugins
	 */
	public static Map<String, String> getTraceableItems()
	{
		Map<String, String> stringModels = new HashMap<String, String>();
		BundleContext context = CorePlugin.getDefault().getContext();
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles)
		{
			Properties props = getTraceOptions(bundle);
			for (Object obj : props.keySet())
			{
				String key = obj.toString();
				stringModels.put(key, props.getProperty(key));
			}
		}
		return stringModels;
	}

	/**
	 * Returns all the trace options for a particular bundle
	 * 
	 * @param bundle
	 * @return
	 */
	public static Properties getTraceOptions(Bundle bundle)
	{
		Path path = new Path(".options"); //$NON-NLS-1$
		URL fileURL = FileLocator.find(bundle, path, null);
		if (fileURL != null)
		{
			InputStream in;
			try
			{
				in = fileURL.openStream();
				Properties options = new Properties();
				options.load(in);
				return options;
			}
			catch (IOException e1)
			{
			}
		}

		return new Properties();

	}

	/**
	 * Returns a map of all loaded bundle symbolic names mapped to bundles
	 * 
	 * @return
	 */
	public static Map<String, BundleContext> getCurrentBundleContexts()
	{
		Map<String, BundleContext> contexts = new HashMap<String, BundleContext>();

		BundleContext context = CorePlugin.getDefault().getContext();
		contexts.put(context.getBundle().getSymbolicName(), context);

		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles)
		{
			BundleContext bContext = bundle.getBundleContext();
			if (bContext == null)
			{
				continue;
			}
			contexts.put(bundle.getSymbolicName(), bContext);
		}

		return contexts;
	}

	/**
	 * Set debugging for the specified bundle
	 * 
	 * @param currentOptions
	 * @param debugEnabled
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setBundleDebugOptions(String[] currentOptions, boolean debugEnabled)
	{
		Map<String, BundleContext> bundles = getCurrentBundleContexts();
		for (String key : currentOptions)
		{
			String symbolicName = key.substring(0, key.indexOf('/'));
			BundleContext bundleContext = bundles.get(symbolicName);
			if (bundleContext == null)
			{
				continue;
			}
			// don't add <?> as it's for Eclipse 3.7's getServiceReference() only
			ServiceReference sRef = bundleContext.getServiceReference(DebugOptions.class.getName());
			DebugOptions options = (DebugOptions) bundleContext.getService(sRef);

			// have to set debug enabled first if re-enabling, or else the internal property list will be null
			// and the set won't happen
			if (debugEnabled)
			{
				options.setDebugEnabled(debugEnabled);
				options.setOption(key, Boolean.toString(debugEnabled));
			}
			else
			{
				options.setOption(key, Boolean.toString(debugEnabled));
				options.setDebugEnabled(debugEnabled);
			}
		}
	}

	/**
	 * Gets the list of components currently in debug mode
	 * 
	 * @return
	 */
	public static String[] getCurrentDebuggableComponents()
	{
		String checked = Platform.getPreferencesService().getString(CorePlugin.PLUGIN_ID,
				ICorePreferenceConstants.PREF_DEBUG_COMPONENT_LIST, null, null);
		if (checked != null)
		{
			return checked.split(","); //$NON-NLS-1$
		}
		return ArrayUtil.NO_STRINGS;
	}

	/**
	 * Find all elements of a given name for an extension point and delegate processing to an
	 * IConfigurationElementProcessor.
	 * 
	 * @param pluginId
	 * @param extensionPointId
	 * @param processor
	 * @param elementNames
	 */
	public static void processConfigurationElements(String pluginId, String extensionPointId,
			IConfigurationElementProcessor processor)
	{
		IExtensionPoint extensionPoint = getExtensionPoint(pluginId, extensionPointId);
		if (extensionPoint != null)
		{
			processElements(extensionPoint, processor);
		}
	}

	public static IExtensionPoint getExtensionPoint(String pluginId, String extensionPointId)
	{
		if (StringUtil.isEmpty(pluginId) || StringUtil.isEmpty(extensionPointId))
		{
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		if (registry != null)
		{
			IdeLog.logInfo(CorePlugin.getDefault(),
					MessageFormat.format("Geting Extension Point for {0} and extensionPoint {1} from {2}", pluginId, //$NON-NLS-1$
							extensionPointId, registry),
					IDebugScopes.EXTENSION_POINTS);
			return registry.getExtensionPoint(pluginId, extensionPointId);
		}
		return null;
	}

	public static void processElements(IExtensionPoint extensionPoint, IConfigurationElementProcessor processor)
	{
		if (processor == null || processor.getSupportElementNames().isEmpty())
		{
			return;
		}

		Set<String> elementNames = processor.getSupportElementNames();
		IdeLog.logInfo(CorePlugin.getDefault(),
				MessageFormat.format("Extension point : {0} and elements : {1}", extensionPoint, //$NON-NLS-1$
						StringUtil.join(",", elementNames)),
				IDebugScopes.EXTENSION_POINTS);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (String elementName : elementNames)
		{
			for (IExtension extension : extensions)
			{
				IConfigurationElement[] elements = extension.getConfigurationElements();

				for (IConfigurationElement element : elements)
				{
					if (element.getName().equals(elementName))
					{
						processor.processElement(element);
						if (IdeLog.isTraceEnabled(CorePlugin.getDefault(), IDebugScopes.EXTENSION_POINTS))
						{
							IdeLog.logTrace(CorePlugin.getDefault(),
									MessageFormat.format("Processing extension element {0} with attributes {1}", //$NON-NLS-1$
											element.getName(), collectElementAttributes(element)),
									IDebugScopes.EXTENSION_POINTS);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns a map of all the configuration element attributes
	 * 
	 * @param element
	 * @return
	 */
	public static Map<String, String> collectElementAttributes(IConfigurationElement element)
	{
		Map<String, String> map = new TreeMap<String, String>();
		String[] attributes = element.getAttributeNames();
		for (String string : attributes)
		{
			map.put(string, element.getAttribute(string));
		}
		return map;
	}

	/**
	 * Use this to load resources from extension points. For relative paths this will convert to a URL referencing the
	 * enclosing plugin and resolve the path. Otherwise this will convert the string to an URL (so a resource could be
	 * pointed at in the plugin.xml definition using http:, ftp:, data:, platform:/plugin/plugin.id URLs)
	 * 
	 * @param element
	 * @param attr
	 * @return
	 */
	public static URL getResourceURL(IConfigurationElement element, String attr)
	{
		String iconPath = element.getAttribute(attr);
		if (iconPath == null)
		{
			return null;
		}

		// If iconPath doesn't specify a scheme, then try to transform to a URL
		// RFC 3986: scheme = ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
		// This allows using data:, http:, or other custom URL schemes
		if (!iconPath.matches("\\p{Alpha}[\\p{Alnum}+.-]*:.*")) //$NON-NLS-1$
		{
			String extendingPluginId = element.getDeclaringExtension().getContributor().getName();
			iconPath = "platform:/plugin/" + extendingPluginId + "/" + iconPath; //$NON-NLS-1$//$NON-NLS-2$
		}
		try
		{
			return new URL(iconPath);
		}
		catch (MalformedURLException e)
		{
			/* IGNORE */
		}
		return null;
	}

	/**
	 * Migrate the existing preferences from instance scope to configuration scope and then remove the preference key
	 * from the instance scope.
	 */
	public static void migratePreference(String pluginId, String preferenceKey)
	{
		IEclipsePreferences configNode = ConfigurationScope.INSTANCE.getNode(pluginId);
		if (StringUtil.isEmpty(configNode.get(preferenceKey, null))) // no value in config scope
		{
			IEclipsePreferences instanceNode = InstanceScope.INSTANCE.getNode(pluginId);
			String instancePrefValue = instanceNode.get(preferenceKey, null);
			if (!StringUtil.isEmpty(instancePrefValue))
			{
				// only migrate if there is a value!
				configNode.put(preferenceKey, instancePrefValue);
				instanceNode.remove(preferenceKey);
				try
				{
					configNode.flush();
					instanceNode.flush();
				}
				catch (BackingStoreException e)
				{
					IdeLog.logWarning(CorePlugin.getDefault(), e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * exposed at package level for testing.
	 * 
	 * @param aboutText
	 * @return
	 */
	static String getProductBuildBranch(String aboutText)
	{
		try
		{
			if (StringUtil.isEmpty(aboutText))
			{
				return null;
			}
			Matcher m = BUILD_BRANCH_PATTERN.matcher(aboutText);
			if (m.find())
			{
				return m.group(2);
			}
		}
		catch (Exception e)
		{
			// ignore
		}
		return null;
	}

	public static String getProductBuildBranch()
	{
		return getProductBuildBranch(getProductProperty("aboutText")); //$NON-NLS-1$

	}

	public static boolean isTycho()
	{
		String appId = getApplicationId();
		return TYCHO_HEADLESS.equals(appId) || TYCHO_UI.equals(appId);
	}

}