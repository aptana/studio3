/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.tests.TextViewer;
import com.aptana.parsing.lexer.Range;

public class RangeTests
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
		ITextViewer viewer = new TextViewer(document);
		CSSContentAssistProcessor processor = new CSSContentAssistProcessor(null);

		for (OffsetSelection selection : selections)
		{
			for (int offset = selection.startingOffset; offset <= selection.endingOffset; offset++)
			{
				ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, offset, '\0', false);
	
				if (proposals != null && proposals.length > 0)
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
					fail("No proposals at offset " + offset);
				}
			}
		}
	}

	/**
	 * testEmptyBody
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEmptyBody()
	{
		String source = "body {}";

		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, 7, Range.EMPTY)
		);
	}
	
	/**
	 * testEmptyBody2
	 */
	@Test
	public void testEmptyBody2()
	{
		String source = "body {\n  \n}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, 11, Range.EMPTY)
		);
	}
	
	/**
	 * testEmptyA - This came from a bug report
	 */
	@Test
	public void testEmptyA()
	{
		String source = "a{\n  \n}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, new Range(0, 0)),
			new OffsetSelection(2, 7, Range.EMPTY)
		);
	}
	
	/**
	 * testEmptyUnknownElement
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEmptyUnknownElement()
	{
		String source = "xxyyzz {}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 6, new Range(0, 5)),
			new OffsetSelection(7, 9, Range.EMPTY)
		);
	}
	
	/**
	 * testProperty
	 */
	@Test
	public void testProperty()
	{
		String source = "body{background}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, Range.EMPTY),
			new OffsetSelection(6, 15, new Range(5, 14)),
			new OffsetSelection(16, Range.EMPTY)
		);
	}
	
	/**
	 * testProperty2
	 */
	@Test
	public void testProperty2()
	{
		String source = "body{\n  background}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, 8, Range.EMPTY),
			new OffsetSelection(9, 18, new Range(8, 17)),
			new OffsetSelection(19, Range.EMPTY)
		);
	}
	
	/**
	 * testPropertyNoValue
	 */
	@Test
	public void testPropertyNoValue()
	{
		String source = "body{background:}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, Range.EMPTY),
			new OffsetSelection(6, 15, new Range(5, 14)),
			new OffsetSelection(16, 17, Range.EMPTY)
		);
	}
	
	/**
	 * testPropertyNoValue
	 */
	@Test
	public void testPropertyNoValue2()
	{
		String source = "body{\n  background:}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, 8, Range.EMPTY),
			new OffsetSelection(9, 18, new Range(8, 17)),
			new OffsetSelection(19, 20, Range.EMPTY)
		);
	}
	
	/**
	 * testPropertyAndValueNoSemi
	 */
	@Test
	public void testPropertyAndValueNoSemi()
	{
		String source = "body{background:red}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, Range.EMPTY),
			new OffsetSelection(6, 15, new Range(5, 14)),
			new OffsetSelection(16, Range.EMPTY),
			new OffsetSelection(17, 18, new Range(16, 18)),
			new OffsetSelection(20, Range.EMPTY)
		);
	}
	
	/**
	 * testPropertyAndValueNoSemi2
	 */
	@Test
	public void testPropertyAndValueNoSemi2()
	{
		String source = "body{\n  background:red\n}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, 8, Range.EMPTY),
			new OffsetSelection(9, 18, new Range(8, 17)),
			new OffsetSelection(19, Range.EMPTY),
			new OffsetSelection(20, 22, new Range(19, 21)),
			new OffsetSelection(23, 24, Range.EMPTY)
		);
	}
	
	/**
	 * testPropertyAndValue
	 */
	@Test
	public void testPropertyAndValue()
	{
		String source = "body{background:red;}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, Range.EMPTY),
			new OffsetSelection(6, 15, new Range(5, 14)),
			new OffsetSelection(16, Range.EMPTY),
			new OffsetSelection(17, 19, new Range(16, 18)),
			new OffsetSelection(20, 21, Range.EMPTY)
		);
	}
	
	/**
	 * testPropertyAndValue2
	 */
	@Test
	public void testPropertyAndValue2()
	{
		String source = "body{\n  background:red;\n}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, 8, Range.EMPTY),
			new OffsetSelection(9, 18, new Range(8, 17)),
			new OffsetSelection(19, Range.EMPTY),
			new OffsetSelection(20, 22, new Range(19, 21)),
			new OffsetSelection(23, 25, Range.EMPTY)
		);
	}
	
	/**
	 * testMultipleProperties
	 */
	@Test
	public void testMultipleProperties()
	{
		String source = "body{background:red;border: 1 solid black}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, Range.EMPTY),
			new OffsetSelection(6, 15, new Range(5, 14)),
			new OffsetSelection(16, Range.EMPTY),
			new OffsetSelection(17, 19, new Range(16, 18)),
			new OffsetSelection(20, Range.EMPTY),
			new OffsetSelection(21, 26, new Range(20, 25)),
			new OffsetSelection(27, 28, Range.EMPTY),
			new OffsetSelection(29, new Range(28, 28)),
			new OffsetSelection(30, Range.EMPTY),
			new OffsetSelection(31, 35, new Range(30, 34)),
			new OffsetSelection(36, Range.EMPTY),
			new OffsetSelection(37, 41, new Range(36, 40)),
			new OffsetSelection(42, Range.EMPTY)
		);
	}
	
	/**
	 * testMultipleProperties2
	 */
	@Test
	public void testMultipleProperties2()
	{
		String source = "body{\n  background: red;\n  border: 1 solid black\n}";
		
		this.rangeTests(
			source,
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 4, new Range(0, 3)),
			new OffsetSelection(5, 8, Range.EMPTY),
			new OffsetSelection(9, 18, new Range(8, 17)),
			new OffsetSelection(19, 20, Range.EMPTY),
			new OffsetSelection(21, 23, new Range(20, 22)),
			new OffsetSelection(24, 27, Range.EMPTY),
			new OffsetSelection(28, 33, new Range(27, 32)),
			new OffsetSelection(34, 35, Range.EMPTY),
			new OffsetSelection(36, new Range(35, 35)),
			new OffsetSelection(37, Range.EMPTY),
			new OffsetSelection(38, 42, new Range(37, 41)),
			new OffsetSelection(43, Range.EMPTY),
			new OffsetSelection(44, 48, new Range(43, 47)),
			new OffsetSelection(49, 50, Range.EMPTY)
		);
	}
}
