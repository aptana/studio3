/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing;

import org.junit.Test;

public class CSSIdentifierTest extends CSSTokensTest
{

	@Test
	public void testSimpleIdentifier()
	{
		assertToken("abc", CSSTokenType.IDENTIFIER); //$NON-NLS-1$
	}

	@Test
	public void testIdentifierWithNumber()
	{
		assertToken("abc0", CSSTokenType.IDENTIFIER); //$NON-NLS-1$
	}

	@Test
	public void testIdentifierWithHyphen()
	{
		assertToken("abc-def", CSSTokenType.IDENTIFIER); //$NON-NLS-1$
	}

	@Test
	public void testIdentifierWithLeadingHyphen()
	{
		assertToken("-abc", CSSTokenType.IDENTIFIER); //$NON-NLS-1$
	}

	@Test
	public void testIdentifierWithUnderscore()
	{
		assertToken("abc_def", CSSTokenType.IDENTIFIER); //$NON-NLS-1$
	}

	@Test
	public void testIdentifierWithLeadingUnderscore()
	{
		assertToken("_abc", CSSTokenType.IDENTIFIER); //$NON-NLS-1$
	}

	/**
	 * APSTUD-4646
	 */
	@Test
	public void testMSIdentifier()
	{
		assertToken("progid:DXImageTransform.Microsoft.DropShadow", CSSTokenType.IDENTIFIER);
	}
}
