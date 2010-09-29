/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

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

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.OutputType;
import com.aptana.scripting.model.SnippetElement;
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
		IDocument document = viewer.getDocument();
		try
		{
			contentTypeString = getDocumentScopeManager().getScopeAtOffset(document,
					region.getOffset() + region.getLength());
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
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
		CommandElement[] commandsFromScope = BundleManager.getInstance().getCommands(filter);
		for (CommandElement commandElement : commandsFromScope)
		{
			String[] triggers = commandElement.getTriggers();
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
				return template1.getDescription().compareTo(template2.getDescription());
			}
		});
		return templatesList.toArray(new Template[0]);
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		ICompletionProposal[] completionProposals = super.computeCompletionProposals(viewer, offset);

		// FIXME We need to be smarter about prefix. If full prefix doesn't match, chop off at non-letter/digit portions
		// from beginning until we have a match!
		// We now check if there is only one proposal that
		// matches exactly with the prefix the user has typed
		String extractPrefix = extractPrefix(viewer, offset);
		int exactPrefixMatches = 0;
		int exactPrefixMatchIndex = -1;
		for (int i = 0; i < completionProposals.length; i++)
		{
			SnippetTemplateProposal snippetTemplateProposal = (SnippetTemplateProposal) completionProposals[i];
			Template template = snippetTemplateProposal.getTemplateSuper();
			if (template instanceof CommandTemplate)
			{
				CommandTemplate commandTemplate = (CommandTemplate) template;
				if (commandTemplate.exactMatches(extractPrefix))
				{
					exactPrefixMatches++;
					exactPrefixMatchIndex = i;
				}
			}
		}

		// There is only one proposal that matches the prefix exactly
		// So we just return it
		if (exactPrefixMatches == 1)
		{
			return new ICompletionProposal[] { completionProposals[exactPrefixMatchIndex] };
		}

		for (int i = 0; i < completionProposals.length; i++)
		{
			if (completionProposals[i] instanceof SnippetTemplateProposal)
			{
				SnippetTemplateProposal snippetTemplateProposal = (SnippetTemplateProposal) completionProposals[i];
				snippetTemplateProposal.setTemplateProposals(completionProposals);
				snippetTemplateProposal.setStyler(getStyler());
				if (i < 9)
				{
					snippetTemplateProposal.setTriggerChar((char) ('1' + i));
				}
			}
		}
		return completionProposals;
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

	static String extractPrefixFromDocument(IDocument document, int offset)
	{
		if (offset > document.getLength())
			return ""; //$NON-NLS-1$
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
			return ""; //$NON-NLS-1$
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
		return ""; //$NON-NLS-1$
	}

}