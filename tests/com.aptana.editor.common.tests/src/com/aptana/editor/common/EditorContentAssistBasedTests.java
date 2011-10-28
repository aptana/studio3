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

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;

import com.aptana.core.util.StringUtil;

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
	 * checkProposals
	 * 
	 * @param resource
	 * @param displayNames
	 */
	protected void checkProposals(String resource, boolean enforceOrder, boolean enforceSize, String... displayNames)
	{
		this.setupTestContext(resource);

		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
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
					if (p.validate(document, offset, null))
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
				assertTrue(
						StringUtil.format(
								"Length of expected proposal list and actual proposal list did not match.\nExpected: <{0}> Actual: <{1}>",
								new Object[] { StringUtil.join(", ", displayNames), StringUtil.join(", ", names) }),
						displayNames.length == names.size());
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
				// verify each specified name is in the resulting proposal list
				for (String displayName : displayNames)
				{
					assertTrue(
							MessageFormat.format("Did not find {0} in the proposal list <{1}>", displayName,
									StringUtil.join(", ", names)), names.contains(displayName));
				}
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
