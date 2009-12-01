package com.aptana.scripting.model;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.jruby.anno.JRubyMethod;

import com.aptana.scripting.Activator;
import com.aptana.scripting.ResourceChangeListener;
import com.aptana.scripting.ScriptingEngine;

public class BundleManager
{
	static final Menu[] NO_MENUS = new Menu[0];
	static final Snippet[] NO_SNIPPETS = new Snippet[0];
	static final Command[] NO_COMMANDS = new Command[0];
	
	private static final IResourceChangeListener resourceListener = new ResourceChangeListener();
	
	private static final String USER_BUNDLE_DIRECTORY_GENERAL = "RadRails Bundles"; //$NON-NLS-1$
	private static final String USER_BUNDLE_DIRECTORY_MACOSX = "/Documents/RadRails Bundles"; //$NON-NLS-1$
	private static final String BUNDLE_FILE = "bundle.rb"; //$NON-NLS-1$
	private static final String RUBY_FILE_EXTENSION = ".rb"; //$NON-NLS-1$
	private static final String BUNDLES_FOLDER_NAME = "bundles"; //$NON-NLS-1$
	private static final String LIB_FOLDER_NAME = "lib"; //$NON-NLS-1$
	private static final String SNIPPETS_FOLDER_NAME = "snippets"; //$NON-NLS-1$
	private static final String COMMANDS_FOLDER_NAME = "commands"; //$NON-NLS-1$
	private static final String USER_HOME_PROPERTY = "user.home"; //$NON-NLS-1$
	private static BundleManager INSTANCE;

	private List<Bundle> _bundles;
	private Map<String, Bundle> _bundlesByPath;
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	@JRubyMethod(name = "instance")
	public static BundleManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new BundleManager();
		}

		return INSTANCE;
	}

	/**
	 * BundleManager
	 */
	private BundleManager()
	{
		// attach resource change listener so we can track changes to the workspace
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener, IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * addBundle
	 * 
	 * @param bundle
	 */
	@JRubyMethod(name = "add_bundle")
	public void addBundle(Bundle bundle)
	{
		if (bundle != null)
		{
			if (this._bundles == null)
			{
				this._bundles = new ArrayList<Bundle>();
			}

			this._bundles.add(bundle);

			if (this._bundlesByPath == null)
			{
				this._bundlesByPath = new HashMap<String, Bundle>();
			}

			this._bundlesByPath.put(bundle.getPath(), bundle);
		}
	}

	/**
	 * getBuiltinsLoadPath
	 * 
	 * @return
	 */
	private String getBuiltinsLoadPath()
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(BUNDLES_FOLDER_NAME), null);
		String result = null;

		try
		{
			URL fileURL = FileLocator.toFileURL(url);
			URI fileURI = URIUtil.toURI(fileURL);	// Use Eclipse to get around Java 1.5 bug on Windows
			File file = new File(fileURI);

			result = file.getAbsolutePath();
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(
				Messages.BundleManager_Cannot_Locate_Built_Ins_Directory,
				new Object[] { url.toString() }
			);

			Activator.logError(message, e);
		}
		catch (URISyntaxException e)
		{
			String message = MessageFormat.format(
				Messages.BundleManager_Malformed_Built_Ins_URI,
				new Object[] { url.toString() }
			);

			Activator.logError(message, e);
		}

		return result;
	}

	/**
	 * getBundleFromPath
	 * 
	 * @param path
	 * @return
	 */
	@JRubyMethod(name = "bundle_from_path")
	public Bundle getBundleFromPath(String path)
	{
		Bundle result = null;

		if (this._bundlesByPath != null)
		{
			result = this._bundlesByPath.get(path);
		}

		return result;
	}
	
	/**
	 * getCommandsFromScope
	 * 
	 * @param scope
	 * @return
	 */
	public Command[] getCommandsFromScope(String scope)
	{
		Command[] result = NO_COMMANDS;
		
		if (this._bundles != null && this._bundles.size() > 0)
		{
			List<Command> commands = new ArrayList<Command>();
			
			for (Bundle bundle : this._bundles)
			{
				for (Command command : bundle.getCommands())
				{
					if (command.getScopeSelector().matches(scope))
					{
						commands.add(command);
					}
				}
			}
			
			result = commands.toArray(new Command[commands.size()]);
		}
		
		return result;
	}

	/**
	 * getCommandsFromScopes
	 * 
	 * @param scopes
	 * @return
	 */
	public Command[] getCommandsFromScopes(String[] scopes)
	{
		Command[] result = NO_COMMANDS;
		
		if (this._bundles != null && this._bundles.size() > 0)
		{
			List<Command> commands = new ArrayList<Command>();
			
			for (Bundle bundle : this._bundles)
			{
				for (Command command : bundle.getCommands())
				{
					if (command.getScopeSelector().matches(scopes))
					{
						commands.add(command);
					}
				}
			}
			
			result = commands.toArray(new Command[commands.size()]);
		}
		
		return result;
	}
	
	/**
	 * getLoadPaths
	 * 
	 * @param resource
	 * @return
	 */
	private List<String> getLoadPaths(File resource)
	{
		File folder = (resource != null && resource.isDirectory()) ? resource : resource.getParentFile();
		List<String> loadPaths = new ArrayList<String>();
		File bundleFolder = folder.getParentFile();
		File bundleLibFolder = new File(bundleFolder.getAbsolutePath() + File.separator + LIB_FOLDER_NAME);
		
		loadPaths.add(this.getBuiltinsLoadPath());
		loadPaths.add(bundleLibFolder.getAbsolutePath());
		loadPaths.add("."); //$NON-NLS-1$
		
		return loadPaths;
	}

	/**
	 * getLoadPaths
	 * 
	 * @param resource
	 * @return
	 */
	private List<String> getLoadPaths(IResource resource)
	{
		return this.getLoadPaths(resource.getLocation().toFile());
	}
	
	/**
	 * getMenusFromScope
	 * 
	 * @param scope
	 * @return
	 */
	public Menu[] getMenusFromScope(String scope)
	{
		Menu[] result = NO_MENUS;
		
		if (this._bundles != null && this._bundles.size() > 0)
		{		
			List<Menu> menus = new ArrayList<Menu>();
			
			for (Bundle bundle : this._bundles)
			{
				for (Menu menu : bundle.getMenus())
				{
					if (menu.getScopeSelector().matches(scope))
					{
						menus.add(menu);
					}
				}
			}
			
			result = menus.toArray(new Menu[menus.size()]);
		}
		
		return result;
	}
	
	/**
	 * getMenusFromScopes
	 * 
	 * @param scopes
	 * @return
	 */
	public Menu[] getMenusFromScopes(String[] scopes)
	{
		Menu[] result = NO_MENUS;
		
		if (this._bundles != null && this._bundles.size() > 0)
		{		
			List<Menu> menus = new ArrayList<Menu>();
			
			for (Bundle bundle : this._bundles)
			{
				for (Menu menu : bundle.getMenus())
				{
					if (menu.getScopeSelector().matches(scopes))
					{
						menus.add(menu);
					}
				}
			}
			
			result = menus.toArray(new Menu[menus.size()]);
		}
		
		return result;
	}
	
	/**
	 * getSnippetsFromScope
	 * 
	 * @param scope
	 * @return
	 */
	public Snippet[] getSnippetsFromScope(String scope)
	{
		Snippet[] result = NO_SNIPPETS;
		
		if (this._bundles != null && this._bundles.size() > 0)
		{
			List<Snippet> snippets = new ArrayList<Snippet>();
			
			for (Bundle bundle : this._bundles)
			{
				for (Snippet snippet : bundle.getSnippets())
				{
					if (snippet.getScopeSelector().matches(scope))
					{
						snippets.add(snippet);
					}
				}
			}
			
			result = snippets.toArray(new Snippet[snippets.size()]);
		}
		
		return result;
	}

	/**
	 * getSnippetsFromScopes
	 * 
	 * @param scopes
	 * @return
	 */
	public Snippet[] getSnippetsFromScopes(String[] scopes)
	{
		Snippet[] result = NO_SNIPPETS;
		
		if (this._bundles != null && this._bundles.size() > 0)
		{
			List<Snippet> snippets = new ArrayList<Snippet>();
			
			for (Bundle bundle : this._bundles)
			{
				for (Snippet snippet : bundle.getSnippets())
				{
					if (snippet.getScopeSelector().matches(scopes))
					{
						snippets.add(snippet);
					}
				}
			}
	
			result = snippets.toArray(new Snippet[snippets.size()]);
		}
		
		return result;
	}

	/**
	 * getUserBundlePath
	 * 
	 * @return
	 */
	public String getUserBundlePath()
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
	 * loadBundles
	 */
	public void loadBundles()
	{
		this.loadProjectBundles();
		this.loadUserBundles();
		
		this.showBundles();
	}
	
	/**
	 * loadProjectBundles
	 */
	private void loadProjectBundles()
	{
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			this.processProject(project);
		}
	}
	
	/**
	 * loadUserBundles
	 */
	private void loadUserBundles()
	{
		String userBundlesPath = this.getUserBundlePath();
		
		if (userBundlesPath != null && userBundlesPath.length() > 0)
		{
			File userBundles = new File(userBundlesPath);
			
			if (userBundles.exists() && userBundles.isDirectory() && userBundles.canRead())
			{
				File[] bundles = userBundles.listFiles(new FileFilter()
				{
					public boolean accept(File pathname)
					{
						return pathname.isDirectory() && pathname.canRead();
					}
				});
				
				for (File bundle : bundles)
				{
					this.processBundle(bundle, true);
				}
			}
		}
	}

	/**
	 * moveBundle
	 * 
	 * @param oldFolder
	 * @param newFolder
	 */
	public void moveBundle(String oldFolder, String newFolder)
	{
		if (newFolder != null && newFolder.length() > 0)
		{
			Bundle bundle = this.getBundleFromPath(oldFolder);

			if (bundle != null)
			{
				// remove bundle path reference
				this._bundlesByPath.remove(oldFolder);

				// update bundle path
				bundle.moveTo(newFolder);

				// add new path reference
				this._bundlesByPath.put(newFolder, bundle);
			}
		}
	}

	/**
	 * processBundle
	 * 
	 * @param bundleRoot
	 * @param processChildren
	 */
	public void processBundle(IResource bundleRoot, boolean processChildren)
	{
		this.processBundle(bundleRoot.getLocation().toFile(), processChildren);
	}
	
	/**
	 * processBundle
	 * 
	 * @param bundleRoot
	 * @param processChildren
	 */
	public void processBundle(File bundleRoot, boolean processChildren)
	{
		String bundlePath = bundleRoot.getAbsolutePath();
		File bundleFile = new File(bundlePath + File.separator + BUNDLE_FILE);
		
		if (bundleFile.exists() && bundleFile.canRead())
		{
			String fullPath = bundleFile.getAbsolutePath();
			List<String> loadPaths = new ArrayList<String>();
			
			loadPaths.add(this.getBuiltinsLoadPath());
			loadPaths.add("."); //$NON-NLS-1$
			
			ScriptingEngine.getInstance().runScript(fullPath, loadPaths);
			
			if (processChildren)
			{
				// process snippets and command folders
				this.processFolder(new File(bundlePath + File.separator + SNIPPETS_FOLDER_NAME));
				this.processFolder(new File(bundlePath + File.separator + COMMANDS_FOLDER_NAME));
			}
		}
		else
		{
			System.out.println(Messages.BundleManager_Missing_Bundle_File + bundlePath);
		}
	}
	
	/**
	 * processFolder
	 * 
	 * @param folder
	 */
	private void processFolder(File folder)
	{
		if (folder != null && folder.isDirectory() && folder.canRead())
		{
			List<String> loadPaths = this.getLoadPaths(folder);
			File[] files = folder.listFiles(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return name.toLowerCase().endsWith(RUBY_FILE_EXTENSION);
				}
			});
			
			for (File file: files)
			{
				String fullPath = file.getAbsolutePath();
				
				ScriptingEngine.getInstance().runScript(fullPath, loadPaths);
			}
		}
	}

	/**
	 * processProject
	 * 
	 * @param project
	 */
	private void processProject(IProject project)
	{
		IFolder bundlesFolder = project.getFolder(BUNDLES_FOLDER_NAME);

		if (bundlesFolder != null)
		{
			try
			{
				for (IResource resource : bundlesFolder.members())
				{
					if (resource instanceof IFolder)
					{
						this.processBundle((IFolder) resource, true);
					}
				}
			}
			catch (CoreException e)
			{
			}
		}
	}

	/**
	 * processFile
	 * 
	 * @param file
	 */
	public void processSnippetOrCommand(IResource file)
	{
		if (file != null)
		{
			if (file.getName().toLowerCase().endsWith(RUBY_FILE_EXTENSION))
			{
				List<String> loadPaths = this.getLoadPaths(file);
				String fullPath = file.getLocation().toPortableString();

				ScriptingEngine.getInstance().runScript(fullPath, loadPaths);
			}
		}
	}

	/**
	 * removeBundle
	 * 
	 * @param bundle
	 */
	public void removeBundle(Bundle bundle)
	{
		if (bundle != null)
		{
			if (this._bundles != null)
			{
				this._bundles.remove(bundle);
			}
		}
	}

	/**
	 * removeBundle
	 * 
	 * @param bundleFolder
	 */
	public void removeBundle(String bundleFolder)
	{
		Bundle bundle = this.getBundleFromPath(bundleFolder);

		if (bundle != null)
		{
			this.removeBundle(bundle);
		}
	}

	/**
	 * removeSnippetOrCommand
	 * 
	 * @param file
	 */
	public void removeSnippetOrCommand(IResource file)
	{
		if (file != null)
		{
			IContainer parentFolder = file.getParent();
			IContainer bundleFolder = parentFolder.getParent();
			Bundle bundle = this.getBundleFromPath(bundleFolder.getLocation().toPortableString());
			
			if (bundle != null)
			{
				if (parentFolder.getName().equals(SNIPPETS_FOLDER_NAME))
				{
					Snippet[] snippets = bundle.findSnippetsFromPath(file.getLocation().toPortableString());
					
					for (Snippet snippet : snippets)
					{
						bundle.removeSnippet(snippet);
					}
				}
				else if (parentFolder.getName().equals(COMMANDS_FOLDER_NAME))
				{
					Command[] commands = bundle.findCommandsFromPath(file.getLocation().toPortableString());
					
					for (Command command : commands)
					{
						bundle.removeCommand(command);
					}
				}
			}
		}
	}
	
	/**
	 * showBundles
	 */
	public void showBundles()
	{
		if (this._bundles != null)
		{
			for (Bundle bundle : this._bundles)
			{
				System.out.println(bundle.toSource());
			}
		}
		else
		{
			System.out.println(Messages.BundleManager_NO_BUNDLES);
		}
	}
}
