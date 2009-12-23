package com.aptana.editor.common.text.rules;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.SingleTagRule;

public class SingleTagRuleTest extends TestCase
{

	public void testSingleTagRule()
	{
		IToken successToken = new Token("name");
		SingleTagRule rule = new SingleTagRule("chris", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("chris sandip kevin andrew bob");
		scanner.setRange(document, 0, 5);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(5, scanner.getColumn());
		scanner.setRange(document, 6, 6);
		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		assertEquals(6, scanner.getColumn());
		scanner.setRange(document, 13, 5);
		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		assertEquals(13, scanner.getColumn());
		scanner.setRange(document, 19, 6);
		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		assertEquals(19, scanner.getColumn());
		scanner.setRange(document, 26, 3);
		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		assertEquals(26, scanner.getColumn());
	}
}
