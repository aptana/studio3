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

package com.aptana.editor.diff;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;

/**
 * @author Max Stepanov
 */
public class DiffSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	/**
	 * Scope names
	 */
	private static final String META_SEPARATOR_DIFF = "meta.separator.diff"; //$NON-NLS-1$
	private static final String META_DIFF_RANGE = "meta.diff.range"; //$NON-NLS-1$
	private static final String META_DIFF_INDEX = "meta.diff.index"; //$NON-NLS-1$
	private static final String META_DIFF_HEADER = "meta.diff.header"; //$NON-NLS-1$
	private static final String MARKUP_DELETED_DIFF = "markup.deleted.diff"; //$NON-NLS-1$
	private static final String MARKUP_CHANGED_DIFF = "markup.changed.diff"; //$NON-NLS-1$
	private static final String MARKUP_INSERTED_DIFF = "markup.inserted.diff"; //$NON-NLS-1$
	private static final String SOURCE_DIFF = "source.diff"; //$NON-NLS-1$

	/**
	 * Partition names
	 */
	public final static String PREFIX = "__diff_"; //$NON-NLS-1$
	public final static String DEFAULT = PREFIX + IDocument.DEFAULT_CONTENT_TYPE;
	public final static String INSERTED = PREFIX + "inserted"; //$NON-NLS-1$
	public final static String CHANGED = PREFIX + "changed"; //$NON-NLS-1$
	public final static String DELETED = PREFIX + "deleted"; //$NON-NLS-1$
	public final static String HEADER = PREFIX + "header"; //$NON-NLS-1$
	public final static String INDEX = PREFIX + "index"; //$NON-NLS-1$
	public final static String RANGE = PREFIX + "range"; //$NON-NLS-1$
	public final static String SEPARATOR = PREFIX + "separator"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, INSERTED, CHANGED, DELETED, HEADER, INDEX,
			RANGE, SEPARATOR };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IDiffConstants.CONTENT_TYPE_DIFF } };

	private IPredicateRule[] partitioningRules;

	private static DiffSourceConfiguration instance;

	static
	{
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		c.addTranslation(new QualifiedContentType(IDiffConstants.CONTENT_TYPE_DIFF), new QualifiedContentType(
				SOURCE_DIFF));
		c.addTranslation(new QualifiedContentType(INSERTED), new QualifiedContentType(MARKUP_INSERTED_DIFF));
		c.addTranslation(new QualifiedContentType(CHANGED), new QualifiedContentType(MARKUP_CHANGED_DIFF));
		c.addTranslation(new QualifiedContentType(DELETED), new QualifiedContentType(MARKUP_DELETED_DIFF));
		c.addTranslation(new QualifiedContentType(HEADER), new QualifiedContentType(META_DIFF_HEADER));
		c.addTranslation(new QualifiedContentType(INDEX), new QualifiedContentType(META_DIFF_INDEX));
		c.addTranslation(new QualifiedContentType(RANGE), new QualifiedContentType(META_DIFF_RANGE));
		c.addTranslation(new QualifiedContentType(SEPARATOR), new QualifiedContentType(META_SEPARATOR_DIFF));
	}

	public static DiffSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			instance = new DiffSourceConfiguration();
		}
		return instance;
	}

	@SuppressWarnings("nls")
	private DiffSourceConfiguration()
	{
		Token changed = new Token(CHANGED);
		Token deleted = new Token(DELETED);
		Token inserted = new Token(INSERTED);
		Token header = new Token(HEADER);
		Token separator = new Token(SEPARATOR);
		Token range = new Token(RANGE);
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

		PatternRule rule;

		// Index
		rule = new EndOfLineRule("Index: ", new Token(INDEX));
		rule.setColumnConstraint(0);
		rules.add(rule);

		// Range
		rule = new EndOfLineRule("@@", range);
		rule.setColumnConstraint(0);
		rules.add(rule);

		// rule = new PatternRule("--- ", " ----\n", range, (char) 0, false);
		// rule.setColumnConstraint(0);
		// rules.add(rule);
		//
		// rule = new SingleLineRule("*** ", " ****", range, (char) 0, true);
		// rule.setColumnConstraint(0);
		// rules.add(rule);

		// headers
		rule = new EndOfLineRule("--- ", header);
		rule.setColumnConstraint(0);
		rules.add(rule);

		rule = new EndOfLineRule("*** ", header);
		rule.setColumnConstraint(0);
		rules.add(rule);

		rule = new EndOfLineRule("==== ", header);
		rule.setColumnConstraint(0);
		rules.add(rule);

		rule = new EndOfLineRule("+++ ", header);
		rule.setColumnConstraint(0);
		rules.add(rule);

		rule = new SingleLineRule(" - ", " ====", header, (char) 0, true);
		rule.setColumnConstraint(0);
		rules.add(rule);

		// Separator
		rule = new PatternRule("===================================================================", null, separator,
				(char) 0, true);
		rule.setColumnConstraint(0);
		rules.add(rule);

		rule = new PatternRule("***************", null, separator, (char) 0, true);
		rule.setColumnConstraint(0);
		rules.add(rule);

		rule = new PatternRule("---", null, separator, (char) 0, true);
		rule.setColumnConstraint(0);
		rules.add(rule);

		// Inserted
		rule = new EndOfLineRule("+", inserted);
		rule.setColumnConstraint(0);
		rules.add(rule);

		rule = new EndOfLineRule(">", inserted);
		rule.setColumnConstraint(0);
		rules.add(rule);

		// Changed
		rule = new EndOfLineRule("!", changed);
		rule.setColumnConstraint(0);
		rules.add(rule);

		// Deleted
		rule = new EndOfLineRule("-", deleted);
		rule.setColumnConstraint(0);
		rules.add(rule);

		rule = new EndOfLineRule("<", deleted);
		rule.setColumnConstraint(0);
		rules.add(rule);

		partitioningRules = new IPredicateRule[rules.size()];
		rules.toArray(partitioningRules);
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
			return IDiffConstants.CONTENT_TYPE_DIFF;
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
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getOneTokenScanner(SOURCE_DIFF));
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(getOneTokenScanner(MARKUP_INSERTED_DIFF));
		reconciler.setDamager(dr, INSERTED);
		reconciler.setRepairer(dr, INSERTED);

		dr = new ThemeingDamagerRepairer(getOneTokenScanner(MARKUP_CHANGED_DIFF));
		reconciler.setDamager(dr, CHANGED);
		reconciler.setRepairer(dr, CHANGED);

		dr = new ThemeingDamagerRepairer(getOneTokenScanner(MARKUP_DELETED_DIFF));
		reconciler.setDamager(dr, DELETED);
		reconciler.setRepairer(dr, DELETED);

		dr = new ThemeingDamagerRepairer(getOneTokenScanner(META_DIFF_HEADER));
		reconciler.setDamager(dr, HEADER);
		reconciler.setRepairer(dr, HEADER);

		dr = new ThemeingDamagerRepairer(getOneTokenScanner(META_DIFF_INDEX));
		reconciler.setDamager(dr, INDEX);
		reconciler.setRepairer(dr, INDEX);

		dr = new ThemeingDamagerRepairer(getOneTokenScanner(META_DIFF_RANGE));
		reconciler.setDamager(dr, RANGE);
		reconciler.setRepairer(dr, RANGE);

		dr = new ThemeingDamagerRepairer(getOneTokenScanner(META_SEPARATOR_DIFF));
		reconciler.setDamager(dr, SEPARATOR);
		reconciler.setRepairer(dr, SEPARATOR);
	}

	private ITokenScanner getOneTokenScanner(String token)
	{
		RuleBasedScanner multilineCommentScanner = new RuleBasedScanner();
		multilineCommentScanner.setDefaultReturnToken(getToken(token));

		return multilineCommentScanner;
	}

	private IToken getToken(String name)
	{
		return new Token(name);
	}
}
