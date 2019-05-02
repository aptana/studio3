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

import com.aptana.core.IMap;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;

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
	 * @deprecated Use {@link #assertProposalApplies(String, IDocument, String, ICompletionProposal[], int, Point)}
	 * @param document
	 * @param proposal
	 * @param proposals
	 * @param offset
	 */
	public static void assertProposalApplies(IDocument document, String proposal, ICompletionProposal[] proposals,
			int offset)
	{
		assertProposalApplies(null, document, proposal, proposals, offset, null);
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
		if (proposal != null)
		{
			ICompletionProposal p = findProposal(proposal, proposals);
			if (p == null)
			{
				String proposalsAsString = "(empty list)";
				if (!ArrayUtil.isEmpty(proposals))
				{
					proposalsAsString = StringUtil.join(",", CollectionsUtil.map(CollectionsUtil.newList(proposals),
							new IMap<ICompletionProposal, String>()
							{
								public String map(ICompletionProposal item)
								{
									return item.getDisplayString();
								}
							}));
				}
				TestCase.fail(MessageFormat.format("Unable to find expected proposal {0} in proposals {1}", proposal,
						proposalsAsString));
			}
			ITextViewer viewer = createTextViewer(document);
			TestCase.assertTrue("Selected proposal doesn't validate against document",
					((ICompletionProposalExtension2) p).validate(document, offset, null));
			((ICompletionProposalExtension2) p).apply(viewer, '\t', SWT.NONE, offset);

			if (expected != null)
			{
				TestCase.assertEquals("Document contents after proposal don't match expectations", expected,
						document.get());
			}

			if (point != null)
			{
				Point pt = p.getSelection(document);
				TestCase.assertEquals("Start of post-proposal selection/cursor position doesn't match", point.x, pt.x);
				TestCase.assertEquals("Length of post-proposal selection/cursor position doesn't match", point.y, pt.y);
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
		TestCase.assertNotNull(
				MessageFormat.format("Proposal {0} not found in list: {1}", proposal, StringUtil.join(", ", proposals)),
				p);
	}

	/**
	 * Assert that the proposal doesn't exist
	 * 
	 * @param proposal
	 * @param proposals
	 */
	public static void assertProposalNotFound(String proposal, ICompletionProposal[] proposals)
	{
		ICompletionProposal p = findProposal(proposal, proposals);
		TestCase.assertNull(MessageFormat.format("Proposal {0} found in list, when it shouldn't have been: {1}",
				proposal, StringUtil.join(", ", proposals)), p);
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
