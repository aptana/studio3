/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg;

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
import com.aptana.editor.common.TextUtils;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.SingleTokenScanner;
import com.aptana.editor.common.text.rules.CommentScanner;
import com.aptana.editor.common.text.rules.ExtendedToken;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.TagRule;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.svg.contentassist.SVGContentAssistProcessor;
import com.aptana.editor.xml.XMLScanner;
import com.aptana.editor.xml.XMLSourceConfiguration;
import com.aptana.editor.xml.XMLTagScanner;
import com.aptana.js.core.IJSConstants;
import com.aptana.xml.core.IXMLConstants;

/**
 * SVGSourceConfiguration
 */
public class SVGSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{
	public final static String PREFIX = "__svg_"; //$NON-NLS-1$
	public final static String DEFAULT = "__svg" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String COMMENT = PREFIX + "comment"; //$NON-NLS-1$
	public final static String CDATA = PREFIX + "cdata"; //$NON-NLS-1$
	public final static String PRE_PROCESSOR = PREFIX + "processing_instruction"; //$NON-NLS-1$
	public final static String TAG = PREFIX + "tag"; //$NON-NLS-1$
	public final static String DOCTYPE = PREFIX + "doctype"; //$NON-NLS-1$
	public final static String SCRIPT = PREFIX + "script"; //$NON-NLS-1$
	public final static String STYLE = PREFIX + "style"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, COMMENT, CDATA, PRE_PROCESSOR, DOCTYPE, SCRIPT, STYLE, TAG };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { //
		{ ISVGConstants.CONTENT_TYPE_SVG }, //
		{ ISVGConstants.CONTENT_TYPE_SVG, IJSConstants.CONTENT_TYPE_JS }, //
		{ ISVGConstants.CONTENT_TYPE_SVG, ICSSConstants.CONTENT_TYPE_CSS } //
	};

	private IPredicateRule[] partitioningRules = new IPredicateRule[] { //
		new MultiLineRule("<!DOCTYPE", ">", getToken(CDATA)), //$NON-NLS-1$ //$NON-NLS-2$
		new MultiLineRule("<?", "?>", getToken(PRE_PROCESSOR)), //$NON-NLS-1$ //$NON-NLS-2$
		new MultiLineRule("<!--", "-->", getToken(COMMENT), (char) 0, true), //$NON-NLS-1$ //$NON-NLS-2$
		new MultiLineRule("<![CDATA[", "]]>", getToken(CDATA)), //$NON-NLS-1$ //$NON-NLS-2$
		new TagRule("script", new ExtendedToken(getToken(SCRIPT))), //$NON-NLS-1$
		new TagRule("style", new ExtendedToken(getToken(STYLE))), //$NON-NLS-1$
		new TagRule("/", getToken(TAG)), //$NON-NLS-1$
		new TagRule(new ExtendedToken(getToken(TAG))) //
	};

	private static SVGSourceConfiguration instance;

	/**
	 * static initializer
	 */
	static
	{
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();

		c.addTranslation(new QualifiedContentType(ISVGConstants.CONTENT_TYPE_SVG), new QualifiedContentType("text.xml.svg")); //$NON-NLS-1$

		// embed JS
		c.addTranslation(new QualifiedContentType(ISVGConstants.CONTENT_TYPE_SVG, IJSConstants.CONTENT_TYPE_JS), new QualifiedContentType("text.xml.svg", "source.js.embedded.svg")); //$NON-NLS-1$ //$NON-NLS-2$
		c.addTranslation(new QualifiedContentType(ISVGConstants.CONTENT_TYPE_SVG, ICSSConstants.CONTENT_TYPE_CSS), new QualifiedContentType("text.xml.svg", "source.css.embedded.svg")); //$NON-NLS-1$ //$NON-NLS-2$

		// partitions
		c.addTranslation(new QualifiedContentType(COMMENT), new QualifiedContentType("comment.block.xml.svg")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(PRE_PROCESSOR), new QualifiedContentType("meta.tag.preprocessor.xml.svg")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(TAG), new QualifiedContentType("meta.tag.xml.svg")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(SCRIPT), new QualifiedContentType("meta.tag.block.any.xml.svg")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(STYLE), new QualifiedContentType("meta.tag.block.any.xml.svg")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(CDATA), new QualifiedContentType("string.unquoted.cdata.xml.svg")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(DOCTYPE), new QualifiedContentType("meta.tag.sgml.doctype.xml.svg")); //$NON-NLS-1$
	}

	private SVGSourceConfiguration() {
	}
	
	/**
	 * getDefault
	 * 
	 * @return
	 */
	public static SVGSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			instance = new SVGSourceConfiguration();
		}

		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#createSubPartitionScanner()
	 */
	public ISubPartitionScanner createSubPartitionScanner()
	{
		return new SVGSubPartitionerScanner();
	}

	/**
	 * getCDATAScanner
	 * 
	 * @return
	 */
	private ITokenScanner getCDATAScanner()
	{
		return new SingleTokenScanner(getToken("string.unquoted.cdata.xml.svg")); //$NON-NLS-1$
	}

	/**
	 * getCommentScanner
	 * 
	 * @return
	 */
	private ITokenScanner getCommentScanner()
	{
		return new CommentScanner(getToken("comment.block.xml.svg")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes()
	{
		return TextUtils.combine(new String[][] { CONTENT_TYPES, JSSourceConfiguration.CONTENT_TYPES, CSSSourceConfiguration.CONTENT_TYPES });
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentDefaultContentType()
	 */
	public String getDocumentContentType(String contentType)
	{
		if (contentType.startsWith(PREFIX))
		{
			return IXMLConstants.CONTENT_TYPE_XML;
		}
		else
		{
			String result = JSSourceConfiguration.getDefault().getDocumentContentType(contentType);

			if (result == null)
			{
				result = CSSSourceConfiguration.getDefault().getDocumentContentType(contentType);
			}

			return result;
		}
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
	 * getPreProcessorScanner
	 * 
	 * @return
	 */
	private ITokenScanner getPreProcessorScanner()
	{
		XMLTagScanner preProcessorScanner = new XMLTagScanner();

		preProcessorScanner.setDefaultReturnToken(getToken("meta.tag.preprocessor.xml.svg")); //$NON-NLS-1$

		return preProcessorScanner;
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

	/**
	 * getXMLScanner
	 * 
	 * @return
	 */
	private ITokenScanner getXMLScanner()
	{
		return new XMLScanner();
	}

	/**
	 * getXMLTagScanner
	 * 
	 * @return
	 */
	private ITokenScanner getXMLTagScanner()
	{
		return new XMLTagScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text
	 * .presentation.PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer)
	{
		JSSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);
		CSSSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);

		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		DefaultDamagerRepairer preprocessorScanner = new ThemeingDamagerRepairer(getPreProcessorScanner());
		reconciler.setDamager(preprocessorScanner, PRE_PROCESSOR);
		reconciler.setRepairer(preprocessorScanner, PRE_PROCESSOR);

		DefaultDamagerRepairer cdataScanner = new ThemeingDamagerRepairer(getCDATAScanner());
		reconciler.setDamager(cdataScanner, CDATA);
		reconciler.setRepairer(cdataScanner, CDATA);

		DefaultDamagerRepairer tagScanner = new ThemeingDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(tagScanner, SCRIPT);
		reconciler.setRepairer(tagScanner, SCRIPT);
		reconciler.setDamager(tagScanner, STYLE);
		reconciler.setRepairer(tagScanner, STYLE);
		reconciler.setDamager(tagScanner, TAG);
		reconciler.setRepairer(tagScanner, TAG);

		DefaultDamagerRepairer commentScanner = new ThemeingDamagerRepairer(getCommentScanner());
		reconciler.setDamager(commentScanner, XMLSourceConfiguration.COMMENT);
		reconciler.setRepairer(commentScanner, XMLSourceConfiguration.COMMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#getContentAssistProcessor(com.aptana.editor.common.AbstractThemeableEditor, java.lang.String)
	 */
	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType)
	{
		if (contentType.startsWith(JSSourceConfiguration.PREFIX))
		{
			return JSSourceConfiguration.getDefault().getContentAssistProcessor(editor, contentType);
		}
		if (contentType.startsWith(CSSSourceConfiguration.PREFIX))
		{
			return CSSSourceConfiguration.getDefault().getContentAssistProcessor(editor, contentType);
		}
		return new SVGContentAssistProcessor(editor);
	}

}
