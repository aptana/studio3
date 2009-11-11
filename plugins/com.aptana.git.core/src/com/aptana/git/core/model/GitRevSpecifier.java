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

	boolean isSimpleRef()
	{
		return parameters.size() == 1 && !parameters.get(0).startsWith("-");
	}

	public GitRef simpleRef()
	{
		if (!isSimpleRef())
			return null;
		return GitRef.refFromString(parameters.get(0));
	}

	boolean hasLeftRight()
	{
		for (String param : parameters)
			if (param.equals("--left-right"))
				return true;
		return false;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		for (String param : parameters)
		{
			builder.append(param).append(" ");
		}
		if (builder.length() > 0)
			builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GitRevSpecifier))
			return false;
		
		GitRevSpecifier other = (GitRevSpecifier) obj;
		if (other.parameters.size() != parameters.size())
			return false;
		for (int i = 0; i < parameters.size(); i++)
		{
			String param = parameters.get(i);
			if (!other.parameters.get(i).equals(param))
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
}
