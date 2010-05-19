package com.aptana.editor.css.contentassist;

import com.aptana.editor.css.contentassist.CSSContentAssistProcessor.LocationType;

public class ContentAssistCoarseLocationTests extends LocationTests
{
	/**
	 * testNoSource
	 */
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
}
