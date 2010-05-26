package com.aptana.editor.css.contentassist;

import junit.framework.TestCase;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.parsing.lexer.Range;

public class RangeTests extends TestCase
{
	static class OffsetSelection
	{
		public final int startingOffset;
		public final int endingOffset;
		public final Range range;

		public OffsetSelection(int offset, Range range)
		{
			this.startingOffset = offset;
			this.endingOffset = offset;
			this.range = range;
		}
		
		public OffsetSelection(int startingOffset, int endingOffset, Range range)
		{
			this.startingOffset = startingOffset;
			this.endingOffset = endingOffset;
			this.range = range;
		}
	}

	/**
	 * rangeTests
	 * 
	 * @param source
	 * @param selections
	 */
	protected void rangeTests(String source, OffsetSelection... selections)
	{
		IDocument document = TestUtil.createDocument(source);
		ITextViewer viewer = new TestTextViewer(document);
		CSSContentAssistProcessor processor = new CSSContentAssistProcessor(null);

		for (OffsetSelection selection : selections)
		{
			for (int offset = selection.startingOffset; offset <= selection.endingOffset; offset++)
			{
				ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, offset, '\0', false);
	
				if (proposals != null && proposals.length > 0)
				{
					CommonCompletionProposal proposal = (CommonCompletionProposal) proposals[0];
					
					if (selection.range.getLength() <= 0)
					{
						assertTrue(proposal.getReplaceRange().getLength() <= 0);
					}
					else
					{
						assertTrue(selection.range.equals(proposal.getReplaceRange()));
					}
				}
				else
				{
					fail("No proposals");
				}
			}
		}
	}

	/**
	 * testEmptyBody
	 * 
	 * @throws Exception
	 */
	public void testEmptyBody()
	{
		String source = "body {}";

		this.rangeTests(
			source,
			new OffsetSelection(0, new Range(0, 3))
		);
	}
}
