package com.aptana.editor.common.scripting.snippets;

import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.SnippetElement;

public class SnippetTemplate extends CommandTemplate
{

	public SnippetTemplate(CommandElement commandElement, String pattern)
	{
		super(commandElement, "", "", "", pattern, true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public SnippetTemplate(SnippetElement snippet, String trigger, String contextTypeId)
	{
		super(snippet, trigger, snippet.getDisplayName(), contextTypeId, snippet.getExpansion(), true);
	}
}