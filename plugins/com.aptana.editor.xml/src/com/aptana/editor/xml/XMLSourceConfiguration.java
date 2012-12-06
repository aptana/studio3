/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.xml;

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
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.TextUtils;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.SingleTokenScanner;
import com.aptana.editor.common.text.rules.CommentScanner;
import com.aptana.editor.common.text.rules.ExtendedToken;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.TagRule;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.dtd.DTDSourceConfiguration;
import com.aptana.editor.xml.contentassist.XMLContentAssistProcessor;
import com.aptana.editor.xml.internal.text.rules.DocTypeRule;
import com.aptana.xml.core.IXMLConstants;
import com.aptana.xml.core.IXMLScopes;

/**
 * @author Max Stepanov
 */
public class XMLSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	public final static String PREFIX = "__xml_"; //$NON-NLS-1$
	public final static String DEFAULT = "__xml" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String COMMENT = PREFIX + "comment"; //$NON-NLS-1$
	public final static String CDATA = PREFIX + "cdata"; //$NON-NLS-1$
	public final static String PRE_PROCESSOR = PREFIX + "pre_processor"; //$NON-NLS-1$
	public final static String TAG = PREFIX + "tag"; //$NON-NLS-1$
	public final static String DOCTYPE = PREFIX + "doctype"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, COMMENT, CDATA, PRE_PROCESSOR, TAG, DOCTYPE };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IXMLConstants.CONTENT_TYPE_XML },
			{ IXMLConstants.CONTENT_TYPE_XML, IDTDConstants.CONTENT_TYPE_DTD } };

	private final IPredicateRule[] partitioningRules = new IPredicateRule[] { //
	new MultiLineRule("<?", "?>", getToken(PRE_PROCESSOR)), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("<!--", "-->", getToken(COMMENT), (char) 0, true), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("<![CDATA[", "]]>", getToken(CDATA)), //$NON-NLS-1$ //$NON-NLS-2$
			new DocTypeRule(new ExtendedToken(getToken(DOCTYPE)), true), new TagRule("/", getToken(TAG)), //$NON-NLS-1$
			new TagRule(new ExtendedToken(getToken(TAG))), //
	};

	private static XMLSourceConfiguration instance;

	private XMLSourceConfiguration()
	{
	}

	public synchronized static XMLSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();

			// Embedded DTD
			c.addTranslation(new QualifiedContentType(IXMLConstants.CONTENT_TYPE_XML, IDTDConstants.CONTENT_TYPE_DTD),
					new QualifiedContentType(IXMLScopes.TEXT_XML, IXMLScopes.SOURCE_DTD_EMBEDDED_XML));

			c.addTranslation(new QualifiedContentType(IXMLConstants.CONTENT_TYPE_XML), new QualifiedContentType(
					IXMLScopes.TEXT_XML));
			c.addTranslation(new QualifiedContentType(COMMENT), new QualifiedContentType(IXMLScopes.COMMENT_BLOCK_XML));
			c.addTranslation(new QualifiedContentType(PRE_PROCESSOR), new QualifiedContentType(
					IXMLScopes.META_TAG_PREPROCESSOR_XML));
			c.addTranslation(new QualifiedContentType(TAG), new QualifiedContentType(IXMLScopes.META_TAG_XML));
			c.addTranslation(new QualifiedContentType(CDATA), new QualifiedContentType(
					IXMLScopes.STRING_UNQUOTED_CDATA_XML));
			c.addTranslation(new QualifiedContentType(DOCTYPE), new QualifiedContentType(
					IXMLScopes.META_TAG_SGML_DOCTYPE_XML));

			instance = new XMLSourceConfiguration();
		}

		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes()
	{
		return TextUtils.combine(new String[][] { CONTENT_TYPES, DTDSourceConfiguration.CONTENT_TYPES });
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
		return new XMLSubPartitionScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentContentType(java.lang.String)
	 */
	public String getDocumentContentType(String contentType)
	{
		if (contentType.startsWith(PREFIX))
		{
			return IXMLConstants.CONTENT_TYPE_XML;
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
		DTDSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);

		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(getPreProcessorScanner());
		reconciler.setDamager(dr, PRE_PROCESSOR);
		reconciler.setRepairer(dr, PRE_PROCESSOR);

		dr = new ThemeingDamagerRepairer(getCDATAScanner());
		reconciler.setDamager(dr, CDATA);
		reconciler.setRepairer(dr, CDATA);

		dr = new ThemeingDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(dr, TAG);
		reconciler.setRepairer(dr, TAG);

		dr = new ThemeingDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, COMMENT);
		reconciler.setRepairer(dr, COMMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#getContentAssistProcessor(com.aptana.editor.common.
	 * AbstractThemeableEditor, java.lang.String)
	 */
	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType)
	{
		if (contentType.startsWith(DTDSourceConfiguration.PREFIX))
		{
			return DTDSourceConfiguration.getDefault().getContentAssistProcessor(editor, contentType);
		}
		return new XMLContentAssistProcessor(editor);
	}

	private ITokenScanner getCommentScanner()
	{
		return new CommentScanner(getToken(IXMLScopes.COMMENT_BLOCK_XML));
	}

	private ITokenScanner getPreProcessorScanner()
	{
		XMLTagScanner preProcessorScanner = new XMLTagScanner();
		preProcessorScanner.setDefaultReturnToken(getToken(IXMLScopes.META_TAG_PREPROCESSOR_XML));
		return preProcessorScanner;
	}

	private ITokenScanner getCDATAScanner()
	{
		return new SingleTokenScanner(getToken(IXMLScopes.STRING_UNQUOTED_CDATA_XML));
	}

	private ITokenScanner getXMLScanner()
	{
		return new XMLScanner();
	}

	private ITokenScanner getXMLTagScanner()
	{
		return new XMLTagScanner();
	}

	private static IToken getToken(String tokenName)
	{
		return CommonUtil.getToken(tokenName);
	}

}
