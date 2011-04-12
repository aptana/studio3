/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.SimpleSourceViewerConfiguration;
import com.aptana.editor.common.text.RubyRegexpAutoIndentStrategy;

public class YAMLSourceViewerConfiguration extends SimpleSourceViewerConfiguration
{
	/**
	 * YAMLSourceViewerConfiguration
	 * 
	 * @param preferences
	 * @param editor
	 */
	public YAMLSourceViewerConfiguration(IPreferenceStore preferences, AbstractThemeableEditor editor)
	{
		super(preferences, editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.SimpleSourceViewerConfiguration#getSourceViewerConfiguration()
	 */
	@Override
	public ISourceViewerConfiguration getSourceViewerConfiguration()
	{
		return YAMLSourceConfiguration.getDefault();
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		return new IAutoEditStrategy[] { new RubyRegexpAutoIndentStrategy(contentType, this, sourceViewer, YAMLPlugin
				.getDefault().getPreferenceStore()) };
	}

}
