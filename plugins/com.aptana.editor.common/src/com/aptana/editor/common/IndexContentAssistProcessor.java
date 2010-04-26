package com.aptana.editor.common;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
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

import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class IndexContentAssistProcessor implements IContentAssistProcessor
{

	private final AbstractThemeableEditor abstractThemeableEditor;

	public IndexContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
	{
		this.abstractThemeableEditor = abstractThemeableEditor;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		IEditorInput editorInput = abstractThemeableEditor.getEditorInput();
		List<ICompletionProposal> completionProposals = new LinkedList<ICompletionProposal>();
		if (editorInput instanceof IFileEditorInput)
		{
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			IFile file = fileEditorInput.getFile();
			IProject project = file.getProject();
			Index index = IndexManager.getInstance().getIndex(project.getFullPath().toPortableString());
			computeCompletionProposalsUsingIndex(viewer, offset, index, completionProposals);
			
			// sort by display name, ignoring case
			Collections.sort(completionProposals, new Comparator<ICompletionProposal>()
			{
				@Override
				public int compare(ICompletionProposal o1, ICompletionProposal o2)
				{
					return o1.getDisplayString().compareToIgnoreCase(o2.getDisplayString());
				}
			});
		}

		return completionProposals.toArray(new ICompletionProposal[0]);
	}

	protected void computeCompletionProposalsUsingIndex(ITextViewer viewer, int offset, Index index,
			List<ICompletionProposal> completionProposals)
	{
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

	protected void addCompletionProposalsForCategory(ITextViewer viewer, int offset, Index index, List<ICompletionProposal> completionProposals, String category)
	{
		try
		{
			List<QueryResult> queryResults;
			queryResults = index.query(new String[] {category}, "", SearchPattern.PREFIX_MATCH); //$NON-NLS-1$
			if (queryResults != null)
			{
				for (QueryResult queryResult : queryResults)
				{
					completionProposals.add(new CompletionProposal(queryResult.getWord(), offset, 0, 0, null, category + ":" + queryResult.getWord(), null, category));
				}
			}
		}
		catch (IOException e)
		{
		}
	}

}
