/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import com.aptana.core.logging.IdeLog;
import com.aptana.filewatcher.FileWatcher;

/**
 * A class meant to simplify adding and removing file change listeners solely for the scripting API.
 * 
 * @author cwilliams
 */
public class FileChangeNotifier
{

	/**
	 * The interface that a listener conforms to. In Ruby this listener is just a block that JRuby magically proxies to
	 * when this method is called.
	 * 
	 * @author cwilliams
	 */
	public interface IFileChangeListener
	{
		public void fileModified(FileModificationEvent e);
	}

	/**
	 * The event class returned on the callback to the listener.
	 * 
	 * @author cwilliams
	 */
	public static class FileModificationEvent
	{
		public static final int CREATED = 0;
		public static final int DELETED = 1;
		public static final int MODIFIED = 2;
		public static final int RENAMED = 3;

		public String oldName;
		public String name;
		public int type;
	}

	private static Map<IFileChangeListener, Integer> listeners = new HashMap<IFileChangeListener, Integer>();

	/**
	 * Adds a listener for a given filepath.
	 * 
	 * @param filepath
	 * @param watchSubtree
	 * @param listener
	 * @return
	 */
	public static boolean addListener(String filepath, boolean watchSubtree, final IFileChangeListener listener)
	{
		// If it's a single file we need to watch parent and filter in the callback methods
		final File file = new File(filepath);
		if (!file.exists())
			return false;
		final boolean filterToSingleFile = file.isFile();
		if (filterToSingleFile)
		{
			filepath = file.getParentFile().getAbsolutePath();
		}
		try
		{
			int watchId = FileWatcher.addWatch(filepath, IJNotify.FILE_ANY, watchSubtree, new JNotifyListener()
			{

				public void fileRenamed(int wd, String rootPath, String oldName, String newName)
				{
					if (oldName != null && newName != null && oldName.equals(newName))
					{
						fileModified(wd, rootPath, newName);
						return;
					}
					if (filterToSingleFile && doesntMatch(rootPath, oldName))
						return;
					// TODO When renamed, we need to update our filename we check against
					FileModificationEvent e = new FileModificationEvent();
					e.type = FileModificationEvent.RENAMED;
					e.oldName = rootPath + File.separator + oldName;
					e.name = rootPath + File.separator + newName;
					listener.fileModified(e);
				}

				public void fileModified(int wd, String rootPath, String name)
				{
					if (filterToSingleFile && doesntMatch(rootPath, name))
						return;

					FileModificationEvent e = new FileModificationEvent();
					e.type = FileModificationEvent.MODIFIED;
					e.name = rootPath + File.separator + name;
					e.oldName = e.name;
					listener.fileModified(e);
				}

				private boolean doesntMatch(String rootPath, String name)
				{
					String singleFilePath = file.getAbsolutePath();
					String fullPath = rootPath + File.separator + name;
					return (!fullPath.equals(singleFilePath));
				}

				public void fileDeleted(int wd, String rootPath, String name)
				{
					if (filterToSingleFile && doesntMatch(rootPath, name))
						return;
					FileModificationEvent e = new FileModificationEvent();
					e.type = FileModificationEvent.DELETED;
					e.name = rootPath + File.separator + name;
					e.oldName = e.name;
					listener.fileModified(e);
				}

				public void fileCreated(int wd, String rootPath, String name)
				{
					if (filterToSingleFile && doesntMatch(rootPath, name))
						return;
					FileModificationEvent e = new FileModificationEvent();
					e.type = FileModificationEvent.CREATED;
					e.name = rootPath + File.separator + name;
					listener.fileModified(e);
				}
			});
			listeners.put(listener, watchId);
			return true;
		}
		catch (JNotifyException e)
		{
			IdeLog.logError(ScriptingActivator.getDefault(), "Error adding file change listener", e); //$NON-NLS-1$
			return false;
		}

	}

	public static boolean removeListener(IFileChangeListener listener)
	{
		if (listener == null || !listeners.containsKey(listener))
			return false;
		int watchId = listeners.remove(listener);
		try
		{
			FileWatcher.removeWatch(watchId);
			return true;
		}
		catch (JNotifyException e)
		{
			IdeLog.logError(ScriptingActivator.getDefault(), "Error removing file change listener", e); //$NON-NLS-1$
			return false;
		}
	}

}
