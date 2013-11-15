/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * Debug UI constants
 * 
 * @author Max Stepanov
 */
public interface IJSDebugUIConstants {
	/**
	 * PLUGIN_ID
	 */
	String PREFIX = JSDebugUIPlugin.PLUGIN_ID;

	/**
	 * PREF_INSTALL_DEBUGGER
	 */
	String PREF_INSTALL_DEBUGGER = PREFIX + ".install_debugger"; //$NON-NLS-1$

	/**
	 * PREF_SKIP_FIREFOX_CHECK
	 */
	String PREF_SKIP_FIREFOX_CHECK = PREFIX + ".skip_firefox_check"; //$NON-NLS-1$

	/**
	 * PREF_CONFIRM_EXIT_DEBUGGER
	 */
	String PREF_CONFIRM_EXIT_DEBUGGER = PREFIX + ".confirm_exit_debugger"; //$NON-NLS-1$

	/**
	 * PREF_SHOW_CONSTANTS
	 */
	String PREF_SHOW_CONSTANTS = PREFIX + ".show_constants"; //$NON-NLS-1$

	/**
	 * PREF_SHOW_DETAILS
	 */
	String PREF_SHOW_DETAILS = PREFIX + ".show_details"; //$NON-NLS-1$

	/**
	 * "Show detail" preference values.
	 */
	String INLINE_ALL = "INLINE_ALL"; //$NON-NLS-1$

	/**
	 * INLINE_FORMATTERS
	 */
	String INLINE_FORMATTERS = "INLINE_FORMATTERS"; //$NON-NLS-1$

	/**
	 * DETAIL_PANE
	 */
	String DETAIL_PANE = "DETAIL_PANE"; //$NON-NLS-1$

	/**
	 * CONSOLE_WARN_COLOR
	 */
	String CONSOLE_WARN_COLOR = "CONSOLE_WARN_COLOR"; //$NON-NLS-1$
}
