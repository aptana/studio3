/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.junit.Before;
import org.junit.Test;

public class HTMLOpenTagCloserTest
{
	protected TextViewer viewer;
	protected HTMLOpenTagCloser closer;

	@Before
	public void setUp() throws Exception
	{
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell shell = display.getActiveShell();
		if (shell == null)
		{
			shell = new Shell(display);
		}
		viewer = new TextViewer(shell, SWT.NONE);
		closer = new HTMLOpenTagCloser(viewer);
	}

	@Test
	public void testDoesntCloseIfIsClosingTag() throws Exception
	{
		IDocument document = setDocument("</p");
		VerifyEvent event = createGreaterThanKeyEvent(3);
		closer.verifyKey(event);

		// This looks wrong, but we didn't add our close tag and the event will eventually finish and stick the '>' in
		assertEquals("</p", document.get());
		assertTrue(event.doit);
	}

	@Test
	public void testCloseOpenTag() throws Exception
	{
		IDocument document = setDocument("<p");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p>", document.get());
		assertFalse(event.doit);
	}

	@Test
	public void testCloseOpenScriptTag() throws Exception
	{
		IDocument document = setDocument("<script");
		VerifyEvent event = createGreaterThanKeyEvent(7);
		closer.verifyKey(event);

		assertEquals("<script></script>", document.get());
		assertFalse(event.doit);
	}

	@Test
	public void testCloseOpenStyleTag() throws Exception
	{
		IDocument document = setDocument("<style");
		VerifyEvent event = createGreaterThanKeyEvent(6);
		closer.verifyKey(event);

		assertEquals("<style></style>", document.get());
		assertFalse(event.doit);
	}

	@Test
	public void testDoesntCloseIfNextTagIsClosingTag() throws Exception
	{
		IDocument document = setDocument("<p </p>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		// This looks wrong, but we didn't add our close tag and the event will eventually finish and stick the '>' in
		assertEquals("<p </p>", document.get());
		assertTrue(event.doit);
	}

	@Test
	public void testDoesCloseIfNextTagIsNotClosingTag() throws Exception
	{
		IDocument document = setDocument("<p <div></div>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p> <div></div>", document.get());
		assertFalse(event.doit);
		assertEquals(3, viewer.getSelectedRange().x);
	}

	@Test
	public void testDoesntCloseIfClosedLater() throws Exception
	{
		IDocument document = setDocument("<p <b></b></p>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p <b></b></p>", document.get());
		assertTrue(event.doit);
	}

	@Test
	public void testDoesCloseIfNotClosedButPairFollows() throws Exception
	{
		IDocument document = setDocument("<p <b></b><p></p>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p> <b></b><p></p>", document.get());
		assertFalse(event.doit);
		assertEquals(3, viewer.getSelectedRange().x);
	}

	@Test
	public void testDoesCloseIfNextCharIsLessThanAndWeNeedToClose() throws Exception
	{
		IDocument document = setDocument("<p>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p>", document.get());
		assertFalse(event.doit);
		assertEquals(3, viewer.getSelectedRange().x);
	}

	@Test
	public void testDoesCloseProperlyWithOpenTagContaingAttrsIfNextCharIsLessThanAndWeNeedToClose() throws Exception
	{
		IDocument document = setDocument("<a href=\"\">");
		VerifyEvent event = createGreaterThanKeyEvent(10);
		closer.verifyKey(event);

		assertEquals("<a href=\"\"></a>", document.get());
		assertFalse(event.doit);
		assertEquals(11, viewer.getSelectedRange().x);
	}

	@Test
	public void testDoesntCloseIfNextCharIsLessThanAndWeDontNeedToCloseButOverwritesExistingLessThan() throws Exception
	{
		IDocument document = setDocument("<p></p>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p>", document.get());
		assertFalse(event.doit);
		assertEquals(3, viewer.getSelectedRange().x);
	}

	@Test
	public void testDoesntCloseImplicitSelfClosingTag() throws Exception
	{
		IDocument document = setDocument("<br");
		VerifyEvent event = createGreaterThanKeyEvent(4);
		closer.verifyKey(event);

		assertEquals("<br", document.get());
		assertTrue(event.doit);
	}

	@Test
	public void testDoesntCloseExplicitSelfClosingTag() throws Exception
	{
		IDocument document = setDocument("<br/");
		VerifyEvent event = createGreaterThanKeyEvent(4);
		closer.verifyKey(event);

		assertEquals("<br/", document.get());
		assertTrue(event.doit);
		assertEquals(4, viewer.getSelectedRange().x);
	}

	@Test
	public void testDoesntCloseExplicitSelfClosingTagWithExtraSpaces() throws Exception
	{
		IDocument document = setDocument("<br /");
		VerifyEvent event = createGreaterThanKeyEvent(5);
		closer.verifyKey(event);

		assertEquals("<br /", document.get());
		assertTrue(event.doit);
	}

	@Test
	public void testDoesntCloseComments() throws Exception
	{
		IDocument document = setDocument("<!-- ");
		VerifyEvent event = createGreaterThanKeyEvent(5);
		closer.verifyKey(event);

		assertEquals("<!-- ", document.get());
		assertTrue(event.doit);
	}

	// test for http://jira.appcelerator.org/browse/TISTUD-270
	@Test
	public void testTISTUD_270() throws Exception
	{
		IDocument document = setDocument("<p><span</p>");
		VerifyEvent event = createGreaterThanKeyEvent(8);
		closer.verifyKey(event);

		assertEquals("<p><span></span></p>", document.get());
		assertFalse(event.doit);
	}

	// https://aptana.lighthouseapp.com/projects/35272/tickets/1592-cursor-moves-back-one-column-when-auto-inserting-closing-tag-in-html
	@Test
	public void testDoesStickCursorBetweenAutoClosedTagPair() throws Exception
	{
		IDocument document = setDocument("<html>");
		VerifyEvent event = createGreaterThanKeyEvent(6);
		closer.verifyKey(event);

		assertEquals("<html></html>", document.get());
		assertFalse(event.doit);
		assertEquals(6, viewer.getTextWidget().getCaretOffset());
		assertEquals(6, viewer.getSelectedRange().x);
	}

	@Test
	public void testDoesntCloseIfHasSpacesInOpenTagAndHasClosingTag() throws Exception
	{
		IDocument document = setDocument("<script src=\"http://example.org/src.js\">\n\n</script>");
		VerifyEvent event = createGreaterThanKeyEvent(39);
		closer.verifyKey(event);

		assertEquals("<script src=\"http://example.org/src.js\">\n\n</script>", document.get());
		assertFalse(event.doit); // don't insert it, we'll just overwrite '>'
		assertEquals(40, viewer.getSelectedRange().x);
	}

	@Test
	public void testOverwritesExistingGreaterThanAtEndOfPHPTag() throws Exception
	{
		IDocument document = setDocument("<input type='text' name='foo' <?=$blah?>>");
		VerifyEvent event = createGreaterThanKeyEvent(39);
		closer.verifyKey(event);

		assertEquals("<input type='text' name='foo' <?=$blah?>>", document.get());
		assertFalse(event.doit);
	}

	@Test
	public void testDoesntOverwriteExistingGreaterThanIfPHPTagIsClosedButHTMLIsnt() throws Exception
	{
		IDocument document = setDocument("<input type='text' name='foo' <?=$blah?>");
		VerifyEvent event = createGreaterThanKeyEvent(39);
		closer.verifyKey(event);

		if (event.doit)
		{
			document.replace(event.start, event.end - event.start, event.text);
		}
		assertEquals("<input type='text' name='foo' <?=$blah?>>", document.get());
	}

	@Test
	public void testWhatever() throws Exception
	{
		IDocument document = setDocument("<input type='text' name='foo' <?=$blah? >");
		VerifyEvent event = createGreaterThanKeyEvent(39);
		closer.verifyKey(event);

		if (event.doit)
		{
			document.replace(event.start, event.end - event.start, event.text);
		}
		assertEquals("<input type='text' name='foo' <?=$blah?> >", document.get());
	}

	@Test
	public void testWhatever2() throws Exception
	{
		IDocument document = setDocument("<input type='text' name='foo' <?=$blah?");
		VerifyEvent event = createGreaterThanKeyEvent(39);
		closer.verifyKey(event);

		assertTrue(event.doit);
		document.replace(event.start, event.end - event.start, event.text);
		assertEquals("<input type='text' name='foo' <?=$blah?>", document.get());
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
		HTMLDocumentProvider provider = new HTMLDocumentProvider()
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
