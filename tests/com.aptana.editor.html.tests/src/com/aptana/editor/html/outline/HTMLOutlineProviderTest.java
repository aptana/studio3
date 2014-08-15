/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.outline;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.EclipseUtil;
import com.aptana.css.core.ICSSConstants;
import com.aptana.css.core.parsing.ast.ICSSNodeTypes;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.editor.html.preferences.HTMLPreferenceUtil;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

public class HTMLOutlineProviderTest
{

	private HTMLOutlineLabelProvider fLabelProvider;
	private HTMLOutlineContentProvider fContentProvider;

	private HTMLParser fParser;
	private HTMLParseState fParseState;

	@Before
	public void setUp() throws Exception
	{
		fLabelProvider = new HTMLOutlineLabelProvider();
		fContentProvider = new HTMLOutlineContentProvider(null);
		fParser = new HTMLParser();
	}

	@After
	public void tearDown() throws Exception
	{
		fLabelProvider = null;
		fContentProvider = null;
		fParser = null;
		fParseState = null;
	}

	@Test
	public void testBasicOutline() throws Exception
	{
		String source = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n"
				+ "<html><head></head><body>Text</body></html>\n";
		fParseState = new HTMLParseState(source);
		IParseNode astRoot = parse();

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals(HTMLPlugin.getImage("icons/element.png"), fLabelProvider.getImage(outlineResult[0]));
		assertEquals("html", fLabelProvider.getText(outlineResult[0]));

		Object[] secondLevel = fContentProvider.getElements(outlineResult[0]);
		assertEquals(2, secondLevel.length);
		assertEquals("head", fLabelProvider.getText(secondLevel[0]));
		assertEquals("body", fLabelProvider.getText(secondLevel[1]));
	}

	@Test
	public void testIdAndClassAttributes() throws Exception
	{
		String source = "<div id=\"content\" class=\"name\"></div>";
		fParseState = new HTMLParseState(source);
		IParseNode astRoot = parse();

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals("div#content.name", fLabelProvider.getText(outlineResult[0]));
	}

	@Test
	public void testSrcAttribute() throws Exception
	{
		String source = "<script src=\"test.js\">";
		fParseState = new HTMLParseState(source);
		IParseNode astRoot = parse();

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals("script test.js", fLabelProvider.getText(outlineResult[0]));
	}

	@Test
	public void testHrefAttribute() throws Exception
	{
		String source = "<link href=\"stylesheet.css\">";
		fParseState = new HTMLParseState(source);
		IParseNode astRoot = parse();

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals("link stylesheet.css", fLabelProvider.getText(outlineResult[0]));
	}

	@Test
	public void testCommentFilter() throws Exception
	{
		String source = "<!-- this is a comment -->";
		fParseState = new HTMLParseState(source);
		IParseNode astRoot = parse();

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(0, outlineResult.length);
	}

	@Test
	public void testCustomAttributeFromPreference() throws Exception
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(HTMLPlugin.PLUGIN_ID);
		try
		{
			String source = "<meta charset=\"utf-8\">";
			fParseState = new HTMLParseState(source);
			IParseNode astRoot = parse();

			prefs.put(IPreferenceConstants.HTML_OUTLINE_TAG_ATTRIBUTES_TO_SHOW, "charset");

			Object[] outlineResult = fContentProvider.getElements(astRoot);
			assertEquals(1, outlineResult.length);
			assertEquals("meta utf-8", fLabelProvider.getText(outlineResult[0]));
		}
		finally
		{
			prefs.remove(IPreferenceConstants.HTML_OUTLINE_TAG_ATTRIBUTES_TO_SHOW);
		}
	}

	@Test
	public void testShowTextNode() throws Exception
	{
		String source = "some texts";
		fParseState = new HTMLParseState(source);
		IParseNode astRoot = parse();

		HTMLPreferenceUtil.setShowTextNodesInOutline(false);
		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(0, outlineResult.length);

		HTMLPreferenceUtil.setShowTextNodesInOutline(true);
		outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals("some texts", fLabelProvider.getText(outlineResult[0]));
	}

	@Test
	public void testInlineCSS() throws Exception
	{
		String source = "<td style=\"color: red;\"></td>";
		fParseState = new HTMLParseState(source);
		IParseNode astRoot = parse();

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals(astRoot.getChild(0), ((CommonOutlineItem) outlineResult[0]).getReferenceNode());

		Object[] cssChildren = fContentProvider.getElements(outlineResult[0]);
		assertEquals(1, cssChildren.length);

		IParseNode cssNode = ((CommonOutlineItem) cssChildren[0]).getReferenceNode();
		assertEquals(ICSSConstants.CONTENT_TYPE_CSS, cssNode.getLanguage());
		assertEquals(ICSSNodeTypes.DECLARATION, cssNode.getNodeType());
		assertEquals(11, cssNode.getStartingOffset());
		assertEquals(21, cssNode.getEndingOffset());
	}

	@Test
	public void testAPSTUD4178() throws Exception
	{
		String source = "<script>\n(function() {\nvar foo = function() {};\nfoo.bar = function() {};\n})();\n</script>";
		fParseState = new HTMLParseState(source);
		IParseNode astRoot = parse();

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals(astRoot.getChild(0), ((CommonOutlineItem) outlineResult[0]).getReferenceNode());

		Object[] childFoo = fContentProvider.getElements(outlineResult[0]);
		assertEquals(1, childFoo.length);
		assertEquals("foo()", fLabelProvider.getText(childFoo[0]));

		Object[] grandchildBar = fContentProvider.getElements(childFoo[0]);
		assertEquals(1, grandchildBar.length);
		assertEquals("bar()", fLabelProvider.getText(grandchildBar[0]));
	}

	private IParseRootNode parse() throws Exception
	{
		return fParser.parse(fParseState).getRootNode();
	}
}
