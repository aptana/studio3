/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class GitRevSpecifierTest extends TestCase
{

	public void testHasLeftRight() throws Exception
	{
		assertTrue(new GitRevSpecifier("something", "--left-right").hasLeftRight());
		assertFalse(new GitRevSpecifier("something").hasLeftRight());
	}

	public void testToString() throws Exception
	{
		assertEquals("something --left-right", new GitRevSpecifier("something", "--left-right").toString());
	}

	public void testIsSimpleRef() throws Exception
	{
		assertTrue(new GitRevSpecifier("master").isSimpleRef());
		assertFalse(new GitRevSpecifier("something", "--left-right").isSimpleRef());
		assertFalse(GitRevSpecifier.allBranchesRevSpec().isSimpleRef());
	}

	public void testSimpleRef() throws Exception
	{
		assertEquals(GitRef.refFromString("master"), new GitRevSpecifier("master").simpleRef());
		assertNull(new GitRevSpecifier("something", "--left-right").simpleRef());
		assertNull(GitRevSpecifier.allBranchesRevSpec().simpleRef());
	}

	public void testAllBranchesSpec() throws Exception
	{
		assertEquals("--all", GitRevSpecifier.allBranchesRevSpec().toString());
		assertFalse(GitRevSpecifier.allBranchesRevSpec().isSimpleRef());
	}

	public void testLocalBranchesSpec() throws Exception
	{
		assertEquals("--branches", GitRevSpecifier.localBranchesRevSpec().toString());
		assertFalse(GitRevSpecifier.localBranchesRevSpec().isSimpleRef());
	}
}
