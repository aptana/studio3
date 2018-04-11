/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.core.preferences;

/**
 * IPreferenceContants
 */
public interface IPreferenceConstants
{
	/**
	 * All the preferences are currently stored in this node. Use it for now until all the preferences are migrated.
	 */
	String PREFERNCES_NODE = "com.aptana.editor.html"; //$NON-NLS-1$

	/**
	 * The value is a double used to indicate the current format being used by the HTML index categories.
	 */
	String HTML_INDEX_VERSION = "HTML_INDEX_VERSION"; //$NON-NLS-1$

	/**
	 * The preference key used to set the option for when we want close tags auto inserted using Content Assist, or
	 * HTMLOpenTagCloser.
	 */
	String HTML_AUTO_CLOSE_TAG_PAIRS = "HTML_AUTO_CLOSE_TAG_PAIRS"; //$NON-NLS-1$

	/**
	 * The preference key used to define what tag attributes should be displayed in the outline
	 */
	String HTML_OUTLINE_TAG_ATTRIBUTES_TO_SHOW = "HTML_OUTLINE_TAG_ATTRIBUTES"; //$NON-NLS-1$

	/**
	 * The preference key on if the text nodes should be shown in the outline
	 */
	String HTML_OUTLINE_SHOW_TEXT_NODES = "HTML_OUTLINE_SHOW_TEXT_NODES"; //$NON-NLS-1$

	/**
	 * The preference key used to set the option for when we allow hitting remote URIs to determine href/src
	 * path/children Content Assist proposals.
	 */
	String HTML_REMOTE_HREF_PROPOSALS = "HTML_REMOTE_HREF_PROPOSALS"; //$NON-NLS-1$

	/**
	 * Default value for {@link #HTML_REMOTE_HREF_PROPOSALS}
	 */
	boolean DEFAULT_REMOTE_HREF_PROPOSALS_VALUE = true;
}
