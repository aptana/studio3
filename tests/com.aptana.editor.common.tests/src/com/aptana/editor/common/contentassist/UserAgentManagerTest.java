/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserAgentManagerTest
{
	private UserAgentManager manager;

	@Before
	public void setUp() throws Exception
	{
		manager = UserAgentManager.getInstance();

	}

	@After
	public void tearDown() throws Exception
	{
		manager = null;
	}

	private void assertIDs(String[] actualIds, String... expectedUserAgentIDs)
	{
		assertNotNull(actualIds);

		// create expected set
		Set<String> expected = new HashSet<String>();
		for (String id : expectedUserAgentIDs)
		{
			expected.add(id);
		}

		// create actual set
		Set<String> actual = new HashSet<String>();
		for (String userAgent : actualIds)
		{
			actual.add(userAgent);
		}

		assertEquals(expected, actual);
	}

	@Test
	public void testGetDefaulUserAgentIDs()
	{
		assertIDs(manager.getDefaultUserAgentIDs("any nature id"), "IE", "Mozilla", "Chrome");
	}
}
