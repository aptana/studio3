package com.aptana.editor.html.contentassist;

import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor.Location;

public class ContentAssistCoarseLocationTests extends LocationTests
{
	/**
	 * openTagTests
	 * 
	 * @param source
	 */
	protected void tagTests(String source, Location mainLocation)
	{
		// this ends with Eclipse's default partition
		this.coarseLocationTests(
			source,
			new LocationRange(Location.IN_TEXT, 0),
			new LocationRange(mainLocation, 1, source.length() - 1),
			new LocationRange(Location.IN_TEXT, source.length())
		);
		
		// this ends with one of our language's default partitions
		source += "\n";
		
		this.coarseLocationTests(
			source,
			new LocationRange(Location.IN_TEXT, 0),
			new LocationRange(mainLocation, 1, source.length() - 2),
			new LocationRange(Location.IN_TEXT, source.length() - 1)
		);
	}
	
	/**
	 * testOpenBracket
	 */
	public void testOpenBracket()
	{
		String source = "<";
		
		this.coarseLocationTests(
			source,
			new LocationRange(Location.IN_TEXT, 0),
			new LocationRange(Location.IN_OPEN_TAG, 1)
		);
	}
	
	/**
	 * testUnclosedOpenTag
	 */
	public void testUnclosedOpenTag()
	{
		String source = "<body";
		
		this.coarseLocationTests(
			source,
			new LocationRange(Location.IN_TEXT, 0),
			new LocationRange(Location.IN_OPEN_TAG, 1, source.length())
		);
	}
	
	/**
	 * testUnclosedOpenTag2
	 */
	public void testUnclosedOpenTag2()
	{
		String source = "<body ";
		
		this.coarseLocationTests(
			source,
			new LocationRange(Location.IN_TEXT, 0),
			new LocationRange(Location.IN_OPEN_TAG, 1, source.length())
		);
	}
	
	/**
	 * testUnclosedOpenTagWithIncompleteAttribute
	 */
	public void testUnclosedOpenTagWithIncompleteAttribute()
	{
		String source = "<body onload";
		
		this.coarseLocationTests(
			source,
			new LocationRange(Location.IN_TEXT, 0),
			new LocationRange(Location.IN_OPEN_TAG, 1, source.length())
		);
	}
	
	/**
	 * testUnclosedOpenTagWithIncompleteAttribute2
	 */
	public void testUnclosedOpenTagWithIncompleteAttribute2()
	{
		String source = "<body onload=";
		
		this.coarseLocationTests(
			source,
			new LocationRange(Location.IN_TEXT, 0),
			new LocationRange(Location.IN_OPEN_TAG, 1, source.length())
		);
	}
	
	/**
	 * testUnclosedOpenTagWithAttribute
	 */
	public void testUnclosedOpenTagWithAttribute()
	{
		String source = "<body onload=\"init()\"";
		
		this.coarseLocationTests(
			source,
			new LocationRange(Location.IN_TEXT, 0),
			new LocationRange(Location.IN_OPEN_TAG, 1, source.length())
		);
	}
	
	/**
	 * testOpenTagNoElementName
	 */
	public void testOpenTagNoElementName()
	{
		String source = "<>";
		
		this.coarseLocationTests(
			source,
			new LocationRange(Location.IN_TEXT, 0),
			new LocationRange(Location.IN_OPEN_TAG, 1),
			new LocationRange(Location.IN_TEXT, source.length())
		);
	}
	
	/**
	 * testOpenTagElementName
	 */
	public void testOpenTagElementName()
	{
		this.tagTests("<body>", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenScriptElement
	 */
	public void testOpenScriptElement()
	{
		this.tagTests("<script>", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenStyleElement
	 */
	public void testOpenStyleElement()
	{
		this.tagTests("<style>", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenTagWithClassAttribute
	 */
	public void testOpenTagWithClassAttribute()
	{
		this.tagTests("<body class=\"testing\">", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenTagWithIDAttribute
	 */
	public void testOpenTagWithIDAttribute()
	{
		this.tagTests("<body id=\"testing\">", Location.IN_OPEN_TAG);
	}

	/**
	 * testSelfClosingTag
	 */
	public void testSelfClosingTag()
	{
		this.tagTests("<body/>", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenAndCloseTag
	 */
	public void testCloseTag()
	{
		this.tagTests("</body>", Location.IN_CLOSE_TAG);
	}
}
