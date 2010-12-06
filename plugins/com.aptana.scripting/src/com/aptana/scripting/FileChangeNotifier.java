/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

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
			ScriptingActivator.logError("Error adding file change listener", e); //$NON-NLS-1$
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
			ScriptingActivator.logError("Error removing file change listener", e); //$NON-NLS-1$
			return false;
		}
	}

}
