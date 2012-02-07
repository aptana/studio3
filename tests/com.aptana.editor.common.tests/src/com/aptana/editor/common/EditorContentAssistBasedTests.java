/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;

import com.aptana.ui.util.UIUtils;

public abstract class EditorContentAssistBasedTests<T extends CommonContentAssistProcessor> extends EditorBasedTests
{
	protected T processor;

	/**
	 * checkProposals
	 * 
	 * @param resource
	 * @param displayNames
	 */
	protected void checkProposals(String resource, String... displayNames)
	{
		checkProposals(resource, false, false, displayNames);
	}

	/**
	 * Assertion that a list of proposals contains the set of displayNames.
	 * 
	 * @param proposals
	 * @param displayNames
	 */
	protected void assertContains(ICompletionProposal[] proposals, String... displayNames)
	{
		Set<String> uniqueDisplayNames = new HashSet<String>(Arrays.asList(displayNames));
		for (ICompletionProposal proposal : proposals)
		{
			if (uniqueDisplayNames.contains(proposal.getDisplayString()))
			{
				uniqueDisplayNames.remove(proposal.getDisplayString());
			}
		}

		if (!uniqueDisplayNames.isEmpty())
		{
			// build a list of display names
			List<String> names = new ArrayList<String>();
			for (ICompletionProposal proposal : proposals)
			{
				names.add(proposal.getDisplayString());
			}
			fail(MessageFormat.format(
					"Proposals do not contain an entry for expected display string(s): {0}.\nProposal list: {1}",
					uniqueDisplayNames, names));
		}
	}

	/**
	 * Tests if the proposals list contains a set of proposals using specific display names.
	 * 
	 * @param proposals
	 * @param displayNames
	 */
	protected void assertDoesntContain(ICompletionProposal[] proposals, String... displayNames)
	{
		Set<String> uniqueDisplayNames = new HashSet<String>(Arrays.asList(displayNames));
		Set<String> matches = new HashSet<String>(uniqueDisplayNames.size());
		for (ICompletionProposal proposal : proposals)
		{
			if (uniqueDisplayNames.contains(proposal.getDisplayString()))
			{
				matches.add(proposal.getDisplayString());
			}
		}

		if (!matches.isEmpty())
		{
			fail(MessageFormat.format("Proposals contain an entry for disallowed display string(s): {0}", matches));
		}
	}

	/**
	 * checkProposals
	 * 
	 * @param resource
	 * @param displayNames
	 */
	protected void checkProposals(String resource, boolean enforceOrder, boolean enforceSize, String... displayNames)
	{
		this.setupTestContext(resource);

		ITextViewer viewer = new TextViewer(UIUtils.getActiveShell(), SWT.NONE);
		viewer.setDocument(this.document);

		for (int offset : this.cursorOffsets)
		{
			// get proposals
			ICompletionProposal[] proposals = this.processor.computeCompletionProposals(viewer, offset, '\0', false);

			// build a list of display names
			ArrayList<String> names = new ArrayList<String>();

			for (ICompletionProposal proposal : proposals)
			{
				// we need to check if it is a valid proposal given the context
				if (proposal instanceof ICompletionProposalExtension2)
				{
					ICompletionProposalExtension2 p = (ICompletionProposalExtension2) proposal;
					if (p.validate(document, offset, null)) // FIXME Should we fail if any turn out to be invalid?
					{
						names.add(proposal.getDisplayString());
					}
				}
				else
				{
					names.add(proposal.getDisplayString());
				}
			}

			if (enforceOrder || enforceSize)
			{
				assertEquals("Length of expected proposal list and actual proposal list did not match.",
						displayNames.length, names.size());
			}

			// this only really makes sense with enforce size
			if (enforceOrder)
			{
				for (int i = 0; i < displayNames.length; i++)
				{
					String displayName = displayNames[i];
					assertEquals("Did not find " + displayName + " in the proposal list at the expected spot",
							displayName, names.get(i));
				}
			}
			else
			{
				assertContains(proposals, displayNames);
			}
		}
	}

	/**
	 * createContentAssistProcessor
	 * 
	 * @param editor
	 * @return
	 */
	protected abstract T createContentAssistProcessor(AbstractThemeableEditor editor);

	/**
	 * setupTestContext
	 * 
	 * @param resource
	 * @return
	 */
	protected void setupTestContext(IFileStore store, IEditorInput editorInput)
	{
		super.setupTestContext(store, editorInput);
		this.processor = this.createContentAssistProcessor((AbstractThemeableEditor) this.editor);
	}

	/**
	 * tearDownTestContext
	 * 
	 * @param resource
	 * @return
	 */
	protected void tearDownTestContext()
	{
		super.tearDownTestContext();

		if (processor != null)
		{
			processor.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		processor = null;
		super.tearDown();
	}
}
