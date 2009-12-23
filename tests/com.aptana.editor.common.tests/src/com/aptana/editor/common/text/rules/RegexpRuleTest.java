package com.aptana.editor.common.text.rules;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.RegexpRule;

public class RegexpRuleTest extends TestCase
{

	public void testRegexp()
	{
		IToken successToken = new Token("name");
		RegexpRule rule = new RegexpRule("\\b(chris|sandip|andrew|kevin)\\b", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("chris sandip kevin andrew bob");
		scanner.setRange(document, 0, 5);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(5, scanner.getColumn());
		scanner.setRange(document, 6, 6);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(12, scanner.getColumn());
		scanner.setRange(document, 13, 5);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(18, scanner.getColumn());
		scanner.setRange(document, 19, 6);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(25, scanner.getColumn());
		scanner.setRange(document, 26, 3);
		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		assertEquals(26, scanner.getColumn());
	}
	
	public void testRegexp2()
	{
		IToken successToken = new Token("name");
		RegexpRule rule = new RegexpRule("chris|sandip|andrew|kevin", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("chris sandip kevin andrew bob");
		scanner.setRange(document, 0, 5);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(5, scanner.getColumn());
		scanner.setRange(document, 6, 6);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(12, scanner.getColumn());
		scanner.setRange(document, 13, 5);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(18, scanner.getColumn());
		scanner.setRange(document, 19, 6);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(25, scanner.getColumn());
		scanner.setRange(document, 26, 3);
		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		assertEquals(26, scanner.getColumn());
	}
	
	public void testRegexpWithOptimizationStillWorksJustIgnoresInvalidOptimization()
	{
		IToken successToken = new Token("name");
		RegexpRule rule = new RegexpRule("\\b(chris|sandip|andrew|kevin)\\b", successToken, true);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("chris sandip kevin andrew bob");
		scanner.setRange(document, 0, 5);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(5, scanner.getColumn());
		scanner.setRange(document, 6, 6);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(12, scanner.getColumn());
		scanner.setRange(document, 13, 5);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(18, scanner.getColumn());
		scanner.setRange(document, 19, 6);
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(25, scanner.getColumn());
		scanner.setRange(document, 26, 3);
		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		assertEquals(26, scanner.getColumn());
	}
	
//	public void testRegexpWithOptimizationStillWorksJustIgnoresInvalidOptimization2()
//	{
//		IToken successToken = new Token("name");
//		RegexpRule rule = new RegexpRule("chris|sandip|andrew|kevin", successToken, true);
//		RuleBasedScanner scanner = new RuleBasedScanner();
//		IDocument document = new Document("chris sandip kevin andrew bob");
//		scanner.setRange(document, 0, 5);
//		assertEquals(successToken, rule.evaluate(scanner));
//		assertEquals(5, scanner.getColumn());
//		scanner.setRange(document, 6, 6);
//		assertEquals(successToken, rule.evaluate(scanner));
//		assertEquals(12, scanner.getColumn());
//		scanner.setRange(document, 13, 5);
//		assertEquals(successToken, rule.evaluate(scanner));
//		assertEquals(18, scanner.getColumn());
//		scanner.setRange(document, 19, 6);
//		assertEquals(successToken, rule.evaluate(scanner));
//		assertEquals(25, scanner.getColumn());
//		scanner.setRange(document, 26, 3);
//		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
//		assertEquals(26, scanner.getColumn());
//	}
	
	public void testRegexpWithOptimization()
	{
		IToken successToken = new Token("name");
		RegexpRule rule = new RegexpRule("chris", successToken, true);
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
