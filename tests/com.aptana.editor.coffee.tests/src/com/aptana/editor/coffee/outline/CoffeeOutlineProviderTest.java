/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.outline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.editor.coffee.CoffeeScriptEditorPlugin;
import com.aptana.editor.coffee.ICoffeeUIConstants;
import com.aptana.editor.coffee.parsing.CoffeeParser;
import com.aptana.editor.coffee.parsing.lexer.CoffeeScanner;

public class CoffeeOutlineProviderTest
{

	private CoffeeOutlineContentProvider fContentProvider;
	private CoffeeOutlineLabelProvider fLabelProvider;
	private CoffeeParser fParser;
	private CoffeeScanner fScanner;

	@Before
	public void setUp() throws Exception
	{
		fContentProvider = new CoffeeOutlineContentProvider();
		fLabelProvider = new CoffeeOutlineLabelProvider();
		fParser = new CoffeeParser();
		fScanner = new CoffeeScanner();
	}

	@After
	public void tearDown() throws Exception
	{
		fContentProvider = null;
		fLabelProvider = null;
		fParser = null;
		fScanner = null;
	}

	@Test
	public void testAssignment() throws Exception
	{
		String source = "number = 42\nopposite = true\nstring = \"text\"\nnullVar = null";
		fScanner.setSource(source);
		Object result = fParser.parse(fScanner);

		Object[] children = fContentProvider.getChildren(result);
		assertEquals(4, children.length);

		assertEquals("number", fLabelProvider.getText(children[0]));
		assertEquals(CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.NUMBER_ICON),
				fLabelProvider.getImage(children[0]));

		assertEquals("opposite", fLabelProvider.getText(children[1]));
		assertEquals(CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.BOOLEAN_ICON),
				fLabelProvider.getImage(children[1]));

		assertEquals("string", fLabelProvider.getText(children[2]));
		assertEquals(CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.STRING_ICON),
				fLabelProvider.getImage(children[2]));

		assertEquals("nullVar", fLabelProvider.getText(children[3]));
		assertEquals(CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.NULL_ICON),
				fLabelProvider.getImage(children[3]));
	}

	@Test
	public void testFunction() throws Exception
	{
		String source = "square = (x) -> x * x";
		fScanner.setSource(source);
		Object result = fParser.parse(fScanner);

		Object[] children = fContentProvider.getChildren(result);
		assertEquals(1, children.length);

		assertEquals("square(x)", fLabelProvider.getText(children[0]));
		assertEquals(CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.FUNCTION_ICON),
				fLabelProvider.getImage(children[0]));
	}

	@Test
	public void testArray() throws Exception
	{
		String source = "list = [1, 2, 3, 4, 5]";
		fScanner.setSource(source);
		Object result = fParser.parse(fScanner);

		Object[] children = fContentProvider.getChildren(result);
		assertEquals(1, children.length);

		assertEquals("list", fLabelProvider.getText(children[0]));
		assertEquals(CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.ARRAY_ICON),
				fLabelProvider.getImage(children[0]));
	}

	@Test
	public void testObject() throws Exception
	{
		String source = "math =\n  root:   Math.sqrt\n  square: square\n  cube:   (x) -> x * square x";
		fScanner.setSource(source);
		Object result = fParser.parse(fScanner);

		Object[] children = fContentProvider.getChildren(result);
		assertEquals(1, children.length);

		assertEquals("math", fLabelProvider.getText(children[0]));
		assertEquals(CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.OBJECT_ICON),
				fLabelProvider.getImage(children[0]));
	}

	@Test
	public void testSplat() throws Exception
	{
		String source = "race = (winner, runners...) ->\n  print winner, runners";
		fScanner.setSource(source);
		Object result = fParser.parse(fScanner);

		Object[] children = fContentProvider.getChildren(result);
		assertEquals(1, children.length);

		assertEquals("race(winner, runners)", fLabelProvider.getText(children[0]));
		assertEquals(CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.FUNCTION_ICON),
				fLabelProvider.getImage(children[0]));

		Object[] grandchildren = fContentProvider.getChildren(children[0]);
		assertEquals(1, grandchildren.length);

		assertEquals("Call", fLabelProvider.getText(grandchildren[0]));
		assertNull(fLabelProvider.getImage(grandchildren[0]));

		Object[] grandgrandchildren = fContentProvider.getChildren(grandchildren[0]);
		assertEquals(3, grandgrandchildren.length);

		assertEquals("print", fLabelProvider.getText(grandgrandchildren[0]));
		assertNull(fLabelProvider.getImage(grandgrandchildren[0]));
		assertEquals("winner", fLabelProvider.getText(grandgrandchildren[1]));
		assertNull(fLabelProvider.getImage(grandgrandchildren[1]));
		assertEquals("runners", fLabelProvider.getText(grandgrandchildren[2]));
		assertNull(fLabelProvider.getImage(grandgrandchildren[2]));
	}

	@Test
	public void testClass() throws Exception
	{
		String source = "class Foo\n  constructor: (@name) ->";
		fScanner.setSource(source);
		Object result = fParser.parse(fScanner);

		Object[] children = fContentProvider.getChildren(result);
		assertEquals(1, children.length);

		assertEquals("Foo", fLabelProvider.getText(children[0]));
		assertEquals(CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.CLASS_ICON),
				fLabelProvider.getImage(children[0]));
	}
}
