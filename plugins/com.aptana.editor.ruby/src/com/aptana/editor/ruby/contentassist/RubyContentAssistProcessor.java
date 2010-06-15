package com.aptana.editor.ruby.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.ruby.Activator;
import com.aptana.editor.ruby.index.IRubyIndexConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class RubyContentAssistProcessor extends CommonContentAssistProcessor
{
	public RubyContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);
	}

	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		// create proposal container
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		Index index = getIndex();
		try
		{
			String prefix = getPrefix(viewer, offset);
			List<QueryResult> results = index.query(new String[] { IRubyIndexConstants.FIELD_DECL,
					IRubyIndexConstants.METHOD_DECL, IRubyIndexConstants.TYPE_DECL }, prefix,
					SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE);
			if (results != null)
			{
				for (QueryResult result : results)
				{
					String name = result.getWord();
					// FIXME We need to "decode" the word since it's the raw key, which has identifier plus a bunch of
					// other info
					int firstSeparator = name.indexOf(IRubyIndexConstants.SEPARATOR);
					if (firstSeparator != -1)
					{
						name = name.substring(0, firstSeparator);
					}
					CommonCompletionProposal proposal = createProposal(offset, prefix, name);
					StringBuilder builder = new StringBuilder();
					for (String doc : result.getDocuments())
					{
						builder.append(doc).append(", "); //$NON-NLS-1$
					}
					if (builder.length() > 0)
					{
						builder.delete(builder.length() - 2, builder.length());
					}
					proposal.setFileLocation(builder.toString());
					// add it to the list
					proposals.add(proposal);
				}
			}
		}
		catch (Exception e)
		{
			Activator.log(e);
		}

		// sort by display name
		Collections.sort(proposals, new Comparator<ICompletionProposal>()
		{
			@Override
			public int compare(ICompletionProposal o1, ICompletionProposal o2)
			{
				return o1.getDisplayString().compareToIgnoreCase(o2.getDisplayString());
			}
		});

		// return results
		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	protected CommonCompletionProposal createProposal(int offset, String prefix, String value)
	{
		String description = ""; //$NON-NLS-1$
		int replaceLength = prefix.length();
		int length = value.length();
		String displayName = value;
		Image image = null;
		IContextInformation contextInfo = null;
		// build proposal
		return new CommonCompletionProposal(value, offset - replaceLength, replaceLength, length, image, displayName,
				contextInfo, description);
	}

	protected String getPrefix(ITextViewer viewer, int offset) throws BadLocationException
	{
		IDocument doc = viewer.getDocument();
		IRegion lineInfo = doc.getLineInformationOfOffset(offset);
		String linePrefix = doc.get(lineInfo.getOffset(), offset - lineInfo.getOffset());
		// find last period/space/:
		int indexOfPeriod = linePrefix.lastIndexOf('.');
		if (indexOfPeriod != -1)
		{
			linePrefix = linePrefix.substring(indexOfPeriod + 1);
		}
		indexOfPeriod = linePrefix.lastIndexOf(':');
		if (indexOfPeriod != -1)
		{
			linePrefix = linePrefix.substring(indexOfPeriod + 1);
		}
		indexOfPeriod = linePrefix.lastIndexOf(' ');
		if (indexOfPeriod != -1)
		{
			linePrefix = linePrefix.substring(indexOfPeriod + 1);
		}
		return linePrefix;
	}
}
