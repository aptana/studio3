/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class OpenTagCloserTest
{

	protected TextViewer viewer;
	protected OpenTagCloser closer;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell shell = display.getActiveShell();
		if (shell == null)
		{
			shell = new Shell(display);
		}
		viewer = new TextViewer(shell, SWT.NONE);
		closer = new OpenTagCloser(viewer);
	}

	@Test
	public void testAPSTUD3323() throws Exception
	{
		IDocument document = setDocument("<test abc=\"\"");
		VerifyEvent event = createGreaterThanKeyEvent(11);
		closer.verifyKey(event);

		// Don't close the tag, just let it insert the character
		assertEquals("<test abc=\"\"", document.get());
		assertTrue(event.doit);
	}

	@Test
	public void testLessThanInsideAttributeValue() throws Exception
	{
		IDocument document = setDocument("<test abc=\"<\"");
		VerifyEvent event = createGreaterThanKeyEvent(13);
		closer.verifyKey(event);

		// Close the tag properly
		assertEquals("<test abc=\"<\"></test>", document.get());
		assertFalse(event.doit);
	}

	@Test
	public void testGreaterThanAtFirstChar() throws Exception
	{
		IDocument document = setDocument("");
		VerifyEvent event = createGreaterThanKeyEvent(0);
		closer.verifyKey(event);

		// No tag to close
		assertEquals("", document.get());
		assertTrue(event.doit);
	}

	@Test
	public void testTwoGreaterThanChars() throws Exception
	{
		IDocument document = setDocument(">");
		VerifyEvent event = createGreaterThanKeyEvent(1);
		closer.verifyKey(event);

		// No tag to close
		assertEquals(">", document.get());
		assertTrue(event.doit);
	}

	@Test
	public void testEmptyTag() throws Exception
	{
		IDocument document = setDocument("<");
		VerifyEvent event = createGreaterThanKeyEvent(1);
		closer.verifyKey(event);

		// Empty tag, don't close it
		assertEquals("<", document.get());
		assertTrue(event.doit);
	}

	@Test
	public void testCloseEmptyTag() throws Exception
	{
		IDocument document = setDocument("<>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		// Adding a new > after the <>, so, do nothing.
		assertEquals("<>", document.get());
		assertTrue(event.doit);
	}

	protected VerifyEvent createGreaterThanKeyEvent(int offset)
	{
		Event e = new Event();
		e.character = '>';
		e.text = ">";
		e.start = offset;
		e.keyCode = 46;
		e.end = offset;
		e.doit = true;
		e.widget = viewer.getTextWidget();
		viewer.setSelectedRange(offset, 0);
		return new VerifyEvent(e);
	}

	protected IDocument setDocument(String string) throws CoreException
	{
		final IDocument document = new Document(string);
		viewer.setDocument(document);
		XMLDocumentProvider provider = new XMLDocumentProvider()
		{
			@Override
			public IDocument getDocument(Object element)
			{
				return document;
			}
		};
		provider.connect(document);
		return document;
	}
}
