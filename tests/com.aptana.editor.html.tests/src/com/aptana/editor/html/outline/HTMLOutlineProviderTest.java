/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.outline;

import junit.framework.TestCase;

import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLParser;
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

	public void testMultipleSelectors() throws Exception
	{
		String source = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n"
				+ "<html><head></head><body>Text</body></html>\n";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode result = fParser.parse(fParseState);

		Object[] selectors = fContentProvider.getElements(result);
		assertEquals(1, selectors.length);
		assertEquals("html", fLabelProvider.getText(selectors[0]));
		assertEquals(HTMLPlugin.getImage("icons/element.png"), fLabelProvider.getImage(selectors[0]));
	}
}
