/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.preferences;

/**
 * IPreferenceContants
 */
public interface IPreferenceContants
{
	/**
	 * The value is a double used to indicate the current format being used by the HTML index categories.
	 */
	String HTML_INDEX_VERSION = "HTML_INDEX_VERSION"; //$NON-NLS-1$

	/**
	 * The value is a string where each character in the string will be used as an auto-activation character in
	 * HTMLContentAssistProcessor
	 */
	String HTML_ACTIVATION_CHARACTERS = "HTML_ACTIVATION_CHARACTERS"; //$NON-NLS-1$

	/**
	 * The preference key used to set the option for when we want close tags auto inserted using Content Assist, or HTMLOpenTagCloser.
	 */
	String HTML_AUTO_CLOSE_TAG_PAIRS = "HTML_AUTO_CLOSE_TAG_PAIRS"; //$NON-NLS-1$
}
