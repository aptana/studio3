package com.aptana.editor.common.text.rules;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

public class ExtendedWordRuleTest extends TestCase
{

	public void testEvaluateICharacterScanner()
	{
		IWordDetector detector = new WordDetector();
		IToken token = new Token("success");
		ExtendedWordRule rule = new ExtendedWordRule(detector, token, true)
		{

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				return word.endsWith("z");
			}
		};
		IToken hazToken = new Token("hazToken");
		rule.addWord("haz", hazToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("word wordz haz");
		scanner.setRange(document, 0, document.getLength());
		assertEquals(Token.UNDEFINED, rule.evaluate(scanner));
		scanner.setRange(document, 5, 9);
		assertEquals(token, rule.evaluate(scanner));
		scanner.setRange(document, 11, 3);
		assertEquals(hazToken, rule.evaluate(scanner));
	}

}
