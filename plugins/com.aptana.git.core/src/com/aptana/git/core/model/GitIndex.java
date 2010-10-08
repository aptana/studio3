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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;

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
	private IPath workingDirectory;

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
	private List<ChangedFile> changedFiles;

	private int refreshStatus = 0;
	private boolean notify;

	private Job indexRefreshJob;

	GitIndex(GitRepository repository, IPath workingDirectory)
	{
		super();

		Assert.isNotNull(repository, "GitIndex requires a repository"); //$NON-NLS-1$
		Assert.isNotNull(workingDirectory, "GitIndex requires a working directory"); //$NON-NLS-1$

		this.repository = repository;
		this.workingDirectory = workingDirectory;
		this.changedFiles = new ArrayList<ChangedFile>();
	}

	/**
	 * Used by callers who don't need to wait for it to finish so we can squash together repeated calls when they come
	 * rapid-fire.
	 */
	public void refreshAsync()
	{
		if (indexRefreshJob == null)
		{
			indexRefreshJob = new Job("Refreshing git index") //$NON-NLS-1$
			{
				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					if (monitor != null && monitor.isCanceled())
						return Status.CANCEL_STATUS;
					refresh(monitor);
					return Status.OK_STATUS;
				}
			};
			indexRefreshJob.setSystem(true);
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

	synchronized IStatus refresh(boolean notify, IProgressMonitor monitor)
	{
		if (monitor != null && monitor.isCanceled())
			return Status.CANCEL_STATUS;
		this.notify = notify;
		refreshStatus = 0;

		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory, "update-index", "-q", //$NON-NLS-1$ //$NON-NLS-2$
				"--unmerged", "--ignore-missing", "--refresh"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (result == null) // couldn't even execute!
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), "Failed to execute git update-index"); //$NON-NLS-1$
		int exitValue = result.keySet().iterator().next();
		if (exitValue != 0)
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), result.values().iterator().next());

		Set<Job> jobs = new HashSet<Job>();
		jobs.add(new Job("other files") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory,
						"ls-files", "--others", //$NON-NLS-1$ //$NON-NLS-2$
						"--exclude-standard", "-z"); //$NON-NLS-1$ //$NON-NLS-2$
				if (result != null && result.keySet().iterator().next() == 0)
				{
					readOtherFiles(result.values().iterator().next());
				}
				return Status.OK_STATUS;
			}
		});
		jobs.add(new Job("unstaged files") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory,
						"diff-files", "-z"); //$NON-NLS-1$ //$NON-NLS-2$
				if (result != null && result.keySet().iterator().next() == 0)
				{
					readUnstagedFiles(result.values().iterator().next());
				}
				return Status.OK_STATUS;
			}
		});
		jobs.add(new Job("staged files") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory,
						"diff-index", "--cached", //$NON-NLS-1$ //$NON-NLS-2$
						"-z", getParentTree()); //$NON-NLS-1$
				if (result != null && result.keySet().iterator().next() == 0)
				{
					readStagedFiles(result.values().iterator().next());
				}
				return Status.OK_STATUS;
			}
		});
		// Last chance to cancel...
		if (monitor != null && monitor.isCanceled())
			return Status.CANCEL_STATUS;

		// Copy the last full list of changed files we built up on refresh. Used to pass along the delta
		Collection<ChangedFile> preRefreshFiles = new ArrayList<ChangedFile>(this.changedFiles.size());
		for (ChangedFile file : this.changedFiles)
		{
			preRefreshFiles.add(new ChangedFile(file));
		}
		// Now create a new temporary list so we can built it up...
		this.files = new Vector<ChangedFile>();

		// Schedule all the jobs
		for (Job toSchedule : jobs)
		{
			refreshStatus++;
			toSchedule.setSystem(true);
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
			catch (InterruptedException e)
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
				deleteFiles.add(file);
		}

		if (!deleteFiles.isEmpty())
		{
			for (ChangedFile file : deleteFiles)
				files.remove(file);
		}

		// Now make the "final" list a copy of the temporary one we were just building up
		synchronized (changedFiles)
		{
			changedFiles.clear();
			for (ChangedFile file : this.files)
			{
				changedFiles.add(new ChangedFile(file));
			}
		}
		// Don't hold onto temp list in memory!
		this.files = null;

		postIndexChange(preRefreshFiles, toRefresh);

		return Status.OK_STATUS;
	}

	private String getParentTree()
	{
		String parent = amend ? "HEAD^" : "HEAD"; //$NON-NLS-1$ //$NON-NLS-2$

		if (repository.parseReference(parent) == null)
			// We don't have a head ref. Return the empty tree.
			return "4b825dc642cb6eb9a060e54bf8d69288fbee4904"; //$NON-NLS-1$

		return parent;
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
				continue;
			dictionary.put(path, fileStatus);
		}

		addFilesFromDictionary(dictionary, false, false);
	}

	private void readStagedFiles(String string)
	{
		List<String> lines = linesFromNotification(string);
		Map<String, List<String>> dic = dictionaryForLines(lines);
		addFilesFromDictionary(dic, true, true);
	}

	private void readUnstagedFiles(String string)
	{
		List<String> lines = linesFromNotification(string);
		Map<String, List<String>> dic = dictionaryForLines(lines);
		addFilesFromDictionary(dic, false, true);
	}

	List<String> linesFromNotification(String string)
	{
		// FIXME: throw an error?
		if (string == null)
			return Collections.emptyList();

		// Strip trailing null
		if (string.endsWith("\0")) //$NON-NLS-1$
			string = string.substring(0, string.length() - 1);

		if (string.length() == 0)
			return Collections.emptyList();

		return StringUtil.tokenize(string, "\0"); //$NON-NLS-1$
	}

	private Map<String, List<String>> dictionaryForLines(List<String> lines)
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

	private void addFilesFromDictionary(Map<String, List<String>> dictionary, boolean staged, boolean tracked)
	{
		if (this.files == null)
		{
			return;
		}
		// Iterate over all existing files
		synchronized (this.files)
		{
			for (ChangedFile file : this.files)
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
								file.hasStagedChanges = true;
							else
								file.hasUnstagedChanges = true;
							if (fileStatus.get(4).equals("D")) //$NON-NLS-1$
								file.status = ChangedFile.Status.DELETED;
							else if (fileStatus.get(4).equals("U")) //$NON-NLS-1$
								file.status = ChangedFile.Status.UNMERGED;
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
							file.hasStagedChanges = false;
						// Tracked file does not have unstaged changes, file is not new,
						// so we can set it to No. (If it would be new, it would not
						// be in this dictionary, but in the "other dictionary").
						else if (tracked && file.status != ChangedFile.Status.NEW)
							file.hasUnstagedChanges = false;
						// Unstaged, untracked dictionary ("Other" files), and file
						// is indicated as new (which would be untracked), so let's
						// remove it
						else if (!tracked && file.status == ChangedFile.Status.NEW)
							file.hasUnstagedChanges = false;
					}
				}
			}
		}
		// Do new files only if necessary
		if (dictionary.isEmpty())
			return;

		// All entries left in the dictionary haven't been accounted for
		// above, so we need to add them to the "files" array
		synchronized (dictionary)
		{
			for (String path : dictionary.keySet())
			{
				List<String> fileStatus = dictionary.get(path);

				ChangedFile file = new ChangedFile(path);
				if (fileStatus.get(4).equals("D")) //$NON-NLS-1$
					file.status = ChangedFile.Status.DELETED;
				else if (fileStatus.get(4).equals("U")) //$NON-NLS-1$
					file.status = ChangedFile.Status.UNMERGED;
				else if (fileStatus.get(0).equals(":000000")) //$NON-NLS-1$
					file.status = ChangedFile.Status.NEW;
				else
					file.status = ChangedFile.Status.MODIFIED;

				if (tracked)
				{
					file.commitBlobMode = fileStatus.get(0).substring(1);
					file.commitBlobSHA = fileStatus.get(2);
				}

				file.hasStagedChanges = staged;
				file.hasUnstagedChanges = !staged;
				synchronized (this.files)
				{
					this.files.add(file);
				}
			}
		}
	}

	private void postIndexChange(Collection<ChangedFile> preChangeFiles, Collection<ChangedFile> postChangeFiles)
	{
		if (this.notify)
			this.repository.fireIndexChangeEvent(preChangeFiles, postChangeFiles);
		else
			this.notify = true;
	}

	/**
	 * Makes a copy of the internal list of changed files so that iterating won't ever result in a
	 * ConcurrentModificationException. try to avoid use if possible, since a deep copy is made which can be expensive.
	 * 
	 * @return
	 */
	public List<ChangedFile> changedFiles()
	{
		synchronized (changedFiles)
		{
			List<ChangedFile> copy = new ArrayList<ChangedFile>(changedFiles.size());
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
			return false;

		List<String> args = new ArrayList<String>();
		args.add("update-index"); //$NON-NLS-1$
		args.add("--add"); //$NON-NLS-1$
		args.add("--remove"); //$NON-NLS-1$
		args.add("--stdin"); //$NON-NLS-1$
		StringBuffer input = new StringBuffer(stageFiles.size()*stageFiles.iterator().next().getPath().length());
		for (ChangedFile file : stageFiles)
		{
			input.append(file.getPath()).append('\n');
		}

		Map<Integer, String> result = GitExecutable.instance().runInBackground(input.toString(), workingDirectory,
				args.toArray(new String[args.size()]));
		if (result == null)
			return false;

		int ret = result.keySet().iterator().next();
		if (ret != 0)
		{
			GitPlugin.logError("Failed to stage files: " + result.values().iterator().next(), null); //$NON-NLS-1$
			return false;
		}
		Collection<ChangedFile> preFiles = new ArrayList<ChangedFile>(stageFiles.size());
		for (ChangedFile file : stageFiles)
		{
			preFiles.add(new ChangedFile(file));
		}
		for (ChangedFile file : stageFiles)
		{
			file.hasUnstagedChanges = false;
			file.hasStagedChanges = true;
		}

		postIndexChange(preFiles, stageFiles);
		return true;
	}

	public boolean unstageFiles(Collection<ChangedFile> unstageFiles)
	{
		if (unstageFiles == null || unstageFiles.isEmpty())
			return false;

		StringBuilder input = new StringBuilder();
		for (ChangedFile file : unstageFiles)
		{
			input.append(file.indexInfo());
		}

		Map<Integer, String> result = GitExecutable.instance().runInBackground(input.toString(),
				workingDirectory, new String[] { "update-index", "-z", "--index-info" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (result == null)
			return false;

		int ret = result.keySet().iterator().next();
		if (ret != 0)
		{
			GitPlugin.logError("Failed to stage files: " + result.values().iterator().next(), null); //$NON-NLS-1$
			return false;
		}

		Collection<ChangedFile> preFiles = new ArrayList<ChangedFile>(unstageFiles.size());
		for (ChangedFile file : unstageFiles)
		{
			preFiles.add(new ChangedFile(file));
		}
		for (ChangedFile file : unstageFiles)
		{
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

		String[] arguments = new String[] { "checkout-index", "--index", "--quiet", "--force", "-z", "--stdin" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

		int ret = 1;
		Map<Integer, String> result = GitExecutable.instance().runInBackground(input.toString(),
				workingDirectory, arguments);
		ret = result.keySet().iterator().next();

		if (ret != 0)
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
			file.hasUnstagedChanges = false;

		postIndexChange(preFiles, discardFiles);
	}

	public boolean commit(String commitMessage)
	{
		boolean success = doCommit(commitMessage);
		if (!success)
			return false;

		repository.hasChanged();

		if (amend)
			this.amend = false;
		else
			refresh(new NullProgressMonitor()); // TODO Run async if we can!
		return true;
	}

	private boolean doCommit(String commitMessage)
	{
		int exitCode = 1;
		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory, "commit", "-m", commitMessage);
		if (result != null && !result.isEmpty())
		{
			exitCode = result.keySet().iterator().next();
		}
		return exitCode == 0;
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
		String result = GitExecutable.instance().outputForCommand(workingDirectory, "log", "--pretty=format:\"%s\"", //$NON-NLS-1$ //$NON-NLS-2$
				sha1 + ".." + sha2); //$NON-NLS-1$
		if (result == null || result.trim().length() == 0)
			return new String[0];
		return result.split("[\r\n]+"); //$NON-NLS-1$
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
			return Messages.GitIndex_BinaryDiff_Message;

		String parameter = "-U" + contextLines; //$NON-NLS-1$
		if (staged)
		{
			String indexPath = ":0:" + file.path; //$NON-NLS-1$

			if (file.status == ChangedFile.Status.NEW)
				return GitExecutable.instance().outputForCommand(workingDirectory, "show", indexPath); //$NON-NLS-1$

			return GitExecutable.instance().outputForCommand(workingDirectory, "diff-index", parameter, "--cached", //$NON-NLS-1$ //$NON-NLS-2$
					getParentTree(), "--", file.path); //$NON-NLS-1$
		}

		// unstaged
		if (file.status == ChangedFile.Status.NEW)
		{
			try
			{
				return ProcessUtil.read(new FileInputStream(workingDirectory.append(file.path).toFile()));
			}
			catch (FileNotFoundException e)
			{
				return null;
			}
		}

		return GitExecutable.instance().outputForCommand(workingDirectory, "diff-files", parameter, "--", file.path); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean hasBinaryAttributes(ChangedFile file)
	{
		String output = GitExecutable.instance().outputForCommand(workingDirectory, "check-attr", "binary", //$NON-NLS-1$ //$NON-NLS-2$
				file.getPath());
		output = output.trim();
		if (output.endsWith("binary: set")) //$NON-NLS-1$
			return true;
		if (output.endsWith("binary: unset")) //$NON-NLS-1$
			return false;
		if (output.endsWith("binary: unspecified")) //$NON-NLS-1$
		{
			// try common filename-extensions
			for (String extension : BINARY_EXTENSIONS)
			{
				if (file.getPath().endsWith(extension))
					return true;
			}
		}
		return false;
	}

	/**
	 * For use in telling if a given resource is a changed file, or is a folder containing changes underneath it.
	 * 
	 * @param resource
	 * @param changedFiles
	 * @return
	 */
	public boolean resourceOrChildHasChanges(IResource resource)
	{
		synchronized (changedFiles)
		{
			if (changedFiles == null || changedFiles.isEmpty())
				return false;

			IPath workingDirectory = repository.workingDirectory();
			IPath resourcePath = resource.getLocation();
			for (ChangedFile changedFile : changedFiles)
			{
				IPath fullPath = workingDirectory.append(changedFile.getPath()).makeAbsolute();
				if (resourcePath.isPrefixOf(fullPath))
					return true;
			}
		}
		return false;
	}

	public boolean isDirty()
	{
		synchronized (changedFiles)
		{
			return !changedFiles.isEmpty();
		}
	}

	public boolean hasUnresolvedMergeConflicts()
	{
		synchronized (changedFiles)
		{
			if (changedFiles.isEmpty())
				return false;
			for (ChangedFile changedFile : changedFiles)
			{
				if (changedFile.hasUnmergedChanges() && changedFile.hasUnstagedChanges())
					return true;
			}
			return false;
		}
	}

	public Set<IResource> getChangedResources()
	{
		Set<IResource> resources = new HashSet<IResource>();
		synchronized (changedFiles)
		{
			for (ChangedFile changedFile : changedFiles)
			{
				IResource resource = getResourceForChangedFile(changedFile);
				if (resource != null)
					resources.add(resource);
			}
		}
		return resources;
	}

	IFile getResourceForChangedFile(ChangedFile changedFile)
	{
		return ResourcesPlugin.getWorkspace().getRoot()
		.getFileForLocation(workingDirectory.append(changedFile.getPath()));
	}

	public ChangedFile getChangedFileForResource(IResource resource)
	{
		if (resource == null || resource.getLocationURI() == null)
			return null;
		IPath resourcePath = resource.getLocation();
		synchronized (changedFiles)
		{
			for (ChangedFile changedFile : changedFiles)
			{
				IPath fullPath = workingDirectory.append(changedFile.getPath());
				if (resourcePath.equals(fullPath))
				{
					return changedFile;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the list of changed files that are underneath the given container.
	 * 
	 * @param container
	 * @return
	 */
	public List<ChangedFile> getChangedFilesForContainer(IContainer container)
	{
		if (container == null || container.getLocationURI() == null)
			return Collections.emptyList();

		IPath resourcePath = container.getLocation();
		List<ChangedFile> filtered = new ArrayList<ChangedFile>();
		IPath workingDirectory = repository.workingDirectory();

		synchronized (changedFiles)
		{
			if (changedFiles == null || changedFiles.isEmpty())
				return Collections.emptyList();
			for (ChangedFile changedFile : changedFiles)
			{
				IPath fullPath = workingDirectory.append(changedFile.getPath()).makeAbsolute();
				if (resourcePath.isPrefixOf(fullPath))
					filtered.add(changedFile);
			}
		}
		return filtered;
	}

	/**
	 * Find the changed file that corresponds to the repo relative path argument.
	 * 
	 * @param path
	 * @return
	 */
	public ChangedFile findChangedFile(String path)
	{
		synchronized (changedFiles)
		{
			for (ChangedFile changedFile : changedFiles)
			{
				if (changedFile.getPath().equals(path))
				{
					return changedFile;
				}
			}
		}
		return null;
	}
}
