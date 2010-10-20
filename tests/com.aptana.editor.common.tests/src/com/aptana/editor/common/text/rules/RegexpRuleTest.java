/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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
