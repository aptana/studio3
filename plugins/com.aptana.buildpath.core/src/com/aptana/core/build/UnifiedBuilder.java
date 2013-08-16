/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.buildpath.core.BuildPathManager;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IMarkerConstants;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.IIndexFileContributor;
import com.aptana.index.core.IndexContainerJob;
import com.aptana.index.core.IndexFileJob;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.index.core.filter.IIndexFilterParticipant;

public class UnifiedBuilder extends IncrementalProjectBuilder
{

	public static final String ID = "com.aptana.ide.core.unifiedBuilder"; //$NON-NLS-1$
	private boolean traceParticipantsEnabled = false;

	public UnifiedBuilder()
	{
	}

	private static void removeProblemsAndTasksFor(IResource resource)
	{
		try
		{
			if (resource != null && resource.isAccessible())
			{
				resource.deleteMarkers(IMarkerConstants.PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
				resource.deleteMarkers(IMarkerConstants.TASK_MARKER, true, IResource.DEPTH_INFINITE);
			}
		}
		catch (CoreException e)
		{
			// assume there were no problems
		}
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException
	{
		super.clean(monitor);

		IProject project = getProjectHandle();

		IBuildParticipantManager manager = getBuildParticipantManager();
		if (manager == null)
		{
			return;
		}
		List<IBuildParticipant> participants = manager.getAllBuildParticipants();
		participants = filterToEnabled(participants, project);

		SubMonitor sub = SubMonitor.convert(monitor, participants.size() + 2);
		sub.worked(1);

		removeProblemsAndTasksFor(project);
		sub.worked(1);

		// FIXME Should we visit all files and call "deleteFile" sort of like what we do with fullBuild?
		for (IBuildParticipant participant : participants)
		{
			if (sub.isCanceled())
			{
				return;
			}

			participant.clean(project, sub.newChild(1));
		}
		sub.done();
	}

	private List<IBuildParticipant> filterToEnabled(List<IBuildParticipant> participants, final IProject project)
	{
		return CollectionsUtil.filter(participants, new IFilter<IBuildParticipant>()
		{
			public boolean include(IBuildParticipant item)
			{
				// Order is important here! If we check for enablement based on build type in prefs, the contributing
				// plugin loads!
				// FIXME is there any way to defer the second enablement check until after we do content type check?
				try
				{
					return item != null && item.isEnabled(project) && item.isEnabled(BuildType.BUILD);
				}
				catch (Exception e)
				{
					IdeLog.logWarning(BuildPathCorePlugin.getDefault(), e);
					return false;
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException
	{
		traceParticipantsEnabled = IdeLog.isTraceEnabled(BuildPathCorePlugin.getDefault(),
				IDebugScopes.BUILDER_PARTICIPANTS);

		boolean logTraceEnabled = traceLoggingEnabled();

		IProject project = getProjectHandle();
		String projectName = project.getName();
		long startTime = System.nanoTime();

		SubMonitor sub = SubMonitor.convert(monitor, 100);

		// Keep these build participant instances and use them in the build process, rather than grabbing new ones
		// in sub-methods. We do pre- and post- setups on them, so we need to retain instances.
		IBuildParticipantManager manager = getBuildParticipantManager();
		if (manager == null)
		{
			return new IProject[0];
		}
		List<IBuildParticipant> participants = manager.getAllBuildParticipants();
		participants = filterToEnabled(participants, project);
		buildStarting(participants, kind, sub.newChild(10));

		if (kind == IncrementalProjectBuilder.FULL_BUILD)
		{
			if (logTraceEnabled)
			{
				logTrace(MessageFormat.format(Messages.UnifiedBuilder_PerformingFullBuld, projectName));
			}
			fullBuild(participants, sub.newChild(80));
		}
		else
		{
			IResourceDelta delta = getResourceDelta();
			if (delta == null)
			{
				if (logTraceEnabled)
				{
					logTrace(MessageFormat.format(Messages.UnifiedBuilder_PerformingFullBuildNullDelta, projectName));
				}
				fullBuild(participants, sub.newChild(80));
			}
			else
			{
				if (logTraceEnabled)
				{
					logTrace(MessageFormat.format(Messages.UnifiedBuilder_PerformingIncrementalBuild, projectName));
				}
				incrementalBuild(participants, delta, sub.newChild(80));
			}
		}

		buildEnding(participants, sub.newChild(10));

		if (logTraceEnabled)
		{
			double endTime = ((double) System.nanoTime() - startTime) / 1000000;
			logTrace(MessageFormat.format(Messages.UnifiedBuilder_FinishedBuild, projectName, endTime));
		}
		return null;
	}

	protected boolean traceLoggingEnabled()
	{
		return IdeLog.isTraceEnabled(CorePlugin.getDefault(), IDebugScopes.BUILDER);
	}

	private static void logTrace(String msg)
	{
		IdeLog.logInfo(CorePlugin.getDefault(), msg, IDebugScopes.BUILDER);
	}

	private void buildEnding(List<IBuildParticipant> participants, IProgressMonitor monitor)
	{
		if (participants == null)
		{
			return;
		}
		SubMonitor sub = SubMonitor.convert(monitor, participants.size());
		for (IBuildParticipant participant : participants)
		{
			participant.buildEnding(sub.newChild(1));
		}
		sub.done();
	}

	private void buildStarting(List<IBuildParticipant> participants, int kind, IProgressMonitor monitor)
	{
		if (CollectionsUtil.isEmpty(participants))
		{
			return;
		}
		SubMonitor sub = SubMonitor.convert(monitor, participants.size());
		for (IBuildParticipant participant : participants)
		{
			participant.buildStarting(getProjectHandle(), kind, sub.newChild(1));
		}
		sub.done();
	}

	private void incrementalBuild(List<IBuildParticipant> participants, IResourceDelta delta, IProgressMonitor monitor)
	{
		try
		{
			SubMonitor sub = SubMonitor.convert(monitor, 100);
			ResourceCollector collector = new ResourceCollector();
			delta.accept(collector);

			indexProjectBuildPaths(sub.newChild(25));

			// Notify of the removed files
			removeFiles(participants, collector.removedFiles, sub.newChild(5));

			// Now build the new/updated files
			buildFiles(participants, collector.updatedFiles, sub.newChild(70));
		}
		catch (CoreException e)
		{
			IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
		}
	}

	private void removeFiles(List<IBuildParticipant> participants, Set<IFile> filesToRemoveFromIndex,
			IProgressMonitor monitor) throws CoreException
	{
		if (CollectionsUtil.isEmpty(filesToRemoveFromIndex))
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, 16 * filesToRemoveFromIndex.size());
		for (IFile file : filesToRemoveFromIndex)
		{
			BuildContext context = new BuildContext(file);
			sub.worked(1);
			IBuildParticipantManager manager = getBuildParticipantManager();
			if (manager == null)
			{
				return;
			}
			List<IBuildParticipant> filteredParticipants = manager.filterParticipants(participants,
					context.getContentType());
			sub.worked(5);
			deleteFile(context, filteredParticipants, sub.newChild(10));
		}
		sub.done();
	}

	private void deleteFile(BuildContext context, List<IBuildParticipant> participants, IProgressMonitor monitor)
	{
		if (CollectionsUtil.isEmpty(participants))
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, participants.size());
		for (IBuildParticipant participant : participants)
		{
			participant.deleteFile(context, sub.newChild(1));
		}
		sub.done();
	}

	protected IBuildParticipantManager getBuildParticipantManager()
	{
		BuildPathCorePlugin plugin = BuildPathCorePlugin.getDefault();
		return (plugin == null) ? null : plugin.getBuildParticipantManager();
	}

	/**
	 * For a full build, we grab all files inside the project and then call build on each file.
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void fullBuild(List<IBuildParticipant> participants, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);

		indexProjectBuildPaths(sub.newChild(50));

		// Index files contributed...
		// TODO Remove these special "contributed" files?
		IProject project = getProjectHandle();
		URI uri = project.getLocationURI();
		Set<IFileStore> contributedFiles = getContributedFiles(uri);
		sub.worked(2);
		buildContributedFiles(participants, contributedFiles, sub.newChild(6));

		// Now index the actual files in the project
		CollectingResourceVisitor visitor = new CollectingResourceVisitor();
		project.accept(visitor);
		visitor.files.trimToSize(); // shrink it down to size when we're done
		sub.worked(2);
		buildFiles(participants, visitor.files, sub.newChild(40));

		sub.done();
	}

	/**
	 * Grabs the list of {@link IBuildPathEntry}s for a project and make sure the indices for them are up-to-date.
	 * 
	 * @param monitor
	 */
	private void indexProjectBuildPaths(IProgressMonitor monitor)
	{
		IProject project = getProjectHandle();
		Set<IBuildPathEntry> entries = getBuildPathManager().getBuildPaths(project);
		SubMonitor sub = SubMonitor.convert(monitor, entries.size());
		for (IBuildPathEntry entry : entries)
		{
			try
			{
				IFileStore fileStore = EFS.getStore(entry.getPath());
				if (fileStore != null)
				{
					if (fileStore.fetchInfo().isDirectory())
					{
						new IndexContainerJob(entry.getDisplayName(), entry.getPath()).run(sub.newChild(1));
					}
					else
					{
						new IndexFileJob(entry.getDisplayName(), entry.getPath()).run(sub.newChild(1));
					}
				}
			}
			catch (Throwable e)
			{
				IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
			}
		}
	}

	protected BuildPathManager getBuildPathManager()
	{
		return BuildPathManager.getInstance();
	}

	/**
	 * @param participants
	 * @param files
	 * @param monitor
	 * @throws CoreException
	 */
	private void buildContributedFiles(List<IBuildParticipant> participants, Set<IFileStore> files,
			IProgressMonitor monitor) throws CoreException
	{
		if (CollectionsUtil.isEmpty(files))
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, 15 * files.size());
		IProject project = getProjectHandle();
		for (IFileStore file : files)
		{
			BuildContext context = new FileStoreBuildContext(project, file);
			sub.worked(1);

			IBuildParticipantManager manager = getBuildParticipantManager();
			if (manager == null)
			{
				return;
			}
			List<IBuildParticipant> filteredParticipants = manager.filterParticipants(participants,
					context.getContentType());
			sub.worked(2);

			buildFile(context, filteredParticipants, sub.newChild(12));

			// stop building if canceled
			if (sub.isCanceled())
			{
				break;
			}
		}
		sub.done();
	}

	/**
	 * getContributedFiles
	 * 
	 * @param container
	 * @return
	 */
	protected Set<IFileStore> getContributedFiles(URI container)
	{
		// FIXME This shoves all contributed files into the same index as the project!
		// We want the notion of a project referring to build path entries that are maintained in their own indices,
		// which we can share across projects!
		Set<IFileStore> result = new HashSet<IFileStore>();

		IndexManager manager = getIndexManager();
		if (manager != null)
		{
			for (IIndexFileContributor contributor : manager.getFileContributors())
			{
				Set<IFileStore> files = contributor.getFiles(container);

				if (!CollectionsUtil.isEmpty(files))
				{
					result.addAll(files);
				}
			}
		}

		return result;
	}

	protected IndexManager getIndexManager()
	{
		IndexPlugin plugin = IndexPlugin.getDefault();
		return (plugin == null) ? null : plugin.getIndexManager();
	}

	/**
	 * Ugly, but necessary for testing so we can pass in a project.
	 * 
	 * @return
	 */
	protected IProject getProjectHandle()
	{
		return getProject();
	}

	/**
	 * Ugly, but necessary for testing so we can pass in our own delta.
	 * 
	 * @return
	 */
	protected IResourceDelta getResourceDelta()
	{
		return getDelta(getProjectHandle());
	}

	private void buildFiles(List<IBuildParticipant> participants, Collection<IFile> files, IProgressMonitor monitor)
			throws CoreException
	{
		if (CollectionsUtil.isEmpty(participants) || CollectionsUtil.isEmpty(files))
		{
			return;
		}
		SubMonitor sub = SubMonitor.convert(monitor, 100);

		// Filter
		files = filterFiles(files, sub.newChild(10));

		// Then build
		doBuildFiles(participants, files, sub.newChild(90));

		sub.done();
	}

	private void doBuildFiles(List<IBuildParticipant> participants, Collection<IFile> files, IProgressMonitor monitor)
			throws CoreException
	{
		if (CollectionsUtil.isEmpty(files))
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, 15 * files.size());
		for (IFile file : files)
		{
			BuildContext context = new BuildContext(file);
			sub.worked(1);

			IBuildParticipantManager manager = getBuildParticipantManager();
			if (manager == null)
			{
				return;
			}
			List<IBuildParticipant> filteredParticipants = manager.filterParticipants(participants,
					context.getContentType());
			sub.worked(2);

			buildFile(context, filteredParticipants, sub.newChild(12));

			// stop building if canceled
			if (sub.isCanceled())
			{
				break;
			}
		}
		sub.done();
	}

	/**
	 * FIXME This is a holy hell of a mess! We map from IFiles to IFileStores, then filter on that, then map back! Can't
	 * we make the IIndexFilterParticipants also operate on IFiles? It seems like the only impl does anyways.
	 * 
	 * @return
	 */
	private Collection<IFile> filterFiles(Collection<IFile> files, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		// First filter out files that don't exist, are derived, or are team-private
		files = CollectionsUtil.filter(files, new IFilter<IFile>()
		{
			public boolean include(IFile item)
			{
				return !ResourceUtil.shouldIgnore(item);
			}
		});
		sub.worked(10);

		// Next map IFiles to IFileStores for filter participants' sake
		Set<IFileStore> fileStores = new HashSet<IFileStore>(CollectionsUtil.map(files, new IMap<IFile, IFileStore>()
		{

			public IFileStore map(IFile item)
			{
				IPath path = item.getLocation();
				return (path == null) ? null : EFS.getLocalFileSystem().getStore(path);
			}

		}));
		sub.worked(15);

		if (!CollectionsUtil.isEmpty(fileStores))
		{
			// Now let filters run
			IndexManager manager = getIndexManager();
			if (manager != null)
			{
				for (IIndexFilterParticipant filterParticipant : manager.getFilterParticipants())
				{
					fileStores = filterParticipant.applyFilter(fileStores);
				}
			}
			sub.worked(60);

			// Now we need to map back to IFiles again. UGH!
			return CollectionsUtil.map(fileStores, new IMap<IFileStore, IFile>()
			{

				public IFile map(IFileStore fileStore)
				{
					IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
					IFile[] iFiles = workspaceRoot.findFilesForLocationURI(fileStore.toURI());
					if (ArrayUtil.isEmpty(iFiles))
					{
						return null;
					}
					return iFiles[0];
				}

			});
		}

		return files;
	}

	private void buildFile(BuildContext context, List<IBuildParticipant> participants, IProgressMonitor monitor)
			throws CoreException
	{
		if (CollectionsUtil.isEmpty(participants))
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, 2 * participants.size());
		for (IBuildParticipant participant : participants)
		{
			long startTime = System.nanoTime();
			participant.buildFile(context, sub.newChild(1));
			if (traceParticipantsEnabled)
			{
				double endTime = ((double) System.nanoTime() - startTime) / 1000000;
				IdeLog.logTrace(
						BuildPathCorePlugin.getDefault(),
						MessageFormat
								.format("Executed build participant ''{0}'' on ''{1}'' in {2} ms.", participant.getName(), context.getURI(), endTime), IDebugScopes.BUILDER_PARTICIPANTS); //$NON-NLS-1$
			}

			// stop building if it has been canceled
			if (sub.isCanceled())
			{
				break;
			}
		}
		updateMarkers(context, sub.newChild(participants.size()));
		sub.done();
	}

	private void updateMarkers(BuildContext context, IProgressMonitor monitor)
	{
		final IFile file = context.getFile();
		final Map<String, Collection<IProblem>> itemsByType = context.getProblems();
		if (CollectionsUtil.isEmpty(itemsByType))
		{
			return;
		}
		// Performance fix: schedules the error handling as a single workspace update so that we don't trigger a
		// bunch of resource updated events while problem markers are being added to the file.
		IWorkspaceRunnable runnable = new IWorkspaceRunnable()
		{
			public void run(IProgressMonitor monitor)
			{
				updateMarkers(file, itemsByType, monitor);
			}
		};

		try
		{
			ResourcesPlugin.getWorkspace().run(runnable, getMarkerRule(file), IWorkspace.AVOID_UPDATE, monitor);
		}
		catch (CoreException e)
		{
			IdeLog.logError(BuildPathCorePlugin.getDefault(), "Error updating markers", e); //$NON-NLS-1$
		}
	}

	/**
	 * @param resource
	 * @return
	 */
	private static ISchedulingRule getMarkerRule(Object resource)
	{
		if (resource instanceof IResource)
		{
			return ResourcesPlugin.getWorkspace().getRuleFactory().markerRule((IResource) resource);
		}
		return null;
	}

	private synchronized void updateMarkers(IFile file, Map<String, Collection<IProblem>> itemsByType,
			IProgressMonitor monitor)
	{
		if (!file.exists())
		{
			// no need to update the marker when the resource no longer exists
			return;
		}
		SubMonitor sub = SubMonitor.convert(monitor, itemsByType.size() * 10);

		// FIXME Do a diff like we do in ValidationManager!
		for (String markerType : itemsByType.keySet())
		{
			try
			{
				Collection<IProblem> newItems = itemsByType.get(markerType);
				// deletes the old markers
				file.deleteMarkers(markerType, false, IResource.DEPTH_INFINITE);
				sub.worked(1);

				// adds the new ones
				addMarkers(newItems, markerType, file, sub.newChild(9));
			}
			catch (CoreException e)
			{
				IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
			}
		}
		sub.done();
	}

	private void addMarkers(Collection<IProblem> items, String markerType, IFile file, IProgressMonitor monitor)
			throws CoreException
	{
		if (CollectionsUtil.isEmpty(items))
		{
			return;
		}
		SubMonitor sub = SubMonitor.convert(monitor, items.size() * 2);
		for (IProblem item : items)
		{
			IMarker marker = file.createMarker(markerType);
			sub.worked(1);
			marker.setAttributes(item.createMarkerAttributes());
			sub.worked(1);
		}
		sub.done();
	}

	/**
	 * Collects all files with infinite depth. Used to grab all files inside an {@link IProject} for full builds.
	 * 
	 * @author cwilliams
	 */
	private static class CollectingResourceVisitor implements IResourceVisitor
	{
		ArrayList<IFile> files;

		private CollectingResourceVisitor()
		{
			files = new ArrayList<IFile>();
		}

		public boolean visit(IResource resource) throws CoreException
		{
			if (IResource.FILE == resource.getType())
			{
				files.add((IFile) resource);
				return false;
			}
			return true;
		}
	}

	/**
	 * Converts an {@link IResourceDelta} into two {@link Set}s: files that have been added/updated, and files that have
	 * been removed/deleted.
	 * 
	 * @author cwilliams
	 */
	private static class ResourceCollector implements IResourceDeltaVisitor
	{
		Set<IFile> updatedFiles = new HashSet<IFile>();
		Set<IFile> removedFiles = new HashSet<IFile>();

		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if (resource instanceof IFile)
			{
				if (delta.getKind() == IResourceDelta.ADDED
						|| (delta.getKind() == IResourceDelta.CHANGED && ((delta.getFlags() & (IResourceDelta.CONTENT | IResourceDelta.ENCODING)) != 0)))
				{
					updatedFiles.add((IFile) resource);
				}
				else if (delta.getKind() == IResourceDelta.REMOVED)
				{
					removedFiles.add((IFile) resource);
				}
			}
			return true;
		}
	}
}
