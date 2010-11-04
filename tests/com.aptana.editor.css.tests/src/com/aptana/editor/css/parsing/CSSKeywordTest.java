/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
