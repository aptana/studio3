/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.json.text.rules.IJSONScopes;
import com.aptana.editor.json.text.rules.JSONPropertyRule;
import com.aptana.json.core.IJSONConstants;

public class JSONSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{
	public static final String PREFIX = "__json__"; //$NON-NLS-1$
	public static final String DEFAULT = "__json" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public static final String STRING_DOUBLE = PREFIX + "string_double"; //$NON-NLS-1$
	public static final String STRING_SINGLE = PREFIX + "string_single"; //$NON-NLS-1$
	public static final String PROPERTY = PREFIX + "property"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, STRING_DOUBLE, STRING_SINGLE, PROPERTY };
	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IJSONConstants.CONTENT_TYPE_JSON } };

	private IPredicateRule[] partitioningRules = new IPredicateRule[] { //
	new JSONPropertyRule( //
			getToken(STRING_SINGLE), //
			getToken(STRING_DOUBLE), //
			getToken(PROPERTY) //
	) //
	};

	private static JSONSourceConfiguration instance;

	private JSONSourceConfiguration()
	{
	}

	/**
	 * getDefault
	 * 
	 * @return
	 */
	public static JSONSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();

			c.addTranslation(new QualifiedContentType(IJSONConstants.CONTENT_TYPE_JSON), new QualifiedContentType(
					IJSONScopes.SOURCE));
			c.addTranslation(new QualifiedContentType(PROPERTY), new QualifiedContentType(IJSONScopes.PROPERTY));
			c.addTranslation(new QualifiedContentType(STRING_DOUBLE), new QualifiedContentType(
					IJSONScopes.STRING_DOUBLE));
			c.addTranslation(new QualifiedContentType(STRING_SINGLE), new QualifiedContentType(
					IJSONScopes.STRING_SINGLE));

			instance = new JSONSourceConfiguration();
		}

		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#createSubPartitionScanner()
	 */
	public ISubPartitionScanner createSubPartitionScanner()
	{
		return new SubPartitionScanner(partitioningRules, CONTENT_TYPES, getToken(DEFAULT));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes()
	{
		return CONTENT_TYPES;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentContentType(java.lang.String)
	 */
	public String getDocumentContentType(String contentType)
	{
		if (contentType.startsWith(PREFIX))
		{
			return IJSONConstants.CONTENT_TYPE_JSON;
		}

		return null;
	}

	/**
	 * getCodeScanner
	 * 
	 * @return
	 */
	private ITokenScanner getCodeScanner()
	{
		return new JSONSourceScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getPartitioningRules()
	 */
	public IPredicateRule[] getPartitioningRules()
	{
		return partitioningRules;
	}

	/**
	 * getToken
	 * 
	 * @param tokenName
	 * @return
	 */
	private static IToken getToken(String tokenName)
	{
		return CommonUtil.getToken(tokenName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ITopContentTypesProvider#getTopContentTypes()
	 */
	public String[][] getTopContentTypes()
	{
		return TOP_CONTENT_TYPES;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text.presentation
	 * .PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer)
	{
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		ThemeingDamagerRepairer p = new ThemeingDamagerRepairer(new JSONEscapeSequenceScanner(IJSONScopes.PROPERTY));
		reconciler.setDamager(p, PROPERTY);
		reconciler.setRepairer(p, PROPERTY);

		ThemeingDamagerRepairer dqs = new ThemeingDamagerRepairer(new JSONEscapeSequenceScanner(
				IJSONScopes.STRING_DOUBLE));
		reconciler.setDamager(dqs, STRING_DOUBLE);
		reconciler.setRepairer(dqs, STRING_DOUBLE);

		ThemeingDamagerRepairer sqs = new ThemeingDamagerRepairer(new JSONEscapeSequenceScanner(
				IJSONScopes.STRING_SINGLE));
		reconciler.setDamager(sqs, STRING_SINGLE);
		reconciler.setRepairer(sqs, STRING_SINGLE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#getContentAssistProcessor(com.aptana.editor.common.
	 * AbstractThemeableEditor, java.lang.String)
	 */
	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType)
	{
		return new CommonContentAssistProcessor(editor);
	}

}
