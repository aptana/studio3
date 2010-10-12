package com.aptana.index.core;

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

public abstract class AbstractFileIndexingParticipant implements IFileStoreIndexingParticipant
{

	protected void createTask(IFileStore store, String message, int priority, int line, int start, int end)
	{
		try
		{
			IResource resource = getResource(store);
			IMarker marker = resource.createMarker(IMarker.TASK);
			if (resource.equals(ResourcesPlugin.getWorkspace().getRoot()))
			{
				marker.setAttribute("uri", store.toURI().toString());
			}
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.PRIORITY, priority);
			marker.setAttribute(IMarker.LINE_NUMBER, line);
			marker.setAttribute(IMarker.CHAR_START, start);
			marker.setAttribute(IMarker.CHAR_END, end);
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private IResource getResource(IFileStore store)
	{
		URI uri = store.toURI();
		if (uri.getScheme().equals("file"))
		{
			File file = new File(uri);
			IFile iFile = ResourcesPlugin.getWorkspace().getRoot()
					.getFileForLocation(Path.fromOSString(file.getAbsolutePath()));
			if (iFile != null)
			{
				return iFile;
			}
		}
		return ResourcesPlugin.getWorkspace().getRoot();
	}

}
