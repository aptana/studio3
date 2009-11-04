package com.aptana.scripting.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.scripting.Activator;
import com.aptana.scripting.ScriptingEngine;

public class BundleLoader
{
	private static final String BUNDLES_FOLDER_NAME = "bundles"; //$NON-NLS-1$
	private static final String SNIPPETS_FOLDER_NAME = "snippets"; //$NON-NLS-1$
	private static final String COMMANDS_FOLDER_NAME = "commands"; //$NON-NLS-1$
	private static BundleLoader INSTANCE;
	
	/**
	 * BundleLoader
	 */
	private BundleLoader()
	{
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
			String fullPath = bundleFile.getLocation().toPortableString();
			List<String> loadPaths = new ArrayList<String>();
			
			loadPaths.add(this.getBuiltinsLoadPath());
			loadPaths.add(".");
			
			ScriptingEngine.getInstance().runScript(fullPath, loadPaths);
			
			// process snippets and command folders
			this.processFolder(bundleRoot.getFolder(SNIPPETS_FOLDER_NAME));
			this.processFolder(bundleRoot.getFolder(COMMANDS_FOLDER_NAME));
		}
		else
		{
			System.out.println("No bundle.rb for " + bundlePath);
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
			try
			{
				for (IResource resource : folder.members())
				{
					if (resource.getName().toLowerCase().endsWith(".rb"))
					{
						String fullPath = resource.getLocation().toPortableString();
						List<String> loadPaths = new ArrayList<String>();
						IFolder bundleFolder = (IFolder) folder.getParent();
						IFolder bundlesFolder = (IFolder) bundleFolder.getParent();
						
						loadPaths.add(this.getBuiltinsLoadPath());
						loadPaths.add(bundlesFolder.getLocation().toPortableString());
						loadPaths.add(bundleFolder.getLocation().toPortableString());
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
