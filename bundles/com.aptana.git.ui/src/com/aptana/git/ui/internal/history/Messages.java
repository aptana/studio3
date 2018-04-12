/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.history;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.history.messages"; //$NON-NLS-1$

	public static String CommitFileDiffViewer_Created;
	public static String CommitFileDiffViewer_Deleted;
	public static String CommitFileDiffViewer_Modified;
	public static String CommitFileDiffViewer_PathColumnLabel;
	public static String CommitFileDiffViewer_Renamed;
	public static String CommitGraphTable_AuthorColumn_Label;
	public static String CommitGraphTable_DateColumn_Label;
	public static String GitCompareFileRevisionEditorInput_workspace;
	public static String GitCompareFileRevisionEditorInput_localRevision;
	public static String GitCompareFileRevisionEditorInput_repository;
	public static String GitCompareFileRevisionEditorInput_0;
	public static String GitCompareFileRevisionEditorInput_1;
	public static String GitCompareFileRevisionEditorInput_2;
	public static String GitCompareFileRevisionEditorInput_CompareInputTitle;
	public static String GitCompareFileRevisionEditorInput_CompareResourceAndVersion;
	public static String GitCompareFileRevisionEditorInput_ProblemGettingContent_Error;
	public static String GitHistoryPage_DateFormat;
	public static String GitHistoryPage_GeneratingHistoryJob_title;
	public static String OpenRevisionAction_Text;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
