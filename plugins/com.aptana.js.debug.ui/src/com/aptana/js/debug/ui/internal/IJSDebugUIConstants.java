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
}
