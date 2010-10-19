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

import java.util.regex.Pattern;

/**
 * Represents a reference in the git repo. Typically branches or tags.
 * 
 * @author cwilliams
 */
public class GitRef
{

	/**
	 * Ref dirs
	 */
	static final String REFS = "refs/"; //$NON-NLS-1$
	static final String REFS_REMOTES = REFS + "remotes/"; //$NON-NLS-1$
	static final String REFS_TAGS = REFS + "tags/"; //$NON-NLS-1$
	static final String REFS_HEADS = REFS + "heads/"; //$NON-NLS-1$

	/**
	 * Ref Types
	 */
	public enum TYPE
	{
		TAG, HEAD, REMOTE
	}

	private String ref;

	private GitRef(String string)
	{
		this.ref = string;
	}

	static GitRef refFromString(String string)
	{
		return new GitRef(string);
	}

	/**
	 * The full name of the ref. i.e. "refs/heads/master" or "refs/tags/v0.7"
	 * 
	 * @return
	 */
	protected String ref()
	{
		return ref;
	}

	/**
	 * Short name for the ref. i.e. "master" or "v0.7"
	 * 
	 * @return
	 */
	public String shortName()
	{
		TYPE type = type();
		if (type == null)
			return ref;
		switch (type)
		{
			case REMOTE:
				return ref.substring(REFS_REMOTES.length());
			case HEAD:
				return ref.substring(REFS_HEADS.length());
			case TAG:
				return ref.substring(REFS_TAGS.length());
			default:
				return ref;
		}
	}

	/**
	 * Type of reference. head (local branch), remote (remote branch) or tag.
	 * 
	 * @return
	 */
	public TYPE type()
	{
		if (ref.startsWith(REFS_HEADS))
			return TYPE.HEAD;
		if (ref.startsWith(REFS_TAGS))
			return TYPE.TAG;
		if (ref.startsWith(REFS_REMOTES))
			return TYPE.REMOTE;
		return null;
	}

	@Override
	public String toString()
	{
		return ref;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GitRef))
			return false;
		GitRef other = (GitRef) obj;
		return toString().equals(other.toString());
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	public String getRemoteName()
	{
		if (type() != TYPE.REMOTE)
			return null;
		String shortName = shortName();
		return shortName.split(Pattern.quote("/"))[0]; //$NON-NLS-1$
	}

	public String getRemoteBranchName()
	{
		if (type() != TYPE.REMOTE)
			return null;
		String shortName = shortName();
		return shortName.split(Pattern.quote("/"))[1]; //$NON-NLS-1$
	}
}
