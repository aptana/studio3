package com.aptana.index.core;

import java.io.File;
import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public abstract class AbstractFileIndexingParticipant implements IFileStoreIndexingParticipant
{

	private static final String EXTERNAL_URI = "uri"; //$NON-NLS-1$

	protected void createTask(IFileStore store, String message, int priority, int line, int start, int end)
	{
		try
		{
			IResource resource = getResource(store);
			IMarker marker = resource.createMarker(IMarker.TASK);
			if (resource.equals(ResourcesPlugin.getWorkspace().getRoot()))
			{
				marker.setAttribute(EXTERNAL_URI, store.toURI().toString());
			}
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.PRIORITY, priority);
			marker.setAttribute(IMarker.LINE_NUMBER, line);
			marker.setAttribute(IMarker.CHAR_START, start);
			marker.setAttribute(IMarker.CHAR_END, end);
		}
		catch (CoreException e)
		{
			IndexPlugin.logError(e);
		}
	}

	private IResource getResource(IFileStore store)
	{
		URI uri = store.toURI();
		if (uri.getScheme().equals(EFS.SCHEME_FILE))
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

	protected void removeTasks(IFileStore store, IProgressMonitor monitor)
	{
		URI uri = store.toURI();
		String uriString = uri.toString();

		try
		{
			IResource resource = getResource(store);
			if (resource.equals(ResourcesPlugin.getWorkspace().getRoot()))
			{
				// Iterate over markers on the root and remove any with matching "uri"
				IMarker[] tasks = resource.findMarkers(IMarker.TASK, true, IResource.DEPTH_ZERO);
				for (IMarker task : tasks)
				{
					if (task == null)
					{
						continue;
					}
					try
					{
						String markerURI = (String) task.getAttribute(EXTERNAL_URI);
						if (markerURI == null)
						{
							continue;
						}
						if (markerURI.equals(uriString))
						{
							task.delete();
						}
					}
					catch (CoreException e)
					{
						IndexPlugin.logError(e);
					}
				}
			}
			else
			{
				resource.deleteMarkers(IMarker.TASK, true, IResource.DEPTH_ZERO);
			}
		}
		catch (CoreException e)
		{
			IndexPlugin.logError(e);
		}
	}

	/**
	 * addIndex
	 * 
	 * @param index
	 * @param file
	 * @param category
	 * @param word
	 */
	protected void addIndex(Index index, IFileStore file, String category, String word)
	{
		index.addEntry(category, word, file.toURI());
	}
	
	/**
	 * Returns a display string for use when indexing files
	 * @param index
	 * @param file
	 * @return
	 */
	protected String getIndexingMessage(Index index, IFileStore file)
	{
		return MessageFormat.format("Indexing {0}", index.getRelativeDocumentPath(file.toURI()).toString()); //$NON-NLS-1$
	}

}
