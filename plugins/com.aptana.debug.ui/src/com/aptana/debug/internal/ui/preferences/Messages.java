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
package com.aptana.debug.internal.ui.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.debug.internal.ui.preferences.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * JSDetailFormattersPreferencePage_DetailFormatters
	 */
	public static String JSDetailFormattersPreferencePage_DetailFormatters;

	/**
	 * JSDetailFormattersPreferencePage_OverrideDefault
	 */
	public static String JSDetailFormattersPreferencePage_OverrideDefault;

	/**
	 * JSDetailFormattersPreferencePage_ShowVariableDetails
	 */
	public static String JSDetailFormattersPreferencePage_ShowVariableDetails;

	/**
	 * JSDetailFormattersPreferencePage_AsLabelForVariablesWithDetailFormatters
	 */
	public static String JSDetailFormattersPreferencePage_AsLabelForVariablesWithDetailFormatters;

	/**
	 * JSDetailFormattersPreferencePage_AsLabelForAllVariables
	 */
	public static String JSDetailFormattersPreferencePage_AsLabelForAllVariables;

	/**
	 * JSDetailFormattersPreferencePage_InDetailPaneOnly
	 */
	public static String JSDetailFormattersPreferencePage_InDetailPaneOnly;

	/**
	 * JSDetailFormattersPreferencePage_TypesWithDetailFormatters
	 */
	public static String JSDetailFormattersPreferencePage_TypesWithDetailFormatters;

	/**
	 * JSDetailFormattersPreferencePage_DetailFormatterCodeSnippetDefinedForSelectedType
	 */
	public static String JSDetailFormattersPreferencePage_DetailFormatterCodeSnippetDefinedForSelectedType;

	/**
	 * JSDetailFormattersPreferencePage_Add
	 */
	public static String JSDetailFormattersPreferencePage_Add;

	/**
	 * JSDetailFormattersPreferencePage_AllowToCreateNewDetailFormatter
	 */
	public static String JSDetailFormattersPreferencePage_AllowToCreateNewDetailFormatter;

	/**
	 * JSDetailFormattersPreferencePage_Edit
	 */
	public static String JSDetailFormattersPreferencePage_Edit;

	/**
	 * JSDetailFormattersPreferencePage_EditSelectedDetailFormatter
	 */
	public static String JSDetailFormattersPreferencePage_EditSelectedDetailFormatter;

	/**
	 * JSDetailFormattersPreferencePage_Remove
	 */
	public static String JSDetailFormattersPreferencePage_Remove;

	/**
	 * JSDetailFormattersPreferencePage_RemoveAllSelectedDetailFormatters
	 */
	public static String JSDetailFormattersPreferencePage_RemoveAllSelectedDetailFormatters;

	/**
	 * JSDebugPreferencePage_JavascriptDebugOptions
	 */
	public static String JSDebugPreferencePage_JavascriptDebugOptions;

	/**
	 * JSDebugPreferencePage_SuspendAtStart
	 */
	public static String JSDebugPreferencePage_SuspendAtStart;

	/**
	 * JSDebugPreferencePage_SuspendOnExceptions
	 */
	public static String JSDebugPreferencePage_SuspendOnExceptions;

	/**
	 * JSDebugPreferencePage_SuspendOnErrors
	 */
	public static String JSDebugPreferencePage_SuspendOnErrors;

	/**
	 * JSDebugPreferencePage_SuspendOnDebuggerKeyword
	 */
	public static String JSDebugPreferencePage_SuspendOnDebuggerKeyword;

	/**
	 * JSDebugPreferencePage_ConfirmExitWhenDebuggerActive
	 */
	public static String JSDebugPreferencePage_ConfirmExitWhenDebuggerActive;
}
