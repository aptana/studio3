package com.aptana.editor.common.scripting.snippets;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.editor.common.scripting.snippets.messages"; //$NON-NLS-1$
	public static String SnippetsContentAssistant_MSG_SelectNthSnippet;
	public static String SnippetTemplateProposal_TITLE_SnippetTemplateProposalError;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
