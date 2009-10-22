package com.aptana.git.model;

/**
 * Used to set and get values from the .gitconfig
 * @author cwilliams
 *
 */
class GitConfig
{

	private String repositoryPath;

	GitConfig(String path)
	{
		repositoryPath = path;
	}
}
