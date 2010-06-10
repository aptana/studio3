/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.debug.core.preferences;

import com.aptana.debug.core.JSDebugPlugin;

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
	String SUSPEND_ON_ERRORS = PREFIX + ".suspend_on_errors"; //$NON-NLS-1$

	/**
	 * suspend on exceptions preference key
	 */
	String SUSPEND_ON_EXCEPTIONS = PREFIX + ".suspend_on_exceptions"; //$NON-NLS-1$

	/**
	 * suspend on "debugger" keyword preference key
	 */
	String SUSPEND_ON_DEBUGGER_KEYWORD = PREFIX + ".suspend_on_debugger_keyword"; //$NON-NLS-1$

	/**
	 * List of defined detail formatters.A String containing a comma separated
	 * list of fully qualified type names, the associated code snippet and an
	 * 'enabled' flag.
	 */
	String PREF_DETAIL_FORMATTERS_LIST = PREFIX + ".detail_formatters"; //$NON-NLS-1$

	/**
	 * DETAIL_FORMATTER_IS_ENABLED
	 */
	String DETAIL_FORMATTER_IS_ENABLED = "1"; //$NON-NLS-1$

	/**
	 * DETAIL_FORMATTER_IS_DISABLED
	 */
	String DETAIL_FORMATTER_IS_DISABLED = "0"; //$NON-NLS-1$

}
