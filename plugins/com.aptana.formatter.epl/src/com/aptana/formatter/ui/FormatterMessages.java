/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.ui;

import org.eclipse.osgi.util.NLS;

public class FormatterMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.formatter.ui.FormatterMessages"; //$NON-NLS-1$
	public static String AbstractScriptFormatterFactory_defaultProfileName;
	public static String AbstractFormatterSelectionBlock_activeProfile;
	public static String AbstractFormatterSelectionBlock_confirmDefaultsMessage;
	public static String AbstractFormatterSelectionBlock_confirmDefaultsTitle;
	public static String AbstractFormatterSelectionBlock_confirmRemoveLabel;
	public static String AbstractFormatterSelectionBlock_confirmRemoveMessage;
	public static String AbstractFormatterSelectionBlock_defaults;
	public static String AbstractFormatterSelectionBlock_edit;
	public static String AbstractFormatterSelectionBlock_importingProfile;
	public static String AbstractFormatterSelectionBlock_importProfile;
	public static String AbstractFormatterSelectionBlock_importProfileLabel;
	public static String AbstractFormatterSelectionBlock_moreRecentVersion;
	public static String AbstractFormatterSelectionBlock_newProfile;
	public static String AbstractFormatterSelectionBlock_noBuiltInProfiles;
	public static String AbstractFormatterSelectionBlock_notValidProfile;
	public static String AbstractFormatterSelectionBlock_preview;
	public static String AbstractFormatterSelectionBlock_removeProfile;
	public static String AbstractFormatterSelectionBlock_profilesGroup;
	public static String AlreadyExistsDialog_loadProfile;
	public static String AlreadyExistsDialog_nameEmpty;
	public static String AlreadyExistsDialog_nameExists;
	public static String AlreadyExistsDialog_nameExistsQuestion;
	public static String AlreadyExistsDialog_overwriteProfile;
	public static String AlreadyExistsDialog_renameProfile;
	public static String CreateProfileDialog_initSettings;
	public static String CreateProfileDialog_nameEmpty;
	public static String CreateProfileDialog_nameExists;
	public static String CreateProfileDialog_newProfile;
	public static String CreateProfileDialog_profileName;
	public static String FormatterModifyDialog_changeBuiltInProfileName;
	public static String FormatterModifyDialog_createNewProfile;
	public static String FormatterModifyDialog_dialogTitle;
	public static String FormatterModifyDialog_export;
	public static String FormatterModifyDialog_exportProblem;
	public static String FormatterModifyDialog_exportProfile;
	public static String FormatterModifyDialog_nameEmpty;
	public static String FormatterModifyDialog_nameExists;
	public static String FormatterModifyDialog_profileName;
	public static String FormatterModifyDialog_replaceFileQuestion;
	public static String FormatterModifyTabPage_showInvisible;
	public static String FormatterModifyTabPage_preview_label_text;
	public static String FormatterPreferencePage_settingsLink;
	public static String Formatter_formatterParsingErrorStatus;
	public static String ScriptFormattingStrategy_breakpointsRestoreError;
	public static String ScriptFormattingStrategy_formattingError;
	public static String ScriptFormattingStrategy_unableToFormatSourceContainingSyntaxError;
	public static String ScriptFormattingStrategy_unexpectedFormatterError;

	public static String IndentationTabPage_generalSettings;

	public static String IndentationTabPage_general_group_option_tab_policy;
	public static String IndentationTabPage_general_group_option_tab_policy_SPACE;
	public static String IndentationTabPage_general_group_option_tab_policy_TAB;
	public static String IndentationTabPage_general_group_option_tab_policy_MIXED;
	public static String IndentationTabPage_general_group_option_tab_policy_EDITOR;
	public static String IndentationTabPage_general_group_option_tab_size;
	public static String BracesTabPage_position_option_SAME_LINE;
	public static String BracesTabPage_position_option_NEW_LINE;

	public static String IndentationTabPage_general_group_option_indent_size;

	public static String Formatter_formatterError;
	public static String Formatter_formatterErrorStatus;
	public static String Formatter_basicLogFormatterError;
	public static String Formatter_formatterErrorCompletedWithErrors;
	public static String RubyFormatterIndentationTabPage_declarationWithinClassBody;
	public static String RubyFormatterIndentationTabPage_declarationWithinMethodBody;
	public static String RubyFormatterIndentationTabPage_declarationWithinModuleBody;
	public static String FormatterModifyTabPage_generalSettings;
	public static String Formatter_contentErrorMessage;
	public static String PHPFormatter_fatalSyntaxErrors;
	public static String RubyFormatterIndentationTabPage_indentDefinitionsGroupTitle;
	public static String RubyFormatterIndentationTabPage_indentWithinBlocks;
	public static String RubyFormatterIndentationTabPage_statementWithinBlocksBody;
	public static String RubyFormatterIndentationTabPage_statementWithinCaseBody;
	public static String RubyFormatterIndentationTabPage_statementWithinIfBody;
	public static String RubyFormatterIndentationTabPage_statementWithinWhenBody;
	

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, FormatterMessages.class);
	}

	private FormatterMessages()
	{
	}
}
