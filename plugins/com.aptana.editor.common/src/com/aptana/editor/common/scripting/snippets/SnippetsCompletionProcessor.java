/**
 * 
 */
package com.aptana.editor.common.scripting.snippets;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.DocumentContentTypeManager;
import com.aptana.editor.common.QualifiedContentType;
import com.aptana.editor.common.tmp.ContentTypeTranslation;
import com.aptana.scripting.model.AndFilter;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ScopeFilter;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.model.HasTriggerFilter;

public class SnippetsCompletionProcessor extends TemplateCompletionProcessor
{

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
			contentTypeString = getContentTypeAtOffset(document, region.getOffset() + region.getLength());
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}
		return new SnippetTemplateContextType(contentTypeString);
	}

	private String getContentTypeAtOffset(IDocument document, int offset) throws BadLocationException
	{
		QualifiedContentType contentType = DocumentContentTypeManager.getInstance().getContentType(document, offset);
		if (contentType != null)
		{
			return ContentTypeTranslation.getDefault().translate(contentType).toString();
		}
		return document.getContentType(offset);
	}

	@Override
	protected Image getImage(Template template)
	{
		if (template instanceof CommandTemplate)
		{
			return CommonEditorPlugin.getDefault().getImageFromImageRegistry(CommonEditorPlugin.COMMAND);
		}
		return CommonEditorPlugin.getDefault().getImageFromImageRegistry(CommonEditorPlugin.SNIPPET);
	}

	@Override
	protected Template[] getTemplates(String contextTypeId)
	{
		List<Template> templatesList = new LinkedList<Template>();
		AndFilter filter = new AndFilter(new ScopeFilter(contextTypeId), new HasTriggerFilter());
		CommandElement[] commandsFromScope = BundleManager.getInstance().getCommands(filter);
		for (CommandElement commandElement : commandsFromScope) {
			String[] triggers = commandElement.getTriggers();
			if (triggers != null) {
				for (String trigger : triggers) {
					if (commandElement instanceof SnippetElement) {
						templatesList.add(new SnippetTemplate((SnippetElement)commandElement, trigger, contextTypeId));
					} else {
						templatesList.add (new CommandTemplate(commandElement, trigger, contextTypeId));
					}
				}
			}
		}
		Collections.sort(templatesList, new Comparator<Template>()
		{
			@Override
			public int compare(Template template1, Template template2)
			{
				return template1.getDescription().compareTo(template2.getDescription());
			}
		});
		return templatesList.toArray(new Template[0]);
	}

	private static final String SPACES = "\\s*+"; //$NON-NLS-1$

	// Transform Textmate variable syntax into Eclipse variable syntax
	static String processExpansion(String expansion)
	{
		// cursor $ or ${0} to ${cursor}
		expansion = expansion.replaceAll(Pattern.quote("$0"), Matcher.quoteReplacement("${cursor}")); //$NON-NLS-1$  //$NON-NLS-2$
		expansion = expansion.replaceAll(Pattern.quote("${0}"), Matcher.quoteReplacement("${cursor}")); //$NON-NLS-1$  //$NON-NLS-2$
		// Turn \$ into $$
		expansion = expansion.replaceAll(Pattern.quote("\\$"), Matcher.quoteReplacement("$$")); //$NON-NLS-1$  //$NON-NLS-2$
		// turn empty tab stops into something eclipse can handle
		expansion = expansion.replaceAll("\\$(\\d)", "\\${value$1:$1('')}"); //$NON-NLS-1$  //$NON-NLS-2$
		// transform ${n:value1/value2/value3} to ${choices:n('value1',value2','value3)} where n is a digit
		Pattern p = Pattern.compile("\\$\\{" //$NON-NLS-1$
				+ SPACES + "(\\d)" //$NON-NLS-1$
				+ SPACES + ":" //$NON-NLS-1$
				+ SPACES + "(([^\\}/]+/)+([^\\}/]+))" //$NON-NLS-1$
				+ "\\}" //$NON-NLS-1$
		);
		Matcher m = p.matcher(expansion);
		while (m.find())
		{
			String values = m.group(2);
			StringBuilder replacement = new StringBuilder("${choices"); //$NON-NLS-1$
			replacement.append(m.group(1));
			replacement.append(':');
			replacement.append(m.group(1));
			replacement.append("("); //$NON-NLS-1$
			StringTokenizer tokenizer = new StringTokenizer(values, "/"); //$NON-NLS-1$
			while (tokenizer.hasMoreTokens())
			{
				replacement.append("'"); //$NON-NLS-1$
				replacement.append(tokenizer.nextToken());
				replacement.append("',"); //$NON-NLS-1$
			}
			replacement.deleteCharAt(replacement.length() - 1);
			replacement.append(")}"); //$NON-NLS-1$
			expansion = expansion.substring(0, m.start()) + replacement.toString() + expansion.substring(m.end());
			m = p.matcher(expansion);
		}
		// Wrap values as an arg so we handle the invalid characters properly and give them identifiers based on tab stop so that equal values with different tab stops produce unique ids.
		p = Pattern.compile("\\$\\{" //$NON-NLS-1$
				+ SPACES + "(\\d)" //$NON-NLS-1$
				+ SPACES + ":" //$NON-NLS-1$
				+ SPACES + "([^\\}]+)" //$NON-NLS-1$
				+ "\\}" //$NON-NLS-1$
		);
		m = p.matcher(expansion);
		while (m.find())
		{
			String value = m.group(2);
			StringBuilder replacement = new StringBuilder("${value"); //$NON-NLS-1$
			replacement.append(m.group(1));
			replacement.append(':');
			replacement.append(m.group(1));
			replacement.append("('"); //$NON-NLS-1$
			replacement.append(value);
			replacement.append("')}"); //$NON-NLS-1$
			expansion = expansion.substring(0, m.start()) + replacement.toString() + expansion.substring(m.end());
			m = p.matcher(expansion);
		}
		return expansion;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		ICompletionProposal[] completionProposals = super.computeCompletionProposals(viewer, offset);

		// We now check if there is only one proposal that
		// matches exactly with the prefix the user has typed
		String extractPrefix = extractPrefix(viewer, offset);
		int exactPrefixMatches = 0;
		int exactPrefixMatchIndex = -1;
		for (int i = 0; i < completionProposals.length; i++)
		{
			SnippetTemplateProposal snippetTemplateProposal = (SnippetTemplateProposal) completionProposals[i];
			Template template = snippetTemplateProposal.getTemplateSuper();
			if (template instanceof SnippetTemplate)
			{
				SnippetTemplate snippetTemplate = (SnippetTemplate) template;
				if (snippetTemplate.exactMatches(extractPrefix))
				{
					exactPrefixMatches++;
					exactPrefixMatchIndex = i;
				}
			}
			else if (template instanceof CommandTemplate)
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
			return new ICompletionProposal[] {completionProposals[exactPrefixMatchIndex]};
		}

		for (int i = 0; i < completionProposals.length; i++)
		{
			if (completionProposals[i] instanceof SnippetTemplateProposal)
			{
				SnippetTemplateProposal snippetTemplateProposal = (SnippetTemplateProposal) completionProposals[i];
				snippetTemplateProposal.setTemplateProposals(completionProposals);
				Template template = snippetTemplateProposal.getTemplateSuper();
				StyledString styledString = new StyledString(
						String.format("%1$-20.20s", template.getDescription()), FIXED_WIDTH_STYLER); //$NON-NLS-1$

				styledString.append(new StyledString(
						String.format("%1$10.10s ", template.getName() + "\u21E5"), FIXED_WIDTH_STYLER)); //$NON-NLS-1$ //$NON-NLS-2$

				if (i < 9)
				{
					char triggerChar = (char) ('1' + i);
					snippetTemplateProposal.setTriggerChar(triggerChar);
					styledString.append(new StyledString(String.valueOf(triggerChar), FIXED_WIDTH_STYLER));
				}
				snippetTemplateProposal.setStyledDisplayString(styledString);
			}
		}
		return completionProposals;
	}

	@Override
	protected ICompletionProposal createProposal(Template template, TemplateContext context, IRegion region,
			int relevance)
	{
		if (template instanceof CommandTemplate)
		{
			return new CommandProposal(template, context, region, getImage(template), relevance);
		}
		return new SnippetTemplateProposal(template, context, region, getImage(template), relevance);
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
					break;
				i--;
			}
			return document.get(i, offset - i);
		}
		catch (BadLocationException e)
		{
			return ""; //$NON-NLS-1$
		}
	}

	public static void insertAsTemplate(ITextViewer textViewer, final int caretOffset, String templateText)
	{
		IRegion region = new IRegion()
		{
			public int getOffset()
			{
				return caretOffset;
			}

			public int getLength()
			{
				return 0;
			}
		};
		insertAsTemplate(textViewer, region, templateText);
	}
	
	public static void insertAsTemplate(ITextViewer textViewer, final IRegion region, String templateText)
	{
		SnippetsCompletionProcessor snippetsCompletionProcessor = new SnippetsCompletionProcessor();
		Template template = new SnippetTemplate("", //$NON-NLS-1$
				"", //$NON-NLS-1$
				"", //$NON-NLS-1$
				SnippetsCompletionProcessor.processExpansion(templateText));
		TemplateContext context = snippetsCompletionProcessor.createContext(textViewer, region);
		SnippetTemplateProposal completionProposal = (SnippetTemplateProposal) snippetsCompletionProcessor
				.createProposal(template, context, region, 0);
		completionProposal.setTemplateProposals(new ICompletionProposal[] { completionProposal });
		completionProposal.apply(textViewer, '0', SWT.NONE, region.getOffset());
	}

	private static class CustomStyler extends Styler
	{
		private static String fForegroundColorName;

		CustomStyler()
		{
			this(null);
		}

		CustomStyler(String foregroundColorName)
		{
			fForegroundColorName = foregroundColorName;
		}

		public void applyStyles(TextStyle textStyle)
		{
			if (fForegroundColorName != null)
			{
				textStyle.foreground = JFaceResources.getColorRegistry().get(fForegroundColorName);
			}

			textStyle.font = JFaceResources.getFontRegistry().get("org.eclipse.jface.textfont"); //$NON-NLS-1$
		}
	}

	private static Styler FIXED_WIDTH_STYLER = new CustomStyler();
}