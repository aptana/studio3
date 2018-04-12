/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

public class SourcePrinterTest
{

	@Test
	public void testGetIndexString()
	{
		String indexString = "  ";
		SourcePrinter printer = new SourcePrinter(indexString);
		printer.increaseIndent();
		assertEquals(indexString, printer.getIndentString());
	}

	@Test
	public void testEmptyStringIndentText()
	{
		SourcePrinter printer = new SourcePrinter(StringUtil.EMPTY);
		printer.increaseIndent();
		printer.increaseIndent();
		assertTrue(printer.getIndentLevel() == 0);

	}

	@Test
	public void testIncreaseIndentLevel()
	{
		SourcePrinter printer = new SourcePrinter("  ");
		printer.increaseIndent();
		printer.increaseIndent();
		printer.increaseIndent();

		assertTrue(printer.getIndentLevel() == 3);

	}

	@Test
	public void testDecreaseIndentLevel()
	{
		SourcePrinter printer = new SourcePrinter(" ");
		printer.increaseIndent();
		printer.increaseIndent();
		printer.increaseIndent();

		printer.decreaseIndent();
		printer.decreaseIndent();
		printer.decreaseIndent();

		assertTrue(printer.getIndentLevel() == 0);

	}

	@Test
	public void testLineDelimiter()
	{
		SourcePrinter printer = new SourcePrinter();
		String delimiter = "\n";

		assertEquals(System.getProperty("line.separator"), printer.getLineDelimeter());

		printer.setLineDelimeter(delimiter);
		assertEquals(delimiter, printer.getLineDelimeter());
	}

	@Test
	public void testPrintln()
	{
		SourcePrinter printer = new SourcePrinter();
		printer.print("This is a test");
		printer.println();

		SourcePrinter printer2 = new SourcePrinter();
		printer2.println("This is a test");

		assertEquals(printer.toString(), printer2.toString());
	}

	@Test
	public void testComplexSource()
	{
		SourcePrinter printer = new SourcePrinter("  ");
		printer.print("Aptana Studi");
		printer.print('o');
		printer.println();
		printer.increaseIndent();
		printer.printlnWithIndent("is");
		printer.decreaseIndent();
		printer.print("awesome!");

		assertEquals("Aptana Studio\n  is\nawesome!", printer.getBuffer().toString());
	}

	@Test
	public void testPrintObject()
	{
		SourcePrinter printer = new SourcePrinter("  ");
		IPath path = new Path("AptanaStudio.exe");
		printer.print("File: ");
		printer.print(path);

		assertEquals("File: AptanaStudio.exe", printer.toString());
	}

	@Test
	public void testPrintIndent()
	{
		SourcePrinter printer = new SourcePrinter("  ");
		printer.increaseIndent();
		printer.print("Aptana");
		printer.printIndent();
		printer.print("Studio");

		assertEquals("Aptana  Studio", printer.toString());
	}

	@Test
	public void testPrintWithIndent()
	{
		SourcePrinter printer = new SourcePrinter();
		printer.increaseIndent();
		printer.printWithIndent("Testing");

		assertEquals("  Testing", printer.getBuffer().toString());
	}

	@Test
	public void testPrintLnText()
	{
		SourcePrinter printer = new SourcePrinter();
		printer.print('a');
		printer.print('b');
		printer.println('c');

		assertEquals("abc\n", printer.toString());
	}

	@Test
	public void testPrintCharTextWithIndent()
	{
		SourcePrinter printer = new SourcePrinter();
		printer.increaseIndent();
		printer.printWithIndent('a');

		assertEquals("  a", printer.toString());
	}

	@Test
	public void testPrintLnCharTextWithIndent()
	{
		SourcePrinter printer = new SourcePrinter();
		printer.increaseIndent();
		printer.printlnWithIndent('a');

		assertEquals("  a\n", printer.toString());
	}
}
