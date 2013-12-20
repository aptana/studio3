/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class GitRefTest
{

	@Test
	public void testMasterRef()
	{
		String fullName = GitRef.REFS_HEADS + "master";
		GitRef ref = GitRef.refFromString(fullName);
		assertEquals("master", ref.shortName());
		assertEquals(fullName, ref.ref());
		assertEquals(GitRef.TYPE.HEAD, ref.type());
	}

	@Test
	public void testRemoteBranchRef()
	{
		String fullName = GitRef.REFS_REMOTES + "remote_branch";
		GitRef ref = GitRef.refFromString(fullName);
		assertEquals("remote_branch", ref.shortName());
		assertEquals(fullName, ref.ref());
		assertEquals(GitRef.TYPE.REMOTE, ref.type());
	}

	@Test
	public void testTagRef()
	{
		String fullName = GitRef.REFS_TAGS + "v1.0.5";
		GitRef ref = GitRef.refFromString(fullName);
		assertEquals("v1.0.5", ref.shortName());
		assertEquals(fullName, ref.ref());
		assertEquals(GitRef.TYPE.TAG, ref.type());
	}

	@Test
	public void testSomeOtherRef()
	{
		String fullName = "v1.0.5";
		GitRef ref = GitRef.refFromString(fullName);
		assertEquals(fullName, ref.shortName());
		assertEquals(fullName, ref.ref());
		assertNull(ref.type());
	}

}
