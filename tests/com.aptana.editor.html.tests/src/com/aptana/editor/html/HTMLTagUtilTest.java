/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import junit.framework.TestCase;

import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.parsing.lexer.Lexeme;

public class HTMLTagUtilTest extends TestCase
{
	public void testIsTag()
	{
		assertTrue(HTMLTagUtil.isTag(new Lexeme<HTMLTokenType>(HTMLTokenType.BLOCK_TAG, 0, 2, "<a>")));
		assertTrue(HTMLTagUtil.isTag(new Lexeme<HTMLTokenType>(HTMLTokenType.INLINE_TAG, 0, 2, "<a>")));
		assertTrue(HTMLTagUtil.isTag(new Lexeme<HTMLTokenType>(HTMLTokenType.STRUCTURE_TAG, 0, 2, "<a>")));
		assertFalse(HTMLTagUtil.isTag(new Lexeme<HTMLTokenType>(HTMLTokenType.ATTRIBUTE, 0, 2, "<a>")));
	}
}
