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
	public static String GitHistoryPage_AuthorHeader;
	public static String GitHistoryPage_CreatedHeader;
	public static String GitHistoryPage_DateFormat;
	public static String GitHistoryPage_DateHeader;
	public static String GitHistoryPage_DeletedHeader;
	public static String GitHistoryPage_ModifiedHeader;
	public static String GitHistoryPage_ParentHeader;
	public static String GitHistoryPage_RenamedHeader;
	public static String GitHistoryPage_SHA;
	public static String GitHistoryPage_SubjectHeader;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
