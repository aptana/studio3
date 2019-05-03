/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.preferences;

import com.aptana.debug.core.IDebugCorePreferenceNames;
import com.aptana.js.debug.core.JSDebugPlugin;

/**
 * @author Max Stepanov
 */
public interface IJSDebugPreferenceNames {

	/**
	 * PREFIX
	 */
	String PREFIX = JSDebugPlugin.PLUGIN_ID;

	/**
	 * suspend on first line preference key
	 */
	String SUSPEND_ON_FIRST_LINE = PREFIX + ".suspend_on_first_line"; //$NON-NLS-1$

	/**
	 * suspend on errors preference key
	 */
	String SUSPEND_ON_UNCAUGHT_EXCEPTIONS = PREFIX + ".suspend_on_uncaught_exceptions"; //$NON-NLS-1$

	/**
	 * suspend on exceptions preference key
	 */
	String SUSPEND_ON_ALL_EXCEPTIONS = PREFIX + ".suspend_on_all_exceptions"; //$NON-NLS-1$

	/**
	 * suspend on "debugger" keyword preference key
	 */
	String SUSPEND_ON_DEBUGGER_KEYWORD = PREFIX + ".suspend_on_debugger_keyword"; //$NON-NLS-1$

	/**
	 * List of defined detail formatters.A String containing a comma separated list of fully qualified type names, the
	 * associated code snippet and an 'enabled' flag.
	 */
	String PREF_DETAIL_FORMATTERS_LIST = PREFIX + IDebugCorePreferenceNames.SUFFIX_DETAIL_FORMATTERS_LIST;

}
