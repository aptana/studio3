package com.aptana.editor.js;

import java.io.IOException;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.IndexContentAssistProcessor;
import com.aptana.editor.js.index.IndexConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

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
		Index metadataIndex = IndexManager.getInstance().getIndex(IndexConstants.METADATA);
		
//		addCompletionProposalsForCategory(viewer, offset, metadataIndex, completionProposals, IndexConstants.TYPE);
		addCompletionProposalsForCategory(viewer, offset, metadataIndex, completionProposals, IndexConstants.FUNCTION);
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IndexContentAssistProcessor#addCompletionProposalsForCategory(org.eclipse.jface.text.ITextViewer, int, com.aptana.index.core.Index, java.util.List, java.lang.String)
	 */
	@Override
	protected void addCompletionProposalsForCategory(ITextViewer viewer, int offset, Index index,
			List<ICompletionProposal> completionProposals, String category)
	{
		try
		{
			List<QueryResult> types = index.query(new String[] { category }, "", SearchPattern.PREFIX_MATCH);
			
			if (types != null)
			{
				for (QueryResult result : types)
				{
					String rawValue = result.getWord();
					String word = rawValue.substring(0, rawValue.indexOf(IndexConstants.DELIMITER));
					String description = rawValue.substring(rawValue.lastIndexOf(IndexConstants.DELIMITER) + 1);
					String info = category + ":" + rawValue + "\n" + description;
					CompletionProposal proposal = new CompletionProposal(word, offset, 0, word.length(), null, word, null, info);
					
					completionProposals.add(proposal);
				}
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
