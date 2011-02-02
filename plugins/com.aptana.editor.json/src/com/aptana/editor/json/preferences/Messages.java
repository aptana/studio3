package com.aptana.editor.json.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.json.preferences.messages"; //$NON-NLS-1$
	public static String JSONFormatterBracesPage_blocks;
	public static String JSONFormatterBracesPage_braces_group_label;
	public static String JSONFormatterCommentsPage_comments_group_label;
	public static String JSONFormatterCommentsPage_enable_warpping;
	public static String JSONFormatterCommentsPage_max_line_width;
	public static String JSONFormatterControlStatementsPage_general_group_label;
	public static String JSONFormatterControlStatementsPage_indentation_size_group_option;
	public static String JSONFormatterControlStatementsPage_tab_policy_group_option;
	public static String JSONFormatterControlStatementsPage_tab_size_group_option;
	public static String JSONFormatterModifyDialog_braces_;
	public static String JSONFormatterModifyDialog_indentation_page_tab_name;
	public static String JSONFormatterModifyDialog_JSON_formater_title;
	public static String JSONPreferencePage_JSON_Page_Title;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
