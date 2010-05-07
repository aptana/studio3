package com.aptana.editor.common.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public interface ICommonContentAssistProcessor
{
	/**
	 * computeCompletionProposals
	 * 
	 * @param viewer
	 * @param offset
	 * @param activationChar
	 * @param autoActivated
	 * @return
	 */
	ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated);
}
