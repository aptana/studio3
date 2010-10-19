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
package com.aptana.filewatcher.poller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * An implementation to fallback to using a 2 second polling mechanism to scan the directories using the Java File API.
 * 
 * @author cwilliams
 */
public class PollingNotifier implements IJNotify
{

	private int id = 0;
	private Map<Integer, DirectoryWatcher> watchers = new HashMap<Integer, DirectoryWatcher>();

	public int addWatch(String path, final int mask, boolean watchSubtree, final JNotifyListener listener)
			throws JNotifyException
	{
		DirectoryWatcher watcher = new DirectoryWatcher(new File(path), watchSubtree);
		watcher.addListener(new DirectoryChangeListener()
		{

			private Map<File, Long> files = new HashMap<File, Long>();

			@Override
			public void startPoll()
			{
			}

			@Override
			public void stopPoll()
			{
			}

			@Override
			public boolean added(File file)
			{
				files.put(file, file.lastModified());
				if ((mask & IJNotify.FILE_CREATED) == 0)
					return false;
				listener.fileCreated(0, file.getParent(), file.getName());
				return true;
			}

			@Override
			public boolean removed(File file)
			{
				files.remove(file);
				if ((mask & IJNotify.FILE_DELETED) == 0)
					return false;
				listener.fileDeleted(0, file.getParent(), file.getName());
				return true;
			}

			@Override
			public boolean changed(File file)
			{
				files.put(file, file.lastModified());
				if ((mask & IJNotify.FILE_MODIFIED) == 0)
					return false;
				listener.fileModified(0, file.getParent(), file.getName());
				return true;
			}

			@Override
			public boolean isInterested(File file)
			{
				return true;
			}

			@Override
			public Long getSeenFile(File file)
			{
				Long timestamp = files.get(file);
				IResource resource = null;
				IPath location = new Path(file.getAbsolutePath());
				if (file.isDirectory())
				{
					resource = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(location);
				}
				else
				{
					resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(location);
				}
				Long resourceTimestamp = null;
				if (resource != null && resource.exists())
					resourceTimestamp = resource.getLocalTimeStamp();
				if (resourceTimestamp == null && timestamp == null)
				{
					return null;
				}
				if (resourceTimestamp != null && (timestamp == null || resourceTimestamp > timestamp))
				{
					files.put(file, resourceTimestamp);
					return resourceTimestamp;
				}
				files.put(file, timestamp);
				return timestamp;
			}
		});
		int thisId = id;
		watchers.put(id++, watcher);
		watcher.start();
		return thisId;
	}

	public boolean removeWatch(int wd) throws JNotifyException
	{
		DirectoryWatcher watcher = watchers.remove(wd);
		if (watcher  == null)
		{
			return false;
		}
		watcher.dispose();
		return true;
	}

}
