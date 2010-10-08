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

package com.aptana.editor.markdown;

import java.util.ArrayList;
import java.util.List;

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
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.NonRuleBasedDamagerRepairer;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.TagRule;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.html.HTMLTagScanner;
import com.aptana.editor.markdown.text.rules.BlockLevelRule;
import com.aptana.editor.markdown.text.rules.HardWrapLineRule;
import com.aptana.editor.markdown.text.rules.MarkdownHeadingScanner;
import com.aptana.editor.markdown.text.rules.MarkdownScanner;
import com.aptana.editor.markdown.text.rules.UnnumberedListScanner;

/**
 * @author Chris Williams
 */
public class MarkdownSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	public final static String PREFIX = "__md_"; //$NON-NLS-1$
	public final static String DEFAULT = "__md" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String HEADING = PREFIX + "heading"; //$NON-NLS-1$
	public final static String HEADING_1 = HEADING + ".1"; //$NON-NLS-1$
	public final static String HEADING_2 = HEADING + ".2"; //$NON-NLS-1$
	public final static String UNNUMBERED_LIST = PREFIX + "unnumbered_list"; //$NON-NLS-1$
	public final static String NUMBERED_LIST = PREFIX + "numbered_list"; //$NON-NLS-1$
	public final static String SEPARATOR = PREFIX + "separator"; //$NON-NLS-1$
	public final static String QUOTE = PREFIX + "quote"; //$NON-NLS-1$
	public final static String BLOCK = PREFIX + "block"; //$NON-NLS-1$
	public final static String HTML_TAG = PREFIX + "html"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, HEADING, HEADING_1, HEADING_2,
			UNNUMBERED_LIST, NUMBERED_LIST, SEPARATOR, QUOTE, BLOCK, HTML_TAG };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IMarkdownConstants.CONTENT_TYPE_MARKDOWN } };

	private MarkdownScanner xmlScanner;
	private RuleBasedScanner headingScanner;

	private static MarkdownSourceConfiguration instance;

	public static MarkdownSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			instance = new MarkdownSourceConfiguration();
			IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
			c.addTranslation(new QualifiedContentType(IMarkdownConstants.CONTENT_TYPE_MARKDOWN),
					new QualifiedContentType("text.html.markdown")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(DEFAULT), new QualifiedContentType("meta.paragraph.markdown")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(HEADING), new QualifiedContentType("markup.heading.markdown")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(HEADING_1), new QualifiedContentType("markup.heading.1.markdown")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(HEADING_2), new QualifiedContentType("markup.heading.2.markdown")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(UNNUMBERED_LIST), new QualifiedContentType(
					"markup.list.unnumbered.markdown")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(NUMBERED_LIST), new QualifiedContentType(
					"markup.list.numbered.markdown")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(SEPARATOR), new QualifiedContentType("meta.separator.markdown")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(QUOTE), new QualifiedContentType(
					"meta.block-level.markdown", "markup.quote.markdown")); //$NON-NLS-1$ //$NON-NLS-2$
			c.addTranslation(new QualifiedContentType(BLOCK), new QualifiedContentType("markup.raw.block.markdown")); //$NON-NLS-1$
			c.addTranslation(new QualifiedContentType(HTML_TAG), new QualifiedContentType(
					"meta.disable-markdown", "meta.tag.block.any.html")); //$NON-NLS-1$ //$NON-NLS-2$
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
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

		// BlockQuotes
		rules.add(new HardWrapLineRule(">", null, new Token(QUOTE))); //$NON-NLS-1$
		rules.add(new HardWrapLineRule(" >", null, new Token(QUOTE))); //$NON-NLS-1$
		rules.add(new HardWrapLineRule("  >", null, new Token(QUOTE))); //$NON-NLS-1$
		rules.add(new HardWrapLineRule("   >", null, new Token(QUOTE))); //$NON-NLS-1$

		// Separators
		final char[] separatorChars = { '*', '-', '_' };
		for (int sepCharIdx = 0; sepCharIdx < separatorChars.length; sepCharIdx++)
		{
			for (int initialSpaces = 0; initialSpaces <= 3; initialSpaces++)
			{
				for (int laterSpaces = 0; laterSpaces <= 2; laterSpaces++)
				{
					rules.add(createSeparatorRule(separatorChars[sepCharIdx], initialSpaces, laterSpaces));
				}
			}
		}

		// Inline HTML, // FIXME This needs to be merged with BlockLevelRule!
		TagRule tagRule = new TagRule("/", new Token(HTML_TAG)); //$NON-NLS-1$
		tagRule.setColumnConstraint(0);
		rules.add(tagRule);
		tagRule = new TagRule(new Token(HTML_TAG));
		tagRule.setColumnConstraint(0);
		rules.add(tagRule);

		// Unnumbered Lists
		for (int i = 0; i <= 3; i++)
		{
			rules.add(createListRule(i, '*'));
			rules.add(createListRule(i, '+'));
			rules.add(createListRule(i, '-'));
		}

		// Numbered Lists
		for (int i = 1; i <= 100; i++)
		{
			rules.add(new BlockLevelRule(i + ".", null, new Token(NUMBERED_LIST))); //$NON-NLS-1$
		}

		// Headings
		rules.add(createSetexHeadingRule('-', 2));
		rules.add(createSetexHeadingRule('=', 1));
		for (int i = 6; i > 0; i--)
		{
			rules.add(createATXHeadingRule(i));
		}

		// Blocks
		SingleLineRule rule = new EndOfLineRule("    ", new Token(BLOCK)); //$NON-NLS-1$
		rule.setColumnConstraint(0);
		rules.add(rule);
		rule = new EndOfLineRule("\t", new Token(BLOCK)); //$NON-NLS-1$
		rule.setColumnConstraint(0);
		rules.add(rule);

		return rules.toArray(new IPredicateRule[rules.size()]);
	}

	@SuppressWarnings("nls")
	protected IPredicateRule createSeparatorRule(char c, int leadingSpaces, int spaces2)
	{
		String string = "";
		for (int x = 0; x < leadingSpaces; x++)
		{
			string += " ";
		}
		string += c;
		for (int x = 0; x < spaces2; x++)
		{
			string += " ";
		}
		string += c;
		for (int x = 0; x < spaces2; x++)
		{
			string += " ";
		}
		string += c;
		SingleLineRule rule = new SingleLineRule(string, "", new Token(SEPARATOR), (char) 0, true);
		rule.setColumnConstraint(0);
		return rule;

	}

	@SuppressWarnings("nls")
	protected IPredicateRule createListRule(int leadingSpaces, char c)
	{
		// [ ]{0,3}([*+-])(?=\s)
		String str = "";
		for (int i = 0; i < leadingSpaces; i++)
		{
			str += " ";
		}
		str += c + " ";
		return new BlockLevelRule(str, null, new Token(UNNUMBERED_LIST));
	}

	protected SingleLineRule createSetexHeadingRule(char c, int level)
	{
		String token = HEADING_1;
		if (level == 2)
		{
			token = HEADING_2;
		}
		SingleLineRule rule = new SingleLineRule("" + c, null, new Token(token)); //$NON-NLS-1$
		rule.setColumnConstraint(0);
		return rule;
	}

	protected SingleLineRule createATXHeadingRule(int hashes)
	{
		String header = ""; //$NON-NLS-1$
		for (int i = 0; i < hashes; i++)
		{
			header += "#"; //$NON-NLS-1$
		}
		SingleLineRule rule = new SingleLineRule(header, header, new Token(HEADING), (char) 0, true);
		rule.setColumnConstraint(0);
		return rule;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#createSubPartitionScanner()
	 */
	public ISubPartitionScanner createSubPartitionScanner()
	{
		return new SubPartitionScanner(getPartitioningRules(), CONTENT_TYPES, new Token(DEFAULT));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentDefaultContentType()
	 */
	public String getDocumentContentType(String contentType)
	{
		if (contentType.startsWith(PREFIX))
		{
			return IMarkdownConstants.CONTENT_TYPE_MARKDOWN;
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
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getMarkdownScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		reconciler.setDamager(dr, NUMBERED_LIST);
		reconciler.setRepairer(dr, NUMBERED_LIST);

		dr = new ThemeingDamagerRepairer(new UnnumberedListScanner());
		reconciler.setDamager(dr, UNNUMBERED_LIST);
		reconciler.setRepairer(dr, UNNUMBERED_LIST);

		dr = new ThemeingDamagerRepairer(getPreProcessorScanner());
		reconciler.setDamager(dr, HEADING);
		reconciler.setRepairer(dr, HEADING);

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(getToken("meta.separator.markdown")); //$NON-NLS-1$
		reconciler.setDamager(ndr, SEPARATOR);
		reconciler.setRepairer(ndr, SEPARATOR);

		ndr = new NonRuleBasedDamagerRepairer(getToken("markup.raw.block.markdown")); //$NON-NLS-1$
		reconciler.setDamager(ndr, BLOCK);
		reconciler.setRepairer(ndr, BLOCK);

		dr = new ThemeingDamagerRepairer(new HTMLTagScanner());
		reconciler.setDamager(dr, HTML_TAG);
		reconciler.setRepairer(dr, HTML_TAG);
	}

	private ITokenScanner getPreProcessorScanner()
	{
		if (headingScanner == null)
		{
			headingScanner = new MarkdownHeadingScanner();
		}
		return headingScanner;
	}

	protected ITokenScanner getMarkdownScanner()
	{
		if (xmlScanner == null)
		{
			xmlScanner = new MarkdownScanner();
		}
		return xmlScanner;
	}

	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}
}
