package com.aptana.scripting.model;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
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
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.jruby.anno.JRubyMethod;

import com.aptana.scripting.ResourceDeltaVisitor;
import com.aptana.scripting.ScriptingEngine;

public class BundleManager implements IResourceChangeListener
{
	static final Menu[] NO_MENUS = new Menu[0];
	static final Snippet[] NO_SNIPPETS = new Snippet[0];
	static final Command[] NO_COMMANDS = new Command[0];
	
	private static final IResourceDeltaVisitor deltaVistor = new ResourceDeltaVisitor();
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
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
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
		return ScriptingEngine.getBuiltinsLoadPath();
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
		return this.getCommandsFromScopes(new String[] { scope }, null);
	}

	/**
	 * getCommandsFromScope
	 * 
	 * @param scope
	 * @param filter
	 * @return
	 */
	public Command[] getCommandsFromScope(String scope, IModelFilter filter)
	{
		return this.getCommandsFromScopes(new String[] { scope }, filter);
	}
	
	/**
	 * getCommandsFromScopes
	 * 
	 * @param scopes
	 * @return
	 */
	public Command[] getCommandsFromScopes(String[] scopes)
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
	public Command[] getCommandsFromScopes(String[] scopes, IModelFilter filter)
	{
		Command[] result = NO_COMMANDS;
		
		if (this._bundles != null && this._bundles.size() > 0 && scopes != null && scopes.length > 0)
		{
			List<Command> commands = new ArrayList<Command>();
			
			for (Bundle bundle : this._bundles)
			{
				for (Command command : bundle.getCommands())
				{
					if (command.matches(scopes) && ((filter != null) ? filter.include(command) : true))
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
		return this.getMenusFromScopes(new String[] { scope }, null);
	}
	
	/**
	 * getMenusFromScope
	 * 
	 * @param scope
	 * @param filter
	 * @return
	 */
	public Menu[] getMenusFromScope(String scope, IModelFilter filter)
	{
		return this.getMenusFromScopes(new String[] { scope }, filter);
	}
	
	/**
	 * getMenusFromScope
	 * 
	 * @param scopes
	 * @return
	 */
	public Menu[] getMenusFromScope(String[] scopes)
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
	public Menu[] getMenusFromScopes(String[] scopes, IModelFilter filter)
	{
		Menu[] result = NO_MENUS;
		
		if (this._bundles != null && this._bundles.size() > 0 && scopes != null && scopes.length > 0)
		{		
			List<Menu> menus = new ArrayList<Menu>();
			
			for (Bundle bundle : this._bundles)
			{
				for (Menu menu : bundle.getMenus())
				{
					if (menu.matches(scopes) && ((filter != null) ? filter.include(menu) : true))
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
		return this.getSnippetsFromScopes(new String[] { scope }, null);
	}
	
	/**
	 * getSnippetsFromScope
	 * 
	 * @param scope
	 * @param filter
	 * @return
	 */
	public Snippet[] getSnippetsFromScope(String scope, IModelFilter filter)
	{
		return this.getSnippetsFromScopes(new String[] { scope }, filter);
	}

	/**
	 * getSnippetsFromScopes
	 * 
	 * @param scopes
	 * @return
	 */
	public Snippet[] getSnippetsFromScopes(String[] scopes)
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
	public Snippet[] getSnippetsFromScopes(String[] scopes, IModelFilter filter)
	{
		Snippet[] result = NO_SNIPPETS;
		
		if (this._bundles != null && this._bundles.size() > 0 && scopes != null && scopes.length > 0)
		{
			List<Snippet> snippets = new ArrayList<Snippet>();
			
			for (Bundle bundle : this._bundles)
			{
				for (Snippet snippet : bundle.getSnippets())
				{
					if (snippet.matches(scopes) && ((filter != null) ? filter.include(snippet) : true))
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
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event)
	{
		try
		{
			event.getDelta().accept(deltaVistor);
		}
		catch (CoreException e)
		{
			// log an error in the error log
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
