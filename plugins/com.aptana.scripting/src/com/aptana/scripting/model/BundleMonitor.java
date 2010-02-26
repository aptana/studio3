package com.aptana.scripting.model;

import java.io.File;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.aptana.filewatcher.FileWatcher;
import com.aptana.scripting.Activator;

public class BundleMonitor implements IResourceChangeListener, IResourceDeltaVisitor, JNotifyListener
{
	// TODO: use constants from BundleManager for bundles, commands, and snippets directory names
	private static final Pattern USER_BUNDLE_PATTERN = Pattern.compile(".+?/bundle\\.rb$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final Pattern USER_FILE_PATTERN = Pattern.compile(".+?/(?:commands|snippets)/[^/]+\\.rb$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final Pattern BUNDLE_PATTERN = Pattern.compile("/.+?/bundles/.+?/bundle\\.rb$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final Pattern FILE_PATTERN = Pattern.compile("/.+?/bundles/.+?/(?:commands|snippets)/[^/]+\\.rb$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	private static BundleMonitor INSTANCE;

	private boolean _registered;
	private int _watchId;

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
	 * BundleMonitor
	 */
	private BundleMonitor()
	{
		this._watchId = -1;
	}

	/**
	 * beginMonitoring
	 * 
	 * @throws JNotifyException
	 */
	public synchronized void beginMonitoring() throws JNotifyException
	{
		if (this._registered == false)
		{
			// begin monitoring resource changes
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
			
			// Make sure the user bundles directory exists
			String userBundlePath = BundleManager.getInstance().getUserBundlesPath();
			File bundleDirectory = new File(userBundlePath);
			boolean directoryExists = true;
			
			if (bundleDirectory.exists() == false)
			{
				directoryExists = bundleDirectory.mkdirs();
			}
			else
			{
				directoryExists = bundleDirectory.isDirectory();
			}
			
			if (directoryExists)
			{
				try
				{
					this._watchId = FileWatcher.addWatch(BundleManager.getInstance().getUserBundlesPath(), IJNotify.FILE_ANY, true, this);
				}
				catch (JNotifyException e)
				{
					Activator.logError("An error occurred while registering a file watcher", e);
				}
			}
			else
			{
				String message = MessageFormat.format(
					"Unable to register file watcher for {0}. The path is not a directory or does not exist",
					userBundlePath
				);
				
				Activator.logError(message, null);
			}

			this._registered = true;
		}
	}

	/**
	 * endMonitoring
	 * 
	 * @throws JNotifyException
	 */
	public synchronized void endMonitoring() throws JNotifyException
	{
		if (this._registered)
		{
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(INSTANCE);
			
			if (this._watchId != -1)
			{
				FileWatcher.removeWatch(this._watchId);
				this._watchId = -1;
			}
			
			this._registered = false;
		}
	}

	/**
	 * fileCreated
	 * 
	 * @param wd
	 * @param rootPath
	 * @param name
	 */
	public void fileCreated(int wd, String rootPath, String name)
	{
		if (USER_BUNDLE_PATTERN.matcher(name).matches() || USER_FILE_PATTERN.matcher(name).matches())
		{
			File file = new File(rootPath + File.separator + name);
			
			BundleManager.getInstance().loadScript(file);
		}
	}

	/**
	 * fileDeleted
	 * 
	 * @param wd
	 * @param rootPath
	 * @param name
	 */
	public void fileDeleted(int wd, String rootPath, String name)
	{
		if (USER_BUNDLE_PATTERN.matcher(name).matches() || USER_FILE_PATTERN.matcher(name).matches())
		{
			File file = new File(rootPath + File.separator + name);
			
			BundleManager.getInstance().unloadScript(file);
		}
	}

	/**
	 * fileModified
	 * 
	 * @param wd
	 * @param rootPath
	 * @param name
	 */
	public void fileModified(int wd, String rootPath, String name)
	{
		if (USER_BUNDLE_PATTERN.matcher(name).matches() || USER_FILE_PATTERN.matcher(name).matches())
		{
			File file = new File(rootPath + File.separator + name);
			
			BundleManager.getInstance().reloadScript(file);
		}
	}

	/**
	 * fileRenamed
	 * 
	 * @param wd
	 * @param rootPath
	 * @param oldName
	 * @param newName
	 */
	public void fileRenamed(int wd, String rootPath, String oldName, String newName)
	{
		if (USER_BUNDLE_PATTERN.matcher(oldName).matches() || USER_FILE_PATTERN.matcher(oldName).matches())
		{
			File oldFile = new File(rootPath + File.separator + oldName);
			
			BundleManager.getInstance().unloadScript(oldFile);
		}
		
		if (USER_BUNDLE_PATTERN.matcher(newName).matches() || USER_FILE_PATTERN.matcher(newName).matches())
		{
			File newFile = new File(rootPath + File.separator + newName);
			
			BundleManager.getInstance().loadScript(newFile);
		}
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
			
			BundlePrecedence scope = manager.getBundlePrecedence(file);
			
			// don't process user bundles that are projects since file watcher will handle those
			if (scope != BundlePrecedence.USER)
			{
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
								manager.unloadScript(movedFrom.getLocation().toFile());
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
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent
	 * )
	 */
	public void resourceChanged(IResourceChangeEvent event)
	{
		try
		{
			event.getDelta().accept(this);
		}
		catch (CoreException e)
		{
			Activator.logError(Messages.BundleMonitor_Error_Processing_Resource_Change, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException
	{
		String fullProjectPath = delta.getFullPath().toString();

		if (BUNDLE_PATTERN.matcher(fullProjectPath).matches() || FILE_PATTERN.matcher(fullProjectPath).matches())
		{
			this.processFile(delta);
		}

		return true;
	}
}
