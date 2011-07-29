/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.io.File;
import java.io.IOException;

import com.aptana.editor.js.tests.JSEditorBasedTests;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SnippetElement;

/**
 * JSContentAssistProposalTests
 */
public class JSContentAssistProposalTests extends JSEditorBasedTests
{
	/**
	 * testStringCharCodeAt
	 */
	public void testStringCharCodeAt()
	{
		this.checkProposals("contentAssist/string-charCodeAt.js", "charCodeAt");
	}

	/**
	 * testStringF
	 * 
	 * @throws IOException
	 */
	public void testStringFPrefix() throws IOException
	{
		File bundleFile = File.createTempFile("editor_unit_tests", "rb");
		bundleFile.deleteOnExit();

		BundleElement bundleElement = new BundleElement(bundleFile.getAbsolutePath());
		bundleElement.setDisplayName("Editor Unit Tests");

		File f = File.createTempFile("snippet", "rb");
		SnippetElement se = createSnippet(f.getAbsolutePath(), "FunctionTemplate", "fun", "source.js");
		bundleElement.addChild(se);
		BundleManager.getInstance().addBundle(bundleElement);

		// note template is before true proposal, as we are ordering by trigger prefix
		this.checkProposals("contentAssist/f-prefix.js", true, true, "false", "finally", "focus", "for", "forward",
				"frames", "function", "FunctionTemplate", "Function");

		BundleManager.getInstance().unloadScript(f);

	}

	/**
	 * testStringFunction
	 */
	public void testStringFunction()
	{
		this.checkProposals("contentAssist/function.js", true, true, "function", "Function");
	}

	/**
	 * testStringFunction
	 */
	public void testStringFunctionCaseOrder()
	{
		// Commented out for the moment until we resolve an issue with indexing
		// this.checkProposals("contentAssist/function-case-order.js", true, true, "focus", "foo", "fooa", "foob",
		// "for",
		// "forward");
	}

	/**
	 * testStringFunction
	 * 
	 * @throws IOException
	 */
	public void testStringThis() throws IOException
	{
		File bundleFile = File.createTempFile("editor_unit_tests", "rb");
		bundleFile.deleteOnExit();

		BundleElement bundleElement = new BundleElement(bundleFile.getAbsolutePath());
		bundleElement.setDisplayName("Editor Unit Tests");

		File f = File.createTempFile("snippet", "rb");
		SnippetElement se = createSnippet(f.getAbsolutePath(), "$(this)", "this", "source.js");
		bundleElement.addChild(se);
		BundleManager.getInstance().addBundle(bundleElement);

		// note after is before true proposal, as we are ordering by trigger prefix
		this.checkProposals("contentAssist/this.js", true, true, "this", "throw", "$(this)");

		BundleManager.getInstance().unloadScript(f);

	}

	/**
	 * testStringD
	 */
	public void testStringDPrefix()
	{
		this.checkProposals("contentAssist/d-prefix.js", true, true, "default", "defaultStatus", "delete", "do",
				"document", "Date");
	}

	/**
	 * testBug_Math
	 */
	public void testBug_Math()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/math.js",
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
	 * testBug_VarAssignWithEndingDot
	 */
	public void testBug_VarAssignWithEndingDot()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/var-assign-with-ending-dot.js",
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
	 * testObjectLiteral
	 */
	public void testObjectLiteral()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/object-literal.js",
			"flag",
			"number"
		);
		// @formatter:on
	}
}
