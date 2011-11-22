/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;

public class GitIndex
{
	/**
	 * File extensions we check against and use to assume if a file may be binary (to not show a diff/content in various
	 * UI views)
	 */
	private static final String[] BINARY_EXTENSIONS = new String[] {
			".pdf", ".jpg", ".jpeg", ".png", ".bmp", ".gif", ".o", ".class", ".zip", ".gz", ".tar", ".ico", ".so", ".jar" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$

	private GitRepository repository;
	private boolean amend;

	/**
	 * Temporary list of changed files that we build up on refreshes. TODO Don't make this a field here that is
	 * redundant with the next list, instead make it a local var to refresh and pass it along to the jobs/methods that
	 * need it.
	 */
	private List<ChangedFile> files;

	/**
	 * The list of changed files that is a copy of the above list. Only copied at the very end of the refresh, so it
	 * always contains the full listing from last finished refresh call.
	 */
	List<ChangedFile> changedFiles;
	private Object changedFilesLock = new Object();

	private boolean notify;

	private Job indexRefreshJob;

	GitIndex(GitRepository repository, IPath workingDirectory)
	{
		super();

		Assert.isNotNull(repository, "GitIndex requires a repository"); //$NON-NLS-1$
		this.repository = repository;
	}

	/**
	 * Used by callers who don't need to wait for it to finish so we can squash together repeated calls when they come
	 * rapid-fire.
	 */
	public synchronized void refreshAsync()
	{
		if (indexRefreshJob == null)
		{
			indexRefreshJob = new Job("Refreshing git index") //$NON-NLS-1$
			{
				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					if (monitor != null && monitor.isCanceled())
					{
						return Status.CANCEL_STATUS;
					}
					refresh(monitor);
					return Status.OK_STATUS;
				}
			};
			indexRefreshJob.setSystem(!EclipseUtil.showSystemJobs());
		}
		else
		{
			indexRefreshJob.cancel();
		}
		indexRefreshJob.schedule(250);
	}

	/**
	 * Run a refresh synchronously.
	 * 
	 * @param monitor
	 * @return
	 */
	public IStatus refresh(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			return refresh(true, sub.newChild(100));
		}
		finally
		{
			sub.done();
		}
	}

	// FIXME this is a very ugly way of generating our index of files. Can we do less commands? Or possibly even read
	// the index itself?
	synchronized IStatus refresh(boolean notify, IProgressMonitor monitor)
	{
		if (monitor != null && monitor.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}
		this.notify = notify;

		// TODO Is this command really necessary? I don't think it is anymore... Can we avoid calling sync refresh from
		// commands like merge/rebase? Also can we have them hook launch terminate listeners and exit write of repo
		// then, rather than loop checking launch terminate status?
		IStatus result = repository.execute(GitRepository.ReadWrite.WRITE, "update-index", "-q", //$NON-NLS-1$ //$NON-NLS-2$
				"--unmerged", "--ignore-missing", "--refresh"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (result == null) // couldn't even execute!
		{
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), "Failed to execute git update-index"); //$NON-NLS-1$
		}
		if (!result.isOK())
		{
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), result.getMessage());
		}

		// FIXME Can we just do something like a "git status --porcelain" to grab all three at once and parse the
		// changed files out? It doesn't include sha/blob mode in that output, so I think we'd need to lazily get that
		// info later when it's needed in indexInfo() for unstageFiles
		Set<Job> jobs = new HashSet<Job>();
		jobs.add(new UntrackedFilesRefreshJob(this));
		jobs.add(new UnstagedFilesRefreshJob(this));
		jobs.add(new StagedFilesRefreshJob(this));

		// Copy the last full list of changed files we built up on refresh. Used to pass along the delta
		Collection<ChangedFile> preRefreshFiles;
		synchronized (this.changedFilesLock)
		{
			if (this.changedFiles != null)
			{
				preRefreshFiles = new ArrayList<ChangedFile>(this.changedFiles.size());
				for (ChangedFile file : this.changedFiles)
				{
					preRefreshFiles.add(new ChangedFile(file));
				}
			}
			else
			{
				preRefreshFiles = new ArrayList<ChangedFile>(0);
			}
		}
		// Now create a new temporary list so we can build it up...
		this.files = new Vector<ChangedFile>(preRefreshFiles.size());

		// Last chance to cancel...
		if (monitor != null && monitor.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		// Schedule all the jobs
		boolean setSystem = !EclipseUtil.showSystemJobs();
		for (Job toSchedule : jobs)
		{
			toSchedule.setSystem(setSystem);
			toSchedule.setPriority(Job.SHORT);
			toSchedule.schedule();
		}
		// Now wait for them to finish
		for (Job toJoin : jobs)
		{
			try
			{
				toJoin.join();
			}
			catch (InterruptedException e) // $codepro.audit.disable emptyCatchClause
			{
				// ignore
			}
		}

		// At this point, all index operations have finished.
		// We need to find all files that don't have either
		// staged or unstaged files, and delete them
		Collection<ChangedFile> toRefresh = new ArrayList<ChangedFile>(this.files);
		List<ChangedFile> deleteFiles = new ArrayList<ChangedFile>();
		for (ChangedFile file : this.files)
		{
			if (!file.hasStagedChanges && !file.hasUnstagedChanges)
			{
				deleteFiles.add(file);
			}
		}

		if (!deleteFiles.isEmpty())
		{
			for (ChangedFile file : deleteFiles)
			{
				this.files.remove(file);
			}
		}

		// Now make the "final" list a copy of the temporary one we were just building up
		synchronized (this.changedFilesLock)
		{
			this.changedFiles = new ArrayList<ChangedFile>(this.files.size());

			for (ChangedFile file : this.files)
			{
				this.changedFiles.add(new ChangedFile(file));
			}
		}
		// Don't hold onto temp list in memory!
		this.files = null;

		postIndexChange(preRefreshFiles, toRefresh);
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
		synchronized (this.changedFilesLock)
		{
			if (this.changedFiles == null)
			{
				// Don't want to call back to fireIndexChangeEvent yet!
				IStatus status = refresh(false, new NullProgressMonitor());
				if (!status.isOK())
				{
					IdeLog.logError(GitPlugin.getDefault(), status.getMessage());
					return Collections.emptyList();
				}
			}

			List<ChangedFile> copy = new ArrayList<ChangedFile>(this.changedFiles.size());
			for (ChangedFile file : this.changedFiles)
			{
				copy.add(new ChangedFile(file));
			}
			return copy;
		}
	}

	public boolean stageFiles(Collection<ChangedFile> stageFiles)
	{
		if (stageFiles == null || stageFiles.isEmpty())
		{
			return false;
		}

		List<String> args = new ArrayList<String>();
		args.add("update-index"); //$NON-NLS-1$
		args.add("--add"); //$NON-NLS-1$
		args.add("--remove"); //$NON-NLS-1$
		args.add("--stdin"); //$NON-NLS-1$
		StringBuffer input = new StringBuffer(stageFiles.size() * stageFiles.iterator().next().getPath().length());
		for (ChangedFile file : stageFiles)
		{
			input.append(file.getPath()).append('\n');
		}

		IStatus result = repository.executeWithInput(input.toString(), args.toArray(new String[args.size()]));
		if (result == null)
		{
			return false;
		}
		if (!result.isOK())
		{
			IdeLog.logError(GitPlugin.getDefault(), "Failed to stage files: " + result.getMessage(), IDebugScopes.DEBUG); //$NON-NLS-1$
			return false;
		}
		Collection<ChangedFile> preFiles = new ArrayList<ChangedFile>(stageFiles.size());
		for (ChangedFile file : stageFiles)
		{
			preFiles.add(new ChangedFile(file));
		}

		// Update the staged/unstaged flags in the passed in copy of changed files, and our internal list of changed
		// files.
		for (ChangedFile file : stageFiles)
		{
			int index = Collections.binarySearch(this.changedFiles, file);
			if (index >= 0)
			{
				synchronized (this.changedFilesLock)
				{
					ChangedFile orig = this.changedFiles.get(index);
					orig.hasUnstagedChanges = false;
					orig.hasStagedChanges = true;
				}
			}

			file.hasUnstagedChanges = false;
			file.hasStagedChanges = true;
		}

		postIndexChange(preFiles, stageFiles);
		return true;
	}

	public boolean unstageFiles(Collection<ChangedFile> unstageFiles)
	{
		if (unstageFiles == null || unstageFiles.isEmpty())
		{
			return false;
		}

		StringBuilder input = new StringBuilder();
		for (ChangedFile file : unstageFiles)
		{
			input.append(file.indexInfo());
		}

		IStatus result = repository.executeWithInput(input.toString(), "update-index", "-z", "--index-info"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (result == null)
		{
			return false;
		}
		if (!result.isOK())
		{
			IdeLog.logError(GitPlugin.getDefault(), "Failed to stage files: " + result.getMessage(), IDebugScopes.DEBUG); //$NON-NLS-1$
			return false;
		}

		Collection<ChangedFile> preFiles = new ArrayList<ChangedFile>(unstageFiles.size());
		for (ChangedFile file : unstageFiles)
		{
			preFiles.add(new ChangedFile(file));
		}

		// Update the staged/unstaged flags in the passed in copy of changed files, and our internal list of changed
		// files.
		for (ChangedFile file : unstageFiles)
		{
			int index = Collections.binarySearch(this.changedFiles, file);
			if (index >= 0)
			{
				synchronized (this.changedFilesLock)
				{
					ChangedFile orig = this.changedFiles.get(index);
					orig.hasUnstagedChanges = true;
					orig.hasStagedChanges = false;
				}
			}

			file.hasUnstagedChanges = true;
			file.hasStagedChanges = false;
		}

		postIndexChange(preFiles, unstageFiles);
		return true;
	}

	public void discardChangesForFiles(Collection<ChangedFile> discardFiles)
	{
		StringBuilder input = new StringBuilder();
		for (ChangedFile file : discardFiles)
		{
			input.append(file.getPath()).append("\0"); //$NON-NLS-1$
		}

		IStatus result = repository.executeWithInput(input.toString(),
				"checkout-index", "--index", "--quiet", "--force", "-z", "--stdin"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		if (result == null || !result.isOK())
		{
			// postOperationFailed("Discarding changes failed with return value " + ret);
			return;
		}
		Collection<ChangedFile> preFiles = new ArrayList<ChangedFile>(discardFiles.size());
		for (ChangedFile file : discardFiles)
		{
			preFiles.add(new ChangedFile(file));
		}
		for (ChangedFile file : discardFiles)
		{
			file.hasUnstagedChanges = false;
		}

		postIndexChange(preFiles, discardFiles);
	}

	public boolean commit(String commitMessage)
	{
		boolean success = doCommit(commitMessage);
		if (!success)
		{
			return false;
		}

		repository.hasChanged();

		if (amend)
		{
			this.amend = false;
		}
		else
		{
			refresh(new NullProgressMonitor()); // TODO Run async if we can!
		}
		return true;
	}

	private boolean doCommit(String commitMessage)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			commitMessage = commitMessage.replace("\"", "\\\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		IStatus result = repository.execute(GitRepository.ReadWrite.WRITE, "commit", "-m", commitMessage); //$NON-NLS-1$ //$NON-NLS-2$
		return result != null && result.isOK();
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
			String indexPath = ":0:" + file.path; //$NON-NLS-1$

			if (file.status == ChangedFile.Status.NEW)
			{
				IStatus status = repository.execute(GitRepository.ReadWrite.READ, "show", indexPath); //$NON-NLS-1$
				return status.getMessage();
			}

			IStatus result = repository.execute(GitRepository.ReadWrite.READ, "diff-index", parameter, "--cached", //$NON-NLS-1$ //$NON-NLS-2$
					GitRepository.HEAD, "--", file.path); //$NON-NLS-1$
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
				return IOUtil.read(new FileInputStream(workingDirectory().append(file.path).toFile()), "UTF-8"); //$NON-NLS-1$ // $codepro.audit.disable closeWhereCreated
			}
			catch (FileNotFoundException e)
			{
				return null;
			}
		}

		IStatus result = repository.execute(GitRepository.ReadWrite.READ, "diff-files", parameter, "--", file.path); //$NON-NLS-1$ //$NON-NLS-2$
		return result.getMessage();
	}

	public boolean hasBinaryAttributes(ChangedFile file)
	{
		IStatus result = repository.execute(GitRepository.ReadWrite.READ, "check-attr", "binary", file.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
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
			// try common filename-extensions
			for (String extension : BINARY_EXTENSIONS)
			{
				if (file.getPath().endsWith(extension))
				{
					return true;
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
		if (changedFiles == null || changedFiles.isEmpty())
		{
			return false;
		}

		IPath workingDirectory = repository.workingDirectory();
		IPath resourcePath = resource.getLocation();
		for (ChangedFile changedFile : changedFiles)
		{
			IPath fullPath = workingDirectory.append(changedFile.getPath()).makeAbsolute();
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
		if (changedFiles.isEmpty())
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
		for (ChangedFile changedFile : changedFiles)
		{
			IResource resource = getResourceForChangedFile(changedFile);
			if (resource != null)
			{
				resources.add(resource);
			}
		}
		return resources;
	}

	IFile getResourceForChangedFile(ChangedFile changedFile)
	{
		return ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(workingDirectory().append(changedFile.getPath()));
	}

	protected ChangedFile getChangedFileForResource(IResource resource)
	{
		if (resource == null || resource.getLocationURI() == null)
		{
			return null;
		}

		IPath resourcePath = resource.getLocation();
		List<ChangedFile> changedFiles = changedFiles();
		for (ChangedFile changedFile : changedFiles)
		{
			IPath fullPath = workingDirectory().append(changedFile.getPath());
			if (resourcePath.equals(fullPath))
			{
				return changedFile;
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
		if (changedFiles == null || changedFiles.isEmpty())
		{
			return Collections.emptyList();
		}

		IPath resourcePath = container.getLocation();
		List<ChangedFile> filtered = new ArrayList<ChangedFile>();
		IPath workingDirectory = repository.workingDirectory();
		for (ChangedFile changedFile : changedFiles)
		{
			IPath fullPath = workingDirectory.append(changedFile.getPath()).makeAbsolute();
			if (resourcePath.isPrefixOf(fullPath))
			{
				filtered.add(changedFile);
			}
		}

		return filtered;
	}

	private abstract static class FilesRefreshJob extends Job
	{
		protected GitRepository repo;
		protected GitIndex index;

		private FilesRefreshJob(String name, GitIndex index)
		{
			super(name);
			this.index = index;
			this.repo = index.repository;
		}

		protected List<String> linesFromNotification(String string)
		{
			// FIXME: throw an error?
			if (string == null)
			{
				return Collections.emptyList();
			}

			// Strip trailing null
			if (string.endsWith("\0")) //$NON-NLS-1$
			{
				string = string.substring(0, string.length() - 1);
			}

			if (string.length() == 0)
			{
				return Collections.emptyList();
			}

			return StringUtil.tokenize(string, "\0"); //$NON-NLS-1$
		}

		protected Map<String, List<String>> dictionaryForLines(List<String> lines)
		{
			Map<String, List<String>> dictionary = new HashMap<String, List<String>>(lines.size() / 2);

			// Fill the dictionary with the new information. These lines are in the form of:
			// :00000 :0644 OTHER INDEX INFORMATION
			// Filename
			Assert.isTrue(lines.size() % 2 == 0, "Lines must have an even number of lines: " + lines); //$NON-NLS-1$
			Iterator<String> iter = lines.iterator();
			while (iter.hasNext())
			{
				String fileStatus = iter.next();
				String fileName = iter.next();
				dictionary.put(fileName, StringUtil.tokenize(fileStatus, " ")); //$NON-NLS-1$
			}

			return dictionary;
		}

		protected void addFilesFromDictionary(Map<String, List<String>> dictionary, boolean staged, boolean tracked)
		{
			if (index.files == null)
			{
				return;
			}
			// Iterate over all existing files
			synchronized (index.files)
			{
				for (ChangedFile file : index.files)
				{
					synchronized (dictionary)
					{
						List<String> fileStatus = dictionary.get(file.path);
						// Object found, this is still a cached / uncached thing
						if (fileStatus != null)
						{
							if (tracked)
							{
								String mode = fileStatus.get(0).substring(1);
								String sha = fileStatus.get(2);
								file.commitBlobSHA = sha;
								file.commitBlobMode = mode;

								if (staged)
								{
									file.hasStagedChanges = true;
								}
								else
								{
									file.hasUnstagedChanges = true;
								}
								if (fileStatus.get(4).equals("D")) //$NON-NLS-1$
								{
									file.status = ChangedFile.Status.DELETED;
								}
								else if (fileStatus.get(4).equals("U")) //$NON-NLS-1$
								{
									file.status = ChangedFile.Status.UNMERGED;
								}
							}
							else
							{
								// Untracked file, set status to NEW, only unstaged changes
								file.hasStagedChanges = false;
								file.hasUnstagedChanges = true;
								file.status = ChangedFile.Status.NEW;
							}

							// We handled this file, remove it from the dictionary
							dictionary.remove(file.path);
						}
						else
						{
							// Object not found in the dictionary, so let's reset its appropriate
							// change (stage or untracked) if necessary.

							// Staged dictionary, so file does not have staged changes
							if (staged)
							{
								file.hasStagedChanges = false;
							}
							// Tracked file does not have unstaged changes, file is not new,
							// so we can set it to No. (If it would be new, it would not
							// be in this dictionary, but in the "other dictionary").
							else if (tracked && file.status != ChangedFile.Status.NEW)
							{
								file.hasUnstagedChanges = false;
							}
							// Unstaged, untracked dictionary ("Other" files), and file
							// is indicated as new (which would be untracked), so let's
							// remove it
							else if (!tracked && file.status == ChangedFile.Status.NEW)
							{
								file.hasUnstagedChanges = false;
							}
						}
					}
				}
			}
			// Do new files only if necessary
			if (dictionary.isEmpty())
			{
				return;
			}

			// All entries left in the dictionary haven't been accounted for
			// above, so we need to add them to the "files" array
			synchronized (dictionary)
			{
				for (String path : dictionary.keySet())
				{
					List<String> fileStatus = dictionary.get(path);

					ChangedFile.Status status = ChangedFile.Status.MODIFIED;
					if (fileStatus.get(4).equals("D")) //$NON-NLS-1$
					{
						status = ChangedFile.Status.DELETED;
					}
					else if (fileStatus.get(4).equals("U")) //$NON-NLS-1$
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

					ChangedFile file = new ChangedFile(path, status);
					if (tracked)
					{
						file.commitBlobMode = fileStatus.get(0).substring(1);
						file.commitBlobSHA = fileStatus.get(2);
					}

					file.hasStagedChanges = staged;
					file.hasUnstagedChanges = !staged;
					synchronized (index.files)
					{
						index.files.add(file);
					}
				}
			}
		}

	}

	private static final class StagedFilesRefreshJob extends FilesRefreshJob
	{
		private StagedFilesRefreshJob(GitIndex index)
		{
			super("staged files", index); //$NON-NLS-1$
		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			// HEAD vs filesystem
			IStatus result = repo.execute(GitRepository.ReadWrite.READ, "diff-index", "--cached", //$NON-NLS-1$ //$NON-NLS-2$
					"-z", GitRepository.HEAD); //$NON-NLS-1$
			if (result != null && result.isOK())
			{
				readStagedFiles(result.getMessage());
			}
			return Status.OK_STATUS;
		}

		private void readStagedFiles(String string)
		{
			List<String> lines = linesFromNotification(string);
			Map<String, List<String>> dic = dictionaryForLines(lines);
			addFilesFromDictionary(dic, true, true);
		}
	}

	private static final class UnstagedFilesRefreshJob extends FilesRefreshJob
	{
		private UnstagedFilesRefreshJob(GitIndex index)
		{
			super("unstaged files", index); //$NON-NLS-1$
		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			// index vs filesystem
			IStatus result = repo.execute(GitRepository.ReadWrite.READ, "diff-files", "-z"); //$NON-NLS-1$ //$NON-NLS-2$
			if (result != null && result.isOK())
			{
				readUnstagedFiles(result.getMessage());
			}
			return Status.OK_STATUS;
		}

		private void readUnstagedFiles(String string)
		{
			List<String> lines = linesFromNotification(string);
			Map<String, List<String>> dic = dictionaryForLines(lines);
			addFilesFromDictionary(dic, false, true);
		}
	}

	private static final class UntrackedFilesRefreshJob extends FilesRefreshJob
	{
		private UntrackedFilesRefreshJob(GitIndex index)
		{
			super("untracked files", index); //$NON-NLS-1$
		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			// index vs working tree (HEAD?)
			IStatus result = repo.execute(GitRepository.ReadWrite.READ, "ls-files", "--others", //$NON-NLS-1$ //$NON-NLS-2$
					"--exclude-standard", "-z"); //$NON-NLS-1$ //$NON-NLS-2$
			if (result != null && result.isOK())
			{
				readOtherFiles(result.getMessage());
			}
			return Status.OK_STATUS;
		}

		private void readOtherFiles(String string)
		{
			List<String> lines = linesFromNotification(string);
			Map<String, List<String>> dictionary = new HashMap<String, List<String>>(lines.size());
			// Other files are untracked, so we don't have any real index information. Instead, we can just fake it.
			// The line below is not used at all, as for these files the commitBlob isn't set
			List<String> fileStatus = new ArrayList<String>();
			fileStatus.add(":000000"); // for new file //$NON-NLS-1$
			fileStatus.add("100644"); //$NON-NLS-1$
			fileStatus.add("0000000000000000000000000000000000000000"); // SHA //$NON-NLS-1$
			fileStatus.add("0000000000000000000000000000000000000000"); //$NON-NLS-1$
			fileStatus.add("A"); // A for Add, D for delete //$NON-NLS-1$
			fileStatus.add(null);
			for (String path : lines)
			{
				if (path.length() == 0)
				{
					continue;
				}
				dictionary.put(path, fileStatus);
			}

			addFilesFromDictionary(dictionary, false, false);
		}
	}
}
