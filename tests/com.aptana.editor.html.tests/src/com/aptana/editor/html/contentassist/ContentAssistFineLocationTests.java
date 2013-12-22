/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import org.junit.Test;

import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor.LocationType;

public class ContentAssistFineLocationTests extends LocationTestCase
{
	/**
	 * testOpenBracket
	 */
	@Test
	public void testOpenBracket()
	{
		String source = "<";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1));
	}

	/**
	 * testUnclosedOpenTag
	 */
	@Test
	public void testUnclosedOpenTag()
	{
		String source = "<body";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length()));
	}

	/**
	 * testUnclosedOpenTagWithIncompleteAttribute
	 */
	@Test
	public void testUnclosedOpenTagWithIncompleteAttribute()
	{
		String source = "<body onload";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
				new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, source.length()));
	}

	/**
	 * testUnclosedOpenTagWithIncompleteAttribute2
	 */
	@Test
	public void testUnclosedOpenTagWithIncompleteAttribute2()
	{
		String source = "<body onload=";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
				new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, source.length() - 1), new LocationTypeRange(
						LocationType.IN_ATTRIBUTE_VALUE, source.length()));
	}

	/**
	 * testUnclosedOpenTagWithIncompleteAttribute3
	 */
	@Test
	public void testUnclosedOpenTagWithIncompleteAttribute3()
	{
		String source = "<img src=\"img/";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 4),
				new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 5, 8), new LocationTypeRange(
						LocationType.IN_ATTRIBUTE_VALUE, 9, 13));
	}

	/**
	 * testUnclosedOpenTagWithAttribute
	 */
	@Test
	public void testUnclosedOpenTagWithAttribute()
	{
		String source = "<body reload=\"init()\"";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
				new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, 12), new LocationTypeRange(
						LocationType.IN_ATTRIBUTE_VALUE, 13, source.length() - 1), new LocationTypeRange(
						LocationType.IN_ATTRIBUTE_NAME, source.length()));
	}

	/**
	 * testOpenTagNoElementName
	 */
	@Test
	public void testOpenTagNoElementName()
	{
		String source = "<>";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1));
	}

	/**
	 * testOpenTagElementName
	 */
	@Test
	public void testOpenTagElementName()
	{
		String source = "<body>";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 1));
	}

	/**
	 * testOpenTagElementName2
	 */
	@Test
	public void testOpenTagElementName2()
	{
		String source = "<body >";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 2),
				new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, source.length() - 1));
	}

	/**
	 * testOpenScriptElement
	 */
	@Test
	public void testOpenScriptElement()
	{
		String source = "<script>";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 1));
	}

	/**
	 * testOpenStyleElement
	 */
	@Test
	public void testOpenStyleElement()
	{
		String source = "<style>";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 1));
	}

	/**
	 * testOpenTagWithClassAttribute
	 */
	@Test
	public void testOpenTagWithClassAttribute()
	{
		String source = "<body class=\"testing\">";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
				new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, 11), new LocationTypeRange(
						LocationType.IN_ATTRIBUTE_VALUE, 12, source.length() - 2), new LocationTypeRange(
						LocationType.IN_ATTRIBUTE_NAME, source.length() - 1));
	}

	/**
	 * testOpenTagWithIDAttribute
	 */
	@Test
	public void testOpenTagWithIDAttribute()
	{
		String source = "<body id=\"testing\">";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
				new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, 8), new LocationTypeRange(
						LocationType.IN_ATTRIBUTE_VALUE, 9, source.length() - 2), new LocationTypeRange(
						LocationType.IN_ATTRIBUTE_NAME, source.length() - 1));
	}

	/**
	 * testSelfClosingTag
	 */
	@Test
	public void testSelfClosingTag()
	{
		String source = "<body/>";

		this.fineLocationTests(source, new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5));
	}
}
