/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.js;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.CommentScanner;
import com.aptana.editor.common.text.rules.EmptyCommentRule;
import com.aptana.editor.common.text.rules.ExtendedToken;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.ResumableSingleLineRule;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.js.contentassist.JSContentAssistProcessor;
import com.aptana.editor.js.text.JSCodeScanner;
import com.aptana.editor.js.text.JSDocScanner;
import com.aptana.editor.js.text.JSEscapeSequenceScanner;
import com.aptana.editor.js.text.rules.JSRegExpRule;
import com.aptana.js.core.IJSConstants;

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
	public final static String JS_DOC = PREFIX + "sdoc_comment"; //$NON-NLS-1$
	public final static String STRING_DOUBLE = PREFIX + "string_double"; //$NON-NLS-1$
	public final static String STRING_SINGLE = PREFIX + "string_single"; //$NON-NLS-1$
	public final static String JS_REGEXP = PREFIX + "regexp"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, JS_MULTILINE_COMMENT, JS_SINGLELINE_COMMENT,
			JS_DOC, STRING_DOUBLE, STRING_SINGLE, JS_REGEXP };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IJSConstants.CONTENT_TYPE_JS } };

	private IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new EndOfLineRule("//", getToken(JS_SINGLELINE_COMMENT)), //$NON-NLS-1$
			new ResumableSingleLineRule("\"", "\"", new ExtendedToken(getToken(STRING_DOUBLE)), '\\', true), //$NON-NLS-1$ //$NON-NLS-2$
			new ResumableSingleLineRule("\'", "\'", new ExtendedToken(getToken(STRING_SINGLE)), '\\', true), //$NON-NLS-1$ //$NON-NLS-2$
			new EmptyCommentRule(getToken(JS_MULTILINE_COMMENT)),
			new MultiLineRule("/**", "*/", getToken(JS_DOC), (char) 0, true), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("/*", "*/", getToken(JS_MULTILINE_COMMENT), (char) 0, true), //$NON-NLS-1$ //$NON-NLS-2$
			new JSRegExpRule(getToken(JS_REGEXP)) };

	private static JSSourceConfiguration INSTANCE;

	static
	{
		CommonEditorPlugin plugin = CommonEditorPlugin.getDefault();
		if (plugin != null) // may be null running test-cases.
		{
			IContentTypeTranslator c = plugin.getContentTypeTranslator();
			c.addTranslation(new QualifiedContentType(IJSConstants.CONTENT_TYPE_JS), new QualifiedContentType(
					"source.js")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(STRING_DOUBLE), new QualifiedContentType(
					"string.quoted.double.js")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(STRING_SINGLE), new QualifiedContentType(
					"string.quoted.single.js")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(JS_REGEXP), new QualifiedContentType("string.regexp.js")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(JS_SINGLELINE_COMMENT), new QualifiedContentType(
					"comment.line.double-slash.js")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(JS_MULTILINE_COMMENT), new QualifiedContentType(
					"comment.block.js")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(JS_DOC), new QualifiedContentType(
					"comment.block.documentation.js")); //$NON-NLS-1$
		}
	}

	private JSSourceConfiguration()
	{
	}

	public static JSSourceConfiguration getDefault()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new JSSourceConfiguration();
		}
		return INSTANCE;
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
		return new SubPartitionScanner(partitioningRules, CONTENT_TYPES, getToken(DEFAULT));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentContentType(java.lang.String)
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#getContentAssistProcessor(com.aptana.editor.common.
	 * AbstractThemeableEditor, java.lang.String)
	 */
	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType)
	{
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType) || JSSourceConfiguration.DEFAULT.equals(contentType))
		{
			return new JSContentAssistProcessor(editor);
		}
		return null;
	}

	private ITokenScanner getMultiLineCommentScanner()
	{
		return new CommentScanner(getToken("comment.block.js")); //$NON-NLS-1$
	}

	private ITokenScanner getSingleLineCommentScanner()
	{
		return new CommentScanner(getToken("comment.line.double-slash.js")); //$NON-NLS-1$
	}

	private ITokenScanner getRegexpScanner()
	{
		return new JSEscapeSequenceScanner("string.regexp.js"); //$NON-NLS-1$
	}

	private ITokenScanner getDoubleQuotedStringScanner()
	{
		return new JSEscapeSequenceScanner("string.quoted.double.js"); //$NON-NLS-1$
	}

	private ITokenScanner getSingleQuotedStringScanner()
	{
		return new JSEscapeSequenceScanner("string.quoted.single.js"); //$NON-NLS-1$
	}

	private ITokenScanner getJSDocScanner()
	{
		return new JSDocScanner();
	}

	private ITokenScanner getCodeScanner()
	{
		return new JSCodeScanner();
	}

	private IToken getToken(String tokenName)
	{
		return CommonUtil.getToken(tokenName);
	}
}
