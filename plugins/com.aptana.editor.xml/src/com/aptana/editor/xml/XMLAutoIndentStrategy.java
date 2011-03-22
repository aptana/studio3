/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.aptana.editor.common.text.RubyRegexpAutoIndentStrategy;
import com.aptana.editor.xml.preferences.IPreferenceConstants;

public class XMLAutoIndentStrategy extends RubyRegexpAutoIndentStrategy
{
	private static boolean shouldAutoIndent;
	private static IPreferenceChangeListener autoIndentPrefChangeListener;

	static
	{
		XMLAutoIndentStrategy.autoIndentPrefChangeListener = new IPreferenceChangeListener()
		{

			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (IPreferenceConstants.XML_AUTO_INDENT.equals(event.getKey()))
					updateAutoIndentPreference();
			}
		};
		new InstanceScope().getNode(XMLPlugin.PLUGIN_ID).addPreferenceChangeListener(autoIndentPrefChangeListener);

		XMLPlugin.getDefault().getBundle().getBundleContext().addBundleListener(new BundleListener()
		{

			public void bundleChanged(BundleEvent event)
			{
				if (event.getType() == BundleEvent.STOPPING)
					new InstanceScope().getNode(XMLPlugin.PLUGIN_ID).removePreferenceChangeListener(
							autoIndentPrefChangeListener);
			}
		});

	}

	public XMLAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration, ISourceViewer sourceViewer)
	{
		super(contentType, configuration, sourceViewer);
		updateAutoIndentPreference();
	}

	@Override
	protected boolean shouldAutoIndent()
	{
		return shouldAutoIndent;
	}

	private static void updateAutoIndentPreference()
	{
		shouldAutoIndent = XMLPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.XML_AUTO_INDENT);
	}

}
