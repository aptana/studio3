package com.aptana.editor.common;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubySymbol;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.ICommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.filters.ScopeFilter;

public class CommonContentAssistProcessor implements IContentAssistProcessor, ICommonContentAssistProcessor
{
	/**
	 * Default image to use for ruble-contributed proposals (that don't override image)
	 */
	private static final String DEFAULT_IMAGE = "icons/proposal.png"; //$NON-NLS-1$

	/**
	 * Strings used in hash for content assist coming from Rubles
	 */
	private static final String INSERT = "insert"; //$NON-NLS-1$
	private static final String DISPLAY = "display"; //$NON-NLS-1$
	private static final String IMAGE = "image"; //$NON-NLS-1$
	private static final String TOOL_TIP = "tool_tip"; //$NON-NLS-1$

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
	protected void addCompletionProposalsForCategory(ITextViewer viewer, int offset, Index index,
			List<ICompletionProposal> completionProposals, String category)
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
					String info = category + " : " + text; //$NON-NLS-1$

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
	 * @see
	 * com.aptana.editor.common.ICommonContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer
	 * , int, char, boolean)
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		List<ICompletionProposal> proposals = addRubleProposals(viewer, offset);
		ICompletionProposal[] others = this.doComputeCompletionProposals(viewer, offset, activationChar, autoActivated);
		if (proposals == null || proposals.isEmpty())
		{
			return others;
		}

		if (others == null || others.length == 0)
		{
			// Pre-select the first ruble-contributed proposal
			ICompletionProposal proposal = proposals.get(0);
			if (proposal instanceof CommonCompletionProposal)
			{
				((CommonCompletionProposal) proposal).setIsDefaultSelection(true);
			}
			return proposals.toArray(new ICompletionProposal[proposals.size()]);
		}

		// Combine the two, leave selection as is
		ICompletionProposal[] combined = new ICompletionProposal[proposals.size() + others.length];
		proposals.toArray(combined);
		System.arraycopy(others, 0, combined, proposals.size(), others.length);
		return combined;
	}

	/**
	 * This hooks our Ruble scripting up to Content Assist, allowing them to contribute possible proposals. Experimental
	 * right now as the way to return results is... interesting.
	 * 
	 * @param viewer
	 * @param offset
	 * @return
	 */
	protected List<ICompletionProposal> addRubleProposals(ITextViewer viewer, int offset)
	{
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		try
		{
			String scope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(viewer.getDocument(), offset);
			CommandElement[] commands = BundleManager.getInstance().getContentAssists(new ScopeFilter(scope));
			if (commands != null && commands.length > 0)
			{
				Ruby ruby = Ruby.newInstance();

				for (CommandElement ce : commands)
				{
					CommandResult result = ce.execute();

					if (!result.executedSuccessfully())
					{
						continue;
					}
					String output = result.getOutputString();
					if (output == null || output.trim().length() == 0)
					{
						continue;
					}
					// This assumes that the command is returning an array that is output as a
					// string I can eval (via inspect)!
					RubyArray object = (RubyArray) ruby.evalScriptlet(output);
					for (IRubyObject element : object.toJavaArray())
					{
						String name;
						String displayName;
						String description = null;
						int length;
						IContextInformation contextInfo = null;
						int replaceLength = 0;
						Image image = CommonEditorPlugin.getImage(DEFAULT_IMAGE);
						if (element instanceof RubyHash)
						{
							RubyHash hash = (RubyHash) element;
							// TODO Move symbol creation to top and re-use them?
							if (hash.containsKey(RubySymbol.newSymbol(ruby, INSERT)))
							{
								continue;
							}
							name = hash.get(RubySymbol.newSymbol(ruby, INSERT)).toString();
							length = name.length();
							if (hash.containsKey(RubySymbol.newSymbol(ruby, DISPLAY)))
							{
								displayName = hash.get(RubySymbol.newSymbol(ruby, DISPLAY)).toString();
							}
							else
							{
								displayName = name;
							}
							if (hash.containsKey(RubySymbol.newSymbol(ruby, IMAGE)))
							{
								String imagePath = hash.get(RubySymbol.newSymbol(ruby, IMAGE)).toString();
								// Turn into image!
								ImageRegistry reg = CommonEditorPlugin.getDefault().getImageRegistry();
								Image fromReg = reg.get(imagePath);
								if (fromReg == null)
								{
									try
									{
										ImageDescriptor desc = ImageDescriptor.createFromURL(new File(imagePath)
												.toURI().toURL());
										reg.put(imagePath, desc);
										image = reg.get(imagePath);
									}
									catch (MalformedURLException e)
									{
										CommonEditorPlugin.logError(e);
									}
								}
								else
								{
									image = fromReg;
								}
							}
							if (hash.containsKey(RubySymbol.newSymbol(ruby, TOOL_TIP)))
							{
								description = hash.get(RubySymbol.newSymbol(ruby, TOOL_TIP)).toString();
							}
							// TODO Allow hash to set offset to insert and replace length?
						}
						else
						{
							// Array of strings
							name = element.toString();
							displayName = name;
							length = name.length();
						}
						// build proposal
						CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, replaceLength,
								length, image, displayName, contextInfo, description);

						// add it to the list
						proposals.add(proposal);
					}
				}
			}
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e.getMessage(), e);
		}
		return proposals;
	}

	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		// NOTE: This is the default implementation. Specific language CA processors
		// should override this method
		return computeCompletionProposals(viewer, offset);
	}

	/**
	 * computeCompletionProposalsUsingIndex
	 * 
	 * @param viewer
	 * @param offset
	 * @param index
	 * @param completionProposals
	 */
	protected void computeCompletionProposalsUsingIndex(ITextViewer viewer, int offset, Index index,
			List<ICompletionProposal> completionProposals)
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

	/**
	 * getAST
	 * 
	 * @return
	 */
	protected IParseNode getAST()
	{
		return editor.getFileService().getParseResult();
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
	 * getFilename
	 * 
	 * @return
	 */
	protected String getFilename()
	{
		return editor.getEditorInput().getName();
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	protected Index getIndex()
	{
		if (editor == null)
		{
			return null;
		}
		IEditorInput editorInput = editor.getEditorInput();
		Index result = null;
		if (editorInput instanceof IFileEditorInput)
		{
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			IFile file = fileEditorInput.getFile();
			IProject project = file.getProject();
			result = IndexManager.getInstance().getIndex(project.getLocationURI());
		}
		else if (editorInput instanceof IURIEditorInput)
		{
			IURIEditorInput uriEditorInput = (IURIEditorInput) editorInput;
			URI uri = uriEditorInput.getURI();
			// FIXME This file may be a child, we need to check to see if there's an index with a parent URI.
			result = IndexManager.getInstance().getIndex(uri);
		}

		return result;
	}

	protected URI getURI()
	{
		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput instanceof IURIEditorInput)
		{
			IURIEditorInput fileEditorInput = (IURIEditorInput) editorInput;
			return fileEditorInput.getURI();
		}
		return null;
	}

	/**
	 * getParseState
	 * 
	 * @return
	 */
	protected IParseState getParseState()
	{
		return editor.getFileService().getParseState();
	}

	/**
	 * getAllUserAgentIcons
	 * 
	 * @return
	 */
	protected Image[] getAllUserAgentIcons()
	{
		UserAgentManager manager = UserAgentManager.getInstance();
		String[] userAgents = manager.getActiveUserAgentIDs();
		Image[] userAgentIcons = manager.getUserAgentImages(userAgents);

		return userAgentIcons;
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
