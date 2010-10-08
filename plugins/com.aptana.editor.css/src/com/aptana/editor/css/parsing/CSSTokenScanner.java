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
package com.aptana.editor.css.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.css.CSSCodeScanner;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;

/**
 * @author Chris Williams
 */
public class CSSTokenScanner extends CSSCodeScanner
{

	/**
	 * A flag to turn on or off the optimization of eligible regexp rules. Seems to make a measurable difference on
	 * large files.
	 */
	private static final boolean OPTIMIZE_REGEXP_RULES = true;

	protected List<IRule> createRules()
	{
		List<IRule> rules = super.createRules();
		rules.addAll(1, createCommentAndStringRules());
		return rules;
	}

	protected Collection<? extends IRule> createScannerSpecificRules()
	{
		List<IRule> rules = new ArrayList<IRule>();
		// url
		// FIXME Don't use a RegexpRule here!
		rules.add(new RegexpRule("url\\([^\\)]*\\)", createToken(CSSTokenType.URL), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$
		
		// FIXME These are all really just numbers followed by measurements. Can't we modify scanner/parser to grab number and then a measurement
		// em
		rules.add(createMeasurementRule("(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)em", CSSTokenType.EMS)); //$NON-NLS-1$
		// length
		rules.add(createMeasurementRule("(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(px|cm|mm|in|pt|pc)", CSSTokenType.LENGTH)); //$NON-NLS-1$
		// percentage
		rules.add(createMeasurementRule("(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)%", CSSTokenType.PERCENTAGE)); //$NON-NLS-1$
		// angle
		rules.add(createMeasurementRule("(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(deg|rad|grad)", CSSTokenType.ANGLE)); //$NON-NLS-1$
		// ex
		rules.add(createMeasurementRule("(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)ex", CSSTokenType.EXS)); //$NON-NLS-1$
		// frequency
		rules.add(createMeasurementRule("(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)([Hh]z|k[Hh]z)", CSSTokenType.FREQUENCY)); //$NON-NLS-1$
		// time
		rules.add(createMeasurementRule("(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(ms|s)", CSSTokenType.TIME)); //$NON-NLS-1$

		return rules;
	}

	private IRule createMeasurementRule(final String regex, CSSTokenType tokenType)
	{
		return new ExtendedWordRule(new IWordDetector()
		{

			public boolean isWordStart(char c)
			{
				return c == '-' || c == '+' || c == '.' || Character.isDigit(c);
			}

			public boolean isWordPart(char c)
			{
				return c == '.' || c == '%' ||Character.isLetterOrDigit(c);
			}
		}, createToken(tokenType), false)
		{
			
			private Pattern pattern;

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				if (pattern == null)
				{
					pattern = Pattern.compile(regex);
				}
				return pattern.matcher(word).matches();
			}
		};
	}

	private List<IRule> createCommentAndStringRules()
	{
		List<IRule> rules = new ArrayList<IRule>();
		// comments
		rules.add(new MultiLineRule("/*", "*/", createToken(CSSTokenType.COMMENT), (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		// quoted strings
		rules.add(new SingleLineRule("\"", "\"", createToken(CSSTokenType.DOUBLE_QUOTED_STRING), '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new SingleLineRule("\'", "\'", createToken(CSSTokenType.SINGLE_QUOTED_STRING), '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		return rules;
	}

	protected IToken createToken(CSSTokenType ctt)
	{
		return new Token(ctt);
	}

}
