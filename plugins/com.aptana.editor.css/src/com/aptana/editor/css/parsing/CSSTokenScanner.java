package com.aptana.editor.css.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

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

	protected Collection<? extends IRule> createScannerSpecificRules() {
		List<IRule> rules = new ArrayList<IRule>();
		// url
		rules.add(new RegexpRule("url\\([^\\)]*\\)", createToken(CSSTokenType.URL), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$
		// em
		rules
				.add(new RegexpRule(
						"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)em", createToken(CSSTokenType.EMS))); //$NON-NLS-1$
		// length
		rules
				.add(new RegexpRule(
						"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(px|cm|mm|in|pt|pc)", createToken(CSSTokenType.LENGTH))); //$NON-NLS-1$
		// percentage
		rules.add(new RegexpRule(
				"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)%", createToken(CSSTokenType.PERCENTAGE))); //$NON-NLS-1$
		// angle
		rules.add(new RegexpRule(
				"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(deg|rad|grad)", createToken(CSSTokenType.ANGLE))); //$NON-NLS-1$
		// ex
		rules
				.add(new RegexpRule(
						"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)ex", createToken(CSSTokenType.EXS))); //$NON-NLS-1$
		// frequency
		rules
				.add(new RegexpRule(
						"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)([Hh]z|k[Hh]z)", createToken(CSSTokenType.FREQUENCY))); //$NON-NLS-1$
		// time
		rules.add(new RegexpRule(
				"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(ms|s)", createToken(CSSTokenType.TIME))); //$NON-NLS-1$

		// numbers
		rules.add(new RegexpRule("(\\-|\\+)?\\s*[0-9]+(\\.[0-9]+)?", createToken(CSSTokenType.NUMBER))); //$NON-NLS-1$
		
		// minus
		rules
				.add(new RegexpRule(
						"-(?=\\s*[0-9\\.])", createToken(CSSTokenType.MINUS), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$
		return rules;
	}

	private List<IRule> createCommentAndStringRules() {
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
