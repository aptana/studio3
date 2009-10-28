package com.aptana.git.core.model;

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
