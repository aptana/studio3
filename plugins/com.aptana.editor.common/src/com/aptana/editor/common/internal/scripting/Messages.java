package com.aptana.editor.common.internal.scripting;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.internal.scripting.messages"; //$NON-NLS-1$
	public static String TemplateSelectionPage_title;
	public static String TemplateSelectionPage_description;
	public static String TemplateSelectionPage_available_templates;
	public static String TemplateSelectionPage_use_templates_button_text;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
