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
package com.aptana.editor.common.internal.peer;

import junit.framework.TestCase;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

public class ExitPolicyTest extends TestCase
{

	private ITextViewer viewer;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		viewer = new TextViewer(new Shell(), SWT.NONE);
	}

	@Override
	protected void tearDown() throws Exception
	{
		viewer = null;
		super.tearDown();
	}

	// TODO Add tests for typing close character over top end!
	// TODO Add tests with escaped chars!
	
	public void testNewLineExitsInsideDoubleQuotedStringPair()
	{
		ExitFlags flags = send('"', '\n');
		assertNull(flags);
	}

	public void testNewLineExitsInsideSingleQuotedStringPair()
	{
		ExitFlags flags = send('\'', '\n');
		assertNull(flags);
	}

	public void testNewLineInsertsInsideParentheses()
	{
		ExitFlags flags = send(')', '\n');
		assertTrue(flags.doit);
		assertEquals(ILinkedModeListener.EXIT_ALL, flags.flags);
	}
	
	public void testNewLineInsertsInsideCurlyBraces()
	{
		ExitFlags flags = send('}', '\n');
		assertTrue(flags.doit);
		assertEquals(ILinkedModeListener.EXIT_ALL, flags.flags);
	}
	
	public void testNewLineInsertsInsideBrackets()
	{
		ExitFlags flags = send(']', '\n');
		assertTrue(flags.doit);
		assertEquals(ILinkedModeListener.EXIT_ALL, flags.flags);
	}
	
	public void testNewLineInsertsInsideGreaterAndLessThanPair()
	{
		ExitFlags flags = send('>', '\n');
		assertNull(flags);
	}

	protected ExitFlags send(char closeChar, char toSend)
	{
		ExitPolicy policy = new ExitPolicy(null, closeChar, '\\', null);
		VerifyEvent event = createEvent(toSend);
		return policy.doExit(null, event, 0, 0);
	}

	protected VerifyEvent createEvent(char c)
	{
		Event e = new Event();
		e.character = c;
		e.start = 0;
		e.end = 0;
		e.keyCode = c;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		return event;
	}
}
