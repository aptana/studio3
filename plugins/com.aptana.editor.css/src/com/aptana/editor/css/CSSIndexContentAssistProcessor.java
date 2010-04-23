package com.aptana.editor.css;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.IndexContentAssistProcessor;
import com.aptana.editor.css.index.IIndexConstants;
import com.aptana.index.core.Index;

public class CSSIndexContentAssistProcessor extends IndexContentAssistProcessor
{

	public CSSIndexContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
	{
		super(abstractThemeableEditor);
	}

	@Override
	protected void computeCompletionProposalsUsingIndex(ITextViewer viewer, int offset, Index index,
			List<ICompletionProposal> completionProposals)
	{
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IIndexConstants.CSS_CLASS);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IIndexConstants.CSS_IDENTIFIER);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IIndexConstants.CSS_COLOR);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IIndexConstants.CSS_ELEMENT);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IIndexConstants.CSS_PROPERTY);
	}

}
