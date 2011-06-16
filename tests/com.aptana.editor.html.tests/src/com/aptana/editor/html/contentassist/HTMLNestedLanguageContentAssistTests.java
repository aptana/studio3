/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import com.aptana.editor.html.tests.HTMLEditorBasedTests;

/**
 * HTMLNestedLanguageContentAssistTests
 */
public class HTMLNestedLanguageContentAssistTests extends HTMLEditorBasedTests
{
	/**
	 * testJSEventAttribute
	 */
	public void testJSEventAttribute()
	{
		this.checkProposals("contentAssist/js-event-attribute.html", "alert");
	}

	/**
	 * testJSMathInAttribute
	 */
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
	public void testCSSStyleAttribute()
	{
		this.checkProposals("contentAssist/css-style-attribute.html", "font");
	}
}
