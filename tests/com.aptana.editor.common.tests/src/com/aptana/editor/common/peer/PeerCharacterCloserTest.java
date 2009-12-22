package com.aptana.editor.common.peer;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import junit.framework.TestCase;

public class PeerCharacterCloserTest extends TestCase
{

	public void testDoesntDoubleEndingUnclosedPair()
	{		
		ITextViewer viewer = new TextViewer(Display.getDefault().getActiveShell(), SWT.NONE);
		IDocument document = new Document("\" ");
		viewer.setDocument(document);
		PeerCharacterCloser closer = new PeerCharacterCloser(viewer);
		Event e = new Event();
		e.character ='"';
		e.start = 0;
		e.end = 0;
		e.keyCode = 39;
		e.doit = true;
		e.stateMask = 131072;
		e.widget = viewer.getTextWidget();
		VerifyEvent event = new VerifyEvent(e);
		closer.verifyKey(event);
		
		// This looks kind of wrong here because the doc should really be '"" ', but since we're hacking the event mechanism we're not really sending a keystroke here.
		assertEquals("\" ", document.get());
	}
	
	public void testAutoClosePair()
	{		
		ITextViewer viewer = new TextViewer(Display.getDefault().getActiveShell(), SWT.NONE);
		IDocument document = new Document(" ");
		viewer.setDocument(document);
		PeerCharacterCloser closer = new PeerCharacterCloser(viewer);
		Event e = new Event();
		e.character ='"';
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
	
	public void testWrapSelected()
	{		
		ITextViewer viewer = new TextViewer(Display.getDefault().getActiveShell(), SWT.NONE);
		IDocument document = new Document("selected ");
		viewer.setDocument(document);
		viewer.setSelectedRange(0, 8);
		PeerCharacterCloser closer = new PeerCharacterCloser(viewer);
		Event e = new Event();
		e.character ='"';
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
}
