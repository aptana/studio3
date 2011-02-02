/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import com.aptana.editor.css.parsing.lexer.CSSTokenType;

public class CSSKeywordTest extends CSSTokensTest
{

	public void testImportKeyword()
	{
		assertToken("@import", CSSTokenType.IMPORT, 0, 7); //$NON-NLS-1$
		assertToken("@imports", CSSTokenType.AT_RULE, 0, 8); //$NON-NLS-1$
	}

	public void testPageKeyword()
	{
		assertToken("@page", CSSTokenType.PAGE, 0, 5); //$NON-NLS-1$
		assertToken("@pages", CSSTokenType.AT_RULE, 0, 6); //$NON-NLS-1$
	}

	public void testMediaKeyword()
	{
		assertToken("@media", CSSTokenType.MEDIA_KEYWORD, 0, 6); //$NON-NLS-1$
		assertToken("@medias", CSSTokenType.AT_RULE, 0, 7); //$NON-NLS-1$
	}

	public void testCharSetKeyword()
	{
		assertToken("@charset", CSSTokenType.CHARSET, 0, 8); //$NON-NLS-1$
		assertToken("@charsets", CSSTokenType.AT_RULE, 0, 9); //$NON-NLS-1$
	}

	public void testFontFaceKeyword()
	{
		assertToken("@font-face", CSSTokenType.FONTFACE, 0, 10); //$NON-NLS-1$
		assertToken("@font-faces", CSSTokenType.AT_RULE, 0, 11); //$NON-NLS-1$
	}

	public void testNamespaceKeyword()
	{
		assertToken("@namespace", CSSTokenType.NAMESPACE, 0, 10); //$NON-NLS-1$
		assertToken("@namespaces", CSSTokenType.AT_RULE, 0, 11); //$NON-NLS-1$
	}

	public void testUrlKeyword()
	{
		assertToken("url(test.css)", CSSTokenType.URL, 0, 13); //$NON-NLS-1$
	}

	public void testImportantKeyword()
	{
		assertToken("!important", CSSTokenType.IMPORTANT, 0, 10); //$NON-NLS-1$
		assertToken("! important", CSSTokenType.IMPORTANT, 0, 11); //$NON-NLS-1$
	}
}
