package com.aptana.editor.html.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.swt.SWT;

import com.aptana.editor.js.tests.TextViewer;

public class HTMLContentAssistProcessorTest extends LocationTestCase
{

	private static final int ELEMENT_PROPOSALS_COUNT = 132;
	private static final int DOCTYPE_PROPOSALS_COUNT = 11;
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
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("a", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<a></a>", fDocument.get());
	}

	public void testABBRProposal()
	{
		int offset = 2;
		IDocument fDocument = createDocument("<a>");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("abbr", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<abbr></abbr>", fDocument.get());
	}

	public void testElementWhichIsClosedProposal()
	{
		int offset = 1;
		IDocument fDocument = createDocument("<></a>");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("a", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<a></a>", fDocument.get());
	}

	public void testElementWhichIsClosedProposal2()
	{
		int offset = 1;
		IDocument fDocument = createDocument("<></a>");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("abbr", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<abbr></abbr></a>", fDocument.get());
	}

	public void testElementWhichIsClosedProposal3()
	{
		int offset = 1;
		IDocument fDocument = createDocument("<</a>");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("abbr", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<abbr></abbr></a>", fDocument.get());
	}

	public void testIMGProposal()
	{
		int offset = 1;
		IDocument fDocument = createDocument("<");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(ELEMENT_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("img", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<img />", fDocument.get());
	}

	public void testHTML5DoctypeProposal()
	{
		int offset = 10;
		IDocument fDocument = createDocument("<!doctype >");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(DOCTYPE_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("HTML 5", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!doctype HTML>", fDocument.get());
	}

	public void testHTML401StrictDoctypeProposal()
	{
		int offset = 10;
		IDocument fDocument = createDocument("<!DOCTYPE ");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(DOCTYPE_PROPOSALS_COUNT, proposals.length);
		ICompletionProposal linkProposal = findProposal("HTML 4.01 Strict", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n\"http://www.w3.org/TR/html4/strict.dtd\"",
				fDocument.get());
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

}
