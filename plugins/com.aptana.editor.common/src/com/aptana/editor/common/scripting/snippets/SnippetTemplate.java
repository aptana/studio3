package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.templates.Template;

import com.aptana.scripting.model.SnippetElement;

public class SnippetTemplate extends Template {

	private SnippetElement snippet;

	public SnippetTemplate(String name, String description, String contextTypeId, String pattern) {
		super(name, description, contextTypeId, pattern, true);
		this.snippet = null;
	}
	
	public SnippetTemplate(SnippetElement snippet, String contextTypeId) {
		this(snippet.getTriggers(),
				snippet.getDisplayName(),
				contextTypeId,
				SnippetsCompletionProcessor.processExpansion(snippet.getExpansion()));
		this.snippet = snippet;
	}
	
	public SnippetElement getSnippet() {
		return snippet;
	}

	@Override
	public boolean matches(String prefix, String contextTypeId) {
		boolean matches = super.matches(prefix, contextTypeId);
		if (!matches) {
			return matches;
		}
		return prefix != null && prefix.length() != 0 && getName() != null && getName().toLowerCase().startsWith(prefix.toLowerCase());
	}
}
