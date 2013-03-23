/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.dtd.core.IDTDConstants;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.CaseInsensitiveMultiLineRule;
import com.aptana.editor.common.text.rules.CommentScanner;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.MultiCharacterRule;
import com.aptana.editor.common.text.rules.NonRuleBasedDamagerRepairer;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.TagRule;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.dtd.text.rules.DTDTagScanner;

public class DTDSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration {
	
	public static final String PREFIX = "__dtd__"; //$NON-NLS-1$
	public static final String DEFAULT = "__dtd" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String PROLOG = PREFIX + "prolog"; //$NON-NLS-1$
	public final static String PI = PREFIX + "pi"; //$NON-NLS-1$
	public static final String COMMENT = PREFIX + "comment"; //$NON-NLS-1$
	public static final String TAG = PREFIX + "tag"; //$NON-NLS-1$
	public static final String SECTION = PREFIX + "section"; //$NON-NLS-1$
	public static final String CDATA = PREFIX + "cdata"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, PROLOG, PI, COMMENT, TAG, SECTION, CDATA };
	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IDTDConstants.CONTENT_TYPE_DTD } };

	private IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new CaseInsensitiveMultiLineRule("<?xml", "?>", getToken(PROLOG)), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("<?", "?>", getToken(PI)), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("<!--", "-->", getToken(COMMENT), '\0', true), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("<![CDATA[", "]]>", getToken(CDATA)), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("<![", "[", getToken(SECTION)), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiCharacterRule("]]>", getToken(SECTION)), //$NON-NLS-1$
			new TagRule("!", getToken(TAG)), //$NON-NLS-1$
	};

	private static DTDSourceConfiguration instance;

	private DTDSourceConfiguration() {
	}

	/**
	 * getDefault
	 * 
	 * @return
	 */
	public static DTDSourceConfiguration getDefault() {
		if (instance == null) {
			IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();

			c.addTranslation(new QualifiedContentType(IDTDConstants.CONTENT_TYPE_DTD), new QualifiedContentType("source.dtd")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(PROLOG), new QualifiedContentType("meta.tag.preprocessor.xml")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(PI), new QualifiedContentType("meta.tag.preprocessor.xml")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(COMMENT), new QualifiedContentType("comment.block.multiline.dtd")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(TAG), new QualifiedContentType("tag.dtd")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(SECTION), new QualifiedContentType("section.dtd")); //$NON-NLS-1$

			instance = new DTDSourceConfiguration();
		}

		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#createSubPartitionScanner()
	 */
	public ISubPartitionScanner createSubPartitionScanner() {
		return new SubPartitionScanner(partitioningRules, CONTENT_TYPES, getToken(DEFAULT));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes() {
		return CONTENT_TYPES;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentContentType(java.lang.String)
	 */
	public String getDocumentContentType(String contentType) {
		if (contentType.startsWith(PREFIX)) {
			return IDTDConstants.CONTENT_TYPE_DTD;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getPartitioningRules()
	 */
	public IPredicateRule[] getPartitioningRules() {
		return partitioningRules;
	}

	/**
	 * getToken
	 * 
	 * @param tokenName
	 * @return
	 */
	private static IToken getToken(String tokenName) {
		return CommonUtil.getToken(tokenName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ITopContentTypesProvider#getTopContentTypes()
	 */
	public String[][] getTopContentTypes() {
		return TOP_CONTENT_TYPES;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text.presentation.PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer) {
		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(getToken("#text")); //$NON-NLS-1$
		reconciler.setDamager(ndr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(ndr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(ndr, DEFAULT);
		reconciler.setRepairer(ndr, DEFAULT);

		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getPIScanner());
		reconciler.setDamager(dr, PI);
		reconciler.setRepairer(dr, PI);

		reconciler.setDamager(dr, PROLOG);
		reconciler.setRepairer(dr, PROLOG);

		dr = new ThemeingDamagerRepairer(getDTDTagScanner());
		reconciler.setDamager(dr, TAG);
		reconciler.setRepairer(dr, TAG);

		reconciler.setDamager(dr, SECTION);
		reconciler.setRepairer(dr, SECTION);

		dr = new ThemeingDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, COMMENT);
		reconciler.setRepairer(dr, COMMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#getContentAssistProcessor(com.aptana.editor.common.AbstractThemeableEditor, java.lang.String)
	 */
	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType) {
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType) || DEFAULT.equals(contentType)) {
			return new CommonContentAssistProcessor(editor);
		}
		return null;
	}

	private ITokenScanner getDTDTagScanner() {
		return new DTDTagScanner();
	}

	private ITokenScanner getCommentScanner() {
		return new CommentScanner(getToken("comment.block.dtd")); //$NON-NLS-1$
	}

	private ITokenScanner getPIScanner() {
		DTDTagScanner piScanner = new DTDTagScanner();
		piScanner.setDefaultReturnToken(getToken("meta.tag.preprocessor.xml")); //$NON-NLS-1$
		return piScanner;
	}

}
