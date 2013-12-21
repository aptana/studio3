/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing;

import org.junit.Test;

public class CSSKeywordTest extends CSSTokensTest
{

	@Test
	public void testImportKeyword()
	{
		assertToken("@import", CSSTokenType.IMPORT, 0, 7); //$NON-NLS-1$
		assertToken("@imports", CSSTokenType.AT_RULE, 0, 8); //$NON-NLS-1$
	}

	@Test
	public void testPageKeyword()
	{
		assertToken("@page", CSSTokenType.PAGE, 0, 5); //$NON-NLS-1$
		assertToken("@pages", CSSTokenType.AT_RULE, 0, 6); //$NON-NLS-1$
	}

	@Test
	public void testMediaKeyword()
	{
		assertToken("@media", CSSTokenType.MEDIA_KEYWORD, 0, 6); //$NON-NLS-1$
		assertToken("@medias", CSSTokenType.AT_RULE, 0, 7); //$NON-NLS-1$
	}

	@Test
	public void testCharSetKeyword()
	{
		assertToken("@charset", CSSTokenType.CHARSET, 0, 8); //$NON-NLS-1$
		assertToken("@charsets", CSSTokenType.AT_RULE, 0, 9); //$NON-NLS-1$
	}

	@Test
	public void testFontFaceKeyword()
	{
		assertToken("@font-face", CSSTokenType.FONTFACE, 0, 10); //$NON-NLS-1$
		assertToken("@font-faces", CSSTokenType.AT_RULE, 0, 11); //$NON-NLS-1$
	}

	@Test
	public void testNamespaceKeyword()
	{
		assertToken("@namespace", CSSTokenType.NAMESPACE, 0, 10); //$NON-NLS-1$
		assertToken("@namespaces", CSSTokenType.AT_RULE, 0, 11); //$NON-NLS-1$
	}

	@Test
	public void testMozDocumentKeyword()
	{
		assertToken("@-moz-document", CSSTokenType.MOZ_DOCUMENT, 0, 14); //$NON-NLS-1$
		assertToken("@-moz-documents", CSSTokenType.AT_RULE, 0, 15); //$NON-NLS-1$
	}

	@Test
	public void testUrlKeyword()
	{
		assertToken("url(test.css)", CSSTokenType.URL, 0, 13); //$NON-NLS-1$
	}

	@Test
	public void testImportantKeyword()
	{
		assertToken("!important", CSSTokenType.IMPORTANT, 0, 10); //$NON-NLS-1$
		assertToken("! important", CSSTokenType.IMPORTANT, 0, 11); //$NON-NLS-1$
		assertToken("!Important", CSSTokenType.IMPORTANT, 0, 10); //$NON-NLS-1$

	}
}
