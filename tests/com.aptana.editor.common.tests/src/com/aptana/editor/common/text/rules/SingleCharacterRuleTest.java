/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;


public class SingleCharacterRuleTest
{
	@Test
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
