/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.aptana.editor.css.tests.EditorBasedTests;

/**
 * JSContentAssistProposalTests
 */
public class CSSContentAssistProposalTests extends EditorBasedTests
{
	protected void checkProposals(String resource, String... displayNames)
	{
		TestContext context = this.getTestContext(resource);

		try
		{
			ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
			viewer.setDocument(context.document);

			for (int offset : context.cursorOffsets)
			{
				// get proposals
				ICompletionProposal[] proposals = context.processor.doComputeCompletionProposals(viewer, offset, '\0',
						false);

				// build a list of display names
				Set<String> names = new HashSet<String>();

				for (ICompletionProposal proposal : proposals)
				{
					names.add(proposal.getDisplayString());
				}

				// verify each specified name is in the resulting proposal list
				for (String displayName : displayNames)
				{
					assertTrue(names.contains(displayName));
				}
			}
		}
		finally
		{
			context.editor.close(false);
		}
	}

	/**
	 * testFailureAfterColon
	 */
	public void testFailureAfterColon()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/failure-after-colon.css",
			"center",
			"inherit",
			"justify",
			"left",
			"right"
		);
		// @formatter:on
	}
}
