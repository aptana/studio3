package com.aptana.editor.yaml.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.yaml.preferences.messages"; //$NON-NLS-1$
	public static String YAMLPreferencePage_YAML_Page_Title;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
