package com.aptana.git.core.model;

class GitRef
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
	static final String TAG_TYPE = "tag";
	private static final String HEAD_TYPE = "head";
	private static final String REMOTE_TYPE = "remote";

	private String ref;

	private GitRef(String string)
	{
		this.ref = string;
	}

	static GitRef refFromString(String string)
	{
		return new GitRef(string);
	}

	String ref()
	{
		return ref;
	}

	String shortName()
	{
		if (type() != null)
			return ref.substring(type().length() + 7);
		return ref;
	}

	private String type()
	{
		if (ref.startsWith(REFS_HEADS))
			return HEAD_TYPE;
		if (ref.startsWith(REFS_TAGS))
			return TAG_TYPE;
		if (ref.startsWith(REFS_REMOTES))
			return REMOTE_TYPE;
		return null;
	}
}
