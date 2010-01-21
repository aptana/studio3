package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.templates.Template;

import com.aptana.scripting.model.SnippetElement;

public class SnippetTemplate extends Template {
	// TODO: We need to figure out a way to have a common base class for this and CommandTemplate

	private SnippetElement snippet;

	public SnippetTemplate(String name, String description, String contextTypeId, String pattern) {
		super(name, description, contextTypeId, pattern, true);
		this.snippet = null;
	}
	
	public SnippetTemplate(SnippetElement snippet, String trigger, String contextTypeId) {
		this(trigger,
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

    boolean exactMatches(String prefix)
    {
        return prefix != null && prefix.length() != 0 && getName().equalsIgnoreCase(prefix);
    }

    @Override
    public String toString()
    {
    	return getName() + " - " + getPattern(); //$NON-NLS-1$
    }
}
