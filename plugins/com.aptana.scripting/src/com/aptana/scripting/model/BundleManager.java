package com.aptana.scripting.model;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.jruby.anno.JRubyMethod;

import com.aptana.scripting.ScriptingEngine;

public class BundleManager implements IResourceChangeListener, IResourceDeltaVisitor
{
	static final MenuElement[] NO_MENUS = new MenuElement[0];
	static final SnippetElement[] NO_SNIPPETS = new SnippetElement[0];
	static final CommandElement[] NO_COMMANDS = new CommandElement[0];
	
	private static final String USER_BUNDLE_DIRECTORY_GENERAL = "RadRails Bundles"; //$NON-NLS-1$
	private static final String USER_BUNDLE_DIRECTORY_MACOSX = "/Documents/RadRails Bundles"; //$NON-NLS-1$
	private static final String BUNDLE_FILE = "bundle.rb"; //$NON-NLS-1$
	private static final String RUBY_FILE_EXTENSION = ".rb"; //$NON-NLS-1$
	private static final String BUNDLES_FOLDER_NAME = "bundles"; //$NON-NLS-1$
	private static final String LIB_FOLDER_NAME = "lib"; //$NON-NLS-1$
	private static final String SNIPPETS_FOLDER_NAME = "snippets"; //$NON-NLS-1$
	private static final String COMMANDS_FOLDER_NAME = "commands"; //$NON-NLS-1$
	private static final String USER_HOME_PROPERTY = "user.home"; //$NON-NLS-1$
	private static final Pattern BUNDLE_PATTERN = Pattern.compile("/.+?/bundles/.+?/bundle\\.rb$"); //$NON-NLS-1$
	private static final Pattern FILE_PATTERN = Pattern.compile("/.+?/bundles/.+?/(?:commands|snippets)/[^/]+\\.rb$"); //$NON-NLS-1$
	private static final Pattern USER_BUNDLE_PATTERN;
	private static final Pattern USER_FILE_PATTERN;
	
	private static BundleManager INSTANCE;
	
	private List<ElementChangeListener> _elementListeners;
	private List<BundleElement> _bundles;
	private Map<String, BundleElement> _bundlesByPath;

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
	 * static constructor
	 */
	static
	{
		String userBundlesRoot = BundleManager.getInstance().getUserBundlePath().toLowerCase();
		
		// TODO: make this work on win32
		USER_BUNDLE_PATTERN = Pattern.compile(userBundlesRoot + "/.+?/bundle\\.rb$"); //$NON-NLS-1$
		USER_FILE_PATTERN = Pattern.compile(userBundlesRoot + "/.+?/(?:commands|snippets)/[^/]+\\.rb$"); //$NON-NLS-1$
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
	public void addBundle(BundleElement bundle)
	{
		if (bundle != null)
		{
			if (this._bundles == null)
			{
				this._bundles = new ArrayList<BundleElement>();
			}

			this._bundles.add(bundle);

			if (this._bundlesByPath == null)
			{
				this._bundlesByPath = new HashMap<String, BundleElement>();
			}

			this._bundlesByPath.put(bundle.getPath(), bundle);
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
	protected void fireElementAddedEvent(AbstractElement element)
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
	protected void fireElementDeletedEvent(AbstractElement element)
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
	protected void fireElementModifiedEvent(AbstractElement element)
	{
		if (this._elementListeners != null)
		{
			for (ElementChangeListener listener : this._elementListeners)
			{
				listener.elementModified(element);
			}
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
	public BundleElement getBundleFromPath(String path)
	{
		BundleElement result = null;

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
		CommandElement[] result = NO_COMMANDS;
		
		if (this._bundles != null && this._bundles.size() > 0 && scopes != null && scopes.length > 0)
		{
			List<CommandElement> commands = new ArrayList<CommandElement>();
			
			for (BundleElement bundle : this._bundles)
			{
				for (CommandElement command : bundle.getCommands())
				{
					if (command.matches(scopes) && ((filter != null) ? filter.include(command) : true))
					{
						commands.add(command);
					}
				}
			}
			
			result = commands.toArray(new CommandElement[commands.size()]);
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
		MenuElement[] result = NO_MENUS;
		
		if (this._bundles != null && this._bundles.size() > 0 && scopes != null && scopes.length > 0)
		{		
			List<MenuElement> menus = new ArrayList<MenuElement>();
			
			for (BundleElement bundle : this._bundles)
			{
				for (MenuElement menu : bundle.getMenus())
				{
					if (menu.matches(scopes) && ((filter != null) ? filter.include(menu) : true))
					{
						menus.add(menu);
					}
				}
			}
			
			result = menus.toArray(new MenuElement[menus.size()]);
		}
		
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
		SnippetElement[] result = NO_SNIPPETS;
		
		if (this._bundles != null && this._bundles.size() > 0 && scopes != null && scopes.length > 0)
		{
			List<SnippetElement> snippets = new ArrayList<SnippetElement>();
			
			for (BundleElement bundle : this._bundles)
			{
				for (SnippetElement snippet : bundle.getSnippets())
				{
					if (snippet.matches(scopes) && ((filter != null) ? filter.include(snippet) : true))
					{
						snippets.add(snippet);
					}
				}
			}
	
			result = snippets.toArray(new SnippetElement[snippets.size()]);
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
		
		//this.showBundles();
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
			BundleElement bundle = this.getBundleFromPath(oldFolder);

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
	 * processBundle
	 * 
	 * @param delta
	 */
	private boolean processBundle(IResourceDelta delta)
	{
		IResource file = delta.getResource();
		boolean visitChildren = true;

		if (file != null && file.getLocation() != null)
		{
			IResource bundleFolder = file.getParent();
			String bundleFolderPath = bundleFolder.getLocation().toPortableString();

			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					BundleManager.getInstance().processBundle(bundleFolder, false);
					break;

				case IResourceDelta.REMOVED:
					BundleManager.getInstance().removeBundle(bundleFolderPath);
					break;

				case IResourceDelta.CHANGED:
					if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0)
					{
						String oldPath = delta.getMovedFromPath().toPortableString();
						
						BundleManager.getInstance().moveBundle(oldPath, bundleFolderPath);
					}
					if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0)
					{
						String newPath = delta.getMovedToPath().toString();
						
						BundleManager.getInstance().moveBundle(bundleFolderPath, newPath);
					}
					if ((delta.getFlags() & IResourceDelta.REPLACED) != 0)
					{
						BundleManager.getInstance().removeBundle(bundleFolderPath);
						BundleManager.getInstance().processBundle(bundleFolder, false);
					}
					if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
					{
						BundleManager.getInstance().removeBundle(bundleFolderPath);
						BundleManager.getInstance().processBundle(bundleFolder, false);
					}
					break;
			}
		}

		return visitChildren;
	}

	/**
	 * processFile
	 * 
	 * @param delta
	 */
	private boolean processFile(IResourceDelta delta)
	{
		IResource file = delta.getResource();
		boolean visitChildren = true;

		if (file != null && file.getLocation() != null)
		{
			BundleManager manager = BundleManager.getInstance();
			
			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					BundleManager.getInstance().processSnippetOrCommand(file);
					break;

				case IResourceDelta.REMOVED:
					BundleManager.getInstance().removeSnippetOrCommand(file);
					break;

				case IResourceDelta.CHANGED:
					if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0)
					{
						IPath movedFromPath = delta.getMovedFromPath();
						IResource movedFrom = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(movedFromPath);
						
						if (movedFrom != null && movedFrom instanceof IFile)
						{
							manager.removeSnippetOrCommand(movedFrom);
							manager.processSnippetOrCommand(file);
						}
					}
					else if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0)
					{
						IPath movedToPath = delta.getMovedToPath();
						IResource movedTo = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(movedToPath);
						
						if (movedTo != null && movedTo instanceof IFile)
						{
							manager.removeSnippetOrCommand(file);
							manager.processSnippetOrCommand(movedTo);
						}
					}
					else if ((delta.getFlags() & IResourceDelta.REPLACED) != 0)
					{
						manager.removeSnippetOrCommand(file);
						manager.processSnippetOrCommand(file);
					}
					else if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
					{
						manager.removeSnippetOrCommand(file);
						manager.processSnippetOrCommand(file);
					}
					break;
			}
		}

		return visitChildren;
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
	public void removeBundle(BundleElement bundle)
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
		BundleElement bundle = this.getBundleFromPath(bundleFolder);

		if (bundle != null)
		{
			this.removeBundle(bundle);
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
			BundleElement bundle = this.getBundleFromPath(bundleFolder.getLocation().toPortableString());
			
			if (bundle != null)
			{
				if (parentFolder.getName().equals(SNIPPETS_FOLDER_NAME))
				{
					SnippetElement[] snippets = bundle.findSnippetsFromPath(file.getLocation().toPortableString());
					
					for (SnippetElement snippet : snippets)
					{
						bundle.removeSnippet(snippet);
					}
				}
				else if (parentFolder.getName().equals(COMMANDS_FOLDER_NAME))
				{
					CommandElement[] commands = bundle.findCommandsFromPath(file.getLocation().toPortableString());
					
					for (CommandElement command : commands)
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
			event.getDelta().accept(this);
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
			for (BundleElement bundle : this._bundles)
			{
				System.out.println(bundle.toSource());
			}
		}
		else
		{
			System.out.println(Messages.BundleManager_NO_BUNDLES);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException
	{
		String fullProjectPath = delta.getFullPath().toString().toLowerCase();
		String fullPath = delta.getResource().getLocation().toPortableString().toLowerCase();
		boolean visitChildren = true;
		
		if (BUNDLE_PATTERN.matcher(fullProjectPath).matches() || USER_BUNDLE_PATTERN.matcher(fullPath).matches())
		{
			visitChildren = this.processBundle(delta);
		}
		else if (FILE_PATTERN.matcher(fullProjectPath).matches() || USER_FILE_PATTERN.matcher(fullPath).matches())
		{
			visitChildren = this.processFile(delta);
		}

		return visitChildren;
	}
}
