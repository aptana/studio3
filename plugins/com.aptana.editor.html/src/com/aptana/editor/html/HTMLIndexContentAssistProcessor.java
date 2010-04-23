package com.aptana.editor.html;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.IndexContentAssistProcessor;
import com.aptana.editor.css.index.IIndexConstants;
import com.aptana.editor.html.index.IHTMLIndexConstants;
import com.aptana.editor.js.index.IndexConstants;
import com.aptana.index.core.Index;

public class HTMLIndexContentAssistProcessor extends IndexContentAssistProcessor
{

	public HTMLIndexContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
	{
		super(abstractThemeableEditor);
	}

	@Override
	protected void computeCompletionProposalsUsingIndex(ITextViewer viewer, int offset, Index index,
			List<ICompletionProposal> completionProposals)
	{
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IHTMLIndexConstants.RESOURCE_CSS);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IHTMLIndexConstants.RESOURCE_JS);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IIndexConstants.CSS_CLASS);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IIndexConstants.CSS_IDENTIFIER);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IIndexConstants.CSS_COLOR);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IIndexConstants.CSS_ELEMENT);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IIndexConstants.CSS_PROPERTY);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IndexConstants.TYPE);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IndexConstants.FUNCTION);
	}

}
