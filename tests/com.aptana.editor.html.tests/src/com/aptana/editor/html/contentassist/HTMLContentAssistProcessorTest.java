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
package com.aptana.editor.html.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.html.HTMLMetadataLoader;
import com.aptana.editor.html.HTMLTestUtil;
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor.LocationType;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;

public class HTMLContentAssistProcessorTest extends LocationTestCase
{

	private static final int ELEMENT_PROPOSALS_COUNT = 132;
	private static final int DOCTYPE_PROPOSALS_COUNT = 11;
	private static final int CLOSE_TAG_PROPOSALS_COUNT = 119;

	private HTMLContentAssistProcessor fProcessor;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		HTMLMetadataLoader loader = new HTMLMetadataLoader();
		loader.schedule();
		loader.join();

		fProcessor = new HTMLContentAssistProcessor(null);
	}

	@Override
	protected void tearDown() throws Exception
	{
		fProcessor = null;
		super.tearDown();
	}

	public void testLinkProposal()
	{
		assertCompletionCorrect("<|", '\t', ELEMENT_PROPOSALS_COUNT, "a", "<a></a>", null);
	}

	public void testDOCTYPEProposal()
	{
		Point p = new Point(10, 0);
		assertCompletionCorrect("<!|", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", p);
		assertCompletionCorrect("<!D|", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", p);
		assertCompletionCorrect("<!D| html>", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE html>", p);
		assertCompletionCorrect("<|>", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", p);
		assertCompletionCorrect("<!|>", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", p);
		assertCompletionCorrect("<!D|OCTYP >", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", p);
		assertCompletionCorrect("<!D|OCTYPE >", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", p);
	}

	public void testDOCTYPEValueReplacement()
	{
		assertCompletionCorrect("<!DOCTYPE |html>", '\t', DOCTYPE_PROPOSALS_COUNT, "HTML 5", "<!DOCTYPE HTML>", null);
		assertCompletionCorrect(
				"<!DOCTYPE |html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n	\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">",
				'\t', DOCTYPE_PROPOSALS_COUNT, "HTML 5", "<!DOCTYPE HTML>", null);
	}

	public void testCloseTagProposal()
	{
		String document = "<a>Test <b>Item</b>";
		IDocument fDocument = createDocument(document, false);

		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);

		// Should be no unclosed tags at this point
		fProcessor.doComputeCompletionProposals(viewer, 0, trigger, false);
		assertEquals(0, fProcessor.getUnclosedTagNames(0).size());

		fProcessor.doComputeCompletionProposals(viewer, 1, trigger, false);
		assertEquals(0, fProcessor.getUnclosedTagNames(1).size());

		fProcessor.doComputeCompletionProposals(viewer, 2, trigger, false);
		assertEquals(0, fProcessor.getUnclosedTagNames(2).size());

		fProcessor.doComputeCompletionProposals(viewer, 3, trigger, false);
		assertEquals(1, fProcessor.getUnclosedTagNames(3).size());

		// show unclosed tag once we get past the '>'
		fProcessor.doComputeCompletionProposals(viewer, 4, trigger, false);
		assertEquals(1, fProcessor.getUnclosedTagNames(4).size());
	}

	public void testABBRProposal()
	{
		assertCompletionCorrect("<a|>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr>", null);
		assertCompletionCorrect("<A|>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr>", null);
	}

	public void testElementWhichIsClosedProposal()
	{
		assertCompletionCorrect("<|></a>", '\t', ELEMENT_PROPOSALS_COUNT, "a", "<a></a>", null);
		assertCompletionCorrect("<|></a>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr></a>", null);
		assertCompletionCorrect("<|</a>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr></a>", null);
		assertCompletionCorrect("<b><a><|</b>", '\t', ELEMENT_PROPOSALS_COUNT + 1, "/a", "<b><a></a></b>", null);
		assertCompletionCorrect("<b><a><|></b>", '\t', ELEMENT_PROPOSALS_COUNT + 1, "/a", "<b><a></a></b>", null);
		assertCompletionCorrect("<b><a></|</b>", '\t', ELEMENT_PROPOSALS_COUNT + 1, "/a", "<b><a></a></b>", null);
		assertCompletionCorrect("<b><a></|></b>", '\t', 1, "/a", "<b><a></a></b>", null);
		assertCompletionCorrect("<b><a></a|</b>", '\t', 1, "/a", "<b><a></a></b>", null);
		assertCompletionCorrect("<b><a></a|></b>", '\t', CLOSE_TAG_PROPOSALS_COUNT, "/a", "<b><a></a></b>", null);
	}

	public void testCloseTagWithNoUnclosedTagsProposal()
	{
		assertCompletionCorrect("</|>", '\t', CLOSE_TAG_PROPOSALS_COUNT, "/ul", "</ul>", null);
	}

	public void testIMGProposal()
	{
		assertCompletionCorrect("<|", '\t', ELEMENT_PROPOSALS_COUNT, "img", "<img />", null);
	}

	public void testHTML5DoctypeProposal()
	{
		assertCompletionCorrect("<!doctype |>", '\t', DOCTYPE_PROPOSALS_COUNT, "HTML 5", "<!doctype HTML>", null);
	}

	public void testHTML401StrictDoctypeProposal()
	{
		assertCompletionCorrect("<!DOCTYPE |", '\t', DOCTYPE_PROPOSALS_COUNT, "HTML 4.01 Strict",
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n\"http://www.w3.org/TR/html4/strict.dtd\"", null);
	}

	private ICompletionProposal findProposal(String string, ICompletionProposal[] proposals)
	{
		for (ICompletionProposal proposal : proposals)
		{
			if (proposal.getDisplayString().equals(string))
			{
				return proposal;
			}
		}
		return null;
	}

	protected ITextViewer createTextViewer(IDocument fDocument)
	{
		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
		viewer.setDocument(fDocument);
		return viewer;
	}

	protected void assertLocation(String document, LocationType location)
	{

		int offset = HTMLTestUtil.findCursorOffset(document);
		IDocument fDocument = HTMLTestUtil.createDocument(document, true);

		LexemeProvider<HTMLTokenType> lexemeProvider = HTMLTestUtil.createLexemeProvider(fDocument, offset);
		LocationType l = fProcessor.getOpenTagLocationType(lexemeProvider, offset);

		assertEquals(location, l);
	}

	protected void assertCompletionCorrect(String document, char trigger, int proposalCount, String proposalToSelect,
			String postCompletion, Point point)
	{
		int offset = HTMLTestUtil.findCursorOffset(document);
		IDocument fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(proposalCount, proposals.length);
		ICompletionProposal closeProposal = findProposal(proposalToSelect, proposals);

		assertTrue(((ICompletionProposalExtension2) closeProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) closeProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals(postCompletion, fDocument.get());

		if (point != null)
		{
			Point p = viewer.getSelectedRange();
			assertEquals(point.x, p.x);
			assertEquals(point.y, p.y);
		}
	}
}
