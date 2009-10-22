package com.aptana.git.core.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.GitRepositoryProvider;

public class GitRepository
{

	private static final String HEAD = "HEAD";
	public static final String GIT_DIR = ".git";

	// private GitRevList revisionList;
	private List<GitRevSpecifier> branches;
	private Map<String, List<GitRef>> refs;
	// private GitConfig config;
	private URI fileURL;
	private GitRevSpecifier _headRef;
	private GitIndex index;
	private boolean hasChanged;

	private static Set<IGitRepositoryListener> listeners = new HashSet<IGitRepositoryListener>();

	private static Map<String, WeakReference<GitRepository>> cachedRepos = new HashMap<String, WeakReference<GitRepository>>(
			3);

	public static void addListener(IGitRepositoryListener listener)
	{
		synchronized (listeners)
		{
			listeners.add(listener);
		}
	}

	public static void removeListener(IGitRepositoryListener listener)
	{
		synchronized (listeners)
		{
			listeners.remove(listener);
		}
	}

	/**
	 * Used to retrieve a git repository for a project. Will return null if Eclipse team provider is not hooked up!
	 * 
	 * @param project
	 * @return
	 */
	public static GitRepository getAttached(IProject project)
	{
		if (project == null)
			return null;

		RepositoryProvider provider = RepositoryProvider.getProvider(project, GitRepositoryProvider.ID);
		if (provider == null)
			return null;

		return getUnattachedExisting(project.getLocationURI());
	}

	/**
	 * Used solely for grabbing an existing repository when attaching Eclipse team stuff to a project!
	 * 
	 * @param path
	 * @return
	 */
	public static synchronized GitRepository getUnattachedExisting(URI path)
	{
		if (GitExecutable.instance().path() == null)
			return null;

		WeakReference<GitRepository> ref = cachedRepos.get(path.getPath());
		if (ref == null || ref.get() == null)
		{
			URI gitDirURL = gitDirForURL(path);
			if (gitDirURL == null)
				return null;

			ref = new WeakReference<GitRepository>(new GitRepository(gitDirURL));
			cachedRepos.put(path.getPath(), ref);
		}
		return ref.get();
	}

	private GitRepository(URI fileURL)
	{
		this.fileURL = fileURL;
		// this.config = new GitConfig(fileURL.getPath());
		this.branches = new ArrayList<GitRevSpecifier>();
		reloadRefs();
		// this.revisionList = new GitRevList(this);
	}

	private String workingDirectory()
	{
		if (fileURL.getPath().endsWith("/" + GIT_DIR + "/"))
			return fileURL.getPath().substring(0, fileURL.getPath().length() - 6);
		else if (GitExecutable.instance().outputForCommand(fileURL.getPath(), "rev-parse --is-inside-work-tree")
				.equals("true"))
			return GitExecutable.instance().path(); // FIXME This doesn't seem right....

		return null;
	}

	public static URI gitDirForURL(URI repositoryURL)
	{
		if (GitExecutable.instance() == null)
			return null;

		String repositoryPath = repositoryURL.getPath();

		if (isBareRepository(repositoryPath))
			return repositoryURL;

		// Use rev-parse to find the .git dir for the repository being opened
		String newPath = GitExecutable.instance().outputForCommand(repositoryPath, "rev-parse", "--git-dir");
		if (newPath.equals(GIT_DIR))
			return new File(repositoryPath, GIT_DIR).toURI();
		if (newPath.length() > 0)
			return new File(newPath).toURI();

		return null;
	}

	public boolean parseReference(String parent)
	{
		Map<Integer, String> result = GitExecutable.instance().runInBackground(workingDirectory(), "rev-parse",
				"--verify", parent);
		int exitValue = result.keySet().iterator().next();
		return exitValue == 0;
	}

	private static boolean isBareRepository(String path)
	{
		String output = GitExecutable.instance().outputForCommand(path, "rev-parse", "--is-bare-repository");
		return "true".equals(output);
	}

	private boolean reloadRefs()
	{
		_headRef = null;
		boolean ret = false;

		refs = new HashMap<String, List<GitRef>>();

		String output = GitExecutable.instance().outputForCommand(fileURL.getPath(), "for-each-ref",
				"--format=%(refname) %(objecttype) %(objectname)", " %(*objectname)", "refs");
		List<String> lines = StringUtil.componentsSeparatedByString(output, "\n");

		for (String line : lines)
		{
			// If its an empty line, skip it (e.g. with empty repositories)
			if (line.length() == 0)
				continue;

			List<String> components = StringUtil.componentsSeparatedByString(line, " ");

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

		// willChangeValueForKey("branches");
		branches.add(rev);
		// didChangeValueForKey("branches");
		return rev;
	}

	private GitRevSpecifier headRef()
	{
		if (_headRef != null)
			return _headRef;

		String branch = parseSymbolicReference(HEAD);
		if (branch != null && branch.startsWith("refs/heads/"))
			_headRef = new GitRevSpecifier(GitRef.refFromString(branch));
		else
			_headRef = new GitRevSpecifier(GitRef.refFromString(HEAD));

		return _headRef;
	}

	private String parseSymbolicReference(String reference)
	{
		String ref = GitExecutable.instance().outputForCommand(workingDirectory(), "symbolic-ref", "-q", reference);
		if (ref.startsWith("refs/"))
			return ref;

		return null;
	}

	private void addRef(GitRef ref, List<String> components)
	{
		String type = components.get(1);

		String sha;
		if (type.equals("tag") && components.size() == 4)
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

	public String currentBranch()
	{
		String output = GitExecutable.instance().outputForCommand(fileURL.getPath(), "branch", "--no-color");
		List<String> lines = StringUtil.componentsSeparatedByString(output, "\n");
		for (String line : lines)
		{
			if (line.trim().startsWith("*"))
			{
				return line.substring(1).trim();
			}
		}
		return null;
	}

	public synchronized GitIndex index()
	{
		if (index == null)
		{
			index = new GitIndex(this, workingDirectory());
			index.refresh(false); // Don't want to call back to fireIndexChangeEvent yet!
		}
		return index;
	}

	void fireIndexChangeEvent()
	{
		IndexChangedEvent e = new IndexChangedEvent(this);
		for (IGitRepositoryListener listener : listeners)
			listener.indexChanged(e);
	}

	private static void fireRepositoryAddedEvent(GitRepository repo)
	{
		RepositoryAddedEvent e = new RepositoryAddedEvent(repo);
		for (IGitRepositoryListener listener : listeners)
			listener.repositoryAdded(e);
	}

	public boolean hasMerges()
	{
		File mergeHead = new File(fileURL.getPath(), "MERGE_HEAD");
		return mergeHead.exists();
	}

	boolean executeHook(String name)
	{
		return executeHook(name, new String[0]);
	}

	boolean executeHook(String name, String... arguments)
	{
		String hookPath = fileURL.getPath();
		if (!hookPath.endsWith(File.separator))
			hookPath += File.separator;
		hookPath += "hooks" + File.separator + name;
		File hook = new File(hookPath);
		if (!hook.exists() || !hook.canExecute())
			return true;

		Map<String, String> env = new HashMap<String, String>();
		env.put("GIT_DIR", fileURL.getPath());
		env.put("GIT_INDEX_FILE", fileURL.getPath() + File.separator + "index");

		int ret = 1;
		Map<Integer, String> result = ProcessUtil.runInBackground(hookPath, workingDirectory(), env, arguments);
		ret = result.keySet().iterator().next();
		return ret == 0;
	}

	String commitMessageFile()
	{
		File commitMessageFile = new File(fileURL.getPath(), "COMMIT_EDITMSG");
		return commitMessageFile.getAbsolutePath();
	}

	void writetoCommitFile(String commitMessage)
	{
		File commitMessageFile = new File(commitMessageFile());
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(commitMessageFile);
			out.write(commitMessage.getBytes("UTF-8"));
			out.flush();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
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

	private void lazyReload()
	{
		if (!hasChanged)
			return;

		reloadRefs();
		// revisionList.reload();
		hasChanged = false;
	}

	void hasChanged()
	{
		hasChanged = true;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GitRepository))
			return false;
		GitRepository other = (GitRepository) obj;
		return fileURL.getPath().equals(other.fileURL.getPath());
	}

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
		String local = "refs/heads/" + branchName;
		String output = GitExecutable.instance().outputForCommand(workingDirectory(), "config", "--get-regexp",
				"^branch\\." + branchName + "\\.remote");
		if (output == null || output.trim().length() == 0)
			return null;
		String remoteSubname = output.substring(14 + branchName.length()).trim();
		String remote = "refs/remotes/" + remoteSubname + "/" + branchName;
		return index().commitsBetween(remote, local);
	}

	public ChangedFile getChangedFileForResource(IResource resource)
	{
		String workingDirectory = workingDirectory();
		if (!workingDirectory.endsWith("/"))
		{
			workingDirectory += "/";
		}
		for (ChangedFile changedFile : index().changedFiles())
		{
			String fullPath = workingDirectory + changedFile.getPath();
			if (resource.getLocationURI().getPath().equals(fullPath))
			{
				return changedFile;
			}
		}
		return null;
	}

	/**
	 * Return the list of commits the local copy of a branch is behind the remote tracking branch.
	 * 
	 * @param branchName
	 * @return null if there's no tracking remote branch
	 */
	public String[] commitsBehind(String branchName)
	{
		// TODO Refactor with commitsAhead
		String local = "refs/heads/" + branchName;
		String output = GitExecutable.instance().outputForCommand(workingDirectory(), "config", "--get-regexp",
				"^branch\\." + branchName + "\\.remote");
		if (output == null || output.trim().length() == 0)
			return null;
		String remoteSubname = output.substring(14 + branchName.length()).trim();
		String remote = "refs/remotes/" + remoteSubname + "/" + branchName;
		return index().commitsBetween(local, remote);
	}

	/**
	 * Generates a brand new git repository in the specified location.
	 */
	public static void create(String path)
	{
		if (path == null)
			return;
		if (path.endsWith(File.separator + GIT_DIR))
		{
			path = path.substring(0, path.length() - GIT_DIR.length());
		}

		URI existing = gitDirForURL(new File(path).toURI());
		if (existing != null)
			return;

		GitExecutable.instance().runInBackground(path, "init");
	}

	/**
	 * Given an existing repo on disk, we wrap it with our model and hook it up to the eclipse team provider.
	 * 
	 * @param project
	 * @param m
	 * @return
	 */
	public static GitRepository attachExisting(IProject project, IProgressMonitor m) throws CoreException
	{
		if (m == null)
			m = new NullProgressMonitor();
		GitRepository repo = GitRepository.getUnattachedExisting(project.getLocationURI());
		m.worked(40);
		if (repo == null)
			return null;

		try
		{
			RepositoryProvider.map(project, GitRepositoryProvider.ID);
			m.worked(10);
			fireRepositoryAddedEvent(repo);
			project.refreshLocal(IResource.DEPTH_INFINITE, new SubProgressMonitor(m, 50));
		}
		catch (TeamException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, GitPlugin.getPluginId(), e.getMessage(), e));
		}
		return repo;

		// GitLightweightDecorator.refresh();
		// TODO Notify Listeners about a new repo
	}
}
