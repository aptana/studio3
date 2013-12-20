/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.dtd.core.parsing.DTDTokenType;
import com.aptana.editor.dtd.text.rules.DTDTagScanner;

public class DTDTagScannerTest {
	
	private DTDTagScanner scanner;

//	@Override
	@Before
	public void setUp() throws Exception {
//		super.setUp();
		scanner = new DTDTagScanner() {
			@Override
			protected IToken createToken(DTDTokenType type) {
				return new Token(type);
			}
		};
	}

//	@Override
	@After
	public void tearDown() throws Exception {
		scanner = null;
//		super.tearDown();
	}

	/**
	 * typeTests
	 * 
	 * @param source
	 * @param types
	 */
	public void typeTests(String source, DTDTokenType... types) {
		IDocument document = new Document(source);
		scanner.setRange(document, 0, source.length());
		for (DTDTokenType type : types) {
			IToken token = scanner.nextToken();
			Object data = token.getData();
			assertEquals(type, data);
		}
	}

	/**
	 * testAttList
	 */
	@Test
	public void testAttList() {
		String source = "<!ATTLIST";
		typeTests(source, DTDTokenType.ATTLIST);
	}

	/**
	 * testElement
	 */
	@Test
	public void testElement() {
		String source = "<!ELEMENT";
		typeTests(source, DTDTokenType.ELEMENT);
	}

	/**
	 * testNotation
	 */
	@Test
	public void testNotation() {
		String source = "<!NOTATION";
		typeTests(source, DTDTokenType.NOTATION);
	}

	/**
	 * testFixed
	 */
	@Test
	public void testFixed() {
		String source = "#FIXED";
		typeTests(source, DTDTokenType.FIXED);
	}

	/**
	 * testImplied
	 */
	@Test
	public void testImplied() {
		String source = "#IMPLIED";
		typeTests(source, DTDTokenType.IMPLIED);
	}

	/**
	 * testPCData
	 */
	@Test
	public void testPCData() {
		String source = "#PCDATA";
		typeTests(source, DTDTokenType.PCDATA);
	}

	/**
	 * testRequired
	 */
	@Test
	public void testRequired() {
		String source = "#REQUIRED";
		typeTests(source, DTDTokenType.REQUIRED);
	}

	/**
	 * testAny
	 */
	@Test
	public void testAny() {
		String source = "ANY";
		typeTests(source, DTDTokenType.ANY);
	}

	/**
	 * testCDataType
	 */
	@Test
	public void testCDataType() {
		String source = "CDATA";
		typeTests(source, DTDTokenType.CDATA_TYPE);
	}

	/**
	 * testEmpty
	 */
	@Test
	public void testEmpty() {
		String source = "EMPTY";
		typeTests(source, DTDTokenType.EMPTY);
	}

	/**
	 * testEntityType
	 */
	@Test
	public void testEntityType() {
		String source = "ENTITY";
		typeTests(source, DTDTokenType.ENTITY_TYPE);
	}

	/**
	 * testEntitiesType
	 */
	@Test
	public void testEntitiesType() {
		String source = "ENTITIES";
		typeTests(source, DTDTokenType.ENTITIES_TYPE);
	}

	/**
	 * testIDType
	 */
	@Test
	public void testIDType() {
		String source = "ID";
		typeTests(source, DTDTokenType.ID_TYPE);
	}

	/**
	 * testIDRefType
	 */
	@Test
	public void testIDRefType() {
		String source = "IDREF";
		typeTests(source, DTDTokenType.IDREF_TYPE);
	}

	/**
	 * testIDRefsType
	 */
	@Test
	public void testIDRefsType() {
		String source = "IDREFS";
		typeTests(source, DTDTokenType.IDREFS_TYPE);
	}

	/**
	 * testIgnore
	 */
	@Test
	public void testIgnore() {
		String source = "IGNORE";
		typeTests(source, DTDTokenType.IGNORE);
	}

	/**
	 * testInclude
	 */
	@Test
	public void testInclude() {
		String source = "INCLUDE";
		typeTests(source, DTDTokenType.INCLUDE);
	}

	/**
	 * testNDataType
	 */
	@Test
	public void testNDataType() {
		String source = "NDATA";
		typeTests(source, DTDTokenType.NDATA);
	}

	/**
	 * testNMTokenType
	 */
	@Test
	public void testNMTokenType() {
		String source = "NMTOKEN";
		typeTests(source, DTDTokenType.NMTOKEN_TYPE);
	}

	/**
	 * testNMTokensType
	 */
	@Test
	public void testNMTokensType() {
		String source = "NMTOKENS";
		typeTests(source, DTDTokenType.NMTOKENS_TYPE);
	}

	/**
	 * testNotationType
	 */
	@Test
	public void testNotationType() {
		String source = "NOTATION";
		typeTests(source, DTDTokenType.NOTATION_TYPE);
	}

	/**
	 * testPublicType
	 */
	@Test
	public void testPublicType() {
		String source = "PUBLIC";
		typeTests(source, DTDTokenType.PUBLIC);
	}

	/**
	 * testSystemType
	 */
	@Test
	public void testSystemType() {
		String source = "SYSTEM";
		typeTests(source, DTDTokenType.SYSTEM);
	}

	/**
	 * testPERef
	 */
	@Test
	public void testPERef() {
		String source = "%PERef;";
		typeTests(source, DTDTokenType.PE_REF);
	}

	/**
	 * testGreaterThan
	 */
	@Test
	public void testGreaterThan() {
		String source = ">";
		typeTests(source, DTDTokenType.GREATER_THAN);
	}

	/**
	 * testLeftParen
	 */
	@Test
	public void testLeftParen() {
		String source = "(";
		typeTests(source, DTDTokenType.LPAREN);
	}

	/**
	 * testPipe
	 */
	@Test
	public void testPipe() {
		String source = "|";
		typeTests(source, DTDTokenType.PIPE);
	}

	/**
	 * testRightParen
	 */
	@Test
	public void testRightParen() {
		String source = ")";
		typeTests(source, DTDTokenType.RPAREN);
	}

	/**
	 * testQuestion
	 */
	@Test
	public void testQuestion() {
		String source = "?";
		typeTests(source, DTDTokenType.QUESTION);
	}

	/**
	 * testAsterisk
	 */
	@Test
	public void testAsterisk() {
		String source = "*";
		typeTests(source, DTDTokenType.STAR);
	}

	/**
	 * testPlus
	 */
	@Test
	public void testPlus() {
		String source = "+";
		typeTests(source, DTDTokenType.PLUS);
	}

	/**
	 * testComma
	 */
	@Test
	public void testComma() {
		String source = ",";
		typeTests(source, DTDTokenType.COMMA);
	}

	/**
	 * testPercent
	 */
	@Test
	public void testPercent() {
		String source = "%";
		typeTests(source, DTDTokenType.PERCENT);
	}

	/**
	 * testLeftBracket
	 */
	@Test
	public void testLeftBracket() {
		String source = "[";
		typeTests(source, DTDTokenType.LBRACKET);
	}

	/**
	 * testName
	 */
	@Test
	public void testName() {
		String source = "Name";
		typeTests(source, DTDTokenType.NAME);
	}

	/**
	 * testNmtoken
	 */
	@Test
	public void testNmtoken() {
		String source = "200";
		typeTests(source, DTDTokenType.NMTOKEN);
	}

	/**
	 * testSectionStart
	 */
	@Test
	public void testSectionStart() {
		String source = "<![";
		typeTests(source, DTDTokenType.SECTION_START);
	}

	/**
	 * testSectionEnd
	 */
	@Test
	public void testSectionEnd() {
		String source = "]]>";
		typeTests(source, DTDTokenType.SECTION_END);
	}

	/**
	 * testSequence
	 */
	@Test
	public void testSequence() {
		String source = "<![%svg-prefw-redecl.module;[%svg-prefw-redecl.mod;]]>";
		typeTests(source, DTDTokenType.SECTION_START, DTDTokenType.PE_REF, DTDTokenType.LBRACKET, DTDTokenType.PE_REF, DTDTokenType.SECTION_END);
	}
}
