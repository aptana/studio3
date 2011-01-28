/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.css;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.CommentScanner;
import com.aptana.editor.common.text.rules.EmptyCommentRule;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;

/**
 * @author Max Stepanov
 */
public class CSSSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	public final static String PREFIX = "__css_"; //$NON-NLS-1$
	public final static String DEFAULT = PREFIX + IDocument.DEFAULT_CONTENT_TYPE;
	public final static String STRING = PREFIX + "string"; //$NON-NLS-1$
	public final static String MULTILINE_COMMENT = PREFIX + "multiline_comment"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, MULTILINE_COMMENT, STRING };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { ICSSConstants.CONTENT_TYPE_CSS } };

	private IToken stringToken = new Token(STRING);

	private IPredicateRule[] partitioningRules;

	private RuleBasedScanner multilineCommentScanner;
	private RuleBasedScanner stringScanner;

	private static CSSSourceConfiguration instance;

	static
	{
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		c.addTranslation(new QualifiedContentType(ICSSConstants.CONTENT_TYPE_CSS), new QualifiedContentType(
				ICSSConstants.CSS_SCOPE));
		c.addTranslation(new QualifiedContentType(MULTILINE_COMMENT), new QualifiedContentType(
				ICSSConstants.CSS_COMMENT_BLOCK_SCOPE));
		c.addTranslation(new QualifiedContentType(STRING), new QualifiedContentType(ICSSConstants.CSS_STRING_SCOPE));
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

		IToken comment = new Token(MULTILINE_COMMENT);

		partitioningRules = new IPredicateRule[] { new SingleLineRule("\"", "\"", stringToken, '\\'), //$NON-NLS-1$ //$NON-NLS-2$
				new SingleLineRule("\'", "\'", stringToken, '\\'), //$NON-NLS-1$ //$NON-NLS-2$
				new EmptyCommentRule(comment), new MultiLineRule("/*", "*/", comment, (char) 0, true) //$NON-NLS-1$ //$NON-NLS-2$
		};
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
		return new SubPartitionScanner(partitioningRules, CONTENT_TYPES, new Token(DEFAULT));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentDefaultContentType()
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
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(new CSSCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, MULTILINE_COMMENT);
		reconciler.setRepairer(dr, MULTILINE_COMMENT);

		dr = new ThemeingDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, STRING);
		reconciler.setRepairer(dr, STRING);
	}

	private ITokenScanner getCommentScanner()
	{
		if (multilineCommentScanner == null)
		{
			multilineCommentScanner = new CommentScanner(getToken(ICSSConstants.CSS_COMMENT_BLOCK_SCOPE));
		}
		return multilineCommentScanner;
	}

	private ITokenScanner getStringScanner()
	{
		if (stringScanner == null)
		{
			stringScanner = new RuleBasedScanner();
			stringScanner.setDefaultReturnToken(getToken(ICSSConstants.CSS_STRING_SCOPE));
		}
		return stringScanner;
	}

	private IToken getToken(String name)
	{
		return new Token(name);
	}
}
