package com.aptana.editor.js.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.parsing.lexer.Range;

public class RangeTests extends EditorBasedTests
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
	 * @param resource
	 * @param selections
	 */
	protected void rangeTests(String resource, OffsetSelection... selections)
	{
		TestContext context = this.getTestContext("locations/global_in_arg.js");
		ITextViewer viewer = new TestTextViewer(context.document);
		JSContentAssistProcessor processor = new JSContentAssistProcessor((AbstractThemeableEditor) context.editor);

		for (OffsetSelection selection : selections)
		{
			for (int offset = selection.startingOffset; offset <= selection.endingOffset; offset++)
			{
				ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, offset, '\0', false);
	
				if (proposals != null && proposals.length > 0)
				{
					if (selection.range != null)
					{
						CommonCompletionProposal proposal = (CommonCompletionProposal) proposals[0];
						
						if (selection.range.isEmpty())
						{
							assertEquals(offset, proposal.getReplaceRange().getStartingOffset());
						}
						else
						{
							assertEquals("offset " + offset, selection.range, proposal.getReplaceRange());
						}
					}
					else
					{
						fail("Unexpected proposals at offset " + offset);
					}
				}
				else
				{
					if (selection.range != null)
					{
						fail("No proposals at offset " + offset);
					}
				}
			}
		}
	}
	
	/**
	 * testNoArgs
	 */
	public void testNoArgs()
	{
		this.rangeTests(
			"ranges/global_in_arg.js",
			new OffsetSelection(0, 8, null)
		);
	}
}
