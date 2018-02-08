/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.preferences;

import com.aptana.editor.findbar.FindBarPlugin;

/**
 * @author Fabio Zadrozny
 */
public interface IPreferencesConstants
{
	/**
	 * Preferences indicating whether we should use the custom find bar on Ctrl+F
     * (if false, we'll use the default find bar dialog).
	 */
	public static final String USE_CUSTOM_FIND_BAR = FindBarPlugin.PLUGIN_ID + ".useCustomFindBar"; //$NON-NLS-1$
	
	
	/**
	 * Preferences indicating whether we should do incremental searches in the find bar.
	 */
	public static final String INCREMENTAL_SEARCH_ON_FIND_BAR = FindBarPlugin.PLUGIN_ID + ".incrementalSearchOnFindBar"; //$NON-NLS-1$
	
	
	/**
	 * Preferences indicating whether Ctrl+F should open the eclipse search or focus the find combo 
	 */
	public static final String CTRL_F_TWICE_OPENS_ECLIPSE_FIND_BAR = FindBarPlugin.PLUGIN_ID + ".ctrlFTwiceOpensEclipseFindBar"; //$NON-NLS-1$

	
	public static final String SEARCH_BACKWARD_IN_FIND_BAR = FindBarPlugin.PLUGIN_ID + ".searchBackwardFindBar"; //$NON-NLS-1$


	public static final String REGULAR_EXPRESSION_IN_FIND_BAR = FindBarPlugin.PLUGIN_ID + ".regularExpressionFindBar"; //$NON-NLS-1$


	public static final String CASE_SENSITIVE_IN_FIND_BAR = FindBarPlugin.PLUGIN_ID + ".caseSensitiveFindBar"; //$NON-NLS-1$

	public static final String WHOLE_WORD_IN_FIND_BAR = FindBarPlugin.PLUGIN_ID + ".wholeWordFindBar"; //$NON-NLS-1$

	public static final String MATCH_COUNT_IN_FIND_BAR = FindBarPlugin.PLUGIN_ID + ".matchCount"; //$NON-NLS-1$

	public static final String SCOPE_IN_FIND_BAR = FindBarPlugin.PLUGIN_ID + ".scope"; //$NON-NLS-1$
}
