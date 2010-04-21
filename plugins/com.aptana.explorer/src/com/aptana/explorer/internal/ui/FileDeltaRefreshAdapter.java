package com.aptana.explorer.internal.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.contentobjects.jnotify.JNotifyAdapter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.explorer.ExplorerPlugin;

class FileDeltaRefreshAdapter extends JNotifyAdapter
{
	private WorkspaceJob job;
	private Map<IResource, Integer> toRefresh = new HashMap<IResource, Integer>();

	private void refresh()
	{
		if (job != null)
			job.cancel();
		if (toRefresh.isEmpty())
			return;
		job = new WorkspaceJob(Messages.SingleProjectView_RefreshJob_title)
		{

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
			{
				Map<IResource, Integer> copy = new HashMap<IResource, Integer>(toRefresh);
				SubMonitor sub = SubMonitor.convert(monitor, copy.size());
				for (Map.Entry<IResource, Integer> entry : copy.entrySet())
				{
					if (sub.isCanceled())
						return Status.CANCEL_STATUS;
					entry.getKey().refreshLocal(entry.getValue(), sub.newChild(1));
					synchronized (toRefresh)
					{
						toRefresh.remove(entry.getKey());
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
			resource = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(
					new Path(file.getAbsolutePath()));
			depth = IResource.DEPTH_INFINITE;
		}
		addToRefreshList(resource, depth);
	}

	private void addToRefreshList(IResource resource, Integer depth)
	{
		if (resource == null)
			return;
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
						toRefresh.put(resource, depth);
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
									return;
							}
						}
					}
					toRefresh.put(resource, depth);
				}
			}
		}
		catch (Throwable e)
		{
			ExplorerPlugin.logError(e.getMessage(), e);
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
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(
				new Path(pathString).removeLastSegments(1));
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
			resource = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(
					new Path(file.getAbsolutePath()));
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