package com.aptana.editor.common.internal.peer;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.aptana.editor.common.internal.peer.PeerCharacterCloser;

import junit.framework.TestCase;

public class PeerCharacterCloserTest extends TestCase
{

	private static final char[] DEFAULT_PAIRS = new char[] { '[', ']', '(', ')', '{', '}', '\'', '\'', '"', '"', '<',
			'>', '`', '`' };

	public void testDoesntDoubleEndingUnclosedPair()
	{
		ITextViewer viewer = new TextViewer(Display.getDefault().getActiveShell(), SWT.NONE);
		IDocument document = new Document("\" ");
		viewer.setDocument(document);
		PeerCharacterCloser closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS);
		Event e = new Event();
		e.character = '"';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		// This looks kind of wrong here because the doc should really be '"" ', but since we're hacking the event
		// mechanism we're not really sending a keystroke here.
		assertEquals("\" ", document.get());
	}

	public void testAutoClosePair()
	{
		ITextViewer viewer = new TextViewer(Display.getDefault().getActiveShell(), SWT.NONE);
		IDocument document = new Document(" ");
		viewer.setDocument(document);
		PeerCharacterCloser closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS);
		Event e = new Event();
		e.character = '"';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		assertFalse(event.doit);
		assertEquals("\"\" ", document.get());
	}
	
	public void testDontCloseWhenSimpleUnopenedPairCloseCharFollows()
	{
		ITextViewer viewer = new TextViewer(Display.getDefault().getActiveShell(), SWT.NONE);
		IDocument document = new Document(" ) ");
		viewer.setDocument(document);
		PeerCharacterCloser closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS);
		Event e = new Event();
		e.character = '(';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		assertTrue(event.doit); // don't interfere
	}
	
	public void testDontCloseWhenUnOpenedPairFollows()
	{
		ITextViewer viewer = new TextViewer(Display.getDefault().getActiveShell(), SWT.NONE);
		IDocument document = new Document(" ()) ");
		viewer.setDocument(document);
		PeerCharacterCloser closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS);
		Event e = new Event();
		e.character = '(';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		assertTrue(event.doit); // don't interfere
	}

	public void testWrapSelected()
	{
		ITextViewer viewer = new TextViewer(Display.getDefault().getActiveShell(), SWT.NONE);
		IDocument document = new Document("selected ");
		viewer.setDocument(document);
		viewer.setSelectedRange(0, 8);
		PeerCharacterCloser closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS);
		Event e = new Event();
		e.character = '"';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);

		assertFalse(event.doit);
		assertEquals("\"selected\" ", document.get());
	}
	
	public void testUnpairedClose() throws Exception
	{
		char[] pairs = new char[] { '(', ')', '"', '"' };
		PeerCharacterCloser closer = new PeerCharacterCloser(null, pairs);
		StringBuilder builder = new StringBuilder();
		int times = 5000;
		for (int i = 0; i < times; i++)
		{
			builder.append("(");
		}
		for (int i = 0; i < times; i++)
		{
			builder.append(")");
		}
		assertFalse(closer.unpairedClose('(', ')', new Document(builder.toString()), times));
		builder.append(")");
		assertTrue(closer.unpairedClose('(', ')', new Document(builder.toString()), times));

	}
}
