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
		int offset = 1;
		IDocument fDocument = createDocument("<");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("a", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<a></a>", fDocument.get());
	}

	public void testDOCTYPEProposal()
	{
		int offset = 2;
		IDocument fDocument = createDocument("<!");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("!DOCTYPE", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE >", fDocument.get());
		Point p = viewer.getSelectedRange();
		assertEquals(10, p.x);
		assertEquals(0, p.y);
	}
	
	public void testDOCTYPEProposal2()
	{
		int offset = 3;
		IDocument fDocument = createDocument("<!D");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("!DOCTYPE", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE >", fDocument.get());
		Point p = viewer.getSelectedRange();
		assertEquals(10, p.x);
		assertEquals(0, p.y);
	}
	
	public void testDOCTYPEProposal3()
	{
		int offset = 3;
		IDocument fDocument = createDocument("<!D html>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("!DOCTYPE", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE html>", fDocument.get());
		Point p = viewer.getSelectedRange();
		assertEquals(10, p.x);
		assertEquals(0, p.y);
	}
	
	public void testDOCTYPEProposal4()
	{
		int offset = 1;
		IDocument fDocument = createDocument("<>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("!DOCTYPE", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE >", fDocument.get());
		Point p = viewer.getSelectedRange();
		assertEquals(10, p.x);
		assertEquals(0, p.y);
	}
	
	public void testDOCTYPEProposal5()
	{
		int offset = 2;
		IDocument fDocument = createDocument("<!>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("!DOCTYPE", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE >", fDocument.get());
		Point p = viewer.getSelectedRange();
		assertEquals(10, p.x);
		assertEquals(0, p.y);
	}
	
	public void testDOCTYPEProposal6()
	{
		int offset = 3;
		IDocument fDocument = createDocument("<!DOCTYP >");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("!DOCTYPE", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE >", fDocument.get());
		Point p = viewer.getSelectedRange();
		assertEquals(10, p.x);
		assertEquals(0, p.y);
	}
	
	public void testDOCTYPEProposal7()
	{
		int offset = 3;
		IDocument fDocument = createDocument("<!DOCTYPE >");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("!DOCTYPE", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE >", fDocument.get());
		Point p = viewer.getSelectedRange();
		assertEquals(10, p.x);
		assertEquals(0, p.y);
	}
	
	public void testDOCTYPEValueReplacement()
	{
		int offset = 10;
		IDocument fDocument = createDocument("<!DOCTYPE html>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(DOCTYPE_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("HTML 5", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE HTML>", fDocument.get());
	}
	
	public void testDOCTYPEValueReplacement2()
	{
		int offset = 10;
		IDocument fDocument = createDocument("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n	\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(DOCTYPE_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("HTML 5", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE HTML>", fDocument.get());
	}

	public void testABBRProposal()
	{
		int offset = 2;
		IDocument fDocument = createDocument("<a>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("abbr", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<abbr></abbr>", fDocument.get());
	}

	public void testABBRCaseSensitiveProposal()
	{
		int offset = 2;
		IDocument fDocument = createDocument("<A>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("abbr", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<abbr></abbr>", fDocument.get());
	}
	
	public void testElementWhichIsClosedProposal()
	{
		int offset = 1;
		IDocument fDocument = createDocument("<></a>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("a", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<a></a>", fDocument.get());
	}

	public void testElementWhichIsClosedProposal2()
	{
		int offset = 1;
		IDocument fDocument = createDocument("<></a>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("abbr", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<abbr></abbr></a>", fDocument.get());
	}

	public void testElementWhichIsClosedProposal3()
	{
		int offset = 1;
		IDocument fDocument = createDocument("<</a>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("abbr", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<abbr></abbr></a>", fDocument.get());
	}

	public void testIMGProposal()
	{
		int offset = 1;
		IDocument fDocument = createDocument("<");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("img", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<img />", fDocument.get());
	}

	public void testHTML5DoctypeProposal()
	{
		int offset = 10;
		IDocument fDocument = createDocument("<!doctype >");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(DOCTYPE_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("HTML 5", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!doctype HTML>", fDocument.get());
	}

	public void testHTML401StrictDoctypeProposal()
	{
		int offset = 10;
		IDocument fDocument = createDocument("<!DOCTYPE ");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(DOCTYPE_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("HTML 4.01 Strict", proposals);

		assertTrue(((ICompletionProposalExtension2) linkProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n\"http://www.w3.org/TR/html4/strict.dtd\"",
				fDocument.get());
	}

	public void testCloseTagProposal()
	{
		int offset = 7;
		IDocument fDocument = createDocument("<ul>\n</>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(1, proposals.length);
		ICompletionProposal closeProposal = findProposal("ul", proposals);

		assertTrue(((ICompletionProposalExtension2) closeProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) closeProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<ul>\n</ul>", fDocument.get());
	}

	public void testCloseTagWithNoUnclosedTagsProposal()
	{
		int offset = 2;
		IDocument fDocument = createDocument("</>");
		char trigger = '\t';
		ITextViewer viewer = createTextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(CLOSE_TAG_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal closeProposal = findProposal("ul", proposals);

		assertTrue(((ICompletionProposalExtension2) closeProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) closeProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("</ul>", fDocument.get());
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

}
