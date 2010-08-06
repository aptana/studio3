/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.editor.common.preferences;

import com.aptana.editor.common.CommonEditorPlugin;

public interface IPreferenceConstants
{

	/**
	 * Do we auto-pop content assist?
	 */
	public static final String CONTENT_ASSIST_AUTO_ACTIVATION = "CONTENT_ASSIST_AUTO_ACTIVATION"; //$NON-NLS-1$

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
	 * Pref key for linking the outline view with the active editor
	 */
	public static final String LINK_OUTLINE_WITH_EDITOR = CommonEditorPlugin.PLUGIN_ID + ".linkOutlineWithEditor"; //$NON-NLS-1$

	/**
	 * Pref key for sorting the outline view alphabetically
	 */
	public static final String SORT_OUTLINE_ALPHABETIC = CommonEditorPlugin.PLUGIN_ID + ".sortOutlineAlphabetic"; //$NON-NLS-1$
}
