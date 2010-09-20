/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
		return parameters.size() == 1 && !parameters.get(0).startsWith("-"); //$NON-NLS-1$
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
			builder.append(param).append(" "); //$NON-NLS-1$
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
