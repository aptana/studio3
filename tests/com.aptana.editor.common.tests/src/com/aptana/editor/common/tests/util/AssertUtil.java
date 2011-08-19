package com.aptana.editor.common.tests.util;

import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class AssertUtil
{
	/**
	 * Create a TextViewer around a document
	 * 
	 * @param document
	 * @return
	 */
	public static ITextViewer createTextViewer(IDocument document)
	{
		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
		viewer.setDocument(document);
		return viewer;
	}

	/**
	 * Assert that the proposal correctly inserts into the document
	 * 
	 * @param expected
	 * @param document
	 * @param proposal
	 * @param proposals
	 * @param offset
	 */
	public static void assertProposalApplies(String expected, IDocument document, String proposal,
			ICompletionProposal[] proposals, int offset, Point point)
	{
		assertProposalApplies(document, proposal, proposals, offset, point);
		TestCase.assertEquals(expected, document.get());
	}

	/**
	 * Assert that the proposal correctly inserts into the document
	 * 
	 * @param expected
	 * @param document
	 * @param proposal
	 * @param proposals
	 * @param offset
	 */
	public static void assertProposalApplies(IDocument document, String proposal, ICompletionProposal[] proposals,
			int offset, Point point)
	{
		if (proposal != null)
		{
			ICompletionProposal p = findProposal(proposal, proposals);
			ITextViewer viewer = createTextViewer(document);
			TestCase.assertTrue("Selected proposal doesn't validate against document",
					((ICompletionProposalExtension2) p).validate(document, offset, null));
			((ICompletionProposalExtension2) p).apply(viewer, '\t', SWT.NONE, offset);

			if (point != null)
			{
				Point pt = viewer.getSelectedRange();
				TestCase.assertEquals(point.x, pt.x);
				TestCase.assertEquals(point.y, pt.y);
			}

		}
	}

	/**
	 * Assert that the proposal exists
	 * 
	 * @param proposal
	 * @param proposals
	 */
	public static void assertProposalFound(String proposal, ICompletionProposal[] proposals)
	{
		ICompletionProposal p = findProposal(proposal, proposals);
		TestCase.assertNotNull(MessageFormat.format("Proposal {0} not found in list", proposal), p);
	}

	/**
	 * Have we found the proposal in the list of proposals
	 * 
	 * @param string
	 * @param proposals
	 * @return
	 */
	public static ICompletionProposal findProposal(String string, ICompletionProposal[] proposals)
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
