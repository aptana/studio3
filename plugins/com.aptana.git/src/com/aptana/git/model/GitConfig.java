package com.aptana.ide.red.git.model;

/**
 * Used to set and get values from the .gitconfig
 * @author cwilliams
 *
 */
public class GitConfig
{

	private String repositoryPath;

	public GitConfig(String path)
	{
		repositoryPath = path;
	}
}
