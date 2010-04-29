package com.aptana.editor.html.contentassist;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.IndexContentAssistProcessor;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.index.core.Index;

public class HTMLContentAssistProcessor extends IndexContentAssistProcessor
{
	private HTMLContentAssistHelper _helper;
	
	/**
	 * HTMLIndexContentAssistProcessor
	 * 
	 * @param abstractThemeableEditor
	 */
	public HTMLContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
	{
		super(abstractThemeableEditor);
		
		this._helper = new HTMLContentAssistHelper();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IndexContentAssistProcessor#computeCompletionProposalsUsingIndex(org.eclipse.jface.text.ITextViewer, int, com.aptana.index.core.Index, java.util.List)
	 */
	@Override
	protected void computeCompletionProposalsUsingIndex(ITextViewer viewer, int offset, Index index,
			List<ICompletionProposal> completionProposals)
	{
		List<ElementElement> elements = this._helper.getElements();
		
		if (elements != null)
		{
			for (ElementElement element : elements)
			{
				String openTag = "<" + element.getName() + ">";
				String closeTag = "</" + element.getName() + ">";
				String insertText = openTag + closeTag;
				int length = openTag.length();
				String displayName = element.getName();
				String description = element.getDescription();
				Image image = null;
				IContextInformation contextInfo = null;
				
				// build proposal
				CompletionProposal proposal = new CompletionProposal(insertText, offset, 0, length, image, displayName, contextInfo, description);
				
				// add it to the list
				completionProposals.add(proposal);
			}
		}
	}
}
