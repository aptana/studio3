/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import junit.framework.TestCase;

import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.CommonEditorPlugin;

/**
 * CompletionProposalTest
 */
public class CompletionProposalTest extends TestCase
{
	Image image;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		image = CommonEditorPlugin.getImage("icons/proposal.png");
	}

	public void testProposalsAreEqual()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");

		assertTrue(proposal1.equals(proposal2));
	}

	public void testProposalsReplacementStringsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("b", 0, 1, 2, image, "abc", null, "none");

		assertFalse(proposal1.equals(proposal2));
	}

	public void testProposalsReplacementOffsetsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 1, 1, 2, image, "abc", null, "none");

		assertFalse(proposal1.equals(proposal2));
	}

	public void testProposalsReplacementLengthsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 2, 2, image, "abc", null, "none");

		assertFalse(proposal1.equals(proposal2));
	}

	public void testProposalsCursorPositionsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 3, image, "abc", null, "none");

		assertFalse(proposal1.equals(proposal2));
	}

	public void testProposalsImagesDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, null, "abc", null, "none");

		assertFalse(proposal1.equals(proposal2));
	}

	public void testProposalsDisplayStringsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, image, "def", null, "none");

		assertFalse(proposal1.equals(proposal2));
	}

	public void testProposalsContextInfosDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc",
				new ContextInformation("def", "ghi"), "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc",
				new ContextInformation("abc", "def"), "none");

		assertFalse(proposal1.equals(proposal2));
	}

	public void testProposalsAdditionalInfosDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "some");

		assertFalse(proposal1.equals(proposal2));
	}

	public void testProposalsLocationsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		proposal1.setFileLocation("somewhere");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");

		assertFalse(proposal1.equals(proposal2));
	}
}
