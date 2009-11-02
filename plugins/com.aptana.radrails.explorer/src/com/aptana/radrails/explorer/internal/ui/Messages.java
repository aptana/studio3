package com.aptana.radrails.explorer.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.radrails.explorer.internal.ui.messages"; //$NON-NLS-1$

	public static String GitProjectView_BranchAhead_msg;
	public static String GitProjectView_CommitTooltip;
	public static String GitProjectView_FileCounts;
	public static String GitProjectView_FileCountsLabel;
	public static String GitProjectView_PullJobTitle;
	public static String GitProjectView_PullTooltip;
	public static String GitProjectView_PushJobTitle;
	public static String GitProjectView_PushTooltip;
	public static String GitProjectView_StashJobTitle;
	public static String GitProjectView_StashTooltip;
	public static String GitProjectView_UnresolvedMerges_msg;
	public static String GitProjectView_UnstashJobTitle;
	public static String GitProjectView_UnstashTooltip;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
