package com.aptana.git.core.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyAdapter;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.filewatcher.FileWatcher;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;
import com.aptana.git.core.model.GitRef.TYPE;

public class GitRepository
{

	private static final String COMMIT_EDITMSG = "COMMIT_EDITMSG"; //$NON-NLS-1$
	private static final String INDEX = "index"; //$NON-NLS-1$
	static final String MERGE_HEAD_FILENAME = "MERGE_HEAD"; //$NON-NLS-1$
	private static final String COMMIT_FILE_ENCODING = "UTF-8"; //$NON-NLS-1$
	private static final String HEAD = "HEAD"; //$NON-NLS-1$

	public static final String GIT_DIR = ".git"; //$NON-NLS-1$

	private List<GitRevSpecifier> branches;
	Map<String, List<GitRef>> refs;
	private URI fileURL;
	private GitRevSpecifier _headRef;
	private GitIndex index;
	private boolean hasChanged;
	GitRevSpecifier currentBranch;
	private Set<Integer> fileWatcherIds = new HashSet<Integer>();
	private int remoteDirCreationWatchId = -1;
	private HashSet<IGitRepositoryListener> listeners;

	GitRepository(URI fileURL)
	{
		this.fileURL = fileURL;
		this.branches = new ArrayList<GitRevSpecifier>();
		reloadRefs();
		readCurrentBranch();
		try
		{
			// FIXME When actions are taken through our model/UI we end up causing multiple refreshes for index changes
			// index appears to change on commit/stage/unstage/pull
			// Add listener for changes in HEAD (i.e. switched branches), and index
			fileWatcherIds.add(FileWatcher.addWatch(gitDirPath().toOSString(), IJNotify.FILE_ANY, false,
					new JNotifyAdapter()
					{

						private Set<String> filesToWatch;

						@Override
						public void fileRenamed(int wd, String rootPath, String oldName, String newName)
						{
							if (newName == null || !filesToWatch().contains(newName))
								return;
							if (newName.equals(HEAD))
								checkForBranchChange();
							else if (newName.equals(INDEX) || newName.equals(COMMIT_EDITMSG))
								refreshIndex();
						}

						@Override
						public void fileCreated(int wd, String rootPath, String name)
						{
							if (name != null && name.equals(INDEX))
								refreshIndex();
						}

						@Override
						public void fileDeleted(int wd, String rootPath, String name)
						{
							if (name != null && name.equals(INDEX))
								refreshIndex();
						}

						private Set<String> filesToWatch()
						{
							if (filesToWatch == null)
							{
								filesToWatch = new HashSet<String>();
								filesToWatch.add(HEAD);
								filesToWatch.add(INDEX);
								filesToWatch.add(COMMIT_EDITMSG);
							}
							return filesToWatch;
						}

						@Override
						public void fileModified(int wd, String rootPath, String name)
						{
							if (name == null || !filesToWatch().contains(name))
								return;
							if (name.equals(HEAD))
								checkForBranchChange();
							else if (name.equals(INDEX) || name.equals(COMMIT_EDITMSG))
								refreshIndex();
						}

						// Do long running work in another thread/job so we don't tie up the jnotify locks!
						private void refreshIndex()
						{
							index().refreshAsync();
						}

						protected void checkForBranchChange()
						{
							Job job = new Job("Checking for current branch switch") //$NON-NLS-1$
							{
								@Override
								protected IStatus run(IProgressMonitor monitor)
								{
									String oldBranchName = currentBranch.simpleRef().shortName();
									_headRef = null;
									readCurrentBranch();
									String newBranchName = currentBranch.simpleRef().shortName();
									if (oldBranchName.equals(newBranchName))
										return Status.OK_STATUS;
									fireBranchChangeEvent(oldBranchName, newBranchName);
									return Status.OK_STATUS;
								}
							};
							job.setSystem(true);
							job.schedule();
						}
					}));

			// Add listener for remotes
			if (gitFile(GitRef.REFS_REMOTES).isDirectory())
			{
				addRemotesFileWatcher();
			}
			else
			{
				// If refs/remote doesn't exist, we need to add a listener on "refs" for creation of remotes!
				remoteDirCreationWatchId = FileWatcher.addWatch(gitFile(GitRef.REFS).getAbsolutePath(),
						IJNotify.FILE_CREATED, false, new JNotifyAdapter()
						{
							public void fileCreated(int wd, String rootPath, String name)
							{
								if (name != null && name.equals("remotes")) //$NON-NLS-1$
								{
									try
									{
										addRemotesFileWatcher();
									}
									catch (JNotifyException e)
									{
										GitPlugin.logError(e.getMessage(), e);
									}

									if (remoteDirCreationWatchId == -1)
										return;
									// Remove this watcher!
									Job job = new Job("Removing file watcher on remotes dir creation") //$NON-NLS-1$
									{
										@Override
										protected IStatus run(IProgressMonitor monitor)
										{
											try
											{
												FileWatcher.removeWatch(remoteDirCreationWatchId);
											}
											catch (JNotifyException e)
											{
												GitPlugin.logError(e.getMessage(), e);
											}
											return Status.OK_STATUS;
										}
									};
									job.setSystem(true);
									job.schedule();
								}
							}
						});
				fileWatcherIds.add(remoteDirCreationWatchId);
			}

			// Add listener for added/removed branches
			fileWatcherIds.add(FileWatcher.addWatch(gitFile(GitRef.REFS_HEADS).getAbsolutePath(), IJNotify.FILE_CREATED
					| IJNotify.FILE_DELETED, false, new JNotifyAdapter()
			{

				@Override
				public void fileDeleted(int wd, String rootPath, final String name)
				{
					// Remove branch in model!
					final GitRevSpecifier rev = new GitRevSpecifier(GitRef.refFromString(GitRef.REFS_HEADS + name));
					branches.remove(rev);

					Job job = new Job("Handle branch removal") //$NON-NLS-1$
					{
						@Override
						protected IStatus run(IProgressMonitor monitor)
						{
							// the branch may in fact still exists
							reloadRefs();
							// only fires the event if the branch is indeed removed
							if (!branches.contains(rev))
							{
								fireBranchRemovedEvent(name);
							}
							return Status.OK_STATUS;
						}
					};
					job.setSystem(true);
					job.schedule();
				}

				@Override
				public void fileCreated(int wd, String rootPath, final String name)
				{
					// a branch has been added
					addBranch(new GitRevSpecifier(GitRef.refFromString(GitRef.REFS_HEADS + name)));

					Job job = new Job("Checking if HEAD changed") //$NON-NLS-1$
					{
						@Override
						protected IStatus run(IProgressMonitor monitor)
						{
							fireBranchAddedEvent(name);
							// Check if our HEAD changed
							String oldBranchName = currentBranch.simpleRef().shortName();
							if (oldBranchName.equals(name))
								return Status.OK_STATUS;
							_headRef = null;
							readCurrentBranch();
							fireBranchChangeEvent(oldBranchName, name);
							return Status.OK_STATUS;
						}
					};
					job.setSystem(true);
					job.schedule();
				}
			}));
		}
		catch (JNotifyException e)
		{
			GitPlugin.logError(e.getMessage(), e);
		}
	}

	/**
	 * @throws JNotifyException
	 */
	protected void addRemotesFileWatcher() throws JNotifyException
	{
		fileWatcherIds.add(FileWatcher.addWatch(gitFile(GitRef.REFS_REMOTES).getAbsolutePath(), IJNotify.FILE_ANY,
				true, new JNotifyListener()
				{

					@Override
					public void fileRenamed(int wd, String rootPath, String oldName, String newName)
					{
						if (newName == null)
							return;
						if (isProbablyBranch(newName))
						{
							// FIXME Can't tell if we pushed or pulled unless we look at sha tree/commit list. For
							// now,
							// seems harmless to fire both.
							Job job = new Job("Firing pull event") //$NON-NLS-1$
							{
								@Override
								protected IStatus run(IProgressMonitor monitor)
								{
									firePullEvent();
									firePushEvent();
									return Status.OK_STATUS;
								}
							};
							job.setSystem(true);
							job.schedule();
						}
					}

					// Determine if filename is referring to a remote branch, and not the remote itself.
					private boolean isProbablyBranch(String newName)
					{
						return newName != null && newName.indexOf(File.separator) != -1;
					}

					@Override
					public void fileModified(int wd, String rootPath, String name)
					{
						if (name == null)
							return;
						if (isProbablyBranch(name))
						{
							// FIXME Can't tell if we pushed or pulled unless we look at sha tree/commit list. For
							// now,
							// seems harmless to fire both.
							Job job = new Job("Firing pull event") //$NON-NLS-1$
							{
								@Override
								protected IStatus run(IProgressMonitor monitor)
								{
									firePullEvent();
									firePushEvent();
									return Status.OK_STATUS;
								}
							};
							job.setSystem(true);
							job.schedule();
						}
					}

					@Override
					public void fileDeleted(int wd, String rootPath, final String name)
					{
						// if path is longer than one segment, then remote branch was deleted. Means we probably
						// pulled.
						if (isProbablyBranch(name))
						{
							branches.remove(new GitRevSpecifier(GitRef.refFromString(GitRef.REFS_REMOTES + name)));
							Job job = new Job("Firing pull event") //$NON-NLS-1$
							{
								@Override
								protected IStatus run(IProgressMonitor monitor)
								{
									fireBranchRemovedEvent(name);
									firePullEvent();
									return Status.OK_STATUS;
								}
							};
							job.setSystem(true);
							job.schedule();
						}
					}

					@Override
					public void fileCreated(int wd, String rootPath, final String name)
					{
						if (isProbablyBranch(name))
						{
							// if path is longer than one segment, then remote branch was created.
							addBranch(new GitRevSpecifier(GitRef.refFromString(GitRef.REFS_REMOTES + name)));
							// Since we suddenly know about a new remote branch, we probably pulled.

							Job job = new Job("Firing pull event") //$NON-NLS-1$
							{
								@Override
								protected IStatus run(IProgressMonitor monitor)
								{
									fireBranchAddedEvent(name);
									firePullEvent();
									return Status.OK_STATUS;
								}
							};
							job.setSystem(true);
							job.schedule();
						}
					}
				}));
	}

	public IPath workingDirectory()
	{
		if (gitDirPath().lastSegment().equals(GIT_DIR))
			return gitDirPath().removeLastSegments(1);
		else if (GitExecutable.instance().outputForCommand(gitDirPath(), "rev-parse", "--is-inside-work-tree") //$NON-NLS-1$ //$NON-NLS-2$
				.equals("true")) //$NON-NLS-1$
			return GitExecutable.instance().path(); // FIXME This doesn't seem right....

		return null;
	}

	/**
	 * Returns the set of local branches that this repo knows about.
	 * 
	 * @return
	 */
	public Set<String> localBranches()
	{
		return branches(GitRef.TYPE.HEAD);
	}

	/**
	 * Returns the set of remote branches that this repo knows about.
	 * 
	 * @return
	 */
	public Set<String> remoteBranches()
	{
		return branches(GitRef.TYPE.REMOTE);
	}

	/**
	 * Returns the set of remotes attached to this repository. 'git remote'
	 * 
	 * @return
	 */
	public Set<String> remotes()
	{
		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory(), "remote"); //$NON-NLS-1$
		int exitValue = result.keySet().iterator().next();
		if (exitValue != 0)
			return Collections.emptySet();
		String output = result.values().iterator().next();
		String[] lines = output.split("\r\n|\r|\n"); //$NON-NLS-1$
		Set<String> set = new HashSet<String>();
		for (String line : lines)
		{
			set.add(line);
		}
		return set;
	}

	/**
	 * Returns a sorted set of local and remote branches known to this repo. Orders local branches before remote
	 * branches.
	 * 
	 * @return
	 */
	public Set<String> allBranches()
	{
		// Return local branches first!
		SortedSet<String> localFirst = new TreeSet<String>(new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				if (o1.contains("/") && !o2.contains("/")) //$NON-NLS-1$ //$NON-NLS-2$
					return 1;
				if (o2.contains("/") && !o1.contains("/")) //$NON-NLS-1$ //$NON-NLS-2$
					return -1;
				return o1.compareTo(o2);
			}
		});
		localFirst.addAll(branches(GitRef.TYPE.HEAD, GitRef.TYPE.REMOTE));
		return localFirst;
	}

	private Set<String> branches(GitRef.TYPE... types)
	{
		if (types == null || types.length == 0)
			return Collections.emptySet();
		Set<GitRef.TYPE> validTypes = new HashSet<GitRef.TYPE>(Arrays.asList(types));
		Set<String> allBranches = new HashSet<String>();
		for (GitRevSpecifier revSpec : branches)
		{
			if (!revSpec.isSimpleRef())
				continue;
			GitRef ref = revSpec.simpleRef();
			if (ref == null || ref.type() == null)
				continue;
			for (GitRef.TYPE string : types)
			{
				if (ref.type().equals(string))
					break;
			}
			if (!validTypes.contains(ref.type()))
				continue;
			// Skip these magical "*.lock" files
			if (ref.type() == TYPE.HEAD && ref.shortName().endsWith(".lock")) //$NON-NLS-1$
				continue;
			allBranches.add(ref.shortName());
		}
		return allBranches;
	}

	/**
	 * Switches the current working branch on the repo. 'git checkout <branchName>'
	 * 
	 * @param branchName
	 *            the new branch to use as the working branch
	 * @return true if the switch happened. false otherwise.
	 */
	public boolean switchBranch(String branchName)
	{
		if (branchName == null)
			return false;
		String oldBranchName = currentBranch.simpleRef().shortName();
		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory(), "checkout", //$NON-NLS-1$
				branchName);
		if (result.keySet().iterator().next().intValue() != 0)
			return false;
		_headRef = null;
		readCurrentBranch();
		fireBranchChangeEvent(oldBranchName, branchName);
		return true;
	}

	private void readCurrentBranch()
	{
		this.currentBranch = addBranch(headRef());
	}

	public String parseReference(String parent)
	{
		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory(), "rev-parse", //$NON-NLS-1$
				"--verify", parent); //$NON-NLS-1$
		int exitValue = result.keySet().iterator().next();
		if (exitValue != 0)
			return null;
		return result.values().iterator().next();
	}

	private boolean reloadRefs()
	{
		_headRef = null;
		boolean ret = false;

		refs = new HashMap<String, List<GitRef>>();

		String output = GitExecutable.instance().outputForCommand(gitDirPath(), "for-each-ref", //$NON-NLS-1$
				"--format=%(refname) %(objecttype) %(objectname) %(*objectname)", "refs"); //$NON-NLS-1$ //$NON-NLS-2$
		List<String> lines = StringUtil.tokenize(output, "\n"); //$NON-NLS-1$

		for (String line : lines)
		{
			// If its an empty line, skip it (e.g. with empty repositories)
			if (line.length() == 0)
				continue;

			List<String> components = StringUtil.tokenize(line, " "); //$NON-NLS-1$

			// First do the ref matching. If this ref is new, add it to our ref list
			GitRef newRef = GitRef.refFromString(components.get(0));
			GitRevSpecifier revSpec = new GitRevSpecifier(newRef);
			if (!addBranch(revSpec).equals(revSpec))
				ret = true;

			// Also add this ref to the refs list
			addRef(newRef, components);
		}

		// Add an "All branches" option in the branches list
		addBranch(GitRevSpecifier.allBranchesRevSpec());
		addBranch(GitRevSpecifier.localBranchesRevSpec());

		return ret;
	}

	private GitRevSpecifier addBranch(GitRevSpecifier rev)
	{
		if (rev.parameters().isEmpty())
			rev = headRef();

		// First check if the branch doesn't exist already
		for (GitRevSpecifier r : branches)
			if (rev.equals(r))
				return r;

		branches.add(rev);
		// TODO Fire a branchAddedEvent?
		return rev;
	}

	public GitRevSpecifier headRef()
	{
		if (_headRef != null)
			return _headRef;

		String branch = parseSymbolicReference(HEAD);
		if (branch != null && branch.startsWith(GitRef.REFS_HEADS))
			_headRef = new GitRevSpecifier(GitRef.refFromString(branch));
		else
			_headRef = new GitRevSpecifier(GitRef.refFromString(HEAD));

		return _headRef;
	}

	private String parseSymbolicReference(String reference)
	{
		String ref = GitExecutable.instance().outputForCommand(workingDirectory(), "symbolic-ref", "-q", reference); //$NON-NLS-1$ //$NON-NLS-2$
		if (ref != null && ref.startsWith(GitRef.REFS))
			return ref;

		return null;
	}

	private void addRef(GitRef ref, List<String> components)
	{
		String type = components.get(1);

		String sha;
		if (type.equals(GitRef.TYPE.TAG) && components.size() == 4)
			sha = components.get(3);
		else
			sha = components.get(2);

		List<GitRef> curRefs = refs.get(sha);
		if (curRefs != null)
		{
			curRefs.add(ref);
		}
		else
		{
			curRefs = new ArrayList<GitRef>();
			curRefs.add(ref);
			refs.put(sha, curRefs);
		}
	}

	/**
	 * get the name of the current branch as a string
	 * 
	 * @return
	 */
	public String currentBranch()
	{
		if (currentBranch == null)
			return null;
		return currentBranch.simpleRef().shortName();
	}

	/**
	 * Return the model encapsulating the index for this repo.
	 * 
	 * @return
	 */
	public synchronized GitIndex index()
	{
		if (index == null)
		{
			index = new GitIndex(this, workingDirectory());
			index.refresh(false, new NullProgressMonitor()); // Don't want to call back to fireIndexChangeEvent yet!
		}
		return index;
	}

	void fireBranchChangeEvent(String oldBranchName, String newBranchName)
	{
		if (listeners == null || listeners.isEmpty())
			return;
		BranchChangedEvent e = new BranchChangedEvent(this, oldBranchName, newBranchName);
		for (IGitRepositoryListener listener : listeners)
			listener.branchChanged(e);
	}

	void fireBranchRemovedEvent(String oldBranchName)
	{
		if (listeners == null || listeners.isEmpty())
			return;
		BranchRemovedEvent e = new BranchRemovedEvent(this, oldBranchName);
		for (IGitRepositoryListener listener : listeners)
			listener.branchRemoved(e);
	}

	void fireBranchAddedEvent(String newBranchName)
	{
		if (listeners == null || listeners.isEmpty())
			return;
		BranchAddedEvent e = new BranchAddedEvent(this, newBranchName);
		for (IGitRepositoryListener listener : listeners)
			listener.branchAdded(e);
	}

	void fireIndexChangeEvent(Collection<ChangedFile> preChangeFiles, Collection<ChangedFile> changedFiles)
	{
		if (listeners == null || listeners.isEmpty())
			return;

		IndexChangedEvent e = new IndexChangedEvent(this, preChangeFiles, changedFiles);
		// If there's no diff, don't even fire the event
		if (!e.hasDiff())
			return;
		for (IGitRepositoryListener listener : listeners)
			listener.indexChanged(e);
	}

	public boolean hasMerges()
	{
		return mergeHeadFile().exists();
	}

	boolean executeHook(String name)
	{
		return executeHook(name, new String[0]);
	}

	boolean executeHook(String name, String... arguments)
	{
		IPath hookPath = gitDirPath();
		hookPath = hookPath.append("hooks").append(name); //$NON-NLS-1$
		File hook = hookPath.toFile();
		if (!hook.exists() || !hook.isFile())
			return true;

		try
		{
			Method method = File.class.getMethod("canExecute", (Class[]) null); //$NON-NLS-1$
			if (method != null)
			{
				Boolean canExecute = (Boolean) method.invoke(hook, (Object[]) null);
				if (!canExecute)
					return true;
			}
		}
		catch (Exception e)
		{
			// ignore
		}

		Map<String, String> env = new HashMap<String, String>();
		env.put(GitEnv.GIT_DIR, gitDirPath().toOSString());
		env.put(GitEnv.GIT_INDEX_FILE, gitFile(INDEX).getAbsolutePath());

		int ret = 1;
		Map<Integer, String> result = ProcessUtil.runInBackground(hookPath.toOSString(), workingDirectory(), env,
				arguments);
		ret = result.keySet().iterator().next();
		return ret == 0;
	}

	String commitMessageFile()
	{
		return gitFile(COMMIT_EDITMSG).getAbsolutePath();
	}

	void writetoCommitFile(String commitMessage)
	{
		File commitMessageFile = new File(commitMessageFile());
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(commitMessageFile);
			out.write(commitMessage.getBytes(COMMIT_FILE_ENCODING));
			out.flush();
		}
		catch (IOException ioe)
		{
			GitPlugin.logError(ioe.getMessage(), ioe);
		}
		finally
		{
			try
			{
				if (out != null)
					out.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

	public void lazyReload()
	{
		if (!hasChanged)
			return;

		reloadRefs();
		hasChanged = false;
	}

	void hasChanged()
	{
		hasChanged = true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GitRepository))
			return false;
		GitRepository other = (GitRepository) obj;
		return fileURL.getPath().equals(other.fileURL.getPath());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return fileURL.getPath().hashCode();
	}

	/**
	 * Return the list of commits the local copy of a branch is ahead of the remote tracking branch.
	 * 
	 * @param branchName
	 * @return null if there's no tracking remote branch
	 */
	public String[] commitsAhead(String branchName)
	{
		GitRef remote = remoteTrackingBranch(branchName);
		if (remote == null)
			return null;
		return index().commitsBetween(remote.ref(), GitRef.REFS_HEADS + branchName);
	}

	public ChangedFile getChangedFileForResource(IResource resource)
	{
		return index().getChangedFileForResource(resource);
	}

	/**
	 * Return the list of commits the local copy of a branch is behind the remote tracking branch.
	 * 
	 * @param branchName
	 * @return null if there's no tracking remote branch
	 */
	public String[] commitsBehind(String branchName)
	{
		GitRef remote = remoteTrackingBranch(branchName);
		if (remote == null)
			return null;
		return index().commitsBetween(GitRef.REFS_HEADS + branchName, remote.ref());
	}

	/**
	 * Tries to calculate if a branch that has a corresponding remote branch has a different SHA as the tree/head. TODO
	 * This is pretty inefficient if we loop over branches calling this. We should do one single ls-remote to grab all
	 * remote SHAs at once rather than making a trip for each branch.
	 * 
	 * @param branchName
	 * @return
	 */
	@SuppressWarnings("nls")
	public boolean shouldPull(String branchName)
	{
		GitRef remote = remoteTrackingBranch(branchName);
		if (remote == null)
			return false;
		String[] commits = index().commitsBetween(GitRef.REFS_HEADS + branchName, remote.ref());
		if (commits != null && commits.length > 0)
			return true;
		// Check to see if user has disabled performing remote fetches for pull indicator calculations.
		boolean performFetches = Platform.getPreferencesService().getBoolean(GitPlugin.getPluginId(),
				IPreferenceConstants.GIT_CALCULATE_PULL_INDICATOR, false, null);
		if (!performFetches)
			return false;

		// Use git ls-remote remotename remote-branchname
		// Parse out the sha and compare vs the branch's local sha!
		String output = GitExecutable.instance().outputForCommand(workingDirectory(), "ls-remote",
				remote.getRemoteName(), remote.getRemoteBranchName());
		if (output == null || output.length() < 40)
		{
			GitPlugin.logWarning(MessageFormat.format(
					"Got back unexpected output for ls-remote {0} {1}, in {2} (local branch: {3}): {4}",
					remote.getRemoteName(), remote.getRemoteBranchName(), workingDirectory(), branchName, output));
			return false;
		}
		String remoteSHA = output.substring(0, 40);
		output = GitExecutable.instance().outputForCommand(workingDirectory(), "ls-remote", ".", "heads/" + branchName);
		if (output == null || output.length() < 40)
		{
			GitPlugin.logWarning(MessageFormat.format("Got back unexpected output for ls-remote . heads/{0}: {1}",
					branchName, output));
			return false;
		}
		String localSHA = output.substring(0, 40);
		return !localSHA.equals(remoteSHA);
	}

	public boolean isDirty()
	{
		return index().isDirty();
	}

	/**
	 * Determine if the passed in branch has a remote tracking branch.
	 * 
	 * @param branchName
	 * @return
	 */
	public boolean trackingRemote(String branchName)
	{
		return remoteTrackingBranch(branchName) != null;
	}

	/**
	 * Returns the remote tracking branch name for the branch passed in. Returns null if there is none.
	 * 
	 * @param branchName
	 * @return
	 */
	public GitRef remoteTrackingBranch(String branchName)
	{
		String output = GitExecutable.instance().outputForCommand(workingDirectory(), "config", "--get-regexp", //$NON-NLS-1$ //$NON-NLS-2$
				"^branch\\." + branchName + "\\.remote"); //$NON-NLS-1$ //$NON-NLS-2$
		if (output == null || output.trim().length() == 0)
		{
			// FIXME Doesn't seem to handle case where we init locally and then add origin and push there...
			// See http://kernel.org/pub/software/scm/git/docs/git-pull.html#REMOTES
			// Git will look in a few places and assume use of remote defined
			return null;
		}
		String remoteSubname = output.substring(14 + branchName.length()).trim();
		return GitRef.refFromString(GitRef.REFS_REMOTES + remoteSubname + "/" + branchName); //$NON-NLS-1$
	}

	/**
	 * Returns the set of URLs for all remotes.
	 * 
	 * @return
	 */
	public Set<String> remoteURLs()
	{
		Set<String> remotes = new HashSet<String>();
		int index;
		for (String remoteBranch : remoteBranches())
		{
			index = remoteBranch.indexOf("/"); //$NON-NLS-1$
			if (index > -1)
			{
				remotes.add(remoteBranch.substring(0, index));
			}
		}

		Set<String> remoteURLs = new HashSet<String>();
		for (String string : remotes)
		{
			String output = GitExecutable.instance().outputForCommand(workingDirectory(), "config", "--get-regexp", //$NON-NLS-1$ //$NON-NLS-2$
					"^remote\\." + string + "\\.url"); //$NON-NLS-1$ //$NON-NLS-2$
			if (output == null || output.trim().length() == 0)
				continue;
			remoteURLs.add(output.substring(output.indexOf(".url ") + 5)); //$NON-NLS-1$
		}
		return remoteURLs;
	}

	/**
	 * @param branchName
	 *            Name of the new branch
	 * @param track
	 *            Whether this branch should track the start point
	 * @param startPoint
	 *            branch name, commit id, or ref/tag to create the branch off of. Null/empty assumes "HEAD"
	 * @return
	 */
	public boolean createBranch(String branchName, boolean track, String startPoint)
	{
		List<String> args = new ArrayList<String>();
		args.add("branch"); //$NON-NLS-1$
		if (track)
			args.add("--track"); //$NON-NLS-1$
		args.add(branchName);
		if (startPoint != null && startPoint.trim().length() > 0)
			args.add(startPoint);

		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory(),
				args.toArray(new String[args.size()]));
		if (result.keySet().iterator().next() != 0)
			return false;
		// Add branch to list in model!
		addBranch(new GitRevSpecifier(GitRef.refFromString(GitRef.REFS_HEADS + branchName)));
		fireBranchAddedEvent(branchName);
		return true;
	}

	public IStatus deleteBranch(String branchName)
	{
		return deleteBranch(branchName, false);
	}

	public IStatus deleteBranch(String branchName, boolean force)
	{
		List<String> args = new ArrayList<String>();
		args.add("branch"); //$NON-NLS-1$
		if (!force)
		{
			args.add("-d"); //$NON-NLS-1$
		}
		else
		{
			args.add("-D"); //$NON-NLS-1$
		}
		args.add(branchName);

		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory(),
				args.toArray(new String[args.size()]));
		int exitCode = result.keySet().iterator().next();
		if (exitCode != 0)
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), exitCode, result.values().iterator().next(), null);
		// Remove branch in model!
		branches.remove(new GitRevSpecifier(GitRef.refFromString(GitRef.REFS_HEADS + branchName)));
		fireBranchRemovedEvent(branchName);
		return Status.OK_STATUS;
	}

	public boolean validBranchName(String branchName)
	{
		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory(), "check-ref-format", //$NON-NLS-1$
				GitRef.REFS_HEADS + branchName);
		return result.keySet().iterator().next() == 0;
	}

	public IStatus deleteFile(String filePath)
	{
		Map<Integer, String> result = GitExecutable.instance()
				.runInBackground(workingDirectory(), "rm", "-f", filePath); //$NON-NLS-1$ //$NON-NLS-2$
		if (result == null)
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), "Failed to execute git rm -f"); //$NON-NLS-1$
		if (result.keySet().iterator().next() != 0)
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), result.values().iterator().next());
		index().refreshAsync();
		return Status.OK_STATUS;
	}

	public IStatus deleteFolder(IPath folderPath)
	{
		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory(), "rm", "-rf", //$NON-NLS-1$ //$NON-NLS-2$
				folderPath.toOSString());
		if (result == null)
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), "Failed to execute git rm -rf"); //$NON-NLS-1$
		if (result.keySet().iterator().next() != 0)
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), result.values().iterator().next());
		index().refreshAsync();
		return Status.OK_STATUS;
	}

	public IStatus moveFile(IPath source, IPath dest)
	{
		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory(),
				"mv", source.toOSString(), dest.toOSString()); //$NON-NLS-1$
		int exitCode = result.keySet().iterator().next();
		if (exitCode != 0)
		{
			String message = result.values().iterator().next();
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), exitCode, message, null);
		}
		index().refreshAsync();
		return Status.OK_STATUS;
	}

	public IPath relativePath(IResource theResource)
	{
		IPath workingDirectory = workingDirectory();
		IPath resourcePath = theResource.getLocation();
		if (workingDirectory.isPrefixOf(resourcePath))
		{
			resourcePath = resourcePath.makeRelativeTo(workingDirectory);
		}
		// What if we have some trailing slash or something?
		if (resourcePath.isEmpty())
		{
			resourcePath = Path.fromOSString(currentBranch());
		}
		return resourcePath;
	}

	/**
	 * Returns the set of short names for all simple refs that are local or remote branches or tags.
	 * 
	 * @return
	 */
	public Set<String> allSimpleRefs()
	{
		Set<String> allRefs = new HashSet<String>();
		for (GitRevSpecifier revSpec : branches)
		{
			if (!revSpec.isSimpleRef())
				continue;
			GitRef ref = revSpec.simpleRef();
			if (ref == null || ref.type() == null)
				continue;
			allRefs.add(ref.shortName());
		}
		return allRefs;
	}

	void dispose()
	{
		// clean up any listeners/etc!
		if (fileWatcherIds != null)
		{
			for (Integer fileWatcherId : fileWatcherIds)
			{
				try
				{
					FileWatcher.removeWatch(fileWatcherId);
				}
				catch (JNotifyException e)
				{
					GitPlugin.logError(e.getMessage(), e);
				}
			}
		}
		fileWatcherIds = null;
		if (listeners != null)
		{
			synchronized (listeners)
			{
				listeners.clear();
				listeners = null;
			}
		}
		_headRef = null;
		hasChanged = false;
		index = null;
		refs = null;
		branches = null;
	}

	public boolean hasUnresolvedMergeConflicts()
	{
		return index().hasUnresolvedMergeConflicts();
	}

	public List<String> getMergeSHAs()
	{
		List<String> shas = new ArrayList<String>();
		if (!mergeHeadFile().exists())
			return shas;
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(mergeHeadFile()));
			String sha = null;
			while ((sha = reader.readLine()) != null)
				shas.add(sha);
		}
		catch (Exception e)
		{
			GitPlugin.logError(e.getMessage(), e);
		}
		finally
		{
			if (reader != null)
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					// ignore
				}
		}
		return shas;
	}

	File mergeHeadFile()
	{
		return gitFile(MERGE_HEAD_FILENAME);
	}

	File gitFile(String string)
	{
		return gitDirPath().append(string).toFile();
	}

	private IPath gitDirPath()
	{
		return Path.fromOSString(new File(fileURL).getAbsolutePath());
	}

	public void firePullEvent()
	{
		if (listeners == null || listeners.isEmpty())
			return;
		PullEvent e = new PullEvent(this);
		for (IGitRepositoryListener listener : listeners)
			listener.pulled(e);
	}

	public void firePushEvent()
	{
		if (listeners == null || listeners.isEmpty())
			return;
		PushEvent e = new PushEvent(this);
		for (IGitRepositoryListener listener : listeners)
			listener.pushed(e);
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
		return index().resourceOrChildHasChanges(resource);
	}

	/**
	 * Gets the list of changed files that are underneath the given container.
	 * 
	 * @param container
	 * @return
	 */
	public List<ChangedFile> getChangedFilesForContainer(IContainer container)
	{
		return index().getChangedFilesForContainer(container);
	}

	URI getFileURL()
	{
		return fileURL;
	}

	public void addListener(IGitRepositoryListener listener)
	{
		if (listener == null)
			return;

		if (listeners == null)
		{
			listeners = new HashSet<IGitRepositoryListener>(3);
		}
		synchronized (listeners)
		{
			listeners.add(listener);
		}
	}

	public void removeListener(IGitRepositoryListener listener)
	{
		if (listener == null || listeners == null)
			return;
		synchronized (listeners)
		{
			listeners.remove(listener);
		}
	}

	public Set<IResource> getChangedResources()
	{
		return index().getChangedResources();
	}

	public IStatus addRemoteTrackingBranch(String localBranchName, String remoteName)
	{
		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory(),
				"config", MessageFormat.format("branch.{0}.remote", localBranchName), remoteName); //$NON-NLS-1$ //$NON-NLS-2$
		if (result == null)
		{
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), MessageFormat.format(
					"Failed to set github remote {0} for local branch {1}", remoteName, localBranchName));
		}
		// Non-zero exit code!
		if (result.keySet().iterator().next() != 0)
		{
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), result.values().iterator().next());
		}

		// set merge for our local branch
		result = GitExecutable
				.instance()
				.runInBackground(
						workingDirectory(),
						"config", MessageFormat.format("branch.{0}.merge", localBranchName), GitRef.REFS_HEADS + localBranchName); //$NON-NLS-1$ //$NON-NLS-2$
		if (result == null)
		{
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), MessageFormat.format(
					"Failed to set merge point for branch {0}", localBranchName));
		}
		// Non-zero exit code!
		if (result.keySet().iterator().next() != 0)
		{
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), result.values().iterator().next());
		}
		return Status.OK_STATUS;
	}

}
