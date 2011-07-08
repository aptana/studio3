/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

@SuppressWarnings("nls")
public class YAMLCodeScannerTest extends AbstractTokenScannerTestCase
{
	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new YAMLCodeScanner();
	}

	public void testKey()
	{
		String src = "development:";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 12);
	}

	public void testAPSTUD2889()
	{
		String src = "receipt-one: Invoice";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 12);
		assertToken(new Token("string.unquoted.yaml"), 12, 1);
		assertToken(new Token("string.unquoted.yaml"), 13, 7);
	}

	public void testIndentedKey()
	{
		String src = "  adapter: sqlite3";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 10);
		assertToken(new Token("string.unquoted.yaml"), 10, 1); // FIXME Should be whitespace!
		assertToken(new Token("string.unquoted.yaml"), 11, 7);
	}

	public void testNumberValue()
	{
		String src = "  pool: 5";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 7);
		assertToken(new Token("string.unquoted.yaml"), 7, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.numeric.yaml"), 8, 1);
	}

	public void testDecimalNumberValue()
	{
		String src = "decimal: +12345";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 8);
		assertToken(new Token("string.unquoted.yaml"), 8, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.numeric.yaml"), 9, 6);
	}

	public void testOctalNumberValue()
	{
		String src = "octal: 0o14";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 6);
		assertToken(new Token("string.unquoted.yaml"), 6, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.numeric.yaml"), 7, 4);
	}

	public void testHexNumberValue()
	{
		String src = "hexadecimal: 0xC";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 12);
		assertToken(new Token("string.unquoted.yaml"), 12, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.numeric.yaml"), 13, 3);
	}

	public void testCanonicalFloatingPointValue()
	{
		String src = "canonical: 1.23015e+3";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 10);
		assertToken(new Token("string.unquoted.yaml"), 10, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.numeric.yaml"), 11, 10);
	}

	public void testExponentialFloatingPointValue()
	{
		String src = "exponential: 12.3015e+02";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 12);
		assertToken(new Token("string.unquoted.yaml"), 12, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.numeric.yaml"), 13, 11);
	}

	public void testFixedFloatingPointValue()
	{
		String src = "fixed: 1230.15";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 6);
		assertToken(new Token("string.unquoted.yaml"), 6, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.numeric.yaml"), 7, 7);
	}

	/*
	 * negative infinity: -.inf not a number: .NaN
	 */
	// TODO Add tests for infinity
	// TODO Add tests for negative numbers!
	// TODO Add tests for .NaN

	public void testUnquotedStringWithNumbers()
	{
		String src = "  database: db/test.sqlite3";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 11);
		assertToken(new Token("string.unquoted.yaml"), 11, 1); // FIXME Should be whitespace!
		assertToken(new Token("string.unquoted.yaml"), 12, 15);
	}

	public void testSimpleDateValue()
	{
		String src = "date: 2002-12-14";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 5);
		assertToken(new Token("string.unquoted.yaml"), 5, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.other.date.yaml"), 6, 10);
	}

	// TODO Add tests for more complex dates!
	/*
	 * canonical: 2001-12-15T02:59:43.1Z iso8601: 2001-12-14t21:59:43.10-05:00 spaced: 2001-12-14 21:59:43.10 -5
	 */

	public void testVariableDeclaration()
	{
		String src = "&SS Sammy Sosa";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("variable.other.yaml"), 0, 3);
		assertToken(new Token("string.unquoted.yaml"), 3, 1); // FIXME Should be whitespace!
		assertToken(new Token("string.unquoted.yaml"), 4, 5);
		assertToken(new Token("string.unquoted.yaml"), 9, 1); // FIXME Should be whitespace!
		assertToken(new Token("string.unquoted.yaml"), 10, 4);
	}

	public void testVariableReference()
	{
		String src = "*SS";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("variable.other.yaml"), 0, 3);
	}

	public void testMergeKey()
	{
		String src = "  <<: 2002-12-14";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 5);
		assertToken(new Token("string.unquoted.yaml"), 5, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.other.date.yaml"), 6, 10);
	}

	public void testValueWithLessThanLessThan()
	{
		String src = "key: <<value";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("entity.name.tag.yaml"), 0, 4);
		assertToken(new Token("string.unquoted.yaml"), 4, 1); // FIXME Should be whitespace!
		assertToken(new Token("string.unquoted.yaml"), 5, 1);
		assertToken(new Token("string.unquoted.yaml"), 6, 1);
		assertToken(new Token("string.unquoted.yaml"), 7, 5);
	}

	// TODO Add test for "---" not on beginning of line

	public void testDirectiveSeparator()
	{
		String src = "---";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("meta.separator.yaml"), 0, 3);
	}

	public void testDocumentSeparator()
	{
		String src = "...";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("meta.separator.yaml"), 0, 3);
	}

	public void testDocumentSeparatorNotAtBeginningOfLine()
	{
		String src = " ...";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("string.unquoted.yaml"), 0, 1); // FIXME Should be whitespace!
		assertToken(new Token("string.unquoted.yaml"), 1, 1);
		assertToken(new Token("string.unquoted.yaml"), 2, 1);
		assertToken(new Token("string.unquoted.yaml"), 3, 1);
	}

	public void testSimpleDocument()
	{
		String src = "---\nadapter: sqlite3\n...\ndate: 2004-10-01\nnumber: 123";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(new Token("meta.separator.yaml"), 0, 3); // ---
		assertToken(new Token("string.unquoted.yaml"), 3, 1); // FIXME Should be whitespace!
		assertToken(new Token("entity.name.tag.yaml"), 4, 8); // adapter:
		assertToken(new Token("string.unquoted.yaml"), 12, 1); // FIXME Should be whitespace!
		assertToken(new Token("string.unquoted.yaml"), 13, 7); // sqlite3
		assertToken(new Token("string.unquoted.yaml"), 20, 1); // FIXME Should be whitespace!
		assertToken(new Token("meta.separator.yaml"), 21, 3); // ...
		assertToken(new Token("string.unquoted.yaml"), 24, 1); // FIXME Should be whitespace!
		assertToken(new Token("entity.name.tag.yaml"), 25, 5); // date:
		assertToken(new Token("string.unquoted.yaml"), 30, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.other.date.yaml"), 31, 10); // 2004-10-01
		assertToken(new Token("string.unquoted.yaml"), 41, 1); // FIXME Should be whitespace!
		assertToken(new Token("entity.name.tag.yaml"), 42, 7); // number:
		assertToken(new Token("string.unquoted.yaml"), 49, 1); // FIXME Should be whitespace!
		assertToken(new Token("constant.numeric.yaml"), 50, 3); // 123
	}
}
