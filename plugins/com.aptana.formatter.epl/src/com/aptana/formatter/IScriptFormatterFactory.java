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
package com.aptana.formatter;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.formatter.preferences.IPreferencesLookupDelegate;
import com.aptana.formatter.preferences.IPreferencesSaveDelegate;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.preferences.profile.IProfile;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;

/**
 * Script source code formatter factory interface.
 */
public interface IScriptFormatterFactory extends IContributedExtension
{

	/**
	 * Retrieves the formatting options from the specified <code>delegate</code>
	 * 
	 * @param delegate
	 * @return
	 */
	Map<String, String> retrievePreferences(IPreferencesLookupDelegate delegate);

	/**
	 * Change the preferences to perform only indenting. Preferences affecting the number of lines will be disabled.
	 * 
	 * @param preferences
	 * @return
	 * @since 2.0
	 */
	Map<String, String> changeToIndentingOnly(Map<String, String> preferences);

	PreferenceKey[] getPreferenceKeys();

	void savePreferences(Map<String, String> preferences, IPreferencesSaveDelegate delegate);

	/**
	 * Creates the {@link IScriptFormatter} with the specified preferences.
	 * 
	 * @param lineDelimiter
	 *            the line delimiter to use
	 * @param preferences
	 *            the formatting options
	 * @param language
	 *            The language this formatter is going to work on
	 */
	IScriptFormatter createFormatter(String lineDelimiter, Map<String, String> preferences);

	void saveCustomProfiles(List<IProfile> profiles);

	/**
	 * Returns the contribution id
	 */
	String getId();

	/**
	 * Validates that this formatter factory is correctly installed.
	 * 
	 * @return
	 */
	boolean isValid();

	/**
	 * Return the preview content to use with this formatter or <code>null</code> if no preview is available.
	 * 
	 * @return
	 */
	URL getPreviewContent();

	/**
	 * @return
	 */
	IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner);

	SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter);

	PreferenceKey getFormatterPreferenceKey();

	IPreferenceStore getPreferenceStore();

	/**
	 * Returns the main content-type identifier string for this formatter.<br>
	 * The main content type may be different then the content type that initiated this formatter factory. This allows
	 * having a Master formatter, such as HTML, working with a parser that is not an HTML parser, but some other parser
	 * that support a nested language such as ERB.
	 * 
	 * @return The main content-type.
	 * @see #setMainContentType(String)
	 */
	String getMainContentType();

	/**
	 * Set the main content-type identifier string for this formatter.<br>
	 * The main content type may be different then the content type that initiated this formatter factory. This allows
	 * having a Master formatter, such as HTML, working with a parser that is not an HTML parser, but some other parser
	 * that support a nested language such as ERB.
	 * 
	 * @param mainContentType
	 * @see #getMainContentType()
	 */
	void setMainContentType(String mainContentType);
}
