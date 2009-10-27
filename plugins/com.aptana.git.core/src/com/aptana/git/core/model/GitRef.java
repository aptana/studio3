package com.aptana.git.core.model;

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
	static final String REFS = "refs/";
	static final String REFS_REMOTES = REFS + "remotes/";
	private static final String REFS_TAGS = REFS + "tags/";
	static final String REFS_HEADS = REFS + "heads/";

	/**
	 * Ref Types
	 */
	public static final String TAG_TYPE = "tag";
	public static final String HEAD_TYPE = "head";
	public static final String REMOTE_TYPE = "remote";

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
	 * @return
	 */
	public String ref()
	{
		return ref;
	}

	/**
	 * Short name for the ref. i.e. "master" or "v0.7"
	 * @return
	 */
	public String shortName()
	{
		if (type() != null)
			return ref.substring(type().length() + 7);
		return ref;
	}

	/**
	 * Type of reference. head (local branch), remote (remote branch) or tag.
	 * @return
	 */
	public String type()
	{
		// TODO Use an enum here?
		if (ref.startsWith(REFS_HEADS))
			return HEAD_TYPE;
		if (ref.startsWith(REFS_TAGS))
			return TAG_TYPE;
		if (ref.startsWith(REFS_REMOTES))
			return REMOTE_TYPE;
		return null;
	}

	@Override
	public String toString()
	{
		return ref;
	}
}
