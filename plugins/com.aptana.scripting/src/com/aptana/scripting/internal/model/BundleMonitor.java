/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.internal.model;

import java.io.File;
import java.io.FilenameFilter;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.filewatcher.FileWatcher;
import com.aptana.scripting.IDebugScopes;
import com.aptana.scripting.ScriptingActivator;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.BundlePrecedence;
import com.aptana.scripting.model.LibraryCrossReference;
import com.aptana.scripting.model.Messages;

public class BundleMonitor implements IResourceChangeListener, IResourceDeltaVisitor, JNotifyListener
{
	private final class CacheFileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name)
		{
			return name.startsWith("cache") && name.endsWith(".yml"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	// @formatter:off
	// TODO: use constants from BundleManager for bundles, commands, and snippets directory names
	private static final Pattern USER_BUNDLE_PATTERN = Pattern.compile(
			".+?[/\\\\]bundle\\.rb$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final Pattern USER_LOCALIZATIONS_PATTERN = Pattern.compile(
			".+?[/\\\\]config/locales/[^/\\\\]+\\.yml$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final Pattern USER_FILE_PATTERN = Pattern.compile(
			".+?[/\\\\](?:commands|snippets|templates|samples)/[^/\\\\]+\\.rb$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final Pattern BUNDLE_PATTERN = Pattern.compile("/.+?/bundle\\.rb$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final Pattern FILE_PATTERN = Pattern.compile(
			"/.+?/(?:commands|snippets|templates|samples)/[^/]+\\.rb$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	private static final Pattern BUNDLE_PATTERN_DEPRECATED = Pattern.compile(
			"/.+?/bundles/.+?/bundle\\.rb$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final Pattern FILE_PATTERN_DEPRECATED = Pattern.compile(
			"/.+?/bundles/.+?/(?:commands|snippets|templates|samples)/[^/]+\\.rb$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	// @formatter:on

	private boolean _registered;
	private int _watchId;
	private BundleManager bm;

	/**
	 * BundleMonitor
	 */
	public BundleMonitor(BundleManager bm)
	{
		this.bm = bm;
		this._watchId = -1;
	}

	/**
	 * beginMonitoring
	 * 
	 * @throws JNotifyException
	 */
	public synchronized void beginMonitoring() throws JNotifyException
	{
		if (!this._registered)
		{
			// begin monitoring resource changes
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
					IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);

			this._registered = true;

			// Make sure the user bundles directory exists
			String userBundlesPath = getBundleManager().getUserBundlesPath();
			if (StringUtil.isEmpty(userBundlesPath))
			{
				IdeLog.logError(ScriptingActivator.getDefault(),
						"Unable to register listener for user bundles path. Was unable to get the user bundle path"); //$NON-NLS-1$
				return;
			}

			// Make sure the user bundles directory exists
			File bundleDirectory = new File(userBundlesPath);
			boolean directoryExists = true;

			if (!bundleDirectory.exists())
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
					this._watchId = FileWatcher.addWatch(userBundlesPath, IJNotify.FILE_ANY, true, this);

					if (IdeLog.isTraceEnabled(ScriptingActivator.getDefault(),
							IDebugScopes.SHOW_BUNDLE_MONITOR_FILE_EVENTS))
					{
						this.showFileEvent("Begin file system monitoring"); //$NON-NLS-1$
					}
				}
				catch (JNotifyException e)
				{
					IdeLog.logError(ScriptingActivator.getDefault(),
							Messages.BundleMonitor_ERROR_REGISTERING_FILE_WATCHER, e);
				}
			}
			else
			{
				String message = MessageFormat.format(Messages.BundleMonitor_INVALID_WATCHER_PATH, userBundlesPath);

				IdeLog.logError(ScriptingActivator.getDefault(), message);
			}
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
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);

			if (this._watchId != -1)
			{
				FileWatcher.removeWatch(this._watchId);
				this._watchId = -1;

				if (IdeLog
						.isTraceEnabled(ScriptingActivator.getDefault(), IDebugScopes.SHOW_BUNDLE_MONITOR_FILE_EVENTS))
				{
					this.showFileEvent("End file system monitoring"); //$NON-NLS-1$
				}
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
	synchronized public void fileCreated(int wd, String rootPath, String name)
	{
		fileCreatedHelper(rootPath, name);

		// used by unit tests
		this.notifyAll();
	}

	/**
	 * fileCreatedHelper
	 * 
	 * @param rootPath
	 * @param name
	 */
	private void fileCreatedHelper(String rootPath, String name)
	{
		if (isUserBundleFile(rootPath, name))
		{
			if (IdeLog.isTraceEnabled(ScriptingActivator.getDefault(), IDebugScopes.SHOW_BUNDLE_MONITOR_FILE_EVENTS))
			{
				this.showFileEvent(MessageFormat.format("File created: {0},{1}", rootPath, name)); //$NON-NLS-1$
			}

			BundleManager manager = getBundleManager();
			File file = new File(rootPath, name);

			if (USER_BUNDLE_PATTERN.matcher(name).matches())
			{
				// load the entire bundle now that we have a bundle.rb file
				manager.loadBundle(file.getParentFile());
			}
			else
			{
				// load the script. isUserBundleFile only returns true if this
				// is part of an existing bundle; otherwise, it will get loaded
				// later once its bundle.rb file has been created
				manager.loadScript(file);
			}
		}
		else
		{
			this.showFileEvent(MessageFormat.format("Skipped file created: {0},{1}", rootPath, name)); //$NON-NLS-1$
		}
	}

	/**
	 * fileDeleted
	 * 
	 * @param wd
	 * @param rootPath
	 * @param name
	 */
	synchronized public void fileDeleted(int wd, String rootPath, String name)
	{
		fileDeletedHelper(rootPath, name);

		// used by unit tests
		this.notifyAll();
	}

	/**
	 * fileDeletedHelper
	 * 
	 * @param rootPath
	 * @param name
	 */
	private void fileDeletedHelper(String rootPath, String name)
	{
		if (isUserBundleFile(rootPath, name))
		{
			if (IdeLog.isTraceEnabled(ScriptingActivator.getDefault(), IDebugScopes.SHOW_BUNDLE_MONITOR_FILE_EVENTS))
			{
				this.showFileEvent(MessageFormat.format("File deleted: {0},{1}", rootPath, name)); //$NON-NLS-1$
			}
			BundleManager manager = getBundleManager();
			File file = new File(rootPath, name);

			if (USER_BUNDLE_PATTERN.matcher(name).matches())
			{
				// unload entire bundle now that we don't have bundle.rb file
				// anymore
				manager.unloadBundle(file.getParentFile());
			}
			else
			{
				manager.unloadScript(file);
			}
		}
		else
		{
			if (IdeLog.isTraceEnabled(ScriptingActivator.getDefault(), IDebugScopes.SHOW_BUNDLE_MONITOR_FILE_EVENTS))
			{
				this.showFileEvent(MessageFormat.format("Skipped file deleted: {0},{1}", rootPath, name)); //$NON-NLS-1$
			}
			reloadDependentScripts(new File(rootPath, name));
		}
	}

	protected BundleManager getBundleManager()
	{
		return bm;
	}

	/**
	 * fileModified
	 * 
	 * @param wd
	 * @param rootPath
	 * @param name
	 */
	synchronized public void fileModified(int wd, String rootPath, String name)
	{
		if (isUserBundleFile(rootPath, name))
		{
			if (IdeLog.isTraceEnabled(ScriptingActivator.getDefault(), IDebugScopes.SHOW_BUNDLE_MONITOR_FILE_EVENTS))
			{
				this.showFileEvent(MessageFormat.format("File modified: {0},{1}", rootPath, name)); //$NON-NLS-1$
			}
			File file = new File(rootPath, name);

			getBundleManager().reloadScript(file);
		}
		else if (isUserLocalizationFile(rootPath, name))
		{
			if (IdeLog.isTraceEnabled(ScriptingActivator.getDefault(), IDebugScopes.SHOW_BUNDLE_MONITOR_FILE_EVENTS))
			{
				this.showFileEvent(MessageFormat.format("Localization file modified: {0},{1}", rootPath, name)); //$NON-NLS-1$
			}
			File file = new File(rootPath, name);
			// We need to reload localizations! Wipe the cache files and then reload the bundle
			BundleManager manager = getBundleManager();
			File bundleDirectory = manager.getBundleDirectory(file);
			for (File localeFile : bundleDirectory.listFiles(new CacheFileFilter()))
			{
				localeFile.delete();
			}
			manager.unloadBundle(bundleDirectory);
			manager.loadBundle(bundleDirectory);
		}
		else
		{
			if (IdeLog.isTraceEnabled(ScriptingActivator.getDefault(), IDebugScopes.SHOW_BUNDLE_MONITOR_FILE_EVENTS))
			{
				this.showFileEvent(MessageFormat.format("Skipped file modified: {0},{1}", rootPath, name)); //$NON-NLS-1$
			}
			reloadDependentScripts(new File(rootPath, name));
		}

		// used by unit tests
		this.notifyAll();
	}

	private boolean isUserLocalizationFile(String rootPath, String name)
	{
		if (USER_LOCALIZATIONS_PATTERN.matcher(name).matches())
		{
			// only return true if the script is part of an existing bundle.
			File script = new File(rootPath, name);
			return isScriptInExistingBundle(script);
		}

		return false;
	}

	/**
	 * fileRenamed
	 * 
	 * @param wd
	 * @param rootPath
	 * @param oldName
	 * @param newName
	 */
	synchronized public void fileRenamed(int wd, String rootPath, String oldName, String newName)
	{
		this.showFileEvent(MessageFormat.format("File renamed: {0},{1}=>{2}", rootPath, oldName, newName)); //$NON-NLS-1$

		this.fileDeletedHelper(rootPath, oldName);
		this.fileCreatedHelper(rootPath, newName);

		// used by unit tests
		this.notifyAll();
	}

	/**
	 * isProjectBundleFile
	 * 
	 * @param delta
	 * @return
	 */
	private boolean isProjectBundleFile(IResourceDelta delta)
	{
		String fullProjectPath = delta.getFullPath().toString();
		boolean result = false;

		if (BUNDLE_PATTERN_DEPRECATED.matcher(fullProjectPath).matches()
				|| BUNDLE_PATTERN.matcher(fullProjectPath).matches())
		{
			// always return true for bundle.rb files
			result = true;
		}
		else if (FILE_PATTERN_DEPRECATED.matcher(fullProjectPath).matches()
				|| FILE_PATTERN.matcher(fullProjectPath).matches())
		{
			// only return true if the script is part of an existing bundle.
			IResource resource = delta.getResource();

			if (resource != null)
			{
				IPath location = resource.getLocation();

				if (location != null)
				{
					File script = delta.getResource().getLocation().toFile();

					result = isScriptInExistingBundle(script);
				}
			}
		}

		return result;
	}

	private boolean isProjectBundleFile(IFile file)
	{
		String fullProjectPath = file.getFullPath().toString();

		if (BUNDLE_PATTERN_DEPRECATED.matcher(fullProjectPath).matches()
				|| BUNDLE_PATTERN.matcher(fullProjectPath).matches())
		{
			// always return true for bundle.rb files
			return true;
		}
		if (FILE_PATTERN_DEPRECATED.matcher(fullProjectPath).matches()
				|| FILE_PATTERN.matcher(fullProjectPath).matches())
		{
			// only return true if the script is part of an existing bundle.
			return isScriptInExistingBundle(file.getLocation().toFile());
		}

		return false;
	}

	/**
	 * isScriptInExistingBundle
	 * 
	 * @param script
	 * @return
	 */
	private boolean isScriptInExistingBundle(File script)
	{
		BundleManager manager = getBundleManager();
		File bundleDirectory = manager.getBundleDirectory(script);

		return manager.hasBundleAtPath(bundleDirectory);
	}

	/**
	 * isUserbundlesFile
	 * 
	 * @param rootPath
	 * @param name
	 * @return
	 */
	private boolean isUserBundleFile(String rootPath, String name)
	{
		boolean result = false;

		if (USER_BUNDLE_PATTERN.matcher(name).matches())
		{
			// always return true for bundle.rb files
			result = true;
		}
		else if (USER_FILE_PATTERN.matcher(name).matches())
		{
			// only return true if the script is part of an existing bundle.
			File script = new File(rootPath, name);

			result = isScriptInExistingBundle(script);
		}

		return result;
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
			BundleManager manager = getBundleManager();
			File file = resource.getLocation().toFile();
			String fullProjectPath = delta.getFullPath().toString();

			BundlePrecedence scope = manager.getBundlePrecedence(file);

			// don't process user bundles that are projects since file watcher will handle those
			if (scope != BundlePrecedence.USER)
			{
				switch (delta.getKind())
				{
					case IResourceDelta.ADDED:
						this.showResourceEvent("Added: " + file); //$NON-NLS-1$

						if (BUNDLE_PATTERN_DEPRECATED.matcher(fullProjectPath).matches()
								|| BUNDLE_PATTERN.matcher(fullProjectPath).matches())
						{
							manager.loadBundle(file.getParentFile());
						}
						else
						{
							manager.loadScript(file);
						}
						break;

					case IResourceDelta.REMOVED:
						this.showResourceEvent("Removed: " + file); //$NON-NLS-1$

						if (BUNDLE_PATTERN_DEPRECATED.matcher(fullProjectPath).matches()
								|| BUNDLE_PATTERN.matcher(fullProjectPath).matches())
						{
							// NOTE: we have to both unload all scripts associated with this bundle
							// and the bundle file itself. Technically, the bundle file doesn't
							// exist any more so it won't get unloaded
							manager.unloadBundle(file.getParentFile());
						}

						manager.unloadScript(file);
						break;

					case IResourceDelta.CHANGED:
						if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0)
						{
							IPath movedFromPath = delta.getMovedFromPath();
							IResource movedFrom = ResourcesPlugin.getWorkspace().getRoot()
									.getFileForLocation(movedFromPath);

							if (movedFrom != null && movedFrom instanceof IFile)
							{
								this.showResourceEvent(MessageFormat.format("Added: {0}=>{1}", movedFrom.getLocation() //$NON-NLS-1$
										.toFile(), file));

								manager.unloadScript(movedFrom.getLocation().toFile());
								manager.loadScript(file);
							}
						}
						else if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0)
						{
							IPath movedToPath = delta.getMovedToPath();
							IResource movedTo = ResourcesPlugin.getWorkspace().getRoot()
									.getFileForLocation(movedToPath);

							if (movedTo != null && movedTo instanceof IFile)
							{
								this.showResourceEvent(MessageFormat.format("Added: {0}=>{1}", file, movedTo //$NON-NLS-1$
										.getLocation().toFile()));

								manager.unloadScript(file);
								manager.loadScript(movedTo.getLocation().toFile());
							}
						}
						else if ((delta.getFlags() & IResourceDelta.REPLACED) != 0)
						{
							this.showResourceEvent("Reload: " + file); //$NON-NLS-1$

							manager.reloadScript(file);
						}
						else if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
						{
							this.showResourceEvent("Reload: " + file); //$NON-NLS-1$

							manager.reloadScript(file);
						}
						break;
				}
			}
		}
	}

	/**
	 * realoadDependentScripts
	 * 
	 * @param file
	 */
	private void reloadDependentScripts(File file)
	{
		BundleManager manager = getBundleManager();
		String fullPath = file.getAbsolutePath();
		LibraryCrossReference xref = LibraryCrossReference.getInstance();

		if (xref.hasLibrary(fullPath))
		{
			for (String script : xref.getPathsFromLibrary(fullPath))
			{
				manager.reloadScript(new File(script));
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
		if (event.getType() == IResourceChangeEvent.PRE_DELETE)
		{
			handleProjectDeleteEvent((IProject) event.getResource());
		}
		else
		{
			try
			{
				event.getDelta().accept(this);
			}
			catch (CoreException e)
			{
				IdeLog.logError(ScriptingActivator.getDefault(),
						Messages.BundleMonitor_Error_Processing_Resource_Change, e);
			}
		}
	}

	private void handleProjectDeleteEvent(IResource resource)
	{
		BundleManager manager = getBundleManager();
		if (resource instanceof IFile)
		{
			IFile file = (IFile) resource;
			if (isProjectBundleFile(file))
			{
				File localFile = file.getLocation().toFile();
				BundlePrecedence scope = manager.getBundlePrecedence(localFile);
				if (scope != BundlePrecedence.USER)
				{
					String fullProjectPath = file.getFullPath().toString();
					if (BUNDLE_PATTERN_DEPRECATED.matcher(fullProjectPath).matches()
							|| BUNDLE_PATTERN.matcher(fullProjectPath).matches())
					{
						// NOTE: we have to both unload all scripts associated with this bundle
						// and the bundle file itself.
						manager.unloadBundle(localFile.getParentFile());
					}

					manager.unloadScript(localFile);
				}
			}
		}
		else if (resource instanceof IContainer)
		{
			try
			{
				IResource[] children = ((IContainer) resource).members();
				for (IResource child : children)
				{
					handleProjectDeleteEvent(child);
				}
			}
			catch (CoreException e)
			{
				IdeLog.logWarning(ScriptingActivator.getDefault(), e);
			}
		}
	}

	/**
	 * showFileEvent
	 * 
	 * @param message
	 */
	protected void showFileEvent(String message)
	{
		IdeLog.logTrace(ScriptingActivator.getDefault(), message, IDebugScopes.SHOW_BUNDLE_MONITOR_FILE_EVENTS);
	}

	/**
	 * showResourceEvent
	 * 
	 * @param message
	 */
	protected void showResourceEvent(String message)
	{
		IdeLog.logTrace(ScriptingActivator.getDefault(), message, IDebugScopes.SHOW_BUNDLE_MONITOR_RESOURCE_EVENTS);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException
	{
		// process project bundle files, but ignore user bundles since file watcher will take care of those
		if (isProjectBundleFile(delta))
		{
			this.processFile(delta);
		}
		else
		{
			if (delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.CONTENT) != 0)
			{
				IResource resource = delta.getResource();

				if (resource != null)
				{
					IPath location = resource.getLocation();

					if (location != null)
					{
						File file = location.toFile();

						if (file != null)
						{
							this.reloadDependentScripts(file);
						}
					}
				}
			}
		}

		return true;
	}
}
