package com.aptana.editor.css;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.css.CSSSourceConfiguration.WordPredicateRule;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;

public class CSSScopeScanner extends CSSCodeScanner
{
	/**
	 * CSSScopeScanner
	 */
	public CSSScopeScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		
		// Add the rules created by the super class first so they have higher
		// precedence
		if (fRules != null)
		{
			rules.addAll(Arrays.asList(fRules));
		}

		// Add the rules for block comments, single and double quoted strings
		rules.add(new SingleLineRule("\"", "\"", createToken(CSSTokenType.DOUBLE_QUOTED_STRING), '\\')); //$NON-NLS-1$ //$NON-NLS-2$ 
		rules.add(new SingleLineRule("\'", "\'", createToken(CSSTokenType.SINGLE_QUOTED_STRING), '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new WordPredicateRule(createToken(CSSTokenType.COMMENT)));
		rules.add(new MultiLineRule("/*", "*/", createToken(CSSTokenType.COMMENT), (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		
		// Add rules for the start characters of classes and ids
		rules.add(new SingleCharacterRule('#', createToken(CSSTokenType.ID)));
		rules.add(new SingleCharacterRule('.', createToken(CSSTokenType.CLASS)));
		rules.add(new SingleCharacterRule(',', createToken(CSSTokenType.COMMA)));
		rules.add(new SingleCharacterRule('/', createToken(CSSTokenType.SLASH)));
		rules.add(new SingleCharacterRule('*', createToken(CSSTokenType.STAR)));

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.CSSCodeScanner#createToken(java.lang.String)
	 */
	@Override
	protected IToken createToken(String string)
	{
		// Simply use the token type string that is passed in as the data
		return new Token(string);
	}
	
	/**
	 * createToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken createToken(CSSTokenType type)
	{
		return this.createToken(type.getScope());
	}
}
