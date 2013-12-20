/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import org.junit.Test;
import static org.junit.Assert.*;
import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.jface.text.IDocument;

import com.aptana.css.core.parsing.CSSTokenType;
import com.aptana.editor.common.contentassist.ILexemeProvider;

public class FineLocationTests
{
	/**
	 * fineLocationTests
	 * 
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 * @param expectedLocation
	 */
	protected void fineLocationTests(String source, LocationTypeRange ... ranges)
	{
		IDocument document = TestUtil.createDocument(source);
		CSSContentAssistProcessor processor = new CSSContentAssistProcessor(null);
		
		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				ILexemeProvider<CSSTokenType> lexemeProvider = processor.createLexemeProvider(document, offset);
				LocationType location = processor.getInsideLocationType(lexemeProvider, offset);
				String message = MessageFormat.format(
					"Expected {0} at location {1} of ''{2}''",
					range.location.toString(),
					Integer.toString(offset),
					source
				);
				assertEquals(message, range.location, location);
			}
		}
	}
	
	/**
	 * testEmptyBody
	 */
	@Test
	public void testEmptyBody()
	{
		String source = "body {}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6)
		);
	}
	
	/**
	 * testEmptyBody2
	 */
	@Test
	public void testEmptyBody2()
	{
		String source = "body {\n  \n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6, 10)
		);
	}
	
	/**
	 * testTwoRules
	 */
	@Test
	public void testTwoRules()
	{
		String source = "body {\n  \n}\n\ntable {\n  \n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6, 10),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 20, 24)
		);
	}
	
	/**
	 * testProperty
	 */
	@Test
	public void testProperty()
	{
		String source = "body{background}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 15)
		);
	}
	
	/**
	 * testProperty2
	 */
	@Test
	public void testProperty2()
	{
		String source = "body{\n  background}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18)
		);
	}
	
	/**
	 * testPropertyNoValue
	 */
	@Test
	public void testPropertyNoValue()
	{
		String source = "body{background:}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 15),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 16)
		);
	}
	
	/**
	 * testPropertyNoValue
	 */
	@Test
	public void testPropertyNoValue2()
	{
		String source = "body{\n  background:}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 19)
		);
	}
	
	/**
	 * testPropertyAndValueNoSemi
	 */
	@Test
	public void testPropertyAndValueNoSemi()
	{
		String source = "body{background:red}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 15),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 16, 19)
		);
	}
	
	/**
	 * testPropertyAndValueNoSemi2
	 */
	@Test
	public void testPropertyAndValueNoSemi2()
	{
		String source = "body{\n  background:red\n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 19, 22),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 23)
		);
	}
	
	/**
	 * testPropertyAndValue
	 */
	@Test
	public void testPropertyAndValue()
	{
		String source = "body{background:red;}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 15),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 16, 19),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 20)
		);
	}
	
	/**
	 * testPropertyAndValue2
	 */
	@Test
	public void testPropertyAndValue2()
	{
		String source = "body{\n  background:red;\n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 19, 22),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 23, 24)
		);
	}
	
	/**
	 * testMultipleProperties
	 */
	@Test
	public void testMultipleProperties()
	{
		String source = "body{background:red;border: 1 solid black}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 15),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 16, 19),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 20, 26),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 27, 41)
		);
	}
	
	/**
	 * testMultipleProperties2
	 */
	@Test
	public void testMultipleProperties2()
	{
		String source = "body{\n  background: red;\n  border: 1 solid black\n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 19, 23),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 24, 33),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 34, 48),
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 49)
		);
	}
	
	/**
	 * testSpaceAfterColon
	 */
	@Test
	public void testSpaceAfterColon()
	{
		String source = ".hello {\n  background: \n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 8, 21),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 22, 24)
		);
	}
	
	/**
	 * testDotInProperty
	 */
	@Test
	public void testDotInProperty()
	{
		String source = "body { background-color.}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6, 23),
			new LocationTypeRange(LocationType.ERROR, 24)
		);
	}
	
	/**
	 * testHashInProperty
	 */
	@Test
	public void testHashInProperty()
	{
		String source = "body { background-color#}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6, 23),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 24)
		);
	}
}
