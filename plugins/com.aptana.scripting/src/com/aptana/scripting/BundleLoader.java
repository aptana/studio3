package com.aptana.scripting;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class BundleLoader
{
	private static final String BUNDLES_FOLDER_NAME = "bundles";
	private static final String SNIPPETS_FOLDER_NAME = "snippets";
	private static final String COMMANDS_FOLDER_NAME = "commands";
	private static BundleLoader INSTANCE;
	
	/**
	 * BundleLoader
	 */
	private BundleLoader()
	{
	}
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static BundleLoader getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new BundleLoader();
		}
		
		return INSTANCE;
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
	 * processBundle
	 * 
	 * @param bundleRoot
	 */
	private void processBundle(IFolder bundleRoot)
	{
		IFile bundleFile = bundleRoot.getFile("bundle.rb");
		String bundlePath = bundleRoot.getLocation().toPortableString();

		if (bundleFile.exists())
		{
			Bundle bundle = new Bundle(bundlePath);
			
			// process snippets
			this.processSnippets(bundle, bundleRoot);
			
			// process commands
			this.processCommands(bundle, bundleRoot);
			
			// add to our list
			Bundle.addBundle(bundle);
		}
		else
		{
			System.out.println("No bundle.rb for " + bundlePath);
		}
	}
	
	/**
	 * processCommands
	 * 
	 * @param bundle
	 * @param bundleRoot
	 */
	private void processCommands(Bundle bundle, IFolder bundleRoot)
	{
		IFolder commands = bundleRoot.getFolder(COMMANDS_FOLDER_NAME);
		
		if (commands != null)
		{
			try
			{
				for (IResource resource : commands.members())
				{
					if (resource.getName().toLowerCase().endsWith(".rb"))
					{
						Command command = new Command(resource.getLocation().toPortableString());
						bundle.addCommand(command);
						
						String fullPath = resource.getLocation().toPortableString();
						List<String> loadPaths = new ArrayList<String>();
						IFolder bundlesFolder = (IFolder) bundleRoot.getParent();
						
						loadPaths.add(bundlesFolder.getLocation().toPortableString());
						loadPaths.add(bundleRoot.getLocation().toPortableString());
						loadPaths.add(".");
						
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
	 * processSnippets
	 * 
	 * @param bundle
	 * @param bundleRoot
	 */
	private void processSnippets(Bundle bundle, IFolder bundleRoot)
	{
		IFolder snippets = bundleRoot.getFolder(SNIPPETS_FOLDER_NAME);
		
		if (snippets != null)
		{
			try
			{
				for (IResource resource : snippets.members())
				{
					if (resource.getName().toLowerCase().endsWith(".rb"))
					{
						Snippet snippet = new Snippet(resource.getLocation().toPortableString());
						bundle.addSnippet(snippet);
						
						String fullPath = resource.getLocation().toPortableString();
						List<String> loadPaths = new ArrayList<String>();
						IFolder bundlesFolder = (IFolder) bundleRoot.getParent();
						
						loadPaths.add(bundlesFolder.getLocation().toPortableString());
						loadPaths.add(bundleRoot.getLocation().toPortableString());
						loadPaths.add(".");
						
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
}
