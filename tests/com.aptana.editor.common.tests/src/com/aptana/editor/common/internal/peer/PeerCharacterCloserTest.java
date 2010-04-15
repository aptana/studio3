package com.aptana.editor.common.internal.peer;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

public class PeerCharacterCloserTest extends TestCase
{

	private static final char[] DEFAULT_PAIRS = new char[] { '[', ']', '(', ')', '{', '}', '\'', '\'', '"', '"', '<',
			'>', '`', '`' };

	private PeerCharacterCloser closer;
	private ITextViewer viewer;
	private IDocument document;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		viewer = new TextViewer(new Shell(), SWT.NONE);
		closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS)
		{
			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				return "source.js";
			}
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		viewer = null;
		closer = null;
		document = null;
		super.tearDown();
	}

	// TODO Add tests so we can verify that newline inside () inserts it, but in "" moves to exit of linked mode
	
	public void testDoesntDoubleEndingUnclosedPair()
	{
		setDocument("\" ");
		VerifyEvent event = sendEvent('"');

		// This looks kind of wrong here because the doc should really be '"" ', but since we're hacking the event
		// mechanism we're not really sending a keystroke here.
		assertTrue(event.doit); // don't interfere
		assertEquals("\" ", document.get());
	}

	public void testAutoClosePair()
	{
		setDocument(" ");
		VerifyEvent event = sendEvent('"');

		assertFalse(event.doit);
		assertEquals("\"\" ", document.get());
	}

	public void testDontCloseWhenSimpleUnopenedPairCloseCharFollows()
	{
		setDocument(" ) ");
		VerifyEvent event = sendEvent('(');

		assertTrue(event.doit); // don't interfere
	}

	public void testDontCloseWhenUnOpenedPairFollows()
	{
		setDocument(" ()) ");
		VerifyEvent event = sendEvent('(');

		assertTrue(event.doit); // don't interfere
	}

	public void testWrapSelected()
	{
		setDocument("selected ");
		viewer.setSelectedRange(0, 8);
		VerifyEvent event = sendEvent('"');

		assertFalse(event.doit);
		assertEquals("\"selected\" ", document.get());
	}

	public void testUnpairedClose() throws Exception
	{
		char[] pairs = new char[] { '(', ')', '"', '"' };
		closer = new PeerCharacterCloser(null, pairs);
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

	public void testDontCloseWhenScopeIsComment()
	{
		setDocument(" ");
		closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS)
		{
			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				return "source.js comment.block";
			}
		};
		VerifyEvent event = sendEvent('(');

		assertTrue(event.doit); // don't interfere
	}

	public void testDontCountCharsInTrailingCommentsForDeterminingPairBalance()
	{
		setDocument("\n // )");
		viewer.setSelectedRange(0, 0);
		closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS)
		{
			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				if (offset >= 2)
					return "source.js comment.block";
				return "source.js";
			}
		};
		VerifyEvent event = sendEvent('(');

		assertFalse(event.doit);
		assertEquals("()\n // )", document.get());
	}

	public void testDontCountCharsInPrecedingCommentsForDeterminingPairBalance()
	{
		setDocument("// '\n ");
		viewer.setSelectedRange(5, 0);
		closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS)
		{
			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				if (offset <= 4)
					return "source.js comment.block";
				return "source.js";
			}
		};
		VerifyEvent event = sendEvent('\'');

		assertFalse(event.doit);
		assertEquals("// '\n'' ", document.get());
	}

	public void testRR3_115()
	{
		setDocument("function x()\n" + "{\n" + "    if (false)\n" + "    \n" + "\n" + "    if (false)\n" + "    {\n"
				+ "        // scroll sub-regions\n" + "    }\n" + "};");
		viewer.setSelectedRange(34, 0);
		closer = new PeerCharacterCloser(viewer, DEFAULT_PAIRS)
		{
			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				if (offset >= 65 && offset <= 85)
					return "source.js comment.block";
				return "source.js";
			}
		};
		VerifyEvent event = sendEvent('{');

		assertFalse(event.doit);
		assertEquals("function x()\n" + "{\n" + "    if (false)\n" + "    {}\n" + "\n" + "    if (false)\n" + "    {\n"
				+ "        // scroll sub-regions\n" + "    }\n" + "};", document.get());
	}

	protected IDocument setDocument(String src)
	{
		document = new Document(src);
		viewer.setDocument(document);
		return document;
	}

	protected VerifyEvent sendEvent(char c)
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
		closer.verifyKey(event);
		return event;
	}
}
