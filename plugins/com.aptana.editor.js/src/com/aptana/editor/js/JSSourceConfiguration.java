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

package com.aptana.editor.js;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
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
import com.aptana.editor.js.text.rules.JSRegExpRule;

/**
 * @author Max Stepanov
 * @author cwilliams
 */
public class JSSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	public final static String PREFIX = "__js_"; //$NON-NLS-1$
	public final static String DEFAULT = "__js" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String JS_MULTILINE_COMMENT = PREFIX + "multiline_comment"; //$NON-NLS-1$
	public final static String JS_SINGLELINE_COMMENT = PREFIX + "singleline_comment"; //$NON-NLS-1$
	public final static String JS_DOC = PREFIX + "sdoc"; //$NON-NLS-1$
	public final static String STRING_DOUBLE = PREFIX + "string_double"; //$NON-NLS-1$
	public final static String STRING_SINGLE = PREFIX + "string_single"; //$NON-NLS-1$
	public final static String JS_REGEXP = PREFIX + "regexp"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, JS_MULTILINE_COMMENT, JS_SINGLELINE_COMMENT,
			JS_DOC, STRING_DOUBLE, STRING_SINGLE, JS_REGEXP };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IJSConstants.CONTENT_TYPE_JS } };

	private IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new EndOfLineRule("//", new Token(JS_SINGLELINE_COMMENT)), //$NON-NLS-1$
			new SingleLineRule("\"", "\"", new Token(STRING_DOUBLE), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
			new SingleLineRule("\'", "\'", new Token(STRING_SINGLE), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
			new EmptyCommentRule(new Token(JS_MULTILINE_COMMENT)),
			new MultiLineRule("/**", "*/", new Token(JS_DOC), (char) 0, true), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("/*", "*/", new Token(JS_MULTILINE_COMMENT), (char) 0, true), //$NON-NLS-1$ //$NON-NLS-2$
			new JSRegExpRule(new Token(JS_REGEXP)) };

	private JSCodeScanner codeScanner;
	private JSDocScanner docScanner;
	private JSEscapeSequenceScanner singleQuoteScanner;
	private JSEscapeSequenceScanner doubleQuoteScanner;
	private JSEscapeSequenceScanner regexpScanner;
	private RuleBasedScanner multiLineCommentScanner;
	private RuleBasedScanner singleLineCommentScanner;

	private static JSSourceConfiguration instance;

	static
	{
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		c.addTranslation(new QualifiedContentType(IJSConstants.CONTENT_TYPE_JS), new QualifiedContentType("source.js")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(STRING_DOUBLE), new QualifiedContentType("string.quoted.double.js")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(STRING_SINGLE), new QualifiedContentType("string.quoted.single.js")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(JS_REGEXP), new QualifiedContentType("string.regexp.js")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(JS_SINGLELINE_COMMENT), new QualifiedContentType(
				"comment.line.double-slash.js")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(JS_MULTILINE_COMMENT), new QualifiedContentType("comment.block.js")); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(JS_DOC), new QualifiedContentType("comment.block.documentation.js")); //$NON-NLS-1$
	}
	
	private JSSourceConfiguration() {
	}

	public static JSSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			instance = new JSSourceConfiguration();
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
			return IJSConstants.CONTENT_TYPE_JS;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text
	 * .presentation.PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer)
	{
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(getJSDocScanner());
		reconciler.setDamager(dr, JS_DOC);
		reconciler.setRepairer(dr, JS_DOC);

		dr = new ThemeingDamagerRepairer(getMultiLineCommentScanner());
		reconciler.setDamager(dr, JS_MULTILINE_COMMENT);
		reconciler.setRepairer(dr, JS_MULTILINE_COMMENT);

		dr = new ThemeingDamagerRepairer(getSingleQuotedStringScanner());
		reconciler.setDamager(dr, STRING_SINGLE);
		reconciler.setRepairer(dr, STRING_SINGLE);

		dr = new ThemeingDamagerRepairer(getDoubleQuotedStringScanner());
		reconciler.setDamager(dr, STRING_DOUBLE);
		reconciler.setRepairer(dr, STRING_DOUBLE);

		dr = new ThemeingDamagerRepairer(getSingleLineCommentScanner());
		reconciler.setDamager(dr, JS_SINGLELINE_COMMENT);
		reconciler.setRepairer(dr, JS_SINGLELINE_COMMENT);

		dr = new ThemeingDamagerRepairer(getRegexpScanner());
		reconciler.setDamager(dr, JS_REGEXP);
		reconciler.setRepairer(dr, JS_REGEXP);
	}

	private ITokenScanner getMultiLineCommentScanner()
	{
		if (multiLineCommentScanner == null)
		{
			multiLineCommentScanner = new CommentScanner(getToken("comment.block.js")); //$NON-NLS-1$
		}
		return multiLineCommentScanner;
	}

	private ITokenScanner getSingleLineCommentScanner()
	{
		if (singleLineCommentScanner == null)
		{
			singleLineCommentScanner = new CommentScanner(getToken("comment.line.double-slash.js")); //$NON-NLS-1$
		}
		return singleLineCommentScanner;
	}

	private ITokenScanner getRegexpScanner()
	{
		if (regexpScanner == null)
		{
			regexpScanner = new JSEscapeSequenceScanner("string.regexp.js"); //$NON-NLS-1$
		}
		return regexpScanner;
	}

	private ITokenScanner getDoubleQuotedStringScanner()
	{
		if (doubleQuoteScanner == null)
		{
			doubleQuoteScanner = new JSEscapeSequenceScanner("string.quoted.double.js"); //$NON-NLS-1$
		}
		return doubleQuoteScanner;
	}

	private ITokenScanner getSingleQuotedStringScanner()
	{
		if (singleQuoteScanner == null)
		{
			singleQuoteScanner = new JSEscapeSequenceScanner("string.quoted.single.js"); //$NON-NLS-1$
		}
		return singleQuoteScanner;
	}

	private ITokenScanner getJSDocScanner()
	{
		if (docScanner == null)
		{
			docScanner = new JSDocScanner();
		}
		return docScanner;
	}

	private ITokenScanner getCodeScanner()
	{
		if (codeScanner == null)
		{
			codeScanner = new JSCodeScanner();
		}
		return codeScanner;
	}

	private IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}
}
