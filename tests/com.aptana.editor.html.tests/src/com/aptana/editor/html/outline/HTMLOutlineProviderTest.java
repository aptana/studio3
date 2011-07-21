/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.outline;

import junit.framework.TestCase;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.editor.html.preferences.IPreferenceConstants;
import com.aptana.parsing.ast.IParseNode;

public class HTMLOutlineProviderTest extends TestCase
{

	private HTMLOutlineLabelProvider fLabelProvider;
	private HTMLOutlineContentProvider fContentProvider;

	private HTMLParser fParser;
	private HTMLParseState fParseState;

	@Override
	protected void setUp() throws Exception
	{
		fLabelProvider = new HTMLOutlineLabelProvider();
		fContentProvider = new HTMLOutlineContentProvider();
		fParser = new HTMLParser();
		fParseState = new HTMLParseState();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fLabelProvider = null;
		fContentProvider = null;
		fParser = null;
		fParseState = null;
	}

	public void testBasicOutline() throws Exception
	{
		String source = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n"
				+ "<html><head></head><body>Text</body></html>\n";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode astRoot = fParser.parse(fParseState);

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals(HTMLPlugin.getImage("icons/element.png"), fLabelProvider.getImage(outlineResult[0]));
		assertEquals("html", fLabelProvider.getText(outlineResult[0]));

		Object[] secondLevel = fContentProvider.getElements(outlineResult[0]);
		assertEquals(2, secondLevel.length);
		assertEquals("head", fLabelProvider.getText(secondLevel[0]));
		assertEquals("body", fLabelProvider.getText(secondLevel[1]));
	}

	public void testIdAndClassAttributes() throws Exception
	{
		String source = "<div id=\"content\" class=\"name\"></div>";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode astRoot = fParser.parse(fParseState);

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals("div#content.name", fLabelProvider.getText(outlineResult[0]));
	}

	public void testSrcAttribute() throws Exception
	{
		String source = "<script src=\"test.js\">";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode astRoot = fParser.parse(fParseState);

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals("script test.js", fLabelProvider.getText(outlineResult[0]));
	}

	public void testHrefAttribute() throws Exception
	{
		String source = "<link src=\"stylesheet.css\">";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode astRoot = fParser.parse(fParseState);

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals("link stylesheet.css", fLabelProvider.getText(outlineResult[0]));
	}

	public void testCommentFilter() throws Exception
	{
		String source = "<!-- this is a comment -->";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode astRoot = fParser.parse(fParseState);

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(0, outlineResult.length);
	}

	public void testCustomAttributeFromPreference() throws Exception
	{
		String source = "<meta charset=\"utf-8\">";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode astRoot = fParser.parse(fParseState);

		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(HTMLPlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.HTML_OUTLINE_TAG_ATTRIBUTES_TO_SHOW, "charset");

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals("meta utf-8", fLabelProvider.getText(outlineResult[0]));
	}

	public void testShowTextNode() throws Exception
	{
		String source = "some texts";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode astRoot = fParser.parse(fParseState);

		Object[] outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(0, outlineResult.length);

		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(HTMLPlugin.PLUGIN_ID);
		prefs.putBoolean(IPreferenceConstants.HTML_OUTLINE_SHOW_TEXT_NODES, true);

		outlineResult = fContentProvider.getElements(astRoot);
		assertEquals(1, outlineResult.length);
		assertEquals("some texts", fLabelProvider.getText(outlineResult[0]));
	}
}
