package com.aptana.editor.findbar.impl;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.editor.findbar.impl.messages"; //$NON-NLS-1$
	public static String FindBarDecorator_LABEL_CaseSensitive;
	public static String FindBarDecorator_LABEL_Elipses;
	public static String FindBarDecorator_LABEL_FInd;
	public static String FindBarDecorator_LABEL_RegularExpression;
	public static String FindBarDecorator_LABEL_WholeWord;
	public static String FindBarDecorator_MSG_StringNotFound;
	public static String FindBarDecorator_TOOLTIP_HideFindBar;
	public static String FindBarDecorator_TOOLTIP_ShowMatchCount;
	public static String FindBarDecorator_TOOLTIP_MatchCount;
	public static String FindBarDecorator_TOOLTIP_ShowFindReplaceDialog;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
