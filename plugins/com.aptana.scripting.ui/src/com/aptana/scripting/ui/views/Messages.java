package com.aptana.scripting.ui.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.scripting.ui.views.messages"; //$NON-NLS-1$
	public static String CommandsNode_Commands_Node;
	public static String MenusNode_Menus_Node;
	public static String SnippetsNode_Snippets_Node;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
