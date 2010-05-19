package com.aptana.editor.css.contentassist;

import com.aptana.editor.css.contentassist.CSSContentAssistProcessor.LocationType;

public class ContentAssistFineLocationTests extends LocationTests
{
	/**
	 * testEmptyBody
	 */
	public void testEmptyBody()
	{
		String source = "body {}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6)
		);
	}
	
	/**
	 * testEmptyBody
	 */
	public void testEmptyBody2()
	{
		String source = "body {\n  \n}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 6, 10)
		);
	}
	
	/**
	 * testEmptyBody2
	 */
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
	public void testPropertyNoValue2()
	{
		String source = "body{\n  background:}";
		
		this.fineLocationTests(
			source,
			new LocationTypeRange(LocationType.INSIDE_PROPERTY, 5, 18),
			new LocationTypeRange(LocationType.INSIDE_VALUE, 19)
		);
	}
}
