/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import com.aptana.scripting.model.CommandElement;

public class CommandTemplateTest
{

	@Test
	public void testMatchesPrefixWhenExactlyTheSame()
	{
		assertTrue(matches("j.u", "j.u"));
	}

	/**
	 * Test that we keep chopping down the prefix at whitespaces and non-letter/digits from beginning until we match.
	 */
	@Test
	public void testMatchesPartialPrefixes()
	{
		assertTrue(matches("j.u", "u"));
	}

	@Test
	public void testMatchesWhenTriggerStartsWithPrefix()
	{
		assertTrue(matches("echo", "echoh"));
	}

	@Test
	public void testMatchesWhenTriggerStartsWithPrefixSegment()
	{
		assertTrue(matches("<div>echo", "echoh"));
	}

	@Test
	public void testMatchesWithCaseDifferences()
	{
		assertTrue(matches("EcHo", "echo"));
	}

	@Test
	public void testMatchesWithCaseDifferencesAndTriggerStartsWithPrefix()
	{
		assertTrue(matches("EcHo", "echoh"));
	}

	@Test
	public void testMatchesWithPrefixTriggerParen()
	{
		assertTrue(matches(")echo", "echo"));
	}

	@Test
	public void testMatchesWithPrefixTriggerBracketColon()
	{
		assertTrue(matches("[:echo", "echo"));
	}

	private boolean matches(String prefix, String trigger)
	{
		CommandElement ce = new CommandElement("");
		ce.setDisplayName("something");
		return new CommandTemplate(ce, trigger, "java").matches(prefix, "java");
	}

}
