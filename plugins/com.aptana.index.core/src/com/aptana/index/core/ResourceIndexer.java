package com.aptana.index.core;

import java.io.IOException;
import java.util.ArrayList;
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
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

public class ResourceIndexer implements IResourceChangeListener
{
	private static final String FILE_INDEXING_PARTICIPANTS_ID = "fileIndexingParticipants"; //$NON-NLS-1$
	private static final String TAG_FILE_INDEXING_PARTICIPANT = "fileIndexingParticipant"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static class IndexProjectJob extends WorkspaceJob
	{

		private final IProject project;

		public IndexProjectJob(IProject project)
		{
			super(project.getName());
			this.project = project;
		}

		@Override
		public boolean belongsTo(Object family)
		{
			return project.getName().equals(family);
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
		{
			// Ask ProjectIndexingParticipants to handle project indexing
			return Status.OK_STATUS;
		}

	}

	private static class RemoveIndexOfProjectJob extends WorkspaceJob
	{

		private final IProject project;

		public RemoveIndexOfProjectJob(IProject project)
		{
			super(project.getName());
			this.project = project;
		}

		@Override
		public boolean belongsTo(Object family)
		{
			return project.getName().equals(family);
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
		{
			if (monitor.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			IndexManager.getInstance().removeIndex(project.getFullPath().toPortableString());

			// Remove any pending jobs in the family
			IJobManager jobManager = WorkspaceJob.getJobManager();
			jobManager.cancel(project.getName());

			return Status.OK_STATUS;
		}

	}

	private static class IndexFilesOfProjectJob extends WorkspaceJob
	{

		private final IProject project;
		private final Set<IFile> files;

		public IndexFilesOfProjectJob(IProject project, Set<IFile> files)
		{
			super(project.getName());
			this.project = project;
			this.files = files;
		}

		@Override
		public boolean belongsTo(Object family)
		{
			return project.getName().equals(family);
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
		{
			if (monitor.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}

			if (!project.isAccessible())
			{
				return Status.CANCEL_STATUS;
			}

			Index index = IndexManager.getInstance().getIndex(project.getFullPath().toPortableString());
			try
			{
				// First cleanup indices for files
				for (IFile file : files)
				{
					if (monitor.isCanceled())
					{
						return Status.CANCEL_STATUS;
					}
					index.remove(file.getProjectRelativePath().toPortableString());
				}

				for (IFileIndexingParticipant fileIndexingParticipant : getFileIndexingParticipants())
				{
					if (monitor.isCanceled())
					{
						return Status.CANCEL_STATUS;
					}
					fileIndexingParticipant.index(files, index, monitor);
				}

			}
			finally
			{
				try
				{
					index.save();
				}
				catch (IOException e)
				{
					IndexActivator.logError("An error occurred while saving an index", e);
				}
			}
			return Status.OK_STATUS;
		}

	}

	private static class RemoveIndexOfFilesOfProjectJob extends WorkspaceJob
	{

		private final IProject project;
		private final Set<IFile> files;

		public RemoveIndexOfFilesOfProjectJob(IProject project, Set<IFile> files)
		{
			super(project.getName());
			this.project = project;
			this.files = files;
		}

		@Override
		public boolean belongsTo(Object family)
		{
			return project.getName().equals(family);
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
		{
			if (monitor.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}

			if (!project.isAccessible())
			{
				return Status.CANCEL_STATUS;
			}

			Index index = IndexManager.getInstance().getIndex(project.getFullPath().toPortableString());
			try
			{
				// Cleanup indices for files
				for (IFile file : files)
				{
					if (monitor.isCanceled())
					{
						return Status.CANCEL_STATUS;
					}
					index.remove(file.getProjectRelativePath().toPortableString());
				}
			}
			finally
			{
				try
				{
					index.save();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return Status.OK_STATUS;
		}

	}

	private static class ResourceCollector implements IResourceDeltaVisitor
	{
		private List<IResourceDelta> resourceDeltas = new LinkedList<IResourceDelta>();

		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if (resource instanceof IProject)
			{
				if (delta.getKind() == IResourceDelta.REMOVED)
				{
					resourceDeltas.add(delta);
					// We will be deleting the index for the whole project
					// No need to traverse the delta
					return false;
				}
				if (delta.getKind() == IResourceDelta.ADDED ||
						(delta.getKind() == IResourceDelta.CHANGED && ((delta.getFlags() & IResourceDelta.OPEN) != 0)))
				{
					resourceDeltas.add(delta);
					// If the project is now closed we
					// will be deleting the index for
					// the whole project. No need to traverse
					// the delta.
					return resource.isAccessible();
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
					else if (delta.getKind() == IResourceDelta.REMOVED)
					{
						removeIndexOfProject(project);
					}
					else if (delta.getKind() == IResourceDelta.CHANGED
							&& ((delta.getFlags() & IResourceDelta.OPEN) != 0))
					{
						if (resource.isAccessible())
						{
							indexProject(project);
						}
						else
						{
							removeIndexOfProject(project);
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

		private void indexProject(IProject project)
		{
			Job job = new IndexProjectJob(project);
			job.setRule(IndexManager.MUTEX_RULE);
			job.setPriority(Job.BUILD);
			job.schedule();
		}

		private void removeIndexOfProject(IProject project)
		{
			Job job = new RemoveIndexOfProjectJob(project);
			job.setRule(IndexManager.MUTEX_RULE);
			job.setPriority(Job.BUILD);
			job.schedule();
		}

		private void indexFilesOfProject(IProject project, Set<IFile> files)
		{
			Job job = new IndexFilesOfProjectJob(project, files);
			job.setRule(IndexManager.MUTEX_RULE);
			job.setPriority(Job.BUILD);
			job.schedule();
		}

		private void removeIndexOfFilesOfProject(IProject project, Set<IFile> files)
		{
			Job job = new RemoveIndexOfFilesOfProjectJob(project, files);
			job.setRule(IndexManager.MUTEX_RULE);
			job.setPriority(Job.BUILD);
			job.schedule();
		}
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
			case IResourceChangeEvent.POST_BUILD:
				ISavedState savedState = processIResourceChangeEventPOST_BUILD.get();
				if (savedState == null)
				{
					return;
				}
			case IResourceChangeEvent.PRE_DELETE:
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

	/**
	 * getContextContributors
	 *
	 * @return
	 */
	public static IFileIndexingParticipant[] getFileIndexingParticipants()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		List<IFileIndexingParticipant> fileIndexingParticipants = new ArrayList<IFileIndexingParticipant>();

		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(IndexActivator.PLUGIN_ID,
					FILE_INDEXING_PARTICIPANTS_ID);

			if (extensionPoint != null)
			{
				IExtension[] extensions = extensionPoint.getExtensions();

				for (IExtension extension : extensions)
				{
					IConfigurationElement[] elements = extension.getConfigurationElements();

					for (IConfigurationElement element : elements)
					{
						if (element.getName().equals(TAG_FILE_INDEXING_PARTICIPANT))
						{
							try
							{
								IFileIndexingParticipant fileIndexingParticipant = (IFileIndexingParticipant) element
										.createExecutableExtension(ATTR_CLASS);

								fileIndexingParticipants.add(fileIndexingParticipant);
							}
							catch (CoreException e)
							{
								// TODO
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		return fileIndexingParticipants.toArray(new IFileIndexingParticipant[fileIndexingParticipants.size()]);
	}
}
