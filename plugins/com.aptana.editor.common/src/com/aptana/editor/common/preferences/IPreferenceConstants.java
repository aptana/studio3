/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import com.aptana.editor.common.CommonEditorPlugin;

public interface IPreferenceConstants
{

	/**
	 * Do we auto-pop content assist?
	 */
	public static final String CONTENT_ASSIST_AUTO_ACTIVATION = "CONTENT_ASSIST_AUTO_ACTIVATION"; //$NON-NLS-1$

	/**
	 * Do we auto-insert single proposals for content assist?
	 */
	public static final String CONTENT_ASSIST_AUTO_INSERT = "CONTENT_ASSIST_AUTO_INSERT"; //$NON-NLS-1$

	/**
	 * The delay before which we show code assist
	 */
	public static final String CONTENT_ASSIST_DELAY = "CONTENT_ASSIST_DELAY"; //$NON-NLS-1$

	/**
	 * Pref key for the enable of coloring pair matches.
	 */
	public String ENABLE_CHARACTER_PAIR_COLORING = CommonEditorPlugin.PLUGIN_ID + ".enableCharacterPairColoring"; //$NON-NLS-1$

	/**
	 * Pref key for the color of the pair matching box.
	 */
	public String CHARACTER_PAIR_COLOR = CommonEditorPlugin.PLUGIN_ID + ".characterPairColor"; //$NON-NLS-1$

	/**
	 * Command line property name/pref key we can set to change the max number of columns to color per-line.
	 */
	public static final String EDITOR_MAX_COLORED_COLUMNS = "studio.maxColoredColumns"; //$NON-NLS-1$
	public static final int EDITOR_MAX_COLORED_COLUMNS_DEFAULT = 200;

	/**
	 * Pref key for linking the outline view with the active editor
	 */
	public static final String LINK_OUTLINE_WITH_EDITOR = CommonEditorPlugin.PLUGIN_ID + ".linkOutlineWithEditor"; //$NON-NLS-1$

	/**
	 * Pref key for sorting the outline view alphabetically
	 */
	public static final String SORT_OUTLINE_ALPHABETIC = CommonEditorPlugin.PLUGIN_ID + ".sortOutlineAlphabetic"; //$NON-NLS-1$

	/**
	 * The preference key for the comma-separated list of task tag names.
	 */
	public static final String TASK_TAG_NAMES = CommonEditorPlugin.PLUGIN_ID + ".taskTagNames"; //$NON-NLS-1$

	/**
	 * The preference key for the comma-separated list of task tag priorities. Order is important and lines up with
	 * {@value #TASK_TAG_NAMES}
	 */
	public static final String TASK_TAG_PRIORITIES = CommonEditorPlugin.PLUGIN_ID + ".taskTagPriorities"; //$NON-NLS-1$

	/**
	 * The preference key for determining if task tags should be treated in a case-sensitive manner when detecting them.
	 */
	public static final String TASK_TAGS_CASE_SENSITIVE = CommonEditorPlugin.PLUGIN_ID + ".taskTagsCaseSensitive"; //$NON-NLS-1$

	/**
	 * Enable highlighting of occurrences of selected text
	 */
	public static final String EDITOR_MARK_OCCURRENCES = CommonEditorPlugin.PLUGIN_ID + ".editorMarkOccurrences"; //$NON-NLS-1$

	/**
	 * Do we automatically insert matching characters?
	 */
	public static final String EDITOR_PEER_CHARACTER_CLOSE = CommonEditorPlugin.PLUGIN_ID + ".editorPeerCharacterClose"; //$NON-NLS-1$

	/**
	 * Do we automatically wrap selections with matching chars?
	 */
	public static final String EDITOR_WRAP_SELECTION = CommonEditorPlugin.PLUGIN_ID + ".editorWrapSelection"; //$NON-NLS-1$

	/**
	 * The preference key for the comma-separated list of selected validators for a language
	 */
	public static final String SELECTED_VALIDATORS = CommonEditorPlugin.PLUGIN_ID + ".selectedValidators"; //$NON-NLS-1$

	/**
	 * The preference key for the list of regular expressions where the validation warnings and errors would be ignored
	 * if matched
	 */
	public static final String FILTER_EXPRESSIONS = CommonEditorPlugin.PLUGIN_ID + ".filterExpressions"; //$NON-NLS-1$

	/**
	 * The preference key to determine whether parse errors are enabled for each language
	 */
	public static final String PARSE_ERROR_ENABLED = CommonEditorPlugin.PLUGIN_ID + ".enableParseError"; //$NON-NLS-1$

	/**
	 * Do we display code assist hovers
	 */
	public static final String CONTENT_ASSIST_HOVER = CommonEditorPlugin.PLUGIN_ID + ".contentAssistHover"; //$NON-NLS-1$

	/**
	 * The preference key used to enable and disable auto indent.
	 */
	public static final String EDITOR_AUTO_INDENT = CommonEditorPlugin.PLUGIN_ID + ".autoIndent"; //$NON-NLS-1$

	/**
	 * The preference key used to determine whether we use global defaults
	 */
	public static final String USE_GLOBAL_DEFAULTS = CommonEditorPlugin.PLUGIN_ID + ".useGlobalDefaults"; //$NON-NLS-1$

	/**
	 * The preference key used to enable and disable folding.
	 */
	public static final String EDITOR_ENABLE_FOLDING = CommonEditorPlugin.PLUGIN_ID + ".enableFolding"; //$NON-NLS-1$

	/**
	 * The preference key used to enable and disable word wrap
	 */
	public static final String ENABLE_WORD_WRAP = CommonEditorPlugin.PLUGIN_ID + ".enableWordWrap"; //$NON-NLS-1$
	
	/**
	 * The preference key used to open extensionless files with the selected editor from preferences.
	 */
	public static final String OPEN_WITH_EDITOR = CommonEditorPlugin.PLUGIN_ID + ".openWithEditor"; //$NON-NLS-1$

	/**
	 * The preference key for if to show the dialog of where the untitled file should save to
	 */
	public static final String REMEMBER_UNTITLED_FILE_SAVE_TYPE = CommonEditorPlugin.PLUGIN_ID
			+ ".rememberUntitledFileSaveType"; //$NON-NLS-1$

	/**
	 * The preference key for if to save the untitled file in local filesystem (false) or project (true)
	 */
	public static final String SAVE_UNTITLED_FILE_TO_PROJECT = CommonEditorPlugin.PLUGIN_ID
			+ ".saveUntitledFileToProject"; //$NON-NLS-1$

	/**
	 * The preference key used to list enabled scopes for spelling.
	 */
	public static final String ENABLED_SPELLING_SCOPES = CommonEditorPlugin.PLUGIN_ID + ".enabledSpellingScopes"; //$NON-NLS-1$

	/**
	 * The preference key used to enable and disable the save-action for removing trailing whitespace.
	 */
	public static final String EDITOR_REMOVE_TRAILING_WHITESPACE = CommonEditorPlugin.PLUGIN_ID
			+ ".removeTrailingWhitespace"; //$NON-NLS-1$

	public static final String EDITOR_SUB_WORD_NAVIGATION = CommonEditorPlugin.PLUGIN_ID + ".camelCaseNavigation"; //$NON-NLS-1$

}
