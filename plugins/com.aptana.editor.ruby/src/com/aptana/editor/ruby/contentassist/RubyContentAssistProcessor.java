package com.aptana.editor.ruby.contentassist;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.HippieProposalProcessor;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;
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
	 * Separates file locations when using multiple values for proposal.
	 */
	private static final String LOCATIONS_DELIMETER = ", "; //$NON-NLS-1$

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
		Map<String, CommonCompletionProposal> proposalMap = new HashMap<String, CommonCompletionProposal>();
		// FIXME What about completions of stuff added to current file since last save? Do we need to hit up AST?
		try
		{
			String prefix = getPrefix(viewer, offset);
			// FIXME This is a giant hack. We try to only search project index for vars/methods/types in current file if
			// no prefix.
			// If there is a prefix, widen to Ruby Core, Std Lib and project and limit categories based on prefix (plus
			// preceding space/./::)
			List<QueryResult> results = new ArrayList<QueryResult>();
			List<Index> indices = getIndices(prefix);
			for (Index index : indices)
			{
				List<QueryResult> partialResults = index.query(getCategories(index, prefix, viewer, offset), prefix,
						SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE);
				if (partialResults != null)
				{
					results.addAll(partialResults);
				}
			}
			for (QueryResult result : results)
			{
				CommonCompletionProposal proposal = createProposal(offset, prefix, result);
				if (proposal == null)
				{
					continue;
				}

				CommonCompletionProposal existing = proposalMap.get(proposal.getDisplayString());
				if (existing != null)
				{
					// Collapse results that have same value into one proposal, combine the filepaths
					String location = existing.getFileLocation();
					if (!proposal.getFileLocation().equals(location))
					{
						String[] existingLocations = location.split(LOCATIONS_DELIMETER);
						String[] newLocations = proposal.getFileLocation().split(LOCATIONS_DELIMETER);
						Set<String> set = new HashSet<String>();
						for (String l : existingLocations)
						{
							set.add(l);
						}
						for (String l : newLocations)
						{
							set.add(l);
						}
						existing.setFileLocation(StringUtil.join(LOCATIONS_DELIMETER, set));
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

		// As a hack we use word completions to help round out possible completions. This helps make the changes
		// introduced since last save on current file a little less of an issue.
		HippieProposalProcessor processor = new HippieProposalProcessor();
		ICompletionProposal[] wordCompletions = processor.computeCompletionProposals(viewer, offset);
		proposals.addAll(Arrays.asList(wordCompletions));

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

	/**
	 * Search fields, methods and types, unless prefix is empty then just types.
	 * 
	 * @param index
	 * @param prefix
	 * @return
	 */
	private String[] getCategories(Index index, String prefix, ITextViewer viewer, int offset)
	{
		try
		{
			// if we're completing after a period, it's a method invocation
			char charBeforePrefix = viewer.getDocument().getChar(offset - (prefix.length() + 1));
			if (charBeforePrefix == '.')
			{
				return new String[] { IRubyIndexConstants.METHOD_DECL };
			}
		}
		catch (BadLocationException e)
		{
			Activator.log(e);
		}

		if (getIndex().equals(index))
		{
			return new String[] { IRubyIndexConstants.FIELD_DECL, IRubyIndexConstants.METHOD_DECL,
					IRubyIndexConstants.CONSTANT_DECL, IRubyIndexConstants.LOCAL_DECL, IRubyIndexConstants.TYPE_DECL };
		}
		// After space or ::, so could be pretty much anything.
		// TODO If after ::, only constants, types, methods
		return new String[] { IRubyIndexConstants.GLOBAL_DECL, IRubyIndexConstants.CONSTANT_DECL,
				IRubyIndexConstants.METHOD_DECL, IRubyIndexConstants.TYPE_DECL };
	}

	private List<Index> getIndices(String prefix)
	{
		List<Index> indices = new ArrayList<Index>();
		indices.add(getIndex());
		if (prefix == null || prefix.trim().length() == 0)
		{
			return indices;
		}
		indices.add(getRubyCoreIndex());

		// TODO Extract common code with CoreStubber out to some class!
		// Now add the Std Lib indices
		String rawLoadPathOutput = ProcessUtil.outputForCommand("ruby", null, ShellExecutable.getEnvironment(), "-e", //$NON-NLS-1$//$NON-NLS-2$
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

		// TODO For rails we need to include the rails gem indices!
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

		// Don't include local/class/instance variables out of current file/scope
		if (isVariable(result) && !isInCurrentFile(result))
		{
			return null;
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

	private boolean isInCurrentFile(QueryResult result)
	{
		URI uri = getURI();
		if (uri == null)
			return false;

		String[] documents = result.getDocuments();
		for (String doc : documents)
		{
			if (doc.equals(uri.getPath()))
			{
				return true;
			}
		}
		return false;
	}

	protected String getLocations(QueryResult result)
	{
		Set<String> set = new HashSet<String>();
		for (String doc : result.getDocuments())
		{
			// HACK Detect when it's a core stub and change the reported name to "Ruby Core"
			if (doc.contains(".metadata")) //$NON-NLS-1$
			{
				doc = "Ruby Core"; //$NON-NLS-1$
			}
			set.add(doc);
		}
		return StringUtil.join(LOCATIONS_DELIMETER, set);
	}

	protected Image getImage(QueryResult result)
	{
		Image image = Activator.getImage(LOCAL_VAR_IMAGE);
		if (result.getWord().startsWith(GLOBAL_NAME_PREFIX))
		{
			image = Activator.getImage(GLOBAL_IMAGE);
		}
		// Must check class var before instance because of prefix!
		else if (isClassVar(result))
		{
			image = Activator.getImage(CLASS_VAR_IMAGE);
		}
		else if (isInstanceVar(result))
		{
			image = Activator.getImage(INSTANCE_VAR_IMAGE);
		}
		else if (!isVariable(result))
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

	protected boolean isVariable(QueryResult result)
	{
		return result.getWord().indexOf(IRubyIndexConstants.SEPARATOR) == -1;
	}

	protected boolean isInstanceVar(QueryResult result)
	{
		return result.getWord().startsWith(INSTANCE_VAR_NAME_PREFIX);
	}

	protected boolean isClassVar(QueryResult result)
	{
		return result.getWord().startsWith(CLASS_VAR_NAME_PREFIX);
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
