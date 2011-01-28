/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.dtd.parsing.lexer.DTDTokenType;

public class DTDSourceScannerTests extends TestCase
{
	private DTDSourceScanner _scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		this._scanner = new DTDSourceScanner()
		{
			/*
			 * (non-Javadoc)
			 * @see com.aptana.editor.dtd.DTDSourceScanner#createToken(com.aptana.editor.dtd.parsing.lexer.DTDTokenType)
			 */
			@Override
			protected IToken createToken(DTDTokenType type)
			{
				// TODO Auto-generated method stub
				return new Token(type);
			}
		};
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
	 * typeTests
	 * 
	 * @param source
	 * @param types
	 */
	public void typeTests(String source, DTDTokenType... types)
	{
		IDocument document = new Document(source);

		this._scanner.setRange(document, 0, source.length());

		for (DTDTokenType type : types)
		{
			IToken token = this._scanner.nextToken();
			Object data = token.getData();

			assertEquals(type, data);
		}
	}

	/**
	 * testEmptyComment
	 */
	public void testEmptyComment()
	{
		String source = "<!---->";

		this.typeTests(source, DTDTokenType.COMMENT);
	}

	/**
	 * testEmptyComment2
	 */
	public void testEmptyComment2()
	{
		String source = "<!-- -->";

		this.typeTests(source, DTDTokenType.COMMENT);
	}

	/**
	 * testComment
	 */
	public void testComment()
	{
		String source = "<!-- This is a simple comment -->";

		this.typeTests(source, DTDTokenType.COMMENT);
	}

	/**
	 * testPI
	 */
	public void testPI()
	{
		String source = "<?Test ?>";

		this.typeTests(source, DTDTokenType.PI);
	}

	/**
	 * testSectionStart
	 */
	public void testSectionStart()
	{
		String source = "<![";

		this.typeTests(source, DTDTokenType.SECTION_START);
	}

	/**
	 * testSectionEnd
	 */
	public void testSectionEnd()
	{
		String source = "]]>";

		this.typeTests(source, DTDTokenType.SECTION_END);
	}

	/**
	 * testAttList
	 */
	public void testAttList()
	{
		String source = "<!ATTLIST";

		this.typeTests(source, DTDTokenType.ATTLIST);
	}

	/**
	 * testElement
	 */
	public void testElement()
	{
		String source = "<!ELEMENT";

		this.typeTests(source, DTDTokenType.ELEMENT);
	}

	/**
	 * testNotation
	 */
	public void testNotation()
	{
		String source = "<!NOTATION";

		this.typeTests(source, DTDTokenType.NOTATION);
	}

	/**
	 * testFixed
	 */
	public void testFixed()
	{
		String source = "#FIXED";

		this.typeTests(source, DTDTokenType.FIXED);
	}

	/**
	 * testImplied
	 */
	public void testImplied()
	{
		String source = "#IMPLIED";

		this.typeTests(source, DTDTokenType.IMPLIED);
	}

	/**
	 * testPCData
	 */
	public void testPCData()
	{
		String source = "#PCDATA";

		this.typeTests(source, DTDTokenType.PCDATA);
	}

	/**
	 * testRequired
	 */
	public void testRequired()
	{
		String source = "#REQUIRED";

		this.typeTests(source, DTDTokenType.REQUIRED);
	}

	/**
	 * testAny
	 */
	public void testAny()
	{
		String source = "ANY";

		this.typeTests(source, DTDTokenType.ANY);
	}

	/**
	 * testCDataType
	 */
	public void testCDataType()
	{
		String source = "CDATA";

		this.typeTests(source, DTDTokenType.CDATA_TYPE);
	}

	/**
	 * testEmpty
	 */
	public void testEmpty()
	{
		String source = "EMPTY";

		this.typeTests(source, DTDTokenType.EMPTY);
	}

	/**
	 * testEntityType
	 */
	public void testEntityType()
	{
		String source = "ENTITY";

		this.typeTests(source, DTDTokenType.ENTITY_TYPE);
	}

	/**
	 * testEntitiesType
	 */
	public void testEntitiesType()
	{
		String source = "ENTITIES";

		this.typeTests(source, DTDTokenType.ENTITIES_TYPE);
	}

	/**
	 * testIDType
	 */
	public void testIDType()
	{
		String source = "ID";

		this.typeTests(source, DTDTokenType.ID_TYPE);
	}

	/**
	 * testIDRefType
	 */
	public void testIDRefType()
	{
		String source = "IDREF";

		this.typeTests(source, DTDTokenType.IDREF_TYPE);
	}

	/**
	 * testIDRefsType
	 */
	public void testIDRefsType()
	{
		String source = "IDREFS";

		this.typeTests(source, DTDTokenType.IDREFS_TYPE);
	}

	/**
	 * testIgnore
	 */
	public void testIgnore()
	{
		String source = "IGNORE";

		this.typeTests(source, DTDTokenType.IGNORE);
	}

	/**
	 * testInclude
	 */
	public void testInclude()
	{
		String source = "INCLUDE";

		this.typeTests(source, DTDTokenType.INCLUDE);
	}

	/**
	 * testNDataType
	 */
	public void testNDataType()
	{
		String source = "NDATA";

		this.typeTests(source, DTDTokenType.NDATA);
	}

	/**
	 * testNMTokenType
	 */
	public void testNMTokenType()
	{
		String source = "NMTOKEN";

		this.typeTests(source, DTDTokenType.NMTOKEN_TYPE);
	}

	/**
	 * testNMTokensType
	 */
	public void testNMTokensType()
	{
		String source = "NMTOKENS";

		this.typeTests(source, DTDTokenType.NMTOKENS_TYPE);
	}

	/**
	 * testNotationType
	 */
	public void testNotationType()
	{
		String source = "NOTATION";

		this.typeTests(source, DTDTokenType.NOTATION_TYPE);
	}

	/**
	 * testPublicType
	 */
	public void testPublicType()
	{
		String source = "PUBLIC";

		this.typeTests(source, DTDTokenType.PUBLIC);
	}

	/**
	 * testSystemType
	 */
	public void testSystemType()
	{
		String source = "SYSTEM";

		this.typeTests(source, DTDTokenType.SYSTEM);
	}

	/**
	 * testPERef
	 */
	public void testPERef()
	{
		String source = "%PERef;";

		this.typeTests(source, DTDTokenType.PE_REF);
	}

	/**
	 * testGreaterThan
	 */
	public void testGreaterThan()
	{
		String source = ">";

		this.typeTests(source, DTDTokenType.GREATER_THAN);
	}

	/**
	 * testLeftParen
	 */
	public void testLeftParen()
	{
		String source = "(";

		this.typeTests(source, DTDTokenType.LPAREN);
	}

	/**
	 * testPipe
	 */
	public void testPipe()
	{
		String source = "|";

		this.typeTests(source, DTDTokenType.PIPE);
	}

	/**
	 * testRightParen
	 */
	public void testRightParen()
	{
		String source = ")";

		this.typeTests(source, DTDTokenType.RPAREN);
	}

	/**
	 * testQuestion
	 */
	public void testQuestion()
	{
		String source = "?";

		this.typeTests(source, DTDTokenType.QUESTION);
	}

	/**
	 * testAsterisk
	 */
	public void testAsterisk()
	{
		String source = "*";

		this.typeTests(source, DTDTokenType.STAR);
	}

	/**
	 * testPlus
	 */
	public void testPlus()
	{
		String source = "+";

		this.typeTests(source, DTDTokenType.PLUS);
	}

	/**
	 * testComma
	 */
	public void testComma()
	{
		String source = ",";

		this.typeTests(source, DTDTokenType.COMMA);
	}

	/**
	 * testPercent
	 */
	public void testPercent()
	{
		String source = "%";

		this.typeTests(source, DTDTokenType.PERCENT);
	}

	/**
	 * testLeftBracket
	 */
	public void testLeftBracket()
	{
		String source = "[";

		this.typeTests(source, DTDTokenType.LBRACKET);
	}

	/**
	 * testName
	 */
	public void testName()
	{
		String source = "Name";

		this.typeTests(source, DTDTokenType.NAME);
	}

	/**
	 * testNmtoken
	 */
	public void testNmtoken()
	{
		String source = "200";

		this.typeTests(source, DTDTokenType.NMTOKEN);
	}

	/**
	 * testSequence
	 */
	public void testSequence()
	{
		String source = "<![%svg-prefw-redecl.module;[%svg-prefw-redecl.mod;]]>";

		this.typeTests(source, DTDTokenType.SECTION_START, DTDTokenType.PE_REF, DTDTokenType.LBRACKET, DTDTokenType.PE_REF, DTDTokenType.SECTION_END);
	}
}
