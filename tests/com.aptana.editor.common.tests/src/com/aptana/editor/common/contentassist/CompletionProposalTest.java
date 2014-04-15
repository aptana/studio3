/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.ui.util.UIUtils;

/**
 * CompletionProposalTest
 */
public class CompletionProposalTest
{
	Image image;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		image = UIUtils.getImage(CommonEditorPlugin.getDefault(), "icons/proposal.png");
	}

	@Test
	public void testProposalsAreEqual()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");

		assertEquals("Proposals should be equal", proposal1, proposal2);
	}

	@Test
	public void testProposalsReplacementStringsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("b", 0, 1, 2, image, "abc", null, "none");

		assertFalse("Replacement strings should not match", proposal1.equals(proposal2));
	}

	@Test
	public void testProposalsReplacementOffsetsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 1, 1, 2, image, "abc", null, "none");

		assertFalse("Replacement offsets should not match", proposal1.equals(proposal2));
	}

	@Test
	public void testProposalsReplacementLengthsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 2, 2, image, "abc", null, "none");

		assertFalse("Replacement lengths should not match", proposal1.equals(proposal2));
	}

	@Test
	public void testProposalsCursorPositionsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 3, image, "abc", null, "none");

		assertFalse("Cursor positions should not match", proposal1.equals(proposal2));
	}

	@Test
	public void testProposalsImagesDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, null, "abc", null, "none");

		assertFalse("Images should not match", proposal1.equals(proposal2));
	}

	@Test
	public void testProposalsDisplayStringsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, image, "def", null, "none");

		assertFalse("Display strings should not match", proposal1.equals(proposal2));
	}

	@Test
	public void testProposalsContextInfosDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc",
				new ContextInformation("def", "ghi"), "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc",
				new ContextInformation("abc", "def"), "none");

		assertFalse("Context infos should not match", proposal1.equals(proposal2));
	}

	@Test
	public void testProposalsAdditionalInfosDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "some");

		assertFalse("Additional infos should not match", proposal1.equals(proposal2));
	}

	@Test
	public void testProposalsLocationsDiffer()
	{
		CommonCompletionProposal proposal1 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");
		proposal1.setFileLocation("somewhere");
		CommonCompletionProposal proposal2 = new CommonCompletionProposal("a", 0, 1, 2, image, "abc", null, "none");

		assertFalse("Locations should not match", proposal1.equals(proposal2));
	}
}
