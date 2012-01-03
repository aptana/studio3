/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.resources;

import java.io.File;

import net.contentobjects.jnotify.JNotifyAdapter;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import com.aptana.core.util.StringUtil;

public class FileDeltaRefreshAdapter extends JNotifyAdapter
{

	private RefreshThread fThread;

	public FileDeltaRefreshAdapter(RefreshThread thread)
	{
		this.fThread = thread;
	}

	@Override
	public void fileCreated(int wd, String rootPath, String name)
	{
		File file = new File(rootPath, name);
		IResource resource = null;
		Integer depth = IResource.DEPTH_ZERO;
		if (file.isFile())
		{
			resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(file.getAbsolutePath()));
		}
		else if (file.isDirectory())
		{
			resource = ResourcesPlugin.getWorkspace().getRoot()
					.getContainerForLocation(new Path(file.getAbsolutePath()));
			depth = IResource.DEPTH_INFINITE;
		}
		addToRefreshList(resource, depth);
	}

	private void addToRefreshList(IResource resource, Integer depth)
	{
		if (resource == null)
		{
			return;
		}
		// Don't refresh stuff we don't want/can't access/can't see (i.e. .git and it's sub-tree)
		if (resource.isPhantom() || resource.isHidden() || resource.isTeamPrivateMember(IResource.CHECK_ANCESTORS))
		{
			return;
		}
		fThread.refresh(resource, depth);
	}

	@Override
	public void fileDeleted(int wd, String rootPath, String name)
	{
		String pathString = rootPath + ((name.length() > 0) ? Path.SEPARATOR + name : StringUtil.EMPTY);
		IResource resource = ResourcesPlugin.getWorkspace().getRoot()
				.getContainerForLocation(new Path(pathString).removeLastSegments(1));
		addToRefreshList(resource, IResource.DEPTH_ONE);
	}

	@Override
	public void fileModified(int wd, String rootPath, String name)
	{
		File file = new File(rootPath, name);
		IResource resource = null;
		Integer depth = IResource.DEPTH_ZERO;
		if (file.isFile())
		{
			resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(file.getAbsolutePath()));
		}
		else if (file.isDirectory())
		{
			resource = ResourcesPlugin.getWorkspace().getRoot()
					.getContainerForLocation(new Path(file.getAbsolutePath()));
			depth = IResource.DEPTH_INFINITE;
		}
		addToRefreshList(resource, depth);
	}

	@Override
	public void fileRenamed(int wd, String rootPath, String oldName, String newName)
	{
		// If the names are the same, call to fileModified
		if (oldName != null && newName != null && oldName.equals(newName))
		{
			fileModified(wd, rootPath, oldName);
		}
		else
		{
			if (oldName != null)
			{
				fileDeleted(wd, rootPath, oldName);
			}
			if (newName != null)
			{
				fileCreated(wd, rootPath, newName);
			}
		}
	}
}