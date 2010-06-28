package com.aptana.editor.html.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.TagRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.common.text.rules.WordDetector;
import com.aptana.editor.html.HTMLCommentRule;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;

public class HTMLTokenScanner extends RuleBasedScanner
{

	public HTMLTokenScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		// generic whitespace rule
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		// comments
		rules.add(new HTMLCommentRule(createToken(getTokenName(HTMLTokens.COMMENT))));
		// DOCTYPE
		rules.add(new MultiLineRule("<!DOCTYPE ", ">", createToken(getTokenName(HTMLTokens.DOCTYPE)))); //$NON-NLS-1$ //$NON-NLS-2$
		// CDATA
		rules.add(new MultiLineRule("<![CDATA[", "]]>", createToken(getTokenName(HTMLTokens.CDATA)))); //$NON-NLS-1$ //$NON-NLS-2$
		// script
		rules.add(new TagRule("script", createToken(getTokenName(HTMLTokens.SCRIPT)), true)); //$NON-NLS-1$
		rules.add(new TagRule("/script", createToken(getTokenName(HTMLTokens.SCRIPT_END)), true)); //$NON-NLS-1$
		// style
		rules.add(new TagRule("style", createToken(getTokenName(HTMLTokens.STYLE)), true)); //$NON-NLS-1$
		rules.add(new TagRule("/style", createToken(getTokenName(HTMLTokens.STYLE_END)), true)); //$NON-NLS-1$
		// xml declaration
		rules.add(new TagRule("?xml", createToken(getTokenName(HTMLTokens.XML_DECL)))); //$NON-NLS-1$
		// tags
		rules.add(new TagRule("/", createToken(getTokenName(HTMLTokens.END_TAG)))); //$NON-NLS-1$
		rules.add(new TagRule(createToken(getTokenName(HTMLTokens.START_TAG))));

		// text
		IToken token = createToken(getTokenName(HTMLTokens.TEXT));
		rules.add(new WordRule(new WordDetector(), token));

		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(token);
	}

	protected IToken createToken(String string)
	{
		return new Token(string);
	}

	private static String getTokenName(short token)
	{
		return HTMLTokens.getTokenName(token);
	}
}
