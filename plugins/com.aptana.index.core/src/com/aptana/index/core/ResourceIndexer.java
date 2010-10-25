package com.aptana.index.core;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.runtime.CoreException;

public class ResourceIndexer implements IResourceChangeListener
{

	private static class ResourceCollector implements IResourceDeltaVisitor
	{
		private List<IResourceDelta> resourceDeltas = new LinkedList<IResourceDelta>();

		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if (resource instanceof IProject)
			{
				if (delta.getKind() == IResourceDelta.ADDED
						|| (delta.getKind() == IResourceDelta.CHANGED && ((delta.getFlags() & IResourceDelta.OPEN) != 0)))
				{
					resourceDeltas.add(delta);
					return false;
				}
			}
			else if (resource instanceof IFile)
			{
				resourceDeltas.add(delta);
			}
			return true;
		}

		void process()
		{
			if (resourceDeltas.size() == 0)
			{
				return;
			}

			Set<IFile> filesToIndex = new LinkedHashSet<IFile>();
			Set<IFile> filesToRemoveFromIndex = new LinkedHashSet<IFile>();

			for (IResourceDelta delta : resourceDeltas)
			{
				IResource resource = delta.getResource();
				if (resource instanceof IProject)
				{
					IProject project = (IProject) resource;
					if (delta.getKind() == IResourceDelta.ADDED)
					{
						indexProject(project);
					}
					else if (delta.getKind() == IResourceDelta.CHANGED
							&& ((delta.getFlags() & IResourceDelta.OPEN) != 0))
					{
						if (resource.isAccessible())
						{
							indexProject(project);
						}
					}
				}
				if (resource instanceof IFile)
				{
					if (delta.getKind() == IResourceDelta.ADDED
							|| (delta.getKind() == IResourceDelta.CHANGED && ((delta.getFlags() & (IResourceDelta.CONTENT | IResourceDelta.ENCODING)) != 0)))
					{
						filesToIndex.add((IFile) resource);
					}
					else if (delta.getKind() == IResourceDelta.REMOVED)
					{
						filesToRemoveFromIndex.add((IFile) resource);
					}
				}
			}

			// Now let the indexing begin
			// Process files to index
			if (filesToIndex.size() > 0)
			{
				Map<IProject, Set<IFile>> projectToFilesMap = getProjectToFilesMap(filesToIndex);
				for (Map.Entry<IProject, Set<IFile>> projectToFilesMapEntry : projectToFilesMap.entrySet())
				{
					indexFilesOfProject(projectToFilesMapEntry.getKey(), projectToFilesMapEntry.getValue());
				}
			}

			if (filesToRemoveFromIndex.size() > 0)
			{
				Map<IProject, Set<IFile>> projectToFilesMap = getProjectToFilesMap(filesToRemoveFromIndex);
				for (Map.Entry<IProject, Set<IFile>> projectToFilesMapEntry : projectToFilesMap.entrySet())
				{
					removeIndexOfFilesOfProject(projectToFilesMapEntry.getKey(), projectToFilesMapEntry.getValue());
				}
			}
		}

		private Map<IProject, Set<IFile>> getProjectToFilesMap(Set<IFile> files)
		{
			Map<IProject, Set<IFile>> projectToFilesMap = new LinkedHashMap<IProject, Set<IFile>>();
			for (IFile file : files)
			{
				IProject project = file.getProject();
				Set<IFile> filesOfProject = projectToFilesMap.get(project);
				if (filesOfProject == null)
				{
					filesOfProject = new LinkedHashSet<IFile>();
					projectToFilesMap.put(project, filesOfProject);
				}
				filesOfProject.add(file);
			}
			return projectToFilesMap;
		}
	}

	private static void indexProject(IProject project)
	{
		new IndexProjectJob(project).schedule();
	}

	private static void indexFilesOfProject(IProject project, Set<IFile> files)
	{
		new IndexFilesOfProjectJob(project, files).schedule();
	}

	private static void removeIndexOfFilesOfProject(IProject project, Set<IFile> files)
	{
		new RemoveIndexOfFilesOfProjectJob(project, files).schedule();
	}

	private static void removeIndexOfProject(IProject project)
	{
		if (project == null)
		{
			return;
		}
		new RemoveIndexOfProjectJob(project).schedule();
	}

	// HACK
	ThreadLocal<ISavedState> processIResourceChangeEventPOST_BUILD = new ThreadLocal<ISavedState>();

	public ResourceIndexer()
	{
	}

	public void resourceChanged(IResourceChangeEvent event)
	{
		switch (event.getType())
		{
			case IResourceChangeEvent.PRE_DELETE:
				removeIndexOfProject((IProject) event.getResource());
				break;
			case IResourceChangeEvent.POST_BUILD:
				ISavedState savedState = processIResourceChangeEventPOST_BUILD.get();
				if (savedState == null)
				{
					return;
				}
				// intentional fall-through!!!!!
			case IResourceChangeEvent.POST_CHANGE:
				IResourceDelta delta = event.getDelta();
				if (delta != null)
				{
					ResourceCollector resourceCollector = new ResourceCollector();
					try
					{
						delta.accept(resourceCollector);
						resourceCollector.process();
					}
					catch (CoreException e)
					{
						IndexActivator.logError(e);
					}
				}
				break;
		}
	}
}
