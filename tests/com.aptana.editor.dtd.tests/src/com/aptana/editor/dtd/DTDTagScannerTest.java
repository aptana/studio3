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

import com.aptana.dtd.core.parsing.DTDTokenType;
import com.aptana.editor.dtd.text.rules.DTDTagScanner;

public class DTDTagScannerTest extends TestCase {
	
	private DTDTagScanner scanner;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		scanner = new DTDTagScanner() {
			@Override
			protected IToken createToken(DTDTokenType type) {
				return new Token(type);
			}
		};
	}

	@Override
	protected void tearDown() throws Exception {
		scanner = null;
		super.tearDown();
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
	public void testAttList() {
		String source = "<!ATTLIST";
		typeTests(source, DTDTokenType.ATTLIST);
	}

	/**
	 * testElement
	 */
	public void testElement() {
		String source = "<!ELEMENT";
		typeTests(source, DTDTokenType.ELEMENT);
	}

	/**
	 * testNotation
	 */
	public void testNotation() {
		String source = "<!NOTATION";
		typeTests(source, DTDTokenType.NOTATION);
	}

	/**
	 * testFixed
	 */
	public void testFixed() {
		String source = "#FIXED";
		typeTests(source, DTDTokenType.FIXED);
	}

	/**
	 * testImplied
	 */
	public void testImplied() {
		String source = "#IMPLIED";
		typeTests(source, DTDTokenType.IMPLIED);
	}

	/**
	 * testPCData
	 */
	public void testPCData() {
		String source = "#PCDATA";
		typeTests(source, DTDTokenType.PCDATA);
	}

	/**
	 * testRequired
	 */
	public void testRequired() {
		String source = "#REQUIRED";
		typeTests(source, DTDTokenType.REQUIRED);
	}

	/**
	 * testAny
	 */
	public void testAny() {
		String source = "ANY";
		typeTests(source, DTDTokenType.ANY);
	}

	/**
	 * testCDataType
	 */
	public void testCDataType() {
		String source = "CDATA";
		typeTests(source, DTDTokenType.CDATA_TYPE);
	}

	/**
	 * testEmpty
	 */
	public void testEmpty() {
		String source = "EMPTY";
		typeTests(source, DTDTokenType.EMPTY);
	}

	/**
	 * testEntityType
	 */
	public void testEntityType() {
		String source = "ENTITY";
		typeTests(source, DTDTokenType.ENTITY_TYPE);
	}

	/**
	 * testEntitiesType
	 */
	public void testEntitiesType() {
		String source = "ENTITIES";
		typeTests(source, DTDTokenType.ENTITIES_TYPE);
	}

	/**
	 * testIDType
	 */
	public void testIDType() {
		String source = "ID";
		typeTests(source, DTDTokenType.ID_TYPE);
	}

	/**
	 * testIDRefType
	 */
	public void testIDRefType() {
		String source = "IDREF";
		typeTests(source, DTDTokenType.IDREF_TYPE);
	}

	/**
	 * testIDRefsType
	 */
	public void testIDRefsType() {
		String source = "IDREFS";
		typeTests(source, DTDTokenType.IDREFS_TYPE);
	}

	/**
	 * testIgnore
	 */
	public void testIgnore() {
		String source = "IGNORE";
		typeTests(source, DTDTokenType.IGNORE);
	}

	/**
	 * testInclude
	 */
	public void testInclude() {
		String source = "INCLUDE";
		typeTests(source, DTDTokenType.INCLUDE);
	}

	/**
	 * testNDataType
	 */
	public void testNDataType() {
		String source = "NDATA";
		typeTests(source, DTDTokenType.NDATA);
	}

	/**
	 * testNMTokenType
	 */
	public void testNMTokenType() {
		String source = "NMTOKEN";
		typeTests(source, DTDTokenType.NMTOKEN_TYPE);
	}

	/**
	 * testNMTokensType
	 */
	public void testNMTokensType() {
		String source = "NMTOKENS";
		typeTests(source, DTDTokenType.NMTOKENS_TYPE);
	}

	/**
	 * testNotationType
	 */
	public void testNotationType() {
		String source = "NOTATION";
		typeTests(source, DTDTokenType.NOTATION_TYPE);
	}

	/**
	 * testPublicType
	 */
	public void testPublicType() {
		String source = "PUBLIC";
		typeTests(source, DTDTokenType.PUBLIC);
	}

	/**
	 * testSystemType
	 */
	public void testSystemType() {
		String source = "SYSTEM";
		typeTests(source, DTDTokenType.SYSTEM);
	}

	/**
	 * testPERef
	 */
	public void testPERef() {
		String source = "%PERef;";
		typeTests(source, DTDTokenType.PE_REF);
	}

	/**
	 * testGreaterThan
	 */
	public void testGreaterThan() {
		String source = ">";
		typeTests(source, DTDTokenType.GREATER_THAN);
	}

	/**
	 * testLeftParen
	 */
	public void testLeftParen() {
		String source = "(";
		typeTests(source, DTDTokenType.LPAREN);
	}

	/**
	 * testPipe
	 */
	public void testPipe() {
		String source = "|";
		typeTests(source, DTDTokenType.PIPE);
	}

	/**
	 * testRightParen
	 */
	public void testRightParen() {
		String source = ")";
		typeTests(source, DTDTokenType.RPAREN);
	}

	/**
	 * testQuestion
	 */
	public void testQuestion() {
		String source = "?";
		typeTests(source, DTDTokenType.QUESTION);
	}

	/**
	 * testAsterisk
	 */
	public void testAsterisk() {
		String source = "*";
		typeTests(source, DTDTokenType.STAR);
	}

	/**
	 * testPlus
	 */
	public void testPlus() {
		String source = "+";
		typeTests(source, DTDTokenType.PLUS);
	}

	/**
	 * testComma
	 */
	public void testComma() {
		String source = ",";
		typeTests(source, DTDTokenType.COMMA);
	}

	/**
	 * testPercent
	 */
	public void testPercent() {
		String source = "%";
		typeTests(source, DTDTokenType.PERCENT);
	}

	/**
	 * testLeftBracket
	 */
	public void testLeftBracket() {
		String source = "[";
		typeTests(source, DTDTokenType.LBRACKET);
	}

	/**
	 * testName
	 */
	public void testName() {
		String source = "Name";
		typeTests(source, DTDTokenType.NAME);
	}

	/**
	 * testNmtoken
	 */
	public void testNmtoken() {
		String source = "200";
		typeTests(source, DTDTokenType.NMTOKEN);
	}

	/**
	 * testSectionStart
	 */
	public void testSectionStart() {
		String source = "<![";
		typeTests(source, DTDTokenType.SECTION_START);
	}

	/**
	 * testSectionEnd
	 */
	public void testSectionEnd() {
		String source = "]]>";
		typeTests(source, DTDTokenType.SECTION_END);
	}

	/**
	 * testSequence
	 */
	public void testSequence() {
		String source = "<![%svg-prefw-redecl.module;[%svg-prefw-redecl.mod;]]>";
		typeTests(source, DTDTokenType.SECTION_START, DTDTokenType.PE_REF, DTDTokenType.LBRACKET, DTDTokenType.PE_REF, DTDTokenType.SECTION_END);
	}
}
