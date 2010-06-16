package com.aptana.editor.ruby.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.ProcessUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.ruby.Activator;
import com.aptana.editor.ruby.index.IRubyIndexConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class RubyContentAssistProcessor extends CommonContentAssistProcessor
{
	/**
	 * Prefixes to cheat and easily tell type of variable
	 */
	private static final String INSTANCE_VAR_NAME_PREFIX = "@"; //$NON-NLS-1$
	private static final String CLASS_VAR_NAME_PREFIX = "@@"; //$NON-NLS-1$
	private static final String GLOBAL_NAME_PREFIX = "$"; //$NON-NLS-1$

	/**
	 * Images for various proposals for Ruby.
	 */
	private static final String CONSTANT_IMAGE = "icons/constant_obj.gif"; //$NON-NLS-1$
	private static final String CLASS_IMAGE = "icons/class_obj.png"; //$NON-NLS-1$
	private static final String MODULE_IMAGE = "icons/module_obj.png"; //$NON-NLS-1$
	private static final String METHOD_IMAGE = "icons/method_public_obj.png"; //$NON-NLS-1$
	private static final String INSTANCE_VAR_IMAGE = "icons/instance_var_obj.gif"; //$NON-NLS-1$
	private static final String CLASS_VAR_IMAGE = "icons/class_var_obj.gif"; //$NON-NLS-1$
	private static final String GLOBAL_IMAGE = "icons/global_obj.png"; //$NON-NLS-1$
	private static final String LOCAL_VAR_IMAGE = "icons/local_var_obj.gif"; //$NON-NLS-1$

	public RubyContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);
	}

	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		// create proposal container
		Map<String, CommonCompletionProposal> proposalMap = new HashMap<String, CommonCompletionProposal>();

		try
		{
			String prefix = getPrefix(viewer, offset);
			List<QueryResult> results = new ArrayList<QueryResult>();
			List<Index> indices = getIndices();
			for (Index index : indices)
			{
				List<QueryResult> partialResults = index.query(new String[] { IRubyIndexConstants.FIELD_DECL,
						IRubyIndexConstants.METHOD_DECL, IRubyIndexConstants.TYPE_DECL }, prefix,
						SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE);
				if (partialResults != null)
				{
					results.addAll(partialResults);
				}
			}
			for (QueryResult result : results)
			{
				CommonCompletionProposal proposal = createProposal(offset, prefix, result);
				CommonCompletionProposal existing = proposalMap.get(proposal.getDisplayString());
				if (existing != null)
				{
					// Collapse results that have same value into one proposal, combine the filepaths
					// FIXME Make file locations unique, we have duplicates here too
					String location = existing.getFileLocation();
					if (!proposal.getFileLocation().equals(location))
					{
						location += ", " + proposal.getFileLocation(); //$NON-NLS-1$
						existing.setFileLocation(location);
					}
				}
				else
				{
					proposalMap.put(proposal.getDisplayString(), proposal);
				}
			}
		}
		catch (Exception e)
		{
			Activator.log(e);
		}

		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>(proposalMap.values());
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

	private List<Index> getIndices()
	{
		List<Index> indices = new ArrayList<Index>();
		indices.add(getIndex());
		indices.add(getRubyCoreIndex());

		// TODO Extract common code with CoreStubber out to some class!
		// Now add the Std Lib indices
		String rawLoadPathOutput = ProcessUtil.outputForCommand("ruby", null, ShellExecutable.getEnvironment(), "-e",  //$NON-NLS-1$//$NON-NLS-2$
				"puts $:"); //$NON-NLS-1$
		String[] loadpaths = rawLoadPathOutput.split("\r\n|\r|\n"); //$NON-NLS-1$
		for (String loadpath : loadpaths)
		{
			if (loadpath.equals(".")) //$NON-NLS-1$
				continue;
			Index index = IndexManager.getInstance().getIndex(loadpath);
			if (index != null)
			{
				indices.add(index);
			}
		}
		return indices;
	}

	protected Index getRubyCoreIndex()
	{
		String rubyVersion = ProcessUtil.outputForCommand("ruby", null, ShellExecutable.getEnvironment(), "-v"); //$NON-NLS-1$ //$NON-NLS-2$
		return IndexManager.getInstance().getIndex(rubyVersion);
	}

	protected CommonCompletionProposal createProposal(int offset, String prefix, QueryResult result)
	{
		String value = result.getWord();
		// We need to "decode" the word since it's the raw key, which has identifier plus a bunch of
		// other info
		int firstSeparator = value.indexOf(IRubyIndexConstants.SEPARATOR);
		if (firstSeparator != -1)
		{
			value = value.substring(0, firstSeparator);
		}

		String description = ""; //$NON-NLS-1$
		int replaceLength = prefix.length();
		int length = value.length();
		String displayName = value;

		CommonCompletionProposal proposal = new CommonCompletionProposal(value, offset - replaceLength, replaceLength,
				length, getImage(result), displayName, null, description);
		proposal.setFileLocation(getLocations(result));

		return proposal;
	}

	protected String getLocations(QueryResult result)
	{
		StringBuilder builder = new StringBuilder();
		for (String doc : result.getDocuments())
		{
			// HACK Detect when it's a core stub and change the reported name to "Ruby Core"
			if (doc.contains(".metadata")) //$NON-NLS-1$
			{
				doc = "Ruby Core"; //$NON-NLS-1$
			}
			builder.append(doc).append(", "); //$NON-NLS-1$
		}
		if (builder.length() > 0)
		{
			builder.delete(builder.length() - 2, builder.length());
		}
		return builder.toString();
	}

	protected Image getImage(QueryResult result)
	{
		Image image = Activator.getImage(LOCAL_VAR_IMAGE);
		if (result.getWord().startsWith(GLOBAL_NAME_PREFIX))
		{
			image = Activator.getImage(GLOBAL_IMAGE);
		}
		// Must check class var before instance because of prefix!
		else if (result.getWord().startsWith(CLASS_VAR_NAME_PREFIX))
		{
			image = Activator.getImage(CLASS_VAR_IMAGE);
		}
		else if (result.getWord().startsWith(INSTANCE_VAR_NAME_PREFIX))
		{
			image = Activator.getImage(INSTANCE_VAR_IMAGE);
		}
		else if (result.getWord().indexOf(IRubyIndexConstants.SEPARATOR) != -1)
		{
			// Must be a class/module/method
			String[] parts = result.getWord().split("" + IRubyIndexConstants.SEPARATOR); //$NON-NLS-1$
			if (parts.length == 2)
			{
				image = Activator.getImage(METHOD_IMAGE);
			}
			else
			{
				String isModule = parts[2];
				if (isModule.equals("" + IRubyIndexConstants.MODULE_SUFFIX)) //$NON-NLS-1$
				{
					image = Activator.getImage(MODULE_IMAGE);
				}
				else
				{
					image = Activator.getImage(CLASS_IMAGE);
				}
			}
		}
		else if (Character.isUpperCase(result.getWord().charAt(0)))
		{
			image = Activator.getImage(CONSTANT_IMAGE);
		}
		return image;
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
