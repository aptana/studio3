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
package com.aptana.editor.common.internal.peer;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.TypedRegion;
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
		closer = new PeerCharacterCloser(viewer)
		{
			protected char[] getPairs(String scope)
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
		final char[] pairs = new char[] { '(', ')', '"', '"' };
		closer = new PeerCharacterCloser(null)
		{
			protected char[] getPairs(String scope)
			{
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

	public void testDontCloseSingleQuotesInComment()
	{
		setDocument(" ");
		closer = new PeerCharacterCloser(viewer)
		{

			protected char[] getPairs(String scope)
			{
				return new char[] { '(', ')', '"', '"'};
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

	public void testDontCountCharsInTrailingCommentsForDeterminingPairBalance()
	{
		setDocument("\n // )");
		viewer.setSelectedRange(0, 0);
		closer = new PeerCharacterCloser(viewer)
		{
			protected char[] getPairs(String scope)
			{
				return DEFAULT_PAIRS;
			}

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
		document = new Document("// '\n ")
		{
			@Override
			public ITypedRegion getPartition(String partitioning, int offset, boolean preferOpenPartitions)
					throws BadLocationException, BadPartitioningException
			{
				if (offset >= 0 && offset <= 4)
					return new TypedRegion(0, 4, "comment");
				return new TypedRegion(4, 2, DEFAULT_PARTITIONING);
			}
		};
		viewer.setDocument(document);
		
		viewer.setSelectedRange(5, 0);
		closer = new PeerCharacterCloser(viewer)
		{

			protected char[] getPairs(String scope)
			{
				return DEFAULT_PAIRS;
			}

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
		closer = new PeerCharacterCloser(viewer)
		{

			protected char[] getPairs(String scope)
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
	
	public void testStudio3_1213()
	{
		setDocument("bundle do |bundle|\n" +
"  bundle.author = 'Ed Spencer'\n" +
"  bundle.contact_email_rot_13 = 'null'\n" +
"  bundle.description =  \"A bundle for ExtJS\"\n" +
"\n" +
"  bundle.menu \"ExtJS do |main_menu|\n" +
"  end\n" +
"end");
		viewer.setSelectedRange(154, 0);
		closer = new PeerCharacterCloser(viewer)
		{

			protected char[] getPairs(String scope)
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
