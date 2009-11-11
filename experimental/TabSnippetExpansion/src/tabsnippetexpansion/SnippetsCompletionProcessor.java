/**
 * 
 */
package tabsnippetexpansion;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;

class SnippetsCompletionProcessor extends TemplateCompletionProcessor {

	private static final SnippetTemplateContextType SNIPPET_TEMPLATE_CONTEXT_TYPE = new SnippetTemplateContextType();
	private final ExpandSnippet expandSnippet;

	public SnippetsCompletionProcessor(ExpandSnippet expandSnippet) {
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

	private static Template[] templates = new Template[] {
		new SnippetTemplate("contact", "Insert Contact", "snippets", 
				  "----------------------------\n"
				+ "First Name : ${firstName:2}\n"
				+ "Last Name  : ${lastName:1}\n"
				+ "Full Name  : Mr./Mrs./Ms. ${firstName}, ${lastName}\n"
				+ "Description: ${cursor}\n"
				+ "----------------------------\n"
				,true)
	};
	
	@Override
	protected Template[] getTemplates(String contextTypeId) {
		return templates;
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
}