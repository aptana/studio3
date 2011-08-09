/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class SourcePrinterTest extends TestCase
{

	public void testGetIndexString()
	{
		String indexString = "  ";
		SourcePrinter printer = new SourcePrinter(indexString);
		printer.increaseIndent();
		assertEquals(indexString, printer.getIndentString());
	}

	public void testEmptyStringIndentText()
	{
		SourcePrinter printer = new SourcePrinter(StringUtil.EMPTY);
		printer.increaseIndent();
		printer.increaseIndent();
		assertTrue(printer.getIndentLevel() == 0);

	}

	public void testIncreaseIndentLevel()
	{
		SourcePrinter printer = new SourcePrinter("  ");
		printer.increaseIndent();
		printer.increaseIndent();
		printer.increaseIndent();

		assertTrue(printer.getIndentLevel() == 3);

	}

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

	public void testLineDelimiter()
	{
		SourcePrinter printer = new SourcePrinter();
		String delimiter = "\n";

		assertEquals(System.getProperty("line.separator"), printer.getLineDelimeter());

		printer.setLineDelimeter(delimiter);
		assertEquals(delimiter, printer.getLineDelimeter());
	}

	public void testPrintln()
	{
		SourcePrinter printer = new SourcePrinter();
		printer.print("This is a test");
		printer.println();

		SourcePrinter printer2 = new SourcePrinter();
		printer2.println("This is a test");

		assertEquals(printer.toString(), printer2.toString());
	}

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

	public void testPrintObject()
	{
		SourcePrinter printer = new SourcePrinter("  ");
		IPath path = new Path("AptanaStudio.exe");
		printer.print("File: ");
		printer.print(path);

		assertEquals("File: AptanaStudio.exe", printer.toString());
	}

	public void testPrintIndent()
	{
		SourcePrinter printer = new SourcePrinter("  ");
		printer.increaseIndent();
		printer.print("Aptana");
		printer.printIndent();
		printer.print("Studio");

		assertEquals("Aptana  Studio", printer.toString());
	}

	public void testPrintWithIndent()
	{
		SourcePrinter printer = new SourcePrinter();
		printer.increaseIndent();
		printer.printWithIndent("Testing");

		assertEquals("  Testing", printer.getBuffer().toString());

	}
}
