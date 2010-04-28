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
