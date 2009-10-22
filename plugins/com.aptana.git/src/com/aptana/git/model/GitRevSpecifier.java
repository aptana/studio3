package com.aptana.git.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class GitRevSpecifier
{

	private List<String> parameters;
	private String description;
	private String workingDirectory;

	GitRevSpecifier(String... parameters)
	{
		this.parameters = Arrays.asList(parameters);
	}

	GitRevSpecifier(GitRef newRef)
	{
		parameters = new ArrayList<String>();
		parameters.add(newRef.ref());
		description = newRef.shortName();
	}

	List<String> parameters()
	{
		return parameters;
	}

	static GitRevSpecifier allBranchesRevSpec()
	{
		GitRevSpecifier revspec = new GitRevSpecifier("--all");
		revspec.description = "All branches";
		return revspec;
	}

	static GitRevSpecifier localBranchesRevSpec()
	{
		GitRevSpecifier revspec = new GitRevSpecifier("--branches");
		revspec.description = "Local branches";
		return revspec;
	}
}
