package com.aptana.editor.common.text.rules;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;


public class SingleCharacterRuleTest extends TestCase
{
	public void testRule()
	{
		IPredicateRule rule = new SingleCharacterRule('.', Token.WHITESPACE);

		String src = "3.14";
		RuleBasedScanner scanner = new RuleBasedScanner();
		scanner.setRules(new IRule[] { rule });
		scanner.setRange(new Document(src), 0, src.length());

		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		scanner.read();
		assertEquals(Token.WHITESPACE, rule.evaluate(scanner));
		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		scanner.read();
		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		scanner.read();
		assertEquals(Token.EOF, scanner.nextToken());
	}
}
