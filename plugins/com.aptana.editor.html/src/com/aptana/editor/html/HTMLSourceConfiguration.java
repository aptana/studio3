/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

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
import com.aptana.editor.common.text.rules.CaseInsensitiveMultiLineRule;
import com.aptana.editor.common.text.rules.CommentScanner;
import com.aptana.editor.common.text.rules.ExtendedToken;
import com.aptana.editor.common.text.rules.FixedMultiLineRule;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.PartitionerSwitchingIgnoreRule;
import com.aptana.editor.common.text.rules.TagRule;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor;
import com.aptana.editor.html.core.IHTMLConstants;
import com.aptana.editor.html.internal.text.rules.DocTypeRule;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.svg.ISVGConstants;
import com.aptana.editor.svg.SVGSourceConfiguration;
import com.aptana.js.core.IJSConstants;

/**
 * @author Max Stepanov
 */
public class HTMLSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	public final static String PREFIX = "__html_"; //$NON-NLS-1$
	public final static String DEFAULT = "__html" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String HTML_COMMENT = PREFIX + "comment"; //$NON-NLS-1$
	public final static String CDATA = PREFIX + "cdata"; //$NON-NLS-1$
	public final static String HTML_DOCTYPE = PREFIX + "doctype"; //$NON-NLS-1$
	public final static String HTML_SCRIPT = PREFIX + "script"; //$NON-NLS-1$
	public final static String HTML_STYLE = PREFIX + "style"; //$NON-NLS-1$
	public final static String HTML_SVG = PREFIX + "svg"; //$NON-NLS-1$
	public final static String HTML_TAG = PREFIX + "tag"; //$NON-NLS-1$
	public final static String HTML_TAG_CLOSE = PREFIX + "tag_close"; //$NON-NLS-1$

	protected static final String[] CONTENT_TYPES = new String[] { DEFAULT, HTML_COMMENT, CDATA, HTML_DOCTYPE,
			HTML_SCRIPT, HTML_STYLE, HTML_SVG, HTML_TAG, HTML_TAG_CLOSE };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IHTMLConstants.CONTENT_TYPE_HTML },
			{ IHTMLConstants.CONTENT_TYPE_HTML, IJSConstants.CONTENT_TYPE_JS },
			{ IHTMLConstants.CONTENT_TYPE_HTML, ICSSConstants.CONTENT_TYPE_CSS },
			{ IHTMLConstants.CONTENT_TYPE_HTML, ISVGConstants.CONTENT_TYPE_SVG } };

	private IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new CaseInsensitiveMultiLineRule("<!DOCTYPE ", ">", getToken(HTML_DOCTYPE)), //$NON-NLS-1$ //$NON-NLS-2$
			new DocTypeRule(getToken(CDATA)),
			new PartitionerSwitchingIgnoreRule(new FixedMultiLineRule("<!--", "-->", getToken(HTML_COMMENT), (char) 0, true)), //$NON-NLS-1$ //$NON-NLS-2$
			new TagRule("script", new ExtendedToken(getToken(HTML_SCRIPT)), true), //$NON-NLS-1$
			new TagRule("style", new ExtendedToken(getToken(HTML_STYLE)), true), //$NON-NLS-1$
			new TagRule("svg", new ExtendedToken(getToken(HTML_SVG)), true), //$NON-NLS-1$
			new TagRule("/", getToken(HTML_TAG_CLOSE)), //$NON-NLS-1$
			new TagRule(new ExtendedToken(getToken(HTML_TAG))) };

	private static HTMLSourceConfiguration instance;

	static
	{
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		// Top-level HTML
		c.addTranslation(new QualifiedContentType(IHTMLConstants.CONTENT_TYPE_HTML), new QualifiedContentType(
				"text.html.basic")); //$NON-NLS-1$
		// Embedded CSS and JS
		c.addTranslation(new QualifiedContentType(IHTMLConstants.CONTENT_TYPE_HTML, ICSSConstants.CONTENT_TYPE_CSS),
				new QualifiedContentType("text.html.basic", "source.css.embedded.html")); //$NON-NLS-1$ //$NON-NLS-2$
		c.addTranslation(new QualifiedContentType(IHTMLConstants.CONTENT_TYPE_HTML, IJSConstants.CONTENT_TYPE_JS),
				new QualifiedContentType("text.html.basic", "source.js.embedded.html")); //$NON-NLS-1$ //$NON-NLS-2$
		c.addTranslation(new QualifiedContentType(IHTMLConstants.CONTENT_TYPE_HTML, ISVGConstants.CONTENT_TYPE_SVG),
				new QualifiedContentType("text.html.basic", "source.svg.embedded.html")); //$NON-NLS-1$ //$NON-NLS-2$
		// Partitions
		c.addTranslation(new QualifiedContentType(HTML_COMMENT), new QualifiedContentType("comment.block.html")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(HTML_TAG), new QualifiedContentType("meta.tag.block.any.html")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(HTML_TAG_CLOSE), new QualifiedContentType("meta.tag.block.any.html")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(HTML_SCRIPT), new QualifiedContentType("meta.tag.block.any.html")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(HTML_STYLE), new QualifiedContentType("meta.tag.block.any.html")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(HTML_SVG), new QualifiedContentType("meta.tag.block.any.html")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(CDATA), new QualifiedContentType("string.unquoted.cdata.xml")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(HTML_DOCTYPE), new QualifiedContentType(
				"meta.tag.sgml.html", "meta.tag.sgml.doctype.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private HTMLSourceConfiguration()
	{
	}

	public synchronized static HTMLSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			instance = new HTMLSourceConfiguration();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes()
	{
		return TextUtils.combine(new String[][] { CONTENT_TYPES, JSSourceConfiguration.CONTENT_TYPES,
				CSSSourceConfiguration.CONTENT_TYPES, SVGSourceConfiguration.CONTENT_TYPES });
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
		return new HTMLSubPartitionScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentContentType(java.lang.String)
	 */
	public String getDocumentContentType(String contentType)
	{
		if (contentType.startsWith(PREFIX))
		{
			return IHTMLConstants.CONTENT_TYPE_HTML;
		}
		String result = JSSourceConfiguration.getDefault().getDocumentContentType(contentType);
		if (result != null)
		{
			return result;
		}
		result = CSSSourceConfiguration.getDefault().getDocumentContentType(contentType);
		if (result != null)
		{
			return result;
		}
		result = SVGSourceConfiguration.getDefault().getDocumentContentType(contentType);
		if (result != null)
		{
			return result;
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
		JSSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);
		CSSSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);
		SVGSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);

		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getHTMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(getHTMLTagScanner());
		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_SCRIPT);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_SCRIPT);

		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_STYLE);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_STYLE);

		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_SVG);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_SVG);

		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_TAG);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_TAG);

		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_TAG_CLOSE);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_TAG_CLOSE);

		dr = new ThemeingDamagerRepairer(getHTMLCommentScanner());
		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_COMMENT);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_COMMENT);

		dr = new ThemeingDamagerRepairer(getDoctypeScanner());
		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_DOCTYPE);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_DOCTYPE);

		dr = new ThemeingDamagerRepairer(getCDATAScanner());
		reconciler.setDamager(dr, CDATA);
		reconciler.setRepairer(dr, CDATA);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#getContentAssistProcessor
	 * (com.aptana.editor.common.AbstractThemeableEditor, java.lang.String)
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
		if (contentType.startsWith(SVGSourceConfiguration.PREFIX))
		{
			return SVGSourceConfiguration.getDefault().getContentAssistProcessor(editor, contentType);
		}
		return new HTMLContentAssistProcessor(editor);
	}

	private ITokenScanner getHTMLCommentScanner()
	{
		return new CommentScanner(getToken("comment.block.html")); //$NON-NLS-1$
	}

	private ITokenScanner getHTMLScanner()
	{
		return new HTMLScanner();
	}

	private ITokenScanner getCDATAScanner()
	{
		return new SingleTokenScanner(getToken("string.unquoted.cdata.xml")); //$NON-NLS-1$
	}

	private ITokenScanner getHTMLTagScanner()
	{
		return new HTMLTagScanner();
	}

	private ITokenScanner getDoctypeScanner()
	{
		return new HTMLDoctypeScanner();
	}

	private static IToken getToken(String tokenName)
	{
		return CommonUtil.getToken(tokenName);
	}

}
