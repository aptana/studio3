/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

package com.aptana.editor.xml;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.ISubPartitionScanner;
import com.aptana.editor.common.NonRuleBasedDamagerRepairer;
import com.aptana.editor.common.SubPartitionScanner;
import com.aptana.editor.common.theme.ThemeUtil;

/**
 * @author Max Stepanov
 */
public class XMLSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	public final static String PREFIX = "__xml_"; //$NON-NLS-1$
	public final static String DEFAULT = "__xml" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String XML_COMMENT = "__xml_comment"; //$NON-NLS-1$
	public final static String STRING_DOUBLE = "__xml_string_double"; //$NON-NLS-1$
	public final static String STRING_SINGLE = "__xml_string_single"; //$NON-NLS-1$
	public final static String CDATA = "__xml_cdata"; //$NON-NLS-1$
	public final static String PRE_PROCESSOR = "__xml_pre_processor"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] {
			DEFAULT,
			XML_COMMENT,
			STRING_SINGLE,
			STRING_DOUBLE,
			CDATA,
			PRE_PROCESSOR
		};

	private IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new SingleLineRule("<?", "?>", new Token(PRE_PROCESSOR)), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("<!--", "-->", new Token(XML_COMMENT)), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("\"", "\"", new Token(STRING_DOUBLE), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("\'", "\'", new Token(STRING_SINGLE), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("<![CDATA[", "]]>", new Token(CDATA))}; //$NON-NLS-1$ //$NON-NLS-2$

	private XMLScanner xmlScanner;
	private RuleBasedScanner doubleQuotedStringScanner;
	private RuleBasedScanner singleQuotedStringScanner;
	private RuleBasedScanner cdataScanner;
	private RuleBasedScanner preProcessorScanner;

	private static XMLSourceConfiguration instance;

	public static XMLSourceConfiguration getDefault()
	{
		if (instance == null)
		{
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
		return CONTENT_TYPES;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getPartitioningRules()
	 */
	public IPredicateRule[] getPartitioningRules()
	{
		return partitioningRules;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#createSubPartitionScanner()
	 */
	public ISubPartitionScanner createSubPartitionScanner() {
		return new SubPartitionScanner(partitioningRules, CONTENT_TYPES, new Token(DEFAULT));
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentDefaultContentType()
	 */
	public String getDocumentContentType(String contentType) {
		if (contentType.startsWith(PREFIX)) {
			return IXMLConstants.CONTENT_TYPE_XML;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text
	 * .presentation.PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer)
	{
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new DefaultDamagerRepairer(getPreProcessorScanner());
		reconciler.setDamager(dr, PRE_PROCESSOR);
		reconciler.setRepairer(dr, PRE_PROCESSOR);
		
		dr = new DefaultDamagerRepairer(getCDATAScanner());
		reconciler.setDamager(dr, CDATA);
		reconciler.setRepairer(dr, CDATA);

		dr = new DefaultDamagerRepairer(getSingleQuotedStringScanner());
		reconciler.setDamager(dr, STRING_SINGLE);
		reconciler.setRepairer(dr, STRING_SINGLE);

		dr = new DefaultDamagerRepairer(getDoubleQuotedStringScanner());
		reconciler.setDamager(dr, STRING_DOUBLE);
		reconciler.setRepairer(dr, STRING_DOUBLE);

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(ThemeUtil.getToken("comment.block.xml")); //$NON-NLS-1$
		reconciler.setDamager(ndr, XMLSourceConfiguration.XML_COMMENT);
		reconciler.setRepairer(ndr, XMLSourceConfiguration.XML_COMMENT);
	}

	private ITokenScanner getPreProcessorScanner()
	{
		if (preProcessorScanner == null)
		{
			preProcessorScanner = new RuleBasedScanner();
			preProcessorScanner.setDefaultReturnToken(ThemeUtil.getToken("meta.tag.preprocessor.xml")); //$NON-NLS-1$
		}
		return preProcessorScanner;
	}
	
	private ITokenScanner getCDATAScanner()
	{
		if (cdataScanner == null)
		{
			cdataScanner = new RuleBasedScanner();
			cdataScanner.setDefaultReturnToken(ThemeUtil.getToken("string.unquoted.cdata.xml")); //$NON-NLS-1$
		}
		return cdataScanner;
	}

	private ITokenScanner getDoubleQuotedStringScanner()
	{
		if (doubleQuotedStringScanner == null)
		{
			doubleQuotedStringScanner = new RuleBasedScanner();
			doubleQuotedStringScanner.setDefaultReturnToken(ThemeUtil.getToken("string.quoted.double.xml")); //$NON-NLS-1$
		}
		return doubleQuotedStringScanner;
	}

	private ITokenScanner getSingleQuotedStringScanner()
	{
		if (singleQuotedStringScanner == null)
		{
			singleQuotedStringScanner = new RuleBasedScanner();
			singleQuotedStringScanner.setDefaultReturnToken(ThemeUtil.getToken("string.quoted.single.xml")); //$NON-NLS-1$
		}
		return singleQuotedStringScanner;
	}

	protected ITokenScanner getXMLScanner()
	{
		if (xmlScanner == null)
		{
			xmlScanner = new XMLScanner();
		}
		return xmlScanner;
	}

}
