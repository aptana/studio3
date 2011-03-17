/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.aptana.editor.js.tests.EditorBasedTests;

/**
 * JSContentAssistProposalTests
 */
public class JSContentAssistProposalTests extends EditorBasedTests
{
	protected void checkProposals(String resource, String... displayNames)
	{
		TestContext context = this.getTestContext(resource);
		int offset = context.source.length();
		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
		viewer.setDocument(context.document);
		ICompletionProposal[] proposals = context.processor.doComputeCompletionProposals(viewer, offset, '\0', false);
		Set<String> names = new HashSet<String>();

		for (ICompletionProposal proposal : proposals)
		{
			names.add(proposal.getDisplayString());
		}

		for (String displayName : displayNames)
		{
			assertTrue(names.contains(displayName));
		}
	}

	/**
	 * testStringCharCodeAt
	 */
	public void testStringCharCodeAt()
	{
		this.checkProposals("contentAssist/string-charCodeAt.js", "charCodeAt");
	}

	/**
	 * testMath
	 */
	public void testMath()
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
}
