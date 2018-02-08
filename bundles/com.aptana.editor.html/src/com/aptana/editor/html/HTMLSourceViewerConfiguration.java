/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.SimpleSourceViewerConfiguration;
import com.aptana.editor.common.TextUtils;
import com.aptana.editor.common.text.RubyRegexpAutoIndentStrategy;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.svg.SVGSourceConfiguration;

public class HTMLSourceViewerConfiguration extends SimpleSourceViewerConfiguration {

	private HTMLDoubleClickStrategy fDoubleClickStrategy;

	/**
	 * HTMLSourceViewerConfiguration
	 * 
	 * @param preferences
	 * @param editor
	 */
	public HTMLSourceViewerConfiguration(IPreferenceStore preferences, AbstractThemeableEditor editor) {
		super(preferences, editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.SimpleSourceViewerConfiguration#getConfiguredContentTypes(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return TextUtils.combine(new String[][] { { IDocument.DEFAULT_CONTENT_TYPE }, HTMLSourceConfiguration.CONTENT_TYPES, JSSourceConfiguration.CONTENT_TYPES,
				CSSSourceConfiguration.CONTENT_TYPES, SVGSourceConfiguration.CONTENT_TYPES });
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonSourceViewerConfiguration#getHyperlinkDetectorTargets(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		Map targets = super.getHyperlinkDetectorTargets(sourceViewer);

		targets.put("com.aptana.editor.html", getEditor()); //$NON-NLS-1$

		return targets;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.SimpleSourceViewerConfiguration#getSourceViewerConfiguration()
	 */
	@Override
	public ISourceViewerConfiguration getSourceViewerConfiguration() {
		return HTMLSourceConfiguration.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonSourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		return new IAutoEditStrategy[] { new RubyRegexpAutoIndentStrategy(contentType, this, sourceViewer, HTMLPlugin.getDefault().getPreferenceStore()) };
	}

	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{
		if (fDoubleClickStrategy == null)
		{
			fDoubleClickStrategy = new HTMLDoubleClickStrategy();
		}
		return fDoubleClickStrategy;
	}

}
