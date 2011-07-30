/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
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
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;

public class IDLSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{
	public static final String PREFIX = "__idl__"; //$NON-NLS-1$
	public static final String DEFAULT = "__idl" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String IDL_SINGLELINE_COMMENT = PREFIX + "singleline_comment"; //$NON-NLS-1$
	public static final String IDL_MULTILINE_COMMENT = PREFIX + "multiline_comment"; //$NON-NLS-1$
	public final static String IDL_DOC_COMMENT = PREFIX + "doc_comment"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, IDL_MULTILINE_COMMENT, IDL_SINGLELINE_COMMENT, IDL_DOC_COMMENT };
	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IIDLConstants.CONTENT_TYPE_IDL } };

	private IPredicateRule[] partitioningRules = new IPredicateRule[] { //
		new EndOfLineRule("//", getToken(IDL_SINGLELINE_COMMENT)), //$NON-NLS-1$
		new MultiLineRule("/**", "*/", getToken(IDL_DOC_COMMENT), '\0', true), //$NON-NLS-1$ //$NON-NLS-2$
		new MultiLineRule("/*", "*/", getToken(IDL_MULTILINE_COMMENT), '\0', true) //$NON-NLS-1$ //$NON-NLS-2$
	};

	private static IDLSourceConfiguration instance;

	private IDLSourceConfiguration() {
	}
	
	/**
	 * getDefault
	 * 
	 * @return
	 */
	public static IDLSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();

			c.addTranslation(new QualifiedContentType(IIDLConstants.CONTENT_TYPE_IDL), new QualifiedContentType("source.idl")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(IDL_SINGLELINE_COMMENT), new QualifiedContentType("comment.line.double-slash.idl")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(IDL_DOC_COMMENT), new QualifiedContentType("comment.block.documentation.idl")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(IDL_MULTILINE_COMMENT), new QualifiedContentType("comment.block.idl")); //$NON-NLS-1$

			instance = new IDLSourceConfiguration();
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
			return IIDLConstants.CONTENT_TYPE_IDL;
		}

		return null;
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
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(new IDLSourceScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		DefaultDamagerRepairer docCommentDR = new ThemeingDamagerRepairer(new CommentScanner(getToken("comment.block.documentation.idl"))); //$NON-NLS-1$
		reconciler.setDamager(docCommentDR, IDL_DOC_COMMENT);
		reconciler.setRepairer(docCommentDR, IDL_DOC_COMMENT);

		DefaultDamagerRepairer multilineCommentDR = new ThemeingDamagerRepairer(new CommentScanner(getToken("comment.block.idl"))); //$NON-NLS-1$
		reconciler.setDamager(multilineCommentDR, IDL_MULTILINE_COMMENT);
		reconciler.setRepairer(multilineCommentDR, IDL_MULTILINE_COMMENT);

		DefaultDamagerRepairer singlelineCommentDR = new ThemeingDamagerRepairer(new CommentScanner(getToken("comment.line.double-slash.idl"))); //$NON-NLS-1$
		reconciler.setDamager(singlelineCommentDR, IDL_SINGLELINE_COMMENT);
		reconciler.setRepairer(singlelineCommentDR, IDL_SINGLELINE_COMMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#getContentAssistProcessor(com.aptana.editor.common.AbstractThemeableEditor, java.lang.String)
	 */
	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType)
	{
		return new CommonContentAssistProcessor(editor);
	}

}
