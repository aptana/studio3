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
	
	/**
	 * testPropertyAndValueNoSemi
	 */
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
}
