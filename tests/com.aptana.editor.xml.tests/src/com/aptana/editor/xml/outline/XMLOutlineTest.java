/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.outline;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import com.aptana.editor.xml.XMLPlugin;
import com.aptana.parsing.ParseState;
import com.aptana.xml.core.parsing.XMLParser;

public class XMLOutlineTest
{

	private XMLOutlineContentProvider fContentProvider;
	private XMLOutlineLabelProvider fLabelProvider;

	private XMLParser fParser;

//	@Override
	@Before
	public void setUp() throws Exception
	{
		fContentProvider = new XMLOutlineContentProvider();
		fLabelProvider = new XMLOutlineLabelProvider();
		fParser = new XMLParser();
	}

//	@Override
	@After
	public void tearDown() throws Exception
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

	@Test
	public void testContent() throws Exception
	{
		String source = "<test></test>";
		ParseState parseState = new ParseState(source);

		Object[] elements = fContentProvider.getElements(fParser.parse(parseState).getRootNode());
		assertEquals(1, elements.length);
		assertEquals("test", fLabelProvider.getText(elements[0]));
		assertEquals(XMLPlugin.getImage("icons/element.png"), fLabelProvider.getImage(elements[0]));
	}
	
	@Test
	public void testContentWithAttributesShowsFirstAttributeValueInLabel() throws Exception
	{
		String source = "<test x=\"100\" y=\"10\"></test>";
		ParseState parseState = new ParseState(source);

		Object[] elements = fContentProvider.getElements(fParser.parse(parseState).getRootNode());
		assertEquals(1, elements.length);
		assertEquals("test : 100", fLabelProvider.getText(elements[0]));
		assertEquals(XMLPlugin.getImage("icons/element.png"), fLabelProvider.getImage(elements[0]));
	}
}
