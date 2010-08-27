package com.aptana.editor.css.outline;

import com.aptana.editor.css.Activator;
import com.aptana.editor.css.parsing.CSSParser;
import com.aptana.editor.css.parsing.CSSScanner;
import com.aptana.editor.css.parsing.ast.CSSRuleNode;
import com.aptana.parsing.ast.IParseNode;

import junit.framework.TestCase;

public class CSSOutlineProviderTest extends TestCase
{

	private CSSOutlineLabelProvider fLabelProvider;
	private CSSOutlineContentProvider fContentProvider;

	private CSSParser fParser;
	private CSSScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		fLabelProvider = new CSSOutlineLabelProvider();
		fContentProvider = new CSSOutlineContentProvider();
		fParser = new CSSParser();
		fScanner = new CSSScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fLabelProvider = null;
		fContentProvider = null;
		fParser = null;
		fScanner = null;
	}

	public void testMultipleSelectors() throws Exception
	{
		String source = "textarea.JScript, textarea.HTML {height:10em;}";
		fScanner.setSource(source);
		IParseNode result = (IParseNode) fParser.parse(fScanner);

		Object[] selectors = fContentProvider.getElements(result);
		assertEquals(2, selectors.length);
		CSSRuleNode rule = (CSSRuleNode) result.getChild(0);
		assertEquals(rule.getSelectors()[0], selectors[0]);
		assertEquals(Activator.getImage("icons/selector.png"), fLabelProvider.getImage(selectors[0]));
		assertEquals(rule.getSelectors()[1], selectors[1]);

		Object[] declarations = fContentProvider.getChildren(selectors[0]);
		assertEquals(1, declarations.length);
		assertEquals(rule.getDeclarations()[0], declarations[0]);
		assertEquals(Activator.getImage("icons/declaration.png"), fLabelProvider.getImage(declarations[0]));

		declarations = fContentProvider.getChildren(selectors[1]);
		assertEquals(1, declarations.length);
		assertEquals(rule.getDeclarations()[0], declarations[0]);
	}

	public void testElementAt() throws Exception
	{
		String source = "textarea.JScript, textarea.HTML {height:10em;}";
		fScanner.setSource(source);
		IParseNode result = (IParseNode) fParser.parse(fScanner);

		CSSRuleNode rule = (CSSRuleNode) result.getChild(0);
		assertEquals(rule.getSelectors()[0], CSSOutlineContentProvider.getElementAt(result, 0));
		assertEquals(rule.getSelectors()[0], CSSOutlineContentProvider.getElementAt(result, 10));
		assertEquals(rule.getSelectors()[1], CSSOutlineContentProvider.getElementAt(result, 20));
		assertEquals(rule.getDeclarations()[0], CSSOutlineContentProvider.getElementAt(result, 40));
		assertEquals(rule.getSelectors()[1], CSSOutlineContentProvider.getElementAt(result, source.length() - 1));
	}
}
