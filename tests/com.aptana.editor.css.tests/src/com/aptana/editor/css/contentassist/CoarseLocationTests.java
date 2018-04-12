/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;

import org.eclipse.jface.text.IDocument;
import org.junit.Test;

import com.aptana.css.core.parsing.CSSTokenType;
import com.aptana.editor.common.contentassist.ILexemeProvider;

public class CoarseLocationTests
{
	/**
	 * coarseLocationTests
	 * 
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 * @param expectedLocation
	 */
	protected void coarseLocationTests(String source, LocationTypeRange... ranges)
	{
		IDocument document = TestUtil.createDocument(source);
		CSSContentAssistProcessor processor = new CSSContentAssistProcessor(null);

		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				ILexemeProvider<CSSTokenType> lexemeProvider = processor.createLexemeProvider(document, offset);
				LocationType location = processor.getCoarseLocationType(lexemeProvider, offset);
				String message = MessageFormat.format("Expected {0} at location {1} of ''{2}''",
						range.location.toString(), Integer.toString(offset), source);
				assertEquals(message, range.location, location);
			}
		}
	}

	// @formatter:off

	/**
	 * testNoSource
	 */
	@Test
	public void testNoSource()
	{
		String source = "";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, source.length())
		);
	}
	
	/**
	 * testElementOnly
	 */
	@Test
	public void testElementOnly()
	{
		String source = "body";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, source.length())
		);
	}
	
	/**
	 * testElementAndClass
	 */
	@Test
	public void testElementAndClass()
	{
		String source = "body.myClass";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, source.length())
		);
	}
	
	/**
	 * testElementAndID
	 */
	@Test
	public void testElementAndID()
	{
		String source = "body#myID";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, source.length())
		);
	}
	
	/**
	 * testMultipleElements
	 */
	@Test
	public void testMultipleElements()
	{
		String source = "a, b, body";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, source.length())
		);
	}
	
	/**
	 * testEmptyBody
	 */
	@Test
	public void testEmptyBody()
	{
		String source = "body {}";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, 5),
			new LocationTypeRange(LocationType.INSIDE_RULE, 6),
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 7, source.length())
		);
	}
	
	/**
	 * testEmptyBody2
	 */
	@Test
	public void testEmptyBody2()
	{
		String source = "body {\n  \n}";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, 5),
			new LocationTypeRange(LocationType.INSIDE_RULE, 6, 10),
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 11, source.length())
		);
	}
	
	/**
	 * testEmptyBody2
	 */
	@Test
	public void testTwoRules()
	{
		String source = "body {\n  \n}\n\ntable {\n  \n}";
		
		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, 5),
			new LocationTypeRange(LocationType.INSIDE_RULE, 6, 10),
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 11, 19),
			new LocationTypeRange(LocationType.INSIDE_RULE, 20, 24),
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 25, source.length())
		);
	}

	@Test
	public void testPseudoClassRegression()
	{
		String source = "a: {}";

		this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.OUTSIDE_RULE, 0, 3)
		);
	}
}
