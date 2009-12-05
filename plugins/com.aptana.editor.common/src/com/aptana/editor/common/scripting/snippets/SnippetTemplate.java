package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.templates.Template;

import com.aptana.scripting.model.Snippet;

public class SnippetTemplate extends Template {

	private Snippet snippet;

	public SnippetTemplate(String name, String description, String contextTypeId, String pattern) {
		super(name, description, contextTypeId, pattern, true);
		this.snippet = null;
	}
	
	public SnippetTemplate(Snippet snippet, String contextTypeId) {
		this(snippet.getTrigger(),
				snippet.getDisplayName(),
				contextTypeId,
				SnippetsCompletionProcessor.processExpansion(snippet.getExpansion()));
		this.snippet = snippet;
	}
	
	public Snippet getSnippet() {
		return snippet;
	}

	@Override
	public boolean matches(String prefix, String contextTypeId) {
		boolean matches = super.matches(prefix, contextTypeId);
		if (!matches) {
			return matches;
		}
		return prefix != null && prefix.length() != 0 && getName().toLowerCase().startsWith(prefix.toLowerCase());
	}
}
