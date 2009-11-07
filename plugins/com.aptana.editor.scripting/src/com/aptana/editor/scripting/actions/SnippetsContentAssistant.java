package com.aptana.editor.scripting.actions;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

public class SnippetsContentAssistant extends ContentAssistant {
	
	private IContentAssistProcessor contentAssistProcessor;
	private ExpandSnippetAction expandSnippetAction;

	public SnippetsContentAssistant(ExpandSnippetAction expandSnippet) {
		super();
		this.expandSnippetAction = expandSnippet;
		enableAutoActivation(false);
//		enableAutoInsert(true);
		enablePrefixCompletion(true);
	}
	
	@Override
	public IContentAssistProcessor getContentAssistProcessor(
			String contentType) {
		if (contentAssistProcessor == null) {
			contentAssistProcessor = new SnippetsCompletionProcessor(expandSnippetAction);
		}
		return contentAssistProcessor;
	}
}
