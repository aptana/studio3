package com.aptana.scripting.model;

import java.io.File;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class BundleMonitor implements IResourceChangeListener, IResourceDeltaVisitor
{
	private static final Pattern BUNDLE_PATTERN = Pattern.compile("/.+?/bundles/.+?/bundle\\.rb$"); //$NON-NLS-1$
	private static final Pattern FILE_PATTERN = Pattern.compile("/.+?/bundles/.+?/(?:commands|snippets)/[^/]+\\.rb$"); //$NON-NLS-1$
	private static final Pattern USER_BUNDLE_PATTERN;
	private static final Pattern USER_FILE_PATTERN;
	
	private static BundleMonitor INSTANCE;
	
	/**
	 * static constructor
	 */
	static
	{
		String userBundlesRoot = BundleManager.getInstance().getUserBundlesPath();
		
		// TODO: make this work on win32
		USER_BUNDLE_PATTERN = Pattern.compile(userBundlesRoot + "/.+?/bundle\\.rb$"); //$NON-NLS-1$
		USER_FILE_PATTERN = Pattern.compile(userBundlesRoot + "/.+?/(?:commands|snippets)/[^/]+\\.rb$"); //$NON-NLS-1$
	}
	
	/**
	 * BundleMonitor
	 */
	private BundleMonitor()
	{
	}
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static BundleMonitor getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new BundleMonitor();
		}
		
		return INSTANCE;
	}
	
	/**
	 * processFile
	 * 
	 * @param delta
	 */
	private void processFile(IResourceDelta delta)
	{
		IResource resource = delta.getResource();

		if (resource != null && resource.getLocation() != null)
		{
			BundleManager manager = BundleManager.getInstance();
			File file = resource.getLocation().toFile();
			
			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					manager.loadScript(file);
					break;

				case IResourceDelta.REMOVED:
					manager.unloadScript(file);
					break;

				case IResourceDelta.CHANGED:
					if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0)
					{
						IPath movedFromPath = delta.getMovedFromPath();
						IResource movedFrom = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(movedFromPath);
						
						if (movedFrom != null && movedFrom instanceof IFile)
						{
							manager.loadScript(movedFrom.getLocation().toFile());
							manager.loadScript(file);
						}
					}
					else if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0)
					{
						IPath movedToPath = delta.getMovedToPath();
						IResource movedTo = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(movedToPath);
						
						if (movedTo != null && movedTo instanceof IFile)
						{
							manager.unloadScript(file);
							manager.loadScript(movedTo.getLocation().toFile());
						}
					}
					else if ((delta.getFlags() & IResourceDelta.REPLACED) != 0)
					{
						manager.reloadScript(file);
					}
					else if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
					{
						manager.reloadScript(file);
					}
					break;
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
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException
	{
		String fullProjectPath = delta.getFullPath().toString();
		String fullPath = delta.getResource().getLocation().toPortableString();
		
		if (BUNDLE_PATTERN.matcher(fullProjectPath).matches() || USER_BUNDLE_PATTERN.matcher(fullPath).matches())
		{
			this.processFile(delta);
		}
		else if (FILE_PATTERN.matcher(fullProjectPath).matches() || USER_FILE_PATTERN.matcher(fullPath).matches())
		{
			this.processFile(delta);
		}

		return true;
	}
}
