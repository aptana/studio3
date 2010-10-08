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
