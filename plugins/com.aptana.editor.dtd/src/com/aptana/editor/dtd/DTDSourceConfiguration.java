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
package com.aptana.editor.dtd;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.NonRuleBasedDamagerRepairer;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;

public class DTDSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{
	public static final String PREFIX = "__dtd__"; //$NON-NLS-1$
	public static final String DEFAULT = "__dtd" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public static final String DTD_COMMENT = PREFIX + "comment"; //$NON-NLS-1$
	public final static String STRING_DOUBLE = PREFIX + "string_double"; //$NON-NLS-1$
	public final static String STRING_SINGLE = PREFIX + "string_single"; //$NON-NLS-1$

	// TODO: add other content types
	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, DTD_COMMENT, STRING_DOUBLE, STRING_SINGLE };
	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IDTDConstants.CONTENT_TYPE_DTD } };

	private IPredicateRule[] partitioningRules = new IPredicateRule[] { new MultiLineRule("<!--", "-->", new Token(DTD_COMMENT), '\0', true), //$NON-NLS-1$ //$NON-NLS-2$
		new MultiLineRule("\"", "\"", new Token(STRING_DOUBLE), '\0', true), //$NON-NLS-1$ //$NON-NLS-2$
		new MultiLineRule("\'", "\'", new Token(STRING_SINGLE), '\0', true) //$NON-NLS-1$ //$NON-NLS-2$
	};
	private DTDSourceScanner dtdScanner;

	private static DTDSourceConfiguration instance;

	/**
	 * getDefault
	 * 
	 * @return
	 */
	public static DTDSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();

			c.addTranslation(new QualifiedContentType(IDTDConstants.CONTENT_TYPE_DTD), new QualifiedContentType("source.dtd")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(DTD_COMMENT), new QualifiedContentType("comment.block.multiline.dtd")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(STRING_DOUBLE), new QualifiedContentType("string.quoted.double.dtd")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(STRING_SINGLE), new QualifiedContentType("string.quoted.single.dtd")); //$NON-NLS-1$

			instance = new DTDSourceConfiguration();
		}

		return instance;
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
			return IDTDConstants.CONTENT_TYPE_DTD;
		}

		return null;
	}

	/**
	 * getDTDScanner
	 * 
	 * @return
	 */
	protected ITokenScanner getDTDScanner()
	{
		if (dtdScanner == null)
		{
			dtdScanner = new DTDSourceScanner();
		}

		return dtdScanner;
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

		NonRuleBasedDamagerRepairer commentDR = new NonRuleBasedDamagerRepairer(this.getToken("comment.block.dtd")); //$NON-NLS-1$
		reconciler.setDamager(commentDR, DTD_COMMENT);
		reconciler.setRepairer(commentDR, DTD_COMMENT);

		NonRuleBasedDamagerRepairer singleQuotedStringDR = new NonRuleBasedDamagerRepairer(this.getToken("string.quoted.single.dtd")); //$NON-NLS-1$
		reconciler.setDamager(singleQuotedStringDR, STRING_SINGLE);
		reconciler.setRepairer(singleQuotedStringDR, STRING_SINGLE);

		NonRuleBasedDamagerRepairer doubleQuotedStringDR = new NonRuleBasedDamagerRepairer(this.getToken("string.quoted.double.dtd")); //$NON-NLS-1$
		reconciler.setDamager(doubleQuotedStringDR, STRING_DOUBLE);
		reconciler.setRepairer(doubleQuotedStringDR, STRING_DOUBLE);
	}
}
