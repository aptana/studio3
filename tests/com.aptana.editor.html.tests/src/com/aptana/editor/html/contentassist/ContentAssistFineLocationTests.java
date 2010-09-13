package com.aptana.editor.html.contentassist;

import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor.LocationType;

public class ContentAssistFineLocationTests extends LocationTestCase
{
	/**
	 * testOpenBracket
	 */
	public void testOpenBracket()
	{
		String source = "<";

		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1)
		);
	}
	
	/**
	 * testUnclosedOpenTag
	 */
	public void testUnclosedOpenTag()
	{
		String source = "<body";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length())
		);
	}
	
	/**
	 * testUnclosedOpenTagWithIncompleteAttribute
	 */
	public void testUnclosedOpenTagWithIncompleteAttribute()
	{
		String source = "<body onload";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, source.length())
		);
	}
	
	/**
	 * testUnclosedOpenTagWithIncompleteAttribute2
	 */
	public void testUnclosedOpenTagWithIncompleteAttribute2()
	{
		String source = "<body onload=";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, source.length() - 1),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_VALUE, source.length())
		);
	}
	
	/**
	 * testUnclosedOpenTagWithAttribute
	 */
	public void testUnclosedOpenTagWithAttribute()
	{
		String source = "<body onload=\"init()\"";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, 12),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_VALUE, 13, source.length() - 1),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, source.length())
		);
	}
	
	/**
	 * testOpenTagNoElementName
	 */
	public void testOpenTagNoElementName()
	{
		String source = "<>";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1)
		);
	}
	
	/**
	 * testOpenTagElementName
	 */
	public void testOpenTagElementName()
	{
		String source = "<body>";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 1)
		);
	}
	
	/**
	 * testOpenTagElementName2
	 */
	public void testOpenTagElementName2()
	{
		String source = "<body >";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 2),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, source.length() - 1)
		);
	}
	
	/**
	 * testOpenScriptElement
	 */
	public void testOpenScriptElement()
	{
		String source = "<script>";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 1)
		);
	}
	
	/**
	 * testOpenStyleElement
	 */
	public void testOpenStyleElement()
	{
		String source = "<style>";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, source.length() - 1)
		);
	}
	
	/**
	 * testOpenTagWithClassAttribute
	 */
	public void testOpenTagWithClassAttribute()
	{
		String source = "<body class=\"testing\">";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, 11),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_VALUE, 12, source.length() - 2),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, source.length() - 1)
		);
	}
	
	/**
	 * testOpenTagWithIDAttribute
	 */
	public void testOpenTagWithIDAttribute()
	{
		String source = "<body id=\"testing\">";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, 6, 8),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_VALUE, 9, source.length() - 2),
			new LocationTypeRange(LocationType.IN_ATTRIBUTE_NAME, source.length() - 1)
		);
	}

	/**
	 * testSelfClosingTag
	 */
	public void testSelfClosingTag()
	{
		String source = "<body/>";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.IN_ELEMENT_NAME, 1, 5)
		);
	}
}
