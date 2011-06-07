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
	 * testCSSStyleAttribute
	 */
	public void testCSSStyleAttribute()
	{
		this.checkProposals("contentAssist/css-style-attribute.html", "font");
	}
}
