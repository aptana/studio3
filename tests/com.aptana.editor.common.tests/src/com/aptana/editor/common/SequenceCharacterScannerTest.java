/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;

import com.aptana.editor.common.text.rules.SequenceCharacterScanner;

public class SequenceCharacterScannerTest extends TestCase
{

	private SequenceCharacterScanner scanner;
	private RuleBasedScanner ruleBasedScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		ruleBasedScanner = new RuleBasedScanner();
		IPartitionScannerSwitchStrategy switchStrategy = new PartitionScannerSwitchStrategy(
				new String[] { "</script>" }, new String[][] {});
		scanner = new SequenceCharacterScanner(ruleBasedScanner, switchStrategy, true);
	}

	@Override
	protected void tearDown() throws Exception
	{
		ruleBasedScanner = null;
		scanner = null;
		super.tearDown();
	}

	private void setSource(String src)
	{
		IDocument document = new Document(src);
		ruleBasedScanner.setRange(document, 0, src.length());
	}

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

	public void testReturnsEOFOnSwitch()
	{
		setSource("<script>'hi'</script>");
		for (int i = 0; i < 12; i++)
		{
			scanner.read();
		}
		assertEquals(ICharacterScanner.EOF, scanner.read());
	}

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
