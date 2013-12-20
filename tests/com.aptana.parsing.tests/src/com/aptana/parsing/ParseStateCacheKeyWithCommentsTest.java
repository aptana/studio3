/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Fabio
 */
public class ParseStateCacheKeyWithCommentsTest
{

	@Test
	public void testParseStateCacheKeyWithCommentsTest() throws Exception
	{
		ParseStateCacheKeyWithComments p0 = new ParseStateCacheKeyWithComments(true, true, new ParseStateCacheKey());
		ParseStateCacheKeyWithComments p1 = new ParseStateCacheKeyWithComments(true, false, new ParseStateCacheKey());
		ParseStateCacheKeyWithComments p2 = new ParseStateCacheKeyWithComments(true, false, new ParseStateCacheKey(
				"test"));

		assertEquals(p0, p1);
		assertEquals(p0.hashCode(), p1.hashCode());
		assertFalse("p0 should NOT require a reparse", p0.requiresReparse(p1));
		assertTrue("p1 should require a reparse", p1.requiresReparse(p0));

		assertFalse(p0.equals(p2));
	}
}
