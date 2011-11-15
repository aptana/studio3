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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;

import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.tests.JSEditorBasedTests;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SnippetElement;

/**
 * JSContentAssistProposalTests
 */
public class JSContentAssistProposalTests extends JSEditorBasedTests
{
	protected void assertAutoActivation(String sourceWithCursors, boolean expectedResult)
	{
		IFileStore fileStore = createFileStore("proposal_tests", "js", sourceWithCursors);

		setupTestContext(fileStore);
		assertEquals(expectedResult, processor.isValidAutoActivationLocation(' ', ' ', document, cursorOffsets.get(0)));
	}

	protected void assertContainsFunctions(Collection<PropertyElement> projectGlobals, String... functionNames)
	{
		Set<String> uniqueFunctionNames = new HashSet<String>(Arrays.asList(functionNames));
		for (PropertyElement element : projectGlobals)
		{
			if (!(element instanceof FunctionElement))
			{
				continue;
			}
			if (uniqueFunctionNames.contains(element.getName()))
			{
				uniqueFunctionNames.remove(element.getName());
			}
		}

		if (!uniqueFunctionNames.isEmpty())
		{
			// build a list of names
			List<String> names = new ArrayList<String>();
			for (PropertyElement element : projectGlobals)
			{
				if (!(element instanceof FunctionElement))
				{
					continue;
				}
				names.add(element.getName());
			}
			fail(MessageFormat.format(
					"Functions do not contain an entry for expected name(s): {0}.\nFunction list: {1}",
					uniqueFunctionNames, names));
		}
	}

	protected void assertDoesntContainFunctions(Collection<PropertyElement> projectGlobals, String... functionNames)
	{
		Set<String> uniqueFunctionNames = new HashSet<String>(Arrays.asList(functionNames));
		Set<String> matches = new HashSet<String>(uniqueFunctionNames.size());
		for (PropertyElement element : projectGlobals)
		{
			if (!(element instanceof FunctionElement))
			{
				continue;
			}
			if (uniqueFunctionNames.contains(element.getName()))
			{
				matches.add(element.getName());
			}
		}

		if (!matches.isEmpty())
		{
			fail(MessageFormat.format("Functions contain an entry for disallowed name(s): {0}", matches));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#createIndexer()
	 */
	@Override
	protected IFileStoreIndexingParticipant createIndexer()
	{
		return new JSFileIndexingParticipant();
	}

	public void testAutoAactivationCommaWithSpace()
	{
		assertAutoActivation("a(abc, \t\r\n|", true);
	}

	public void testAutoActivationComma()
	{
		assertAutoActivation("a(abc,|", true);
	}

	public void testAutoActivationIdentifier()
	{
		assertAutoActivation("a|(abc,", false);
	}

	public void testAutoActivationIdentifierWithSpace()
	{
		assertAutoActivation("a \t\r\n|(abc,", false);
	}

	public void testAutoActivationLeftParen()
	{
		assertAutoActivation("a(|abc,", true);
	}

	public void testAutoActivationLeftParenWithSpace()
	{
		assertAutoActivation("a( \t\r\n|abc,", true);
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

	/**
	 * testStringCharCodeAt
	 */
	public void testStringCharCodeAt()
	{
		this.checkProposals("contentAssist/string-charCodeAt.js", "charCodeAt");
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

		try
		{
			// note template is before true proposal, as we are ordering by trigger prefix
			this.checkProposals("contentAssist/f-prefix.js", true, true, "FunctionTemplate", "false", "finally",
					"focus", "for", "forward", "frames", "function", "Function");
		}
		finally
		{
			BundleManager.getInstance().unloadScript(f);
		}

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

		this.checkProposals("contentAssist/this.js", true, true, "$(this)", "this", "throw");

		BundleManager.getInstance().unloadScript(f);

	}
}
