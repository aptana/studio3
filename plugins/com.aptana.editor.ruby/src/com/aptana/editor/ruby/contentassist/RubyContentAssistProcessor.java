package com.aptana.editor.ruby.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
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
			IDocument doc = viewer.getDocument();
			IRegion lineInfo = doc.getLineInformationOfOffset(offset);
			String linePrefix = doc.get(lineInfo.getOffset(), offset - lineInfo.getOffset());
			// find last period/space/:
			int indexOfPeriod = linePrefix.lastIndexOf(".");
			if (indexOfPeriod != -1)
			{
				linePrefix = linePrefix.substring(indexOfPeriod + 1);
			}
			indexOfPeriod = linePrefix.lastIndexOf(":");
			if (indexOfPeriod != -1)
			{
				linePrefix = linePrefix.substring(indexOfPeriod + 1);
			}
			indexOfPeriod = linePrefix.lastIndexOf(" ");
			if (indexOfPeriod != -1)
			{
				linePrefix = linePrefix.substring(indexOfPeriod + 1);
			}

			String prefix = linePrefix;

			List<QueryResult> results = index.query(new String[] { IRubyIndexConstants.FIELD_DECL,
					IRubyIndexConstants.METHOD_DECL, IRubyIndexConstants.TYPE_DECL }, prefix,
					SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE);
			if (results != null)
			{
				for (QueryResult result : results)
				{
					String name = result.getWord();
					// FIXME We need to "decode" the word since it's the raw key, which has identifier plus a bunch of other info
					int firstSeparator = name.indexOf(IRubyIndexConstants.SEPARATOR);
					if (firstSeparator != -1)
					{
						name = name.substring(0, firstSeparator);
					}
					String description = "";
					int replaceLength = prefix.length();
					int length = name.length();
					String displayName = name;
					Image image = null;
					IContextInformation contextInfo = null;
					// build proposal
					CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset - replaceLength,
							replaceLength, length, image, displayName, contextInfo, description);

					// add it to the list
					proposals.add(proposal);
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
