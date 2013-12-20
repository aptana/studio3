/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("nls")
public class GitRevSpecifierTest
{

	@Test
	public void testHasLeftRight() throws Exception
	{
		assertTrue(new GitRevSpecifier("something", "--left-right").hasLeftRight());
		assertFalse(new GitRevSpecifier("something").hasLeftRight());
	}

	@Test
	public void testToString() throws Exception
	{
		assertEquals("something --left-right", new GitRevSpecifier("something", "--left-right").toString());
	}

	@Test
	public void testIsSimpleRef() throws Exception
	{
		assertTrue(new GitRevSpecifier("master").isSimpleRef());
		assertFalse(new GitRevSpecifier("something", "--left-right").isSimpleRef());
		assertFalse(GitRevSpecifier.allBranchesRevSpec().isSimpleRef());
	}

	@Test
	public void testSimpleRef() throws Exception
	{
		assertEquals(GitRef.refFromString("master"), new GitRevSpecifier("master").simpleRef());
		assertNull(new GitRevSpecifier("something", "--left-right").simpleRef());
		assertNull(GitRevSpecifier.allBranchesRevSpec().simpleRef());
	}

	@Test
	public void testAllBranchesSpec() throws Exception
	{
		assertEquals("--all", GitRevSpecifier.allBranchesRevSpec().toString());
		assertFalse(GitRevSpecifier.allBranchesRevSpec().isSimpleRef());
	}

	@Test
	public void testLocalBranchesSpec() throws Exception
	{
		assertEquals("--branches", GitRevSpecifier.localBranchesRevSpec().toString());
		assertFalse(GitRevSpecifier.localBranchesRevSpec().isSimpleRef());
	}
}
