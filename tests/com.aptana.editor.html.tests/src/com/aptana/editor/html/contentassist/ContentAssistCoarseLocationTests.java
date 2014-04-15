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

public class ContentAssistCoarseLocationTests extends LocationTestCase
{
	/**
	 * openTagTests
	 * 
	 * @param source
	 */
	protected void tagTests(String source, LocationType mainLocation)
	{
		// this ends with Eclipse's default partition
		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				mainLocation, 1, source.length() - 1), new LocationTypeRange(LocationType.IN_TEXT, source.length()));

		// this ends with one of our language's default partitions
		source += "\n";

		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				mainLocation, 1, source.length() - 2), new LocationTypeRange(LocationType.IN_TEXT, source.length() - 1));
	}

	/**
	 * testOpenBracket
	 */
	@Test
	public void testOpenBracket()
	{
		String source = "<";

		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				LocationType.IN_OPEN_TAG, 1));
	}

	/**
	 * testUnclosedOpenTag
	 */
	@Test
	public void testUnclosedOpenTag()
	{
		String source = "<body";

		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				LocationType.IN_OPEN_TAG, 1, source.length()));
	}

	/**
	 * testUnclosedOpenTag2
	 */
	@Test
	public void testUnclosedOpenTag2()
	{
		String source = "<body ";

		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				LocationType.IN_OPEN_TAG, 1, source.length()));
	}

	/**
	 * testUnclosedOpenTagWithIncompleteAttribute
	 */
	@Test
	public void testUnclosedOpenTagWithIncompleteAttribute()
	{
		String source = "<body onload";

		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				LocationType.IN_OPEN_TAG, 1, source.length()));
	}

	/**
	 * testUnclosedOpenTagWithIncompleteAttribute2
	 */
	@Test
	public void testUnclosedOpenTagWithIncompleteAttribute2()
	{
		String source = "<body onload=";

		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				LocationType.IN_OPEN_TAG, 1, source.length()));
	}

	/**
	 * testUnclosedOpenTagWithAttribute
	 */
	@Test
	public void testUnclosedOpenTagWithAttribute()
	{
		String source = "<body onload=\"init()\"";

		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				LocationType.IN_OPEN_TAG, 1, source.length()));
	}

	/**
	 * testOpenTagNoElementName
	 */
	@Test
	public void testOpenTagNoElementName()
	{
		String source = "<>";

		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				LocationType.IN_OPEN_TAG, 1), new LocationTypeRange(LocationType.IN_TEXT, source.length()));
	}

	/**
	 * testOpenTagElementName
	 */
	@Test
	public void testOpenTagElementName()
	{
		this.tagTests("<body>", LocationType.IN_OPEN_TAG);
	}

	/**
	 * testOpenScriptElement
	 */
	@Test
	public void testOpenScriptElement()
	{
		this.tagTests("<script>", LocationType.IN_OPEN_TAG);
	}

	/**
	 * testOpenStyleElement
	 */
	@Test
	public void testOpenStyleElement()
	{
		this.tagTests("<style>", LocationType.IN_OPEN_TAG);
	}

	/**
	 * testOpenTagWithClassAttribute
	 */
	@Test
	public void testOpenTagWithClassAttribute()
	{
		this.tagTests("<body class=\"testing\">", LocationType.IN_OPEN_TAG);
	}

	/**
	 * testOpenTagWithIDAttribute
	 */
	@Test
	public void testOpenTagWithIDAttribute()
	{
		this.tagTests("<body id=\"testing\">", LocationType.IN_OPEN_TAG);
	}

	/**
	 * testSelfClosingTag
	 */
	@Test
	public void testSelfClosingTag()
	{
		this.tagTests("<body/>", LocationType.IN_OPEN_TAG);
	}

	/**
	 * testOpenAndCloseTag
	 */
	@Test
	public void testCloseTag()
	{
		this.tagTests("</body>", LocationType.IN_CLOSE_TAG);
	}

	/**
	 * testEmptyText
	 */
	@Test
	public void testEmptyText()
	{
		String source = "<div>   </div>";

		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				LocationType.IN_OPEN_TAG, 1, 4), new LocationTypeRange(LocationType.IN_TEXT, 5, 8),
				new LocationTypeRange(LocationType.IN_CLOSE_TAG, 9, 13),
				new LocationTypeRange(LocationType.IN_TEXT, 14));
	}

	/**
	 * testText
	 */
	@Test
	public void testText()
	{
		String source = "<div> a </div>";

		this.coarseLocationTests(source, new LocationTypeRange(LocationType.IN_TEXT, 0), new LocationTypeRange(
				LocationType.IN_OPEN_TAG, 1, 4), new LocationTypeRange(LocationType.IN_TEXT, 5, 8),
				new LocationTypeRange(LocationType.IN_CLOSE_TAG, 9, 13),
				new LocationTypeRange(LocationType.IN_TEXT, 14));
	}

	@Test
	public void testUnclosedCloseTagWithNoPrecedingText()
	{
		String source = "<div></";

		// @formatter:off
        this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_TEXT, 0),
			new LocationTypeRange(LocationType.IN_OPEN_TAG, 1, 4),
			new LocationTypeRange(LocationType.IN_TEXT, 5, 5),
			new LocationTypeRange(LocationType.IN_CLOSE_TAG, 6, 7)
        );
        // @formatter:off
    }

    /**
     * Bug 2264
     */
	@Test
    public void testUnclosedCloseTagWithPrecedingText()
    {
        String source = "<div>Test</";

        // @formatter:off
        this.coarseLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_TEXT, 0),
			new LocationTypeRange(LocationType.IN_OPEN_TAG, 1, 4),
			new LocationTypeRange(LocationType.IN_TEXT, 5, 9),
			new LocationTypeRange(LocationType.IN_CLOSE_TAG, 10, 11)
        );
        // @formatter:on
	}
}
