/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.outline;

import junit.framework.TestCase;

import com.aptana.editor.xml.XMLPlugin;
import com.aptana.editor.xml.parsing.XMLParser;
import com.aptana.parsing.ParseState;

public class XMLOutlineTest extends TestCase
{

	private XMLOutlineContentProvider fContentProvider;
	private XMLOutlineLabelProvider fLabelProvider;

	private XMLParser fParser;

	@Override
	protected void setUp() throws Exception
	{
		fContentProvider = new XMLOutlineContentProvider();
		fLabelProvider = new XMLOutlineLabelProvider();
		fParser = new XMLParser();
	}

	@Override
	protected void tearDown() throws Exception
	{
		if (fContentProvider != null)
		{
			fContentProvider.dispose();
			fContentProvider = null;
		}
		if (fLabelProvider != null)
		{
			fLabelProvider.dispose();
			fLabelProvider = null;
		}
		fParser = null;
	}

	public void testContent() throws Exception
	{
		String source = "<test></test>";
		ParseState parseState = new ParseState();
		parseState.setEditState(source, source, 0, 0);
		fParser.parse(parseState);

		Object[] elements = fContentProvider.getElements(parseState.getParseResult());
		assertEquals(1, elements.length);
		assertEquals("test", fLabelProvider.getText(elements[0]));
		assertEquals(XMLPlugin.getImage("icons/element.png"), fLabelProvider.getImage(elements[0]));
	}
	
	public void testContentWithAttributesShowsFirstAttributeValueInLabel() throws Exception
	{
		String source = "<test x=\"100\" y=\"10\"></test>";
		ParseState parseState = new ParseState();
		parseState.setEditState(source, source, 0, 0);
		fParser.parse(parseState);

		Object[] elements = fContentProvider.getElements(parseState.getParseResult());
		assertEquals(1, elements.length);
		assertEquals("test : 100", fLabelProvider.getText(elements[0]));
		assertEquals(XMLPlugin.getImage("icons/element.png"), fLabelProvider.getImage(elements[0]));
	}
}
