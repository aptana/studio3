/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;

public class GitIndex
{

	/**
	 * Short status constants
	 */
	private static final String ADD_STATUS = "A"; //$NON-NLS-1$
	private static final String DELETED_STATUS = "D"; //$NON-NLS-1$
	private static final String UNMERGED_STATUS = "U"; //$NON-NLS-1$

	private static final String NULL_DELIMITER = "\0"; //$NON-NLS-1$

	/**
	 * File extensions we check against and use to assume if a file may be binary (to not show a diff/content in various
	 * UI views)
	 */
	@SuppressWarnings("nls")
	private static final String[] BINARY_EXTENSIONS = new String[] { "pdf", "jpg", "jpeg", "png", "bmp", "gif", "o",
			"class", "zip", "gz", "tar", "ico", "so", "jar", "exe", "7z", "app", "apk", "ipa", "war" };

	private GitRepository repository;

	/**
	 * The list of changed files that is a copy of the above list. Only copied at the very end of the refresh, so it
	 * always contains the full listing from last finished refresh call.
	 */
	List<ChangedFile> changedFiles;
	private Object changedFilesLock = new Object();

	private boolean notify;

	/**
	 * Service which launches the refresh commands in threads.
	 */
	private ExecutorService es;

	/**
	 * A job which gathers up refresh requests and runs them.
	 */
	private GitIndexRefreshJob refreshJob;

	GitIndex(GitRepository repository)
	{
		Assert.isNotNull(repository, "GitIndex requires a repository"); //$NON-NLS-1$
		this.repository = repository;
		this.refreshJob = new GitIndexRefreshJob(this);
		this.es = Executors.newFixedThreadPool(3);
	}

	/**
	 * Used by callers who don't need to wait for it to finish so we can squash together repeated calls when they come
	 * rapid-fire.
	 */
	synchronized void scheduleBatchRefresh()
	{
		if (refreshJob != null)
		{
			refreshJob.refreshAll();
		}
	}

	/**
	 * Run a refresh synchronously. FIXME Should this even be visible to callers? We should pick up file events via
	 * watcher to refresh whenever we really need to. This should become default visibility.
	 * 
	 * @param monitor
	 * @return
	 */
	public IStatus refresh(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			return refresh(true, null, sub.newChild(100));
		}
		finally
		{
			sub.done();
		}
	}

	/**
	 * If the filePaths is empty, do batch operations!
	 * 
	 * @param notify
	 * @param filePaths
	 * @param monitor
	 * @return
	 */
	IStatus refresh(boolean notify, final Collection<IPath> filePaths, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		if (sub.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}
		this.notify = notify;

		// If we don't run this, we end up showing files as unstaged when they're no longer modified!
		IStatus result;
		synchronized (this)
		{
			repository.forceWrite(); // Do we only want to try the lock if we're in UI thread?
			result = GitExecutable.instance().runInBackground(repository.workingDirectory(), "update-index", "-q", //$NON-NLS-1$ //$NON-NLS-2$
					"--unmerged", "--ignore-missing", "--refresh"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			repository.exitWriteProcess();
		}
		if (result == null) // couldn't even execute!
		{
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), "Failed to execute git update-index"); //$NON-NLS-1$
		}
		if (!result.isOK())
		{
			IdeLog.logError(GitPlugin.getDefault(), "Unable to run update-index: " + result.getMessage()); //$NON-NLS-1$
			return result;
		}

		final Set<String> portablePathStrings = new HashSet<String>(CollectionsUtil.map(filePaths,
				new IMap<IPath, String>()
				{
					public String map(IPath item)
					{
						return item.toPortableString();
					}
				}));

		Set<Callable<Map<IPath, ChangedFile>>> jobs = new HashSet<Callable<Map<IPath, ChangedFile>>>(3);
		jobs.add(new UntrackedFilesRefreshJob(this, portablePathStrings));
		jobs.add(new UnstagedFilesRefreshJob(this, portablePathStrings));
		jobs.add(new StagedFilesRefreshJob(this, portablePathStrings));

		// Last chance to cancel...
		if (monitor != null && monitor.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		// Now create a new temporary list so we can build it up...
		Map<IPath, ChangedFile> newChangedFiles = new HashMap<IPath, ChangedFile>();

		// Schedule all the jobs
		MultiStatus errors = new MultiStatus(GitPlugin.PLUGIN_ID, 1,
				"Errors occurred while grabbing changed file listings", null); //$NON-NLS-1$
		try
		{
			if (es.isShutdown())
			{
				return Status.CANCEL_STATUS;
			}
			List<Future<Map<IPath, ChangedFile>>> futures = es.invokeAll(jobs);

			// Now wait for them to finish
			for (Future<Map<IPath, ChangedFile>> future : futures)
			{
				while (!future.isDone())
				{
					if (monitor != null && monitor.isCanceled())
					{
						future.cancel(true);
					}
					Thread.yield();
				}

				// When done, get their result
				try
				{
					Map<IPath, ChangedFile> map = future.get();
					if (newChangedFiles.isEmpty())
					{
						newChangedFiles.putAll(map);
					}
					else
					{
						// we may get multiple entries for the same path, we need to be careful and merge them together
						// (i.e. we may have staged and unstaged changes for same file)
						for (Entry<IPath, ChangedFile> entry : map.entrySet())
						{
							IPath path = entry.getKey();
							ChangedFile file = entry.getValue();
							if (newChangedFiles.containsKey(path))
							{
								// Merge the two entries
								newChangedFiles.put(path, file.merge(newChangedFiles.get(path)));
							}
							else
							{
								newChangedFiles.put(path, file);
							}
						}
					}
				}
				catch (CancellationException ce)
				{
					// ignore
				}
				catch (ExecutionException e)
				{
					Throwable t = e.getCause();
					if (t instanceof CoreException)
					{
						errors.merge(((CoreException) t).getStatus());
					}
					else
					{
						errors.merge(new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, t.getMessage(), t));
					}
				}
			}
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(GitPlugin.getDefault(), e);
		}

		// Copy the last full list of changed files we built up on refresh. Used to pass along the delta
		// FIXME I think the values here may have already changed! I saw a file that had staged changes but no unstaged
		// prior to commit
		// but here it showed true for both (which should have only gotten modified by a pre-commit hook)
		Collection<ChangedFile> preRefresh;
		synchronized (this.changedFilesLock)
		{
			// Make a copy of the changed file listing, pre-refresh
			if (this.changedFiles != null)
			{
				preRefresh = new ArrayList<ChangedFile>(this.changedFiles.size());
				for (ChangedFile file : this.changedFiles)
				{
					preRefresh.add(file.clone());
				}
			}
			else
			{
				preRefresh = new ArrayList<ChangedFile>(0);
			}

			// Now wipe any existing ChangedFile entries for any of the filePaths and add the ones we generated in
			// dictionary
			if (CollectionsUtil.isEmpty(filePaths))
			{
				this.changedFiles = new ArrayList<ChangedFile>();
			}
			else
			{
				this.changedFiles = CollectionsUtil.filter(this.changedFiles, new IFilter<ChangedFile>()
				{
					public boolean include(ChangedFile item)
					{
						return !portablePathStrings.contains(item.getRelativePath().toPortableString());
					}
				});
			}
			if (!CollectionsUtil.isEmpty(newChangedFiles))
			{
				this.changedFiles.addAll(newChangedFiles.values());
			}
		}

		// Don't hold onto temp list in memory!
		newChangedFiles = null;

		postIndexChange(preRefresh, this.changedFiles);
		sub.done();
		if (!errors.isOK())
		{
			return errors;
		}
		return Status.OK_STATUS;
	}

	private void postIndexChange(Collection<ChangedFile> preChangeFiles, Collection<ChangedFile> postChangeFiles)
	{
		if (this.notify)
		{
			this.repository.fireIndexChangeEvent(preChangeFiles, postChangeFiles);
		}
		else
		{
			this.notify = true;
		}
	}

	/**
	 * Makes a copy of the internal list of changed files so that iterating won't ever result in a
	 * ConcurrentModificationException. try to avoid use if possible, since a deep copy is made which can be expensive.
	 * This method populates the changedFiles collection lazily on first demand.
	 * 
	 * @return
	 */
	public List<ChangedFile> changedFiles()
	{
		boolean isNull = false;
		synchronized (this.changedFilesLock)
		{
			isNull = (this.changedFiles == null);
		}

		if (isNull)
		{
			// Don't want to call back to fireIndexChangeEvent yet!
			IStatus status = refresh(false, null, new NullProgressMonitor());
			if (!status.isOK())
			{
				IdeLog.logError(GitPlugin.getDefault(), status.getMessage());
				return Collections.emptyList();
			}
		}

		synchronized (this.changedFilesLock)
		{
			if (this.changedFiles == null)
			{
				return Collections.emptyList();
			}

			List<ChangedFile> copy = new ArrayList<ChangedFile>(this.changedFiles.size());
			for (ChangedFile file : this.changedFiles)
			{
				copy.add(file.clone());
			}
			return copy;
		}
	}

	public IStatus stageFiles(Collection<ChangedFile> stageFiles)
	{
		if (CollectionsUtil.isEmpty(stageFiles))
		{
			// no-op
			return Status.OK_STATUS;
		}

		StringBuffer input = new StringBuffer(stageFiles.size()
				* stageFiles.iterator().next().getRelativePath().toPortableString().length());
		for (ChangedFile file : stageFiles)
		{
			input.append(file.getRelativePath()).append('\n');
		}

		@SuppressWarnings("nls")
		IStatus result = repository.executeWithInput(input.toString(), "update-index", "--add", "--remove", "--stdin");
		if (result == null)
		{
			return new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, "Failed to stage files. Process failed to run."); //$NON-NLS-1$;
		}
		if (!result.isOK())
		{
			IdeLog.logError(GitPlugin.getDefault(),
					MessageFormat.format("Failed to stage files: {0}", result.getMessage()), IDebugScopes.DEBUG); //$NON-NLS-1$
			return result;
		}

		ArrayList<ChangedFile> preFiles = new ArrayList<ChangedFile>(stageFiles.size());
		// Update the staged/unstaged flags in the passed in copy of changed files, and our internal list of changed
		// files.
		for (ChangedFile file : stageFiles)
		{
			preFiles.add(file.clone());
			synchronized (changedFilesLock)
			{
				if (this.changedFiles != null)
				{
					int index = Collections.binarySearch(this.changedFiles, file);
					if (index >= 0)
					{

						ChangedFile orig = this.changedFiles.get(index);
						orig.makeStaged();
					}
				}
			}

			file.makeStaged();
		}
		preFiles.trimToSize();

		postIndexChange(preFiles, stageFiles);
		return result;
	}

	public IStatus unstageFiles(Collection<ChangedFile> unstageFiles)
	{
		if (CollectionsUtil.isEmpty(unstageFiles))
		{
			// no-op, return OK
			return Status.OK_STATUS;
		}

		StringBuilder input = new StringBuilder();
		for (ChangedFile file : unstageFiles)
		{
			input.append(file.indexInfo());
		}

		IStatus result = repository.executeWithInput(input.toString(), "update-index", "-z", "--index-info"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (result == null)
		{
			return new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, "Failed to unstage files. Process failed to run."); //$NON-NLS-1$
		}
		if (!result.isOK())
		{
			IdeLog.logError(GitPlugin.getDefault(),
					MessageFormat.format("Failed to stage files: {0}", result.getMessage()), IDebugScopes.DEBUG); //$NON-NLS-1$
			return result;
		}

		// Update the staged/unstaged flags in the passed in copy of changed files, and our internal list of changed
		// files.
		ArrayList<ChangedFile> preFiles = new ArrayList<ChangedFile>(unstageFiles.size());
		for (ChangedFile file : unstageFiles)
		{
			preFiles.add(file.clone());

			synchronized (this.changedFilesLock)
			{
				if (this.changedFiles != null)
				{
					int index = Collections.binarySearch(this.changedFiles, file);
					if (index >= 0)
					{

						ChangedFile orig = this.changedFiles.get(index);
						orig.makeUnstaged();
					}
				}
			}

			file.makeUnstaged();
		}
		preFiles.trimToSize();

		postIndexChange(preFiles, unstageFiles);
		return result;
	}

	public IStatus discardChangesForFiles(Collection<ChangedFile> discardFiles)
	{
		StringBuilder input = new StringBuilder();
		for (ChangedFile file : discardFiles)
		{
			input.append(file.getRelativePath().toPortableString()).append(NULL_DELIMITER);
		}

		IStatus result = repository.executeWithInput(input.toString(),
				"checkout-index", "--index", "--quiet", "--force", "-z", "--stdin"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		if (result == null)
		{
			return new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, "Failed to revert files. Process failed to run."); //$NON-NLS-1$
		}
		if (!result.isOK())
		{
			IdeLog.logError(GitPlugin.getDefault(),
					MessageFormat.format("Failed to revert files: {0}", result.getMessage()), IDebugScopes.DEBUG); //$NON-NLS-1$
			return result;
		}

		ArrayList<ChangedFile> preFiles = new ArrayList<ChangedFile>(discardFiles.size());
		for (ChangedFile file : discardFiles)
		{
			preFiles.add(file.clone());
			file.setUnstaged(false);
		}
		preFiles.trimToSize();

		postIndexChange(preFiles, discardFiles);
		return result;
	}

	public IStatus commit(String commitMessage)
	{
		IStatus status = doCommit(commitMessage);
		if (status.isOK())
		{
			repository.hasChanged();
		}
		// even if a commit fails, the repository's changed files listing may have changed.
		refresh(new NullProgressMonitor());
		return status;
	}

	private IStatus doCommit(String commitMessage)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			commitMessage = commitMessage.replace("\"", "\\\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return repository.execute(GitRepository.ReadWrite.WRITE, repository.workingDirectory(),
				ShellExecutable.getEnvironment(repository.workingDirectory()), "commit", "-m", commitMessage); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Returns the summary of all commits between two revisions.
	 * 
	 * @param sha1
	 *            SHA commit hash, or name of a ref (i.e. 'refs/heads/master')
	 * @param sha2
	 *            SHA commit hash, or name of a ref (i.e. 'refs/heads/master')
	 * @return
	 */
	String[] commitsBetween(String sha1, String sha2)
	{
		// Speed up the most common case of when the two SHAs are the same for refs!
		if (sha1.startsWith(GitRef.REFS))
		{
			sha1 = repository.toSHA(GitRef.refFromString(sha1));
		}
		if (sha2.startsWith(GitRef.REFS))
		{
			sha2 = repository.toSHA(GitRef.refFromString(sha2));
		}
		if (sha1.equals(sha2))
		{
			return ArrayUtil.NO_STRINGS;
		}
		IStatus status = repository.execute(GitRepository.ReadWrite.READ, "log", "--pretty=format:\"%s\"", //$NON-NLS-1$ //$NON-NLS-2$
				sha1 + ".." + sha2); //$NON-NLS-1$
		if (status == null || !status.isOK() || status.getMessage().trim().length() == 0)
		{
			return ArrayUtil.NO_STRINGS;
		}
		return status.getMessage().split("[\r\n]+"); //$NON-NLS-1$ // $codepro.audit.disable platformSpecificLineSeparator
	}

	/**
	 * @param file
	 *            the ChangedFile to generate a diff for.
	 * @param staged
	 *            Whether the file is staged or not
	 * @param contextLines
	 *            number of lines to show context for. default for underlying command is 3.
	 * @return
	 */
	public String diffForFile(ChangedFile file, boolean staged, int contextLines)
	{
		if (hasBinaryAttributes(file))
		{
			return Messages.GitIndex_BinaryDiff_Message;
		}

		String parameter = "-U" + contextLines; //$NON-NLS-1$
		if (staged)
		{
			String indexPath = ":0:" + file.getRelativePath().toPortableString(); //$NON-NLS-1$

			if (file.status == ChangedFile.Status.NEW)
			{
				IStatus status = repository.execute(GitRepository.ReadWrite.READ, "show", indexPath); //$NON-NLS-1$
				return status.getMessage();
			}

			IStatus result = repository.execute(GitRepository.ReadWrite.READ, "diff-index", parameter, "--cached", //$NON-NLS-1$ //$NON-NLS-2$
					GitRepository.HEAD, "--", file.getRelativePath().toPortableString()); //$NON-NLS-1$
			if (result == null || !result.isOK())
			{
				return null;
			}
			return result.getMessage();
		}

		// unstaged
		if (file.status == ChangedFile.Status.NEW)
		{
			try
			{
				return IOUtil.read(
						new FileInputStream(workingDirectory().append(file.getRelativePath().toPortableString())
								.toFile()), IOUtil.UTF_8); // $codepro.audit.disable
				// closeWhereCreated
			}
			catch (FileNotFoundException e)
			{
				return null;
			}
		}

		IStatus result = repository.execute(GitRepository.ReadWrite.READ,
				"diff-files", parameter, "--", file.getRelativePath().toPortableString()); //$NON-NLS-1$ //$NON-NLS-2$
		return result.getMessage();
	}

	public boolean hasBinaryAttributes(ChangedFile file)
	{
		IStatus result = repository.execute(GitRepository.ReadWrite.READ,
				"check-attr", "binary", file.getRelativePath().toPortableString()); //$NON-NLS-1$ //$NON-NLS-2$
		String output = result.getMessage();
		output = output.trim();
		if (output.endsWith("binary: set")) //$NON-NLS-1$
		{
			return true;
		}
		if (output.endsWith("binary: unset")) //$NON-NLS-1$
		{
			return false;
		}
		if (output.endsWith("binary: unspecified")) //$NON-NLS-1$
		{
			String fileExtension = file.getRelativePath().getFileExtension();
			if (fileExtension != null)
			{
				// try common filename-extensions
				for (String extension : BINARY_EXTENSIONS)
				{
					if (fileExtension.equalsIgnoreCase(extension))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * For use in telling if a given resource is a changed file, or is a folder containing changes underneath it.
	 * 
	 * @param resource
	 * @return
	 */
	protected boolean resourceOrChildHasChanges(IResource resource)
	{
		List<ChangedFile> changedFiles = changedFiles();
		if (CollectionsUtil.isEmpty(changedFiles))
		{
			return false;
		}
		// FIXME Can we sort the changed files by path or something to help speed up this lookup?
		IPath workingDirectory = repository.workingDirectory();
		IPath resourcePath = resource.getLocation();
		for (ChangedFile changedFile : changedFiles)
		{
			IPath fullPath = workingDirectory.append(changedFile.getRelativePath()).makeAbsolute();
			if (resourcePath.isPrefixOf(fullPath))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isDirty()
	{
		return !changedFiles().isEmpty();
	}

	protected boolean hasUnresolvedMergeConflicts()
	{
		List<ChangedFile> changedFiles = changedFiles();
		if (CollectionsUtil.isEmpty(changedFiles))
		{
			return false;
		}
		for (ChangedFile changedFile : changedFiles)
		{
			if (changedFile.hasUnmergedChanges() && changedFile.hasUnstagedChanges())
			{
				return true;
			}
		}
		return false;
	}

	public Set<IResource> getChangedResources()
	{
		Set<IResource> resources = new HashSet<IResource>();
		List<ChangedFile> changedFiles = changedFiles();
		if (!CollectionsUtil.isEmpty(changedFiles))
		{
			for (ChangedFile changedFile : changedFiles)
			{
				IResource resource = getResourceForChangedFile(changedFile);
				if (resource != null)
				{
					resources.add(resource);
				}
			}
		}
		return resources;
	}

	IFile getResourceForChangedFile(ChangedFile changedFile)
	{
		return ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(workingDirectory().append(changedFile.getRelativePath()));
	}

	protected ChangedFile getChangedFileForResource(IResource resource)
	{
		if (resource == null || resource.getLocationURI() == null)
		{
			return null;
		}

		IPath resourcePath = resource.getLocation();
		List<ChangedFile> changedFiles = changedFiles();
		// FIXME Doing searches like this every time
		if (!CollectionsUtil.isEmpty(changedFiles))
		{
			for (ChangedFile changedFile : changedFiles)
			{
				IPath fullPath = workingDirectory().append(changedFile.getRelativePath());
				if (resourcePath.equals(fullPath))
				{
					return changedFile;
				}
			}
		}
		return null;
	}

	private IPath workingDirectory()
	{
		return repository.workingDirectory();
	}

	/**
	 * Gets the list of changed files that are underneath the given container.
	 * 
	 * @param container
	 * @return
	 */
	protected List<ChangedFile> getChangedFilesForContainer(IContainer container)
	{
		if (container == null || container.getLocationURI() == null)
		{
			return Collections.emptyList();
		}

		List<ChangedFile> changedFiles = changedFiles();
		if (CollectionsUtil.isEmpty(changedFiles))
		{
			return Collections.emptyList();
		}

		IPath resourcePath = container.getLocation();
		List<ChangedFile> filtered = new ArrayList<ChangedFile>();
		IPath workingDirectory = repository.workingDirectory();
		for (ChangedFile changedFile : changedFiles)
		{
			IPath fullPath = workingDirectory.append(changedFile.getRelativePath()).makeAbsolute();
			if (resourcePath.isPrefixOf(fullPath))
			{
				filtered.add(changedFile);
			}
		}

		return filtered;
	}

	/**
	 * Aschedules a job to refresh the passed in filepaths. paths are expected to be relative to the repo root/working
	 * dir!
	 * 
	 * @param paths
	 *            A {@link Collection} of relative {@link IPath} - relative to the {@link #workingDirectory()}
	 */
	public void refreshAsync(final Collection<IPath> paths)
	{
		if (refreshJob != null)
		{
			refreshJob.refresh(paths);
		}
	}

	private abstract class FilesRefreshJob implements Callable<Map<IPath, ChangedFile>>
	{

		protected GitRepository repo;
		protected GitIndex index;
		protected Set<String> filePaths;

		private FilesRefreshJob(GitIndex index, Set<String> filePaths)
		{
			this.index = index;
			this.repo = index.repository;
			this.filePaths = filePaths;
		}

		protected List<String> linesFromNotification(String string)
		{
			// FIXME: throw an error?
			if (string == null)
			{
				return Collections.emptyList();
			}

			// Strip trailing null
			if (string.endsWith(NULL_DELIMITER))
			{
				string = string.substring(0, string.length() - 1);
			}

			if (string.length() == 0)
			{
				return Collections.emptyList();
			}

			return StringUtil.tokenize(string, NULL_DELIMITER);
		}

		protected Map<IPath, List<String>> dictionaryForLines(List<String> lines)
		{
			Map<IPath, List<String>> dictionary = new HashMap<IPath, List<String>>(lines.size() / 2);

			// Fill the dictionary with the new information. These lines are in the form of:
			// :00000 :0644 OTHER INDEX INFORMATION
			// Filename
			Assert.isTrue(lines.size() % 2 == 0, "Must have an even number of lines: " + lines); //$NON-NLS-1$
			Iterator<String> iter = lines.iterator();
			while (iter.hasNext())
			{
				String fileStatus = iter.next();
				String fileName = iter.next();
				dictionary.put(Path.fromPortableString(fileName), StringUtil.tokenize(fileStatus, " ")); //$NON-NLS-1$
			}

			return dictionary;
		}

		protected Map<IPath, ChangedFile> addFilesFromDictionary(final Map<IPath, List<String>> dictionary,
				final boolean staged, final boolean tracked)
		{
			// Do new files only if necessary
			if (dictionary == null || dictionary.isEmpty())
			{
				return Collections.emptyMap();
			}

			// All entries left in the dictionary haven't been accounted for
			// above, so we need to add them to the "files" array
			Map<IPath, ChangedFile> result = new HashMap<IPath, ChangedFile>(dictionary.size());
			for (Map.Entry<IPath, List<String>> entry : dictionary.entrySet())
			{
				IPath path = entry.getKey();
				// try a simple check for a sane path here
				if (path.isAbsolute())
				{
					IdeLog.logWarning(
							GitPlugin.getDefault(),
							MessageFormat
									.format("Found an entry for an absoolute path ({0}), won't add to our changed file listing for repo at {1}",
											path.toOSString(), workingDirectory().toOSString()));
					continue;
				}
				List<String> fileStatus = entry.getValue();

				ChangedFile.Status status = ChangedFile.Status.MODIFIED;
				if (fileStatus.get(4).equals(DELETED_STATUS))
				{
					status = ChangedFile.Status.DELETED;
				}
				else if (fileStatus.get(4).equals(UNMERGED_STATUS))
				{
					status = ChangedFile.Status.UNMERGED;
				}
				else if (fileStatus.get(0).equals(":000000")) //$NON-NLS-1$
				{
					status = ChangedFile.Status.NEW;
				}
				else
				{
					status = ChangedFile.Status.MODIFIED;
				}

				String mode = null;
				String sha = null;
				if (tracked)
				{
					mode = fileStatus.get(0).substring(1);
					sha = fileStatus.get(2);
				}
				result.put(path, new ChangedFile(repository, path, status, mode, sha, staged, !staged));
			}
			return result;
		}
	}

	private final class StagedFilesRefreshJob extends FilesRefreshJob
	{
		private StagedFilesRefreshJob(GitIndex index, Set<String> filePaths)
		{
			super(index, filePaths);
		}

		public Map<IPath, ChangedFile> call() throws Exception
		{
			// HEAD vs filesystem
			List<String> args = CollectionsUtil.newList("diff-index", "--cached", //$NON-NLS-1$ //$NON-NLS-2$
					"-z", GitRepository.HEAD); //$NON-NLS-1$
			if (!CollectionsUtil.isEmpty(filePaths))
			{
				args.add("--"); //$NON-NLS-1$
				args.addAll(filePaths);
			}

			IStatus result = repo.execute(GitRepository.ReadWrite.READ, args.toArray(new String[args.size()]));
			if (result != null && result.isOK())
			{
				return readStagedFiles(result.getMessage());
			}
			// We can get an error if this is a brand new repo with no commits (or staged changes)
			if (result != null
					&& result.getCode() == 128
					&& result.getMessage().startsWith(
							"fatal: ambiguous argument 'HEAD': unknown revision or path not in the working tree"))
			{
				return Collections.emptyMap();
			}
			throw new CoreException(result);
		}

		private Map<IPath, ChangedFile> readStagedFiles(String string)
		{
			List<String> lines = linesFromNotification(string);
			Map<IPath, List<String>> dic = dictionaryForLines(lines);
			return addFilesFromDictionary(dic, true, true);
		}
	}

	private final class UnstagedFilesRefreshJob extends FilesRefreshJob
	{
		private UnstagedFilesRefreshJob(GitIndex index, Set<String> filePaths)
		{
			super(index, filePaths);
		}

		public Map<IPath, ChangedFile> call() throws Exception
		{
			// index vs filesystem
			List<String> args = CollectionsUtil.newList("diff-files", "-z"); //$NON-NLS-1$ //$NON-NLS-2$
			if (!CollectionsUtil.isEmpty(filePaths))
			{
				args.add("--"); //$NON-NLS-1$
				args.addAll(filePaths);
			}

			IStatus result = repo.execute(GitRepository.ReadWrite.READ, args.toArray(new String[args.size()]));
			if (result != null && result.isOK())
			{
				return readUnstagedFiles(result.getMessage());
			}
			throw new CoreException(result);
		}

		private Map<IPath, ChangedFile> readUnstagedFiles(String string)
		{
			List<String> lines = linesFromNotification(string);
			Map<IPath, List<String>> dic = dictionaryForLines(lines);
			return addFilesFromDictionary(dic, false, true);
		}
	}

	private final class UntrackedFilesRefreshJob extends FilesRefreshJob
	{

		private UntrackedFilesRefreshJob(GitIndex index, Set<String> filePaths)
		{
			super(index, filePaths);
		}

		public Map<IPath, ChangedFile> call() throws Exception
		{
			// index vs working tree (HEAD?)
			List<String> args = CollectionsUtil.newList("ls-files", "--others", //$NON-NLS-1$ //$NON-NLS-2$
					"--exclude-standard", "-z"); //$NON-NLS-1$ //$NON-NLS-2$
			if (!CollectionsUtil.isEmpty(filePaths))
			{
				args.add("--"); //$NON-NLS-1$
				args.addAll(filePaths);
			}

			IStatus result = repo.execute(GitRepository.ReadWrite.READ, args.toArray(new String[args.size()]));
			if (result != null && result.isOK())
			{
				return readOtherFiles(result.getMessage());
			}
			throw new CoreException(result);
		}

		private Map<IPath, ChangedFile> readOtherFiles(String string)
		{
			List<String> lines = linesFromNotification(string);
			Map<IPath, List<String>> dictionary = new HashMap<IPath, List<String>>(lines.size());
			// Other files are untracked, so we don't have any real index information. Instead, we can just fake it.
			// The line below is not used at all, as for these files the commitBlob isn't set
			List<String> fileStatus = CollectionsUtil.newList(":000000", // for new file //$NON-NLS-1$
					"100644", //$NON-NLS-1$
					"0000000000000000000000000000000000000000", // SHA //$NON-NLS-1$
					"0000000000000000000000000000000000000000", //$NON-NLS-1$
					ADD_STATUS, // A for Add, D for delete
					null);
			for (String path : lines)
			{
				if (path.length() == 0)
				{
					continue;
				}
				dictionary.put(Path.fromPortableString(path), fileStatus);
			}

			return addFilesFromDictionary(dictionary, false, false);
		}
	}

	void dispose()
	{
		if (es != null)
		{
			// If the git index is being refreshed at the same time, we shall give some time to wait until all its tasks
			// are completed.
			try
			{
				es.awaitTermination(2000, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
			}
			es.shutdown();
		}
		if (refreshJob != null)
		{
			refreshJob.cancel();
			refreshJob = null;
		}
		// Cancel any jobs we're running in the index!
		IJobManager jobManager = Job.getJobManager();
		if (jobManager != null)
		{
			jobManager.cancel(this);
		}
	}
}
