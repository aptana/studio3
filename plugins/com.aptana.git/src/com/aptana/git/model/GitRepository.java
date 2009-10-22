package com.aptana.git.model;

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
import org.eclipse.team.core.RepositoryProvider;

import com.aptana.git.GitPlugin;
import com.aptana.git.ProcessUtil;

public class GitRepository
{

	private static final String HEAD = "HEAD";
	private static final String GIT_DIR = ".git";

	// private GitRevList revisionList;
	private List<GitRevSpecifier> branches;
	private Map<String, List<GitRef>> refs;
	// private GitConfig config;
	private URI fileURL;
	private GitRevSpecifier _headRef;
	private GitIndex index;
	private boolean hasChanged;

	private static Set<IGitRepositoryListener> listeners = new HashSet<IGitRepositoryListener>();

	private static Map<URI, WeakReference<GitRepository>> cachedRepos = new HashMap<URI, WeakReference<GitRepository>>(
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
	public static GitRepository instance(IProject project)
	{
		if (project == null)
			return null;

		RepositoryProvider provider = RepositoryProvider.getProvider(project,
				com.aptana.git.RepositoryProvider.ID);
		if (provider == null)
			return null;

		return create(project.getLocationURI());
	}

	/**
	 * Used solely for creating a new repository when connecting Eclipse team stuff to a project!
	 * 
	 * @param path
	 * @return
	 */
	public static GitRepository create(URI path)
	{
		if (GitPlugin.getDefault().getExecutable().path() == null)
			return null;

		URI gitDirURL = gitDirForURL(path);
		if (gitDirURL == null)
			return null;

		WeakReference<GitRepository> ref = cachedRepos.get(gitDirURL);
		if (ref == null || ref.get() == null)
		{
			GitRepository repo = new GitRepository(gitDirURL);
			cachedRepos.put(gitDirURL, new WeakReference<GitRepository>(repo));
			return repo;
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

	public String workingDirectory()
	{
		if (fileURL.getPath().endsWith("/" + GIT_DIR + "/"))
			return fileURL.getPath().substring(0, fileURL.getPath().length() - 6);
		else if (GitPlugin.getDefault().getExecutable().outputForCommand(fileURL.getPath(),
				"rev-parse --is-inside-work-tree").equals("true"))
			return GitPlugin.getDefault().getExecutable().path(); // FIXME This doesn't seem right....

		return null;
	}

	public static URI gitDirForURL(URI repositoryURL)
	{
		if (GitPlugin.getDefault().getExecutable() == null)
			return null;

		String repositoryPath = repositoryURL.getPath();

		if (isBareRepository(repositoryPath))
			return repositoryURL;

		// Use rev-parse to find the .git dir for the repository being opened
		String newPath = GitPlugin.getDefault().getExecutable().outputForCommand(repositoryPath, "rev-parse",
				"--git-dir");
		if (newPath.equals(GIT_DIR))
			return new File(repositoryPath, GIT_DIR).toURI();
		if (newPath.length() > 0)
			return new File(newPath).toURI();

		return null;
	}

	public boolean parseReference(String parent)
	{
		Map<Integer, String> result = GitPlugin.getDefault().getExecutable().runInBackground(workingDirectory(),
				"rev-parse", "--verify", parent);
		int exitValue = result.keySet().iterator().next();
		return exitValue == 0;
	}

	private static boolean isBareRepository(String path)
	{
		String output = GitPlugin.getDefault().getExecutable().outputForCommand(path, "rev-parse",
				"--is-bare-repository");
		return "true".equals(output);
	}

	private boolean reloadRefs()
	{
		_headRef = null;
		boolean ret = false;

		refs = new HashMap<String, List<GitRef>>();

		String output = GitPlugin.getDefault().getExecutable().outputForCommand(fileURL.getPath(), "for-each-ref",
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
		String ref = GitPlugin.getDefault().getExecutable().outputForCommand(workingDirectory(), "symbolic-ref", "-q",
				reference);
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
		String output = GitPlugin.getDefault().getExecutable().outputForCommand(fileURL.getPath(), "branch",
				"--no-color");
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

	public GitIndex index()
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

	public String commitMessageFile()
	{
		File commitMessageFile = new File(fileURL.getPath(), "COMMIT_EDITMSG");
		return commitMessageFile.getAbsolutePath();
	}

	public void writetoCommitFile(String commitMessage)
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
		String output = GitPlugin.getDefault().getExecutable().outputForCommand(workingDirectory(), "config",
				"--get-regexp", "^branch\\." + branchName + "\\.remote");
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
		String output = GitPlugin.getDefault().getExecutable().outputForCommand(workingDirectory(), "config",
				"--get-regexp", "^branch\\." + branchName + "\\.remote");
		if (output == null || output.trim().length() == 0)
			return null;
		String remoteSubname = output.substring(14 + branchName.length()).trim();
		String remote = "refs/remotes/" + remoteSubname + "/" + branchName;
		return index().commitsBetween(local, remote);
	}
}
