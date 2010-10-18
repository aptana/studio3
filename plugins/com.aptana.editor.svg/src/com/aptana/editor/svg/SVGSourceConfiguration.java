/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.svg;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.TextUtils;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.CommentScanner;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.TagRule;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.xml.IXMLConstants;
import com.aptana.editor.xml.XMLScanner;
import com.aptana.editor.xml.XMLSourceConfiguration;
import com.aptana.editor.xml.XMLTagScanner;

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

	public static final String[] CONTENT_TYPES = new String[] { //
		DEFAULT, //
		COMMENT, //
		CDATA, //
		PRE_PROCESSOR, //
		DOCTYPE, //
		SCRIPT, //
		STYLE, //
		TAG, //
	};

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { //
		{ ISVGConstants.CONTENT_TYPE_SVG }, //
		{ ISVGConstants.CONTENT_TYPE_SVG, IJSConstants.CONTENT_TYPE_JS }, //
		{ ISVGConstants.CONTENT_TYPE_SVG, ICSSConstants.CONTENT_TYPE_CSS } //
	};

	private IPredicateRule[] partitioningRules = new IPredicateRule[] { //
		new MultiLineRule("<!DOCTYPE", ">", new Token(CDATA)), //$NON-NLS-1$ //$NON-NLS-2$
		new MultiLineRule("<?", "?>", new Token(PRE_PROCESSOR)), //$NON-NLS-1$ //$NON-NLS-2$
		new MultiLineRule("<!--", "-->", new Token(COMMENT), (char) 0, true), //$NON-NLS-1$ //$NON-NLS-2$
		new MultiLineRule("<![CDATA[", "]]>", new Token(CDATA)), //$NON-NLS-1$ //$NON-NLS-2$
		new TagRule("script", new Token(SCRIPT)), //$NON-NLS-1$
		new TagRule("style", new Token(STYLE)), //$NON-NLS-1$
		new TagRule("/", new Token(TAG)), //$NON-NLS-1$
		new TagRule(new Token(TAG)) //
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
		RuleBasedScanner cdataScanner = new RuleBasedScanner();

		cdataScanner.setDefaultReturnToken(getToken("string.unquoted.cdata.xml.svg")); //$NON-NLS-1$

		return cdataScanner;
	}

	/**
	 * getCommentScanner
	 * 
	 * @return
	 */
	protected ITokenScanner getCommentScanner()
	{
		return new CommentScanner(getToken("comment.block.xml.svg")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes()
	{
		return TextUtils.combine( //
			new String[][] { //
				CONTENT_TYPES, //
				JSSourceConfiguration.CONTENT_TYPES, //
				CSSSourceConfiguration.CONTENT_TYPES //
			} //
		);
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
	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
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
	protected ITokenScanner getXMLScanner()
	{
		return new XMLScanner();
	}

	/**
	 * getXMLTagScanner
	 * 
	 * @return
	 */
	protected ITokenScanner getXMLTagScanner()
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
}
