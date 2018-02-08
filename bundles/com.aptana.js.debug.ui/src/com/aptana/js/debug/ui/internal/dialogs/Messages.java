/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.dialogs;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.js.debug.ui.internal.dialogs.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * AddExceptionBreakpointDialog_AddJavaScriptExceptionBreakpoint
	 */
	public static String AddExceptionBreakpointDialog_AddJavaScriptExceptionBreakpoint;

	/**
	 * DetailFormatterDialog_EditDetailFormatter
	 */
	public static String DetailFormatterDialog_EditDetailFormatter;

	/**
	 * DetailFormatterDialog_AddDetailFormatter
	 */
	public static String DetailFormatterDialog_AddDetailFormatter;

	/**
	 * DetailFormatterDialog_QualifiedTypeName
	 */
	public static String DetailFormatterDialog_QualifiedTypeName;

	/**
	 * DetailFormatterDialog_SelectNType
	 */
	public static String DetailFormatterDialog_SelectNType;

	/**
	 * DetailFormatterDialog_DetailFormatterCodeSnippet
	 */
	public static String DetailFormatterDialog_DetailFormatterCodeSnippet;

	/**
	 * DetailFormatterDialog_Enable
	 */
	public static String DetailFormatterDialog_Enable;

	/**
	 * DetailFormatterDialog_QualifiedTypeNameMustNotBeEmpty
	 */
	public static String DetailFormatterDialog_QualifiedTypeNameMustNotBeEmpty;

	/**
	 * DetailFormatterDialog_DetailFormatterIsDefinedForThisType
	 */
	public static String DetailFormatterDialog_DetailFormatterIsDefinedForThisType;

	/**
	 * DetailFormatterDialog_CodeSnippetMustNotBeEmpty
	 */
	public static String DetailFormatterDialog_CodeSnippetMustNotBeEmpty;

	/**
	 * DetailFormatterDialog_NoTypeWithGivenNameFoundInWorkspace
	 */
	public static String DetailFormatterDialog_NoTypeWithGivenNameFoundInWorkspace;

	/**
	 * DetailFormatterDialog_SelectType
	 */
	public static String DetailFormatterDialog_SelectType;

	/**
	 * DetailFormatterDialog_SelectTypeToFormatWhenDisplayingDetail
	 */
	public static String DetailFormatterDialog_SelectTypeToFormatWhenDisplayingDetail;

	public static String HttpServerPathDialog_Error_EmptyWorkspaceLocation;
	public static String HttpServerPathDialog_Error_IncompleteServerPath;
	public static String HttpServerPathDialog_SelectWorkspaceFolder;
	public static String HttpServerPathDialog_ServerPath;
	public static String HttpServerPathDialog_WorkspaceLocation;
}
