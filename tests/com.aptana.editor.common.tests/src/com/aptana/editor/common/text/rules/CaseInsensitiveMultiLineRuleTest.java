package com.aptana.editor.common.text.rules;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

public class CaseInsensitiveMultiLineRuleTest extends TestCase
{

	protected void assertMatches(String startSequence, String src)
	{
		IToken found = new Token("Found!");
		IPredicateRule rule = new CaseInsensitiveMultiLineRule(startSequence, ">", found);
		RuleBasedScanner scanner = new RuleBasedScanner();
		scanner.setRules(new IRule[] { rule });
		scanner.setRange(new Document(src), 0, src.length());

		assertEquals(found, rule.evaluate(scanner));
	}

	public void testRuleMatchesSamecase()
	{
		assertMatches("<!DOCTYPE", "<!DOCTYPE>");
	}

	public void testRuleMatchesMixedcase()
	{
		assertMatches("<!DOCTYPE", "<!DoCtYpE>");
	}

	public void testMexiedCaseRuleMatchesMixedcase()
	{
		assertMatches("<!DoCtYpE", "<!dOcTyPe>");
	}

	public void testRuleMatchesUppercase()
	{
		assertMatches("<!doctype", "<!DOCTYPE>");
	}

	public void testRuleMatchesLowercase()
	{
		assertMatches("<!DOCTYPE", "<!doctype>");
	}

}
