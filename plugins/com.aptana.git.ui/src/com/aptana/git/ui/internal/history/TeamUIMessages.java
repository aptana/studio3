package com.aptana.git.ui.internal.history;

import org.eclipse.osgi.util.NLS;

public class TeamUIMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.history.messages"; //$NON-NLS-1$
	public static String GitCompareFileRevisionEditorInput_ProblemGettingContent_Error;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, TeamUIMessages.class);
	}

	private TeamUIMessages()
	{
	}
}
