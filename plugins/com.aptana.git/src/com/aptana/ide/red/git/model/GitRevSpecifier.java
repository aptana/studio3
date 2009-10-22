package com.aptana.ide.red.git.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitRevSpecifier
{

	private List<String> parameters;
	private String description;
	private String workingDirectory;

	public GitRevSpecifier(String... parameters)
	{
		this.parameters = Arrays.asList(parameters);
	}

	public GitRevSpecifier(GitRef newRef)
	{
		parameters = new ArrayList<String>();
		parameters.add(newRef.ref());
		description = newRef.shortName();
	}

	public List<String> parameters()
	{
		return parameters;
	}

	public static GitRevSpecifier allBranchesRevSpec()
	{
		GitRevSpecifier revspec = new GitRevSpecifier("--all");
		revspec.description = "All branches";
		return revspec;
	}

	public static GitRevSpecifier localBranchesRevSpec()
	{
		GitRevSpecifier revspec = new GitRevSpecifier("--branches");
		revspec.description = "Local branches";
		return revspec;
	}
}
