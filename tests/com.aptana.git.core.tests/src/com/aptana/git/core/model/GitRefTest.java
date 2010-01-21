package com.aptana.git.core.model;

import junit.framework.TestCase;

public class GitRefTest extends TestCase
{

	public void testMasterRef()
	{
		String fullName = GitRef.REFS_HEADS + "master";
		GitRef ref = GitRef.refFromString(fullName);
		assertEquals("master", ref.shortName());
		assertEquals(fullName, ref.ref());
		assertEquals(GitRef.TYPE.HEAD, ref.type());
	}

	public void testRemoteBranchRef()
	{
		String fullName = GitRef.REFS_REMOTES + "remote_branch";
		GitRef ref = GitRef.refFromString(fullName);
		assertEquals("remote_branch", ref.shortName());
		assertEquals(fullName, ref.ref());
		assertEquals(GitRef.TYPE.REMOTE, ref.type());
	}

	public void testTagRef()
	{
		String fullName = GitRef.REFS_TAGS + "v1.0.5";
		GitRef ref = GitRef.refFromString(fullName);
		assertEquals("v1.0.5", ref.shortName());
		assertEquals(fullName, ref.ref());
		assertEquals(GitRef.TYPE.TAG, ref.type());
	}
	
	public void testSomeOtherRef()
	{
		String fullName = "v1.0.5";
		GitRef ref = GitRef.refFromString(fullName);
		assertEquals(fullName, ref.shortName());
		assertEquals(fullName, ref.ref());
		assertNull(ref.type());
	}

}
