/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.peer;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

public class ExitPolicyTest
{

	private ITextViewer viewer;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		viewer = new TextViewer(new Shell(), SWT.NONE);
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		viewer = null;
//		super.tearDown();
	}

	// TODO Add tests for typing close character over top end!
	// TODO Add tests with escaped chars!
	
	@Test
	public void testNewLineExitsInsideDoubleQuotedStringPair()
	{
		ExitFlags flags = send('"', '\n');
		assertNull(flags);
	}

	@Test
	public void testNewLineExitsInsideSingleQuotedStringPair()
	{
		ExitFlags flags = send('\'', '\n');
		assertNull(flags);
	}

	@Test
	public void testNewLineInsertsInsideParentheses()
	{
		ExitFlags flags = send(')', '\n');
		assertTrue(flags.doit);
		assertEquals(ILinkedModeListener.EXIT_ALL, flags.flags);
	}
	
	@Test
	public void testNewLineInsertsInsideCurlyBraces()
	{
		ExitFlags flags = send('}', '\n');
		assertTrue(flags.doit);
		assertEquals(ILinkedModeListener.EXIT_ALL, flags.flags);
	}
	
	@Test
	public void testNewLineInsertsInsideBrackets()
	{
		ExitFlags flags = send(']', '\n');
		assertTrue(flags.doit);
		assertEquals(ILinkedModeListener.EXIT_ALL, flags.flags);
	}
	
	@Test
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
