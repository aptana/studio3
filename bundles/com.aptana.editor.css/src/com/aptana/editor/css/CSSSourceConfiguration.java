/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.css;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.css.core.ICSSConstants;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.SingleTokenScanner;
import com.aptana.editor.common.text.rules.CommentScanner;
import com.aptana.editor.common.text.rules.EmptyCommentRule;
import com.aptana.editor.common.text.rules.ExtendedToken;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.ResumableSingleLineRule;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.css.contentassist.CSSContentAssistProcessor;

/**
 * @author Max Stepanov
 */
public class CSSSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	public final static String PREFIX = "__css_"; //$NON-NLS-1$
	public final static String DEFAULT = PREFIX + IDocument.DEFAULT_CONTENT_TYPE;
	public final static String STRING_SINGLE = PREFIX + "string_single"; //$NON-NLS-1$
	public final static String STRING_DOUBLE = PREFIX + "string_double"; //$NON-NLS-1$
	public final static String MULTILINE_COMMENT = PREFIX + "multiline_comment"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, MULTILINE_COMMENT, STRING_SINGLE,
			STRING_DOUBLE };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { ICSSConstants.CONTENT_TYPE_CSS } };

	private IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new ResumableSingleLineRule("\"", "\"", new ExtendedToken(getToken(STRING_DOUBLE)), '\\', true), //$NON-NLS-1$ //$NON-NLS-2$
			new ResumableSingleLineRule("\'", "\'", new ExtendedToken(getToken(STRING_SINGLE)), '\\', true), //$NON-NLS-1$ //$NON-NLS-2$
			new EmptyCommentRule(getToken(MULTILINE_COMMENT)),
			new MultiLineRule("/*", "*/", getToken(MULTILINE_COMMENT), (char) 0, true) //$NON-NLS-1$ //$NON-NLS-2$
	};

	private static CSSSourceConfiguration instance;

	static
	{
		if (CommonEditorPlugin.getDefault() != null)
		{
			IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
			c.addTranslation(new QualifiedContentType(ICSSConstants.CONTENT_TYPE_CSS), new QualifiedContentType(
					ICSSConstants.CSS_SCOPE));
			c.addTranslation(new QualifiedContentType(MULTILINE_COMMENT), new QualifiedContentType(
					ICSSConstants.CSS_COMMENT_BLOCK_SCOPE));
			c.addTranslation(new QualifiedContentType(STRING_DOUBLE), new QualifiedContentType(
					ICSSConstants.CSS_STRING_SCOPE));
			c.addTranslation(new QualifiedContentType(STRING_SINGLE), new QualifiedContentType(
					ICSSConstants.CSS_STRING_SCOPE));
		}
	}

	public static CSSSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			instance = new CSSSourceConfiguration();
		}
		return instance;
	}

	private CSSSourceConfiguration()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes()
	{
		return CONTENT_TYPES;
	}

	public String[][] getTopContentTypes()
	{
		return TOP_CONTENT_TYPES;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getPartitioningRules()
	 */
	public IPredicateRule[] getPartitioningRules()
	{
		return partitioningRules;
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
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentContentType(java.lang.String)
	 */
	public String getDocumentContentType(String contentType)
	{
		if (contentType.startsWith(PREFIX))
		{
			return ICSSConstants.CONTENT_TYPE_CSS;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text.presentation
	 * .PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer)
	{
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(new CSSCodeScannerFlex());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, MULTILINE_COMMENT);
		reconciler.setRepairer(dr, MULTILINE_COMMENT);

		dr = new ThemeingDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, STRING_DOUBLE);
		reconciler.setRepairer(dr, STRING_DOUBLE);

		dr = new ThemeingDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, STRING_SINGLE);
		reconciler.setRepairer(dr, STRING_SINGLE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#getContentAssistProcessor(com.aptana.editor.common.
	 * AbstractThemeableEditor, java.lang.String)
	 */
	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType)
	{
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType) || CSSSourceConfiguration.DEFAULT.equals(contentType))
		{
			return new CSSContentAssistProcessor(editor);
		}
		return null;
	}

	private ITokenScanner getCommentScanner()
	{
		return new CommentScanner(getToken(ICSSConstants.CSS_COMMENT_BLOCK_SCOPE));
	}

	private ITokenScanner getStringScanner()
	{
		return new SingleTokenScanner(getToken(ICSSConstants.CSS_STRING_SCOPE));
	}

	private static IToken getToken(String tokenName)
	{
		return CommonUtil.getToken(tokenName);
	}

}
