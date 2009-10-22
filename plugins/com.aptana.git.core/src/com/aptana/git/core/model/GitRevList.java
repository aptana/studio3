package com.aptana.git.model;

class GitRevList
{
	private GitRepository repository;

	GitRevList(GitRepository repo)
	{
		repository = repo;
		// TODO Uncomment
//		repository.addObserver(this, "currentBranch", 0, null);
	}

}
