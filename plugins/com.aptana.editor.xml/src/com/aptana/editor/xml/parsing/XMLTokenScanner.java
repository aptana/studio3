package com.aptana.editor.xml.parsing;

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
import com.aptana.editor.xml.parsing.lexer.XMLToken;

public class XMLTokenScanner extends RuleBasedScanner
{

	public XMLTokenScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		// generic whitespace rule
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		// comments
		rules.add(new MultiLineRule("<!--", "-->", createToken(XMLToken.COMMENT))); //$NON-NLS-1$ //$NON-NLS-2$
		// CDATA
		rules.add(new MultiLineRule("<![CDATA[", "]]>", createToken(XMLToken.CDATA))); //$NON-NLS-1$ //$NON-NLS-2$
		// declaration
		rules.add(new TagRule("?xml", createToken(XMLToken.DECLARATION))); //$NON-NLS-1$
		// tags
		rules.add(new TagRule("/", createToken(XMLToken.END_TAG))); //$NON-NLS-1$
		rules.add(new TagRule(createToken(XMLToken.START_TAG)));

		// text
		IToken token = createToken(XMLToken.TEXT);
		rules.add(new WordRule(new WordDetector(), token));

		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(token);
	}

	protected IToken createToken(Object data)
	{
		return new Token(data);
	}
}
