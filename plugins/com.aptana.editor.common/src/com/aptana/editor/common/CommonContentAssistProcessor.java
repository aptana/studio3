/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.PerformanceStats;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Image;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubySymbol;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.CompletionProposalComparator;
import com.aptana.editor.common.contentassist.ICommonCompletionProposal;
import com.aptana.editor.common.contentassist.ICommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.IPreferenceConstants;
import com.aptana.editor.common.contentassist.UserAgentFilterType;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.editor.common.scripting.snippets.SnippetsCompletionProcessor;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.ContentAssistElement;
import com.aptana.scripting.model.filters.ScopeFilter;
import com.aptana.ui.util.UIUtils;

public class CommonContentAssistProcessor implements IContentAssistProcessor, ICommonContentAssistProcessor,
		IPreferenceChangeListener
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
	private static final String LOCATION = "location"; //$NON-NLS-1$

	private static final String PERFORMANCE_EVENT_PREFIX = CommonEditorPlugin.PLUGIN_ID + "/perf/content_assist"; //$NON-NLS-1$
	private static final String RUBLE_PERF = PERFORMANCE_EVENT_PREFIX + "/rubles"; //$NON-NLS-1$
	private static final String SNIPPET_PERF = PERFORMANCE_EVENT_PREFIX + "/snippets"; //$NON-NLS-1$

	protected static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];

	private char[] _completionProposalChars = null;
	private char[] _contextInformationChars = null;
	private char[] _proposalTriggerChars = null;

	private UserAgentFilterType _filterType = UserAgentFilterType.NO_FILTER;

	protected final AbstractThemeableEditor editor;

	/**
	 * IndexContentAssistProcessor
	 * 
	 * @param editor
	 */
	public CommonContentAssistProcessor(AbstractThemeableEditor editor)
	{
		this.editor = editor;

		_completionProposalChars = retrieveCAPreference(IPreferenceConstants.COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS);
		_contextInformationChars = retrieveCAPreference(IPreferenceConstants.CONTEXT_INFORMATION_ACTIVATION_CHARACTERS);
		_proposalTriggerChars = retrieveCAPreference(IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS);

		IPreferenceStore commonPreferences = CommonEditorPlugin.getDefault().getPreferenceStore();
		String filterTypeString = commonPreferences
				.getString(IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE);
		_filterType = UserAgentFilterType.get(filterTypeString);
		commonPreferences.addPropertyChangeListener(new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE.equals(event.getProperty()))
				{
					_filterType = UserAgentFilterType.get(event.getNewValue().toString());
				}
			}
		});

		if (getPreferenceNodeQualifier() != null)
		{
			EclipseUtil.instanceScope().getNode(getPreferenceNodeQualifier()).addPreferenceChangeListener(this);
		}
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

	/**
	 * Do the dirty work of executing the content assist element, and then inserting it's resulting proposals. Executed
	 * once per contributed content assist. This allows subclasses to manipulate individual elements or skip them if
	 * necessary.
	 * 
	 * @param viewer
	 * @param offset
	 * @param ruby
	 *            shared Ruby interpreter we're launching the content assist inside.
	 * @param ce
	 *            The content assist element contributed by a ruble.
	 * @return
	 */
	protected Collection<? extends ICompletionProposal> addRubleCAProposals(ITextViewer viewer, int offset, Ruby ruby,
			ContentAssistElement ce)
	{
		final boolean recordPerf = PerformanceStats.isEnabled(RUBLE_PERF);
		PerformanceStats stats = null;

		CommandContext context = ce.createCommandContext();
		context.setInputStream(new ByteArrayInputStream(viewer.getDocument().get().getBytes()));
		if (recordPerf)
		{
			stats = PerformanceStats.getStats(RUBLE_PERF, ce.getDisplayName());
			stats.startRun();
		}
		CommandResult result = ce.execute(context);
		if (recordPerf)
		{
			stats.endRun();
		}
		if (result == null || !result.executedSuccessfully())
		{
			return Collections.emptyList();
		}
		String output = result.getOutputString();
		if (StringUtil.isEmpty(output))
		{
			return Collections.emptyList();
		}
		// This assumes that the command is returning an array that is output as a
		// string I can eval (via inspect)!
		RubyArray object = (RubyArray) ruby.evalScriptlet(output);
		RubySymbol insertSymbol = RubySymbol.newSymbol(ruby, INSERT);
		RubySymbol displaySymbol = RubySymbol.newSymbol(ruby, DISPLAY);
		RubySymbol imageSymbol = RubySymbol.newSymbol(ruby, IMAGE);
		RubySymbol tooltipSymbol = RubySymbol.newSymbol(ruby, TOOL_TIP);
		RubySymbol locationSymbol = RubySymbol.newSymbol(ruby, LOCATION);
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		for (IRubyObject element : object.toJavaArray())
		{
			String name;
			String displayName;
			String description = null;
			int length;
			String location = null;
			IContextInformation contextInfo = null;
			int replaceLength = 0;
			Image image = UIUtils.getImage(CommonEditorPlugin.getDefault(), DEFAULT_IMAGE);
			if (element instanceof RubyHash)
			{
				Map<?, ?> hash = (RubyHash) element;
				if (!hash.containsKey(insertSymbol))
				{
					continue;
				}
				name = hash.get(insertSymbol).toString();
				length = name.length();
				if (hash.containsKey(displaySymbol))
				{
					displayName = hash.get(displaySymbol).toString();
				}
				else
				{
					displayName = name;
				}
				if (hash.containsKey(locationSymbol))
				{
					location = hash.get(locationSymbol).toString();
				}
				if (hash.containsKey(imageSymbol))
				{
					String imagePath = hash.get(imageSymbol).toString();
					// Turn into image!
					ImageRegistry reg = CommonEditorPlugin.getDefault().getImageRegistry();
					Image fromReg = reg.get(imagePath);
					if (fromReg == null)
					{
						URL imageURL = null;
						try
						{
							imageURL = new URL(imagePath);
						}
						catch (MalformedURLException e)
						{
							try
							{
								imageURL = new File(imagePath).toURI().toURL();
							}
							catch (MalformedURLException e1)
							{
								IdeLog.logError(CommonEditorPlugin.getDefault(), e1);
							}
						}
						if (imageURL != null)
						{
							ImageDescriptor desc = ImageDescriptor.createFromURL(imageURL);
							reg.put(imagePath, desc);
							image = reg.get(imagePath);
						}
					}
					else
					{
						image = fromReg;
					}
				}
				if (hash.containsKey(tooltipSymbol))
				{
					description = hash.get(tooltipSymbol).toString();
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
			CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, replaceLength, length,
					image, displayName, contextInfo, description);
			if (location != null)
			{
				proposal.setFileLocation(location);
			}
			// add it to the list
			proposals.add(proposal);
		}
		return proposals;
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
		ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		try
		{
			String scope = getDocumentScopeManager().getScopeAtOffset(viewer, offset);
			List<ContentAssistElement> commands = getBundleManager().getContentAssists(new ScopeFilter(scope));
			if (!CollectionsUtil.isEmpty(commands))
			{
				Ruby ruby = Ruby.newInstance();
				for (ContentAssistElement ce : commands)
				{
					proposals.addAll(addRubleCAProposals(viewer, offset, ruby, ce));
				}
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}

		proposals.trimToSize();
		return proposals;
	}

	/**
	 * Calls the SnippetsCompletionProcessor to contribute any relevant snippets for the offset.
	 * 
	 * @param viewer
	 * @param offset
	 * @return
	 */
	protected Collection<ICompletionProposal> addSnippetProposals(ITextViewer viewer, int offset)
	{
		if (viewer != null && viewer.getSelectionProvider() != null)
		{
			PerformanceStats stats = null;
			if (PerformanceStats.isEnabled(SNIPPET_PERF))
			{
				stats = PerformanceStats.getStats(SNIPPET_PERF, "SnippetsCompletionProcessor"); //$NON-NLS-1$
				stats.startRun();
			}
			ICompletionProposal[] snippets = new SnippetsCompletionProcessor().computeCompletionProposals(viewer,
					offset);
			if (stats != null)
			{
				stats.endRun();
			}
			if (snippets == null)
			{
				return Collections.emptyList();
			}
			return Arrays.asList(snippets);
		}
		return Collections.emptyList();

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text
	 * .ITextViewer, int)
	 */
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
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		PerformanceStats stats = null;
		try
		{
			if (PerformanceStats.isEnabled(PERFORMANCE_EVENT_PREFIX))
			{
				stats = PerformanceStats.getStats(PERFORMANCE_EVENT_PREFIX, this);
				stats.startRun();
			}

			Collection<ICompletionProposal> rubleProposals = addRubleProposals(viewer, offset);
			Collection<ICompletionProposal> snippetProposals = addSnippetProposals(viewer, offset);

			Collection<ICompletionProposal> proposals = CollectionsUtil.union(rubleProposals, snippetProposals);

			ICompletionProposal[] others = this.doComputeCompletionProposals(viewer, offset, activationChar,
					autoActivated);

			// create empty array to simplify logic
			if (others == null)
			{
				others = NO_PROPOSALS;
			}

			if (IdeLog.isTraceEnabled(CommonEditorPlugin.getDefault(), IDebugScopes.CONTENT_ASSIST))
			{
				IdeLog.logTrace(CommonEditorPlugin.getDefault(), MessageFormat.format(
						"Generated {0} ruble proposals, {0} snippet proposals, and {0} language proposals", //$NON-NLS-1$
						rubleProposals.size(), snippetProposals.size(), others.length), IDebugScopes.CONTENT_ASSIST);
			}

			// Combine the two
			ICompletionProposal[] combined = new ICompletionProposal[proposals.size() + others.length];
			proposals.toArray(combined);
			System.arraycopy(others, 0, combined, proposals.size(), others.length);

			if (IdeLog.isTraceEnabled(CommonEditorPlugin.getDefault(), IDebugScopes.CONTENT_ASSIST))
			{
				IdeLog.logTrace(CommonEditorPlugin.getDefault(),
						MessageFormat.format("Combined {0} total proposals", combined.length), //$NON-NLS-1$
						IDebugScopes.CONTENT_ASSIST);
			}

			// sort proposals using default mechanism
			sortProposals(combined);

			// selection currently is set to first item in list
			return combined;
		}
		finally
		{
			if (stats != null)
			{
				stats.endRun();
			}
		}
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
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		return null;
	}

	/**
	 * dispose
	 */
	public void dispose()
	{
		if (getPreferenceNodeQualifier() != null)
		{
			EclipseUtil.instanceScope().getNode(getPreferenceNodeQualifier()).removePreferenceChangeListener(this);
		}
	}

	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		// NOTE: This is the default implementation. Specific language CA processors
		// should override this method
		return computeCompletionProposals(viewer, offset);
	}

	/**
	 * getActiveUserAgentIds
	 * 
	 * @return
	 */
	public String[] getActiveUserAgentIds()
	{
		return UserAgentManager.getInstance().getActiveUserAgentIDs(getProject());
	}

	/**
	 * getAllUserAgentIcons
	 * 
	 * @return
	 */
	protected Image[] getAllUserAgentIcons()
	{
		return UserAgentManager.getInstance().getUserAgentImages(getProject());
	}

	/**
	 * getAST
	 * 
	 * @return
	 */
	protected IParseNode getAST()
	{
		return editor.getAST();
	}

	/**
	 * getBundleManager
	 * 
	 * @return
	 */
	protected BundleManager getBundleManager()
	{
		return BundleManager.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return _completionProposalChars;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters()
	{
		return _contextInformationChars;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator()
	{
		return null;
	}

	protected IDocumentScopeManager getDocumentScopeManager()
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage()
	{
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
		return EditorUtil.getIndex(editor);
	}

	/**
	 * Returns the qualifier for the preference service. Gnerally the plugin ID as that's where the relevant preferences
	 * are stored.
	 * 
	 * @return
	 */
	protected String getPreferenceNodeQualifier()
	{
		return null;
	}

	/**
	 * getProject
	 * 
	 * @return
	 */
	protected IProject getProject()
	{
		return EditorUtil.getProject(editor);
	}

	/**
	 * getProjectURI
	 * 
	 * @return
	 */
	protected URI getProjectURI()
	{
		return EditorUtil.getProjectURI(editor);
	}

	/*
	 * return the characters to insert proposals
	 */
	public char[] getProposalTriggerCharacters()
	{
		return _proposalTriggerChars;
	}

	/**
	 * getURI
	 * 
	 * @return
	 */
	protected URI getURI()
	{
		return EditorUtil.getURI(editor);
	}

	/**
	 * isActiveByUserAgent
	 * 
	 * @param userAgents
	 * @return
	 */
	public boolean isActiveByUserAgent(String[] userAgents)
	{
		boolean result = false;

		if (userAgents == null || userAgents.length == 0)
		{
			// NOTE: libraries oftentimes do not tag their types and members with user agent info, so we intentionally
			// do not filter items that have no user agent info
			result = true;
		}
		else
		{
			switch (_filterType)
			{
				case NO_FILTER:
					// no filtering, so all proposals are OK
					result = true;
					break;

				case ONE_OR_MORE:
				{
					// if any of the active user agents are in the specified list, then allow this proposal
					String[] userAgentIds = UserAgentManager.getInstance().getActiveUserAgentIDs(getProject());
					Set<String> activeNameSet = new HashSet<String>(Arrays.asList(userAgentIds));

					for (String id : userAgents)
					{
						if (activeNameSet.contains(id))
						{
							result = true;
							break;
						}
					}

					break;
				}

				case ALL:
				{
					// if all of the active user agents are in the specified list, then allow this proposal
					Set<String> nameSet = new HashSet<String>(Arrays.asList(userAgents));
					String[] activeUserAgentIds = UserAgentManager.getInstance().getActiveUserAgentIDs(getProject());

					result = nameSet.containsAll(Arrays.asList(activeUserAgentIds));

					break;
				}

				default:
					break;
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonContentAssistProcessor#isValidActivationCharacter(char, int)
	 */
	public boolean isValidActivationCharacter(char c, int keyCode)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ICommonContentAssistProcessor#isValidAssistLocation(char, int,
	 * org.eclipse.jface.text.IDocument, int)
	 */
	public boolean isValidAutoActivationLocation(char c, int keyCode, IDocument document, int offset)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ICommonContentAssistProcessor#isValidIdentifier(char, int)
	 */
	public boolean isValidIdentifier(char c, int keyCode)
	{
		return false;
	}

	/**
	 * Respond to preference change events
	 */
	public void preferenceChange(PreferenceChangeEvent event)
	{
		String key = event.getKey();

		if (IPreferenceConstants.COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS.equals(key))
		{
			_completionProposalChars = retrieveCAPreference(IPreferenceConstants.COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS);
		}
		else if (IPreferenceConstants.CONTEXT_INFORMATION_ACTIVATION_CHARACTERS.equals(key))
		{
			_contextInformationChars = retrieveCAPreference(IPreferenceConstants.CONTEXT_INFORMATION_ACTIVATION_CHARACTERS);
		}
		else if (IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS.equals(key))
		{
			_proposalTriggerChars = retrieveCAPreference(IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS);
		}
	}

	/**
	 * Retrieves a content assist preference and converts it into a char array
	 * 
	 * @param preferenceKey
	 * @return
	 */
	private char[] retrieveCAPreference(String preferenceKey)
	{
		String chars = retrievePreference(preferenceKey);

		return (chars != null) ? chars.toCharArray() : null;
	}

	/**
	 * Retrieves a preference value as a string
	 * 
	 * @param preferenceKey
	 * @return
	 */
	private String retrievePreference(String preferenceKey)
	{
		String qualifier = getPreferenceNodeQualifier();
		if (qualifier == null)
		{
			return null;
		}

		return Platform.getPreferencesService().getString(getPreferenceNodeQualifier(), preferenceKey,
				StringUtil.EMPTY, null);
	}

	/**
	 * setSelectedProposal
	 * 
	 * @param prefix
	 * @param proposals
	 */
	protected void setSelectedProposal(String prefix, ICompletionProposal[] proposals)
	{
		if (StringUtil.isEmpty(prefix) || proposals == null)
		{
			return;
		}

		for (ICompletionProposal proposal : proposals)
		{
			String displayString = proposal.getDisplayString();
			int comparison = displayString.compareToIgnoreCase(prefix);

			if (comparison >= 0)
			{
				if (displayString.toLowerCase().startsWith(prefix.toLowerCase()))
				{
					// Only change relevance if this new value is higher than old!
					ICommonCompletionProposal common = (ICommonCompletionProposal) proposal;
					if (displayString.startsWith(prefix))
					{
						if (common.getRelevance() < ICommonCompletionProposal.RELEVANCE_HIGH)
						{
							common.setRelevance(ICommonCompletionProposal.RELEVANCE_HIGH);
						}
					}
					else
					{
						if (common.getRelevance() < ICommonCompletionProposal.RELEVANCE_MEDIUM)
						{
							common.setRelevance(ICommonCompletionProposal.RELEVANCE_MEDIUM);
						}
					}
				}
			}
		}
	}

	/**
	 * Sorts the completion proposals (by default, by display string)
	 * 
	 * @param proposals
	 */
	protected void sortProposals(ICompletionProposal[] proposals)
	{
		// Sort by relevance first, descending, and then alphabetically, ascending
		Arrays.sort(proposals, CompletionProposalComparator.decending(CompletionProposalComparator.getComparator(
				CompletionProposalComparator.RelevanceSort, CompletionProposalComparator.NameSort)));
	}
}
