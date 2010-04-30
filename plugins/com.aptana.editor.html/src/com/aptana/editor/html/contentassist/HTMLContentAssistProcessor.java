package com.aptana.editor.html.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.html.contentassist.model.ElementElement;

public class HTMLContentAssistProcessor implements IContentAssistProcessor
{
	private HTMLContentAssistHelper _helper;
	
	/**
	 * HTMLIndexContentAssistProcessor
	 * 
	 * @param abstractThemeableEditor
	 */
	public HTMLContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
	{
		this._helper = new HTMLContentAssistHelper();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		List<ElementElement> elements = this._helper.getElements();
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		
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
				result.add(proposal);
			}
		}
		
		return result.toArray(new ICompletionProposal[result.size()]);
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
