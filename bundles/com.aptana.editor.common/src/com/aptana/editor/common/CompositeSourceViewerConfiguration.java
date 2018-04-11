/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.SingleTagRule;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;

/**
 * @author Max Stepanov
 */
public abstract class CompositeSourceViewerConfiguration extends CommonSourceViewerConfiguration {

	private ITokenScanner startEndTokenScanner;
	private ISourceViewerConfiguration defaultSourceViewerConfiguration;
	private ISourceViewerConfiguration primarySourceViewerConfiguration;

	private String[][] topContentTypesArray;

	/**
	 * @param defaultSourceViewerConfiguration
	 * @param primarySourceViewerConfiguration
	 * @param preferences
	 * @param editor
	 */
	protected CompositeSourceViewerConfiguration(ISourceViewerConfiguration defaultSourceViewerConfiguration, ISourceViewerConfiguration primarySourceViewerConfiguration,
			IPreferenceStore preferences, AbstractThemeableEditor editor) {
		super(preferences, editor);
		this.defaultSourceViewerConfiguration = defaultSourceViewerConfiguration;
		this.primarySourceViewerConfiguration = primarySourceViewerConfiguration;

		// Compute the top contents types
		String[][] defaultTopContentTypesArray = defaultSourceViewerConfiguration.getTopContentTypes();
		for (int i = 0; i < defaultTopContentTypesArray.length; i++) {
			defaultTopContentTypesArray[i][0] = getTopContentType();
		}
		String[][] primaryContentTypesArray = primarySourceViewerConfiguration.getTopContentTypes();
		for (int i = 0; i < primaryContentTypesArray.length; i++) {
			String[] topContentTypes = primaryContentTypesArray[i];
			primaryContentTypesArray[i] = new String[topContentTypes.length + 1];
			primaryContentTypesArray[i][0] = getTopContentType();
			System.arraycopy(topContentTypes, 0, primaryContentTypesArray[i], 1, topContentTypes.length);
		}
		topContentTypesArray = TextUtils.combineArrays(defaultTopContentTypesArray, primaryContentTypesArray);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredContentTypes(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public final String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return TextUtils.combine(new String[][] { { IDocument.DEFAULT_CONTENT_TYPE }, CompositePartitionScanner.SWITCHING_CONTENT_TYPES,
				primarySourceViewerConfiguration.getContentTypes(), defaultSourceViewerConfiguration.getContentTypes() });
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ITopContentTypesProvider#getTopContentTypes()
	 */
	public String[][] getTopContentTypes() {
		return topContentTypesArray;
	}

	/**
	 * Returns top (document) content type
	 * @return
	 */
	protected abstract String getTopContentType();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = (PresentationReconciler) super.getPresentationReconciler(sourceViewer);

		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getStartEndTokenScanner());
		reconciler.setDamager(dr, CompositePartitionScanner.START_SWITCH_TAG);
		reconciler.setRepairer(dr, CompositePartitionScanner.START_SWITCH_TAG);
		reconciler.setDamager(dr, CompositePartitionScanner.END_SWITCH_TAG);
		reconciler.setRepairer(dr, CompositePartitionScanner.END_SWITCH_TAG);

		defaultSourceViewerConfiguration.setupPresentationReconciler(reconciler, sourceViewer);
		primarySourceViewerConfiguration.setupPresentationReconciler(reconciler, sourceViewer);

		return reconciler;
	}

	/**
	 * Returns switching strategy for this composite partitioners viewer configuration
	 * @return
	 */
	protected abstract IPartitionerSwitchStrategy getPartitionerSwitchStrategy();

	/**
	 * Returns content type of switching start and end tokens
	 * @return
	 */
	protected abstract String getStartEndTokenType();

	private ITokenScanner getStartEndTokenScanner() {
		if (startEndTokenScanner == null) {
			RuleBasedScanner ts = new RuleBasedScanner();
			IToken seqToken = new Token(getStartEndTokenType());
			List<IRule> rules = new ArrayList<IRule>();
			for (String[] pair : getPartitionerSwitchStrategy().getSwitchTagPairs()) {
				rules.add(new SingleTagRule(pair[0], seqToken));
				rules.add(new SingleTagRule(pair[1], seqToken));
			}
			ts.setRules(rules.toArray(new IRule[rules.size()]));
			ts.setDefaultReturnToken(new Token("text")); //$NON-NLS-1$
			startEndTokenScanner = ts;
		}
		return startEndTokenScanner;
	}
}
