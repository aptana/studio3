/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.text.RubyRegexpAutoIndentStrategy;

public class HTMLAutoIndentStrategy extends RubyRegexpAutoIndentStrategy
{
	private static boolean shouldAutoIndent;

	static
	{
		addPreferenceListener(HTMLPlugin.PLUGIN_ID, HTMLPlugin.getDefault().getBundle().getBundleContext(),
				new Runnable()
				{
					public void run()
					{
						updateAutoIndentPreference();
					}
				});
	}

	public HTMLAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
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
		shouldAutoIndent = HTMLPlugin.getDefault().getPreferenceStore()
				.getBoolean(IPreferenceConstants.EDITOR_AUTO_INDENT);
	}

}
