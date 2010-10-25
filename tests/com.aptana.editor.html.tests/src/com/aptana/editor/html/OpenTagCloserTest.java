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
package com.aptana.editor.html;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class OpenTagCloserTest extends TestCase
{
	private TextViewer viewer;
	private OpenTagCloser closer;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell shell = display.getActiveShell();
		if (shell == null)
			shell = new Shell(display);
		viewer = new TextViewer(shell, SWT.NONE);
		closer = new OpenTagCloser(viewer)
		{
			protected boolean shouldAutoClose(IDocument document, int offset, VerifyEvent event)
			{
				return true;
			};
		};
	}

	public void testDoesntCloseIfIsClosingTag()
	{
		IDocument document = setDocument("</p");
		VerifyEvent event = createGreaterThanKeyEvent(3);
		closer.verifyKey(event);

		// This looks wrong, but we didn't add our close tag and the event will eventually finish and stick the '>' in
		assertEquals("</p", document.get());
		assertTrue(event.doit);
	}

	public void testCloseOpenTag()
	{
		IDocument document = setDocument("<p");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p>", document.get());
		assertFalse(event.doit);
	}

	public void testDoesntCloseIfNextTagIsClosingTag()
	{
		IDocument document = setDocument("<p </p>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		// This looks wrong, but we didn't add our close tag and the event will eventually finish and stick the '>' in
		assertEquals("<p </p>", document.get());
		assertTrue(event.doit);
	}

	public void testDoesCloseIfNextTagIsNotClosingTag()
	{
		IDocument document = setDocument("<p <div></div>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p> <div></div>", document.get());
		assertFalse(event.doit);
		assertEquals(3, viewer.getSelectedRange().x);
	}

	public void testDoesntCloseIfClosedLater()
	{
		IDocument document = setDocument("<p <b></b></p>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p <b></b></p>", document.get());
		assertTrue(event.doit);
	}

	public void testDoesCloseIfNotClosedButPairFollows()
	{
		IDocument document = setDocument("<p <b></b><p></p>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p> <b></b><p></p>", document.get());
		assertFalse(event.doit);
		assertEquals(3, viewer.getSelectedRange().x);
	}

	public void testDoesCloseIfNextCharIsLessThanAndWeNeedToClose()
	{
		IDocument document = setDocument("<p>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p>", document.get());
		assertFalse(event.doit);
		assertEquals(3, viewer.getSelectedRange().x);
	}

	public void testDoesCloseProperlyWithOpenTagContaingAttrsIfNextCharIsLessThanAndWeNeedToClose()
	{
		IDocument document = setDocument("<a href=\"\">");
		VerifyEvent event = createGreaterThanKeyEvent(10);
		closer.verifyKey(event);

		assertEquals("<a href=\"\"></a>", document.get());
		assertFalse(event.doit);
		assertEquals(11, viewer.getSelectedRange().x);
	}

	public void testDoesntCloseIfNextCharIsLessThanAndWeDontNeedToCloseButOverwritesExistingLessThan()
	{
		IDocument document = setDocument("<p></p>");
		VerifyEvent event = createGreaterThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p>", document.get());
		assertFalse(event.doit);
		assertEquals(3, viewer.getSelectedRange().x);
	}

	public void testDoesntCloseImplicitSelfClosingTag()
	{
		IDocument document = setDocument("<br");
		VerifyEvent event = createGreaterThanKeyEvent(4);
		closer.verifyKey(event);

		assertEquals("<br", document.get());
		assertTrue(event.doit);
	}

	public void testDoesntCloseExplicitSelfClosingTag()
	{
		IDocument document = setDocument("<br/");
		VerifyEvent event = createGreaterThanKeyEvent(4);
		closer.verifyKey(event);

		assertEquals("<br/", document.get());
		assertTrue(event.doit);
		assertEquals(4, viewer.getSelectedRange().x);
	}

	public void testDoesntCloseExplicitSelfClosingTagWithExtraSpaces()
	{
		IDocument document = setDocument("<br /");
		VerifyEvent event = createGreaterThanKeyEvent(5);
		closer.verifyKey(event);

		assertEquals("<br /", document.get());
		assertTrue(event.doit);
	}

	public void testDoesntCloseSpecialERBTags()
	{
		IDocument document = setDocument("<%= %");
		VerifyEvent event = createGreaterThanKeyEvent(5);
		closer.verifyKey(event);

		assertEquals("<%= %", document.get());
		assertTrue(event.doit);
	}

	public void testDoesntCloseComments()
	{
		IDocument document = setDocument("<!-- ");
		VerifyEvent event = createGreaterThanKeyEvent(5);
		closer.verifyKey(event);

		assertEquals("<!-- ", document.get());
		assertTrue(event.doit);
	}

	public void testDoesStickCursorBetweenAutoClosedTagPair()
	{
		IDocument document = setDocument("<html>");
		VerifyEvent event = createGreaterThanKeyEvent(6);
		closer.verifyKey(event);

		assertEquals("<html></html>", document.get());
		assertFalse(event.doit);
		assertEquals(6, viewer.getTextWidget().getCaretOffset());
		assertEquals(6, viewer.getSelectedRange().x);
	}

	public void testDoesntCloseIfHasSpacesInOpenTagAndHasClosingTag()
	{
		IDocument document = setDocument("<script src=\"http://example.org/src.js\">\n\n</script>");
		VerifyEvent event = createGreaterThanKeyEvent(39);
		closer.verifyKey(event);

		assertEquals("<script src=\"http://example.org/src.js\">\n\n</script>", document.get());
		assertFalse(event.doit); // don't insert it, we'll just overwrite '>'
		assertEquals(40, viewer.getSelectedRange().x);
	}

	protected VerifyEvent createGreaterThanKeyEvent(int offset)
	{
		Event e = new Event();
		e.character = '>';
		e.start = 0;
		e.keyCode = 46;
		e.end = 0;
		e.doit = true;
		e.widget = viewer.getTextWidget();
		viewer.setSelectedRange(offset, 0);
		return new VerifyEvent(e);
	}

	private IDocument setDocument(String string)
	{
		IDocument document = new Document(string);
		viewer.setDocument(document);
		return document;
	}
}
