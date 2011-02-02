/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.parsing;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;

import com.aptana.editor.js.sdoc.lexer.SDocTokenType;

public abstract class SDocScannerTestBase extends TestCase
{
	private RuleBasedScanner _scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		this._scanner = this.createScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		this._scanner = null;

		super.tearDown();
	}

	/**
	 * createScanner
	 * 
	 * @return
	 */
	protected abstract RuleBasedScanner createScanner();

	/**
	 * lexemeTypeTests
	 * 
	 * @param source
	 * @param types
	 */
	protected void lexemeTypeTests(String source, SDocTokenType... types)
	{
		IDocument document = new Document(source);

		this._scanner.setRange(document, 0, source.length());

		for (int i = 0; i < types.length; i++)
		{
			SDocTokenType type = types[i];
			IToken token = this._scanner.nextToken();
			Object data = token.getData();

			assertEquals("at index " + i, type, data);
		}
	}
}
