package com.aptana.editor.html;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public class OpenTagCloserTest extends TestCase
{
	private TextViewer viewer;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		viewer = new TextViewer(Display.getDefault().getActiveShell(), SWT.NONE);
	}

	public void testDoesntCloseIfIsClosingTag()
	{
		IDocument document = setDocument("</p");
		OpenTagCloser closer = new OpenTagCloser(viewer);
		VerifyEvent event = createLessThanKeyEvent(3);
		closer.verifyKey(event);

		// This looks wrong, but we didn't add our close tag and the event will eventually finish and stick the '>' in
		assertEquals("</p", document.get());
		assertTrue(event.doit);
	}

	public void testCloseOpenTag()
	{
		IDocument document = setDocument("<p");
		OpenTagCloser closer = new OpenTagCloser(viewer);
		VerifyEvent event = createLessThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p>", document.get());
		assertFalse(event.doit);
	}

	public void testDoesntCloseIfNextTagIsClosingTag()
	{
		IDocument document = setDocument("<p </p>");
		OpenTagCloser closer = new OpenTagCloser(viewer);
		VerifyEvent event = createLessThanKeyEvent(2);
		closer.verifyKey(event);

		// This looks wrong, but we didn't add our close tag and the event will eventually finish and stick the '>' in
		assertEquals("<p </p>", document.get());
		assertTrue(event.doit);
	}

	public void testDoesCloseIfNextTagIsNotClosingTag()
	{
		// FIXME This particular case is actually incorrect since the tag is eventually closed
		IDocument document = setDocument("<p <b></b></p>");
		OpenTagCloser closer = new OpenTagCloser(viewer);
		VerifyEvent event = createLessThanKeyEvent(2);
		closer.verifyKey(event);

		assertEquals("<p></p> <b></b></p>", document.get());
		assertFalse(event.doit);
	}

	protected VerifyEvent createLessThanKeyEvent(int offset)
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
