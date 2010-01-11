package com.aptana.scripting.model;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.scripting.Activator;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptingEngine;
import com.aptana.util.ResourceUtils;

public class BundleManager
{
	static final String SNIPPETS_DIRECTORY_NAME = "snippets"; //$NON-NLS-1$
	static final String COMMANDS_DIRECTORY_NAME = "commands"; //$NON-NLS-1$
	static final BundleElement[] NO_BUNDLES = new BundleElement[0];
	static final CommandElement[] NO_COMMANDS = new CommandElement[0];
	static final MenuElement[] NO_MENUS = new MenuElement[0];
	static final SnippetElement[] NO_SNIPPETS = new SnippetElement[0];
	
	private static final File[] NO_FILES = new File[0];
	private static final String[] NO_STRINGS = new String[0];
	
	private static final String BUILTIN_BUNDLES = "bundles"; //$NON-NLS-1$
	private static final String BUNDLE_FILE = "bundle.rb"; //$NON-NLS-1$
	private static final String RUBY_FILE_EXTENSION = ".rb"; //$NON-NLS-1$
	private static final String LIB_DIRECTORY_NAME = "lib"; //$NON-NLS-1$
	private static final String USER_HOME_PROPERTY = "user.home"; //$NON-NLS-1$
	private static final String USER_BUNDLE_DIRECTORY_GENERAL = "RadRails Bundles"; //$NON-NLS-1$
	private static final String USER_BUNDLE_DIRECTORY_MACOSX = "/Documents/RadRails Bundles"; //$NON-NLS-1$

	private static BundleManager INSTANCE;
	
	private String applicationBundlesPath;
	private String userBundlesPath;
	private Map<File, List<BundleElement>> _bundlesByPath;
	private Map<String, BundleEntry> _entriesByName;
	
	private List<ElementChangeListener> _elementListeners;
	
	/**
	 * getInstance - used for unit testing
	 * 
	 * @param applicationBundlesPath
	 * @param userBundlesPath
	 * @return
	 */
	public static BundleManager getInstance(String applicationBundlesPath, String userBundlesPath)
	{
		if (INSTANCE == null)
		{
			// create new instance
			INSTANCE = new BundleManager();
			
			// setup default application bundles path
			URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(BUILTIN_BUNDLES), null);
			
			if (url != null)
			{
				INSTANCE.applicationBundlesPath = ResourceUtils.resourcePathToString(url);
			}
			
			String OS = Platform.getOS();
			String userHome = System.getProperty(USER_HOME_PROPERTY);
			
			// setup default user bundles path
			if (OS.equals(Platform.OS_MACOSX) || OS.equals(Platform.OS_LINUX))
			{
				INSTANCE.userBundlesPath = userHome + USER_BUNDLE_DIRECTORY_MACOSX;
			}
			else
			{
				INSTANCE.userBundlesPath = userHome + File.separator + USER_BUNDLE_DIRECTORY_GENERAL;
			}
		}
			
		// setup application bundles path
		if (applicationBundlesPath != null && applicationBundlesPath.length() > 0)
		{
			INSTANCE.applicationBundlesPath = applicationBundlesPath;
		}

		// setup user bundles path
		if (userBundlesPath != null && userBundlesPath.length() > 0)
		{
			INSTANCE.userBundlesPath = userBundlesPath;
		}
		
		return INSTANCE;
	}
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static BundleManager getInstance()
	{
		return getInstance(null, null);
	}

	/**
	 * BundleManager
	 */
	private BundleManager()
	{
	}
	
	/**
	 * addBundle
	 * 
	 * @param bundle
	 */
	public void addBundle(BundleElement bundle)
	{
		if (bundle != null)
		{
			File bundleFile = bundle.getBundleDirectory();
			
			// store bundle by path
			if (this._bundlesByPath == null)
			{
				this._bundlesByPath = new HashMap<File, List<BundleElement>>();
			}
			
			if (this._bundlesByPath.containsKey(bundleFile) == false)
			{
				List<BundleElement> bundles = new ArrayList<BundleElement>();
				
				bundles.add(bundle);
				
				this._bundlesByPath.put(bundleFile, bundles);
			}
			else
			{
				List<BundleElement> bundles = this._bundlesByPath.get(bundleFile);
				
				bundles.add(bundle);
			}
			
			// store bundle by name
			String name = bundle.getDisplayName();
			
			if (this._entriesByName == null)
			{
				this._entriesByName = new HashMap<String, BundleEntry>();
			}
			
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
	
	/**
	 * addElementChangeListener
	 * 
	 * @param listener
	 */
	public void addElementChangeListener(ElementChangeListener listener)
	{
		if (listener != null)
		{
			if (this._elementListeners == null)
			{
				this._elementListeners = new ArrayList<ElementChangeListener>();
			}
			
			this._elementListeners.add(listener);
		}
	}
	
	/**
	 * fireElementAddedEvent
	 * 
	 * @param element
	 */
	void fireElementAddedEvent(AbstractElement element)
	{
		if (this._elementListeners != null && element != null)
		{
			for (ElementChangeListener listener : this._elementListeners)
			{
				listener.elementAdded(element);
			}
		}
	}
	
	/**
	 * fireElementDeletedEvent
	 * 
	 * @param element
	 */
	void fireElementDeletedEvent(AbstractElement element)
	{
		if (this._elementListeners != null && element != null)
		{
			for (ElementChangeListener listener : this._elementListeners)
			{
				listener.elementDeleted(element);
			}
		}
	}
	
	/**
	 * fireElementModifiedEvent
	 * 
	 * @param element
	 */
	void fireElementModifiedEvent(AbstractElement element)
	{
		if (this._elementListeners != null && element != null)
		{
			boolean sendEvent = true;
			
			if (element instanceof AbstractBundleElement)
			{
				sendEvent = (((AbstractBundleElement) element).getOwningBundle() != null);
			}
			
			if (sendEvent)
			{
				for (ElementChangeListener listener : this._elementListeners)
				{
					listener.elementDeleted(element);
					listener.elementAdded(element);
//					listener.elementModified(element);
				}
			}
		}
	}

	/**
	 * getBuiltinsLoadPath
	 * 
	 * @return
	 */
	public String getApplicationBundlesPath()
	{
		return this.applicationBundlesPath;
	}
	
	/**
	 * getBundleCommands
	 * 
	 * @param name
	 * @return
	 */
	public CommandElement[] getBundleCommands(String name)
	{
		CommandElement[] result = NO_COMMANDS;
		
		if (this._entriesByName != null && this._entriesByName.containsKey(name))
		{
			// grab all bundles of the given name
			BundleEntry entry = this._entriesByName.get(name);
			
			result = entry.getCommands();
		}
		
		return result;
	}
	
	/**
	 * getBundles
	 * 
	 * @param bundlesDirectory
	 * @return
	 */
	protected File[] getBundleDirectories(File bundlesDirectory)
	{
		File[] result = NO_FILES;
		
		if (bundlesDirectory != null && bundlesDirectory.isDirectory() && bundlesDirectory.canRead())
		{
			result = bundlesDirectory.listFiles(new FileFilter()
			{
				public boolean accept(File pathname)
				{
					return (pathname.isDirectory() && pathname.getName().startsWith(".") == false); //$NON-NLS-1$
				}
			});
		}
		
		return result;
	}
	
	/**
	 * getBundle
	 * 
	 * @param name
	 * @return
	 */
	public BundleEntry getBundleEntry(String name)
	{
		BundleEntry result = null;
		
		if (this._entriesByName != null)
		{
			result = this._entriesByName.get(name);
		}
		
		return result;
	}
	
	/**
	 * getBundleFromPath
	 * 
	 * @param path
	 * @return
	 */
	public BundleElement getBundleFromPath(String path)
	{
		BundleElement result = null;
		
		if (path != null)
		{
			result = this.getBundleFromPath(new File(path));
		}
		
		return result;
	}
	
	/**
	 * getBundleFromPath
	 * 
	 * @param path
	 * @return
	 */
	public BundleElement getBundleFromPath(File bundleDirectory)
	{
		BundleElement result = null;
		
		if (this._bundlesByPath != null)
		{
			List<BundleElement> bundles = this._bundlesByPath.get(bundleDirectory);
			
			if (bundles != null)
			{
				int size = bundles.size();
				
				if (size > 0)
				{
					result = bundles.get(size - 1);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * getBundleLoadPaths
	 * 
	 * @param bundleDirectory
	 * @return
	 */
	protected List<String> getBundleLoadPaths(File bundleDirectory)
	{
		List<String> result = new ArrayList<String>();
		List<String> paths = ScriptingEngine.getInstance().getContributedLoadPaths();
		
		result.addAll(paths);
		result.add(bundleDirectory.getAbsolutePath() + File.separator + LIB_DIRECTORY_NAME);
		
		return result;
	}
	
	/**
	 * getBundleCommands
	 * 
	 * @param name
	 * @return
	 */
	public MenuElement[] getBundleMenus(String name)
	{
		MenuElement[] result = NO_MENUS;
		
		if (this._entriesByName != null && this._entriesByName.containsKey(name))
		{
			// grab all bundles of the given name
			BundleEntry entry = this._entriesByName.get(name);
			
			result = entry.getMenus();
		}
		
		return result;
	}
	
	/**
	 * getBundleNames
	 * 
	 * @return
	 */
	public String[] getBundleNames()
	{
		String[] result = NO_STRINGS;
		
		if (this._entriesByName != null && this._entriesByName.size() > 0)
		{
			result = this._entriesByName.keySet().toArray(new String[this._entriesByName.size()]);
			
			Arrays.sort(result);
		}
		
		return result;
	}
	
	/**
	 * getBundleScopeFromPath
	 * 
	 * @param path
	 * @return
	 */
	public BundleScope getBundleScopeFromPath(File path)
	{
		return this.getBundleScopeFromPath(path.getAbsolutePath());
	}
	
	/**
	 * getBundleScopeFromPath
	 * 
	 * @param path
	 * @return
	 */
	public BundleScope getBundleScopeFromPath(String path)
	{
		BundleScope result = BundleScope.PROJECT;
		
		if (path != null)
		{
			if (path.startsWith(this.applicationBundlesPath))
			{
				result = BundleScope.APPLICATION;
			}
			else if (path.startsWith(this.userBundlesPath))
			{
				result = BundleScope.USER;
			}
		}
			
		return result;
	}
	
	/**
	 * getBundleScripts
	 * 
	 * @return
	 */
	protected File[] getBundleScripts(File bundleDirectory)
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

			result.addAll(Arrays.asList(this.getScriptsFromDirectory(commandsDirectory)));
			
			// check for scripts inside "snippets" directory
			File snippetsDirectory = new File(bundleDirectory, SNIPPETS_DIRECTORY_NAME);
			
			result.addAll(Arrays.asList(this.getScriptsFromDirectory(snippetsDirectory)));
		}

		return result.toArray(new File[result.size()]);
	}
	
	/**
	 * getCommands
	 * 
	 * @return
	 */
	public CommandElement[] getCommands()
	{
		List<CommandElement> result = new ArrayList<CommandElement>();
		
		for (String name : this.getBundleNames())
		{
			result.addAll(Arrays.asList(this.getBundleCommands(name)));
		}
		
		return result.toArray(new CommandElement[result.size()]);
	}
	
	/**
	 * getCommands
	 * 
	 * @param filter
	 * @return
	 */
	public CommandElement[] getCommands(IModelFilter filter)
	{
		List<CommandElement> result = new ArrayList<CommandElement>();
		
		if (filter != null)
		{
			for (String name : this.getBundleNames())
			{
				for (CommandElement command : this.getBundleCommands(name))
				{
					if (filter.include(command))
					{
						result.add(command);
					}
				}
			}
		}
		
		return result.toArray(new CommandElement[result.size()]);
	}
	
	/**
	 * getMenus
	 * 
	 * @param filter
	 * @return
	 */
	public MenuElement[] getMenus(IModelFilter filter)
	{
		List<MenuElement> result = new ArrayList<MenuElement>();
		
		if (filter != null)
		{
			for (String name : this.getBundleNames())
			{
				for (MenuElement menu : this.getBundleMenus(name))
				{
					if (filter.include(menu))
					{
						result.add(menu);
					}
				}
			}
		}
		
		return result.toArray(new MenuElement[result.size()]);
	}
	
	/**
	 * getScriptsFromDirectory
	 * 
	 * @param directory
	 * @return
	 */
	protected File[] getScriptsFromDirectory(File directory)
	{
		File[] result = NO_FILES;

		if (directory.exists() && directory.canRead())
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
		
		return result;
	}

	/**
	 * getUserBundlePath
	 * 
	 * @return
	 */
	public String getUserBundlesPath()
	{
		return this.userBundlesPath;
	}
	
	/**
	 * isValidBundleDirectory
	 * 
	 * @param bundleDirectory
	 * @return
	 */
	protected boolean isValidBundleDirectory(File bundleDirectory)
	{
		return this.isValidBundleDirectory(bundleDirectory, true);
	}
	
	/**
	 * isValidBundleDirectory
	 * 
	 * @param bundleDirectory
	 * @param logErrors
	 * @return
	 */
	protected boolean isValidBundleDirectory(File bundleDirectory, boolean logErrors)
	{
		String message = null;
		boolean result = false;

		if (bundleDirectory.exists())
		{
			if (bundleDirectory.isDirectory())
			{
				if (bundleDirectory.canRead())
				{
					File bundleFile = new File(bundleDirectory.getAbsolutePath(), BUNDLE_FILE);
					
					// NOTE: We verify readability when we try to execute the scripts in the bundle
					// so, there's no need to do that here.
					if (bundleFile.exists() && bundleFile.isFile())
					{
						result = true;
					}
					else
					{
						message = MessageFormat.format(
							Messages.BundleManager_No_Bundle_File,
							new Object[] { bundleDirectory.getAbsolutePath(), BUNDLE_FILE }
						);
					}
				}
				else
				{
					message = MessageFormat.format(
						Messages.BundleManager_BUNDLE_FILE_NOT_A_DIRECTORY,
						new Object[] { bundleDirectory.getAbsolutePath() }
					);
				}
			}
			else
			{
				message = MessageFormat.format(
					Messages.BundleManager_BUNDLE_FILE_NOT_A_DIRECTORY,
					new Object[] { bundleDirectory.getAbsolutePath() }
				);
			}
		}
		else
		{
			message = MessageFormat.format(
				Messages.BundleManager_BUNDLE_DIRECTORY_DOES_NOT_EXIST,
				new Object[] { bundleDirectory.getAbsolutePath() }
			);
		}

		if (result == false && logErrors && message != null && message.length() > 0)
		{
			ScriptLogger.logError(message);
		}

		return result;
	}
	
	/**
	 * loadApplicationBundles
	 */
	public void loadApplicationBundles()
	{
		String applicationBundles = this.getApplicationBundlesPath();
		
		if (applicationBundles != null)
		{
			File applicationBundlesDirectory = new File(applicationBundles);
			
			for (File bundle : this.getBundleDirectories(applicationBundlesDirectory))
			{
				this.loadBundle(bundle);
			}
		}
	}
	
	/**
	 * loadBundle
	 * 
	 * @param bundleDirectory
	 */
	public void loadBundle(File bundleDirectory)
	{
		File[] bundleScripts = this.getBundleScripts(bundleDirectory);

		if (bundleScripts.length > 0)
		{
			List<String> bundleLoadPaths = this.getBundleLoadPaths(bundleDirectory);
			
			for (File script : bundleScripts)
			{
				this.loadScript(script, bundleLoadPaths);
			}
		}
	}

	/**
	 * loadBundles
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
	 * loadProjectBundles
	 */
	public void loadProjectBundles()
	{
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			File projectDirectory = project.getLocation().toFile();
			File bundlesDirectory = new File(projectDirectory.getAbsolutePath() + File.separator + BUILTIN_BUNDLES);
			
			for (File bundle : this.getBundleDirectories(bundlesDirectory))
			{
				this.loadBundle(bundle);
			}
		}
	}
	
	/**
	 * loadScript
	 * 
	 * @param script
	 */
	public void loadScript(File script)
	{
		if (script != null)
		{
			// determine bundle root directory
			String scriptPath = script.getAbsolutePath();
			File bundleDirectory = null;
			
			if (scriptPath.endsWith(BUNDLE_FILE))
			{
				bundleDirectory = script.getParentFile();
			}
			else
			{
				bundleDirectory = script.getParentFile().getParentFile();
			}
	
			// get bundle load paths
			List<String> bundleLoadPaths = this.getBundleLoadPaths(bundleDirectory);
			
			// execute script
			this.loadScript(script, bundleLoadPaths);
		}
	}
	
	/**
	 * loadScript
	 * 
	 * @param script
	 */
	public void loadScript(File script, List<String> loadPaths)
	{
		boolean execute = true;
		
		if (script == null)
		{
			String message = MessageFormat.format(
				Messages.BundleManager_Executed_Null_Script,
				new Object[] { }
			);
			
			ScriptLogger.logError(message);
			execute = false;
		}
		
		if (execute && script.canRead() == false)
		{
			String message = MessageFormat.format(
				Messages.BundleManager_UNREADABLE_SCRIPT,
				new Object[] { script.getAbsolutePath() }
			);
			
			ScriptLogger.logError(message);
			execute = false;
		}
		
		if (execute)
		{
			ScriptingEngine.getInstance().runScript(script.getAbsolutePath(), loadPaths);
		}
	}

	/**
	 * loadUserBundles
	 */
	public void loadUserBundles()
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
	 * reloadScript
	 * 
	 * @param script
	 */
	public void reloadScript(File script)
	{
		if (script != null)
		{
			this.unloadScript(script);
			this.loadScript(script);
		}
		else
		{
			String message = MessageFormat.format(
				Messages.BundleManager_Reloaded_Null_Script,
				new Object[] {}
			);
			
			ScriptLogger.logError(message);
		}
	}
	
	/**
	 * removeElementChangeListener
	 * 
	 * @param listener
	 */
	public void removeElementChangeListener(ElementChangeListener listener)
	{
		if (this._elementListeners != null)
		{
			this._elementListeners.remove(listener);
		}
	}
	
	/**
	 * reset
	 */
	public void reset()
	{
		// TODO: should unload all commands, menus, and snippets so events fire, but
		// this is used for test only right now.
		if (this._bundlesByPath != null)
		{
			this._bundlesByPath.clear();
		}
		
		if (this._entriesByName != null)
		{
			this._entriesByName.clear();
		}
	}
	
	/**
	 * unloadScript
	 * 
	 * @param script
	 */
	public void unloadScript(File script)
	{
		if (script != null)
		{
			String scriptPath = script.getAbsolutePath();
			AbstractElement[] elements = AbstractElement.getRegisteredElements(scriptPath);
			
			// remove bundle members in pass 1
			for (AbstractElement element : elements)
			{
				if (element instanceof AbstractBundleElement)
				{
					AbstractBundleElement bundleElement = (AbstractBundleElement) element;
					BundleElement bundle = bundleElement.getOwningBundle();
					
					if (bundle != null)
					{
						bundle.removeElement(bundleElement);
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
						AbstractElement.unregisterElement(bundle);
					}
				}
			}
		}
		else
		{
			String message = MessageFormat.format(
				Messages.BundleManager_Unloaded_Null_Script,
				new Object[] {}
			);
			
			ScriptLogger.logError(message);
		}
	}
}
