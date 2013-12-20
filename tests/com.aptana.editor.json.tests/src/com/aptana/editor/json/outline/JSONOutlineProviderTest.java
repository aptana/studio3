/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.outline;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.editor.json.JSONPlugin;
import com.aptana.json.core.parsing.JSONParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ParseState;

public class JSONOutlineProviderTest
{

	private JSONOutlineContentProvider fContentProvider;
	private JSONOutlineLabelProvider fLabelProvider;
	private JSONParser fParser;

	@Before
	public void setUp() throws Exception
	{
		fContentProvider = new JSONOutlineContentProvider();
		fLabelProvider = new JSONOutlineLabelProvider();
		fParser = new JSONParser();
	}

	@After
	public void tearDown() throws Exception
	{
		fContentProvider = null;
		fLabelProvider = null;
		fParser = null;
	}

	@Test
	public void testOutline() throws Exception
	{
		String source = "{\n\"name\": \"Product\",\n\"properties\": {\n\"required\": true,\n\"width\": 1024,\n\"optional\": null,\n\"days\": [\"Sunday\", \"Saturday\"]\n}\n}";
		IParseState parseState = new ParseState(source);
		ParseResult result = fParser.parse(parseState);

		Object[] children = fContentProvider.getChildren(result.getRootNode());
		assertEquals(1, children.length);
		assertEquals("<Object>: Product", fLabelProvider.getText(children[0]));
		assertEquals(JSONPlugin.getImage("icons/object-literal.png"), fLabelProvider.getImage(children[0]));

		Object[] grandchildren = fContentProvider.getChildren(children[0]);
		assertEquals(2, grandchildren.length);
		assertEquals("name", fLabelProvider.getText(grandchildren[0]));
		assertEquals(JSONPlugin.getImage("icons/string.png"), fLabelProvider.getImage(grandchildren[0]));
		assertEquals("properties", fLabelProvider.getText(grandchildren[1]));
		assertEquals(JSONPlugin.getImage("icons/object-literal.png"), fLabelProvider.getImage(grandchildren[1]));

		Object[] grandgrandchildren = fContentProvider.getChildren(grandchildren[1]);
		assertEquals(4, grandgrandchildren.length);
		assertEquals("required", fLabelProvider.getText(grandgrandchildren[0]));
		assertEquals(JSONPlugin.getImage("icons/boolean.png"), fLabelProvider.getImage(grandgrandchildren[0]));
		assertEquals("width", fLabelProvider.getText(grandgrandchildren[1]));
		assertEquals(JSONPlugin.getImage("icons/number.png"), fLabelProvider.getImage(grandgrandchildren[1]));
		assertEquals("optional", fLabelProvider.getText(grandgrandchildren[2]));
		assertEquals(JSONPlugin.getImage("icons/null.png"), fLabelProvider.getImage(grandgrandchildren[2]));
		assertEquals("days", fLabelProvider.getText(grandgrandchildren[3]));
		assertEquals(JSONPlugin.getImage("icons/array-literal.png"), fLabelProvider.getImage(grandgrandchildren[3]));
	}
}
