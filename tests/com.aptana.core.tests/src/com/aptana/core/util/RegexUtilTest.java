/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class RegexUtilTest
{
	@Test
	public void testCreateQuotedListPattern()
	{
		List<String> sports = new ArrayList<String>();
		sports.add("Football");
		sports.add("Soccer");
		sports.add("Basketball");

		assertEquals("(\\QFootball\\E|\\QSoccer\\E|\\QBasketball\\E)", RegexUtil.createQuotedListPattern(sports));
		assertEquals(StringUtil.EMPTY, RegexUtil.createQuotedListPattern(null));
	}
}
