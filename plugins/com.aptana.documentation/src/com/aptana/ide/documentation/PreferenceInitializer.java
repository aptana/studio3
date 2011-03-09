package com.aptana.ide.documentation;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	private static final String DEFAULT_GETTING_STARTED_URL = "http://content.aptana.com/aptana/tutorials/index.php"; //$NON-NLS-1$

	@Override
	public void initializeDefaultPreferences()
	{
		new DefaultScope().getNode(DocumentationPlugin.PLUGIN_ID).put(DocumentationPlugin.GETTING_STARTED_CONTENT_URL, DEFAULT_GETTING_STARTED_URL);
	}

}
