/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.github;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.git.core.model.GitRepository;

/**
 * @author cwilliams
 */
public interface IGithubRepository
{

	public long getID();

	/**
	 * Name of the repo.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Is this a preivate repo?
	 * 
	 * @return
	 */
	public boolean isPrivate();

	/**
	 * Is this is a fork of another repo?
	 * 
	 * @return
	 */
	public boolean isFork();

	public String getSSHURL();

	/**
	 * What org or user owns this repo? FIXME Return IGithubUser?
	 * 
	 * @return
	 */
	public String getOwner();

	/**
	 * If this is a fork of another github repository we will return the parent repo. Otherwise returns null. Throws
	 * CoreException if we're unable to get parent repo from Github API.
	 * 
	 * @return
	 * @throws CoreException
	 */
	public IGithubRepository getParent() throws CoreException;

	/**
	 * The original source for the network of the repo.
	 * 
	 * @return
	 * @throws CoreException
	 */
	public IGithubRepository getSource() throws CoreException;

	/**
	 * Gets the list of forks for a given repository. https://developer.github.com/v3/repos/forks/
	 * 
	 * @return
	 * @throws CoreException
	 */
	public List<IGithubRepository> getForks() throws CoreException;

	/**
	 * Creates a PR. This PR is done on the parent. so this assume this repo is the fork holding the feature
	 * branch/changes you'd like merged to the parent.
	 * 
	 * @param title
	 *            required
	 * @param body
	 *            optional
	 * @param head
	 *            The local repo to use as the head of the PR (it's current branch is the assumed headBranch)
	 * @param baseRepo
	 *            The repo to use as the base for the PR
	 * @param baseBranch
	 *            The name of the branch from the base repo
	 * @return
	 * @throws CoreException
	 */
	public IGithubPullRequest createPullRequest(String title, String body, GitRepository head,
			IGithubRepository baseRepo, String baseBranch, IProgressMonitor monitor) throws CoreException;

	/**
	 * The default branch for this repo.
	 * 
	 * @return
	 */
	public String getDefaultBranch();

	/**
	 * Returns the list of open PRs for this repo.
	 * 
	 * @return
	 * @throws CoreException
	 */
	public List<IGithubPullRequest> getOpenPullRequests() throws CoreException;

	/**
	 * Returns the Set of branch names for the remote repo.
	 */
	public Set<String> getBranches();

	/**
	 * Fully qualified name. Equivalent to getOwner() + '/' + getName();
	 * 
	 * @return
	 */
	public String getFullName();
}
