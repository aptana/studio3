package com.aptana.editor.js;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.IndexContentAssistProcessor;
import com.aptana.editor.js.index.IndexConstants;
import com.aptana.index.core.Index;

public class JSIndexContentAssistProcessor extends IndexContentAssistProcessor
{

	public JSIndexContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
	{
		super(abstractThemeableEditor);
	}

	@Override
	protected void computeCompletionProposalsUsingIndex(ITextViewer viewer, int offset, Index index,
			List<ICompletionProposal> completionProposals)
	{
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IndexConstants.TYPE);
		addCompletionProposalsForCategory(viewer, offset, index, completionProposals, IndexConstants.FUNCTION);
	}

}
