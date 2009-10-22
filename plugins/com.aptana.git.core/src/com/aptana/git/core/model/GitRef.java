package com.aptana.git.model;

class GitRef
{

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

	String type()
	{
		if (ref.startsWith("refs/heads"))
			return "head";
		if (ref.startsWith("refs/tags"))
			return "tag";
		if (ref.startsWith("refs/remotes"))
			return "remote";
		return null;
	}
}
