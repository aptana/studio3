package com.aptana.editor.common.spelling;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.editor.common.spelling.messages"; //$NON-NLS-1$
	public static String ScopeDefinitions_BlockComment;
	public static String ScopeDefinitions_Documentation;
	public static String ScopeDefinitions_DoubleQuotedString;
	public static String ScopeDefinitions_LineComment;
	public static String ScopeDefinitions_SingleQiotedString;
	public static String ScopeDefinitions_UnquotedStringHeredoc;
	public static String SpellingPreferencePage_DisabledMessage;
	public static String SpellingPreferencePage_EnabledMessage;
	public static String SpellingPreferencePage_label;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
