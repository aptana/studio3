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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

public class PeerCharacterCloserTest
{

	private static List<Character> DEFAULT_PAIRS;

	private PeerCharacterCloser closer;
	private ITextViewer viewer;
	private IDocument document;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		DEFAULT_PAIRS = new ArrayList<Character>();
		DEFAULT_PAIRS.add('[');
		DEFAULT_PAIRS.add(']');
		DEFAULT_PAIRS.add('(');
		DEFAULT_PAIRS.add(')');
		DEFAULT_PAIRS.add('{');
		DEFAULT_PAIRS.add('}');
		DEFAULT_PAIRS.add('\'');
		DEFAULT_PAIRS.add('\'');
		DEFAULT_PAIRS.add('"');
		DEFAULT_PAIRS.add('"');
		DEFAULT_PAIRS.add('<');
		DEFAULT_PAIRS.add('>');
		DEFAULT_PAIRS.add('`');
		DEFAULT_PAIRS.add('`');
		viewer = new TextViewer(new Shell(), SWT.NONE);
		closer = new PeerCharacterCloser(viewer)
		{
			protected List<Character> getPairs(String scope)
			{
				return DEFAULT_PAIRS;
			}

			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				return "source.js";
			}
		};
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		viewer = null;
		closer = null;
		document = null;
//		super.tearDown();
	}

	// TODO Add tests so we can verify that newline inside () inserts it, but in "" moves to exit of linked mode

	@Test
	public void testDoesntDoubleEndingUnclosedPair()
	{
		setDocument("\" ");
		VerifyEvent event = sendEvent('"');

		// This looks kind of wrong here because the doc should really be '"" ', but since we're hacking the event
		// mechanism we're not really sending a keystroke here.
		assertTrue(event.doit); // don't interfere
		assertEquals("\" ", document.get());
	}

	@Test
	public void testAutoClosePair()
	{
		setDocument(" ");
		VerifyEvent event = sendEvent('"');

		assertFalse(event.doit);
		assertEquals("\"\" ", document.get());
	}

	@Test
	public void testDontCloseWhenSimpleUnopenedPairCloseCharFollows()
	{
		setDocument(" ) ");
		VerifyEvent event = sendEvent('(');

		assertTrue(event.doit); // don't interfere
	}

	@Test
	public void testDontCloseWhenUnOpenedPairFollows()
	{
		setDocument(" ()) ");
		VerifyEvent event = sendEvent('(');

		assertTrue(event.doit); // don't interfere
	}

	@Test
	public void testWrapSelected()
	{
		setDocument("selected ");
		viewer.setSelectedRange(0, 8);
		VerifyEvent event = sendEvent('"');

		assertFalse(event.doit);
		assertEquals("\"selected\" ", document.get());
	}

	@Test
	public void testUnpairedClose() throws Exception
	{
		closer = new PeerCharacterCloser(null)
		{
			protected List<Character> getPairs(String scope)
			{
				List<Character> pairs = new ArrayList<Character>();
				pairs.add('(');
				pairs.add(')');
				pairs.add('"');
				pairs.add('"');
				return pairs;
			}
		};
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

	@Test
	public void testDontCloseSingleQuotesInComment()
	{
		setDocument(" ");
		closer = new PeerCharacterCloser(viewer)
		{

			protected List<Character> getPairs(String scope)
			{
				List<Character> pairs = new ArrayList<Character>();
				pairs.add('(');
				pairs.add(')');
				pairs.add('"');
				pairs.add('"');
				return pairs;
			}

			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				return "source.js comment.block";
			}
		};
		VerifyEvent event = sendEvent('\'');

		assertTrue(event.doit); // don't interfere
	}

	@Test
	public void testDontCountCharsInTrailingCommentsForDeterminingPairBalance()
	{
		setDocument("\n // )");
		viewer.setSelectedRange(0, 0);
		closer = new PeerCharacterCloser(viewer)
		{
			protected List<Character> getPairs(String scope)
			{
				return DEFAULT_PAIRS;
			}

			@Override
			protected ITypedRegion[] computePartitioning(IDocument document, int offset, int length)
					throws BadLocationException
			{
				return new ITypedRegion[] { new TypedRegion(0, 2, "__js" + IDocument.DEFAULT_CONTENT_TYPE),
						new TypedRegion(2, 4, "__js_comment") };
			}
		};
		VerifyEvent event = sendEvent('(');

		assertFalse(event.doit);
		assertEquals("()\n // )", document.get());
	}

	@Test
	public void testDontCountCharsInPrecedingCommentsForDeterminingPairBalance()
	{
		document = new Document("// '\n ");
		viewer.setDocument(document);

		viewer.setSelectedRange(5, 0);
		closer = new PeerCharacterCloser(viewer)
		{

			protected List<Character> getPairs(String scope)
			{
				return DEFAULT_PAIRS;
			}

			protected ITypedRegion getPartition(IDocumentExtension3 ext, String defaultPartitioning, int offset,
					boolean b) throws BadLocationException, BadPartitioningException
			{
				if (offset >= 4)
				{
					return new TypedRegion(4, 2, "__js" + IDocument.DEFAULT_CONTENT_TYPE);
				}
				return new TypedRegion(0, 4, "__js_comment");
			}

			@Override
			protected ITypedRegion[] computePartitioning(IDocument document, int offset, int length)
					throws BadLocationException
			{
				return new ITypedRegion[] { new TypedRegion(0, 4, "__js_comment"),
						new TypedRegion(4, 2, "__js" + IDocument.DEFAULT_CONTENT_TYPE) };
			}
		};
		VerifyEvent event = sendEvent('\'');

		assertFalse(event.doit);
		assertEquals("// '\n'' ", document.get());
	}

	@Test
	public void testRR3_115()
	{
		setDocument("function x()\n" + "{\n" + "    if (false)\n" + "    \n" + "\n" + "    if (false)\n" + "    {\n"
				+ "        // scroll sub-regions\n" + "    }\n" + "};");
		viewer.setSelectedRange(34, 0);
		closer = new PeerCharacterCloser(viewer)
		{

			protected List<Character> getPairs(String scope)
			{
				return DEFAULT_PAIRS;
			}

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

	@Test
	public void testStudio3_1213()
	{
		setDocument("bundle do |bundle|\n" + "  bundle.author = 'Ed Spencer'\n"
				+ "  bundle.contact_email_rot_13 = 'null'\n" + "  bundle.description =  \"A bundle for ExtJS\"\n"
				+ "\n" + "  bundle.menu \"ExtJS do |main_menu|\n" + "  end\n" + "end");
		viewer.setSelectedRange(154, 0);
		closer = new PeerCharacterCloser(viewer)
		{

			protected List<Character> getPairs(String scope)
			{
				return DEFAULT_PAIRS;
			}

			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				if (offset >= 148) // Add the first double string too
					return "source.ruby.rails string.quoted.double.ruby";
				if ((offset >= 37 && offset <= 48) || (offset >= 82 && offset <= 87))
					return "source.ruby.rails string.quoted.single.ruby";
				return "source.ruby.rails";
			}
		};
		VerifyEvent event = sendEvent('"');

		assertTrue(event.doit); // Don't pair, insert single character!
	}

	@Test
	public void testignoresRubyHashesForHTMLTagPairs()
	{
		// FIXME This is pretty ugly here. We should probably have just created a temp file, opened it with our editor
		// and then sent a keypress to it...
		String src = "<%= stylesheet_link_tag 'iphone', :media => 'only screen and (max-device-width: 480px)' %>\n ";
		document = new Document(src)
		{
			public ITypedRegion[] computePartitioning(int offset, int length) throws BadLocationException
			{
				return new ITypedRegion[] { new TypedRegion(0, 3, "__common_start_switch_tag"),
						new TypedRegion(3, 21, "__rb__dftl_partition_content_type"),
						new TypedRegion(24, 8, "__rb_string_single"),
						new TypedRegion(32, 12, "__rb__dftl_partition_content_type"),
						new TypedRegion(44, 43, "__rb_string_single"),
						new TypedRegion(87, 1, "__rb__dftl_partition_content_type"),
						new TypedRegion(88, 2, "__common_end_switch_tag"),
						new TypedRegion(90, 1, "__html__dftl_partition_content_type") };
			}

			public String getContentType(int offset) throws BadLocationException
			{
				return "__html__dftl_partition_content_type";
			}
		};
		viewer.setDocument(document);
		viewer.setSelectedRange(src.length() - 1, 0);
		closer = new PeerCharacterCloser(viewer)
		{

			protected List<Character> getPairs(String scope)
			{
				return DEFAULT_PAIRS;
			}

			@Override
			protected String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
			{
				if ((offset >= 0 && offset <= 3) || (offset >= 89 && offset <= 90))
					return "text.html.ruby source.erb.embedded.html";
				if ((offset >= 4 && offset <= 24) || (offset >= 33 && offset <= 44) || (offset == 88))
					return "text.html.ruby source.ruby.rails.embedded.html";
				if ((offset >= 25 && offset <= 32) || (offset >= 45 && offset <= 87))
					return "text.html.ruby source.ruby.rails.embedded.html string.quoted.single.ruby";
				return "text.html.ruby";
			}
		};
		VerifyEvent event = sendEvent('<');
		// Make sure we pair this!
		assertFalse("Should have paired the character!", event.doit);
		assertEquals("<%= stylesheet_link_tag 'iphone', :media => 'only screen and (max-device-width: 480px)' %>\n<> ",
				document.get());
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
