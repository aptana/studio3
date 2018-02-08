/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.outline;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.css.core.parsing.CSSParser;
import com.aptana.css.core.parsing.ast.CSSRuleNode;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.parsing.CSSScanner;
import com.aptana.parsing.ast.IParseNode;

public class CSSOutlineProviderTest
{

	private CSSOutlineLabelProvider fLabelProvider;
	private CSSOutlineContentProvider fContentProvider;

	private CSSParser fParser;
	private CSSScanner fScanner;

	@Before
	public void setUp() throws Exception
	{
		fLabelProvider = new CSSOutlineLabelProvider();
		fContentProvider = new CSSOutlineContentProvider();
		fParser = new CSSParser();
		fScanner = new CSSScanner();
	}

	@After
	public void tearDown() throws Exception
	{
		fLabelProvider = null;
		fContentProvider = null;
		fParser = null;
		fScanner = null;
	}

	@Test
	public void testMultipleSelectors() throws Exception
	{
		String source = "textarea.JScript, textarea.HTML {height:10em;}";
		fScanner.setSource(source);
		IParseNode result = (IParseNode) fParser.parse(fScanner);

		Object[] selectors = fContentProvider.getElements(result);
		assertEquals(2, selectors.length);
		CSSRuleNode rule = (CSSRuleNode) result.getChild(0);
		assertEquals(rule.getSelectors()[0], getNode(selectors[0]));
		assertEquals(CSSPlugin.getImage("icons/selector.png"), fLabelProvider.getImage(selectors[0]));
		assertEquals(rule.getSelectors()[1], getNode(selectors[1]));

		Object[] declarations = fContentProvider.getChildren(selectors[0]);
		assertEquals(1, declarations.length);
		assertEquals(rule.getDeclarations()[0], getNode(declarations[0]));
		assertEquals(CSSPlugin.getImage("icons/declaration.png"), fLabelProvider.getImage(declarations[0]));

		declarations = fContentProvider.getChildren(selectors[1]);
		assertEquals(1, declarations.length);
		assertEquals(rule.getDeclarations()[0], getNode(declarations[0]));
	}

	@Test
	public void testElementAt() throws Exception
	{
		String source = "textarea.JScript, textarea.HTML {height:10em;}";
		fScanner.setSource(source);
		IParseNode result = (IParseNode) fParser.parse(fScanner);

		CSSRuleNode rule = (CSSRuleNode) result.getChild(0);
		assertEquals(rule.getSelectors()[0], getNode(CSSOutlineContentProvider.getElementAt(result, 0)));
		assertEquals(rule.getSelectors()[0], getNode(CSSOutlineContentProvider.getElementAt(result, 10)));
		assertEquals(rule.getSelectors()[1], getNode(CSSOutlineContentProvider.getElementAt(result, 20)));
		assertEquals(rule.getDeclarations()[0], getNode(CSSOutlineContentProvider.getElementAt(result, 40)));
		assertEquals(rule.getSelectors()[1],
				getNode(CSSOutlineContentProvider.getElementAt(result, source.length() - 1)));
	}

	@Test
	public void testAtCharsetRule() throws Exception
	{
		String source = "@charset \"utf-8\";";
		fScanner.setSource(source);
		IParseNode astRoot = (IParseNode) fParser.parse(fScanner);

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);

		assertEquals(astRoot.getChild(0), getNode(outlineResult[0]));
		assertEquals(CSSPlugin.getImage("icons/at_rule.png"), fLabelProvider.getImage(outlineResult[0]));
		assertEquals(astRoot.getChild(0).getText().substring(1), fLabelProvider.getText(outlineResult[0]));
	}

	private static IParseNode getNode(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return ((CommonOutlineItem) element).getReferenceNode();
		}
		if (element instanceof IParseNode)
		{
			return (IParseNode) element;
		}
		return null;
	}
}
