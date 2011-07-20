/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.beaver;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.CommentScanner;
import com.aptana.editor.common.text.rules.EmptyCommentRule;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.NonRuleBasedDamagerRepairer;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;

public class BeaverSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{
	public static final String PREFIX = "__beaver__"; //$NON-NLS-1$
	public static final String DEFAULT = "__beaver" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String BEAVER_SINGLELINE_COMMENT = PREFIX + "singleline_comment"; //$NON-NLS-1$
	public static final String BEAVER_MULTILINE_COMMENT = PREFIX + "multiline_comment"; //$NON-NLS-1$
	public static final String BEAVER_BLOCK = PREFIX + "block"; //$NON-NLS-1$

	// TODO: add other content types
	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, BEAVER_MULTILINE_COMMENT, BEAVER_SINGLELINE_COMMENT, BEAVER_BLOCK };
	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IBeaverConstants.CONTENT_TYPE_BEAVER } };

	private IPredicateRule[] partitioningRules = new IPredicateRule[] { //
		new EndOfLineRule("//", getToken(BEAVER_SINGLELINE_COMMENT)), //$NON-NLS-1$
		new EmptyCommentRule(getToken(BEAVER_MULTILINE_COMMENT)),
		new MultiLineRule("/*", "*/", getToken(BEAVER_MULTILINE_COMMENT), '\0', true), //$NON-NLS-1$ //$NON-NLS-2$
		new MultiLineRule("{:", ":}", getToken(BEAVER_BLOCK), '\0', true) //$NON-NLS-1$ //$NON-NLS-2$
	};

	private static BeaverSourceConfiguration instance;

	private BeaverSourceConfiguration() {
	}
	
	/**
	 * getDefault
	 * 
	 * @return
	 */
	public static BeaverSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();

			c.addTranslation(new QualifiedContentType(IBeaverConstants.CONTENT_TYPE_BEAVER), new QualifiedContentType("source.beaver")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(BEAVER_SINGLELINE_COMMENT), new QualifiedContentType("comment.line.double-slash.beaver")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(BEAVER_MULTILINE_COMMENT), new QualifiedContentType("comment.block.beaver")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(BEAVER_BLOCK), new QualifiedContentType("source.block.beaver")); //$NON-NLS-1$

			instance = new BeaverSourceConfiguration();
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
			return IBeaverConstants.CONTENT_TYPE_BEAVER;
		}

		return null;
	}

	/**
	 * getDTDScanner
	 * 
	 * @return
	 */
	private ITokenScanner getDTDScanner()
	{
		return new BeaverSourceScanner();
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
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getDTDScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(new CommentScanner(getToken("comment.block.beaver"))); //$NON-NLS-1$
		reconciler.setDamager(dr, BEAVER_MULTILINE_COMMENT);
		reconciler.setRepairer(dr, BEAVER_MULTILINE_COMMENT);

		dr = new ThemeingDamagerRepairer(new CommentScanner(getToken("comment.line.double-slash.beaver"))); //$NON-NLS-1$
		reconciler.setDamager(dr, BEAVER_SINGLELINE_COMMENT);
		reconciler.setRepairer(dr, BEAVER_SINGLELINE_COMMENT);

		NonRuleBasedDamagerRepairer blockDR = new NonRuleBasedDamagerRepairer(getToken("source.block.beaver")); //$NON-NLS-1$
		reconciler.setDamager(blockDR, BEAVER_BLOCK);
		reconciler.setRepairer(blockDR, BEAVER_BLOCK);
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#getContentAssistProcessor(com.aptana.editor.common.AbstractThemeableEditor, java.lang.String)
	 */
	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType)
	{
		return new CommonContentAssistProcessor(editor);
	}
}
