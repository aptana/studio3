/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.SimpleSourceViewerConfiguration;

public class DTDSourceViewerConfiguration extends SimpleSourceViewerConfiguration {
	
	/**
	 * DTDSourceViewerConfiguration
	 * 
	 * @param preferences
	 * @param editor
	 */
	public DTDSourceViewerConfiguration(IPreferenceStore preferences, AbstractThemeableEditor editor) {
		super(preferences, editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.SimpleSourceViewerConfiguration#getSourceViewerConfiguration()
	 */
	@Override
	public ISourceViewerConfiguration getSourceViewerConfiguration() {
		return DTDSourceConfiguration.getDefault();
	}
}
