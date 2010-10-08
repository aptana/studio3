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
package com.aptana.scripting.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.scripting.model.messages"; //$NON-NLS-1$
	
	public static String BundleEntry_Name_Not_Defined;

	public static String BundleManager_BUNDLE_DIRECTORY_DOES_NOT_EXIST;
	public static String BundleManager_BUNDLE_FILE_NOT_A_DIRECTORY;

	public static String BundleManager_Executed_Null_Script;

	public static String BundleManager_No_Bundle_File;

	public static String BundleManager_Reloaded_Null_Script;
	public static String BundleManager_Unloaded_Null_Script;

	public static String BundleManager_UNREADABLE_SCRIPT;

	public static String BundleManager_USER_PATH_NOT_DIRECTORY;

	public static String BundleManager_USER_PATH_NOT_READ_WRITE;

	public static String BundleMonitor_Error_Processing_Resource_Change;

	public static String BundleMonitor_ERROR_REGISTERING_FILE_WATCHER;

	public static String BundleMonitor_INVALID_WATCHER_PATH;

	public static String CommandElement_Error_Building_Env_Variables;

	public static String CommandElement_Error_Creating_Contributor;

	public static String CommandElement_Error_Executing_Command;

	public static String CommandElement_Error_Processing_Command_Block;

	public static String CommandElement_Invalid_Key_Binding;

	public static String CommandElement_Undefined_Key_Binding;

	public static String CommandElement_Unrecognized_OS;

	public static String CommandScriptRunner_CANNOT_LOCATE_SHELL;

	public static String CommandScriptRunner_UNABLE_TO_LOCATE_SHELL_FOR_COMMAND;
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
