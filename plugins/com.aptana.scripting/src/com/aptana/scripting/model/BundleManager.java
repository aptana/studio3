package com.aptana.scripting.model;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.scripting.Activator;
import com.aptana.scripting.ResourceUtils;
import com.aptana.scripting.ScriptingEngine;

public class BundleManager
{
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
	private static final String SNIPPETS_DIRECTORY_NAME = "snippets"; //$NON-NLS-1$
	private static final String COMMANDS_DIRECTORY_NAME = "commands"; //$NON-NLS-1$
	private static final String USER_HOME_PROPERTY = "user.home"; //$NON-NLS-1$
	private static final String USER_BUNDLE_DIRECTORY_GENERAL = "RadRails Bundles"; //$NON-NLS-1$
	private static final String USER_BUNDLE_DIRECTORY_MACOSX = "/Documents/RadRails Bundles"; //$NON-NLS-1$

	private static BundleManager INSTANCE;
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static BundleManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new BundleManager();
		}

		return INSTANCE;
	}
	private String applicationBundlesPath;
	private String userBundlesPath;
	private Map<File, List<BundleElement>> _bundlesByPath;
	private Map<String, BundleEntry> _entriesByName;

	private List<ElementChangeListener> _elementListeners;

	/**
	 * BundleManager
	 */
	private BundleManager()
	{
		// initialize app's bundle path
		this.applicationBundlesPath = this.getApplicationBundlesPath();
		
		// initialize user's bundle path
		String OS = Platform.getOS();
		String userHome = System.getProperty(USER_HOME_PROPERTY);
		
		if (OS.equals(Platform.OS_MACOSX) || OS.equals(Platform.OS_LINUX))
		{
			this.userBundlesPath = userHome + USER_BUNDLE_DIRECTORY_MACOSX;
		}
		else
		{
			this.userBundlesPath = userHome + File.separator + USER_BUNDLE_DIRECTORY_GENERAL;
		}
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
			String path = bundle.getPath();
			File bundleFile = new File(path);
			
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
		if (this._elementListeners == null)
		{
			this._elementListeners = new ArrayList<ElementChangeListener>();
		}
		
		this._elementListeners.add(listener);
	}
	
	/**
	 * fireElementAddedEvent
	 * 
	 * @param element
	 */
	void fireElementAddedEvent(AbstractElement element)
	{
		if (this._elementListeners != null)
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
		if (this._elementListeners != null)
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
		if (this._elementListeners != null)
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
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(BUILTIN_BUNDLES), null);
		
		return ResourceUtils.resourcePathToString(url);
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
	 * getBundleCommands
	 * 
	 * @param name
	 * @return
	 */
	public CommandElement[] getBundleCommands(String name)
	{
		final Set<String> names = new HashSet<String>();
		final List<CommandElement> result = new ArrayList<CommandElement>();
		
		if (this._entriesByName != null && this._entriesByName.containsKey(name))
		{
			// grab all bundles of the given name
			BundleEntry entry = this._entriesByName.get(name);
			
			entry.processMembers(new BundleProcessor()
			{
				public void processBundle(BundleElement bundle)
				{
					for (CommandElement command : bundle.getCommands())
					{
						String name = command.getDisplayName();
						
						if (names.contains(name) == false)
						{
							names.add(name);
							result.add(command);
						}
					}
				}
				
			});
		}
		
		return result.toArray(new CommandElement[result.size()]);
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
		
		result.add(ScriptingEngine.getBuiltinsLoadPath());
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
		final Set<String> names = new HashSet<String>();
		final List<MenuElement> result = new ArrayList<MenuElement>();
		
		if (this._entriesByName != null && this._entriesByName.containsKey(name))
		{
			// grab all bundles of the given name
			BundleEntry entry = this._entriesByName.get(name);
			
			entry.processMembers(new BundleProcessor()
			{
				public void processBundle(BundleElement bundle)
				{
					for (MenuElement menu : bundle.getMenus())
					{
						String name = menu.getDisplayName();
						
						if (names.contains(name) == false)
						{
							names.add(name);
							result.add(menu);
						}
					}
				}
			});
		}
		
		return result.toArray(new MenuElement[result.size()]);
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
					return (pathname.isDirectory() && pathname.getName().startsWith(".") == false);
				}
			});
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
			String bundlePath = bundleDirectory.getAbsolutePath();

			// check for a top-level bundle.rb file
			File bundleFile = new File(bundlePath + File.separator + BUNDLE_FILE);

			if (bundleFile.exists())
			{
				result.add(bundleFile);
			}

			// check for scripts inside "commands" directory
			File commandsDirectory = new File(bundlePath + File.separator + COMMANDS_DIRECTORY_NAME);

			result.addAll(Arrays.asList(this.getScriptsFromDirectory(commandsDirectory)));
			
			// check for scripts inside "snippets" directory
			File snippetsDirectory = new File(bundlePath + File.separator + SNIPPETS_DIRECTORY_NAME);
			
			result.addAll(Arrays.asList(this.getScriptsFromDirectory(snippetsDirectory)));
		}

		return result.toArray(new File[result.size()]);
	}
	
	/**
	 * getBundleCommands
	 * 
	 * @param name
	 * @return
	 */
	public SnippetElement[] getBundleSnippets(String name)
	{
		final Set<String> names = new HashSet<String>();
		final List<SnippetElement> result = new ArrayList<SnippetElement>();
		
		if (this._entriesByName != null && this._entriesByName.containsKey(name))
		{
			// grab all bundles of the given name
			BundleEntry entry = this._entriesByName.get(name);
			
			entry.processMembers(new BundleProcessor()
			{
				public void processBundle(BundleElement bundle)
				{
					for (SnippetElement snippet : bundle.getSnippets())
					{
						String name = snippet.getDisplayName();
						
						if (names.contains(name) == false)
						{
							names.add(name);
							result.add(snippet);
						}
					}
				}
				
			});
		}
		
		return result.toArray(new SnippetElement[result.size()]);
	}
	
	/**
	 * getCommands
	 * 
	 * @return
	 */
	public CommandElement[] getCommands()
	{
		CommandElement[] result = NO_COMMANDS;
		
		String[] bundleNames = this.getBundleNames();
		
		if (bundleNames != null && bundleNames.length > 0)
		{
			List<CommandElement> commands = new ArrayList<CommandElement>();
			
			for (String name : bundleNames)
			{
				commands.addAll(Arrays.asList(this.getBundleCommands(name)));
			}
			
			result = commands.toArray(new CommandElement[commands.size()]);
		}
		
		return result;
	}
	
	/**
	 * getCommandsFromScope
	 * 
	 * @param scope
	 * @return
	 */
	public CommandElement[] getCommandsFromScope(String scope)
	{
		return this.getCommandsFromScopes(new String[] { scope }, null);
	}
	
	/**
	 * getCommandsFromScope
	 * 
	 * @param scope
	 * @param filter
	 * @return
	 */
	public CommandElement[] getCommandsFromScope(String scope, IModelFilter filter)
	{
		return this.getCommandsFromScopes(new String[] { scope }, filter);
	}
	
	/**
	 * getCommandsFromScopes
	 * 
	 * @param scopes
	 * @return
	 */
	public CommandElement[] getCommandsFromScopes(String[] scopes)
	{
		return this.getCommandsFromScopes(scopes, null);
	}
	
	/**
	 * getCommandsFromScopes
	 * 
	 * @param scopes
	 * @param filter
	 * @return
	 */
	public CommandElement[] getCommandsFromScopes(String[] scopes, IModelFilter filter)
	{
		List<CommandElement> result = new ArrayList<CommandElement>();
		
		if (scopes != null && scopes.length > 0)
		{
			for (String name : this.getBundleNames())
			{
				for (CommandElement command : this.getBundleCommands(name))
				{
					if (command.matches(scopes) && ((filter != null) ? filter.include(command) : true))
					{
						result.add(command);
					}
				}
			}
		}
		
		return result.toArray(new CommandElement[result.size()]);
	}
	
	/**
	 * getMenusFromScope
	 * 
	 * @param scope
	 * @return
	 */
	public MenuElement[] getMenusFromScope(String scope)
	{
		return this.getMenusFromScopes(new String[] { scope }, null);
	}
	
	/**
	 * getMenusFromScope
	 * 
	 * @param scope
	 * @param filter
	 * @return
	 */
	public MenuElement[] getMenusFromScope(String scope, IModelFilter filter)
	{
		return this.getMenusFromScopes(new String[] { scope }, filter);
	}
	
	/**
	 * getMenusFromScope
	 * 
	 * @param scopes
	 * @return
	 */
	public MenuElement[] getMenusFromScope(String[] scopes)
	{
		return this.getMenusFromScopes(scopes, null);
	}

	/**
	 * getMenusFromScopes
	 * 
	 * @param scopes
	 * @param filter
	 * @return
	 */
	public MenuElement[] getMenusFromScopes(String[] scopes, IModelFilter filter)
	{
		List<MenuElement> result = new ArrayList<MenuElement>();
		
		if (scopes != null && scopes.length > 0)
		{
			for (String name : this.getBundleNames())
			{
				for (MenuElement menu : this.getBundleMenus(name))
				{
					if (menu.matches(scopes) && ((filter != null) ? filter.include(menu) : true))
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
		}

		Arrays.sort(result, new Comparator<File>()
		{
			public int compare(File o1, File o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		return result;
	}

	/**
	 * getSnippetsFromScope
	 * 
	 * @param scope
	 * @return
	 */
	public SnippetElement[] getSnippetsFromScope(String scope)
	{
		return this.getSnippetsFromScopes(new String[] { scope }, null);
	}

	/**
	 * getSnippetsFromScope
	 * 
	 * @param scope
	 * @param filter
	 * @return
	 */
	public SnippetElement[] getSnippetsFromScope(String scope, IModelFilter filter)
	{
		return this.getSnippetsFromScopes(new String[] { scope }, filter);
	}

	/**
	 * getSnippetsFromScopes
	 * 
	 * @param scopes
	 * @return
	 */
	public SnippetElement[] getSnippetsFromScopes(String[] scopes)
	{
		return this.getSnippetsFromScopes(scopes, null);
	}

	/**
	 * getSnippetsFromScopes
	 * 
	 * @param scopes
	 * @param filter
	 * @return
	 */
	public SnippetElement[] getSnippetsFromScopes(String[] scopes, IModelFilter filter)
	{
		List<SnippetElement> result = new ArrayList<SnippetElement>();
		
		if (scopes != null && scopes.length > 0)
		{
			for (String name : this.getBundleNames())
			{
				for (SnippetElement snippet : this.getBundleSnippets(name))
				{
					if (snippet.matches(scopes) && ((filter != null) ? filter.include(snippet) : true))
					{
						result.add(snippet);
					}
				}
			}
		}
		
		return result.toArray(new SnippetElement[result.size()]);
	}
	
	/**
	 * getUserBundlePath
	 * 
	 * @return
	 */
	public String getUserBundlesPath()
	{
		String OS = Platform.getOS();
		String userHome = System.getProperty(USER_HOME_PROPERTY);
		String result = null;
		
		// TODO: define user bundle paths for other platforms
		if (OS.equals(Platform.OS_MACOSX))
		{
			result = userHome + USER_BUNDLE_DIRECTORY_MACOSX;
		}
		else
		{
			result = userHome + File.separator + USER_BUNDLE_DIRECTORY_GENERAL;
		}
		
		return result;
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
					result = true;
				}
				else
				{
					message = MessageFormat.format("The specified bundle file is not a directory: {0}",
							new Object[] { bundleDirectory.getAbsolutePath() });
				}
			}
			else
			{
				message = MessageFormat.format("The specified bundle file is not a directory: {0}",
						new Object[] { bundleDirectory.getAbsolutePath() });
			}
		}
		else
		{
			message = MessageFormat.format("The specified bundle directory does not exist: {0}",
					new Object[] { bundleDirectory.getAbsolutePath() });
		}

		if (result == false && logErrors && message != null && message.length() > 0)
		{
			this.logError(message);
		}

		return result;
	}
	
	/**
	 * loadApplicationBundles
	 */
	public void loadApplicationBundles()
	{
		File applicationBundlesDirectory = new File(this.getApplicationBundlesPath());
		
		for (File bundle : this.getBundleDirectories(applicationBundlesDirectory))
		{
			this.loadBundle(bundle);
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
				if (script.canRead())
				{
					ScriptingEngine.getInstance().runScript(script.getAbsolutePath(), bundleLoadPaths);
				}
				else
				{
					String message = MessageFormat.format(
						"Skipping script because its current access privileges make it unreadable: {0}",
						new Object[] { script.getAbsolutePath() }
					);
					
					this.logError(message);
				}
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
			
			for (File bundle : this.getBundleDirectories(projectDirectory))
			{
				this.loadBundle(bundle);
			}
		}
	}

	/**
	 * loadUserBundles
	 */
	public void loadUserBundles()
	{
		File userBundlesDirectory = new File(this.getUserBundlesPath());
		
		for (File bundle : this.getBundleDirectories(userBundlesDirectory))
		{
			this.loadBundle(bundle);
		}
	}

	/**
	 * logError
	 * 
	 * @param message
	 */
	void logError(String message)
	{
		// TODO: create and write to bundle console
		System.out.println("error: " + message);
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
	protected void reset()
	{
		// TODO: not implemented
	}
}
