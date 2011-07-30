/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;

public class CSSTokensTest extends TestCase
{
	class TokenInfo
	{
		public final Object data;
		public final int offset;
		public final int length;

		public TokenInfo(Object data, int offset, int length)
		{
			this.data = data;
			this.offset = offset;
			this.length = length;
		}
	}
	private CSSTokenScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		fScanner = new CSSTokenScanner();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;
	}

	protected void assertToken(String source, Object data, int offset, int length)
	{
		assertToken(source, new TokenInfo(data, offset, length));
		assertToken(source.toUpperCase(), new TokenInfo(data, offset, length));
	}

	protected void assertToken(String source, TokenInfo... infos)
	{
		setSource(source);
		assertToken(infos);
	}

	protected void assertToken(Object data, int offset, int length)
	{
		assertToken(new TokenInfo(data, offset, length));
	}

	protected void assertToken(TokenInfo... infos)
	{
		for (TokenInfo info : infos)
		{
			assertEquals("Checking token type", info.data, fScanner.nextToken().getData());
			assertEquals("Checking token offset", info.offset, fScanner.getTokenOffset());
			assertEquals("Checking token length", info.length, fScanner.getTokenLength());
		}
	}

	protected void setSource(String source)
	{
		fScanner.setRange(new Document(source), 0, source.length());
	}
}
