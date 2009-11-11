package tabsnippetexpansion;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

public class SnippetsContentAssistant extends ContentAssistant {
	
	private IContentAssistProcessor contentAssistProcessor;
	private ExpandSnippet expandSnippet;

	public SnippetsContentAssistant(ExpandSnippet expandSnippet) {
		super();
		this.expandSnippet = expandSnippet;
		enableAutoActivation(false);
//		enableAutoInsert(true);
		enablePrefixCompletion(true);
	}
	
	@Override
	public IContentAssistProcessor getContentAssistProcessor(
			String contentType) {
		if (contentAssistProcessor == null) {
			contentAssistProcessor = new SnippetsCompletionProcessor(expandSnippet);
		}
		return contentAssistProcessor;
	}
}
