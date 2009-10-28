package com.aptana.git.core.model;

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

	String getWorkingDirectory()
	{
		return workingDirectory;
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

	public boolean isSimpleRef()
	{
		return parameters.size() == 1 && !parameters.get(0).startsWith("-");
	}

	public String simpleRef()
	{
		if (!isSimpleRef())
			return null;
		return parameters.get(0);
	}

	public boolean hasLeftRight()
	{
		for (String param : parameters)
			if (param.equals("--left-right"))
				return true;
		return false;
	}
}
