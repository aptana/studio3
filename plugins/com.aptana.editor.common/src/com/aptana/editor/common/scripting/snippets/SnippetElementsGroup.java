/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.common.scripting.snippets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * We need to only group the templates with the same name. Otherwise, we might end up auto inserting the snippet for the
 * wrong prefix.
 * 
 * @author pinnamuri
 */
public class SnippetElementsGroup
{
	private List<ICompletionProposal> snippetProposals;
	private int currTriggerNumber;

	public SnippetElementsGroup()
	{
		snippetProposals = new ArrayList<ICompletionProposal>();
	}

	public List<ICompletionProposal> getSnippetProposals()
	{
		return snippetProposals;
	}

	public void addSnippetProposal(ICompletionProposal snippetProposal)
	{
		if (snippetProposal instanceof SnippetTemplateProposal && currTriggerNumber < 9)
		{
			((SnippetTemplateProposal) snippetProposal).setTriggerChar((char) ('1' + currTriggerNumber));
			currTriggerNumber++;
		}
		snippetProposals.add(snippetProposal);
	}
}
