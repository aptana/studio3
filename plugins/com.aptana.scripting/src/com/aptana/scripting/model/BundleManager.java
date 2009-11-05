package com.aptana.scripting.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.jruby.anno.JRubyMethod;

import com.aptana.scripting.Activator;
import com.aptana.scripting.ScriptingEngine;

public class BundleManager
{
	private static final String BUNDLES_FOLDER_NAME = "bundles"; //$NON-NLS-1$
	private static final String SNIPPETS_FOLDER_NAME = "snippets"; //$NON-NLS-1$
	private static final String COMMANDS_FOLDER_NAME = "commands"; //$NON-NLS-1$
	private static BundleManager INSTANCE;
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	@JRubyMethod(name="instance")
	public static BundleManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new BundleManager();
		}
		
		return INSTANCE;
	}
	private List<Bundle> _bundles;
	
	private Map<String,Bundle> _bundlesByPath;
	
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
	@JRubyMethod(name="add_bundle")
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
	 * getBuildtinsLoadPath
	 * @return
	 */
	private String getBuiltinsLoadPath()
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(BUNDLES_FOLDER_NAME), null);
		String result = null;

		try
		{
			URL fileURL = FileLocator.toFileURL(url);
			File file = new File(fileURL.toURI());
			
			result = file.getAbsolutePath();
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(
				"Error locating built-ins directory",
				new Object[] { url.toString() }
			);
			
			Activator.logError(message, e);
		}
		catch (URISyntaxException e)
		{
			String message = MessageFormat.format(
				"Malformed built-ins directory URI",
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
	@JRubyMethod(name="bundle_from_path")
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
		List<Command> result = new ArrayList<Command>();
		
		for (Bundle bundle : this._bundles)
		{
			Command[] commands = bundle.getCommands();
			
			result.addAll(Arrays.asList(commands));
		}
		
		return result.toArray(new Command[result.size()]);
	}
	
	/**
	 * getLoadPaths
	 * 
	 * @param folder
	 * @return
	 */
	private List<String> getLoadPaths(IResource resource)
	{
		IFolder folder = (resource instanceof IFolder) ? (IFolder) resource : (IFolder) resource.getParent();
		List<String> loadPaths = new ArrayList<String>();
		IFolder bundleFolder = (IFolder) folder.getParent();
		IFolder bundlesFolder = (IFolder) bundleFolder.getParent();
		
		loadPaths.add(this.getBuiltinsLoadPath());
		loadPaths.add(bundlesFolder.getLocation().toPortableString());
		loadPaths.add(bundleFolder.getLocation().toPortableString());
		loadPaths.add(".");
		
		return loadPaths;
	}
	
	/**
	 * getSnippetsFromScope
	 * 
	 * @param scope
	 * @return
	 */
	public Snippet[] getSnippetsFromScope(String scope)
	{
		List<Snippet> result = new ArrayList<Snippet>();
		
		for (Bundle bundle : this._bundles)
		{
			Snippet[] snippets = bundle.getSnippets();
			
			result.addAll(Arrays.asList(snippets));
		}
		
		return result.toArray(new Snippet[result.size()]);
	}
	
	/**
	 * loadProjectBundles
	 */
	public void loadProjectBundles()
	{
		// possibly clear current scripts here

		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			this.processProject(project);
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
	 */
	public void processBundle(IFolder bundleRoot)
	{
		this.processBundle(bundleRoot, true);
	}
	
	/**
	 * processBundle
	 * 
	 * @param bundleRoot
	 * @param processChildren
	 */
	public void processBundle(IFolder bundleRoot, boolean processChildren)
	{
		IFile bundleFile = bundleRoot.getFile("bundle.rb");
		String bundlePath = bundleRoot.getLocation().toPortableString();

		if (bundleFile.exists())
		{
			String fullPath = bundleFile.getLocation().toPortableString();
			List<String> loadPaths = new ArrayList<String>();
			
			loadPaths.add(this.getBuiltinsLoadPath());
			loadPaths.add(".");
			
			ScriptingEngine.getInstance().runScript(fullPath, loadPaths);
			
			if (processChildren)
			{
				// process snippets and command folders
				this.processFolder(bundleRoot.getFolder(SNIPPETS_FOLDER_NAME));
				this.processFolder(bundleRoot.getFolder(COMMANDS_FOLDER_NAME));
			}
		}
		else
		{
			System.out.println("No bundle.rb for " + bundlePath);
		}
	}

	/**
	 * processFile
	 * 
	 * @param file
	 */
	public void processFile(IFile file)
	{
		if (file != null)
		{
			List<String> loadPaths = getLoadPaths(file);
			
			if (file.getName().toLowerCase().endsWith(".rb"))
			{
				String fullPath = file.getLocation().toPortableString();
				
				ScriptingEngine.getInstance().runScript(fullPath, loadPaths);
			}
		}
	}
	
	/**
	 * processFolder
	 * 
	 * @param bundleRoot
	 */
	private void processFolder(IFolder folder)
	{
		if (folder != null)
		{
			List<String> loadPaths = getLoadPaths(folder);
			
			try
			{
				for (IResource resource : folder.members())
				{
					if (resource.getName().toLowerCase().endsWith(".rb"))
					{
						String fullPath = resource.getLocation().toPortableString();
						
						ScriptingEngine.getInstance().runScript(fullPath, loadPaths);
					}
				}
			}
			catch (CoreException e)
			{
				e.printStackTrace();
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
						this.processBundle((IFolder) resource);
					}
				}
			}
			catch (CoreException e)
			{
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
	 * showBundles
	 */
	public void showBundles()
	{
		if (this._bundles != null)
		{
			for (Bundle bundle : this._bundles)
			{
				System.out.println(bundle);
			}
		}
	}
}
