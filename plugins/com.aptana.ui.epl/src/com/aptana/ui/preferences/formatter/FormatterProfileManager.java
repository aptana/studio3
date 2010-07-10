/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.preferences.PreferencesAccess;

/**
 *
 */
public class FormatterProfileManager extends ProfileManager {

	public final static String APTANA_PROFILE = "com.aptana.ui.formatting.default.aptana_profile"; //$NON-NLS-1$
	public final static String NO_FORMATTING = "com.aptana.ui.formatting._noformatting"; //$NON-NLS-1$
	public final static String DEFAULT_PROFILE = APTANA_PROFILE;

	private final static KeySet[] KEY_SETS = new KeySet[] {
			new KeySet(UIEplPlugin.PLUGIN_ID, new ArrayList<String>(getNoFormattingSettings().keySet())),
			new KeySet(UIEplPlugin.PLUGIN_ID, Collections.EMPTY_LIST) };

	private final static String PROFILE_KEY = CommentsTabPage.FORMATTER_PROFILE;
	private final static String FORMATTER_SETTINGS_VERSION = "formatter_settings_version"; //$NON-NLS-1$

	/**
	 * @param profiles
	 * @param context
	 * @param preferencesAccess
	 * @param pluginId
	 */
	public FormatterProfileManager(List<Profile> profiles, IScopeContext context, PreferencesAccess preferencesAccess,
			String pluginId)
	{
		super(addBuiltinProfiles(profiles, pluginId), context, preferencesAccess, KEY_SETS, PROFILE_KEY,
				FORMATTER_SETTINGS_VERSION, pluginId);
	}

	// Add the Aptana Built-in profile
	private static List<Profile> addBuiltinProfiles(List<Profile> profiles, String pluginId) {
		Map<String, String> aptanaSettings = getAptanaSettings();
		if (pluginId.equals(UIEplPlugin.PLUGIN_ID))
		{
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, " "); //$NON-NLS-1$
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BODY, Boolean.TRUE
					.toString());
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BLOCK, Boolean.TRUE
					.toString());
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH,
					Boolean.TRUE.toString());
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES, Boolean.TRUE
					.toString());
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES, Boolean.TRUE
					.toString());
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS, Boolean.TRUE
					.toString());
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_INDENT_INLINE_PHP_BLOCK, Boolean.FALSE.toString());
			
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_IMPORTS, "5"); //$NON-NLS-1$
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD, "0"); //$NON-NLS-1$
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY, "0"); //$NON-NLS-1$
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT,
					CommentsTabPage.DO_NOT_INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT,
					CommentsTabPage.DO_NOT_INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT,
					CommentsTabPage.DO_NOT_INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT,
					CommentsTabPage.DO_NOT_INSERT);
			// keep 'elst if' on one line
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF,  Boolean.TRUE.toString());
			// classes
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TYPE_HEADER, Boolean.TRUE.toString());
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_SUPERINTERFACES, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_TYPE_DECLARATION, CommentsTabPage.INSERT);
			// fields
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, CommentsTabPage.INSERT);
			// constants
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_CONSTANT_DECLARATIONS, CommentsTabPage.INSERT);
			// methods
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_METHOD_DECLARATION, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_PARAMETERS, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_THROWS, CommentsTabPage.INSERT);
			// blocks
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_BLOCK, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK, CommentsTabPage.INSERT);
			// if else
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IF, CommentsTabPage.INSERT);
			// for
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INITS, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INCREMENTS, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_FOR, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_FOR, CommentsTabPage.INSERT);
			// switch
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_SWITCH, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH, CommentsTabPage.INSERT);
			// while & do-while
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE, CommentsTabPage.INSERT);
			// catch
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH, CommentsTabPage.INSERT);
			// function invocation
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_INVOCATION_ARGUMENTS, CommentsTabPage.INSERT);
			// assignment
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR, CommentsTabPage.INSERT);
			// operators
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR, CommentsTabPage.INSERT);
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OBJECT_OPERATOR, CommentsTabPage.DO_NOT_INSERT);
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OBJECT_OPERATOR, CommentsTabPage.DO_NOT_INSERT);
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CONCAT, CommentsTabPage.DO_NOT_INSERT);
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CONCAT, CommentsTabPage.DO_NOT_INSERT);
			// type cast
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_PAREN_IN_TYPE_CAST, CommentsTabPage.INSERT);
			// conditionals
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_QUESTION_IN_CONDITIONAL, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_QUESTION_IN_CONDITIONAL, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CONDITIONAL, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_CONDITIONAL, CommentsTabPage.INSERT);
			// arrays
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ARRAY_INITIALIZER, CommentsTabPage.INSERT);
			// blank lines
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_CLASSDECL, "0"); //$NON-NLS-1$
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_CONSTANT, "0"); //$NON-NLS-1$
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_FIELD, "0"); //$NON-NLS-1$
			// insert new lines
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_METHOD_BODY, CommentsTabPage.INSERT);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_BLOCK, CommentsTabPage.INSERT);
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_CLASS_BODY, CommentsTabPage.INSERT);
			aptanaSettings.put(PHPCodeFormatterConstants.FORMATTER_PUT_EMPTY_STATEMENT_ON_NEW_LINE, Boolean.TRUE.toString());
			// braces positions
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK, DefaultCodeFormatterConstants.END_OF_LINE);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE, DefaultCodeFormatterConstants.END_OF_LINE);
			aptanaSettings.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH, DefaultCodeFormatterConstants.END_OF_LINE);
		}
		final Profile aptanaProfile = new BuiltInProfile(APTANA_PROFILE,
				FormatterMessages.ProfileManager_aptana_conventions_profile_name, aptanaSettings, 1, 1, ""); //$NON-NLS-1$

		final Profile noFormatting = new BuiltInProfile(NO_FORMATTING,
				FormatterMessages.ProfileManager_noformatting_profile_name, getNoFormattingSettings(), 1, 1, ""); //$NON-NLS-1$

		profiles.add(aptanaProfile);
		profiles.add(noFormatting);
		return profiles;
	}

	
	/**
	 * @return Returns the settings for the no formatting profile.
	 */
	public static Map<String, String> getNoFormattingSettings()
	{
		Map<String, String> original = getAptanaSettings();
		original.put(DefaultCodeFormatterConstants.NO_FORMATTING, "yes"); //$NON-NLS-1$
		return original;
	}

	/**
	 * @return Returns the settings for the default profile.
	 */
	public static Map<String, String> getEclipse21Settings()
	{
		final Map<String, String> options = DefaultCodeFormatterConstants.getEclipse21Settings();

		return options;
	}

	/**
	 * @return Returns the settings for the new eclipse profile.
	 */
	public static Map<String, String> getEclipseSettings()
	{
		final Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

		return options;
	}

	/**
	 * @return Returns the settings for the Java Conventions profile.
	 */
	public static Map<String, String> getAptanaSettings()
	{
		final Map<String, String> options = DefaultCodeFormatterConstants.getJavaConventionsSettings();

		return options;
	}

	/**
	 * @return Returns the default settings.
	 */
	public static Map<String, String> getDefaultSettings()
	{
		return getEclipseSettings();
	}

	protected String getSelectedProfileId(IScopeContext instanceScope)
	{
		String profileId = instanceScope.getNode(pluginId).get(PROFILE_KEY, null);
		if (profileId == null)
		{
			// request from bug 129427
			profileId = new DefaultScope().getNode(pluginId).get(PROFILE_KEY, null);
			// fix for bug 89739
			if (DEFAULT_PROFILE.equals(profileId))
			{ // default default:
				IEclipsePreferences node = instanceScope.getNode(pluginId);
				if (node != null)
				{
					String tabSetting = node.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, null);
					if (CommentsTabPage.SPACE.equals(tabSetting))
					{
						profileId = APTANA_PROFILE;
					}
				}
			}
		}
		return profileId;
	}

	public Profile getDefaultProfile()
	{
		return getProfile(DEFAULT_PROFILE);
	}

}
