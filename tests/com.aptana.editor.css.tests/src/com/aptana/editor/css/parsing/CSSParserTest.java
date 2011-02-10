/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import junit.framework.TestCase;

import com.aptana.parsing.ast.IParseNode;

/**
 * @author Kevin Lindsey
 * @author Michael Xia
 */
public class CSSParserTest extends TestCase
{

	private static final String EOL = "\n"; //$NON-NLS-1$

	private CSSParser fParser;
	private CSSScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		fParser = new CSSParser();
		fScanner = new CSSScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		fScanner = null;
	}

	/**
	 * Test empty import with a string
	 * 
	 * @throws Exception
	 */
	public void testImportStringNoIdentifier() throws Exception
	{
		parseTest("@import 'test';" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test empty import with a url
	 * 
	 * @throws Exception
	 */
	public void testImportUrlNoIdentifier() throws Exception
	{
		parseTest("@import url('test');" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test import with a string and a single identifier
	 * 
	 * @throws Exception
	 */
	public void testImportStringSingleIdentifier() throws Exception
	{
		parseTest("@import 'test' abc123;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test import with a url and a single identifier
	 * 
	 * @throws Exception
	 */
	public void testImportUrlSingleIdentifier() throws Exception
	{
		parseTest("@import url('test') abc123;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test import with a string and multiple identifiers
	 * 
	 * @throws Exception
	 */
	public void testImportStringMultipleIdentifiers() throws Exception
	{
		parseTest("@import 'test' abc123, def456;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test import with a url and multiple identifiers
	 * 
	 * @throws Exception
	 */
	public void testImportUrlMultipleIdentifiers() throws Exception
	{
		parseTest("@import url('test') abc123, def456;" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test empty media
	 * 
	 * @throws Exception
	 */
	public void testMediaEmpty() throws Exception
	{
		parseTest("@media test{}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test media with statement
	 * 
	 * @throws Exception
	 */
	public void testMediaStatement() throws Exception
	{
		parseTest("@media test{body {testing: 10;}}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test empty page
	 * 
	 * @throws Exception
	 */
	public void testPageEmpty() throws Exception
	{
		parseTest("@page {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with an identifier
	 * 
	 * @throws Exception
	 */
	public void testPagePseudoIdentifier() throws Exception
	{
		parseTest("@page :abc123 {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a number declaration
	 * 
	 * @throws Exception
	 */
	public void testPageNumberDeclaration() throws Exception
	{
		parseTest("@page {testing: 10;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a percent declaration
	 * 
	 * @throws Exception
	 */
	public void testPagePercentDeclaration() throws Exception
	{
		parseTest("@page {testing: 10%;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a pixel declaration
	 * 
	 * @throws Exception
	 */
	public void testPagePixelDeclaration() throws Exception
	{
		parseTest("@page {testing: 10px;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a centimeter declaration
	 * 
	 * @throws Exception
	 */
	public void testPageCentimeterDeclaration() throws Exception
	{
		parseTest("@page {testing: 10cm;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a millimeter declaration
	 * 
	 * @throws Exception
	 */
	public void testPageMillimeterDeclaration() throws Exception
	{
		parseTest("@page {testing: 10mm;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a inch declaration
	 * 
	 * @throws Exception
	 */
	public void testPageInchDeclaration() throws Exception
	{
		parseTest("@page {testing: 10in;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a point declaration
	 * 
	 * @throws Exception
	 */
	public void testPagePointDeclaration() throws Exception
	{
		parseTest("@page {testing: 10pt;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a pica declaration
	 * 
	 * @throws Exception
	 */
	public void testPagePicaDeclaration() throws Exception
	{
		parseTest("@page {testing: 10pc;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with an em declaration
	 * 
	 * @throws Exception
	 */
	public void testPageEmDeclaration() throws Exception
	{
		parseTest("@page {testing: 10em;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with an ex declaration
	 * 
	 * @throws Exception
	 */
	public void testPageExDeclaration() throws Exception
	{
		parseTest("@page {testing: 10ex;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a degree declaration
	 * 
	 * @throws Exception
	 */
	public void testPageDegreeDeclaration() throws Exception
	{
		parseTest("@page {testing: 10deg;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a radian declaration
	 * 
	 * @throws Exception
	 */
	public void testPageRadianDeclaration() throws Exception
	{
		parseTest("@page {testing: 10rad;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a gradian declaration
	 * 
	 * @throws Exception
	 */
	public void testPageGradianDeclaration() throws Exception
	{
		parseTest("@page {testing: 10grad;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a millisecond declaration
	 * 
	 * @throws Exception
	 */
	public void testPageMillisecondDeclaration() throws Exception
	{
		parseTest("@page {testing: 10ms;}" + EOL);
	}

	/**
	 * Test page with a second declaration
	 * 
	 * @throws Exception
	 */
	public void testPageSecondDeclaration() throws Exception
	{
		parseTest("@page {testing: 10s;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a hertz declaration
	 * 
	 * @throws Exception
	 */
	public void testPageHertzDeclaration() throws Exception
	{
		parseTest("@page {testing: 10hz;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a kilohertz declaration
	 * 
	 * @throws Exception
	 */
	public void testPageKilohertzDeclaration() throws Exception
	{
		parseTest("@page {testing: 10khz;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a single-quoted string declaration
	 * 
	 * @throws Exception
	 */
	public void testPageSingleQuotedStringDeclaration() throws Exception
	{
		parseTest("@page {testing: '10';}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a single-quoted string declaration
	 * 
	 * @throws Exception
	 */
	public void testPageDoubleQuotedStringDeclaration() throws Exception
	{
		parseTest("@page {testing: \"10\";}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with an identifier declaration
	 * 
	 * @throws Exception
	 */
	public void testPageIdentifierDeclaration() throws Exception
	{
		parseTest("@page {testing: abc123;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a url declaration
	 * 
	 * @throws Exception
	 */
	public void testPageUrlDeclaration() throws Exception
	{
		parseTest("@page {testing: url(abc123);}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a 3-digit color declaration
	 * 
	 * @throws Exception
	 */
	public void testPageThreeDigitColorDeclaration() throws Exception
	{
		parseTest("@page {testing: #eee;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a 6-digit color declaration
	 * 
	 * @throws Exception
	 */
	public void testPageSixDigitColorDeclaration() throws Exception
	{
		parseTest("@page {testing: #80A0FF;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a plus declaration
	 * 
	 * @throws Exception
	 */
	public void testPagePlusDeclaration() throws Exception
	{
		parseTest("@page {testing: +10;}" + EOL, "@page {testing: 10;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a minus declaration
	 * 
	 * @throws Exception
	 */
	public void testPageMinusDeclaration() throws Exception
	{
		parseTest("@page {testing: -10;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a function declaration
	 * 
	 * @throws Exception
	 */
	public void testPageFunctionDeclaration() throws Exception
	{
		parseTest("@page {testing: rgb(10,20,30);}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with an important declaration
	 * 
	 * @throws Exception
	 */
	public void testPageImportantDeclaration() throws Exception
	{
		parseTest("@page {testing: aptana !important;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a slash declaration
	 * 
	 * @throws Exception
	 */
	public void testPageSlashDeclaration() throws Exception
	{
		parseTest("@page {testing: abc/123;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with multiple slashes declaration
	 * 
	 * @throws Exception
	 */
	public void testPageMultiSlashDeclaration() throws Exception
	{
		parseTest("@page {testing: abc/123/rgb(1,2,3);}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a comma declaration
	 * 
	 * @throws Exception
	 */
	public void testPageCommaDeclaration() throws Exception
	{
		parseTest("@page {testing: abc,123;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with multiple commas declaration
	 * 
	 * @throws Exception
	 */
	public void testPageMultiCommaDeclaration() throws Exception
	{
		parseTest("@page {testing: abc,123,rgb(1,2,3);}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with a space-delimited declaration
	 * 
	 * @throws Exception
	 */
	public void testPageSpaceDeclaration() throws Exception
	{
		parseTest("@page {testing: abc 123;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with multiple space-delimiters declaration
	 * 
	 * @throws Exception
	 */
	public void testPageMultiSpaceDeclaration() throws Exception
	{
		parseTest("@page {testing: abc 123 rgb(1,2,3);}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test page with multiple declaration
	 * 
	 * @throws Exception
	 */
	public void testPageMultipleDeclarations() throws Exception
	{
		parseTest("@page {testing: abc123; forward: 10pt;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test single-quoted charset
	 * 
	 * @throws Exception
	 */
	public void testCharsetSingleQuotedCharSet() throws Exception
	{
		parseTest("@charset 'test';" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test double-quoted charset
	 * 
	 * @throws Exception
	 */
	public void testCharsetDoubleQuotedCharSet() throws Exception
	{
		parseTest("@charset \"test\";" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test empty font-face
	 * 
	 * @throws Exception
	 */
	public void testFontFaceEmpty() throws Exception
	{
		parseTest("@font-face {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test font-face with declaration
	 * 
	 * @throws Exception
	 */
	public void testFontFaceDeclaration() throws Exception
	{
		parseTest("@font-face {font-family: name;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test namespace with a string
	 * 
	 * @throws Exception
	 */
	public void testNamespaceString() throws Exception
	{
		parseTest("@namespace \"test\";" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test namespace with a prefix and a string
	 * 
	 * @throws Exception
	 */
	public void testNamespacePrefixString() throws Exception
	{
		parseTest("@namespace foo \"test\";" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test the star, '*', selector
	 * 
	 * @throws Exception
	 */
	public void testAnyElementSelector() throws Exception
	{
		parseTest("* {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test a simple element selector
	 * 
	 * @throws Exception
	 */
	public void testElementSelector() throws Exception
	{
		parseTest("a {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test descendant selector
	 * 
	 * @throws Exception
	 */
	public void testDescendantSelector() throws Exception
	{
		parseTest("table td {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test child selector
	 * 
	 * @throws Exception
	 */
	public void testChildSelector() throws Exception
	{
		// parseTest("table > tr {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test element pseudo-class selector
	 * 
	 * @throws Exception
	 */
	public void testElementPseudoclassSelector() throws Exception
	{
		parseTest("td:first-child {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test element pseudo-class function selector
	 * 
	 * @throws Exception
	 */
	public void testElementPseudoclassFunctionSelector() throws Exception
	{
		// parseTest("p:lang(en) {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test adjacent element selector
	 * 
	 * @throws Exception
	 */
	public void testAdjacentSelector() throws Exception
	{
		// parseTest("p + p {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test attribute-exists element selector
	 * 
	 * @throws Exception
	 */
	public void testAttributeSetSelector() throws Exception
	{
		parseTest("a[href] {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test attribute-value element selector
	 * 
	 * @throws Exception
	 */
	public void testAttributeValueSelector() throws Exception
	{
		parseTest("p[lang = \"en\"] {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test attribute-value-in-list element selector
	 * 
	 * @throws Exception
	 */
	public void testAttributeValueInListSelector() throws Exception
	{
		// parseTest("p[lang ~= \"en\"] {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test attribute-hyphenated-value-in-list-starts-with-value selector
	 * 
	 * @throws Exception
	 */
	public void testAttributeHyphenateInListSelector() throws Exception
	{
		// parseTest("p[lang |= \"en\"] {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test element class-value-in-list selector
	 * 
	 * @throws Exception
	 */
	public void testClassSelector() throws Exception
	{
		parseTest("div.warning {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * test element id selector
	 * 
	 * @throws Exception
	 */
	public void testIdSelector() throws Exception
	{
		parseTest("div#menu {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * test multiple element selector
	 * 
	 * @throws Exception
	 */
	public void testMultipleElementSelector() throws Exception
	{
		parseTest("h1, h2, h3 {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test the universal selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalSelector() throws Exception
	{
		parseTest("* {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test universal with descendant selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalDescendantSelector() throws Exception
	{
		parseTest("* td {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test universal with child selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalChildSelector() throws Exception
	{
		// parseTest("* > tr {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test universal element pseudo-class selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalPseudoclassSelector() throws Exception
	{
		parseTest("*:first-child {}" + EOL); //$NON-NLS-1$
		parseTest(":first-child {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test universal element pseudo-class function selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalPseudoclassFunctionSelector() throws Exception
	{
		// parseTest("*:lang(en) {}" + EOL); //$NON-NLS-1$
		// parseTest(":lang(en) {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test universal element adjacent element selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalAdjacentSelector() throws Exception
	{
		// parseTest("* + p {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test universal element attribute-exists element selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalAttributeSetSelector() throws Exception
	{
		parseTest("*[href] {}" + EOL); //$NON-NLS-1$
		parseTest("[href] {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test universal element attribute-value element selector
	 * 
	 * @throws Exception
	 */
	public void testUniveralAttributeValueSelector() throws Exception
	{
		parseTest("*[lang = \"en\"] {}" + EOL); //$NON-NLS-1$
		parseTest("[lang = \"en\"] {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test universal element attribute-value-in-list element selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalAttributeValueInListSelector() throws Exception
	{
		// parseTest("*[lang ~= \"en\"] {}" + EOL); //$NON-NLS-1$
		// parseTest("[lang ~= \"en\"] {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test universal element attribute-hyphenated-value-in-list-starts-with-value selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalAttributeHyphenateInListSelector() throws Exception
	{
		// parseTest("*[lang |= \"en\"] {}" + EOL); //$NON-NLS-1$
		// parseTest("[lang |= \"en\"] {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * Test universal element class-value-in-list selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalClassSelector() throws Exception
	{
		parseTest("*.warning {}" + EOL); //$NON-NLS-1$
		parseTest(".warning {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * test universal element id selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalIdSelector() throws Exception
	{
		parseTest("*#menu {}" + EOL); //$NON-NLS-1$
		parseTest("#menu {}" + EOL); //$NON-NLS-1$
	}

	/**
	 * test single type selector with one property
	 * 
	 * @throws Exception
	 */
	public void testSimpleSelectorOneProperty() throws Exception
	{
		parseTest("a {testing: 10;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * test single type selector with multiple properties
	 * 
	 * @throws Exception
	 */
	public void testSimpleSelectorMultipleProperties() throws Exception
	{
		parseTest("a {testing: abc123; forward: 10pt;}" + EOL); //$NON-NLS-1$
	}

	/**
	 * test multiple rule set definitions
	 * 
	 * @throws Exception
	 */
	public void testMultipleRuleSets() throws Exception
	{
		parseTest("a {}" + EOL + "b {}" + EOL); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * This case was causing the parser to go into an infinite loop. The scanner would get a bad location exception
	 * after the comment and did not return EOF in that case. This caused the parser to loop endlessly
	 * 
	 * @throws Exception
	 */
	public void testCommentBug() throws Exception
	{
		parseTest("body {\n\tbackground: red;\n}\n\n/**\n * \n */", "body {background: red;}" + EOL);
	}

	protected void parseTest(String source) throws Exception
	{
		parseTest(source, source);
	}

	protected void parseTest(String source, String expected) throws Exception
	{
		fScanner.setSource(source);

		IParseNode result = (IParseNode) fParser.parse(fScanner);
		StringBuilder text = new StringBuilder();
		IParseNode[] children = result.getChildren();
		for (IParseNode child : children)
		{
			text.append(child).append(EOL);
		}
		assertEquals(expected, text.toString());
	}
}
