/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.yaml;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
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

/**
 * @author Max Stepanov
 */
public class YAMLSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	public final static String PREFIX = "__yaml_"; //$NON-NLS-1$
	public final static String DEFAULT = PREFIX + IDocument.DEFAULT_CONTENT_TYPE;
	public final static String DIRECTIVE = PREFIX + "directive"; //$NON-NLS-1$
	public final static String STRING_SINGLE = PREFIX + "string_single"; //$NON-NLS-1$
	public final static String STRING_DOUBLE = PREFIX + "string_double"; //$NON-NLS-1$
	public final static String INTERPOLATED = PREFIX + "interpolated"; //$NON-NLS-1$
	public final static String COMMENT = PREFIX + "comment"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, DIRECTIVE, COMMENT, STRING_SINGLE,
			STRING_DOUBLE, INTERPOLATED };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IYAMLConstants.CONTENT_TYPE_YAML } };

	private IPredicateRule[] partitioningRules;

	private static YAMLSourceConfiguration instance;

	static
	{
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		c.addTranslation(new QualifiedContentType(IYAMLConstants.CONTENT_TYPE_YAML), new QualifiedContentType(
				IYAMLConstants.YAML_SCOPE));
		c.addTranslation(new QualifiedContentType(COMMENT), new QualifiedContentType(IYAMLConstants.YAML_COMMENT_SCOPE));
		c.addTranslation(new QualifiedContentType(STRING_SINGLE), new QualifiedContentType(
				IYAMLConstants.YAML_STRING_SINGLE_SCOPE));
		c.addTranslation(new QualifiedContentType(STRING_DOUBLE), new QualifiedContentType(
				IYAMLConstants.YAML_STRING_DOUBLE_SCOPE));
		c.addTranslation(new QualifiedContentType(INTERPOLATED), new QualifiedContentType(
				IYAMLConstants.YAML_INTERPOLATED_STRING_SCOPE));
		c.addTranslation(new QualifiedContentType(DIRECTIVE), new QualifiedContentType(
				IYAMLConstants.YAML_DIRECTIVE_SCOPE));
	}

	public static YAMLSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			instance = new YAMLSourceConfiguration();
		}
		return instance;
	}

	private YAMLSourceConfiguration()
	{
		EndOfLineRule directiveRule = new EndOfLineRule("%", getToken(DIRECTIVE)); //$NON-NLS-1$
		directiveRule.setColumnConstraint(0);

		partitioningRules = new IPredicateRule[] { new SingleLineRule("`", "`", getToken(INTERPOLATED), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
				new SingleLineRule("'", "'", getToken(STRING_SINGLE), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
				new SingleLineRule("\"", "\"", getToken(STRING_DOUBLE), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
				new EndOfLineRule("#", getToken(COMMENT)), //$NON-NLS-1$
				directiveRule };
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
		return new SubPartitionScanner(partitioningRules, CONTENT_TYPES, getToken(DEFAULT));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentDefaultContentType()
	 */
	public String getDocumentContentType(String contentType)
	{
		if (contentType.startsWith(PREFIX))
		{
			return IYAMLConstants.CONTENT_TYPE_YAML;
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
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(new YAMLCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, COMMENT);
		reconciler.setRepairer(dr, COMMENT);

		dr = new ThemeingDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, STRING_SINGLE);
		reconciler.setRepairer(dr, STRING_SINGLE);

		dr = new ThemeingDamagerRepairer(getDoubleStringScanner());
		reconciler.setDamager(dr, STRING_DOUBLE);
		reconciler.setRepairer(dr, STRING_DOUBLE);

		dr = new ThemeingDamagerRepairer(getInterpolatedScanner());
		reconciler.setDamager(dr, INTERPOLATED);
		reconciler.setRepairer(dr, INTERPOLATED);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#getContentAssistProcessor(com.aptana.editor.common.AbstractThemeableEditor, java.lang.String)
	 */
	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType)
	{
		return new CommonContentAssistProcessor(editor);
	}

	private ITokenScanner getCommentScanner()
	{
		return new CommentScanner(getToken(IYAMLConstants.YAML_COMMENT_SCOPE));
	}

	private ITokenScanner getStringScanner()
	{
		return new SingleTokenScanner(getToken(IYAMLConstants.YAML_STRING_SINGLE_SCOPE));
	}

	private ITokenScanner getDoubleStringScanner()
	{
		return new SingleTokenScanner(getToken(IYAMLConstants.YAML_STRING_DOUBLE_SCOPE));
	}

	private ITokenScanner getInterpolatedScanner()
	{
		return new SingleTokenScanner(getToken(IYAMLConstants.YAML_INTERPOLATED_STRING_SCOPE));
	}

	private IToken getToken(String name)
	{
		return CommonUtil.getToken(name);
	}
}
