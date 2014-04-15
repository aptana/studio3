/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Test;

import beaver.Parser.Exception;

import com.aptana.core.util.ArrayUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.tests.TextViewer;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class RangeTest extends JSEditorBasedTestCase
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
		this.setupTestContext(resource);

		ITextViewer viewer = new TextViewer(this.document);
		JSContentAssistProcessor processor = new JSContentAssistProcessor((AbstractThemeableEditor) this.editor);

		for (OffsetSelection selection : selections)
		{
			for (int offset = selection.startingOffset; offset <= selection.endingOffset; offset++)
			{
				ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, offset, '\0', false);

				if (!ArrayUtil.isEmpty(proposals))
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
	 * rangeTest
	 * 
	 * @param resource
	 * @param offset
	 * @param length
	 */
	protected void rangeTest(String resource, int offset, int length)
	{
		this.setupTestContext(resource);

		// discard type since we only care about the side-effect that sets the replace range
		JSContentAssistProcessor processor = new JSContentAssistProcessor((AbstractThemeableEditor) this.editor);
		processor.getLocationType(document, offset);

		IRange range = processor.getReplaceRange();
		assertNotNull(range);
		assertEquals(offset - length, range.getStartingOffset());
		assertEquals(length, range.getLength());
	}

	// @formatter:off

	/**
	 * testFunctionWithoutArgs
	 */
	@Test public void testFunctionWithoutArgs()
	{
		this.rangeTests(
			"ranges/functionWithoutArgs.js",
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 15, null),
			new OffsetSelection(16, 17, Range.EMPTY)
		);
	}
	
	/**
	 * testFunctionWithArgs
	 */
	@Test public void testFunctionWithArgs()
	{
		this.rangeTests(
			"ranges/functionWithArgs.js",
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 24, null),
			new OffsetSelection(25, 26, Range.EMPTY)
		);
	}
	
	/**
	 * testInvokeWithoutParams
	 */
	@Test public void testInvokeWithoutParams()
	{
		this.rangeTests(
			"ranges/invokeWithoutParams.js",
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, new Range(0, 0)),
			new OffsetSelection(2, new Range(0, 1)),
			new OffsetSelection(3, new Range(0, 2)),
			new OffsetSelection(4, 5, Range.EMPTY)
		);
	}
	
	/**
	 * testInvokeWithParams
	 */
	@Test public void testInvokeWithParams()
	{
		this.rangeTests(
			"ranges/invokeWithParams.js",
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, new Range(0, 0)),
			new OffsetSelection(2, new Range(0, 1)),
			new OffsetSelection(3, new Range(0, 2)),
			new OffsetSelection(4, Range.EMPTY),
			new OffsetSelection(5, new Range(4)),
			new OffsetSelection(6, Range.EMPTY),
			new OffsetSelection(7, new Range(6, 6)),
			new OffsetSelection(8, new Range(6, 7)),
			new OffsetSelection(9, 10, Range.EMPTY),
			new OffsetSelection(11, new Range(10, 10)),
			new OffsetSelection(12, new Range(10, 11)),
			new OffsetSelection(13, new Range(10, 12)),
			new OffsetSelection(14, Range.EMPTY)
		);
	}
	
	/**
	 * testIfStatement
	 */
	@Test public void testIfStatement()
	{
		this.rangeTests(
			"ranges/ifStatement.js",
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 3, null),
			new OffsetSelection(4, Range.EMPTY),
			new OffsetSelection(5, new Range(4)),
			new OffsetSelection(6, 7, null),
			new OffsetSelection(8, Range.EMPTY),
			new OffsetSelection(9, 15, null),
			new OffsetSelection(16, 17, Range.EMPTY)
		);
	}
	
	/**
	 * testWhileStatement
	 */
	@Test public void testWhileStatement()
	{
		this.rangeTests(
			"ranges/whileStatement.js",
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 6, null),
			new OffsetSelection(7, Range.EMPTY),
			new OffsetSelection(8, new Range(7)),
			new OffsetSelection(9, 10, null),
			new OffsetSelection(11, 12, Range.EMPTY)
		);
	}
	
	/**
	 * testForStatement
	 */
	@Test public void testForStatement()
	{
		this.rangeTests(
			"ranges/forStatement.js",
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 11, null),
			new OffsetSelection(12, 13, Range.EMPTY),
			new OffsetSelection(14, null),
			new OffsetSelection(15, 16, Range.EMPTY),
			new OffsetSelection(17, new Range(16, 16)),
			new OffsetSelection(18, 20, Range.EMPTY),
			new OffsetSelection(21, 22, null),
			new OffsetSelection(23, 24, Range.EMPTY),
			new OffsetSelection(25, new Range(24, 24)),
			new OffsetSelection(26, 29, null),
			new OffsetSelection(30, 31, Range.EMPTY)
		);
	}
	
	/**
	 * testForInStatement
	 */
	@Test public void testForInStatement()
	{
		this.rangeTests(
			"ranges/forInStatement.js",
			new OffsetSelection(0, Range.EMPTY),
			new OffsetSelection(1, 13, null),
			new OffsetSelection(14, Range.EMPTY),
			new OffsetSelection(15, new Range(14, 14)),
			new OffsetSelection(16, new Range(14, 15)),
			new OffsetSelection(17, new Range(14, 16)),
			new OffsetSelection(18, 19, null),
			new OffsetSelection(20, 21, Range.EMPTY)
		);
	}

	/**
	 * Test fix for APSTUD-3005
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test public void testApstud3005() throws IOException, Exception
	{
		rangeTest("ranges/apstud-3005.js", 14, 2);
	}

	/**
	 * Test fix for APSTUD-3017
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test public void testApstud3017() throws IOException, Exception
	{
		rangeTest("ranges/apstud-3017.js", 40, 1);
	}

	/**
	 * Test secondary fix for APSTUD-3017
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test public void testApstud3017_2() throws IOException, Exception
	{
		rangeTest("ranges/apstud-3017-2.js", 55, 1);
	}
}
