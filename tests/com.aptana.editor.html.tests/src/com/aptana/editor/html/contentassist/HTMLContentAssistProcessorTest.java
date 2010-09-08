package com.aptana.editor.html.contentassist;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.swt.SWT;

import com.aptana.editor.js.tests.TextViewer;

public class HTMLContentAssistProcessorTest extends TestCase
{

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
		IDocument fDocument = new Document("<");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(130, proposals.length);
		ICompletionProposal linkProposal = findProposal("a", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<a></a>", fDocument.get());
	}
	
	public void testABBRProposal()
	{
		int offset = 2;
		IDocument fDocument = new Document("<a>");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(130, proposals.length);
		ICompletionProposal linkProposal = findProposal("abbr", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<abbr></abbr>", fDocument.get());
	}
	
	public void testElementWhichIsClosedProposal()
	{
		int offset = 1;
		IDocument fDocument = new Document("<></a>");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(130, proposals.length);
		ICompletionProposal linkProposal = findProposal("a", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<a></a>", fDocument.get());
	}
	
	public void testElementWhichIsClosedProposal2()
	{
		int offset = 1;
		IDocument fDocument = new Document("<></a>");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(130, proposals.length);
		ICompletionProposal linkProposal = findProposal("abbr", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<abbr></abbr></a>", fDocument.get());
	}

	public void testElementWhichIsClosedProposal3()
	{
		int offset = 1;
		IDocument fDocument = new Document("<</a>");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(130, proposals.length);
		ICompletionProposal linkProposal = findProposal("abbr", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<abbr></abbr></a>", fDocument.get());
	}

	public void testIMGProposal()
	{
		int offset = 1;
		IDocument fDocument = new Document("<");
		char trigger = '\t';
		ITextViewer viewer = new TextViewer(fDocument);
		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(130, proposals.length);
		ICompletionProposal linkProposal = findProposal("img", proposals);

		((ICompletionProposalExtension2) linkProposal).apply(viewer, trigger, SWT.NONE, offset);
		assertEquals("<img/>", fDocument.get());
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
