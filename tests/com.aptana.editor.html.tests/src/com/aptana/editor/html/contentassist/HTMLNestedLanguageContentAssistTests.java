/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import org.junit.Test;

import com.aptana.editor.html.tests.HTMLEditorBasedTests;

/**
 * HTMLNestedLanguageContentAssistTests
 */
public class HTMLNestedLanguageContentAssistTests extends HTMLEditorBasedTests
{
	/**
	 * testJSEventAttribute
	 */
	@Test
	public void testJSEventAttribute()
	{
		this.checkProposals("contentAssist/js-event-attribute.html", false, false, "alert");
	}

	/**
	 * testJSMathInAttribute
	 */
	@Test
	public void testJSMathInAttribute()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/js-math-in-attribute.html",
			"E",
			"LN10",
			"LN2",
			"LOG10E",
			"LOG2E",
			"PI",
			"SQRT1_2",
			"SQRT2",
			"abs",
			"acos",
			"asin",
			"atan",
			"atan2",
			"ceil",
			"cos",
			"exp",
			"floor",
			"log",
			"max",
			"min",
			"pow",
			"random",
			"round",
			"sin",
			"sqrt",
			"tan"
		);
		// @formatter:on
	}

	/**
	 * testCSSStyleAttribute
	 */
	@Test
	public void testCSSStyleAttribute()
	{
		// we check the style attribute twice to confirm that calling the same processor multiple times is not an issue.
		this.checkProposals("contentAssist/css-style-attribute.html", "font");
	}

	/**
	 * testCSSIDAttribute
	 */
	@Test
	public void testCSSIDAttribute()
	{
		this.checkProposals("contentAssist/css-id-attribute.html", "testid");
	}

	/**
	 * testCSSIDAttribute
	 */
	@Test
	public void testCSSClassAttribute()
	{
		this.checkProposals("contentAssist/css-class-attribute.html", "testclass");
	}

	/**
	 * testCSSClassAttributeDuplicated
	 */
	@Test
	public void testCSSClassAttributeDuplicated()
	{
		this.checkProposals("contentAssist/css-class-attribute-duplicated.html", "testclass2");
	}

	/**
	 * testCSSIDAttributeDuplicated
	 */
	@Test
	public void testCSSIDAttributeDuplicated()
	{
		this.checkProposals("contentAssist/css-id-attribute-duplicated.html", "testid2");
	}

}
