package com.aptana.editor.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.ICommonContentAssistProcessor;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class CommonContentAssistProcessor implements IContentAssistProcessor, ICommonContentAssistProcessor
{
	protected final AbstractThemeableEditor editor;

	/**
	 * IndexContentAssistProcessor
	 * 
	 * @param editor
	 */
	public CommonContentAssistProcessor(AbstractThemeableEditor editor)
	{
		this.editor = editor;
	}

	/**
	 * addCompletionProposalsForCategory
	 * 
	 * @param viewer
	 * @param offset
	 * @param index
	 * @param completionProposals
	 * @param category
	 */
	protected void addCompletionProposalsForCategory(ITextViewer viewer, int offset, Index index, List<ICompletionProposal> completionProposals, String category)
	{
		try
		{
			List<QueryResult> queryResults = index.query(new String[] { category }, "", SearchPattern.PREFIX_MATCH); //$NON-NLS-1$
			
			if (queryResults != null)
			{
				for (QueryResult queryResult : queryResults)
				{
					String text = queryResult.getWord();
					int length = text.length();
					String info = category + " : " + text;
					
					completionProposals.add(new CompletionProposal(text, offset, 0, length, null, text, null, info));
				}
			}
		}
		catch (IOException e)
		{
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text
	 * .ITextViewer, int)
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		List<ICompletionProposal> completionProposals = new ArrayList<ICompletionProposal>();
		Index index = this.getIndex();

		if (index != null)
		{
			this.computeCompletionProposalsUsingIndex(viewer, offset, index, completionProposals);
		}

		return completionProposals.toArray(new ICompletionProposal[completionProposals.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ICommonContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int, char, boolean)
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated)
	{
		// NOTE: This is the default implementation. Specific language CA processors
		// should override this method
		return this.computeCompletionProposals(viewer, offset);
	}

	/**
	 * computeCompletionProposalsUsingIndex
	 * 
	 * @param viewer
	 * @param offset
	 * @param index
	 * @param completionProposals
	 */
	protected void computeCompletionProposalsUsingIndex(ITextViewer viewer, int offset, Index index, List<ICompletionProposal> completionProposals)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text
	 * .ITextViewer, int)
	 */
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	@Override
	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * getIndex
	 * 
	 * @return
	 */
	protected Index getIndex()
	{
		IEditorInput editorInput = editor.getEditorInput();
		Index result = null;

		if (editorInput instanceof IFileEditorInput)
		{
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			IFile file = fileEditorInput.getFile();
			IProject project = file.getProject();
			
			result = IndexManager.getInstance().getIndex(project.getFullPath().toPortableString());
		}
		
		return result;
	}
	
	/**
	 * setSelectedProposal
	 * 
	 * @param prefix
	 * @param proposals
	 */
	protected void setSelectedProposal(String prefix, List<ICompletionProposal> proposals)
	{
		ICompletionProposal caseSensitiveProposal = null;
		ICompletionProposal caseInsensitiveProposal = null;
		ICompletionProposal suggestedProposal = null;

		for (ICompletionProposal proposal : proposals)
		{
			String displayString = proposal.getDisplayString();
			int comparison = displayString.compareToIgnoreCase(prefix);

			if (comparison >= 0)
			{
				if (displayString.toLowerCase().startsWith(prefix.toLowerCase()))
				{
					caseInsensitiveProposal = proposal;

					if (displayString.startsWith(prefix))
					{
						caseSensitiveProposal = proposal;
						// found a match, so exit loop
						break;
					}
				}
			}
		}

		if (caseSensitiveProposal instanceof CommonCompletionProposal)
		{
			((CommonCompletionProposal) caseSensitiveProposal).setIsDefaultSelection(true);
		}
		else if (caseInsensitiveProposal instanceof CommonCompletionProposal)
		{
			((CommonCompletionProposal) caseInsensitiveProposal).setIsDefaultSelection(true);
		}
		else if (suggestedProposal instanceof CommonCompletionProposal)
		{
			((CommonCompletionProposal) suggestedProposal).setIsSuggestedSelection(true);
		}
		else
		{
			if (proposals.size() > 0)
			{
				ICompletionProposal proposal = proposals.get(0);
				
				if (proposal instanceof CommonCompletionProposal)
				{
					((CommonCompletionProposal) proposal).setIsSuggestedSelection(true);
				}
			}
		}
	}
}
