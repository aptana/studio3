/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.jruby.RubyRegexp;
import org.osgi.framework.Bundle;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;
import com.aptana.scripting.IDebugScopes;
import com.aptana.scripting.IScriptingSystemProperties;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptingActivator;
import com.aptana.scripting.ScriptingEngine;
import com.aptana.scripting.model.filters.AndFilter;
import com.aptana.scripting.model.filters.IModelFilter;
import com.aptana.scripting.model.filters.IsExecutableCommandFilter;

public class BundleManager
{

	/**
	 * The system property to look at for an override of the # of nundles to load in parallel.
	 */
	private static final String STUDIO_BUNDLE_LOAD_CONCURRENCY = "studio.bundleLoadConcurrency"; //$NON-NLS-1$

	private static final String APPLICATION_BUNDLE_PATHS_ID = "applicationBundlePaths"; //$NON-NLS-1$
	private static final String TAG_BUNDLE_PATH = "bundlePath"; //$NON-NLS-1$
	private static final String ATTR_PATH = "path"; //$NON-NLS-1$

	/**
	 * System property to forcibly turn off caching.
	 */
	private static final String USE_BUNDLE_CACHE = "use.bundle.cache"; //$NON-NLS-1$

	private class BundleLoadJob extends Job
	{

		private File bundleDirectory;

		BundleLoadJob(File bundleDirectory)
		{
			super("Loading bundle: " + bundleDirectory.getAbsolutePath()); //$NON-NLS-1$

			this.bundleDirectory = bundleDirectory;

			setPriority(Job.SHORT);
		}

		public IStatus run(IProgressMonitor monitor)
		{
			List<File> bundleScripts = getBundleScripts(bundleDirectory);
			SubMonitor sub = SubMonitor.convert(monitor, bundleScripts.size() + 1);
			try
			{
				if (bundleScripts.size() > 0)
				{
					showBundleLoadInfo("cached failed, loading files directly: " + bundleDirectory); //$NON-NLS-1$

					List<String> bundleLoadPaths = getBundleLoadPaths(bundleDirectory);

					// first script is always bundle.rb, so go ahead
					// and process that
					File bundleScript = bundleScripts.get(0);
					sub.subTask(bundleScript.getAbsolutePath());
					loadScript(bundleScript, true, bundleLoadPaths);
					sub.worked(1);

					// some new scripts may have come in while we were
					// processing bundle.rb, so recalculate the list of
					// scripts to process
					bundleScripts = getBundleScripts(bundleDirectory);

					if (bundleScripts.size() > 0)
					{
						// we've already loaded bundle.rb, so remove it from
						// the list. Note that at this point we have a
						// bundle element for this bundle, so any file
						// events that occur now correctly update the bundle
						// element
						bundleScripts.remove(0);

						// process the rest of the scripts in the bundle
						for (File script : bundleScripts)
						{
							sub.subTask(script.getAbsolutePath());
							loadScript(script, true, bundleLoadPaths);
							sub.worked(1);
						}
					}
				}
			}
			finally
			{
				sub.done();
			}
			return Status.OK_STATUS;
		}
	}

	/**
	 * This is a rule which reports a conflict when two rules wrap the same object. It is used to enforce a max job
	 * count for parallel bundle loads.
	 */
	private class SerialPerObjectRule implements ISchedulingRule
	{
		private Object fObject = null;

		public SerialPerObjectRule(Object lock)
		{
			fObject = lock;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
		 */
		public boolean contains(ISchedulingRule rule)
		{
			return rule == this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
		 */
		public boolean isConflicting(ISchedulingRule rule)
		{
			if (rule instanceof SerialPerObjectRule)
			{
				SerialPerObjectRule vup = (SerialPerObjectRule) rule;

				return fObject == vup.fObject;
			}

			return false;
		}
	}

	// split patterns
	private static final Pattern DOT_PATTERN = Pattern.compile("\\."); //$NON-NLS-1$
	private static final Pattern STAR_PATTERN = Pattern.compile("\\*"); //$NON-NLS-1$

	// special sub-directories within a bundle directory
	public static final String SNIPPETS_DIRECTORY_NAME = "snippets"; //$NON-NLS-1$
	private static final String COMMANDS_DIRECTORY_NAME = "commands"; //$NON-NLS-1$
	private static final String TEMPLATES_DIRECTORY_NAME = "templates"; //$NON-NLS-1$
	private static final String SAMPLES_DIRECTORY_NAME = "samples"; //$NON-NLS-1$
	private static final String CONFIG_DIRECTORY_NAME = "config"; //$NON-NLS-1$
	private static final String LOCALES_DIRECTORY_NAME = "locales"; //$NON-NLS-1$

	// constant to indicated we have no file instances
	private static final File[] NO_FILES = new File[0];

	// project directory name where project bundles are located
	private static final String BUILTIN_BUNDLES = "bundles"; //$NON-NLS-1$

	// special file name used to define a bundle
	public static final String BUNDLE_FILE = "bundle.rb"; //$NON-NLS-1$

	private static final String RUBY_FILE_EXTENSION = ".rb"; //$NON-NLS-1$

	// locations for user rubles
	private static final String USER_HOME_PROPERTY = "user.home"; //$NON-NLS-1$
	private static final String USER_BUNDLE_DIRECTORY_GENERAL = "Aptana Rubles"; //$NON-NLS-1$
	private static final String USER_BUNDLE_DIRECTORY_MACOSX = "/Documents/Aptana Rubles"; //$NON-NLS-1$

	// singleton instance
	private static BundleManager INSTANCE;

	/**
	 * Return the singleton instance of BundleManager
	 * 
	 * @return The singleton instance of BundleManager
	 */
	public static BundleManager getInstance()
	{
		return getInstance(null, null);
	}

	/**
	 * Return the singleton instance of BundleManager. Optional arguments allow for the explicit definition of the
	 * directories used for application bundles and user bundles. However, this should only be used by unit tests. In
	 * other words, don't use this unless you know what you're doing
	 * 
	 * @param applicationBundlesPath
	 *            The path to use when looking for application bundles. Note this may be null in which case the standard
	 *            default location will be used
	 * @param userBundlesPath
	 *            The path to use when looking for user bundles. Note this may be null in which case the standard
	 *            default location will be used
	 * @return The singleton instance of BundleManager
	 */
	public static synchronized BundleManager getInstance(String applicationBundlesPath, String userBundlesPath)
	{
		if (INSTANCE == null)
		{
			// create new instance
			INSTANCE = new BundleManager();
			INSTANCE.initializeBundlePaths();
		}

		// setup application bundles path
		if (!StringUtil.isEmpty(applicationBundlesPath))
		{
			// NOTE: setting the application bundles path directly is done for testing purposes only, so we wipe any
			// loaded application bundle paths and store the specified path only
			INSTANCE.applicationBundlesPaths = CollectionsUtil.newList(applicationBundlesPath);
		}

		// setup user bundles path
		if (!StringUtil.isEmpty(userBundlesPath))
		{
			INSTANCE.userBundlesPath = userBundlesPath;
		}

		return INSTANCE;
	}

	/**
	 * initializeBundlePaths
	 */
	private void initializeBundlePaths()
	{
		if (ScriptingActivator.getDefault() != null) // For when run outside the IDE...
		{
			final List<String> paths = new ArrayList<String>();

			// @formatter:off
			EclipseUtil.processConfigurationElements(
				ScriptingActivator.PLUGIN_ID,
				APPLICATION_BUNDLE_PATHS_ID,
				new IConfigurationElementProcessor()
				{
					public void processElement(IConfigurationElement element)
					{
						String path = element.getAttribute(ATTR_PATH);

						IExtension declaring = element.getDeclaringExtension();
						String declaringPluginID = declaring.getNamespaceIdentifier();
						Bundle bundle = Platform.getBundle(declaringPluginID);
						URL url = bundle.getEntry(path);
						String urlAsPath = ResourceUtil.resourcePathToString(url);

						if (urlAsPath != null && urlAsPath.length() > 0)
						{
							paths.add(urlAsPath);
						}
						else
						{
							String message = MessageFormat.format(
								"Unable to convert resource URL in plugin {0} to a string: {1}", //$NON-NLS-1$
								declaringPluginID,
								url
							);

							IdeLog.logError(ScriptingActivator.getDefault(), message);
						}
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(TAG_BUNDLE_PATH);
					}
				}
			);
			// @formatter:on

			this.applicationBundlesPaths = paths;
		}

		// get possible user override
		boolean validUserBundlePath = false;
		String userBundlePathOverride = System.getProperty(IScriptingSystemProperties.RUBLE_USER_LOCATION);

		if (userBundlePathOverride != null)
		{
			File f = new File(userBundlePathOverride);

			if (f.exists())
			{
				if (f.isDirectory())
				{
					if (f.canRead() && f.canWrite())
					{
						validUserBundlePath = true;
					}
					else
					{
						IdeLog.logError(ScriptingActivator.getDefault(), MessageFormat
								.format(Messages.BundleManager_USER_PATH_NOT_READ_WRITE, f.getAbsolutePath()));
					}
				}
				else
				{
					IdeLog.logError(ScriptingActivator.getDefault(),
							MessageFormat.format(Messages.BundleManager_USER_PATH_NOT_DIRECTORY, f.getAbsolutePath()));
				}
			}
			else
			{
				// try to create the path
				validUserBundlePath = f.mkdirs();
			}

			if (validUserBundlePath)
			{
				this.userBundlesPath = f.getAbsolutePath();
			}
			else
			{
				IdeLog.logError(ScriptingActivator.getDefault(),
						MessageFormat.format(Messages.BundleManager_USER_PATH_NOT_DIRECTORY, f.getAbsolutePath()));
			}
		}

		if (!validUserBundlePath)
		{
			String userHome = System.getProperty(USER_HOME_PROPERTY);
			// setup default user bundles path
			if (PlatformUtil.isMac() || PlatformUtil.isLinux())
			{
				this.userBundlesPath = userHome + USER_BUNDLE_DIRECTORY_MACOSX;
			}
			else
			{
				this.userBundlesPath = userHome + File.separator + USER_BUNDLE_DIRECTORY_GENERAL;
			}
		}
	}

	/**
	 * Determine if the specified path is one of the special directories in a bundle: commands, snippets, templates,
	 * samples, etc.
	 * 
	 * @param path
	 *            The path to test. This may be null, in which case this predicate will return false
	 * @return A boolean indicating if the specified directory is "special"
	 */
	static boolean isSpecialDirectory(File path)
	{
		boolean result = false;

		if (path != null)
		{
			String pathString = path.getName();

			// @formatter:off
			result = COMMANDS_DIRECTORY_NAME.equals(pathString)
					|| SNIPPETS_DIRECTORY_NAME.equals(pathString)
					|| TEMPLATES_DIRECTORY_NAME.equals(pathString)
					|| SAMPLES_DIRECTORY_NAME.equals(pathString);
			// @formatter:on
		}

		return result;
	}

	/**
	 * counter to cycle through for use in enforcing max parallel bundle loads. We compare versus the number of
	 * available processors reported by Java's Runtime.
	 */
	private int counter = 0;

	private List<String> applicationBundlesPaths;
	private String userBundlesPath;

	// TODO We should do a better job synchronizing these collections. We need to synchronize the Lists inside this
	// first map too
	private Map<File, List<BundleElement>> _bundlesByPath;
	private Map<String, BundleEntry> _entriesByName;

	private List<BundleVisibilityListener> _bundleVisibilityListeners;
	private List<ElementVisibilityListener> _elementVisibilityListeners;
	private List<LoadCycleListener> _loadCycleListeners;

	/**
	 * This is a pool to reduce duplicated string values eating up RAM. This happens most often with paths (like say in
	 * MenuElements)
	 */
	private Map<String, String> _stringPool;

	/**
	 * The number of bundles to load in parallel. Uninitialized value is -1. After initialization, value must be an
	 * integer, value of 1 (meaning only load one at a time sequentially) or greater.
	 */
	private int fBundlesToLoadInParallel = -1;

	/**
	 * Create a new instance of BundleManager and initialize its internal structure. Note that this constructor is
	 * private so it can only be instantiated within a static method in this class
	 */
	private BundleManager()
	{
		// NOTE: It is very likely that we have at least one bundle, so pre-create these maps. This gets rid of a number
		// of null checks and allows us to lock on the field directly instead of using separate locks
		this._bundlesByPath = new HashMap<File, List<BundleElement>>();
		this._entriesByName = new HashMap<String, BundleEntry>();

		this._stringPool = new HashMap<String, String>();

		// NOTE: similar logic for these guys too
		this._bundleVisibilityListeners = new ArrayList<BundleVisibilityListener>();
		this._elementVisibilityListeners = new ArrayList<ElementVisibilityListener>();
		this._loadCycleListeners = new ArrayList<LoadCycleListener>();
	}

	/**
	 * Add a new BundleElement to the scripting environment. Note this is called from the JRuby framework
	 * 
	 * @param bundle
	 */
	public void addBundle(BundleElement bundle)
	{
		if (bundle != null)
		{
			File bundleFile = bundle.getBundleDirectory();

			// store bundle by path
			synchronized (this._bundlesByPath)
			{
				if (this._bundlesByPath.containsKey(bundleFile) == false)
				{
					this._bundlesByPath.put(bundleFile, CollectionsUtil.newList(bundle));
				}
				else
				{
					List<BundleElement> bundles = this._bundlesByPath.get(bundleFile);
					bundles.add(bundle);
				}
			}

			// store bundle by name
			String name = bundle.getDisplayName();

			synchronized (this._entriesByName)
			{
				if (this._entriesByName.containsKey(name) == false)
				{
					BundleEntry entry = new BundleEntry(name);
					entry.addBundle(bundle);
					this._entriesByName.put(name, entry);
				}
				else
				{
					BundleEntry entry = this._entriesByName.get(name);
					entry.addBundle(bundle);
				}
			}
		}
	}

	/**
	 * Add a new listener to fire when any bundle changes visibility.
	 * 
	 * @param listener
	 *            The listener to fire upon bundle visibility changes. A listener is added only once. Null values are
	 *            ignored
	 */
	public void addBundleVisibilityListener(BundleVisibilityListener listener)
	{
		if (listener != null)
		{
			synchronized (this._bundleVisibilityListeners)
			{
				if (!this._bundleVisibilityListeners.contains(listener))
				{
					this._bundleVisibilityListeners.add(listener);
				}
			}
		}
	}

	/**
	 * Add a new listener to fire when any bundle element changes visibility.
	 * 
	 * @param listener
	 *            The listener to fire upon element visibility changes. A listener is added only once. Null values are
	 *            ignored
	 */
	public void addElementVisibilityListener(ElementVisibilityListener listener)
	{
		if (listener != null)
		{
			synchronized (this._elementVisibilityListeners)
			{
				if (!this._elementVisibilityListeners.contains(listener))
				{
					this._elementVisibilityListeners.add(listener);
				}
			}
		}
	}

	/**
	 * Add a new listener to fire when any script is loaded, unloaded, or reloaded.
	 * 
	 * @param listener
	 *            The listener to fire when scripts are processed. A listener is added only once. Null values are
	 *            ignored
	 */
	public void addLoadCycleListener(LoadCycleListener listener)
	{
		if (listener != null)
		{
			synchronized (this._loadCycleListeners)
			{
				if (!this._loadCycleListeners.contains(listener))
				{
					this._loadCycleListeners.add(listener);
				}
			}
		}
	}

	/**
	 * Fire a bundle-became-hidden visibility event to all listeners
	 * 
	 * @param bundle
	 *            The bundle entry affected by this visibility event. Null values are ignored and do not cause the event
	 *            to fire.
	 */
	void fireBundleBecameHiddenEvent(BundleEntry entry)
	{
		if (entry != null)
		{
			for (BundleVisibilityListener listener : this.getBundleVisibilityListeners())
			{
				try
				{
					listener.bundlesBecameHidden(entry);
				}
				catch (Throwable t)
				{
					IdeLog.logError(ScriptingActivator.getDefault(),
							Messages.BundleManager_Bundle_Became_Hidden_Event_Error, t);
				}
			}
		}
	}

	/**
	 * Fire a bundle-became-visible visibility event to all listeners
	 * 
	 * @param bundle
	 *            The bundle entry affected by this visibility event. Null values are ignored and do not cause the event
	 *            to fire.
	 */
	void fireBundleBecameVisibleEvent(BundleEntry entry)
	{
		if (entry != null)
		{
			for (BundleVisibilityListener listener : this.getBundleVisibilityListeners())
			{
				try
				{
					listener.bundlesBecameVisible(entry);
				}
				catch (Throwable t)
				{
					IdeLog.logError(ScriptingActivator.getDefault(),
							Messages.BundleManager_Bundle_Became_Visible_Event_Error, t);
				}
			}
		}
	}

	/**
	 * Fire an element-became-hidden visibility event to all listeners
	 * 
	 * @param element
	 *            The element affected by this visibility event. Null values are ignored and do not cause the event to
	 *            fire.
	 */
	void fireElementBecameHiddenEvent(AbstractElement element)
	{
		if (element != null)
		{
			for (ElementVisibilityListener listener : this.getElementVisibilityListeners())
			{
				try
				{
					listener.elementBecameHidden(element);
				}
				catch (Throwable t)
				{
					IdeLog.logError(ScriptingActivator.getDefault(),
							Messages.BundleManager_Element_Became_Hidden_Event_Error, t);
				}
			}
		}
	}

	/**
	 * Fire an element-became-visible visibility event to all listeners
	 * 
	 * @param element
	 *            The element affected by this visibility event. Null values are ignored and do not cause the event to
	 *            fire.
	 */
	void fireElementBecameVisibleEvent(AbstractElement element)
	{
		if (element != null)
		{
			for (ElementVisibilityListener listener : this.getElementVisibilityListeners())
			{
				try
				{
					listener.elementBecameVisible(element);
				}
				catch (Throwable t)
				{
					IdeLog.logError(ScriptingActivator.getDefault(),
							Messages.BundleManager_Element_Became_Visible_Event_Error, t);
				}
			}
		}
	}

	/**
	 * Fire a script-loaded load cycle event to all listeners
	 * 
	 * @param path
	 *            The path affected by this load cycle event. Null values are ignored and do not cause the event to
	 *            fire.
	 */
	void fireScriptLoadedEvent(File script)
	{
		if (script != null)
		{
			for (LoadCycleListener listener : this.getLoadCycleListeners())
			{
				try
				{
					listener.scriptLoaded(script);
				}
				catch (Throwable t)
				{
					IdeLog.logError(ScriptingActivator.getDefault(), Messages.BundleManager_Script_Loaded_Event_Error,
							t);
				}
			}
		}
	}

	/**
	 * Fire a script-reloaded load cycle event to all listeners
	 * 
	 * @param path
	 *            The path affected by this load cycle event. Null values are ignored and do not cause the event to
	 *            fire.
	 */
	void fireScriptReloadedEvent(File script)
	{
		if (script != null)
		{
			for (LoadCycleListener listener : this.getLoadCycleListeners())
			{
				try
				{
					listener.scriptReloaded(script);
				}
				catch (Throwable t)
				{
					IdeLog.logError(ScriptingActivator.getDefault(), Messages.BundleManager_Script_Reloaded_Event_Error,
							t);
				}
			}
		}
	}

	/**
	 * Fire a script-unloaded load cycle event to all listeners
	 * 
	 * @param path
	 *            The path affected by this load cycle event. Null values are ignored and do not cause the event to
	 *            fire.
	 */
	void fireScriptUnloadedEvent(File script)
	{
		if (script != null)
		{
			for (LoadCycleListener listener : this.getLoadCycleListeners())
			{
				try
				{
					listener.scriptUnloaded(script);
				}
				catch (Throwable t)
				{
					IdeLog.logError(ScriptingActivator.getDefault(), Messages.BundleManager_Script_Unloaded_Event_Error,
							t);
				}
			}
		}
	}

	/**
	 * Return a list of all bundle elements categorized as application bundles
	 * 
	 * @return A list of bundles. This list will always be defined
	 */
	public List<BundleElement> getApplicationBundles()
	{
		List<BundleElement> result = new ArrayList<BundleElement>();

		if (applicationBundlesPaths != null)
		{
			// make local copy of active keys (files)
			Set<File> keys;

			synchronized (this._bundlesByPath)
			{
				keys = new HashSet<File>(this._bundlesByPath.keySet());
			}

			// filter set
			Set<File> applicationKeys = new HashSet<File>();

			for (File key : keys)
			{
				String path = key.getAbsolutePath();

				for (String applicationBundlesPath : applicationBundlesPaths)
				{
					if (path.startsWith(applicationBundlesPath))
					{
						applicationKeys.add(key);
					}
				}
			}

			// build result list
			for (File key : applicationKeys)
			{
				result.add(this.getBundleFromPath(key));
			}
		}

		return result;
	}

	/**
	 * Return the path that contains application bundles
	 * 
	 * @return
	 */
	List<String> getApplicationBundlesPaths()
	{
		return this.applicationBundlesPaths;
	}

	/**
	 * Return a list of commands in the specified bundle name. Note that bundle precedence is taken into account, so
	 * only visible elements are returned in this list
	 * 
	 * @param name
	 *            The name of the bundle to query
	 * @return A list of elements matching the specified criteria
	 */
	public List<CommandElement> getBundleCommands(String name)
	{
		BundleEntry entry = this.getBundleEntry(name);
		if (entry != null)
		{
			return entry.getCommands();
		}
		return Collections.emptyList();
	}

	/**
	 * Return a list of content assist elements in the specified bundle name. Note that bundle precedence is taken into
	 * account, so only visible elements are returned in this list
	 * 
	 * @param name
	 *            The name of the bundle to query
	 * @return A list of elements matching the specified criteria
	 */
	public List<ContentAssistElement> getBundleContentAssists(String name)
	{
		BundleEntry entry = this.getBundleEntry(name);
		if (entry != null)
		{
			return entry.getContentAssists();
		}
		return Collections.emptyList();
	}

	/**
	 * Return a list of directories within the specified directory. Each directory returned in the list becomes a
	 * candidate bundle determined by other methods.
	 * 
	 * @param bundlesDirectory
	 *            The directory to query for child directories. It is verified that the specified file is a directory
	 *            and is readable. Null values are ignored
	 * @return The list of directories within the specified directory
	 */
	protected List<File> getBundleDirectories(File bundlesDirectory)
	{
		File[] result = NO_FILES;

		if (bundlesDirectory != null && bundlesDirectory.isDirectory() && bundlesDirectory.canRead())
		{
			result = bundlesDirectory.listFiles(new FileFilter()
			{
				public boolean accept(File pathname)
				{
					return (pathname.isDirectory() && !pathname.getName().startsWith(".") //$NON-NLS-1$
							&& isValidBundleDirectory(pathname));
				}
			});
		}

		return Arrays.asList(result);
	}

	/**
	 * Return the bundle directory that contains the specified script. We traverse parents until we find a directory
	 * that {@link #isValidBundleDirectory(File)}
	 * 
	 * @param script
	 *            The descendant script used to locate the bundle directory. Null values are ignored
	 * @return The bundle directory or null if the specified script was null
	 */
	public File getBundleDirectory(File script)
	{
		File result = null;

		if (script != null)
		{
			IPath path = Path.fromOSString(script.getAbsolutePath());
			if (script.isFile())
			{
				// Cheat a little and skip to parent already if this is a file
				path = path.removeLastSegments(1);
			}
			// Search up the hierarchy until we hit the bundle dir
			File file = path.toFile();
			while (true)
			{
				if (isValidBundleDirectory(file))
				{
					return file;
				}
				if (path.segmentCount() == 0)
				{
					break;
				}
				path = path.removeLastSegments(1);
				file = path.toFile();
			}
		}

		return result;
	}

	/**
	 * Return the underlying bundle entry which contains all bundle elements of the specified name
	 * 
	 * @param name
	 *            The bundle name used to locate the bundle entry
	 * @return The bundle entry or null if no bundle exists for the specified name
	 */
	public BundleEntry getBundleEntry(String name)
	{
		synchronized (this._entriesByName)
		{
			return this._entriesByName.get(name);
		}
	}

	/**
	 * Return a list of environment elements in the specified bundle name. Note that bundle precedence is taken into
	 * account, so only visible elements are returned in this list
	 * 
	 * @param name
	 *            The name of the bundle to query
	 * @return A list of elements matching the specified criteria
	 */
	public List<EnvironmentElement> getBundleEnvs(String name)
	{
		BundleEntry entry = this.getBundleEntry(name);
		if (entry != null)
		{
			return entry.getEnvs();
		}
		return Collections.emptyList();
	}

	/**
	 * Find the bundle element contained within the specified directory
	 * 
	 * @param bundleDirectory
	 *            A bundle directory. Null values are ignored
	 * @return The bundle element that is defined within the specified directory or null if none is defined
	 */
	public BundleElement getBundleFromPath(File bundleDirectory)
	{
		List<BundleElement> bundles = null;
		BundleElement result = null;

		synchronized (this._bundlesByPath)
		{
			if (this._bundlesByPath.containsKey(bundleDirectory))
			{
				bundles = new ArrayList<BundleElement>(this._bundlesByPath.get(bundleDirectory));
			}
		}

		if (bundles != null)
		{
			int size = bundles.size();

			if (size > 0)
			{
				result = bundles.get(size - 1);
			}
		}

		return result;
	}

	/**
	 * Find the bundle element contained within the specified directory
	 * 
	 * @param path
	 *            A bundle directory. Null values are ignored
	 * @return The bundle element that is defined within the specified directory or null if none is defined
	 */
	public BundleElement getBundleFromPath(String path)
	{
		if (path != null)
		{
			return this.getBundleFromPath(new File(path));
		}

		return null;
	}

	/**
	 * Return a list of paths to be used by ruby when locating imported scripts
	 * 
	 * @param bundleDirectory
	 *            The bundle directory from which load paths will be calculated
	 * @return A list of ruby load paths
	 */
	protected List<String> getBundleLoadPaths(File bundleDirectory)
	{
		List<String> result = null;

		if (bundleDirectory != null)
		{
			File bundleFile = new File(bundleDirectory, BUNDLE_FILE);
			String bundleName = BundleUtils.getBundleName(bundleFile);

			if (bundleName != null)
			{
				String defaultName = BundleUtils.getDefaultBundleName(bundleFile.getAbsolutePath());

				if (bundleName.equals(defaultName) == false)
				{
					result = this.getBundleLoadPaths(bundleName);
				}
			}

			if (result == null)
			{
				result = new ArrayList<String>(getScriptingEngine().getContributedLoadPaths());
			}

			result.add(0, BundleUtils.getBundleLibDirectory(bundleDirectory));
		}

		return result;
	}

	/**
	 * Return a list of load paths for the specified bundle name. Note that bundle precedence is taken into account, so
	 * only visible load paths are returned in this list
	 * 
	 * @param name
	 *            The name of the bundle to query
	 * @return A list of elements matching the specified criteria
	 */
	protected List<String> getBundleLoadPaths(String name)
	{
		BundleEntry entry = this.getBundleEntry(name);
		List<String> result = new ArrayList<String>();

		if (entry != null)
		{
			result.addAll(entry.getLoadPaths());
		}

		result.addAll(getScriptingEngine().getContributedLoadPaths());

		return result;
	}

	/**
	 * Return a list of snippet category elements in the specified bundle name. Note that bundle precedence is taken
	 * into account, so only visible elements are returned in this list
	 * 
	 * @param name
	 *            The name of the bundle to query
	 * @return A list of elements matching the specified criteria
	 */
	public List<SnippetCategoryElement> getBundleSnippetCategories(String name)
	{
		BundleEntry entry = this.getBundleEntry(name);
		if (entry != null)
		{
			return entry.getSnippetCategories();
		}
		return Collections.emptyList();
	}

	/**
	 * Return a list of snippet elements in the specified bundle name. Note that bundle precedence is taken into
	 * account, so only visible elements are returned in this list
	 * 
	 * @param name
	 *            The name of the bundle to query
	 * @return A list of elements matching the specified criteria
	 */
	public List<SnippetElement> getBundleSnippets(String name)
	{
		BundleEntry entry = this.getBundleEntry(name);
		if (entry != null)
		{
			return entry.getSnippets();
		}
		return Collections.emptyList();
	}

	/**
	 * Return a list of menu elements in the specified bundle name. Note that bundle precedence is taken into account,
	 * so only visible elements are returned in this list
	 * 
	 * @param name
	 *            The name of the bundle to query
	 * @return A list of elements matching the specified criteria
	 */
	public List<MenuElement> getBundleMenus(String name)
	{
		BundleEntry entry = this.getBundleEntry(name);
		if (entry != null)
		{
			return entry.getMenus();
		}
		return Collections.emptyList();
	}

	/**
	 * Return a list of names for all active bundles
	 * 
	 * @return A list of bundle names
	 */
	public List<String> getBundleNames()
	{
		synchronized (this._entriesByName)
		{
			return new ArrayList<String>(this._entriesByName.keySet());
		}
	}

	/**
	 * Return a list of smart typing pairs elements in the specified bundle name. Note that bundle precedence is taken
	 * into account, so only visible elements are returned in this list
	 * 
	 * @param name
	 *            The name of the bundle to query
	 * @return A list of elements matching the specified criteria
	 */
	public List<SmartTypingPairsElement> getBundlePairs(String name)
	{
		BundleEntry entry = this.getBundleEntry(name);
		if (entry != null)
		{
			return entry.getPairs();
		}
		return Collections.emptyList();
	}

	/**
	 * Determine the bundle precedence of the specified path. Note this method assumes that if a path is not an
	 * application bundle nor a user bundle, then it is a project bundle. This may not be exactly true. A more thorough
	 * check against the current workspace will be required to verify that case.
	 * 
	 * @param path
	 *            The path to use to determine bundle precedence
	 * @return The BundlePrecedence type
	 */
	public BundlePrecedence getBundlePrecedence(File path)
	{
		return this.getBundlePrecedence(path.getAbsolutePath());
	}

	/**
	 * Determine the bundle precedence of the specified path. Note this method assumes that if a path is not an
	 * application bundle nor a user bundle, then it is a project bundle. This may not be exactly true. A more thorough
	 * check against the current workspace will be required to verify that case.
	 * 
	 * @param path
	 *            The path to use to determine bundle precedence
	 * @return The BundlePrecedence type
	 */
	public BundlePrecedence getBundlePrecedence(String path)
	{
		if (path != null && applicationBundlesPaths != null)
		{
			for (String applicationBundlesPath : applicationBundlesPaths)
			{
				if (path.startsWith(applicationBundlesPath))
				{
					return BundlePrecedence.APPLICATION;
				}
			}

			if (this.userBundlesPath != null && path.startsWith(this.userBundlesPath))
			{
				return BundlePrecedence.USER;
			}
		}

		return BundlePrecedence.PROJECT;
	}

	/**
	 * Return a list of all scripts that need to be processed in a specified bundle directory. Note that the items are
	 * returned in the following order: bundle.rb, commands, snippets, project templates, and then samples.
	 * 
	 * @param bundleDirectory
	 *            The bundle directory used to search for scripts
	 * @return A list of all active scripts in the specified bundle directory
	 */
	protected List<File> getBundleScripts(File bundleDirectory)
	{
		List<File> result = new ArrayList<File>();

		if (this.isValidBundleDirectory(bundleDirectory))
		{
			// check for a top-level bundle.rb file
			File bundleFile = new File(bundleDirectory, BUNDLE_FILE);

			if (bundleFile.exists())
			{
				result.add(bundleFile);
			}

			// check for scripts inside "commands" directory
			File commandsDirectory = new File(bundleDirectory, COMMANDS_DIRECTORY_NAME);

			result.addAll(this.getScriptsFromDirectory(commandsDirectory));

			// check for scripts inside "snippets" directory
			File snippetsDirectory = new File(bundleDirectory, SNIPPETS_DIRECTORY_NAME);

			result.addAll(this.getScriptsFromDirectory(snippetsDirectory));

			// look for templates inside "templates" directory
			File templatesDirectory = new File(bundleDirectory, TEMPLATES_DIRECTORY_NAME);

			result.addAll(this.getScriptsFromDirectory(templatesDirectory));

			// look for samples inside "samples" directory
			File samplesDirectory = new File(bundleDirectory, SAMPLES_DIRECTORY_NAME);
			result.addAll(getScriptsFromDirectory(samplesDirectory));
		}

		return result;
	}

	/**
	 * Return a list of all the *.yml localization files that need to be processed in a specified bundle directory.
	 * 
	 * @param bundleDirectory
	 *            The bundle directory used to search for localization files
	 * @return A list of all *.yml files in the specified bundle's locales folder.
	 */
	protected List<File> localizationFiles(File bundleDirectory)
	{
		if (isValidBundleDirectory(bundleDirectory))
		{
			// check for yml files inside "config/locales" directory
			File directory = new File(new File(bundleDirectory, CONFIG_DIRECTORY_NAME), LOCALES_DIRECTORY_NAME);
			if (directory != null && directory.exists() && directory.canRead())
			{
				File[] ymlFiles = directory.listFiles(new FilenameFilter()
				{

					public boolean accept(File dir, String name)
					{
						return name.endsWith(".yml"); //$NON-NLS-1$
					}
				});
				return Arrays.asList(ymlFiles);
			}
		}
		return Collections.emptyList();
	}

	/**
	 * getBundleVisibilityListeners
	 * 
	 * @return
	 */
	protected List<BundleVisibilityListener> getBundleVisibilityListeners()
	{
		synchronized (this._bundleVisibilityListeners)
		{
			return new ArrayList<BundleVisibilityListener>(this._bundleVisibilityListeners);
		}
	}

	/**
	 * Return a list of all active commands. Note that bundle precedence is taken into account, so only visible elements
	 * are returned in this list. Note that this method is called from the scripting framework
	 * 
	 * @return A list of elements that are visible
	 */
	public List<CommandElement> getCommands()
	{
		return this.getCommands(null);
	}

	/**
	 * Return a list of all active commands. Note that bundle precedence is taken into account, so only visible elements
	 * are returned in this list
	 * 
	 * @param filter
	 *            A filter to apply to each active element. Only elements that pass the filter will be included in the
	 *            result. The filter may be null which is equivalent to a filter that returns true for all elements
	 * @return A list of elements that are visible and that pass the specified filter
	 */
	public List<CommandElement> getCommands(IModelFilter filter)
	{
		List<CommandElement> result = new ArrayList<CommandElement>();

		for (String name : this.getBundleNames())
		{
			CollectionsUtil.filter(getBundleCommands(name), result, filter);
		}

		return result;
	}

	/**
	 * Return a list of all active content assist elements. Note that bundle precedence is taken into account, so only
	 * visible elements are returned in this list
	 * 
	 * @param filter
	 *            A filter to apply to each active element. Only elements that pass the filter will be included in the
	 *            result. The filter may be null which is equivalent to a filter that returns true for all elements
	 * @return A list of elements that are visible and that pass the specified filter
	 */
	public List<ContentAssistElement> getContentAssists(IModelFilter filter)
	{
		List<ContentAssistElement> result = new ArrayList<ContentAssistElement>();

		for (String name : this.getBundleNames())
		{
			CollectionsUtil.filter(getBundleContentAssists(name), result, filter);
		}

		return result;
	}

	/**
	 * getDecreaseIndentRegexp
	 * 
	 * @param scope
	 * @return
	 */
	public RubyRegexp getDecreaseIndentRegexp(String scope)
	{
		Map<IScopeSelector, RubyRegexp> map = new HashMap<IScopeSelector, RubyRegexp>();
		for (String bundleName : this.getBundleNames())
		{
			BundleEntry bundleEntry = this.getBundleEntry(bundleName);
			map.putAll(bundleEntry.getDecreaseIndentMarkers());
		}
		if (map.isEmpty())
		{
			return null;
		}
		IScopeSelector bestMatch = ScopeSelector.bestMatch(map.keySet(), scope);
		if (bestMatch == null)
		{
			return null;
		}
		return map.get(bestMatch);
	}

	/**
	 * getElementVisibilityListeners
	 * 
	 * @return
	 */
	protected List<ElementVisibilityListener> getElementVisibilityListeners()
	{
		synchronized (this._elementVisibilityListeners)
		{
			return new ArrayList<ElementVisibilityListener>(this._elementVisibilityListeners);
		}
	}

	/**
	 * Return a list of all active environment elements. Note that bundle precedence is taken into account, so only
	 * visible elements are returned in this list
	 * 
	 * @param filter
	 *            A filter to apply to each active element. Only elements that pass the filter will be included in the
	 *            result. The filter may be null which is equivalent to a filter that returns true for all elements
	 * @return A list of elements that are visible and that pass the specified filter
	 */
	public List<EnvironmentElement> getEnvs(IModelFilter filter)
	{
		List<EnvironmentElement> result = new ArrayList<EnvironmentElement>();

		for (String name : this.getBundleNames())
		{
			CollectionsUtil.filter(getBundleEnvs(name), result, filter);
		}

		return result;
	}

	/**
	 * Get a list of all active and executable commands. Note that bundle precedence is taken into account, so only
	 * visible elements are returned in this list. This method is convenience method and is equivalent to constructing
	 * an AndFilter with an IsExecutableCommandFilter and the specified filter
	 * 
	 * @param filter
	 *            A filter to apply to each active command. Only commands which pass the filter will be included in the
	 *            result. The filter may be null which is equivalent to a filter that returns true for all elements
	 * @return A list of commands that are visible, executable, and that pass the specified filter
	 */
	public List<CommandElement> getExecutableCommands(IModelFilter filter)
	{
		IModelFilter executableFilter = new IsExecutableCommandFilter();

		filter = (filter == null) ? executableFilter : new AndFilter(filter, executableFilter);

		return this.getCommands(filter);
	}

	/**
	 * getFoldingStartRegexp
	 * 
	 * @param scope
	 * @return
	 */
	public RubyRegexp getFoldingStartRegexp(String scope)
	{
		Map<IScopeSelector, RubyRegexp> map = new HashMap<IScopeSelector, RubyRegexp>();
		for (String bundleName : this.getBundleNames())
		{
			BundleEntry bundleEntry = this.getBundleEntry(bundleName);
			map.putAll(bundleEntry.getFoldingStartMarkers());
		}
		if (map.isEmpty())
		{
			return null;
		}
		IScopeSelector bestMatch = ScopeSelector.bestMatch(map.keySet(), scope);
		if (bestMatch == null)
		{
			return null;
		}
		return map.get(bestMatch);
	}

	/**
	 * getFoldingStopRegexp
	 * 
	 * @param scope
	 * @return
	 */
	public RubyRegexp getFoldingStopRegexp(String scope)
	{
		Map<IScopeSelector, RubyRegexp> map = new HashMap<IScopeSelector, RubyRegexp>();
		for (String bundleName : this.getBundleNames())
		{
			BundleEntry bundleEntry = this.getBundleEntry(bundleName);
			map.putAll(bundleEntry.getFoldingStopMarkers());
		}
		if (map.isEmpty())
		{
			return null;
		}
		IScopeSelector bestMatch = ScopeSelector.bestMatch(map.keySet(), scope);
		if (bestMatch == null)
		{
			return null;
		}
		return map.get(bestMatch);
	}

	/**
	 * getIncreaseIndentRegexp
	 * 
	 * @param scope
	 * @return
	 */
	public RubyRegexp getIncreaseIndentRegexp(String scope)
	{
		Map<IScopeSelector, RubyRegexp> map = new HashMap<IScopeSelector, RubyRegexp>();
		for (String bundleName : this.getBundleNames())
		{
			BundleEntry bundleEntry = this.getBundleEntry(bundleName);
			map.putAll(bundleEntry.getIncreaseIndentMarkers());
		}
		if (map.isEmpty())
		{
			return null;
		}
		IScopeSelector bestMatch = ScopeSelector.bestMatch(map.keySet(), scope);
		if (bestMatch == null)
		{
			return null;
		}
		return map.get(bestMatch);
	}

	/**
	 * getLoadCycleListeners
	 * 
	 * @return
	 */
	protected List<LoadCycleListener> getLoadCycleListeners()
	{
		synchronized (this._loadCycleListeners)
		{
			return new ArrayList<LoadCycleListener>(this._loadCycleListeners);
		}
	}

	/**
	 * Return a list of all active top-level menu elements. Note that bundle precedence is taken into account, so only
	 * visible elements are returned in this list
	 * 
	 * @param filter
	 *            A filter to apply to each active element. Only elements that pass the filter will be included in the
	 *            result. The filter may be null which is equivalent to a filter that returns true for all elements
	 * @return A list of elements that are visible and that pass the specified filter
	 */
	public List<MenuElement> getMenus(IModelFilter filter)
	{
		List<MenuElement> result = new ArrayList<MenuElement>();

		for (String name : this.getBundleNames())
		{
			CollectionsUtil.filter(getBundleMenus(name), result, filter);
		}

		return result;
	}

	/**
	 * Return a list of all active smart typing pairs elements. Note that bundle precedence is taken into account, so
	 * only visible elements are returned in this list
	 * 
	 * @param filter
	 *            A filter to apply to each active element. Only elements that pass the filter will be included in the
	 *            result. The filter may be null which is equivalent to a filter that returns true for all elements
	 * @return A list of elements that are visible and that pass the specified filter
	 */
	public List<SmartTypingPairsElement> getPairs(IModelFilter filter)
	{
		List<SmartTypingPairsElement> result = new ArrayList<SmartTypingPairsElement>();

		for (String name : this.getBundleNames())
		{
			CollectionsUtil.filter(getBundlePairs(name), result, filter);
		}

		return result;
	}

	/**
	 * Return a list of all active project template elements. Note that bundle precedence is taken into account, so only
	 * visible elements are returned in this list
	 * 
	 * @param filter
	 *            A filter to apply to each active element. Only elements that pass the filter will be included in the
	 *            result. The filter may be null which is equivalent to a filter that returns true for all elements
	 * @return A list of elements that are visible and that pass the specified filter
	 */
	public List<ProjectTemplateElement> getProjectTemplates(IModelFilter filter)
	{
		List<ProjectTemplateElement> result = new ArrayList<ProjectTemplateElement>();

		for (String name : this.getBundleNames())
		{
			BundleEntry bundleEntry = this.getBundleEntry(name);

			CollectionsUtil.filter(bundleEntry.getProjectTemplates(), result, filter);
		}

		return result;
	}

	/**
	 * Return a list of all active project sample elements. Note that bundle precedence is taken into account, so only
	 * visible elements are returned in this list
	 * 
	 * @param filter
	 *            A filter to apply to each active element. Only elements that pass the filter will be included in the
	 *            result. The filter may be null which is equivalent to a filter that returns true for all elements
	 * @return A list of elements that are visible and that pass the specified filter
	 */
	public List<ProjectSampleElement> getProjectSamples(IModelFilter filter)
	{
		List<ProjectSampleElement> result = new ArrayList<ProjectSampleElement>();

		for (String name : this.getBundleNames())
		{
			BundleEntry bundleEntry = this.getBundleEntry(name);

			CollectionsUtil.filter(bundleEntry.getProjectSamples(), result, filter);
		}

		return result;
	}

	/**
	 * Return a list of all active snippets. Note that bundle precedence is taken into account, so only visible elements
	 * are returned in this list
	 * 
	 * @param filter
	 *            A filter to apply to each active element. Only elements that pass the filter will be included in the
	 *            result. The filter may be null which is equivalent to a filter that returns true for all elements
	 * @return A list of elements that are visible and that pass the specified filter
	 */
	public List<SnippetElement> getSnippets(IModelFilter filter)
	{
		List<SnippetElement> result = new ArrayList<SnippetElement>();

		for (String name : this.getBundleNames())
		{
			CollectionsUtil.filter(getBundleSnippets(name), result, filter);
		}

		return result;
	}

	/**
	 * Return a list of all active snippet categories. Note that bundle precedence is taken into account, so only
	 * visible elements are returned in this list
	 * 
	 * @param filter
	 *            A filter to apply to each active element. Only elements that pass the filter will be included in the
	 *            result. The filter may be null which is equivalent to a filter that returns true for all elements
	 * @return A list of elements that are visible and that pass the specified filter
	 */
	public List<SnippetCategoryElement> getSnippetCategories(IModelFilter filter)
	{
		List<SnippetCategoryElement> result = new ArrayList<SnippetCategoryElement>();

		for (String name : this.getBundleNames())
		{
			CollectionsUtil.filter(getBundleSnippetCategories(name), result, filter);
		}

		return result;
	}

	/**
	 * Return a list of ruby files contained within a specified directory. The resulting list is sorted by file name.
	 * Note that the search for scripts is non-recursive and only includes children of the specified directory.
	 * 
	 * @param directory
	 *            The directory used to search for ruby files. Null values are ignored.
	 * @return The list of scripts in the specified directory
	 */
	protected List<File> getScriptsFromDirectory(File directory)
	{
		File[] result = NO_FILES;

		if (directory != null && directory.exists() && directory.canRead())
		{
			result = directory.listFiles(new FileFilter()
			{
				public boolean accept(File pathname)
				{
					return pathname.isFile() && pathname.getName().toLowerCase().endsWith(RUBY_FILE_EXTENSION);
				}
			});

			Arrays.sort(result, new Comparator<File>()
			{
				public int compare(File o1, File o2)
				{
					return o1.getName().compareTo(o2.getName());
				}
			});
		}

		return Arrays.asList(result);
	}

	/**
	 * getTopLevelScope
	 * 
	 * @param fileName
	 * @return
	 */
	public String getTopLevelScope(String fileName)
	{
		String result = null;
		String matchedPattern = null;

		for (String bundleName : this.getBundleNames())
		{
			BundleEntry bundleEntry = this.getBundleEntry(bundleName);
			Map<String, String> registry = bundleEntry.getFileTypeRegistry();

			for (Map.Entry<String, String> entry : registry.entrySet())
			{
				String pattern = entry.getKey();

				// Escape periods in pattern (for regexp)
				pattern = DOT_PATTERN.matcher(pattern).replaceAll("\\\\."); //$NON-NLS-1$

				// Replace * wildcard pattern with .+? regexp
				pattern = STAR_PATTERN.matcher(pattern).replaceAll(".+?"); //$NON-NLS-1$

				if (fileName.matches(pattern))
				{
					if (result == null)
					{
						result = entry.getValue();
						matchedPattern = pattern;
					}
					else
					{
						// Now check to see if this is more specific than the existing match before we set this as our
						// return value
						// TODO: Check for simple case where one is a subset scope of the other, use the more specific
						// one and move on

						// split on periods to see the specificity of scope name
						int existingLength = StringUtil.characterInstanceCount(result, '.') + 1;
						int newLength = StringUtil.characterInstanceCount(entry.getValue(), '.') + 1;

						if (newLength > existingLength)
						{
							result = entry.getValue();
							matchedPattern = pattern;
						}
						else if (newLength == existingLength)
						{
							// Now we need to check if the file matching pattern is more specific
							// FIXME: Just using length is hacky and can be incorrect
							if (pattern.length() > matchedPattern.length())
							{
								result = entry.getValue();
								matchedPattern = pattern;
							}
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * Return the path that contains user bundles
	 * 
	 * @return
	 */
	public String getUserBundlesPath()
	{
		return this.userBundlesPath;
	}

	/**
	 * A predicate that returns if the specified bundle directory contains a bundle that has been processed
	 * 
	 * @param bundleDirectory
	 *            A directory that may contain a processed bundle
	 * @return Returns true if the specified directory has a bundle element associated with it
	 */
	public boolean hasBundleAtPath(File bundleDirectory)
	{
		synchronized (this._bundlesByPath)
		{
			return this._bundlesByPath.containsKey(bundleDirectory);
		}
	}

	/**
	 * Determine if the specified directory is in the user bundle path
	 * 
	 * @param bundleDirectory
	 * @return
	 */
	protected boolean isUserBundleDirectory(File bundleDirectory)
	{
		if (bundleDirectory != null)
		{
			return (getBundlePrecedence(bundleDirectory) == BundlePrecedence.USER);
		}

		return false;
	}

	/**
	 * Determine if the specified directory minimally defines a bundle. In order to return true, the specified directory
	 * must exist, must be a directory, it must be readable, and it must contain a bundle.rb file
	 * 
	 * @param bundleDirectory
	 *            The directory to test
	 * @return
	 */
	protected boolean isValidBundleDirectory(File bundleDirectory)
	{
		if (bundleDirectory.isDirectory() && bundleDirectory.canRead())
		{
			File bundleFile = new File(bundleDirectory.getAbsolutePath(), BUNDLE_FILE);

			// NOTE: We verify readability when we try to execute the scripts in the bundle
			// so, there's no need to do that here.
			if (bundleFile.isFile())
			{
				return true;
			}
		}

		return false;
	};

	/**
	 * Load all application bundles
	 */
	protected void loadApplicationBundles()
	{
		if (applicationBundlesPaths != null)
		{
			for (String applicationBundle : applicationBundlesPaths)
			{
				File applicationBundlesDirectory = new File(applicationBundle);

				for (File bundle : this.getBundleDirectories(applicationBundlesDirectory))
				{
					this.loadBundle(bundle);
				}
			}
		}
	}

	/**
	 * Load the bundle in the specified directory
	 * 
	 * @param bundleDirectory
	 *            The directory containing a bundle and its children
	 */
	public void loadBundle(File bundleDirectory)
	{
		loadBundle(bundleDirectory, false);
	}

	/**
	 * Load the bundle in the specified directory
	 * 
	 * @param bundleDirectory
	 *            The directory containing a bundle and its children
	 * @param wait
	 *            Allows us to be synchronous when executing this
	 */
	public void loadBundle(File bundleDirectory, boolean wait)
	{
		BundleLoadJob job = new BundleLoadJob(bundleDirectory);

		// If we're running and not testing, schedule in parallel but limit how many run in parallel based on processor
		// count
		if (!EclipseUtil.isTesting() && Platform.isRunning())
		{
			job.setRule(new SerialPerObjectRule(counter++));

			if (counter >= maxBundlesToLoadInParallel())
			{
				counter = 0;
			}

			job.setPriority(Job.SHORT);
			job.schedule();
			if (wait)
			{
				try
				{
					job.join();
				}
				catch (InterruptedException e)
				{
					// ignore error
				}
			}
		}
		else
		{
			// We're already running inline, so don't need to ever "wait"
			job.run(new NullProgressMonitor());
		}
	}

	/**
	 * We cap the number of bundles to load in parallel. By default we'll cap it based on number of processors in the
	 * system. You can override this by setting an integer value in the system property
	 * {@value #STUDIO_BUNDLE_LOAD_CONCURRENCY}
	 * 
	 * @return
	 */
	protected synchronized int maxBundlesToLoadInParallel()
	{
		if (fBundlesToLoadInParallel < 0)
		{
			fBundlesToLoadInParallel = Runtime.getRuntime().availableProcessors();
			String max = System.getProperty(STUDIO_BUNDLE_LOAD_CONCURRENCY);
			if (!StringUtil.isEmpty(max))
			{
				try
				{
					fBundlesToLoadInParallel = Integer.parseInt(max);
				}
				catch (NumberFormatException e)
				{
					// ignore
				}
			}
			fBundlesToLoadInParallel = Math.max(1, fBundlesToLoadInParallel);
		}
		return fBundlesToLoadInParallel;
	}

	/**
	 * Load all application, user, and project bundles
	 */
	public void loadBundles()
	{
		// clear out any existing bundles since we're rebuilding from scratch
		this.reset();

		this.loadApplicationBundles();
		this.loadUserBundles();
		this.loadProjectBundles();
	}

	/**
	 * Load all project bundles
	 */
	protected void loadProjectBundles()
	{
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			IPath location = project.getLocation();

			if (location != null)
			{
				File projectDirectory = location.toFile();
				File bundlesDirectory = new File(projectDirectory.getAbsolutePath(), BUILTIN_BUNDLES);

				for (File bundle : this.getBundleDirectories(bundlesDirectory))
				{
					String message = MessageFormat.format(
							Messages.BundleManager_ProjectBundlesInBundlesDirectoryIsDeprecated,
							bundle.getAbsolutePath());
					ScriptLogger.logWarning(message);
					IdeLog.logWarning(ScriptingActivator.getDefault(), message);

					this.loadBundle(bundle);
				}

				// Now load from project directly
				if (!isUserBundleDirectory(projectDirectory) && isValidBundleDirectory(projectDirectory))
				{
					loadBundle(projectDirectory);
				}
			}
			// Log that it was null somehow to track down when this occurs?
		}
	}

	/**
	 * Process the specified script. This is a convenience method for loadScript(File, boolean) and is equivalent to
	 * invoking loadScript(script, true).
	 * 
	 * @param script
	 *            The script to load
	 */
	public void loadScript(File script)
	{
		this.loadScript(script, true);
	}

	/**
	 * Process the specified script. This is a convenience method for loadScript(File, boolean, List<String>) and is
	 * equivalent to invoking loadScript(script, fireEvent, bundleLoadPaths) where bundleLoadPaths is calculated
	 * getBundleDirectory and getBundleLoadPaths
	 * 
	 * @param script
	 *            The script to load
	 * @param fireEvent
	 *            A flag indicating if load cycle events should be fired
	 */
	public void loadScript(File script, boolean fireEvent)
	{
		if (script != null)
		{
			// determine bundle root directory
			File bundleDirectory = this.getBundleDirectory(script);

			// get bundle load paths
			List<String> bundleLoadPaths = this.getBundleLoadPaths(bundleDirectory);

			// execute script
			this.loadScript(script, fireEvent, bundleLoadPaths);
		}
	}

	/**
	 * Process the specified script, possibly firing a script-load load cycle event
	 * 
	 * @param script
	 *            The script to load
	 * @param fireEvent
	 *            A flag indicating if load cycle events should be fired
	 * @param loadPaths
	 *            A list of paths for ruby to use to locate libraries when executing the script
	 */
	public void loadScript(File script, boolean fireEvent, List<String> loadPaths)
	{
		if (script == null)
		{
			ScriptLogger.logError(Messages.BundleManager_Executed_Null_Script);
			return;
		}

		if (!script.canRead())
		{
			ScriptLogger
					.logError(MessageFormat.format(Messages.BundleManager_UNREADABLE_SCRIPT, script.getAbsolutePath()));
			return;
		}

		this.showBundleLoadInfo(MessageFormat.format("Loading script: {0}, fire event={1}", script, fireEvent)); //$NON-NLS-1$
		getScriptingEngine().runScript(script.getAbsolutePath(), loadPaths);
		this.showBundleLoadInfo(MessageFormat.format("Loading complete: {0}", script)); //$NON-NLS-1$

		if (fireEvent)
		{
			this.fireScriptLoadedEvent(script);
		}
	}

	/**
	 * Load all user bundles
	 */
	protected void loadUserBundles()
	{
		String userBundles = this.getUserBundlesPath();

		if (userBundles != null)
		{
			File userBundlesDirectory = new File(userBundles);

			for (File bundle : this.getBundleDirectories(userBundlesDirectory))
			{
				this.loadBundle(bundle);
			}
		}
	}

	/**
	 * Reload the specified bundle. This is equivalent to calling unloadBundle(bundleDirectory) followed by
	 * loadBundle(bundleDirectory)
	 * 
	 * @param bundle
	 *            The bundle element to reload. Null values are ignored
	 */
	public void reloadBundle(BundleElement bundle)
	{
		if (bundle != null)
		{
			File bundleDirectory = bundle.getBundleDirectory();

			this.unloadBundle(bundleDirectory);
			this.loadBundle(bundleDirectory);
		}
	}

	/**
	 * Reload the specified script. A script-reloaded load cycle event will be fired.
	 * 
	 * @param script
	 *            The script to re-process. Null values are ignored
	 */
	public void reloadScript(File script)
	{
		if (script == null)
		{
			ScriptLogger.logError(Messages.BundleManager_Reloaded_Null_Script);
			return;
		}

		this.unloadScript(script, false);

		// determine bundle root directory
		File bundleDirectory = this.getBundleDirectory(script);

		// get bundle load paths
		List<String> loadPaths = this.getBundleLoadPaths(bundleDirectory);

		// execute script. Load order is important, so we force synchronous execution here
		getScriptingEngine().runScript(script.getAbsolutePath(), loadPaths, RunType.THREAD, false);

		// fire reload event
		this.fireScriptReloadedEvent(script);
	}

	protected ScriptingEngine getScriptingEngine()
	{
		return ScriptingEngine.getInstance();
	}

	/**
	 * Remove the specified bundle from the scripting environment
	 * 
	 * @param bundle
	 *            The bundle to remove. Null values are ignored
	 */
	private void removeBundle(BundleElement bundle)
	{
		if (bundle != null)
		{
			File bundleFile = bundle.getBundleDirectory();
			String name = bundle.getDisplayName();

			synchronized (this._bundlesByPath)
			{
				if (this._bundlesByPath.containsKey(bundleFile))
				{
					List<BundleElement> bundles = this._bundlesByPath.get(bundleFile);

					bundles.remove(bundle);

					if (bundles.size() == 0)
					{
						this._bundlesByPath.remove(bundleFile);
					}
				}
			}

			synchronized (this._entriesByName)
			{
				if (this._entriesByName.containsKey(name))
				{
					BundleEntry entry = this._entriesByName.get(name);

					entry.removeBundle(bundle);

					if (entry.size() == 0)
					{
						this._entriesByName.remove(name);
					}
				}
			}

			AbstractElement.unregisterElement(bundle);
		}
	}

	/**
	 * Remove a listener from the bundle visibility event list
	 * 
	 * @param listener
	 *            The listener to remove. Null values are ignored
	 */
	public void removeBundleVisibilityListener(BundleVisibilityListener listener)
	{
		synchronized (this._bundleVisibilityListeners)
		{
			this._bundleVisibilityListeners.remove(listener);
		}
	}

	/**
	 * Remove a listener from the element visibility event list
	 * 
	 * @param listener
	 *            The listener to remove. Null values are ignored
	 */
	public void removeElementVisibilityListener(ElementVisibilityListener listener)
	{
		synchronized (this._elementVisibilityListeners)
		{
			this._elementVisibilityListeners.remove(listener);
		}
	}

	/**
	 * Remove a listener from the script load cycle event list
	 * 
	 * @param listener
	 *            The listener to remove. Null values are ignored
	 */
	public void removeLoadCycleListener(LoadCycleListener listener)
	{
		synchronized (this._loadCycleListeners)
		{
			this._loadCycleListeners.remove(listener);
		}
	}

	/**
	 * Clear internal state in the bundle manager. This used for unit testing, so don't use this unless you know what
	 * you're doing
	 */
	public void reset()
	{
		// TODO: should unload all commands, menus, and snippets so events fire, but
		// this is used for test only right now.
		synchronized (this._bundlesByPath)
		{
			this._bundlesByPath.clear();
		}

		synchronized (this._entriesByName)
		{
			this._entriesByName.clear();
		}
	}

	/**
	 * Turn on or off bundle caching
	 * 
	 * @param value
	 */
	public void setUseCache(boolean value)
	{
		System.setProperty(USE_BUNDLE_CACHE, Boolean.toString(value));
	}

	/**
	 * Show bundle load info
	 * 
	 * @param message
	 */
	protected void showBundleLoadInfo(String message)
	{
		IdeLog.logInfo(ScriptingActivator.getDefault(), message, IDebugScopes.SHOW_BUNDLE_LOAD_INFO);
	}

	/**
	 * Unload all scripts that have been processed in the specified bundle directory. This effectively unloads all
	 * scripts associated with a bundle and the bundle.rb script as well
	 * 
	 * @param bundleDirectory
	 *            The directory (and its descendants) to unload
	 */
	public void unloadBundle(File bundleDirectory)
	{
		List<AbstractElement> elements = AbstractElement.getElementsByDirectory(bundleDirectory.getAbsolutePath());
		Set<File> scripts = new HashSet<File>();

		if (elements != null)
		{
			for (AbstractElement element : elements)
			{
				scripts.add(new File(element.getPath()));
			}
		}

		List<File> reverseOrder = new ArrayList<File>(scripts);

		Collections.sort(reverseOrder, new Comparator<File>()
		{
			public int compare(File o1, File o2)
			{
				// reverse sort
				return o2.getAbsolutePath().compareToIgnoreCase(o1.getAbsolutePath());
			}
		});

		for (File script : reverseOrder)
		{
			showBundleLoadInfo("Unload script: " + script.toString()); //$NON-NLS-1$

			this.unloadScript(script);
		}
	}

	/**
	 * Unload the specified script. This is a convenience method for unloadScript(File, boolean) and is equivalent to
	 * invoking unloadScript(script, true).
	 * 
	 * @param script
	 *            The script to unload
	 */
	public void unloadScript(File script)
	{
		this.unloadScript(script, true);
	}

	/**
	 * Unload the specified script.
	 * 
	 * @param script
	 *            The script to unload. Null values are ignored and will not fire load cycle events.
	 * @param fireEvent
	 *            A flag indicating if load cycle events should be fired
	 */
	public void unloadScript(File script, boolean fireEvent)
	{
		if (script == null)
		{
			ScriptLogger.logError(Messages.BundleManager_Unloaded_Null_Script);
			return;
		}

		String scriptPath = script.getAbsolutePath();
		List<AbstractElement> elements = AbstractElement.getElementsByPath(scriptPath);

		// remove bundle members in pass 1
		for (AbstractElement element : elements)
		{
			if (element instanceof AbstractBundleElement)
			{
				AbstractBundleElement bundleElement = (AbstractBundleElement) element;
				BundleElement bundle = bundleElement.getOwningBundle();

				if (bundle != null)
				{
					bundle.removeChild(bundleElement);
				}
				else
				{
					// TODO: this should not be happening
				}
			}
		}

		// clear (and possibly remove) bundles in pass 2
		for (AbstractElement element : elements)
		{
			if (element instanceof BundleElement)
			{
				BundleElement bundle = (BundleElement) element;

				bundle.clearMetadata();

				if (bundle.isEmpty())
				{
					this.removeBundle(bundle);
				}
			}
		}

		if (fireEvent)
		{
			this.fireScriptUnloadedEvent(script);
		}
	}

	/**
	 * Determine if bundle caching is turned on or off
	 * 
	 * @return
	 */
	public boolean useCache()
	{
		return Boolean.valueOf(System.getProperty(USE_BUNDLE_CACHE, Boolean.TRUE.toString()));
	}

	/**
	 * This pools duplicate strings so that we only store one instance of that particular value and shared the
	 * reference. useful for commonly repeated strings, such as paths where elements are defined, common scope
	 * selectors, key used to hold snippet/command triggers.
	 * 
	 * @param value
	 * @return
	 */
	String sharedString(String value)
	{
		if (value == null)
		{
			return null;
		}
		synchronized (_stringPool)
		{
			String result = _stringPool.get(value);
			if (result != null)
			{
				return result;
			}
			_stringPool.put(value, value);
		}
		return value;
	}
}
