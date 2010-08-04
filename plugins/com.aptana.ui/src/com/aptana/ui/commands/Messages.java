package com.aptana.ui.commands;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.commands.messages"; //$NON-NLS-1$
	public static String OpenFileManagerContributionItem_FileBrowserLabel;
	public static String OpenFileManagerContributionItem_FinderLabel;
	public static String OpenFileManagerContributionItem_WindowsExplorerLabel;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
