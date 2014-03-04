/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.contentassist.ICommonCompletionProposal;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.OutputType;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.model.TriggerType;
import com.aptana.scripting.model.filters.AndFilter;
import com.aptana.scripting.model.filters.HasTriggerFilter;
import com.aptana.scripting.model.filters.ScopeFilter;

public class SnippetsCompletionProcessor extends TemplateCompletionProcessor
{

	private static class TextFontStyler extends Styler
	{

		private Font maxHeightTextFont;

		private TextFontStyler()
		{
			Font textFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
			FontData[] textFontData = textFont.getFontData();
			// limit the height of the font
			if (textFontData[0].getHeight() > SnippetsContentAssistant.MAX_HEIGHT)
			{
				maxHeightTextFont = new Font(textFont.getDevice(), textFontData[0].getName(),
						SnippetsContentAssistant.MAX_HEIGHT, textFontData[0].getStyle());
			}
		}

		@Override
		public void applyStyles(TextStyle textStyle)
		{
			if (maxHeightTextFont != null)
			{
				// Use font with limited max height
				textStyle.font = maxHeightTextFont;
			}
			else
			{
				textStyle.font = JFaceResources.getTextFont();
			}
		}

		private void dispose()
		{
			// if we allocated a height limited font - dispose it
			if (maxHeightTextFont != null)
			{
				if (!maxHeightTextFont.isDisposed())
				{
					maxHeightTextFont.dispose();
				}
				maxHeightTextFont = null;
			}
		}

	}

	// Styler used to style the proposals
	private TextFontStyler textFontStyler;

	public SnippetsCompletionProcessor()
	{
	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region)
	{
		String contentTypeString = ""; //$NON-NLS-1$
		try
		{
			contentTypeString = getDocumentScopeManager().getScopeAtOffset(viewer,
					region.getOffset() + region.getLength());
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		return new SnippetTemplateContextType(contentTypeString);
	}

	protected IDocumentScopeManager getDocumentScopeManager()
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager();
	}

	@Override
	protected Image getImage(Template template)
	{
		if (template instanceof SnippetTemplate
				|| (template instanceof CommandTemplate && OutputType.INSERT_AS_SNIPPET.getName().equals(
						((CommandTemplate) template).getCommandElement().getOutputType())))
		{
			return CommonEditorPlugin.getDefault().getImageFromImageRegistry(CommonEditorPlugin.SNIPPET);
		}
		return CommonEditorPlugin.getDefault().getImageFromImageRegistry(CommonEditorPlugin.COMMAND);
	}

	@Override
	protected Template[] getTemplates(String contextTypeId)
	{
		List<Template> templatesList = new LinkedList<Template>();
		AndFilter filter = new AndFilter(new ScopeFilter(contextTypeId), new HasTriggerFilter());
		List<CommandElement> commandsFromScope = BundleManager.getInstance().getExecutableCommands(filter);
		for (CommandElement commandElement : commandsFromScope)
		{
			String[] triggers = commandElement.getTriggerTypeValues(TriggerType.PREFIX);
			if (triggers != null)
			{
				for (String trigger : triggers)
				{
					if (commandElement instanceof SnippetElement)
					{
						templatesList.add(new SnippetTemplate((SnippetElement) commandElement, trigger, contextTypeId));
					}
					else
					{
						templatesList.add(new CommandTemplate(commandElement, trigger, contextTypeId));
					}
				}
			}
		}
		Collections.sort(templatesList, new Comparator<Template>()
		{
			public int compare(Template template1, Template template2)
			{
				return template1.getName().compareTo(template2.getName());
			}
		});
		return templatesList.toArray(new Template[0]);
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		ICompletionProposal[] completionProposals = super.computeCompletionProposals(viewer, offset);

		for (int i = 0; i < completionProposals.length; i++)
		{
			if (completionProposals[i] instanceof SnippetTemplateProposal)
			{
				SnippetTemplateProposal snippetTemplateProposal = (SnippetTemplateProposal) completionProposals[i];
				ICompletionProposal[] similarProposals = getTemplatesWithSameName(snippetTemplateProposal,
						completionProposals);
				snippetTemplateProposal.setTemplateProposals(similarProposals);
				snippetTemplateProposal.setStyler(getStyler());
			}
		}

		String prefix = extractPrefix(viewer, offset);
		setSelectedProposal(prefix, completionProposals);
		return completionProposals;
	}

	/**
	 * We need to only group the templates with the same name. Otherwise, we might end up auto inserting the snippet for
	 * the wrong prefix.
	 * 
	 * @param srcTemplate
	 * @param completionProposals
	 * @return
	 */
	private ICompletionProposal[] getTemplatesWithSameName(SnippetTemplateProposal srcTemplate,
			ICompletionProposal[] completionProposals)
	{
		int i = 0;
		ArrayList<ICompletionProposal> result = new ArrayList<ICompletionProposal>(completionProposals.length);
		for (ICompletionProposal proposal : completionProposals)
		{
			if (proposal instanceof SnippetTemplateProposal)
			{
				SnippetTemplateProposal snippetProposal = (SnippetTemplateProposal) proposal;
				if (snippetProposal.getTemplateSuper().getName().equals(srcTemplate.getTemplateSuper().getName()))
				{
					result.add(proposal);
					if (i < 9)
					{
						snippetProposal.setTriggerChar((char) ('1' + i));
					}
					i++;
				}
			}
			else
			{
				result.add(proposal);
			}
		}
		result.trimToSize();
		return CollectionsUtil.toArray(result);
	}

	/**
	 * setSelectedProposal
	 * 
	 * @param prefix
	 * @param proposals
	 */
	public void setSelectedProposal(String prefix, ICompletionProposal[] proposals)
	{
		if (prefix == null || prefix.equals(StringUtil.EMPTY) || proposals == null)
		{
			return;
		}

		for (ICompletionProposal proposal : proposals)
		{
			String displayString = proposal.getDisplayString();
			if (proposal instanceof SnippetTemplateProposal)
			{
				displayString = ((SnippetTemplateProposal) proposal).getActivationString();
			}
			int comparison = displayString.compareToIgnoreCase(prefix);

			if (comparison >= 0)
			{
				if (displayString.toLowerCase().startsWith(prefix.toLowerCase()))
				{
					if (displayString.startsWith(prefix))
					{
						((ICommonCompletionProposal) proposal).setRelevance(ICommonCompletionProposal.RELEVANCE_HIGH);
					}
					else
					{
						((ICommonCompletionProposal) proposal).setRelevance(ICommonCompletionProposal.RELEVANCE_MEDIUM);
					}
				}
			}
		}
	}

	@Override
	protected ICompletionProposal createProposal(Template template, TemplateContext context, IRegion region,
			int relevance)
	{
		if (template instanceof SnippetTemplate)
		{
			return new SnippetTemplateProposal(template, context, region, getImage(template), relevance);
		}
		return new CommandProposal(template, context, region, getImage(template), relevance);
	}

	@Override
	protected TemplateContext createContext(ITextViewer viewer, IRegion region)
	{
		TemplateContextType contextType = getContextType(viewer, region);
		if (contextType != null)
		{
			IDocument document = viewer.getDocument();
			return new DocumentSnippetTemplateContext(contextType, document, region.getOffset(), region.getLength());
		}
		return null;
	}

	// Allow any non-whitespace as a prefix.
	protected String extractPrefix(ITextViewer viewer, int offset)
	{
		return extractPrefixFromDocument(viewer.getDocument(), offset);
	}

	/**
	 * Walk backwards through the document to find the first whitespace character.
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	static String extractPrefixFromDocument(IDocument document, int offset)
	{
		if (offset > document.getLength())
			return StringUtil.EMPTY;
		int i = offset;
		try
		{
			while (i > 0)
			{
				char ch = document.getChar(i - 1);
				if (Character.isWhitespace(ch))
				{
					break;
				}
				i--;
			}
			return document.get(i, offset - i);
		}
		catch (BadLocationException e)
		{
			return StringUtil.EMPTY;
		}
	}

	public static void insertAsTemplate(ITextViewer textViewer, final IRegion region, String templateText,
			CommandElement commandElement)
	{
		SnippetsCompletionProcessor snippetsCompletionProcessor = new SnippetsCompletionProcessor();
		Template template = new SnippetTemplate(commandElement, templateText);
		TemplateContext context = snippetsCompletionProcessor.createContext(textViewer, region);
		SnippetTemplateProposal completionProposal = (SnippetTemplateProposal) snippetsCompletionProcessor
				.createProposal(template, context, region, 0);
		completionProposal.setTemplateProposals(new ICompletionProposal[] { completionProposal });
		completionProposal.apply(textViewer, '0', SWT.NONE, region.getOffset());

		Point selection = completionProposal.getSelection(textViewer.getDocument());
		if (selection != null)
		{
			textViewer.setSelectedRange(selection.x, selection.y);
			textViewer.revealRange(selection.x, selection.y);
		}
	}

	private Styler getStyler()
	{
		if (textFontStyler == null)
		{
			textFontStyler = new TextFontStyler();
		}
		return textFontStyler;
	}

	void possibleCompletionsClosed()
	{
		if (textFontStyler != null)
		{
			textFontStyler.dispose();
			textFontStyler = null;
		}
	}

	/**
	 * Walks forward through the prefix until it hits something besides a letter or a number, and returns the remaining
	 * substring. Note that substring may still have non letter-or-digits in it later on.
	 * 
	 * @param prefix
	 * @return
	 */
	static String narrowPrefix(String prefix)
	{
		for (int i = 0; i < prefix.length(); i++)
		{
			char ch = prefix.charAt(i);
			if (!Character.isLetterOrDigit(ch))
			{
				return prefix.substring(i + 1);
			}
		}
		return StringUtil.EMPTY;
	}

}