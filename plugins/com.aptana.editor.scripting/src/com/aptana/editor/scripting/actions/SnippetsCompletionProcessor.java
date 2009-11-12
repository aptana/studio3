/**
 * 
 */
package com.aptana.editor.scripting.actions;

import java.util.LinkedList;
import java.util.List;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

import com.aptana.editor.scripting.Activator;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.Snippet;

class SnippetsCompletionProcessor extends TemplateCompletionProcessor {

	private static final SnippetTemplateContextType SNIPPET_TEMPLATE_CONTEXT_TYPE = new SnippetTemplateContextType();
	private final ExpandSnippetAction expandSnippet;

	public SnippetsCompletionProcessor(ExpandSnippetAction expandSnippet) {
		this.expandSnippet = expandSnippet;
	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer,
			IRegion region) {
		return SNIPPET_TEMPLATE_CONTEXT_TYPE;
	}

	@Override
	protected Image getImage(Template template) {
		return Activator.getDefault().getImage(Activator.SNIPPET);
	}
	
	@Override
	protected Template[] getTemplates(String contextTypeId) {
		Snippet[] snippetsFromScope = BundleManager.getInstance().getSnippetsFromScope("source.ruby.rails");
		List<Template> templates = new LinkedList<Template>();
		templates.add(new SnippetTemplate("contact",
				"Insert Contact",
				"snippets", 
				  "----------------------------\n"
				+ "First Name : ${firstName:2}\n"
				+ "Last Name  : ${lastName:1}\n"
				+ "Full Name  : Mr./Mrs./Ms. ${firstName}, ${lastName}\n"
				+ "Description: ${0}\n"
				+ "----------------------------\n"
				,true));
		for (Snippet snippet : snippetsFromScope) {
			String expansion = snippet.getExpansion();
			
			templates.add(new SnippetTemplate(
					snippet.getTrigger(),
					snippet.getDisplayName(),
					"snippets", 
					processExpansion(expansion),
					true));
		}
		return templates.toArray(new Template[0]);
	}
	
	private static final String SPACES= "\\s*+"; //$NON-NLS-1$
	
	// Transform Textmate variable syntax into Eclipse variable syntax
	private static String processExpansion(String expansion) {
		// cursor $ or ${0} to ${cursor}
		expansion = expansion.replaceAll(Pattern.quote("$0"), Matcher.quoteReplacement("${cursor}"));
		expansion = expansion.replaceAll(Pattern.quote("${0}"), Matcher.quoteReplacement("${cursor}"));
		
		// transform ${n:default value} to ${default value:n} where n is a digit
		expansion = expansion.replaceAll(
				  "\\$\\{" 
				+ SPACES
				+ "(\\d)"
				+ SPACES
				+ ":"
				+ SPACES
				+ "(\\w+)"
				+ "\\}"
				,"\\${$2:$1}");
		return expansion;
	}
	
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		ICompletionProposal[] completionProposals = super.computeCompletionProposals(viewer, offset);
		for (int i = 0; i < completionProposals.length; i++) {
			if (completionProposals[i] instanceof SnippetTemplateProposal) {
				SnippetTemplateProposal snippetTemplateProposal = (SnippetTemplateProposal) completionProposals[i];
				snippetTemplateProposal.setTemplateProposals(completionProposals);
				Template template = snippetTemplateProposal.getTemplateSuper();
				StyledString styledString =
					new StyledString(String.format("%1$-20.20s", template.getDescription()), FIXED_WIDTH_STYLER);
				
				styledString.append(new StyledString(String.format("%1$10.10s ", template.getName() + "\u21E5"), FIXED_WIDTH_STYLER));

				if (i < 9) {
					char triggerChar = (char)('1'+i);
					snippetTemplateProposal.setTriggerChar(triggerChar);
					styledString.append(new StyledString(String.valueOf(triggerChar), FIXED_WIDTH_STYLER));
				}
				snippetTemplateProposal.setStyledDisplayString(styledString);
			}
		}
		return completionProposals;
	}
	
	@Override
	protected ICompletionProposal createProposal(Template template, TemplateContext context, IRegion region, int relevance) {
		return new SnippetTemplateProposal(template, context, region, getImage(template), relevance, expandSnippet);
	}
	
	@Override
	protected TemplateContext createContext(ITextViewer viewer, IRegion region) {
		TemplateContextType contextType= getContextType(viewer, region);
		if (contextType != null) {
			IDocument document= viewer.getDocument();
			return new DocumentSnippetTemplateContext(contextType, document, region.getOffset(), region.getLength());
		}
		return null;
	}
	
	// Allow any non-whitespace as a prefix.
	protected String extractPrefix(ITextViewer viewer, int offset) {
		int i= offset;
		IDocument document= viewer.getDocument();
		if (i > document.getLength())
			return ""; //$NON-NLS-1$

		try {
			while (i > 0) {
				char ch= document.getChar(i - 1);
				if (Character.isWhitespace(ch))
					break;
				i--;
			}

			return document.get(i, offset - i);
		} catch (BadLocationException e) {
			return ""; //$NON-NLS-1$
		}
	}
	
	private static class CustomStyler extends Styler {
		private static String fForegroundColorName;

		CustomStyler() {
			this(null);
		}
		
		CustomStyler(String foregroundColorName) {
			fForegroundColorName = foregroundColorName;
		}

		public void applyStyles(TextStyle textStyle) {
			if (fForegroundColorName != null) {
				textStyle.foreground = JFaceResources.getColorRegistry().get(fForegroundColorName);
			}
			
			textStyle.font = JFaceResources.getFontRegistry().get("org.eclipse.jface.textfont");
		}
	}
	
	private static Styler FIXED_WIDTH_STYLER = new CustomStyler();
}