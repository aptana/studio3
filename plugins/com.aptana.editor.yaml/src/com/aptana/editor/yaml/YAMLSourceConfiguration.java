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

package com.aptana.editor.yaml;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
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

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, DIRECTIVE, COMMENT, STRING_SINGLE, STRING_DOUBLE,
			INTERPOLATED };

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
		EndOfLineRule directiveRule = new EndOfLineRule("%", new Token(DIRECTIVE)); //$NON-NLS-1$
		directiveRule.setColumnConstraint(0);
		
		partitioningRules = new IPredicateRule[] { 
				new SingleLineRule("`", "`", new Token(INTERPOLATED), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
				new SingleLineRule("'", "'", new Token(STRING_SINGLE), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
				new SingleLineRule("\"", "\"", new Token(STRING_DOUBLE), '\\'), //$NON-NLS-1$ //$NON-NLS-2$
				new EndOfLineRule("#", new Token(COMMENT)), //$NON-NLS-1$
				directiveRule
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

	protected ITokenScanner getCommentScanner()
	{
		return new CommentScanner(getToken(IYAMLConstants.YAML_COMMENT_SCOPE));
	}

	protected ITokenScanner getStringScanner()
	{
		RuleBasedScanner stringScanner = new RuleBasedScanner();
		stringScanner.setDefaultReturnToken(getToken(IYAMLConstants.YAML_STRING_SINGLE_SCOPE));
		return stringScanner;
	}

	protected ITokenScanner getDoubleStringScanner()
	{
		RuleBasedScanner stringScanner = new RuleBasedScanner();
		stringScanner.setDefaultReturnToken(getToken(IYAMLConstants.YAML_STRING_DOUBLE_SCOPE));
		return stringScanner;
	}

	protected ITokenScanner getInterpolatedScanner()
	{
		RuleBasedScanner stringScanner = new RuleBasedScanner();
		stringScanner.setDefaultReturnToken(getToken(IYAMLConstants.YAML_INTERPOLATED_STRING_SCOPE));
		return stringScanner;
	}

	protected IToken getToken(String name)
	{
		return new Token(name);
	}
}
