/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitRevSpecifier
{

	private List<String> parameters;

	public GitRevSpecifier(String... parameters)
	{
		this.parameters = Arrays.asList(parameters);
	}

	GitRevSpecifier(GitRef newRef)
	{
		parameters = new ArrayList<String>();
		parameters.add(newRef.ref());
	}

	List<String> parameters()
	{
		return parameters;
	}

	static GitRevSpecifier allBranchesRevSpec()
	{
		return new GitRevSpecifier("--all"); //$NON-NLS-1$
	}

	static GitRevSpecifier localBranchesRevSpec()
	{
		return new GitRevSpecifier("--branches"); //$NON-NLS-1$
	}

	boolean isSimpleRef()
	{
		return parameters.size() == 1 && (parameters.get(0).length() == 0 || parameters.get(0).charAt(0) != '-');
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
			if (param.equals("--left-right")) //$NON-NLS-1$
				return true;
		return false;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		for (String param : parameters)
		{
			builder.append(param).append(' ');
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
