package com.aptana.editor.xml.formatter.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.xml.formatter.preferences.messages"; //$NON-NLS-1$
	public static String XMLFormatterBlankLinesPage_afterElementsLabel;
	public static String XMLFormatterBlankLinesPage_afterNonXMLElementsLabel;
	public static String XMLFormatterBlankLinesPage_beforeNonXMLElementsLabel;
	public static String XMLFormatterBlankLinesPage_blankLinesGroupLabel;
	public static String XMLFormatterBlankLinesPage_existingBlankLinesLabel;
	public static String XMLFormatterBlankLinesPage_existingBlankLinesToPreserveLabel;
	public static String XMLFormatterCommentsPage_commentsGroupLabel;
	public static String XMLFormatterCommentsPage_enableWrappingLabel;
	public static String XMLFormatterCommentsPage_maxWidthLabel;
	public static String XMLFormatterIndentationPage_generalGroupLabel;
	public static String XMLFormatterIndentationPage_exclusionsLabel;
	public static String XMLFormatterIndentationPage_exclusionsMessageLabel;
	
	public static String XMLFormatterModifyDialog_BlankLinesTabName;
	public static String XMLFormatterModifyDialog_indentationTabName;
	public static String XMLFormatterModifyDialog_newLinesTabName;
	public static String XMLFormatterNewLinesPage_exclude_text_node_label;
	public static String XMLFormatterNewLinesPage_exclusionsGroupLabel;
	public static String XMLFormatterNewLinesPage_exclusionsMessageLabel;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
