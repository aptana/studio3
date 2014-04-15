/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;

import com.aptana.editor.common.text.rules.SequenceCharacterScanner;

public class SequenceCharacterScannerTest
{

	private SequenceCharacterScanner scanner;
	private RuleBasedScanner ruleBasedScanner;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		ruleBasedScanner = new RuleBasedScanner();
		IPartitionScannerSwitchStrategy switchStrategy = new PartitionScannerSwitchStrategy(new String[] { "</script>" });
		scanner = new SequenceCharacterScanner(ruleBasedScanner, switchStrategy, true);
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		ruleBasedScanner = null;
		scanner = null;
//		super.tearDown();
	}

	private void setSource(String src)
	{
		IDocument document = new Document(src);
		ruleBasedScanner.setRange(document, 0, src.length());
	}

	@Test
	public void testFindSwitch()
	{
		setSource("<script>'hi'</script>");
		for (int i = 0; i < 13; i++)
		{
			scanner.read();
		}
		assertTrue(scanner.foundSequence());
		scanner.unread();
		assertFalse(scanner.foundSequence());
	}

	@Test
	public void testFindSwitchAndUnreadBeforeQuerying()
	{
		// FIXME Is this actually correct behavior? Do we want it to drop found flag as soon as we unread EOF?
		setSource("<script>'hi'</script>");
		for (int i = 0; i < 13; i++)
		{
			scanner.read();
		}
		scanner.unread(); // unread EOF
		scanner.unread(); // unread last char
		assertFalse(scanner.foundSequence());
	}

	@Test
	public void testFindSwitchUnreadEOFThenReadAgain()
	{
		setSource("<script>'hi'</script>");
		for (int i = 0; i < 13; i++)
		{
			scanner.read();
		}
		scanner.unread(); // unread EOF
		assertEquals(ICharacterScanner.EOF, scanner.read());
	}

	@Test
	public void testReturnsEOFOnSwitch()
	{
		setSource("<script>'hi'</script>");
		for (int i = 0; i < 12; i++)
		{
			scanner.read();
		}
		assertEquals(ICharacterScanner.EOF, scanner.read());
	}

	@Test
	public void testIgnoreCase()
	{
		setSource("<SCRIPT>'hi'</SCRIPT>");
		for (int i = 0; i < 13; i++)
		{
			scanner.read();
		}
		assertTrue(scanner.foundSequence());
		scanner.unread();
		assertFalse(scanner.foundSequence());
	}
}
