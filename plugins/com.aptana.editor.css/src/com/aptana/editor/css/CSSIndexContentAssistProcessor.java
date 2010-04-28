package com.aptana.editor.css;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.IndexContentAssistProcessor;
import com.aptana.editor.css.index.CSSIndexConstants;
import com.aptana.index.core.Index;

public class CSSIndexContentAssistProcessor extends IndexContentAssistProcessor
{
	/**
	 * CSSIndexContentAssistProcessor
	 * 
	 * @param abstractThemeableEditor
	 */
	public CSSIndexContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
	{
		super(abstractThemeableEditor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IndexContentAssistProcessor#computeCompletionProposalsUsingIndex(org.eclipse.jface.text.ITextViewer, int, com.aptana.index.core.Index, java.util.List)
	 */
	@Override
	protected void computeCompletionProposalsUsingIndex(ITextViewer viewer, int offset, Index index,
			List<ICompletionProposal> completionProposals)
	{
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, CSSIndexConstants.CLASS);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, CSSIndexConstants.IDENTIFIER);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, CSSIndexConstants.COLOR);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, CSSIndexConstants.ELEMENT);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, CSSIndexConstants.PROPERTY);
	}
}
