package com.aptana.editor.html;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.IndexContentAssistProcessor;
import com.aptana.editor.css.index.CSSIndexConstants;
import com.aptana.editor.html.index.IHTMLIndexConstants;
import com.aptana.editor.js.index.JSIndexConstants;
import com.aptana.index.core.Index;

public class HTMLIndexContentAssistProcessor extends IndexContentAssistProcessor
{
	/**
	 * HTMLIndexContentAssistProcessor
	 * 
	 * @param abstractThemeableEditor
	 */
	public HTMLIndexContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
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
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IHTMLIndexConstants.RESOURCE_CSS);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IHTMLIndexConstants.RESOURCE_JS);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, CSSIndexConstants.CLASS);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, CSSIndexConstants.IDENTIFIER);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, CSSIndexConstants.COLOR);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, CSSIndexConstants.ELEMENT);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, CSSIndexConstants.PROPERTY);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, JSIndexConstants.TYPE);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, JSIndexConstants.FUNCTION);
	}
}
