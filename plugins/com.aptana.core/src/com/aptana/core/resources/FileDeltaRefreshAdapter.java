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
package com.aptana.core.resources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.contentobjects.jnotify.JNotifyAdapter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.CorePlugin;

public class FileDeltaRefreshAdapter extends JNotifyAdapter
{
	private WorkspaceJob job;
	private Map<IResource, Integer> toRefresh = new HashMap<IResource, Integer>();

	private void refresh()
	{
		if (job != null)
		{
			job.cancel();
		}
		if (toRefresh.isEmpty())
		{
			return;
		}
		job = new WorkspaceJob("Refresh...") //$NON-NLS-1$
		{

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
			{
				Map<IResource, Integer> copy;
				synchronized (toRefresh)
				{
					copy = new HashMap<IResource, Integer>(toRefresh);
				}
				SubMonitor sub = SubMonitor.convert(monitor, copy.size());
				for (Map.Entry<IResource, Integer> entry : copy.entrySet())
				{
					if (sub.isCanceled())
					{
						return Status.CANCEL_STATUS;
					}
					IResource resource = entry.getKey();
					try
					{
						if (resource.getType() == IResource.PROJECT)
						{
							// Check to see if this project exists in the new branch! If not, auto-close the project, or
							// just not refresh it?
							IPath path = resource.getLocation();
							if (path == null || !path.toFile().exists())
							{
								// Close the project, this actually causes the .project file to get generated, though!
								try
								{
									if (resource.getProject().exists())
									{
										resource.getProject().close(sub.newChild(100));
									}
								}
								catch (CoreException e)
								{
									if (e.getStatus().getSeverity() > IStatus.WARNING)
									{
										throw e;
									}
								}
								if (path != null)
								{
									File projectFile = path.toFile();
									if (projectFile != null)
									{
										File dotProject = new File(projectFile,
												IProjectDescription.DESCRIPTION_FILE_NAME);
										if (dotProject.delete())
										{
											projectFile.delete();
										}
									}
								}
								continue;
							}
						}
						resource.refreshLocal(entry.getValue(), sub.newChild(1));
					}
					finally
					{
						synchronized (toRefresh)
						{
							toRefresh.remove(resource);
						}
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule(200); // give a little delay so we can have a chance to cancel and batch together refreshes!
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
		try
		{
			synchronized (toRefresh)
			{
				if (toRefresh.containsKey(resource))
				{
					Integer oldDepth = toRefresh.get(resource);
					if (oldDepth < depth)
					{
						toRefresh.put(resource, depth);
					}
				}
				else
				{
					for (IResource refreshing : toRefresh.keySet())
					{
						if (refreshing instanceof IContainer)
						{
							IContainer container = (IContainer) refreshing;
							if (resource.getLocation() != null
									&& container.getLocation().isPrefixOf(resource.getLocation()))
							{
								// We already have an ancestor in the map. If it's refreshing infinitely don't add this
								// resource
								if (toRefresh.get(container) == IResource.DEPTH_INFINITE)
								{
									return;
								}
							}
						}
					}
					toRefresh.put(resource, depth);
				}
			}
		}
		catch (Throwable e)
		{
			CorePlugin.logError(e.getMessage(), e);
		}
		finally
		{
			refresh();
		}
	}

	@Override
	public void fileDeleted(int wd, String rootPath, String name)
	{
		String pathString = rootPath + (name.length() > 0 ? Path.SEPARATOR + name : ""); //$NON-NLS-1$
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
			fileModified(wd, rootPath, oldName);
		else
		{
			fileDeleted(wd, rootPath, oldName);
			fileCreated(wd, rootPath, newName);
		}
	}
}