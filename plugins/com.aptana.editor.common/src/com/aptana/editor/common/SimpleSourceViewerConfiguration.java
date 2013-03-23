/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * SimpleSourceViewerConfiguration
 */
public abstract class SimpleSourceViewerConfiguration extends CommonSourceViewerConfiguration {

	/**
	 * SimpleSourceViewerConfiguration
	 * 
	 * @param preferenceStore
	 * @param editor
	 */
	protected SimpleSourceViewerConfiguration(IPreferenceStore preferenceStore, AbstractThemeableEditor editor) {
		super(preferenceStore, editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#
	 * getConfiguredContentTypes(org.eclipse.jface.text.source .ISourceViewer)
	 */
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return TextUtils.combine(new String[][] { { IDocument.DEFAULT_CONTENT_TYPE }, this.getSourceViewerConfiguration().getContentTypes() });
	}

	/**
	 * Return the source viewer configuration for this language
	 * 
	 * @return
	 */
	public abstract ISourceViewerConfiguration getSourceViewerConfiguration();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = (PresentationReconciler) super.getPresentationReconciler(sourceViewer);
		ISourceViewerConfiguration configuration = this.getSourceViewerConfiguration();

		configuration.setupPresentationReconciler(reconciler, sourceViewer);

		return reconciler;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ITopContentTypesProvider#getTopContentTypes()
	 */
	public String[][] getTopContentTypes() {
		return this.getSourceViewerConfiguration().getTopContentTypes();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonSourceViewerConfiguration#getContentAssistProcessor(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	@Override
	protected IContentAssistProcessor getContentAssistProcessor(ISourceViewer sourceViewer, String contentType) {
		return getSourceViewerConfiguration().getContentAssistProcessor(getEditor(), contentType);
	}
}
