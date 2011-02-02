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
		setSource(source);
		assertToken(data, offset, length);
	}

	protected void assertToken(Object data, int offset, int length)
	{
		assertEquals(data, fScanner.nextToken().getData());
		assertEquals(offset, fScanner.getTokenOffset());
		assertEquals(length, fScanner.getTokenLength());
	}

	protected void setSource(String source)
	{
		fScanner.setRange(new Document(source), 0, source.length());
	}
}
