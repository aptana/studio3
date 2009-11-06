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

package com.aptana.radrails.editor.css;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.radrails.editor.common.IPartitioningConfiguration;
import com.aptana.radrails.editor.common.ISourceViewerConfiguration;
import com.aptana.radrails.editor.common.ISubPartitionScanner;
import com.aptana.radrails.editor.common.SubPartitionScanner;
import com.aptana.radrails.editor.common.theme.ThemeUtil;

/**
 * @author Max Stepanov
 *
 */
public class CSSSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration {

	public final static String DEFAULT = "__css" + IDocument.DEFAULT_CONTENT_TYPE;
	public final static String STRING = "__css_string";
	public final static String MULTILINE_COMMENT = "__css_multiline_comment";

	public static final String[] CONTENT_TYPES = new String[] {
		MULTILINE_COMMENT,
		STRING
	};
	
	/**
	 * Detector for empty comments.
	 */
	static class EmptyCommentDetector implements IWordDetector
	{
		/**
		 * isWordStart
		 */
		public boolean isWordStart(char c)
		{
			return (c == '/');
		}

		/**
		 * isWordPart
		 */
		public boolean isWordPart(char c)
		{
			return (c == '*' || c == '/');
		}
	}

	/**
	 * WordPredicateRule
	 */
	static class WordPredicateRule extends WordRule implements IPredicateRule
	{
		private IToken fSuccessToken;

		/**
		 * WordPredicateRule
		 * 
		 * @param successToken
		 */
		public WordPredicateRule(IToken successToken)
		{
			super(new EmptyCommentDetector());
			fSuccessToken = successToken;
			addWord("/**/", fSuccessToken); //$NON-NLS-1$
		}

		/**
		 * evaluate
		 */
		public IToken evaluate(ICharacterScanner scanner, boolean resume)
		{
			return super.evaluate(scanner);
		}

		/**
		 * getSuccessToken
		 */
		public IToken getSuccessToken()
		{
			return fSuccessToken;
		}
	}

	private IToken stringToken = new Token(STRING);
	
	private IPredicateRule[] partitioningRules;

	private RuleBasedScanner multilineCommentScanner;
	private RuleBasedScanner stringScanner;

	private static CSSSourceConfiguration instance;
	
	public static CSSSourceConfiguration getDefault() {
		if (instance == null) {
			instance = new CSSSourceConfiguration();
		}
		return instance;
	}
	
	private CSSSourceConfiguration() {
		
		IToken comment = new Token(MULTILINE_COMMENT);
		
		partitioningRules = new IPredicateRule[] {
				new SingleLineRule("\"", "\"", stringToken, '\\'),
				new SingleLineRule("\'", "\'", stringToken, '\\'),
				new WordPredicateRule(comment),
				new MultiLineRule("/*", "*/", comment, (char) 0, true)
		};
	}

	/* (non-Javadoc)
	 * @see com.aptana.radrails.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes() {
		return CONTENT_TYPES;
	}

	/* (non-Javadoc)
	 * @see com.aptana.radrails.editor.common.IPartitioningConfiguration#getPartitioningRules()
	 */
	public IPredicateRule[] getPartitioningRules() {
		return partitioningRules;
	}

	/* (non-Javadoc)
	 * @see com.aptana.radrails.editor.common.IPartitioningConfiguration#createSubPartitionScanner()
	 */
	public ISubPartitionScanner createSubPartitionScanner() {
		return new SubPartitionScanner(partitioningRules, CONTENT_TYPES, new Token(DEFAULT));
	}

	/* (non-Javadoc)
	 * @see com.aptana.radrails.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text.presentation.PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer) {
		
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(Activator.getDefault().getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);
		
		dr = new DefaultDamagerRepairer(getWordScanner());
		reconciler.setDamager(dr, MULTILINE_COMMENT);
		reconciler.setRepairer(dr, MULTILINE_COMMENT);

		dr = new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, STRING);
		reconciler.setRepairer(dr, STRING);
	}

	protected ITokenScanner getWordScanner() {
		if (multilineCommentScanner == null) {
			multilineCommentScanner = new RuleBasedScanner();
			multilineCommentScanner.setDefaultReturnToken(ThemeUtil.getToken("comment.block.css"));
		}
		return multilineCommentScanner;
	}
	
	protected ITokenScanner getStringScanner() {
		if (stringScanner == null) {
			stringScanner = new RuleBasedScanner();
			stringScanner.setDefaultReturnToken(ThemeUtil.getToken("string.quoted.single.css"));
		}
		return stringScanner;
	}

}
