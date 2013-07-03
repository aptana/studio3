/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.SingleTokenScanner;
import com.aptana.editor.common.text.rules.CommentScanner;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.js.text.JSEscapeSequenceScanner;
import com.aptana.editor.js.text.rules.JSRegExpRule;

/**
 * @author Max Stepanov
 * @author cwilliams
 */
public class CoffeeSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	public final static String PREFIX = "__coffee_"; //$NON-NLS-1$
	public final static String DEFAULT = "__coffee" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String SINGLELINE_COMMENT = PREFIX + "singleline_comment"; //$NON-NLS-1$
	public final static String MULTILINE_COMMENT = PREFIX + "block_comment"; //$NON-NLS-1$
	public final static String STRING_DOUBLE = PREFIX + "string_double"; //$NON-NLS-1$
	public final static String STRING_SINGLE = PREFIX + "string_single"; //$NON-NLS-1$
	public final static String REGEXP = PREFIX + "regexp"; //$NON-NLS-1$
	public static final String HEREDOC = PREFIX + "heredoc"; //$NON-NLS-1$
	public static final String DOUBLE_HEREDOC = PREFIX + "heredoc_double"; //$NON-NLS-1$
	public static final String COMMAND = PREFIX + "command"; //$NON-NLS-1$
	public static final String HEREGEX = PREFIX + "heregex"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, SINGLELINE_COMMENT, MULTILINE_COMMENT,
			STRING_DOUBLE, STRING_SINGLE, REGEXP, HEREDOC, DOUBLE_HEREDOC, COMMAND, HEREGEX };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { ICoffeeConstants.CONTENT_TYPE_COFFEE } };

	private IPredicateRule[] partitioningRules = new IPredicateRule[] {
			// Special rule to avoid matching multiline comments for more than 3 #
			new EndOfLineRule("####", getToken(SINGLELINE_COMMENT)), //$NON-NLS-1$
			new MultiLineRule("###", "###", getToken(MULTILINE_COMMENT)), //$NON-NLS-1$ //$NON-NLS-2$
			new EndOfLineRule("#", getToken(SINGLELINE_COMMENT)), //$NON-NLS-1$			
			new MultiLineRule("`", "`", getToken(COMMAND), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("'''", "'''", getToken(HEREDOC)), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("\"\"\"", "\"\"\"", getToken(DOUBLE_HEREDOC)), //$NON-NLS-1$ //$NON-NLS-2$
			new SingleLineRule("\"", "\"", getToken(STRING_DOUBLE), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
			new SingleLineRule("\'", "\'", getToken(STRING_SINGLE), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("///", "///", getToken(HEREGEX)), //$NON-NLS-1$ //$NON-NLS-2$
			new JSRegExpRule(new Token(REGEXP)) };

	private static CoffeeSourceConfiguration instance;

	static
	{
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		c.addTranslation(new QualifiedContentType(ICoffeeConstants.CONTENT_TYPE_COFFEE), new QualifiedContentType(
				ICoffeeScopeConstants.TOPLEVEL));
		c.addTranslation(new QualifiedContentType(HEREDOC), new QualifiedContentType(
				ICoffeeScopeConstants.STRING_HEREDOC_SINGLE));
		c.addTranslation(new QualifiedContentType(DOUBLE_HEREDOC), new QualifiedContentType(
				ICoffeeScopeConstants.STRING_HEREDOC_DOUBLE));
		c.addTranslation(new QualifiedContentType(STRING_SINGLE), new QualifiedContentType(
				ICoffeeScopeConstants.STRING_SINGLE));
		c.addTranslation(new QualifiedContentType(STRING_DOUBLE), new QualifiedContentType(
				ICoffeeScopeConstants.STRING_DOUBLE));
		c.addTranslation(new QualifiedContentType(COMMAND), new QualifiedContentType(ICoffeeScopeConstants.COMMAND));
		c.addTranslation(new QualifiedContentType(MULTILINE_COMMENT), new QualifiedContentType(
				ICoffeeScopeConstants.COMMENT_BLOCK));
		c.addTranslation(new QualifiedContentType(SINGLELINE_COMMENT), new QualifiedContentType(
				ICoffeeScopeConstants.COMMENT_LINE));
		c.addTranslation(new QualifiedContentType(HEREGEX), new QualifiedContentType(ICoffeeScopeConstants.REGEXP));
		c.addTranslation(new QualifiedContentType(REGEXP), new QualifiedContentType(ICoffeeScopeConstants.REGEXP));
	}

	private CoffeeSourceConfiguration()
	{
	}

	public static CoffeeSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			instance = new CoffeeSourceConfiguration();
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
		return new SubPartitionScanner(getPartitioningRules(), CONTENT_TYPES, getToken(DEFAULT));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentContentType(java.lang.String)
	 */
	public String getDocumentContentType(String contentType)
	{
		if (contentType.startsWith(PREFIX))
		{
			return ICoffeeConstants.CONTENT_TYPE_COFFEE;
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
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(getBlockCommentScanner());
		reconciler.setDamager(dr, MULTILINE_COMMENT);
		reconciler.setRepairer(dr, MULTILINE_COMMENT);

		dr = new ThemeingDamagerRepairer(getSingleQuotedStringScanner());
		reconciler.setDamager(dr, STRING_SINGLE);
		reconciler.setRepairer(dr, STRING_SINGLE);

		dr = new ThemeingDamagerRepairer(getDoubleQuotedStringScanner());
		reconciler.setDamager(dr, STRING_DOUBLE);
		reconciler.setRepairer(dr, STRING_DOUBLE);

		dr = new ThemeingDamagerRepairer(getSingleLineCommentScanner());
		reconciler.setDamager(dr, SINGLELINE_COMMENT);
		reconciler.setRepairer(dr, SINGLELINE_COMMENT);

		dr = new ThemeingDamagerRepairer(getRegexpScanner());
		reconciler.setDamager(dr, REGEXP);
		reconciler.setRepairer(dr, REGEXP);

		dr = new ThemeingDamagerRepairer(getHeredocScanner());
		reconciler.setDamager(dr, HEREDOC);
		reconciler.setRepairer(dr, HEREDOC);

		dr = new ThemeingDamagerRepairer(getDoubleHeredocScanner());
		reconciler.setDamager(dr, DOUBLE_HEREDOC);
		reconciler.setRepairer(dr, DOUBLE_HEREDOC);

		dr = new ThemeingDamagerRepairer(getCommandScanner());
		reconciler.setDamager(dr, COMMAND);
		reconciler.setRepairer(dr, COMMAND);

		dr = new ThemeingDamagerRepairer(getHeregexScanner());
		reconciler.setDamager(dr, HEREGEX);
		reconciler.setRepairer(dr, HEREGEX);
	}

	private ITokenScanner getDoubleQuotedStringScanner()
	{
		// TODO Need to handle character escapes and interpolation (like in ruby)!
		return new JSEscapeSequenceScanner(ICoffeeScopeConstants.STRING_DOUBLE);
	}

	private ITokenScanner getDoubleHeredocScanner()
	{
		// TODO Need to handle character escapes and interpolation (like in ruby)!
		return new JSEscapeSequenceScanner(ICoffeeScopeConstants.STRING_HEREDOC_DOUBLE);
	}

	private ITokenScanner getRegexpScanner()
	{
		return new JSEscapeSequenceScanner(ICoffeeScopeConstants.REGEXP);
	}

	private ITokenScanner getHeregexScanner()
	{
		// TODO Need to handle allowing singleline comments inside!
		return getRegexpScanner();
	}

	private ITokenScanner getCommandScanner()
	{
		return new SingleTokenScanner(getToken(ICoffeeScopeConstants.COMMAND));
	}

	private ITokenScanner getSingleQuotedStringScanner()
	{
		return new SingleTokenScanner(getToken(ICoffeeScopeConstants.STRING_SINGLE));
	}

	private ITokenScanner getHeredocScanner()
	{
		return new SingleTokenScanner(getToken(ICoffeeScopeConstants.STRING_HEREDOC_SINGLE));
	}

	private ITokenScanner getBlockCommentScanner()
	{
		return new CommentScanner(getToken(ICoffeeScopeConstants.COMMENT_BLOCK));
	}

	private ITokenScanner getSingleLineCommentScanner()
	{
		return new CommentScanner(getToken(ICoffeeScopeConstants.COMMENT_LINE));
	}

	private ITokenScanner getCodeScanner()
	{
		return new CoffeeCodeScanner();
	}

	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType)
	{
		// TODO Add CA for coffeescript!
		return new CommonContentAssistProcessor(editor);
	}

	private static IToken getToken(String tokenName)
	{
		return CommonUtil.getToken(tokenName);
	}

}
