/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

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
	public void testStringCharCodeAt()
	{
		TestContext context = this.getTestContext("contentAssist/string-charCodeAt.js");
		int offset = context.source.length();
		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
		viewer.setDocument(context.document);
		ICompletionProposal[] proposals = context.processor.doComputeCompletionProposals(viewer, offset, '\0', false);
		boolean foundCharCodeAt = false;
		
		for (ICompletionProposal proposal : proposals)
		{
			if ("charCodeAt".equals(proposal.getDisplayString()))
			{
				foundCharCodeAt = true;
				break;
			}
		}
		assertTrue(foundCharCodeAt);
	}
}
