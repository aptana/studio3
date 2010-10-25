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

	protected ITokenScanner getCommentScanner()
	{
		if (multilineCommentScanner == null)
		{
			multilineCommentScanner = new CommentScanner(getToken(ICSSConstants.CSS_COMMENT_BLOCK_SCOPE));
		}
		return multilineCommentScanner;
	}

	protected ITokenScanner getStringScanner()
	{
		if (stringScanner == null)
		{
			stringScanner = new RuleBasedScanner();
			stringScanner.setDefaultReturnToken(getToken(ICSSConstants.CSS_STRING_SCOPE));
		}
		return stringScanner;
	}

	protected IToken getToken(String name)
	{
		return new Token(name);
	}
}
